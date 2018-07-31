/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：LexiconRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.lexicon;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

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
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.AlwaysQuoteMode;

import cloud.optim.aivoiceanalytics.api.app.lexicon.LexiconResponse.BulkResult;
import cloud.optim.aivoiceanalytics.api.recaius.Lexicon;
import cloud.optim.aivoiceanalytics.api.recaius.LexiconError;
import cloud.optim.aivoiceanalytics.api.recaius.LexiconErrorCode;
import cloud.optim.aivoiceanalytics.api.recaius.result.ErrorResult;
import cloud.optim.aivoiceanalytics.api.recaius.result.LexiconGetResult;
import cloud.optim.aivoiceanalytics.api.recaius.result.LexiconUpdateErrorDetailResult;
import cloud.optim.aivoiceanalytics.api.recaius.result.LexiconUpdateSuccessResult;
import cloud.optim.aivoiceanalytics.api.recaius.result.RecaiusResult;
import cloud.optim.aivoiceanalytics.api.recaius.service.RecaiusAuthService;
import cloud.optim.aivoiceanalytics.api.recaius.service.RecaiusSpeechService;
//import cloud.optim.aivoiceanalytics.api.util.ExtractUtil;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.CustomUser;
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
 * LexiconRestService 実装.<br/>
 */
@Path( "/lexicon" )
@Consumes( { "application/json", "application/xml" } )
@Produces( { "application/json", "application/xml" } )
@Component
public class LexiconRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() );

	// -------------------------------------------------------------------------

	/** バリデータ */
	@Resource private LexiconRestValidator validator;

	/** RecaiusAuthService */
	@Resource private RecaiusAuthService authService;

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

//	/** ExtractUtil */
//	@Resource private ExtractUtil extractUtil;

	// -------------------------------------------------------------------------

	/** インポート返却エラー数上限値 */
	@Value( "${recaius.lexicon.update.max.error.count}" )
	private int maxImportErrorCount;

	/** CSVファイル文字エンコード */
	@Value( "${recaius.lexicon.update.char.encoding}" )
	private String csvCharEncoding;

	/** リカイアス音声解析サービスID */
	@Value( "${recaius.service.speech.type}" )
	private String serviceType;

//	/** 形態素解析ユーザ辞書ファイル名パス */
//	@Value( "${morphlogical.analyze.user.dictionary.base.path}" )
//	private String dictionaryPath;
//
//	/** 形態素解析ユーザ辞書CSVファイル文字エンコード */
//	@Value( "${morphlogical.analyze.user.dictionary.char.encoding}" )
//	private String dictionaryCharEncoding;

	/** リカイアス品詞コード名詞 */
	private static final int RECAIUS_CLASS_ID_NOUN = 6;

	/** CSVファイルヘッダー */
	@Value( "${lexicon.export.header}" )
	private String[] exportHeader;

	/** ダウンロードエラーページURL */
	@Value( "${download.error.page.url}" )
	private String downloadErrorPageUrl;

	// -------------------------------------------------------------------------

	/**
	 * リカイアスユーザ辞書登録単語一覧取得
	 *
	 * @param req 取得条件（PK 項目のみ使用）
	 *
	 * @return 取得エンティティ
	 */
	@POST
	@Path( "/get" )
	public LexiconResponse get( LexiconRequest req ) {

		String MNAME = "get";
		restlog.start( log, MNAME, req );

		String token = null;

		try {
			// ----- 入力チェック

			validator.validateForGet( req );

			// ユーザ情報を取得する
			CustomUser customUser = loginUtility.getCustomUser();
			Integer modelId = customUser.getRecaiusModelId();
			String serviceId = customUser.getRecaiusServiceId();
			String password = customUser.getRecaiusPassword();

			if( modelId == null || serviceId == null || password == null ) {
				throw new RestException( new RestResult(ResponseCode.RECAIUS_SPEECH_LEXICON_GET_ERROR) );
			}

			// ----- 認証処理でトークンを取得する
			token = authService.auth(serviceType, serviceId, password);

			// ----- ユーザ辞書取得
			LexiconGetResult lexicons = recaiusSpeechService.getLexicon(token, modelId);


			// ----- レスポンス作成
			LexiconResponse res = new LexiconResponse();
			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setLexicons( lexicons );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		} finally {
			// リカイアス認証トークンを削除する
			if (token != null) {
				try {
					authService.disconnect(token);
				} catch (Exception e) {
					log.error(e);
				}
			}
		}
	}


	// -------------------------------------------------------------------------

	/**
	 * リカイアス音声認識辞書一括登録
	 *
	 * @param req 登録内容
	 *
	 * @return 処理結果
	 */
	@POST
	@Path( "/update" )
	@Consumes( "multipart/form-data" )
	@Produces( { "application/json", "text/json" } )
	public LexiconResponse importCsv(@Context HttpServletRequest req) {

		String MNAME = "update";
		restlog.start( log, MNAME, null );

		String token = null;

		try {

			// ----- バリデート
			// modelIdを指定せずに読んだ場合404エラーになるのでバリデートは行わない
			// ユーザ情報を取得する
			CustomUser customUser = loginUtility.getCustomUser();
			String companyId = customUser.getCompanyId();
			Integer modelId = customUser.getRecaiusModelId();
			String serviceId = customUser.getRecaiusServiceId();
			String password = customUser.getRecaiusPassword();

			if(companyId == null || modelId == null || serviceId == null || password == null ) {
				throw new RestException( new RestResult(ResponseCode.RECAIUS_SPEECH_LEXICON_UPDATE_ERROR) );
			}

			// ----- CSVファイルを読み込む
			LexiconResponse res = new LexiconResponse();
			List<Lexicon> lexicons = readCsv(req, res);
			// ----- レスポンス作成
			if (res.getBulkResultList().isEmpty()) {

				// ----- 認証処理でトークンを取得する
				token = authService.auth(serviceType, serviceId, password);

				// ----- ユーザ辞書登録
				RecaiusResult result = recaiusSpeechService.updateLexicon(token, modelId, lexicons);

				// レスポンスコードが400番(入力エラー)の時のエラー処理(400番以外はサービスの中で例外がスローされる)
				if (result.isError()) {
					ErrorResult error = result.getResponse(ErrorResult.class);
					// 件数が多すぎる
					if (error.getCode() == LexiconErrorCode.INVALID_PARAMETER.getCode()) {
						throw new RestException(new RestResult(ResponseCode.LEXICON_TOO_MANY_WORDS, null, result.getErrorDetails() ));
					}

					// その他の入力エラー
					if (error.getCode() != LexiconErrorCode.PARTIAL_ERROR.getCode()) {
						throw new RestException(new RestResult(ResponseCode.RECAIUS_SPEECH_LEXICON_UPDATE_ERROR, null, result.getErrorDetails() ));
					}

					// 各入力内容にエラーがある
					res.addResult( new RestResult( ResponseCode.PARTIAL ) );

					// エラー詳細情報を取得する
					List<BulkResult> bulkResultList =new ArrayList<>();
					res.setBulkResultList(bulkResultList);
					for(LexiconUpdateErrorDetailResult each : error.toListMoreInfoObj(LexiconUpdateErrorDetailResult.class)) {
						// 入力エラー
						BulkResult bulkResult = new BulkResult();
						bulkResult.setResult(new RestResult(LexiconError.getResponseCode(each.getErrmsg())));
						bulkResult.setNumber(each.getNo());
						messageUtility.fillMessage( bulkResult.getResultList() );
						bulkResultList.add(bulkResult);
					}
					res.setErrorCount(bulkResultList.size());
				} else {

					// ----- kuromoji用の辞書を出力する
					//updateKuromojiDictionary(lexicons);

					// ----- レスポンスの生成
					LexiconUpdateSuccessResult successResult = result.getResponse(LexiconUpdateSuccessResult.class);

					res.addResult( new RestResult( ResponseCode.OK ) );
					res.setDataCount(successResult.getEntry_num());
				}

			} else {
				res.addResult( new RestResult( ResponseCode.PARTIAL ) );
			}
			messageUtility.fillMessage( res.getResultList() );

			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;

		} catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		} finally {
			// リカイアス認証トークンを削除する
			if (token != null) {
				try {
					authService.disconnect(token);
				} catch (Exception e) {
					log.error(e);
				}
			}
		}
	}

	/**
	 * リクエストの入力ストリームからCSVファイルを読み込む
	 * @param req リクエスト
	 * @return 編集フォームリスト
	 * @throws Exception
	 */
	private List<Lexicon> readCsv(HttpServletRequest req, LexiconResponse res) throws Exception {
		List<Lexicon> lexicons = new ArrayList<>();
		List<BulkResult> bulkResultList =new ArrayList<>();
		res.setBulkResultList(bulkResultList);

		// アップロードファイルからCSVファイルの内容を読み込む
		try(
			InputStreamReader inputReader = new InputStreamReader(fileUploadUtility.getUploadInputStream(req), csvCharEncoding);
			ICsvBeanReader beanReader = new CsvBeanReader(inputReader, CsvPreference.STANDARD_PREFERENCE)) {

			// ヘッダ行を読み飛ばす
			beanReader.getHeader(true);

			final String[] headers = new String[]{"surface", "pron"};
			final CellProcessor[] processors = getProcessors();
			int dataNo = 1;
			int errorCount = 0;
			while (true) {
				try {
					Lexicon lexicon = beanReader.read(Lexicon.class, headers, processors);
					if (lexicon == null) break;
					// リカイアス用の品詞IDを設定する
					lexicon.setClass_id(RECAIUS_CLASS_ID_NOUN);
					lexicons.add(lexicon);
				} catch (Exception e) {
					// 上限値超えたら結果に追加せず件数だけインクリメント
					if (++errorCount > maxImportErrorCount) continue;

					// CSV フォーマットエラー
					BulkResult result = new BulkResult();
					result.setResult(new RestResult(ResponseCode.LEXICON_CSV_FORMAT_ERROR));
					result.setNumber(dataNo);
					messageUtility.fillMessage( result.getResultList() );
					bulkResultList.add(result);
				}
				dataNo++;
			}

			res.setErrorCount(errorCount);
		}
		return lexicons;
	}

	/**
	 * ユーザ辞書用セルプロセッサの取得
	 * @return ユーザ辞書用セルプロセッサ
	 */
	private CellProcessor[] getProcessors() {
		return new CellProcessor[] {
				new NotNull(new Trim()), // surface
				new NotNull(new Trim())  // pron
		};
	}

//	private void updateKuromojiDictionary(List<Lexicon> lexicons) {
//		try {
//			// リカイアス音声辞書データからkuromoji用の辞書データを生成する
//			List<KuromojiDictionary> dictionarys = lexicons.stream()
//					.map(each -> new KuromojiDictionary(each.getSurface(), each.getPron()))
//					.collect(Collectors.toList());
//
//			 //プロパティ名の配列をヘッダーと同じ順番で作成
//			String[] header = {"surface","segmentationValue", "readingsValue", "partOfSpeech"};
//
//			// ファイルパスの生成
//			Long companyManagementId = loginUtility.getCustomUser().getCompanyManagementId();
//			if (companyManagementId == null) throw new NullPointerException();
//
//			String filePath = String.format(dictionaryPath, companyManagementId);
//			try(
//				FileOutputStream fileOutput = new FileOutputStream(filePath);
//				OutputStreamWriter outputWriter = new OutputStreamWriter(fileOutput, dictionaryCharEncoding);
//				ICsvBeanWriter beanWriter = new CsvBeanWriter(outputWriter, CsvPreference.STANDARD_PREFERENCE)) {
//				for(KuromojiDictionary each: dictionarys) {
//					//Beanの内容を書き込み
//					beanWriter.write(each, header);
//				}
//			}
//			// 形態素解析の辞書を更新する
//			extractUtil.reloadUserDictionary();
//		} catch (Exception e) {
//			throw new RestException(new RestResult(ResponseCode.LEXICON_MONOPHOLOGICAL_ANALYZE_DICTIONARY_UPDATE_ERROR), e);
//		}
//	}


	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM+";charset=Windows-31J")
	@Path("/file/{ext}/{id}/")
	public Response getDict(@Context HttpServletRequest req, @Context HttpServletResponse res, @PathParam("ext") String ext, @PathParam("id") String fileId) throws IOException {

		String MNAME = "file";
		restlog.start( log, MNAME, null );


		String token = null;

		try {

			// ユーザ情報を取得する
			CustomUser customUser = loginUtility.getCustomUser();
			Integer modelId = customUser.getRecaiusModelId();
			String serviceId = customUser.getRecaiusServiceId();
			String password = customUser.getRecaiusPassword();

			if( modelId == null || serviceId == null || password == null ) {
				throw new RestException( new RestResult(ResponseCode.RECAIUS_SPEECH_LEXICON_GET_ERROR) );
			}

			// ----- 認証処理でトークンを取得する
			token = authService.auth(serviceType, serviceId, password);

			// ----- ユーザ辞書取得
			LexiconGetResult lexicons = recaiusSpeechService.getLexicon(token, modelId);

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

						for(Lexicon each : lexicons.getUlex()) {
							listWriter.write(each.getSurface(), each.getPron());
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

		} finally {

			// リカイアス認証トークンを削除する
			if (token != null) {
				try {
					authService.disconnect(token);
				} catch (Exception e) {
					log.error(e);
				}
			}
		}
	}
}