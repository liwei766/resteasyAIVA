/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：FillerRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.filler;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.AlwaysQuoteMode;

import cloud.optim.aivoiceanalytics.api.app.filler.FillerResponse.BulkResult;
import cloud.optim.aivoiceanalytics.api.recaius.service.RecaiusSpeechService;
import cloud.optim.aivoiceanalytics.api.util.ExtractUtil;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.LoginUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.CustomExceptionMapper.RestResponse;
import cloud.optim.aivoiceanalytics.core.modules.rest.ExceptionUtil;
import cloud.optim.aivoiceanalytics.core.modules.rest.MessageUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestLog;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;
import cloud.optim.aivoiceanalytics.core.modules.rest.app.fileupload.FileUploadUtility;

/**
 * FillerRestService 実装.<br/>
 */
@Path( "/filler" )
@Consumes( { "application/json", "application/xml" } )
@Produces( { "application/json", "application/xml" } )
@Component
public class FillerRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() );

	// -------------------------------------------------------------------------

	/** バリデータ */
	@Resource private FillerRestValidator validator;

	/** RecaiusSpeechService */
	@Resource private RecaiusSpeechService recaiusSpeechService;

	/** RestLog */
	@Resource private RestLog restlog;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility;

	/** FileUploadUtility */
	@Resource private FileUploadUtility fileUploadUtility;

	/** ExtractUtil */
	@Resource private ExtractUtil extractUtil;

	// -------------------------------------------------------------------------

	/** インポート返却エラー数上限値 */
	@Value( "${filler.update.max.error.count}" )
	private int maxImportErrorCount;

	/** CSVファイル文字エンコード */
	@Value( "${filler.update.char.encoding}" )
	private String csvCharEncoding;

	/** フィラー情報ファイル名パス */
	@Value( "${morphlogical.analyze.user.dictionary.base.path}" )
	private String dictionaryPath;

	/** フィラー情報ファイル名 */
	@Value( "${morphlogical.analyze.user.dictionary.file.name}" )
	private String fileName;

	/** フィラー情報CSVファイル文字エンコード */
	@Value( "${morphlogical.analyze.user.dictionary.char.encoding}" )
	private String dictionaryCharEncoding;

	/** フィラー品詞コード名詞 */
	private static final int CLASS_ID_NOUN = 6;

	/** CSVファイルヘッダー */
	@Value( "${filler.export.header}" )
	private String[] exportHeader;

	/** ダウンロードエラーページURL */
	@Value( "${download.error.page.url}" )
	private String downloadErrorPageUrl;

	// -------------------------------------------------------------------------



	/**
	 * フィラー情報一括登録
	 *
	 * @param req 登録内容
	 *
	 * @return 処理結果
	 */
	@POST
	@Path( "/update" )
	@Consumes( "multipart/form-data" )
	@Produces( { "application/json", "text/json" } )
	public FillerResponse importCsv(@Context HttpServletRequest req) {

		String MNAME = "update";
		restlog.start( log, MNAME, null );


		try {
			// ----- CSVファイルを読み込む
			FillerResponse res = new FillerResponse();
			List<Filler> fillers = readCsv(req, res);
			// ----- レスポンス作成
			if (res.getBulkResultList().isEmpty()) {


				// ----- フィラー情報登録
				//RecaiusResult result = recaiusSpeechService.updateFiller(fillers);

				int fillerCount = updateFillerDictionary(fillers);

				res.addResult( new RestResult( ResponseCode.OK ) );

				// 登録数
				res.setDataCount(fillerCount);

			} else {
				res.addResult( new RestResult( ResponseCode.PARTIAL ) );
			}
			messageUtility.fillMessage( res.getResultList() );

			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;

		} catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	/**
	 * リクエストの入力ストリームからCSVファイルを読み込む
	 * @param req リクエスト
	 * @return 編集フォームリスト
	 * @throws Exception
	 */
	private List<Filler> readCsv(HttpServletRequest req, FillerResponse res) throws Exception {
		List<Filler> fillers = new ArrayList<>();
		List<BulkResult> bulkResultList =new ArrayList<>();
		res.setBulkResultList(bulkResultList);

		// アップロードファイルからCSVファイルの内容を読み込む
		try(
			InputStreamReader inputReader = new InputStreamReader(fileUploadUtility.getUploadInputStream(req), csvCharEncoding);
			ICsvBeanReader beanReader = new CsvBeanReader(inputReader, CsvPreference.STANDARD_PREFERENCE)) {

			// ヘッダ行を読み飛ばす
			beanReader.getHeader(true);

			final String[] headers = new String[]{"surface"};
			final CellProcessor[] processors = new CellProcessor[] {
					new NotNull(new Trim())};
			int dataNo = 1;
			int errorCount = 0;
			while (true) {
				try {
					Filler filler = beanReader.read(Filler.class, headers, processors);
					if (filler == null) break;
					// フィラー情報用の品詞IDを設定する
					filler.setClass_id(CLASS_ID_NOUN);
					fillers.add(filler);
				} catch (Exception e) {
					// 上限値超えたら結果に追加せず件数だけインクリメント
					if (++errorCount > maxImportErrorCount) continue;

					// CSV フォーマットエラー
					BulkResult result = new BulkResult();
					result.setResult(new RestResult(ResponseCode.FILLER_CSV_FORMAT_ERROR));
					result.setNumber(dataNo);
					messageUtility.fillMessage( result.getResultList() );
					bulkResultList.add(result);
				}
				dataNo++;
			}

			res.setErrorCount(errorCount);
		}
		return fillers;
	}


	private int updateFillerDictionary(List<Filler> fillers) {
		try {
			// フィラー情報を生成する
			List<FillerDictionary> dictionarys = fillers.stream()
					.map(each -> new FillerDictionary(each.getSurface() ))
					.collect(Collectors.toList());

			 //プロパティ名の配列をヘッダーと同じ順番で作成
			String[] header = {"surface","segmentationValue","readingsValue","partOfSpeech"};

			int dataCount = 0;
			try(
				FileOutputStream fileOutput = new FileOutputStream(new File(dictionaryPath,fileName));
				OutputStreamWriter outputWriter = new OutputStreamWriter(fileOutput, dictionaryCharEncoding);
				ICsvBeanWriter beanWriter = new CsvBeanWriter(outputWriter, CsvPreference.STANDARD_PREFERENCE)) {
				for(FillerDictionary each: dictionarys) {
					//Beanの内容を書き込み
					beanWriter.write(each, header);
					dataCount ++;
				}
			}
			// 形態素解析の辞書を更新する
			extractUtil.reloadUserDictionary();

			return dataCount;
		} catch (Exception e) {
			throw new RestException(new RestResult(ResponseCode.FILLER_UPDATE_ERROR), e);
		}
	}


	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM+";charset=Windows-31J")
	@Path("/file/{ext}/{id}/")
	public Response getDict(@Context HttpServletRequest req, @Context HttpServletResponse res, @PathParam("ext") String ext, @PathParam("id") String fileId) throws IOException {

		String MNAME = "file";
		restlog.start( log, MNAME, null );

		try {
			// ----- フィラー情報取得
			List<FillerDictionary> fillersDic = new ArrayList<>();

			ICsvBeanReader beanReader = new CsvBeanReader(new FileReader(new File(dictionaryPath,fileName)),  CsvPreference.EXCEL_PREFERENCE);

			// ヘッダ行を読み飛ばす
			//beanReader.getHeader(true);

			final String[] header = {"surface","segmentationValue","readingsValue","partOfSpeech"};
			final CellProcessor[] processors = new CellProcessor[] {
					new NotNull(new Trim()),new NotNull(new Trim()),null,new NotNull(new Trim())};;

			try {
				FillerDictionary filler = null;
				while ((filler = beanReader.read(FillerDictionary.class, header, processors)) != null) {
					fillersDic.add(new FillerDictionary(filler.getSurface()));
				}
			} finally {
				beanReader.close();
			}

			// ----- レスポンス作成
			StreamingOutput output = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {

					// 全カラムダブルコーテーションで括る
					CsvPreference preference = new CsvPreference.Builder(CsvPreference.EXCEL_PREFERENCE).useQuoteMode(new AlwaysQuoteMode()).build();
					try(
						OutputStreamWriter outputWriter = new OutputStreamWriter(output, csvCharEncoding);
						ICsvListWriter listWriter = new CsvListWriter(outputWriter, preference)) {
						listWriter.writeHeader(exportHeader);

						for(FillerDictionary each : fillersDic) {
							listWriter.write(each.getSurface() );
						}
					}

				}
			};

			return Response.ok().entity(output)
					.header("Content-Disposition", "attachment; filename=" + fileId + "." + ext).build();

		}	catch ( Exception ex ) {

			RestException restException = ExceptionUtil.handleException( log, ResponseCode.SYS_ERROR, null, null, null, ex );

			RestResponse restResponse = new RestResponse() ;
			restResponse.setResultList( restException.getRestResultList() ) ;
			messageUtility.fillMessage( restResponse.getResultList() ) ;
			restlog.abort( restException.getLogger() != null ? restException.getLogger() : log, restResponse, restResponse.getResultList(), restException ) ;

			res.sendRedirect(req.getContextPath() + downloadErrorPageUrl);
			return null;

		}
	}


}



