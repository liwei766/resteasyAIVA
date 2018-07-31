/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechLogSearchForm.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.speechlog;

import org.apache.commons.lang3.builder.ToStringBuilder ;
import org.apache.commons.lang3.builder.ToStringStyle ;

import cloud.optim.aivoiceanalytics.core.common.utility.QueryHelper;

/**
 * SpeechLog 検索フォーム.<br/>
 *
 */
public class SpeechLogSearchForm implements java.io.Serializable {

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


	/** 企業 ID  */
	private String companyId = null;
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

	// ------------------------------------------------------------
	/** ユーザ ID  */
	private String userId = null;

	/**
	 *	ユーザ ID取得
	 *	@return userId ユーザ ID
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 *	ユーザ ID設定
	 *	@param userId ユーザ ID
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	// ------------------------------------------------------------
	/** ユーザ名  */
	private String userName = null;
	/** ユーザ名未登録検索設定項目  */
	private Boolean userNameNull = null;
	/** ユーザ名検索オプション項目  */
	private String userNameOption = null;

	/**
	 *	ユーザ名取得
	 *	@return userName ユーザ名
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 *	ユーザ名設定
	 *	@param userName ユーザ名
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 *	ユーザ名未登録検索設定項目取得
	 *	@return userName ユーザ名
	 */
	public Boolean getUserNameNull() {
		return this.userNameNull;
	}

	/**
	 *	ユーザ名未登録検索設定項目設定
	 *	@param userNameNull ユーザ名未登録検索設定項目
	 */
	public void setUserNameNull(Boolean userNameNull) {
		this.userNameNull = userNameNull;
	}

	/**
	 *	ユーザ名検索オプション項目取得
	 *	@return userNameOption ユーザ名検索オプション項目
	 */
	public String getUserNameOption() {
		return this.userNameOption;
	}

	/**
	 *	ユーザ名検索オプション項目設定
	 *	@param userNameOption ユーザ名検索オプション項目
	 */
	public void setUserNameOption(String userNameOption) {
		this.userNameOption = userNameOption;
	}

	/**
	 *	ユーザ名のクエリでの使用値取得
	 *	@return userNameQuery ユーザ名Query
	 */
	public String getUserNameQuery() {

		String query = QueryHelper.escape(this.userName);
		if( this.userNameOption.equals("1") ){
			return query + "%";
		}else if( this.userNameOption.equals("2") ){
			return "%" + query;
		}else if( this.userNameOption.equals("3") ){
			return "%" + query + "%";
		}

		return query;
	}

	// ------------------------------------------------------------
	/** 開始日時検索範囲（開始）  */
	private java.util.Date startDateFrom = null;
	/** 開始日時検索範囲（終了）  */
	private java.util.Date startDateTo = null;

	/**
	 *	開始日時検索範囲（開始）取得
	 *	@return startDateFrom 開始日時検索範囲（開始）
	 */
	public java.util.Date getStartDateFrom() {
		return (this.startDateFrom != null) ? (java.util.Date)this.startDateFrom.clone() : null;
	}

	/**
	 *	開始日時検索範囲（開始）設定
	 *	@param startDateFrom 開始日時検索範囲（開始）
	 */
	public void setStartDateFrom(java.util.Date startDateFrom) {
		this.startDateFrom = (startDateFrom != null) ? (java.util.Date)startDateFrom.clone() : null;
	}

	/**
	 *	開始日時検索範囲（終了）取得
	 *	@return startDateTo 開始日時検索範囲（終了）
	 */
	public java.util.Date getStartDateTo() {
		return this.startDateTo != null ? (java.util.Date)this.startDateTo.clone() : null;
	}

	/**
	 *	開始日時検索範囲（終了）設定
	 *	@param startDateTo 開始日時検索範囲（終了）
	 */
	public void setStartDateTo(java.util.Date startDateTo) {
		this.startDateTo = startDateTo != null ? (java.util.Date)startDateTo.clone() : null;
	}
}

