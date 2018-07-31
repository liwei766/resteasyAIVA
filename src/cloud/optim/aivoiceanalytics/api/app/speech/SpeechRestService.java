/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.speech;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays ;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import cloud.optim.aivoiceanalytics.api.app.speechlog.SpeechLogService;
import cloud.optim.aivoiceanalytics.api.app.speechlogdetail.SpeechLogDetailService;
import cloud.optim.aivoiceanalytics.api.constant.SpeechLogType;
import cloud.optim.aivoiceanalytics.api.entity.SpeechLog;
import cloud.optim.aivoiceanalytics.api.entity.SpeechLogDetail;
import cloud.optim.aivoiceanalytics.api.entity.UseTime;
import cloud.optim.aivoiceanalytics.api.recaius.SpeechResultType;
import cloud.optim.aivoiceanalytics.api.recaius.result.SpeechNBestResult;
import cloud.optim.aivoiceanalytics.api.recaius.result.SpeechNBestResultDetail;
import cloud.optim.aivoiceanalytics.api.recaius.result.SpeechResult;
import cloud.optim.aivoiceanalytics.api.recaius.service.RecaiusAuthService;
import cloud.optim.aivoiceanalytics.api.recaius.service.RecaiusSpeechService;
import cloud.optim.aivoiceanalytics.api.recaius.util.Util;
import cloud.optim.aivoiceanalytics.api.util.ExtractUtil;
import cloud.optim.aivoiceanalytics.core.common.utility.FileHelper;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.CustomUser;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.LoginUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ExceptionUtil;
import cloud.optim.aivoiceanalytics.core.modules.rest.MessageUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestLog;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;
import cloud.optim.aivoiceanalytics.core.modules.rest.app.fileupload.FileUploadUtility;


/**
 * SpeechRestService 実装.<br/>
 */
@Path( "/speech" )
@Consumes( { "application/json", "application/xml" } )
@Produces( { "application/json", "application/xml" } )
@Component
public class SpeechRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() );

	/** PK の項目名 */
	private static final String SPEECH_LOG_NAME_PK = "#speechLog.speechLogId";

	// -------------------------------------------------------------------------
	/** SpeechService */
	@Resource private SpeechService speechService;

	/** RecaiusAuthService */
	@Resource private RecaiusAuthService authService;

	/** RecaiusSpeechService */
	@Resource private RecaiusSpeechService recaiusSpeechService;

	/** SpeechLogService */
	@Resource private SpeechLogService speechLogService;

	/** SpeechLogDetailService */
	@Resource private SpeechLogDetailService speechLogDetailService;

	/** RestLog */
	@Resource private RestLog restlog;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility;

	/** FileUploadUtility */
	@Resource private FileUploadUtility fileUploadUtility;

	/** オーディオフォーマット */
	@Resource(name="defaultAudioFormat") private AudioFormat audioFormat;

	/** 音声認識情報(セッションスコープに格納されている) */
	@Resource private SpeechInfo speechInfo;

	/** ExtractUtil */
	@Resource private ExtractUtil extractUtil;

	// -------------------------------------------------------------------------

	/** リカイアス音声解析サービスID */
	@Value( "${recaius.service.speech.type}" )
	private String serviceType;

	/** リカイアスセッション有効時間(秒) */
	@Value( "${recaius.session.expiry.sec}" )
	private long expirySec;

	/** リカイアスセッション延長閾値(ミリ秒) */
	@Value( "${recaius.session.extention.threshold}" )
	private long extentionThreshold;

	/** ワークディレクトリ */
	@Value( "${recaius.speech.work.directory}" )
	private String speechWorkDirectory;

	/** 音声ファイル保存ルートディレクトリ */
	@Value( "${speech.voice.file.root.directory}" )
	private String voiceFileRootDirectory;

	/** 一時ファイル保存ディレクトリ */
	@Value( "${speech.tmp.file.directory}" )
	private String tmpFileDirectory;

	/** 分割音声ファイル名 */
	@Value( "${speech.voice.file.name}" )
	private String voiceFileName;

	/** アップロード音声ファイル 変換前拡張子(wav以外で対応可能なもの) */
	@Value( "${speech.upload.file.input.ext}" )
	private String[] uploadFileIutputExt;

	/** アップロード音声ファイル 変換後拡張子 */
	@Value( "${speech.upload.file.output.ext}" )
	private String uploadFileOutputExt;

	// -------------------------------------------------------------------------

	/**
	 * 音声解析開始API
	 *
	 * @param req
	 *
	 * @return 結果
	 */
	@POST
	@Path( "/start" )
	public SpeechResponse start(SpeechRequest req) {

		String MNAME = "start";
		restlog.start( log, MNAME, req );

		// 前回までの結果、利用時間をリセットする
		speechInfo.reset();

		String token = null;
		String uuid = null;

		try {

			// ユーザ情報を取得する
			CustomUser customUser = loginUtility.getCustomUser();
			Integer threshold = customUser.getRecaiusEnergyThreshold();
			Integer modelId = customUser.getRecaiusModelId();
			String serviceId = customUser.getRecaiusServiceId();
			String password = customUser.getRecaiusPassword();

			if(threshold == null || modelId == null || serviceId == null || password == null ) {
				throw new RestException( new RestResult(ResponseCode.CALL_RECAIUS_ERROR) );
			}

			// ----- 認証処理でトークンを取得する
			token = authService.auth(serviceType, serviceId, password);
			speechInfo.setSessionStartTime(System.currentTimeMillis());

			// ----- 音声解析開始
			uuid = recaiusSpeechService.startSession(token, threshold, modelId);


			// ----- 音声解析ログ、利用時間テーブルの登録
			SpeechLog speechLog = new SpeechLog();
			UseTime useTime = new UseTime();

			// 音声解析ログテーブルに登録
			speechService.start(
					speechLog, useTime, customUser.getCompanyId(), customUser.getUserId(),
					customUser.getUserName(), SpeechLogType.MIC.getValue(), "");

			// 音声保存する企業は音声ファイル名の生成、保存ディレクトリの作成を行う
			if (customUser.isSaveVoice()) {
				preparationForSaveVoice(customUser.getCompanyId(), speechLog.getSpeechLogId(), token, uuid);
			}

			// ----- 登録した音声解析ログ IDをセッションに設定
			speechInfo.setSpeechLogId(speechLog.getSpeechLogId());

			// ----- 登録した利用時間IDをセッションに設定
			speechInfo.setUseTimeId(useTime.getUseTimeId());


			// ----- レスポンス作成
			SpeechResponse res = new SpeechResponse();
			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setToken(token);
			res.setUuid(uuid);
			res.setSpeechLogId(speechLog.getSpeechLogId());

			return res;
		}
		catch ( Exception ex ) {

			// セッション終了する
			endSession(token, uuid);

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 音声解析更新API
	 *
	 * @param req
	 *
	 * @return 結果
	 */
	@POST
	@Path( "/update/{token}/{uuid}/{voiceId}" )
	@Consumes( "multipart/form-data" )
	public SpeechResponse update(
			@RequestBody byte[] body,
			@PathParam( "token" ) String token, @PathParam( "uuid" ) String uuid, @PathParam( "voiceId" ) int voiceId) {

		String MNAME = "update";
		restlog.start( log, MNAME, "" );
		try {
			// ユーザ情報を取得する
			CustomUser customUser = loginUtility.getCustomUser();
			Integer threshold = customUser.getRecaiusEnergyThreshold();
			Integer modelId = customUser.getRecaiusModelId();
			String serviceId = customUser.getRecaiusServiceId();
			String password = customUser.getRecaiusPassword();

			if(threshold == null || modelId == null || serviceId == null || password == null ) {
				throw new RestException( new RestResult(ResponseCode.CALL_RECAIUS_ERROR) );
			}

			// ----- リカイアスのセッション有効時間が切れそうなら延長する(残り一分ぐらい)
			if (expirySec * 1000L - (System.currentTimeMillis() - speechInfo.getSessionStartTime()) < extentionThreshold) {
				// リカイアスセッション開始からの経過時間が延長閾値を下回った場合はセッション有効時間の延長を行う
				try {
					authService.extention(serviceType, serviceId, password, token);
					speechInfo.setSessionStartTime(System.currentTimeMillis());
				} catch(Exception e) {
					// 延長できなくても送られてきた音声データの解析を行わないと通話ログがなくなるので握りつぶす
					log.error(e);
				}
			}

			// ----- 音声データをリカイアスへ送信する
			SpeechResult result = recaiusSpeechService.sendData(token, uuid, voiceId, body);

			// 音声保存する企業は音声ファイル名の生成、保存ディレクトリの作成を行う
			if (customUser.isSaveVoice()) {
				try {
					saveTmpFile(body);
				} catch (Exception e) {
					// 例外発生時は以降の処理を続行するためログのみ出力する
					log.error(e);
				}
			}

			// ----- 利用時間をセッションに加算する
			// 1ミリ秒辺りの配列要素数 = サンプリング周波数÷1000 * フレームサイズ
			speechInfo.addTime(body.length / (((int)audioFormat.getFrameRate() / 1000) * audioFormat.getFrameSize()));

			// 音声解析結果を取得する
			List<SpeechNBestResult> speechResult = result.getResultList();

			// リカイアスの音声解析結果のうち、最終結果の解析テキストからフィラーを除去
			removeFiller(speechResult);

			// リカイアスの解析結果から最終結果のみを抽出する
			List<SpeechNBestResultDetail> resultDetails = Util.extractResult(speechResult);

			// リカイアスの解析結果に最終結果が1件以上ある場合は音声解析ログ、利用時間の更新を行う
			if (!resultDetails.isEmpty()) {
				// 更新内容のログテキストを生成
				List<SpeechNBestResultDetail> logs = new ArrayList<>(speechInfo.getUncommittedLog());
				logs.addAll(resultDetails);
				speechInfo.clearUncommittedLog();

				// ----- 音声解析ログの更新
				Date now = new Date();
				try {
					speechService.updateSpeechLog(
							speechInfo.getSpeechLogId(), logs,
							customUser.getCompanyId(), customUser.getUserId(), customUser.getUserName(), now);
				} catch (Exception e) {
					// 登録できなかった音声解析ログの内容をログ出力内する
					String text = speechInfo.getUncommittedLog().stream().map(each -> each.getResult()).collect(Collectors.joining("\r\n\r\n"));
					log.error(String.format("音声解析ログを更新できませんでした：[%s], 利用時間：%d", text, speechInfo.getTime()), e);
					// 更新処理でエラーになった場合は更新できなかったログをセッションに保持する
					speechInfo.addAllLog(logs);
				}

				// 利用時間テーブルの更新
				speechService.updateUseTime(
						speechInfo.getUseTimeId(), speechInfo.getTime(),
						customUser.getCompanyId(), customUser.getUserId(), customUser.getUserName());

			}


			// ----- レスポンス作成

			SpeechResponse res = new SpeechResponse();
			res.setResult( new RestResult( ResponseCode.OK ) );

			// リスト配列に変換
			res.setAnalyzeResult(extractAnalyzeResult(speechResult));

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, null, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {
			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}


	// -------------------------------------------------------------------------


	/**
	 * 音声解析終了API
	 *
	 * @param req
	 *
	 * @return 結果
	 */
	@POST
	@Path( "/end" )
	public SpeechResponse end(SpeechRequest req) {

		String MNAME = "end";
		restlog.start( log, MNAME, req );

		SpeechResult result = null;

		// 音声解析を終了していない場合のみ終了処理を行う
		if (req.getToken() != null && req.getUuid() != null) {
			try {
				// エラーが発生しても終了処理、更新処理は行うため処理は続行する
				result = recaiusSpeechService.flush(req.getToken(), req.getUuid(), req.getVoiceId());
			} catch (Exception e) {
				log.error(e);
			}

			// セッション終了する(例外は発生しても無視する)
			endSession(req.getToken(), req.getUuid());
		}

		try {
			// ユーザ情報を取得する
			CustomUser customUser = loginUtility.getCustomUser();

			// 音声解析結果を取得する
			List<SpeechNBestResult> speechResult = result == null ? new ArrayList<>() : result.getResultList();

			// リカイアスの音声解析結果のうち、最終結果の解析テキストからフィラーを除去
			removeFiller(speechResult);

			// リカイアスの解析結果から最終結果のみを抽出する
			List<SpeechNBestResultDetail> resultDetails = Util.extractResult(speechResult);

			// 更新内容のログテキストを生成
			List<SpeechNBestResultDetail> logs = new ArrayList<>(speechInfo.getUncommittedLog());
			logs.addAll(resultDetails);
			speechInfo.clearUncommittedLog();

			// ----- 音声解析ログの更新
			Date now = new Date();
			try {
				speechService.updateSpeechLog(
						speechInfo.getSpeechLogId(), logs,
						customUser.getCompanyId(), customUser.getUserId(), customUser.getUserName(), now);

			} catch (Exception e) {
				// 登録できなかった音声解析ログの内容をログ出力する
				String text = speechInfo.getUncommittedLog().stream().map(each -> each.getResult()).collect(Collectors.joining("\r\n\r\n"));
				log.error(String.format("音声解析ログを更新できませんでした：[%s], 利用時間：%d", text, speechInfo.getTime()), e);
			}

			// 利用時間テーブルの更新
			speechService.updateUseTime(
					speechInfo.getUseTimeId(), speechInfo.getTime(),
					customUser.getCompanyId(), customUser.getUserId(), customUser.getUserName());

			// 音声保存する企業は音声ファイルの分割を行い再生可能な形式に保存する
			if (customUser.isSaveVoice()) {
				try{
					divideAudioData(speechInfo.getSpeechLogId());
				} catch (Exception e) {
					// 例外発生時は以降の処理を続行するためログのみ出力する
					log.error(e);
				}
			}

			// ----- レスポンス作成
			SpeechResponse res = new SpeechResponse();
			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setAnalyzeResult(extractAnalyzeResult(speechResult));

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
	 * 音声ファイル解析API
	 *
	 * @param req
	 *
	 * @return 結果
	 */
	@POST
	@Path( "/fileAnalyze" )
	@Consumes( "multipart/form-data" )
	public SpeechResponse fileAnalyze(@Context HttpServletRequest request) {

		String MNAME = "fileAnalyze";
		restlog.start( log, MNAME, "" );

		String token = null;
		String uuid = null;
		File tmpFile = null;

		String text = "";

		// 前回までの結果、利用時間をリセットする
		speechInfo.reset();

		// 進捗率:0%
		speechInfo.setProgressRate(0);

		try {

			// ユーザ情報を取得する
			CustomUser customUser = loginUtility.getCustomUser();
			Integer threshold = customUser.getRecaiusEnergyThreshold();
			Integer modelId = customUser.getRecaiusModelId();
			String serviceId = customUser.getRecaiusServiceId();
			String password = customUser.getRecaiusPassword();

			if(threshold == null || modelId == null || serviceId == null || password == null ) {
				throw new RestException( new RestResult(ResponseCode.CALL_RECAIUS_ERROR) );
			}

			// ----- アップロードファイルを一時ファイルとして保存する
			StringBuilder inputFileName = new StringBuilder();
			tmpFile = saveTmpFileUpload(request, inputFileName);

			// 進捗率:5%
			speechInfo.setProgressRate(5);

			// ----- オーディオファイルフォーマットをチェックする
			// WAVEファイルの場合はリカイアスで解析可能な形式に変換する
			tmpFile = checkAudioFormat(request, tmpFile);

			List<SpeechNBestResult> speechResult = new ArrayList<>();
			List<SpeechNBestResult> tmpSpeechResult = new ArrayList<>();
			List<SpeechNBestResultDetail> resultDetails = new ArrayList<>();

			try(

				FileInputStream fileInput = new FileInputStream(tmpFile);
				BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedInput)) {

				// ----- 認証処理でトークンを取得する
				token = authService.auth(serviceType, serviceId, password, 1);
				speechInfo.setSessionStartTime(System.currentTimeMillis());

				// ----- 音声解析開始
				uuid = recaiusSpeechService.startSession(token, threshold, modelId);

				// 進捗率:10%
				speechInfo.setProgressRate(10);

				// ----- 音声解析ログ、利用時間テーブルの登録
				SpeechLog speechLog = new SpeechLog();
				UseTime useTime = new UseTime();

				// 音声解析ログテーブルに登録
				speechService.start(
						speechLog, useTime, customUser.getCompanyId(), customUser.getUserId(),
						customUser.getUserName(), SpeechLogType.FILE.getValue(), inputFileName.toString());

				// 音声保存する企業は音声ファイル名のセッションに設定、保存ディレクトリの作成を行う
				if (customUser.isSaveVoice()) {
					preparationForSaveVoiceUpload(customUser.getCompanyId(), speechLog.getSpeechLogId(), tmpFile);
				}

				// ----- 登録した音声解析ログ IDをセッションに設定
				speechInfo.setSpeechLogId(speechLog.getSpeechLogId());

				// ----- 登録した利用時間IDをセッションに設定
				speechInfo.setUseTimeId(useTime.getUseTimeId());

				// -----  送信率
				double total = audioStream.available(); // 送信ファイルの総データサイズ
				double send = 0; // 送信したファイルのデータサイズ
				Integer progressRate = 0; // 進捗率(0-100%)
				Integer tmpProgressRate = 0;

				// 約1秒分のデータに分割して送る
				int voiceId = 1;
				while(audioStream.available() > 0) {
					int bufsize = audioStream.available() > 16384 * audioFormat.getFrameSize() ? 16384 * audioFormat.getFrameSize() : audioStream.available();
					byte[] buf=new byte[bufsize];
					audioStream.read(buf);

					// ----- 音声データをリカイアスへ送信する
					SpeechResult result = recaiusSpeechService.sendData(token, uuid, voiceId++, buf);

					// ----- 利用時間をセッションに加算する
					// 1ミリ秒辺りの配列要素数 = サンプリング周波数÷1000 * フレームサイズ
					speechInfo.addTime(buf.length / (((int)audioFormat.getFrameRate() / 1000) * audioFormat.getFrameSize()));

					// 送信済データサイズを加算する
					send += buf.length;

					// 送信率から進捗率(10%から80%)をセット
					tmpProgressRate = 10 + (int)( ( (send / total) * 0.7 ) * 100 ) ;

					// セッションの進捗率を更新
					if (tmpProgressRate.compareTo(progressRate) > 0) {
						progressRate = tmpProgressRate;
						speechInfo.setProgressRate(progressRate);
					}

					// 音声解析結果（今回分）を取得する
					tmpSpeechResult = result.getResultList();

					// リカイアスの音声解析結果（今回分）のうち、最終結果の解析テキストからフィラーを除去
					removeFiller(tmpSpeechResult);

					// 解析結果（今回分）を全体の解析結果に追加する
					speechResult.addAll(tmpSpeechResult);

					// リカイアスの解析結果から最終結果のみを抽出する
					resultDetails = Util.extractResult(tmpSpeechResult);

					// リカイアスの解析結果に最終結果が1件以上ある場合は音声解析ログ、利用時間の更新を行う
					if (!resultDetails.isEmpty()) {
						// 更新内容のログテキストを生成
						List<SpeechNBestResultDetail> logs = new ArrayList<>(speechInfo.getUncommittedLog());
						logs.addAll(resultDetails);
						speechInfo.clearUncommittedLog();

						// ----- 音声解析ログの更新
						Date now = new Date();
						try {
							speechService.updateSpeechLog(
									speechInfo.getSpeechLogId(), logs,
									customUser.getCompanyId(), customUser.getUserId(), customUser.getUserName(), now);
						} catch (Exception e) {
							// 登録できなかった音声解析ログの内容をログ出力内する
							text = speechInfo.getUncommittedLog().stream().map(each -> each.getResult()).collect(Collectors.joining("\r\n\r\n"));
							log.error(String.format("音声解析ログを更新できませんでした：[%s], 利用時間：%d", text, speechInfo.getTime()), e);
							// 更新処理でエラーになった場合は更新できなかったログをセッションに保持する
							speechInfo.addAllLog(logs);
						}

						// 利用時間テーブルの更新
						speechService.updateUseTime(
								speechInfo.getUseTimeId(), speechInfo.getTime(),
								customUser.getCompanyId(), customUser.getUserId(), customUser.getUserName());
					}

				}

				// 進捗率:80%
				speechInfo.setProgressRate(80);

				// 最終結果を取得する
				// エラーが発生しても終了処理、更新処理は行うため処理は続行する
				SpeechResult result = recaiusSpeechService.flush(token, uuid, voiceId);

				// 音声解析結果（今回分）を取得する
				tmpSpeechResult = result.getResultList();

				// リカイアスの音声解析結果のうち、最終結果の解析テキストからフィラーを除去
				removeFiller(tmpSpeechResult);

				// 解析結果（今回分）を全体の解析結果に追加する
				speechResult.addAll(tmpSpeechResult);

				// リカイアスの解析結果（今回分）から最終結果のみを抽出する
				resultDetails = Util.extractResult(tmpSpeechResult);

				// 更新内容のログテキストを生成
				List<SpeechNBestResultDetail> logs = new ArrayList<>(speechInfo.getUncommittedLog());
				logs.addAll(resultDetails);
				speechInfo.clearUncommittedLog();

				// ----- 音声解析ログの更新
				Date now = new Date();
				try {
					speechService.updateSpeechLog(
							speechInfo.getSpeechLogId(), logs,
							customUser.getCompanyId(), customUser.getUserId(), customUser.getUserName(), now);
				} catch (Exception e) {
					// 登録できなかった音声解析ログの内容をログ出力する
					text = speechInfo.getUncommittedLog().stream().map(each -> each.getResult()).collect(Collectors.joining("\r\n\r\n"));
					log.error(String.format("音声解析ログを更新できませんでした：[%s], 利用時間：%d", text, speechInfo.getTime()), e);
				}


			} catch (UnsupportedAudioFileException e) {
				// オーディオファイルでない場合
				throw new RestException(new RestResult(ResponseCode.SPEECH_AUDIO_UNSUPPORTED_FILE ), e);
			}

			// 進捗率:90%
			speechInfo.setProgressRate(90);

			// ----- 利用時間テーブル登録
			speechService.updateUseTime(
					speechInfo.getUseTimeId(), speechInfo.getTime(),
					customUser.getCompanyId(), customUser.getUserId(), customUser.getUserName());


			// 音声保存する企業は音声ファイルの分割を行い再生可能な形式に保存する
			if (customUser.isSaveVoice()) {
				try{
			 		divideAudioData(speechInfo.getSpeechLogId());

					// tmpFileはdivideAudioData()内で削除される
					tmpFile = null;

				} catch (Exception e) {
					// 例外発生時は以降の処理を続行するためログのみ出力する
					log.error(e);
				}
			}

			// ----- レスポンス作成

			SpeechResponse res = new SpeechResponse();
			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setAnalyzeResult(extractAnalyzeResult(speechResult));
			res.setSpeechLogId(speechInfo.getSpeechLogId());
			res.setTime(speechInfo.getTime());

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, null, res, res.getResultList() );

			// 進捗率:100%
			speechInfo.setProgressRate(100);

			return res;
		}
		catch ( Exception ex ) {
			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		 } finally {
			// セッション終了する(例外は発生しても無視する)
			endSession(token, uuid);

			// 一時ファイルを削除する
			if(tmpFile != null) tmpFile.delete();
		 }
	}


	/**
	 * ファイル解析の進捗を取得する
	 *
	 * @return 取得結果
	 */
	@POST
	@Path( "/getProgressRate" )
	public SpeechResponse getProgressRate() {

		String MNAME = "getProgressRate";
		restlog.start( log, MNAME, null );

		log.debug("getProgressRateApi["+speechInfo.getProgressRate()+"]");

		try {

			// ----- レスポンス作成
			SpeechResponse res = new SpeechResponse();
			res.setProgressRate(speechInfo.getProgressRate());
			res.setResult( new RestResult( ResponseCode.OK ) );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, null, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {
			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	/**
	 * アップロードされたファイルをワークディレクトリに保存する
	 * @param request リクエスト
	 * @param inputFileName 入力ファイル名
	 * @return ファイルパス
	 * @throws IOException
	 * @throws
	 */
	private File saveTmpFileUpload(HttpServletRequest request, StringBuilder inputFileName) throws Exception {

		// 出力ファイルパスの生成
		String fileName = String.join("_", String.valueOf(System.currentTimeMillis()), request.getSession().getId());
		File file = new File(speechWorkDirectory + fileName);

		// リクエストからアップロードされたファイルの入力ストリームを取得する
		try(InputStream in = fileUploadUtility.getUploadInputStreamAndInputFileName(request, inputFileName)) {
			try (FileOutputStream out = new FileOutputStream(file)) {
				int size;
				byte[] buf = new byte[5120];
				while ( ( size = in.read( buf ) ) != -1 ) {
					out.write( buf, 0, size ) ;
				}
				out.flush() ;
			}
		}

		String ext = FileHelper.getExtension(inputFileName.toString());

		ext = ext.toLowerCase();

		if (ext.equals(uploadFileOutputExt)) {
			// wavはそのまま返す
			return file;

		} else {

			if(Arrays.asList(uploadFileIutputExt).contains(ext)){
				// wav以外で対応可能な拡張子のファイルはwavに変換
				speechService.conversion(fileName);

				return file;

			} else {
				// 対応可能でない拡張子のファイル時はエラー
				throw new RestException(new RestResult(ResponseCode.SPEECH_AUDIO_UNSUPPORTED_FILE ));
			}

		}

	}

	/**
	 * 音声ファイルのフォーマットをチェックする
	 * @param request リクエスト
	 * @return ファイルパス
	 * @throws IOException
	 * @throws
	 */
	private File checkAudioFormat(HttpServletRequest request, File tmpFile) throws Exception {
		try(
			FileInputStream fileInput = new FileInputStream(tmpFile);
			BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedInput)) {

			// ----- オーディオファイルフォーマットをチェックする
			AudioFormat format = audioStream.getFormat();
			if(audioFormat.matches(format)) return tmpFile;

			// WAVEファイルの形式を変換する

			// 出力ファイルパスの生成
			// ファイル名が前の名前とかぶらないようにするために念のため1ミリ秒スリープする
			Thread.sleep(1);
			String fileName = String.join("_", String.valueOf(System.currentTimeMillis()), request.getSession().getId());
			File convertedFile = new File(speechWorkDirectory + fileName);

			try(AudioInputStream outStream = AudioSystem.getAudioInputStream(audioFormat, audioStream)) {
				AudioSystem.write(outStream, AudioFileFormat.Type.WAVE, convertedFile);
			}

			// 前の一時ファイルを不要なので削除する
			tmpFile.delete();

			return convertedFile;
		} catch (UnsupportedAudioFileException e) {
			// オーディオファイルでない場合
			throw new RestException(new RestResult(ResponseCode.SPEECH_AUDIO_UNSUPPORTED_FILE ), e);
		}

	}


	// -------------------------------------------------------------------------
	/**
	 * リカイアス音声認識を終了する。終了時に発生した例外はスローしない。
	 * @param token 認証トークン
	 * @param uuid UUID
	 */
	private void endSession (String token, String uuid) {
		// リカイアス音声認識を終了する
		if (uuid != null) {
			try {
				recaiusSpeechService.stopSession(token, uuid);
			} catch (Exception e) {
				log.error(e);
			}
		}

		// リカイアス認証トークンを削除する
		if (token != null) {
			try {
				authService.disconnect(token);
			} catch (Exception e) {
				log.error(e);
			}
		}
	}

	// -------------------------------------------------------------------------

	/**
	 *  リカイアスの解析結果をレスポンスの形式に変換する
	 * @param speechResult リカイアス解析結果
	 * @return レスポンスの形式の解析結果
	 */
	private List<AnalyzeResult> extractAnalyzeResult (List<SpeechNBestResult> speechResult) {
		List<AnalyzeResult> result = new ArrayList<>();

		if (speechResult.isEmpty()) return result;

		for (SpeechNBestResult each : speechResult) {
			SpeechNBestResultDetail detail = each.getResultDetail();

			AnalyzeResult analyzeResult = new AnalyzeResult();

			analyzeResult.setType(each.getType());
			analyzeResult.setStr(detail.getResult());

			analyzeResult.setTime(detail.getBegin());
			result.add(analyzeResult);
		}

		return result;
	}

	/**
	 * リカイアスの音声解析結果のうち、最終結果の解析テキストからフィラーを除去
	 * @param speechResult リカイアス解析結果
	 *
	 */
	private void removeFiller(List<SpeechNBestResult> speechResult) {

		if (speechResult.isEmpty()) return;

		String removeResult;

		for (SpeechNBestResult each : speechResult) {

			SpeechNBestResultDetail detail = each.getResultDetail();

			if (SpeechResultType.RESULT.toString().equals(each.getType())) {

				removeResult = extractUtil.removeFiller(detail.getResult());

				detail.setResult(removeResult);

				each.setResultDetail(detail);

			}

		}

	}

	// -------------------------------------------------------------------------
	// 音声ファイル操作系の処理
	// -------------------------------------------------------------------------

	/**
	 * 音声ファイル保存準備（マイク入力用）
	 * @param companyId
	 * @param speechLogId
	 * @param token
	 * @param uuid
	 * @throws IOException
	 */
	private void preparationForSaveVoice(String companyId, Long speechLogId, String token, String uuid) throws IOException {
		// 一時ファイル名の生成
		speechInfo.setTmpFileName(String.format("%s%d_%s_%s", tmpFileDirectory, speechLogId, token, uuid));

		// 音声ファイル保存ディレクトリの作成
		Files.createDirectories(Paths.get(voiceFileRootDirectory, companyId, speechLogId.toString()));
	}

	/**
	 * 音声ファイル保存準備（ファイル入力用）
	 * @param companyId
	 * @param speechLogId
	 * @param token
	 * @param uuid
	 * @throws IOException
	 */
	private void preparationForSaveVoiceUpload(String companyId, Long speechLogId, File tmpFile) throws IOException {
		// 一時ファイル名のセット
		speechInfo.setTmpFileName(String.format("%s", tmpFile));

		// 音声ファイル保存ディレクトリの作成
		Files.createDirectories(Paths.get(voiceFileRootDirectory, companyId, speechLogId.toString()));
	}

	/**
	 * 音声データを一時ファイルに保存する
	 * @param data 音声データ
	 * @throws IOException
	 */
	private void saveTmpFile(byte[] data) throws IOException {
		try (FileOutputStream out = new FileOutputStream(Paths.get(speechInfo.getTmpFileName()).toFile(), true)) {
			out.write( data ) ;
			out.flush() ;
		}
	}

	/**
	 * 一時保存した音声データ音声データをリカイアス解析結果の文節毎に分割し、再生可能なファイルに保存する
	 * @param speechLogId 音声解析ログID
	 * @throws Exception
	 */
	private void divideAudioData(Long speechLogId) throws Exception {

		// ユーザ情報を取得する
		CustomUser customUser = loginUtility.getCustomUser();

		// 音声が生成されていない通話ログ詳細を取得する
		List<SpeechLogDetail> detailList = speechLogDetailService.searchNoVoice(customUser.getCompanyId(), speechLogId);

		try (FileInputStream tmpFile = new FileInputStream(Paths.get(speechInfo.getTmpFileName()).toFile())) {

			SpeechLogDetail prev = null;
			for(int i = 0; i < detailList.size(); i++) {
				// 対象の要素と次の要素を取得する
				SpeechLogDetail each = detailList.get(i);
				SpeechLogDetail next = i + 1 < detailList.size() ? detailList.get(i + 1) : null;

				// 開始秒数の取得
				// 先頭の要素の場合は始めから、それ以外の場合は前の要素の終了+1から開始する
				Integer begin = prev == null ?   0 : prev.getEnd() + 1;

				// 終了秒数の取得
				// 末尾の要素の場合はファイルの最後まで、それ以外の場合は対象要素の終了+1から開始する
				Integer end = next == null ?  null : each.getEnd();

				// 読み込むサイズの取得
				int size = end == null ? tmpFile.available() :  (end - begin) * ((int)audioFormat.getFrameRate() / 1000) * audioFormat.getFrameSize();

				// バイトデータを一時ファイルから読む
				byte[] buf = new byte[size];
				tmpFile.read(buf);

				// バイト配列をwaveファイルにする
				try (
					ByteArrayInputStream byteArrayInput = new ByteArrayInputStream(buf);
					AudioInputStream audioFile = new AudioInputStream(byteArrayInput, audioFormat, size / audioFormat.getFrameSize())) {

					String fileName = String.format(voiceFileName, each.getSpeechLogDetailId());
					AudioSystem.write(
							audioFile,
							AudioFileFormat.Type.WAVE,
							Paths.get(voiceFileRootDirectory, customUser.getCompanyId(), speechLogId.toString(), fileName).toFile());
				}

				// 通話ログ詳細を更新する
				each.setVoiceExistence(true);
				each.setUpdateUserId(customUser.getUserId());
				each.setUpdateUserName(customUser.getUserName());
				speechLogDetailService.updateAndRegistQueue(each);

				// 前要素設定
				prev = each;
			}
		}

		// 一時ファイル削除する
		Files.delete(Paths.get(speechInfo.getTmpFileName()));
	}

	// -------------------------------------------------------------------------

	/**
	 * 音声解析再開API
	 *
	 * @param req
	 *
	 * @return 結果
	 */
	@POST
	@Path( "/resume" )
	public SpeechResponse resume(SpeechRequest req) {

		String MNAME = "resume";
		restlog.start( log, MNAME, req );

		String token = null;
		String uuid = null;

		try {

			// ユーザ情報を取得する
			CustomUser customUser = loginUtility.getCustomUser();
			Integer threshold = customUser.getRecaiusEnergyThreshold();
			Integer modelId = customUser.getRecaiusModelId();
			String serviceId = customUser.getRecaiusServiceId();
			String password = customUser.getRecaiusPassword();

			if(threshold == null || modelId == null || serviceId == null || password == null ) {
				throw new RestException( new RestResult(ResponseCode.CALL_RECAIUS_ERROR) );
			}

			// ----- 音声解析ログ取得
			SpeechLog speechLog = getSpeechLog(req.getSpeechLogId(), customUser.getCompanyId());

			// 音声解析者がログインユーザかチェックする
			if (speechLog == null || !customUser.getUserId().equals(speechLog.getUserId())) {
				throw new RestException( new RestResult(ResponseCode.CALL_RESUME_ERROR) );
			}

			// ----- 認証処理でトークンを取得する
			token = authService.auth(serviceType, serviceId, password);
			speechInfo.setSessionStartTime(System.currentTimeMillis());

			// ----- 音声解析開始
			uuid = recaiusSpeechService.startSession(token, threshold, modelId);

			// 音声保存する企業は音声ファイル名の生成、保存ディレクトリの作成を行う
			if (customUser.isSaveVoice()) {
				preparationForSaveVoice(customUser.getCompanyId(), speechLog.getSpeechLogId(), token, uuid);
			}

			// ----- レスポンス作成
			SpeechResponse res = new SpeechResponse();
			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setToken(token);
			res.setUuid(uuid);
			res.setSpeechLogId(speechLog.getSpeechLogId());

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			// セッション終了する
			endSession(token, uuid);

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	/**
	 * 音声解析ログを取得する
	 * @param speechLogId 音声解析ログID
	 * @param companyId 企業ID
	 * @return 音声解析ログ
	 */
	private SpeechLog getSpeechLog(Long speechLogId, String companyId) {
		if(speechLogId == null) return new SpeechLog();

		// 音声解析ログを取得する
		SpeechLog result = speechLogService.get(speechLogId);

		// 取得できない
		if (result == null) {
			throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, SPEECH_LOG_NAME_PK, speechLogId ) );
		}

		// 企業IDが違う
		if (!companyId.equals(result.getCompanyId())) {
			throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, SPEECH_LOG_NAME_PK, speechLogId ) );
		}

		return result;
	}

}