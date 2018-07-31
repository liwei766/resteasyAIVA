/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：UseTimeSearchResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.usetime;


import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement ;

import org.apache.commons.beanutils.PropertyUtils ;

import cloud.optim.aivoiceanalytics.api.entity.UseTime ;

/**
 * UseTime 検索結果.<br/>
 */
@XmlRootElement
public class UseTimeSearchResult implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;


	/**
	 * 文字列表現への変換
	 *
	 * @return このインスタンスの文字列表現
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
		sb.append("useTimeId").append("='").append(getUseTimeId()).append("' ");
		sb.append("companyId").append("='").append(getCompanyId()).append("' ");
		sb.append("userId").append("='").append(getUserId()).append("' ");
		sb.append("type").append("='").append(getType()).append("' ");
		sb.append("startDate").append("='").append(getStartDate()).append("' ");
		sb.append("endDate").append("='").append(getEndDate()).append("' ");
		sb.append("useTime").append("='").append(getUseTime()).append("' ");
		sb.append("yearMonths").append("='").append(getYearMonths()).append("' ");
		sb.append("]");

		return sb.toString();
	}

	/**
	 * エンティティオブジェクトへの変換
	 *
	 * @return 新たに作成した UseTime オブジェクト
	 */
	public UseTime toEntity() {
		UseTime ret = new UseTime() ;

		try {
			PropertyUtils.copyProperties( ret, this ) ;
		}
		catch ( Exception e ) {
			ret = null ;
		}

		return ret ;
	}




	/** 利用時間 ID */
	private Long useTimeId;

	/** 企業 ID */
	private String companyId;

	/** 企業名 */
	private String companyName;

	/** ユーザ ID */
	private String userId;

	/** ユーザ名 */
	private String userName;

	/** 機能区分 */
	private String type;

	/** 開始日時 */
	private Date startDate;

	/** 終了日時 */
	private Date endDate;

	/** 利用時間 */
	private Long useTime;

	/** 年月 */
	private String yearMonths;


	/**
	 * useTimeId 取得
	 * @return useTimeId
	 */
	public Long getUseTimeId() {
		return this.useTimeId;
	}

	/**
	 * useTimeId 設定
	 * @param useTimeId 利用時間 ID
	 */
	public void setUseTimeId(Long useTimeId) {
		this.useTimeId = useTimeId;
	}

	/**
	 * companyId 取得
	 * @return companyId
	 */
	public String getCompanyId() {
		return this.companyId;
	}

	/**
	 * companyId 設定
	 * @param companyId 企業 ID
	 */
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	/**
	 * companyName 取得
	 * @return companyName
	 */
	public String getCompanyName() {
		return this.companyName;
	}

	/**
	 * companyName 設定
	 * @param companyName 企業名
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * userId 取得
	 * @return userId
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 * userId 設定
	 * @param userId ユーザ ID
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * userName 取得
	 * @return userName
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * userName 設定
	 * @param userName ユーザ名
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * type 取得
	 * @return type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * type 設定
	 * @param type 機能区分
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * startDate 取得
	 * @return startDate
	 */
	public Date getStartDate() {
		return this.startDate;
	}

	/**
	 * startDate 設定
	 * @param startDate 開始日時
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * endDate 取得
	 * @return endDate
	 */
	public Date getEndDate() {
		return this.endDate;
	}

	/**
	 * endDate 設定
	 * @param endDate 終了日時
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * useTime 取得
	 * @return useTime
	 */
	public Long getUseTime() {
		return this.useTime;
	}

	/**
	 * useTime 設定
	 * @param useTime 利用時間
	 */
	public void setUseTime(Long useTime) {
		this.useTime = useTime;
	}

	/**
	 * yearMonths 取得
	 * @return yearMonths
	 */
	public String getYearMonths() {
		return this.yearMonths;
	}

	/**
	 * yearMonths 設定
	 * @param yearMonths 年月
	 */
	public void setYearMonths(String yearMonths) {
		this.yearMonths = yearMonths;
	}

}