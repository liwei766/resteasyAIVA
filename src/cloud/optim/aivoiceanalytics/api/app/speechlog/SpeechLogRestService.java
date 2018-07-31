/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechLogRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.speechlog;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.api.app.speechlog.SpeechLogResponse.BulkResult;
import cloud.optim.aivoiceanalytics.api.app.speechlog.SpeechLogResponse.EditResult;
import cloud.optim.aivoiceanalytics.api.app.speechlogdetail.SpeechLogDetailService;
import cloud.optim.aivoiceanalytics.api.entity.SpeechLog;
import cloud.optim.aivoiceanalytics.api.entity.SpeechLogDetail;
import cloud.optim.aivoiceanalytics.api.util.AuthUtil;
import cloud.optim.aivoiceanalytics.core.modules.ffmpeg.FFmpeg;
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
 * SpeechLogRestService 実装.<br/>
 */
@Path( "/speechlog" )
@Consumes( { "application/json", "application/xml" } )
@Produces( { "application/json", "application/xml" } )
@Component
public class SpeechLogRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() );

	/** PK の項目名 */
	private static final String NAME_PK = "#speechLog.speechLogId";

	/** LoginUtility */
	@Resource private LoginUtility loginUtility ;

	// -------------------------------------------------------------------------

	/** AuthUtil */
	@Resource private AuthUtil authUtil;

	/** バリデータ */
	@Resource private SpeechLogRestValidator validator;

	/** SpeechLogService */
	@Resource private SpeechLogService speechLogService;

	/** SpeechLogDetailService */
	@Resource private SpeechLogDetailService speechLogDetailService;

	/** RestLog */
	@Resource private RestLog restlog;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility;

	/** MessageUtility */
	@Resource private FileUploadUtility fileUploadUtility;

	/** FFmpeg */
	@Resource private FFmpeg ffmpeg;

	/** FileIdHolder */
	@Resource private FileIdHolder fileIdHolder ;

	/** ExecutorService */
	@Resource private ExecutorService executorService ;

	// -------------------------------------------------------------------------

	/** 一時ファイル保存ディレクトリ */
	@Value( "${speech.tmp.file.directory}" )
	private String tmpFileDirectory;

	/** 音声ファイル保存ルートディレクトリ */
	@Value( "${speech.voice.file.root.directory}" )
	private String voiceFileRootDirectory;

	/** 分割音声ファイル名 */
	@Value( "${speech.voice.file.name}" )
	private String voiceFileName;

	/** 圧縮音声ファイル名 */
	@Value( "${encode.output.file.name}" )
	private String encodedFileName;

	/** ダウンロードエラーページURL */
	@Value( "${download.error.page.url}" )
	private String downloadErrorPageUrl;

	/** Content-Disposition レスポンスヘッダー */
	@Value( "${speech.log.download.header.content.disposition}" )
	private String downloadHeader;

	/** ダウンロードファイル拡張子 */
	@Value( "${speech.log.download.file.ext}" )
	private String downloadfileExt;

	/** ダウンロードファイル名フォーマット */
	@Value( "${speech.log.download.filename.format}" )
	private String filenameformat;

	/** テキストとCSVファイル文字エンコード */
	@Value( "${speech.log.download.char.encoding}" )
	private String txtCsvCharEncoding;


	// -------------------------------------------------------------------------

	/**
	 * 検索
	 *
	 * @param req 検索条件
	 *
	 * @return 検索結果
	 */
	@POST
	@Path( "/searchByUser" )
	public SpeechLogResponse searchByUser( SpeechLogRequest req ) {

		String MNAME = "searchByUser";
		restlog.start( log, MNAME, req );

		try {

			SpeechLogResponse res = new SpeechLogResponse();

			// ----- 入力チェック

			// 検索条件の指定がない場合は全検索として扱う

			if ( req == null ) req = new SpeechLogRequest();

			validator.validateForSearchByUser( req );

			// ----- 検索

			SearchForm form = req.getSearchForm();

			List<SearchResult> list = speechLogService.searchByUser( form );

			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setSearchResultList( list );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 検索2 企業単位での検索
	 *
	 * @param req 検索条件
	 *
	 * @return 検索結果
	 */
	@POST
	@Path( "/searchByCompany" )
	public SpeechLogResponse searchByCompany( SpeechLogRequest req ) {

		String MNAME = "searchByCompany";
		restlog.start( log, MNAME, req );

		try {

			SpeechLogResponse res = new SpeechLogResponse();

			// ----- 入力チェック

			// 検索条件の指定がない場合は全検索として扱う

			if ( req == null ) req = new SpeechLogRequest();

			validator.validateForSearchByCompany( req );

			// ----- 検索

			SearchForm form = req.getSearchForm();

			List<SearchResult> list = speechLogService.searchByCompany( form );

			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setSearchResultList( list );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 取得
	 *
	 * @param req 取得条件（PK 項目のみ使用）
	 *
	 * @return 取得エンティティ
	 */
	@POST
	@Path( "/get" )
	public SpeechLogResponse get( SpeechLogRequest req ) {

		String MNAME = "get";
		restlog.start( log, MNAME, req );

		try {

			SpeechLogResponse res = new SpeechLogResponse();

			// ----- 入力チェック

			validator.validateForGet( req );

			// ----- 入力データ取得

			SpeechLog speechLog = req.getEditForm().getSpeechLog();

			// ----- 取得
			// ユーザ情報取得
			CustomUser customUser = loginUtility.getCustomUser();

			// 音声解析ログ取得
			SpeechLog entity = null;
			// 企業管理者権限を保持している場合には、ユーザ企業ID配下の全ユーザの音声解析ログを取得可能
			if (this.authUtil.isAdmin()) {
				entity = speechLogService.getSpeechLogCompanyAllUser( speechLog.getSpeechLogId(), customUser.getCompanyId() );
			} else {
				entity = speechLogService.getSpeechLog( speechLog.getSpeechLogId(), customUser.getCompanyId(), customUser.getUserId() );
			}

			if ( entity == null ) {

				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, NAME_PK, speechLog.getSpeechLogId() ) );
			}

			// 論理削除されている
			if ( entity.getDeleteDate() != null ) { 
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, NAME_PK, speechLog.getSpeechLogId() ) );
			}

			// 音声解析ログ詳細取得
			List<SpeechLogDetail> details = speechLogDetailService.getDetails(customUser.getCompanyId(), entity.getSpeechLogId());


			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setEditResult( new EditResult() );
			res.getEditResult().setSpeechLog( entity );
			res.getEditResult().setSpeechLogDetails( details );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * ダウンロード用音声ファイル生成（ユーザ自身のみ）
	 *
	 * @param req 取得条件（PK 項目のみ使用）
	 *
	 * @return ファイルID
	 */
	@POST
	@Path( "/generateVoiceByUser" )
	public SpeechLogResponse generateVoiceByUser( SpeechLogRequest req ) {

		String MNAME = "generateVoiceByUser";
		restlog.start( log, MNAME, req );

		try {
			// ----- 入力チェック

			validator.validateForGenerateVoice( req );

			// ----- 入力データ取得

			SpeechLog speechLog = req.getEditForm().getSpeechLog();

			// ----- レスポンス作成
			SpeechLogResponse res = new SpeechLogResponse();

			// ----- 音声解析ログ取得

			// ユーザ情報取得
			CustomUser customUser = loginUtility.getCustomUser();

			// 音声解析ログ取得
			SpeechLog entity = speechLogService.get( speechLog.getSpeechLogId() );
			if ( entity == null ) {
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, NAME_PK, speechLog.getSpeechLogId() ) );
			}

			// 論理削除されている
			if ( entity.getDeleteDate() != null ) { 
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, NAME_PK, speechLog.getSpeechLogId() ) );
			}

			// 企業IDが異なる
			if ( !entity.getCompanyId().equals(customUser.getCompanyId()) ) {
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, NAME_PK, speechLog.getSpeechLogId() ) );
			}

			// ----- 音声解析ログ詳細一覧取得
			// 音声の有る音声解析ログ詳細取得
			List<SpeechLogDetail> details = speechLogDetailService.searchExistVoiceByUser(customUser.getCompanyId(), customUser.getUserId(), entity.getSpeechLogId());

			if (details == null || details.isEmpty()) {
				// ファイルIDの生成
				String fileId = fileUploadUtility.getNextUploadId("voice");

				// セッションにファイルIDを設定する
				fileIdHolder.setFileId(fileId, speechLog.getSpeechLogId());

				res.setFileId(fileId);

				res.setResult( new RestResult( ResponseCode.OK ) );
			} else {
				// ファイル名リスト生成
				List<java.nio.file.Path> paths = new ArrayList<>();
				// 音声ファイル名の取得
				boolean partialError = false;
				String dirPath  = Paths.get(voiceFileRootDirectory, customUser.getCompanyId(), speechLog.getSpeechLogId().toString()).toString();
				for (SpeechLogDetail each : details) {

					// 圧縮音声ファイルの有無チェック
					java.nio.file.Path filePath = Paths.get(dirPath, String.format(encodedFileName, each.getSpeechLogDetailId()));
					if(filePath.toFile().exists()) {
						paths.add(filePath);
						continue;
					}

					// 圧縮音声が無い場合はwav形式のファイルのパスを設定する
					filePath = Paths.get(dirPath, String.format(voiceFileName, each.getSpeechLogDetailId()));
					if(filePath.toFile().exists()) {
						paths.add(filePath);
						continue;
					}

					// 一部エラー有にする
					partialError = true;
				}

				// ファイルIDの生成
				String fileId = fileUploadUtility.getNextUploadId("voice");

				// セッションにファイルIDを設定する
				fileIdHolder.setFileId(fileId, speechLog.getSpeechLogId());

				// 出力ファイル名
				java.nio.file.Path outputFile = Paths.get(tmpFileDirectory, fileId);

				// 音声ファイルのマージをする
				// 設定ファイルで定義した多重度以上実行されない
				Future<Exception> future = executorService.submit(() -> {
					try {
						ffmpeg.marge(paths, outputFile);
					} catch ( Exception e ) {
						return e;
					}
					return null;
				});

				// 処理終了まで待機する
				Exception exception = future.get();
				if (exception != null) {
					res.setResult( new RestResult( ResponseCode.SPEECH_LOG_DWONLOAD_FAIL_MERGE ) );
				} else {
					if (partialError) {
						res.setResult( new RestResult( ResponseCode.PARTIAL ) );
					} else {
						res.setResult( new RestResult( ResponseCode.OK ) );
					}
				}
				res.setFileId(fileId);
			}

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {
			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	/**
	 * ダウンロード用音声ファイル生成（企業ID配下の全ユーザ）
	 *
	 * @param req 取得条件（PK 項目のみ使用）
	 *
	 * @return ファイルID
	 */
	@POST
	@Path( "/generateVoiceByCompany" )
	public SpeechLogResponse generateVoiceByCompany( SpeechLogRequest req ) {

		String MNAME = "generateVoiceByCompany";
		restlog.start( log, MNAME, req );

		try {
			// ----- 入力チェック

			validator.validateForGenerateVoice( req );

			// ----- 入力データ取得

			SpeechLog speechLog = req.getEditForm().getSpeechLog();

			// ----- レスポンス作成
			SpeechLogResponse res = new SpeechLogResponse();

			// ----- 音声解析ログ取得

			// ユーザ情報取得
			CustomUser customUser = loginUtility.getCustomUser();

			// 音声解析ログ取得
			SpeechLog entity = speechLogService.get( speechLog.getSpeechLogId() );
			if ( entity == null ) {
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, NAME_PK, speechLog.getSpeechLogId() ) );
			}

			// 論理削除されている
			if ( entity.getDeleteDate() != null ) { 
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, NAME_PK, speechLog.getSpeechLogId() ) );
			}

			// 企業IDが異なる
			if ( !entity.getCompanyId().equals(customUser.getCompanyId()) ) {
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, NAME_PK, speechLog.getSpeechLogId() ) );
			}

			// ----- 音声解析ログ詳細一覧取得
			// 音声の有る音声解析ログ詳細取得
			List<SpeechLogDetail> details = speechLogDetailService.searchExistVoiceByCompany(customUser.getCompanyId(), entity.getSpeechLogId());
			if (details == null || details.isEmpty()) {
				// ファイルIDの生成
				String fileId = fileUploadUtility.getNextUploadId("voice");

				// セッションにファイルIDを設定する
				fileIdHolder.setFileId(fileId, speechLog.getSpeechLogId());

				res.setFileId(fileId);

				res.setResult( new RestResult( ResponseCode.OK ) );
			} else {

				// ファイル名リスト生成
				List<java.nio.file.Path> paths = new ArrayList<>();
				// 音声ファイル名の取得
				boolean partialError = false;
				String dirPath  = Paths.get(voiceFileRootDirectory, customUser.getCompanyId(), speechLog.getSpeechLogId().toString()).toString();
				for (SpeechLogDetail each : details) {

					// 圧縮音声ファイルの有無チェック
					java.nio.file.Path filePath = Paths.get(dirPath, String.format(encodedFileName, each.getSpeechLogDetailId()));
					if(filePath.toFile().exists()) {
						paths.add(filePath);
						continue;
					}

					// 圧縮音声が無い場合はwav形式のファイルのパスを設定する
					filePath = Paths.get(dirPath, String.format(voiceFileName, each.getSpeechLogDetailId()));
					if(filePath.toFile().exists()) {
						paths.add(filePath);
						continue;
					}

					// 一部エラー有にする
					partialError = true;
				}

				// ファイルIDの生成
				String fileId = fileUploadUtility.getNextUploadId("voice");

				// セッションにファイルIDを設定する
				fileIdHolder.setFileId(fileId, speechLog.getSpeechLogId());

				// 出力ファイル名
				java.nio.file.Path outputFile = Paths.get(tmpFileDirectory, fileId);

				// 音声ファイルのマージをする
				// 設定ファイルで定義した多重度以上実行されない
				Future<Exception> future = executorService.submit(() -> {
					try {
						ffmpeg.marge(paths, outputFile);
					} catch ( Exception e ) {
						return e;
					}
					return null;
				});

				// 処理終了まで待機する
				Exception exception = future.get();
				if (exception != null) {
					res.setResult( new RestResult( ResponseCode.SPEECH_LOG_DWONLOAD_FAIL_MERGE ) );
				} else {
					if (partialError) {
						res.setResult( new RestResult( ResponseCode.PARTIAL ) );
					} else {
						res.setResult( new RestResult( ResponseCode.OK ) );
					}
				}
				res.setFileId(fileId);
			}
			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {
			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 音声解析ファイルダウンロード(ユーザ自身のみ)
	 *
	 * @param req Httpリクエスト
	 * @param res Httpレスポンス
	 * @param speedLogId 音声解析ログID
	 * @param fileId ファイルID
	 *
	 * @return レスポンス
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path( "/downloadFileByUser/{speedLogId}/{fileId}" )
	public Response downloadFileByUser(@Context HttpServletRequest req, @Context HttpServletResponse res, @PathParam( "speedLogId" ) Long speedLogId,
			@PathParam( "fileId" ) String fileId ) throws IOException {

		String MNAME = "downloadFileByUser";
		restlog.start( log, MNAME, fileId );

		try {

			// ----- 入力チェック

			// セッションにファイルIDがあるかチェックする
			Long sessionSpeechLogId = fileIdHolder.isExistFileId(fileId);
			if (sessionSpeechLogId == null) {
				throw new RestException( new RestResult(ResponseCode.SPEECH_LOG_DWONLOAD_INVALID_FILE_ID) );
			}

			// セッションに音声解析ログIDとリクエストの音声解析ログIDが一致するかチェックする
			if (!sessionSpeechLogId.equals(speedLogId)) {
				throw new RestException( new RestResult(ResponseCode.SPEECH_LOG_DWONLOAD_INVALID_FILE_ID) );
			}

			// ユーザ情報取得
			CustomUser customUser = loginUtility.getCustomUser();

			// 音声解析ログ取得
			SpeechLog speechEntity = speechLogService.get(speedLogId);

			// 音声解析ログ番号
			Long speechLogNo = speechEntity.getSpeechLogNo();

			// 音声解析ログ詳細取得
			List<SpeechLogDetail> details = speechLogDetailService.getDetailsByUser(customUser.getCompanyId(), customUser.getUserId(), Long.valueOf(speedLogId));

			// 音声解析詳細テーブルにデータがない場合
			if ( details == null ) {
				throw new RestException( new RestResult(ResponseCode.SPEECH_LOG_DWONLOAD_NO_DATA) );
			}

			// テキストファイル内容を作成（音声解析ログの内容を改行で連結する）
			String txtLogs = "";
			txtLogs = details.stream().map(each -> each.getLog()).collect(Collectors.joining("\r\n"));

			// CSVファイル内容を作成（音声解析ログの作成日時と内容がカンマでつながって、改行で連結する）
			String csvLogs  = "";
			SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd HH:mm:ss.SSS");
			csvLogs = details.stream().map(each -> "\"" + sdf.format(each.getCreateDate()) + "\",\"" + each.getLog() + "\"" ).collect(Collectors.joining("\r\n"));

			// 音声ファイルの有無チェック(音声解析ログ種別==マイク入力且つ音声ファイル存在する場合、圧縮処理を行う)
			String voicePath  = Paths.get(tmpFileDirectory, fileId).toString();
			java.nio.file.Path voiceFilePath = Paths.get(voicePath);

			if(voiceFilePath.toFile().exists()) {

				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
				ZipOutputStream zipOutputStream =new ZipOutputStream(bufferedOutputStream);

				// テキストファイル圧縮
				String txtFileName = String.format(filenameformat, speechLogNo, "txt");
				zipOutputStream.putNextEntry(new ZipEntry(txtFileName));
				BufferedInputStream txtBis =  new BufferedInputStream(new ByteArrayInputStream(txtLogs.getBytes(txtCsvCharEncoding)));
				byte[] strTxtBuf = new byte[1024];
				IOUtils.copyLarge(txtBis, zipOutputStream, strTxtBuf);
				txtBis.close();
				zipOutputStream.closeEntry();

				// CSVファイル圧縮
				String csvFileName = String.format(filenameformat, speechLogNo, "csv");
				zipOutputStream.putNextEntry(new ZipEntry(csvFileName));
				BufferedInputStream csvBis =  new BufferedInputStream(new ByteArrayInputStream(csvLogs.getBytes(txtCsvCharEncoding)));
				byte[] strCsvBuf = new byte[1024];
				IOUtils.copyLarge(csvBis, zipOutputStream, strCsvBuf);
				csvBis.close();
				zipOutputStream.closeEntry();

				// 音声ファイル圧縮
				String voiceFileName = String.format(filenameformat, speechLogNo, downloadfileExt);
				zipOutputStream.putNextEntry(new ZipEntry(voiceFileName));
				File tmpFile = Paths.get(tmpFileDirectory, fileId).toFile();
				FileInputStream fileInputStream = new FileInputStream(tmpFile);
				byte[] voiceBuf = new byte[16384];
				IOUtils.copyLarge(fileInputStream, zipOutputStream, voiceBuf);
				fileInputStream.close();
				zipOutputStream.closeEntry();

				if (zipOutputStream != null) {
					zipOutputStream.finish();
					zipOutputStream.flush();
					IOUtils.closeQuietly(zipOutputStream);
				}
				IOUtils.closeQuietly(bufferedOutputStream);
				IOUtils.closeQuietly(byteArrayOutputStream);

				// 生成した一時ファイルを削除する
				tmpFile.delete();

				// ----- レスポンス作成
				return Response
						.ok()
						.entity(byteArrayOutputStream.toByteArray())
						.header("Content-Disposition", String.format(downloadHeader, speechLogNo, "zip"))
						.build();

			} else {
				// テキストファイルとCSVファイルのみ圧縮
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
				ZipOutputStream zipOutputStream =new ZipOutputStream(bufferedOutputStream);

				// テキストファイル圧縮
				String txtFileName = String.format(filenameformat, speechLogNo, "txt");
				zipOutputStream.putNextEntry(new ZipEntry(txtFileName));
				BufferedInputStream txtBis =  new BufferedInputStream(new ByteArrayInputStream(txtLogs.getBytes(txtCsvCharEncoding)));
				byte[] strTxtBuf = new byte[1024];
				IOUtils.copyLarge(txtBis, zipOutputStream, strTxtBuf);
				txtBis.close();
				zipOutputStream.closeEntry();

				// CSVファイル圧縮
				String csvFileName = String.format(filenameformat, speechLogNo, "csv");
				zipOutputStream.putNextEntry(new ZipEntry(csvFileName));
				BufferedInputStream csvBis =  new BufferedInputStream(new ByteArrayInputStream(csvLogs.getBytes(txtCsvCharEncoding)));
				byte[] strCsvBuf = new byte[1024];
				IOUtils.copyLarge(csvBis, zipOutputStream, strCsvBuf);
				csvBis.close();
				zipOutputStream.closeEntry();

				if (zipOutputStream != null) {
					zipOutputStream.finish();
					zipOutputStream.flush();
					IOUtils.closeQuietly(zipOutputStream);
				}
				IOUtils.closeQuietly(bufferedOutputStream);
				IOUtils.closeQuietly(byteArrayOutputStream);

				// ----- レスポンス作成
				return Response
						.ok()
						.entity(byteArrayOutputStream.toByteArray())
						.header("Content-Disposition", String.format(downloadHeader, speechLogNo, "zip"))
						.build();
			}

		} catch ( Exception ex ) {
			RestException restException = ExceptionUtil.handleException( log, ResponseCode.SYS_ERROR, null, null, null, ex );

			RestResponse restResponse = new RestResponse() ;
			restResponse.setResultList( restException.getRestResultList() ) ;
			messageUtility.fillMessage( restResponse.getResultList() ) ;
			restlog.abort( restException.getLogger() != null ? restException.getLogger() : log, restResponse, restResponse.getResultList(), restException ) ;

			// ダウンロードエラーページへリダイレクト
			res.sendRedirect(req.getContextPath() + downloadErrorPageUrl);
			return null;
		}
	}

	/**
	 * 音声解析ファイルダウンロード（企業ID配下の全ユーザ）
	 *
	 * @param req Httpリクエスト
	 * @param res Httpレスポンス
	 * @param speedLogId 音声解析ログID
	 * @param fileId ファイルID
	 *
	 * @return レスポンス
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path( "/downloadFileByCompany/{speedLogId}/{fileId}" )
	public Response downloadFileByCompany(@Context HttpServletRequest req, @Context HttpServletResponse res, @PathParam( "speedLogId" ) Long speedLogId,
			@PathParam( "fileId" ) String fileId ) throws IOException {

		String MNAME = "downloadFileByCompany";
		restlog.start( log, MNAME, fileId );

		try {
			// ----- 入力チェック
			// セッションにファイルIDがあるかチェックする
			Long sessionSpeechLogId = fileIdHolder.isExistFileId(fileId);
			if (sessionSpeechLogId == null) {
				throw new RestException( new RestResult(ResponseCode.SPEECH_LOG_DWONLOAD_INVALID_FILE_ID) );
			}

			// セッションに音声解析ログIDとリクエストの音声解析ログIDが一致するかチェックする
			if (!sessionSpeechLogId.equals(speedLogId)) {
				throw new RestException( new RestResult(ResponseCode.SPEECH_LOG_DWONLOAD_INVALID_FILE_ID) );
			}

			// ユーザ情報取得
			CustomUser customUser = loginUtility.getCustomUser();

			// 音声解析ログ取得
			SpeechLog speechEntity = speechLogService.get(speedLogId);

			// 音声解析ログ番号
			Long speechLogNo = speechEntity.getSpeechLogNo();

			// 音声解析ログ詳細取得
			List<SpeechLogDetail> details = speechLogDetailService.getDetails(customUser.getCompanyId(), Long.valueOf(speedLogId));

			// 音声解析詳細テーブルにデータがない場合
			if ( details == null ) {
				throw new RestException( new RestResult(ResponseCode.SPEECH_LOG_DWONLOAD_NO_DATA) );
			}

			// テキストファイル内容を作成（音声解析ログの内容を改行で連結する）
			String txtLogs = "";
			txtLogs = details.stream().map(each -> each.getLog()).collect(Collectors.joining("\r\n"));

			// CSVファイル内容を作成（音声解析ログの作成日時と内容がカンマでつながって、改行で連結する）
			String csvLogs  = "";
			SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd HH:mm:ss.SSS");
			csvLogs = details.stream().map(each -> "\"" + sdf.format(each.getCreateDate()) + "\",\"" + each.getLog() + "\"" ).collect(Collectors.joining("\r\n"));

			// 音声ファイルの有無チェック(音声解析ログ種別==マイク入力且つ音声ファイル存在する場合、圧縮処理を行う)
			String voicePath  = Paths.get(tmpFileDirectory, fileId).toString();
			java.nio.file.Path voiceFilePath = Paths.get(voicePath);

			if(voiceFilePath.toFile().exists()) {

				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
				ZipOutputStream zipOutputStream =new ZipOutputStream(bufferedOutputStream);

				// テキストファイル圧縮
				String txtFileName = String.format(filenameformat, speechLogNo, "txt");
				zipOutputStream.putNextEntry(new ZipEntry(txtFileName));
				BufferedInputStream txtBis =  new BufferedInputStream(new ByteArrayInputStream(txtLogs.getBytes(txtCsvCharEncoding)));
				byte[] strTxtBuf = new byte[1024];
				IOUtils.copyLarge(txtBis, zipOutputStream, strTxtBuf);
				txtBis.close();
				zipOutputStream.closeEntry();

				// CSVファイル圧縮
				String csvFileName = String.format(filenameformat, speechLogNo, "csv");
				zipOutputStream.putNextEntry(new ZipEntry(csvFileName));
				BufferedInputStream csvBis =  new BufferedInputStream(new ByteArrayInputStream(csvLogs.getBytes(txtCsvCharEncoding)));
				byte[] strCsvBuf = new byte[1024];
				IOUtils.copyLarge(csvBis, zipOutputStream, strCsvBuf);
				csvBis.close();
				zipOutputStream.closeEntry();

				// 音声ファイル圧縮
				String voiceFileName = String.format(filenameformat, speechLogNo, downloadfileExt);
				zipOutputStream.putNextEntry(new ZipEntry(voiceFileName));
				File tmpFile = Paths.get(tmpFileDirectory, fileId).toFile();
				FileInputStream fileInputStream = new FileInputStream(tmpFile);
				byte[] voiceBuf = new byte[16384];
				IOUtils.copyLarge(fileInputStream, zipOutputStream, voiceBuf);
				fileInputStream.close();
				zipOutputStream.closeEntry();

				if (zipOutputStream != null) {
					zipOutputStream.finish();
					zipOutputStream.flush();
					IOUtils.closeQuietly(zipOutputStream);
				}
				IOUtils.closeQuietly(bufferedOutputStream);
				IOUtils.closeQuietly(byteArrayOutputStream);

				// 生成した一時ファイルを削除する
				tmpFile.delete();

				// ----- レスポンス作成
				return Response
						.ok()
						.entity(byteArrayOutputStream.toByteArray())
						.header("Content-Disposition", String.format(downloadHeader, speechLogNo, "zip"))
						.build();

			} else {
				// テキストファイルとCSVファイルのみ圧縮
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
				ZipOutputStream zipOutputStream =new ZipOutputStream(bufferedOutputStream);

				// テキストファイル圧縮
				String txtFileName = String.format(filenameformat, speechLogNo, "txt");
				zipOutputStream.putNextEntry(new ZipEntry(txtFileName));
				BufferedInputStream txtBis =  new BufferedInputStream(new ByteArrayInputStream(txtLogs.getBytes(txtCsvCharEncoding)));
				byte[] strTxtBuf = new byte[1024];
				IOUtils.copyLarge(txtBis, zipOutputStream, strTxtBuf);
				txtBis.close();
				zipOutputStream.closeEntry();

				// CSVファイル圧縮
				String csvFileName = String.format(filenameformat, speechLogNo, "csv");
				zipOutputStream.putNextEntry(new ZipEntry(csvFileName));
				BufferedInputStream csvBis =  new BufferedInputStream(new ByteArrayInputStream(csvLogs.getBytes(txtCsvCharEncoding)));
				byte[] strCsvBuf = new byte[1024];
				IOUtils.copyLarge(csvBis, zipOutputStream, strCsvBuf);
				csvBis.close();
				zipOutputStream.closeEntry();

				if (zipOutputStream != null) {
					zipOutputStream.finish();
					zipOutputStream.flush();
					IOUtils.closeQuietly(zipOutputStream);
				}
				IOUtils.closeQuietly(bufferedOutputStream);
				IOUtils.closeQuietly(byteArrayOutputStream);

				// ----- レスポンス作成
				return Response
						.ok()
						.entity(byteArrayOutputStream.toByteArray())
						.header("Content-Disposition", String.format(downloadHeader, speechLogNo, "zip"))
						.build();
			}

		} catch ( Exception ex ) {
			RestException restException = ExceptionUtil.handleException( log, ResponseCode.SYS_ERROR, null, null, null, ex );

			RestResponse restResponse = new RestResponse() ;
			restResponse.setResultList( restException.getRestResultList() ) ;
			messageUtility.fillMessage( restResponse.getResultList() ) ;
			restlog.abort( restException.getLogger() != null ? restException.getLogger() : log, restResponse, restResponse.getResultList(), restException ) ;

			// ダウンロードエラーページへリダイレクト
			res.sendRedirect(req.getContextPath() + downloadErrorPageUrl);
			return null;
		}
	}

	/**
	 * 一括削除
	 *
	 * @param req 取得条件（PK 項目のみ使用）
	 *
	 * @return 処理結果ステータスのみ
	 */
	@POST
	@Path( "/delete" )
	public SpeechLogResponse delete( SpeechLogRequest req ) {

		String MNAME = "delete";
		restlog.start( log, MNAME, req );

		try {

			SpeechLogResponse res = new SpeechLogResponse();
			res.setBulkResultList( new ArrayList<BulkResult>() );

			// ----- 入力チェック

			validator.validateForDelete( req );

			// ----- 入力データ取得

			boolean error = false;

			// ----- 1 件ずつ削除

			for ( SearchResult form : req.getBulkFormList() ) {

				BulkResult result = new BulkResult();

				try {

					deleteOne( form );

					// ----- レスポンス作成

					result.setResult( new RestResult( ResponseCode.OK ) );
					result.setSpeechLog( form.getSpeechLog() );

					messageUtility.fillMessage( result.getResultList() );
					restlog.endOne( log, MNAME, result, result.getResultList() );
				}
				catch ( Exception ex ) {

					error = true;

					result.setResultList( // 応答結果を作成
						ExceptionUtil.handleException( log, ResponseCode.SYS_ERROR, null, null, null, ex )
						.getRestResultList() );
					result.setSpeechLog( form.getSpeechLog() );

					messageUtility.fillMessage( result.getResultList() );
					restlog.abortOne( log, MNAME, result, result.getResultList(), ex );
				}

				res.getBulkResultList().add( result );
			}

			// ----- レスポンス作成

			if ( error ) res.setResult( new RestResult( ResponseCode.PARTIAL ) );
			else res.setResult( new RestResult( ResponseCode.OK ) );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );

		}
	}

	/**
	 * 1 件削除
	 *
	 * @param form 削除する 1 コンテンツの情報
	 *
	 * @throws Exception エラー発生時
	 */
	private void deleteOne( SearchResult form ) throws Exception {

		// ----- 入力チェック

		SpeechLog entity = validator.validateForDeleteOne( form );


		// ----- 論理削除

		// ユーザ情報取得
		CustomUser customUser = loginUtility.getCustomUser();

		// 論理削除日時取得
		Date deleteDate = new Date();

		// 音声解析ログ 1件 論理削除
		entity.setDeleteDate(deleteDate);
		speechLogService.update( entity );

		// 音声解析ログに紐づく音声解析ログ詳細 全件  論理削除
		speechLogDetailService.logicalDeleteAllSpeechLogDetailsByCompany( form.getSpeechLog().getSpeechLogId(),
			customUser.getCompanyId(), customUser.getUserId(), customUser.getUserName(), deleteDate);

	}

}