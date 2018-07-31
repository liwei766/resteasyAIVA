/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechRequest.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.speech;

import javax.xml.bind.annotation.XmlRootElement;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;

/**
 * SpeechRequest API リクエストクラス.<br/>
 */
@XmlRootElement( name="restRequest" )
public class SpeechRequest implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	private String token;

	private String uuid;

	private Integer voiceId;

	private Integer energyThreshold;

	/** 音声解析ログID */
	private Long speechLogId;

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString()
	{
		return ToStringHelper.toString( this );
	}

	/**
	 * @return token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token セットする token
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid セットする uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return voiceId
	 */
	public Integer getVoiceId() {
		return voiceId;
	}

	/**
	 * @param voiceId セットする voiceId
	 */
	public void setVoiceId(Integer voiceId) {
		this.voiceId = voiceId;
	}

	/**
	 * @return energyThreshold
	 */
	public Integer getEnergyThreshold() {
		return energyThreshold;
	}

	/**
	 * @param energyThreshold セットする energyThreshold
	 */
	public void setEnergyThreshold(Integer energyThreshold) {
		this.energyThreshold = energyThreshold;
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
}
