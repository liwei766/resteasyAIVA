/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechInfo.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.speech;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.api.recaius.result.SpeechNBestResultDetail;

/**
 * 通話中の情報をセッションに保持するためのクラス
 */
@Component
@Scope( proxyMode=ScopedProxyMode.TARGET_CLASS, value="session" )
public class SpeechInfo implements Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** 利用時間 */
	private long time;

	/** 未登録通話ログ */
	private List<SpeechNBestResultDetail> uncommittedLog;

	/** リカイアスセッション開始時間 */
	private long sessionStartTime;

	/** 音声解析ログ ID */
	private Long speechLogId;

	/** 利用時間ID */
	private Long useTimeId;

	/** 一時保存ファイル名 */
	private String tmpFileName;

	/** 進捗率 */
	private Integer progressRate;

	/**
	 * クリアする
	 */
	public void reset() {
		this.time = 0L;
		this.uncommittedLog = new ArrayList<>();
		this.sessionStartTime = 0L;
		this.tmpFileName = null;
		this.useTimeId = null;
	}

	/**
	 * @return time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time 加算する time
	 */
	public void addTime(long time) {
		this.time += time;
	}

	/**
	 * @param time セットする time
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * @return uncommittedLog
	 */
	public List<SpeechNBestResultDetail> getUncommittedLog() {
		return uncommittedLog;
	}

	/**
	 * @param log セットする log
	 */
	public void addLog(SpeechNBestResultDetail log) {
		this.uncommittedLog.add(log);
	}

	/**
	 * @param log セットする log
	 */
	public void addAllLog(List<SpeechNBestResultDetail> logs) {
		this.uncommittedLog.addAll(logs);
	}

	/**
	 * @param 未コミットログをクリアする
	 */
	public void clearUncommittedLog() {
		this.uncommittedLog.clear();
	}

	/**
	 * @return sessionStartTime
	 */
	public long getSessionStartTime() {
		return sessionStartTime;
	}

	/**
	 * @param sessionStartTime セットする sessionStartTime
	 */
	public void setSessionStartTime(long sessionStartTime) {
		this.sessionStartTime = sessionStartTime;
	}

	/**
	 * @return useTimeId
	 */
	public Long getUseTimeId() {
		return useTimeId;
	}

	/**
	 * @param useTimeId セットする useTimeId
	 */
	public void setUseTimeId(Long useTimeId) {
		this.useTimeId = useTimeId;
	}

	/**
	 * @return tmpFileName
	 */
	public String getTmpFileName() {
		return tmpFileName;
	}

	/**
	 * @param tmpFileName セットする tmpFileName
	 */
	public void setTmpFileName(String tmpFileName) {
		this.tmpFileName = tmpFileName;
	}

	/**
	 * @return speechLogId
	 */
	public Long getSpeechLogId() {
		return speechLogId;
	}

	/**
	 * @param speechLogId セットする speechLogId
	 */
	public void setSpeechLogId(Long speechLogId) {
		this.speechLogId = speechLogId;
	}

	/**
	 * @return progressRate
	 */
	public Integer getProgressRate() {
		return progressRate;
	}

	/**
	 * @param progressRate セットする progressRate
	 */
	public void setProgressRate(Integer progressRate) {
		this.progressRate = progressRate;
	}


}
