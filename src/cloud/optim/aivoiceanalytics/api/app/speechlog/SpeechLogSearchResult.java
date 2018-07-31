/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechLogSearchResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.speechlog;


import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement ;

import org.apache.commons.beanutils.PropertyUtils ;

import cloud.optim.aivoiceanalytics.api.entity.SpeechLog;

/**
 * SpeechLog 検索結果.<br/>
 */
@XmlRootElement
public class SpeechLogSearchResult implements java.io.Serializable {

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
		sb.append("speechLogId").append("='").append(getSpeechLogId()).append("' ");
		sb.append("companyId").append("='").append(getCompanyId()).append("' ");
		sb.append("speechLogNo").append("='").append(getSpeechLogNo()).append("' ");
		sb.append("userId").append("='").append(getUserId()).append("' ");
		sb.append("userName").append("='").append(getUserName()).append("' ");
		sb.append("type").append("='").append(getType()).append("' ");
		sb.append("fileName").append("='").append(getFileName()).append("' ");
		sb.append("startDate").append("='").append(getStartDate()).append("' ");
		sb.append("endDate").append("='").append(getEndDate()).append("' ");
		sb.append("createDate").append("='").append(getCreateDate()).append("' ");
		sb.append("createUserId").append("='").append(getCreateUserId()).append("' ");
		sb.append("createUserName").append("='").append(getCreateUserName()).append("' ");
		sb.append("updateDate").append("='").append(getUpdateDate()).append("' ");
		sb.append("updateUserId").append("='").append(getUpdateUserId()).append("' ");
		sb.append("updateUserName").append("='").append(getUpdateUserName()).append("' ");
		sb.append("]");
		return sb.toString();
	}

	/**
	 * エンティティオブジェクトへの変換
	 *
	 * @return 新たに作成した SpeechLog オブジェクト
	 */
	public SpeechLog toEntity() {
		SpeechLog ret = new SpeechLog() ;

		try {
			PropertyUtils.copyProperties( ret, this ) ;
		}
		catch ( Exception e ) {
			ret = null ;
		}

		return ret ;
	}

	/**
	 * 音声解析ログ ID
	 */
	private Long speechLogId;

	/**
	 * 更新日時
	 */
	private Date updateDate;

	/**
	 * 企業 ID
	 */
	private String companyId;

	/**
	 * 音声解析ログ番号
	 */
	private Long speechLogNo;

	/**
	 * ユーザ ID
	 */
	private String userId;

	/**
	 * ユーザ名
	 */
	private String userName;

	/**
	 * 種別
	 */
	private String type;

	/**
	 * ファイル名
	 */
	private String fileName;

	/**
	 * 開始日時
	 */
	private Date startDate;

	/**
	 * 終了日時
	 */
	private Date endDate;

	/**
	 * 作成日時
	 */
	private Date createDate;

	/**
	 * 作成ユーザ ID
	 */
	private String createUserId;

	/**
	 * 作成ユーザ名
	 */
	private String createUserName;

	/**
	 * 更新ユーザ ID
	 */
	private String updateUserId;

	/**
	 * 更新ユーザ名
	 */
	private String updateUserName;

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
	 * @return updateDate
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updateDate セットする updateDate
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * @return companyId
	 */
	public String getCompanyId() {
		return companyId;
	}

	/**
	 * @param companyId セットする companyId
	 */
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	/**
	 * @return speechLogNo
	 */
	public Long getSpeechLogNo() {
		return this.speechLogNo;
	}

	/**
	 * @param speechLogNo セットする speechLogNo
	 */
	public void setSpeechLogNo(Long speechLogNo) {
		this.speechLogNo = speechLogNo;
	}

	/**
	 * @return userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId セットする userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName セットする userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate セットする startDate
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate セットする endDate
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * @param createDate セットする createDate
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return createUserId
	 */
	public String getCreateUserId() {
		return createUserId;
	}

	/**
	 * @param createUserId セットする createUserId
	 */
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	/**
	 * @return createUserName
	 */
	public String getCreateUserName() {
		return createUserName;
	}

	/**
	 * @param createUserName セットする createUserName
	 */
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	/**
	 * @return updateUserId
	 */
	public String getUpdateUserId() {
		return updateUserId;
	}

	/**
	 * @param updateUserId セットする updateUserId
	 */
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	/**
	 * @return updateUserName
	 */
	public String getUpdateUserName() {
		return updateUserName;
	}

	/**
	 * @param updateUserName セットする updateUserName
	 */
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

}