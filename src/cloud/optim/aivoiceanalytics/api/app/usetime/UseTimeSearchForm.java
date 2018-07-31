/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：UseTimeSearchForm.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.usetime;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder ;
import org.apache.commons.lang3.builder.ToStringStyle ;

/**
 * UseTime 検索フォーム.<br/>
 *
 */
public class UseTimeSearchForm implements java.io.Serializable {

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
	/** 年 */
	private String year = null;

	/**
	 *	年取得
	 *	@return year 年
	 */
	public String getYear() {
		return this.year;
	}

	/**
	 *	年設定
	 *	@param year 年
	 */
	public void setYear(String year) {
		this.year = year;
	}

	// ------------------------------------------------------------
	/** 月 */
	private String month = null;

	/**
	 *	月取得
	 *	@return month 月
	 */
	public String getMonth() {
		return this.month;
	}

	/**
	 *	月設定
	 *	@param month 月
	 */
	public void setMonth(String month) {
		this.month = month;
	}

	// ------------------------------------------------------------

	/**
	 * 検索条件用の年月を取得する
	 * @return 検索条件用年月
	 */
	public String getYearMonth() {
		return String.join("-", this.year, StringUtils.leftPad(month, 2, '0'), "01");
	}

	// ------------------------------------------------------------

	/** 代理店企業ID. */
	private String agencyCompanyId = null;

	/**
	* 代理店企業ID取得.
	* 
	* @return agencyCompanyId 代理店企業ID
	*/
	public String getAgencyCompanyId() {
		return this.agencyCompanyId;
	}

	/**
	 * 代理店企業ID設定.
	 * 
	 * @param agencyCompanyId 代理店企業ID
	 */
	public void setAgencyCompanyId(String agencyCompanyId) {
		this.agencyCompanyId = agencyCompanyId;
	}
}

