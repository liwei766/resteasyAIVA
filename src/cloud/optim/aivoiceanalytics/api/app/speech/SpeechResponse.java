/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechResponse.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.speech;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;

/**
 * SpeechResponse API レスポンスクラス.<br/>
 */
/**
 * @author raifuyor
 *
 */
@XmlRootElement( name="restResponse" )
public class SpeechResponse implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** 処理結果 */
	private List<RestResult> resultList = new ArrayList<RestResult>();


	/** リカイアス認証トークン */
	private String token;

	/** リカイアスUUID */
	private String uuid;

	/** 音声解析ログID */
	private Long speechLogId;

	/** 音声解析ログ番号 */
	private Long speechLogNo;

	/** 音声解析者ログID */
	private Long speechUserId;

	/** 利用時間ID */
	private Long useTimeId;

	/** 利用時間 */
	private long time;

	/** 解析結果 */
	private List<AnalyzeResult> analyzeResult;

	/** 進捗率 */
	private float progressRate;




	// -------------------------------------------------------------------------


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

	// -------------------------------------------------------------------------
	// 処理結果を扱う処理
	// -------------------------------------------------------------------------

	/**
	 * 処理結果数を取得する.
	 *
	 * @return 登録されている処理結果数
	 */
	@XmlTransient
	@JsonIgnore
	public int getResultLength() {

		if ( resultList == null ) return 0;

		return resultList.size();
	}

	/**
	 * 処理結果を登録する.
	 *
	 * @param result 登録する処理結果
	 */
	public void addResult( RestResult result ) {

		if ( resultList == null )
		{
			resultList = new ArrayList<RestResult>();
		}

		resultList.add( result );
	}

	/**
	 * 処理結果を登録する.
	 *
	 * @param result 登録する処理結果
	 */
	public void setResult( RestResult result ) {

		if ( resultList == null )
		{
			resultList = new ArrayList<RestResult>();
		}

		resultList.clear();
		resultList.add( result );
	}

	// -------------------------------------------------------------------------
	// アクセサメソッド
	// -------------------------------------------------------------------------

	/**
	 * resultList 取得.
	 *
	 * @return resultList
	 */
	@XmlElementWrapper( name="resultList" )
	@XmlElement( name="result" )
	@JsonProperty( "resultList" )
	public List<RestResult> getResultList() {

		return resultList;
	}

	/**
	 * resultList 設定.
	 *
	 * @param resultList resultList に設定する値.
	 */
	public void setResultList( List<RestResult> resultList ) {

		this.resultList = resultList;
	}

	/**
	 * token 取得.
	 *
	 * @return token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * token 設定.
	 *
	 * @param token token に設定する値.
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * uuid 取得.
	 *
	 * @return uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * uuid 設定.
	 *
	 * @param uuid uuid に設定する値.
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * speechLogId 取得.
	 *
	 * @return speechLogId
	 */
	public Long getSpeechLogId() {
		return speechLogId;
	}

	/**
	 * speechLogId 設定.
	 *
	 * @param speechLogId speechLogId に設定する値.
	 */
	public void setSpeechLogId(Long speechLogId) {
		this.speechLogId = speechLogId;
	}

	/**
	 * speechLogNo 取得.
	 *
	 * @return speechLogNo
	 */
	public Long getSpeechLogNo() {
		return speechLogNo;
	}

	/**
	 * speechLogNo 設定.
	 *
	 * @param speechLogNo speechLogNo に設定する値.
	 */
	public void setSpeechLogNo(Long speechLogNo) {
		this.speechLogNo = speechLogNo;
	}

	/**
	 * speechUserId 取得.
	 *
	 * @return speechUserId
	 */
	public Long getSpeechUserId() {
		return speechUserId;
	}

	/**
	 * speechUserId 設定.
	 *
	 * @param speechUserId speechUserId に設定する値.
	 */
	public void setSpeechUserId(Long speechUserId) {
		this.speechUserId = speechUserId;
	}

	/**
	 * useTimeId 取得.
	 *
	 * @return useTimeId
	 */
	public Long getUseTimeId() {
		return useTimeId;
	}

	/**
	 * useTimeId 設定.
	 *
	 * @param useTimeId useTimeId に設定する値.
	 */
	public void setUseTimeId(Long useTimeId) {
		this.useTimeId = useTimeId;
	}

	/**
	 * time 取得.
	 *
	 * @return time
	 */
	public Long getTime() {
		return time;
	}

	/**
	 * time 設定.
	 *
	 * @param time time に設定する値.
	 */
	public void setTime(Long time) {
		this.time = time;
	}

	/**
	 * analyzeResult 取得.
	 *
	 * @return analyzeResult
	 */
	public List<AnalyzeResult> getAnalyzeResult() {
		return analyzeResult;
	}

	/**
	 * analyzeResult 設定.
	 *
	 * @param analyzeResult analyzeResult に設定する値.
	 */
	public void setAnalyzeResult(List<AnalyzeResult> analyzeResult) {
		this.analyzeResult = analyzeResult;
	}

	public float getProgressRate() {
		return progressRate;
	}

	public void setProgressRate(float progressRate) {
		this.progressRate = progressRate;
	}
}
