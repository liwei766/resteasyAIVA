/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechLogDetailSearchForm.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.speechlogdetail;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.commons.lang3.builder.ToStringBuilder ;
import org.apache.commons.lang3.builder.ToStringStyle ;

import cloud.optim.aivoiceanalytics.core.common.utility.QueryHelper;

/**
 * SpeechLogDetail 検索フォーム.<br/>
 *
 */
public class SpeechLogDetailSearchForm implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;


	/**
	 * 文字列表現への変換
	 *
	 * @return このインスタンスの文字列表現
	 */
	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(
			this, ToStringStyle.DEFAULT_STYLE ) ;
	}


	// ------------------------------------------------------------
	// 検索条件
	// ------------------------------------------------------------

	// ------------------------------------------------------------
	/** 音声解析ログ詳細 ID  */
	private String speechLogDetailId = null;

	/**
	 *	音声解析ログ詳細 ID取得
	 *	@return speechLogDetailId 音声解析ログ詳細 ID
	 */
	public String getSpeechLogDetailId() {
		return this.speechLogDetailId;
	}

	/**
	 *	音声解析ログ詳細 ID設定
	 *	@param speechLogDetailId 音声解析ログ詳細 ID
	 */
	public void setSpeechLogDetailId(String speechLogDetailId) {
		this.speechLogDetailId = speechLogDetailId;
	}


	/** 音声解析ログ詳細 ID検索範囲（開始）  */
	private String speechLogDetailIdFrom = null;
	/** 音声解析ログ詳細 ID検索範囲（終了）  */
	private String speechLogDetailIdTo = null;
	/** 音声解析ログ詳細 ID未登録検索設定項目  */
	private Boolean speechLogDetailIdNull = null;

	/**
	 *	音声解析ログ詳細 ID検索範囲（開始）取得
	 *	@return speechLogDetailIdFrom 音声解析ログ詳細 ID検索範囲（開始）
	 */
	public String getSpeechLogDetailIdFrom() {
		return this.speechLogDetailIdFrom;
	}

	/**
	 *	音声解析ログ詳細 ID検索範囲（開始）設定
	 *	@param speechLogDetailIdFrom 音声解析ログ詳細 ID検索範囲（開始）
	 */
	public void setSpeechLogDetailIdFrom(String speechLogDetailIdFrom) {
		this.speechLogDetailIdFrom = speechLogDetailIdFrom;
	}

	/**
	 *	音声解析ログ詳細 ID検索範囲（終了）取得
	 *	@return speechLogDetailIdTo 音声解析ログ詳細 ID検索範囲（終了）
	 */
	public String getSpeechLogDetailIdTo() {
		return this.speechLogDetailIdTo;
	}

	/**
	 *	音声解析ログ詳細 ID検索範囲（終了）設定
	 *	@param speechLogDetailIdTo 音声解析ログ詳細 ID検索範囲（終了）
	 */
	public void setSpeechLogDetailIdTo(String speechLogDetailIdTo) {
		this.speechLogDetailIdTo = speechLogDetailIdTo;
	}

	/**
	 *	音声解析ログ詳細 ID未登録検索設定項目取得
	 *	@return speechLogDetailIdNull 音声解析ログ詳細 ID未登録検索設定項目
	 */
	public Boolean getSpeechLogDetailIdNull() {
		return this.speechLogDetailIdNull;
	}

	/**
	 *	音声解析ログ詳細 ID未登録検索設定項目設定
	 *	@param speechLogDetailIdNull 音声解析ログ詳細 ID未登録検索設定項目
	 */
	public void setSpeechLogDetailIdNull(Boolean speechLogDetailIdNull) {
		this.speechLogDetailIdNull = speechLogDetailIdNull;
	}


	// ------------------------------------------------------------
	/** 企業 ID  */
	private String companyId = null;
	/** 企業 ID未登録検索設定項目  */
	private Boolean companyIdNull = null;
	/** 企業 ID検索オプション項目  */
	private String companyIdOption = null;

	/**
	 *	企業 ID取得
	 *	@return companyId 企業 ID
	 */
	public String getCompanyId() {
		return this.companyId;
	}

	/**
	 *	企業 ID設定
	 *	@param companyId 企業 ID
	 */
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	/**
	 *	企業 ID未登録検索設定項目取得
	 *	@return companyId 企業 ID
	 */
	public Boolean getCompanyIdNull() {
		return this.companyIdNull;
	}

	/**
	 *	企業 ID未登録検索設定項目設定
	 *	@param companyIdNull 企業 ID未登録検索設定項目
	 */
	public void setCompanyIdNull(Boolean companyIdNull) {
		this.companyIdNull = companyIdNull;
	}

	/**
	 *	企業 ID検索オプション項目取得
	 *	@return companyIdOption 企業 ID検索オプション項目
	 */
	public String getCompanyIdOption() {
		return this.companyIdOption;
	}

	/**
	 *	企業 ID検索オプション項目設定
	 *	@param companyIdOption 企業 ID検索オプション項目
	 */
	public void setCompanyIdOption(String companyIdOption) {
		this.companyIdOption = companyIdOption;
	}

	/**
	 *	企業 IDのクエリでの使用値取得
	 *	@return companyIdQuery 企業 IDQuery
	 */
	public String getCompanyIdQuery() {

		String query = QueryHelper.escape(this.companyId);
		if( this.companyIdOption.equals("1") ){
			return query + "%";
		}else if( this.companyIdOption.equals("2") ){
			return "%" + query;
		}else if( this.companyIdOption.equals("3") ){
			return "%" + query + "%";
		}

		return query;
	}


	// ------------------------------------------------------------
	/** 音声解析ログ ID検索範囲（開始）  */
	private String speechLogIdFrom = null;
	/** 音声解析ログ ID検索範囲（終了）  */
	private String speechLogIdTo = null;
	/** 音声解析ログ ID未登録検索設定項目  */
	private Boolean speechLogIdNull = null;

	/**
	 *	音声解析ログ ID検索範囲（開始）取得
	 *	@return speechLogIdFrom 音声解析ログ ID検索範囲（開始）
	 */
	public String getSpeechLogIdFrom() {
		return this.speechLogIdFrom;
	}

	/**
	 *	音声解析ログ ID検索範囲（開始）設定
	 *	@param speechLogIdFrom 音声解析ログ ID検索範囲（開始）
	 */
	public void setSpeechLogIdFrom(String speechLogIdFrom) {
		this.speechLogIdFrom = speechLogIdFrom;
	}

	/**
	 *	音声解析ログ ID検索範囲（終了）取得
	 *	@return speechLogIdTo 音声解析ログ ID検索範囲（終了）
	 */
	public String getSpeechLogIdTo() {
		return this.speechLogIdTo;
	}

	/**
	 *	音声解析ログ ID検索範囲（終了）設定
	 *	@param speechLogIdTo 音声解析ログ ID検索範囲（終了）
	 */
	public void setSpeechLogIdTo(String speechLogIdTo) {
		this.speechLogIdTo = speechLogIdTo;
	}

	/**
	 *	音声解析ログ ID未登録検索設定項目取得
	 *	@return speechLogIdNull 音声解析ログ ID未登録検索設定項目
	 */
	public Boolean getSpeechLogIdNull() {
		return this.speechLogIdNull;
	}

	/**
	 *	音声解析ログ ID未登録検索設定項目設定
	 *	@param speechLogIdNull 音声解析ログ ID未登録検索設定項目
	 */
	public void setSpeechLogIdNull(Boolean speechLogIdNull) {
		this.speechLogIdNull = speechLogIdNull;
	}


	// ------------------------------------------------------------
	/** 内容  */
	private String log = null;
	/** 内容未登録検索設定項目  */
	private Boolean logNull = null;
	/** 内容検索オプション項目  */
	private String logOption = null;

	/**
	 *	内容取得
	 *	@return log 内容
	 */
	public String getLog() {
		return this.log;
	}

	/**
	 *	内容設定
	 *	@param log 内容
	 */
	public void setLog(String log) {
		this.log = log;
	}

	/**
	 *	内容未登録検索設定項目取得
	 *	@return log 内容
	 */
	public Boolean getLogNull() {
		return this.logNull;
	}

	/**
	 *	内容未登録検索設定項目設定
	 *	@param logNull 内容未登録検索設定項目
	 */
	public void setLogNull(Boolean logNull) {
		this.logNull = logNull;
	}

	/**
	 *	内容検索オプション項目取得
	 *	@return logOption 内容検索オプション項目
	 */
	public String getLogOption() {
		return this.logOption;
	}

	/**
	 *	内容検索オプション項目設定
	 *	@param logOption 内容検索オプション項目
	 */
	public void setLogOption(String logOption) {
		this.logOption = logOption;
	}

	/**
	 *	内容のクエリでの使用値取得
	 *	@return logQuery 内容Query
	 */
	public String getLogQuery() {

		String query = QueryHelper.escape(this.log);
		if( this.logOption.equals("1") ){
			return query + "%";
		}else if( this.logOption.equals("2") ){
			return "%" + query;
		}else if( this.logOption.equals("3") ){
			return "%" + query + "%";
		}

		return query;
	}


	// ------------------------------------------------------------
	/** 開始秒数検索範囲（開始）  */
	private String beginFrom = null;
	/** 開始秒数検索範囲（終了）  */
	private String beginTo = null;
	/** 開始秒数未登録検索設定項目  */
	private Boolean beginNull = null;

	/**
	 *	開始秒数検索範囲（開始）取得
	 *	@return beginFrom 開始秒数検索範囲（開始）
	 */
	public String getBeginFrom() {
		return this.beginFrom;
	}

	/**
	 *	開始秒数検索範囲（開始）設定
	 *	@param beginFrom 開始秒数検索範囲（開始）
	 */
	public void setBeginFrom(String beginFrom) {
		this.beginFrom = beginFrom;
	}

	/**
	 *	開始秒数検索範囲（終了）取得
	 *	@return beginTo 開始秒数検索範囲（終了）
	 */
	public String getBeginTo() {
		return this.beginTo;
	}

	/**
	 *	開始秒数検索範囲（終了）設定
	 *	@param beginTo 開始秒数検索範囲（終了）
	 */
	public void setBeginTo(String beginTo) {
		this.beginTo = beginTo;
	}

	/**
	 *	開始秒数未登録検索設定項目取得
	 *	@return beginNull 開始秒数未登録検索設定項目
	 */
	public Boolean getBeginNull() {
		return this.beginNull;
	}

	/**
	 *	開始秒数未登録検索設定項目設定
	 *	@param beginNull 開始秒数未登録検索設定項目
	 */
	public void setBeginNull(Boolean beginNull) {
		this.beginNull = beginNull;
	}


	// ------------------------------------------------------------
	/** 終了秒数検索範囲（開始）  */
	private String endFrom = null;
	/** 終了秒数検索範囲（終了）  */
	private String endTo = null;
	/** 終了秒数未登録検索設定項目  */
	private Boolean endNull = null;

	/**
	 *	終了秒数検索範囲（開始）取得
	 *	@return endFrom 終了秒数検索範囲（開始）
	 */
	public String getEndFrom() {
		return this.endFrom;
	}

	/**
	 *	終了秒数検索範囲（開始）設定
	 *	@param endFrom 終了秒数検索範囲（開始）
	 */
	public void setEndFrom(String endFrom) {
		this.endFrom = endFrom;
	}

	/**
	 *	終了秒数検索範囲（終了）取得
	 *	@return endTo 終了秒数検索範囲（終了）
	 */
	public String getEndTo() {
		return this.endTo;
	}

	/**
	 *	終了秒数検索範囲（終了）設定
	 *	@param endTo 終了秒数検索範囲（終了）
	 */
	public void setEndTo(String endTo) {
		this.endTo = endTo;
	}

	/**
	 *	終了秒数未登録検索設定項目取得
	 *	@return endNull 終了秒数未登録検索設定項目
	 */
	public Boolean getEndNull() {
		return this.endNull;
	}

	/**
	 *	終了秒数未登録検索設定項目設定
	 *	@param endNull 終了秒数未登録検索設定項目
	 */
	public void setEndNull(Boolean endNull) {
		this.endNull = endNull;
	}


	// ------------------------------------------------------------
	/** 音声有無フラグ  */
	private List<Boolean> voiceExistence;
	/** 音声有無フラグ未登録検索項目  */
	private Boolean voiceExistenceNull;

	/**
	 *	音声有無フラグ取得
	 *	@return voiceExistence 音声有無フラグ
	 */
	public List<Boolean> getVoiceExistence() {
		if(this.voiceExistence == null){
			return null;
		}
		List<Boolean> copy = new ArrayList<Boolean>(this.voiceExistence);
		return copy;
	}

	/**
	 *	音声有無フラグ設定
	 *	@param voiceExistence 音声有無フラグ
	 */
	public void setVoiceExistence(List<Boolean> voiceExistence) {
		if(voiceExistence == null){
			this.voiceExistence = null;
		}else {
			List<Boolean> copy = new ArrayList<Boolean>(voiceExistence);
			this.voiceExistence = copy;
		}
	}

	/**
	 *	音声有無フラグ未登録検索設定項目取得
	 *	@return voiceExistenceNull 音声有無フラグ未登録検索設定項目
	 */
	public Boolean getVoiceExistenceNull() {
		return this.voiceExistenceNull;
	}

	/**
	 *	音声有無フラグ未登録検索設定項目設定
	 *	@param voiceExistenceNull 音声有無フラグ未登録検索設定項目
	 */
	public void setVoiceExistenceNull(Boolean voiceExistenceNull) {
		this.voiceExistenceNull = voiceExistenceNull;
	}


}

