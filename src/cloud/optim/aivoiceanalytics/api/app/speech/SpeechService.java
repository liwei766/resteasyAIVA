/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：SpeechService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.speech;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.aivoiceanalytics.api.app.speechlog.SpeechLogService;
import cloud.optim.aivoiceanalytics.api.constant.UseTimeType;
import cloud.optim.aivoiceanalytics.api.entity.SpeechLog;
import cloud.optim.aivoiceanalytics.api.entity.SpeechLogDetail;
import cloud.optim.aivoiceanalytics.api.entity.UseTime;
import cloud.optim.aivoiceanalytics.api.entity.dao.SpeechLogDao;
import cloud.optim.aivoiceanalytics.api.entity.dao.SpeechLogDetailDao;
import cloud.optim.aivoiceanalytics.api.entity.dao.UseTimeDao;
import cloud.optim.aivoiceanalytics.api.recaius.result.SpeechNBestResultDetail;
import cloud.optim.aivoiceanalytics.core.common.utility.Cryptor;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;

import cloud.optim.aivoiceanalytics.core.modules.ffmpeg.FFmpeg;


/**
 * SpeechService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class SpeechService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

	/** SpeechLogService */
	@Resource private SpeechLogService speechLogService;

	/** SpeechLogDao */
	@Resource private SpeechLogDao speechLogDao;

	/** SpeechLogDetailDao */
	@Resource private SpeechLogDetailDao speechLogDetailDao;

	/** UseTimeDao */
	@Resource private UseTimeDao useTimeDao;

	/** FFmpeg  */
	@Resource
	private FFmpeg ffmpeg;

	/** 暗号ユーティリティの共通鍵. */
	@Value("${cryptor.key}")
	private String cryptorKey;

	/** ワークディレクトリ */
	@Value( "${recaius.speech.work.directory}" )
	private String speechWorkDirectory;

	//-----------------------------------------------------------------------

	/**
	 * 通話開始
	 * @param speechLog 通話ログエンティティ
	 * @param useTime 利用時間エンティティ
	 * @param companyId 企業ID
	 * @param userId ユーザID
	 * @param userName ユーザ名
	 * @param type 種別
	 * @param fileName ファイル名
	 */
	public void start( SpeechLog speechLog, UseTime useTime, String companyId, String userId, String userName, String type, String fileName) {

		Date now = new Date();

		// ----- 通話ログの登録
		speechLog.setUpdateDate(now);
		speechLog.setCompanyId(companyId);
		speechLog.setUserId(userId);
		speechLog.setUserName(userName);
		speechLog.setSpeechLogNo(speechLogService.getMaxSpeechLogNo(companyId));
		speechLog.setType(type);
		speechLog.setFileName(fileName);
		speechLog.setStartDate(now);
		speechLog.setEndDate(now);

		speechLog.setCreateDate(now);
		speechLog.setCreateUserId(userId);
		speechLog.setCreateUserName(userName);
		speechLog.setUpdateUserId(userId);
		speechLog.setUpdateUserName(userName);

		speechLogDao.saveOrUpdate(speechLog);

		// 利用時間登録
		useTime.setUpdateDate(now);
		useTime.setCompanyId(companyId);
//		useTime.setCompanyName(companyName); 未登録
		useTime.setUserId(userId);
		useTime.setUserName(userName);
		useTime.setType(UseTimeType.SPEECH.getValue());
		useTime.setStartDate(now);
		useTime.setEndDate(now);
		useTime.setUseTime(0L);

		useTime.setCreateDate(now);
		useTime.setCreateUserId(userId);
		useTime.setCreateUserName(userName);
		useTime.setUpdateUserId(userId);
		useTime.setUpdateUserName(userName);

		useTimeDao.save(useTime);
	}


	/**
	 * 通話ログテーブルの更新
	 * @param speechLogId 通話ログID
	 * @param logs 更新内容
	 * @param companyId 企業ID
	 * @param userId ユーザID
	 * @param userName ユーザ名
	 */
	public void updateSpeechLog( Long speechLogId, List<SpeechNBestResultDetail> logs, String companyId, String userId, String userName, Date now ) {
		if (speechLogId == null) {
			throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_REQUIRED, null, "#speechLog.speechLogId", speechLogId ) );
		}

		// 以下処理は本来バリデータでやる処理だが、通話更新は呼ばれる頻度が高いのでDBアクセスを少なくするためサービスクラスでチェックする
		SpeechLog speechLog =  speechLogDao.get(speechLogId);
		// 取得できない、企業IDが違う
		if (speechLog == null || !companyId.equals(speechLog.getCompanyId())) {
			throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, "#speechLog.speechLogId", speechLogId ) );
		}

		// ----- 通話ログの更新
		speechLog.setEndDate(now);
		speechLog.setUpdateUserId(userId);
		speechLog.setUpdateUserName(userName);
		speechLogDao.update(speechLog);


		// ----- 通話ログ詳細の登録

		// リカイアス解析結果の登録
		for (SpeechNBestResultDetail each : logs) {
			SpeechLogDetail speechLogDetail = new SpeechLogDetail();
			speechLogDetail.setUpdateDate(now);
			speechLogDetail.setCompanyId(companyId);
			speechLogDetail.setSpeechLogId(speechLogId);
			speechLogDetail.setLog(Cryptor.encrypt(this.cryptorKey, each.getResult()));
			speechLogDetail.setBegin(each.getBegin());
			speechLogDetail.setEnd(each.getEnd());
			speechLogDetail.setVoiceExistence(false);

			speechLogDetail.setCreateDate(now);
			speechLogDetail.setCreateUserId(userId);
			speechLogDetail.setCreateUserName(userName);
			speechLogDetail.setUpdateUserId(userId);
			speechLogDetail.setUpdateUserName(userName);

			speechLogDetailDao.save(speechLogDetail);
		}

	}

	/**
	 * 利用時間テーブルの更新
	 * @param useTimeId 利用時間ID
	 * @param time 利用時間
	 * @param companyId 企業ID
	 * @param userId ユーザID
	 * @param userName ユーザ名
	 */
	public void updateUseTime(Long useTimeId, Long time, String companyId, String userId, String userName) {
		if (useTimeId == null) {
			throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_REQUIRED, null, "#useTime.useTimeId", useTimeId ) );
		}

		// ----- 利用時間の取得
		UseTime useTime =  useTimeDao.get(useTimeId);
		// 以下処理は本来バリデータでやる処理だが、通話更新は呼ばれる頻度が高いのでDBアクセスを少なくするためサービスクラスでチェックする
		// 取得できない、企業IDが違う
		if (useTime == null || !companyId.equals(useTime.getCompanyId()) || !userId.equals(useTime.getUserId())) {
			throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, "#useTime.useTimeId", useTimeId ) );
		}

		// ----- 利用時間の更新
		Date now = new Date();
		useTime.setUseTime(time);
		useTime.setEndDate(now);
		useTime.setUpdateUserId(userId);
		useTime.setUpdateUserName(userName);
		useTimeDao.update(useTime);
	}


	/**
	 * FFmpegで音声ファイルを変換
	 * @param fileName ファイル名
	 * @throws Exception
	 */
	public void conversion( String fileName ) throws Exception {

		String dirPath = speechWorkDirectory;

		Path inputFile = Paths.get(dirPath, fileName);
		Path tmpOutputFile = Paths.get(dirPath, "tmp_" + fileName);

		// FFmpegで音声ファイルを変換
		ffmpeg.conversion(inputFile, tmpOutputFile);

		// 元ファイルを削除する
		inputFile.toFile().delete();

		// 変換後のファイルを元ファイル名にリネーム
		if(!tmpOutputFile.toFile().renameTo(inputFile.toFile())) {
			throw new Exception("ファイルのリネームに失敗しました。");
		}

	}

}