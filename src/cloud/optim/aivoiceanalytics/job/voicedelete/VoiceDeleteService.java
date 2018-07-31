/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：VoiceEncodeQueueService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.job.voicedelete;

import java.io.File;
import java.nio.file.Paths;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.aivoiceanalytics.api.app.speechlog.SpeechLogService;
import cloud.optim.aivoiceanalytics.api.app.speechlogdetail.SpeechLogDetailMapper;
import cloud.optim.aivoiceanalytics.api.app.speechlogdetail.SpeechLogDetailService;
import cloud.optim.aivoiceanalytics.api.entity.SpeechLogDetail;


/**
 * VoiceEncodeQueueService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class VoiceDeleteService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

	/** SpeechLogService */
	@Resource
	private SpeechLogService speechLogService;

	/** SpeechLogDetailService */
	@Resource
	private SpeechLogDetailService speechLogDetailService;

	/**
	 * MyBatis Mapper
	 */
	@Resource
	private SpeechLogDetailMapper speechLogDetailMapper;

	/**
	 * SpeechLogDetailMapper 取得
	 * @return SpeechLogDetailMapper
	 */
	public SpeechLogDetailMapper getSpeechLogDetailMapper() {
		return speechLogDetailMapper;
	}

	// -------------------------------------------------------------------------

	/** 音声ファイル保存ルートディレクトリ */
	@Value( "${speech.voice.file.root.directory}" )
	private String voiceFileRootDirectory;

	/** 分割音声ファイル名 */
	@Value( "${speech.voice.file.name}" )
	private String voiceFileName;

	/** 圧縮音声ファイル名 */
	@Value( "${encode.output.file.name}" )
	private String encodedFileName;

	// -------------------------------------------------------------------------

	/**
	 * 音声解析ログ詳細の削除または音声有無フラグの更新を行い、音声ファイルを削除する
	 * @param entity 音声解析ログ詳細エンティティ
	 * @param speechLogDetailDeleteFlg 音声解析ログ詳細の削除フラグ
	 * @throws Exception
	 */
	public void deleteVoice( SpeechLogDetail entity, Boolean speechLogDetailDeleteFlg) throws Exception {

		if (speechLogDetailDeleteFlg) {
			// 音声解析ログ詳細の削除
			speechLogDetailService.delete(entity.getSpeechLogDetailId());
		} else {
			// 音声有無フラグの更新
			speechLogDetailMapper.updateVoiceExistence(entity.getSpeechLogDetailId());
		}

		// ファイル削除
		String dirPath  = Paths.get(voiceFileRootDirectory, entity.getCompanyId(), entity.getSpeechLogId().toString()).toString();

		// 圧縮音声ファイルの有無チェック
		File targetFile = Paths.get(dirPath, String.format(encodedFileName, entity.getSpeechLogDetailId())).toFile();
		if(!targetFile.exists()) {
			// 圧縮音声が無い場合はwav形式のファイルを設定する
			targetFile = Paths.get(dirPath, String.format(voiceFileName, entity.getSpeechLogDetailId())).toFile();
		}

		// ファイルの削除
		// 圧縮形式とWAV形式のファイルが両方とも存在しない場合はDBの更新のみ行う
		if(targetFile.exists()) {
			if(!targetFile.delete()) {
				throw new Exception("ファイルの削除に失敗しました。");
			}
		}

	}

	/**
	 * 音声解析ログ詳細、音声解析ログテーブルを削除する
	 * @param id 音声解析ログID
	 * @throws Exception
	 */
	public void deleteLog( Long speechLogId) throws Exception {

		// 音声解析ログ詳細の一括削除
		speechLogDetailMapper.deleteAllSpeechLogDetails(speechLogId);

		// 音声解析ログの削除
		speechLogService.delete(speechLogId);
	}

}