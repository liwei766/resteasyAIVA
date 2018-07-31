/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechLog.java
 */

package cloud.optim.aivoiceanalytics.api.entity;
// Generated by Hibernate Tools 3.2.2.CR1


import java.util.Date;

/**

 */
public class SpeechLog  implements java.io.Serializable {


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
	 * ユーザ ID
	 */
	private String userId;

	/**
	 * ユーザ名
	 */
	private String userName;

	/**
	 * 音声解析ログ番号
	 */
	private Long speechLogNo;

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
	 * 削除日時
	 */
	private Date deleteDate;


	/**
	 * SpeechLog デフォルトコンストラクター
	 */
	public SpeechLog() {
	}

	/**
	 * SpeechLog 最小コンストラクター
	 * @param companyId 企業 ID
	 * @param userId ユーザ ID
	 * @param userName ユーザ名
	 * @param speechLogNo 音声解析ログ番号
	 * @param type 種別
	 * @param startDate 開始日時
	 * @param endDate 終了日時
	 * @param createDate 作成日時
	 * @param createUserId 作成ユーザ ID
	 * @param createUserName 作成ユーザ名
	 * @param updateUserId 更新ユーザ ID
	 * @param updateUserName 更新ユーザ名
	 */
	public SpeechLog(String companyId, String userId, String userName, Long speechLogNo, String type, Date startDate, Date endDate, Date createDate, String createUserId, String createUserName, String updateUserId, String updateUserName) {
		this.companyId = companyId;
		this.userId = userId;
		this.userName = userName;
		this.speechLogNo = speechLogNo;
		this.type = type;
		this.startDate = startDate;
		this.endDate = endDate;
		this.createDate = createDate;
		this.createUserId = createUserId;
		this.createUserName = createUserName;
		this.updateUserId = updateUserId;
		this.updateUserName = updateUserName;
    }
	/**
	 * SpeechLog フルコンストラクター
	 * @param companyId 企業 ID
	 * @param userId ユーザ ID
	 * @param userName ユーザ名
	 * @param speechLogNo 音声解析ログ番号
	 * @param type 種別
	 * @param fileName ファイル名
	 * @param startDate 開始日時
	 * @param endDate 終了日時
	 * @param createDate 作成日時
	 * @param createUserId 作成ユーザ ID
	 * @param createUserName 作成ユーザ名
	 * @param updateUserId 更新ユーザ ID
	 * @param updateUserName 更新ユーザ名
	 * @param deleteDate 削除日時
	 */
	public SpeechLog(String companyId, String userId, String userName, Long speechLogNo, String type, String fileName, Date startDate, Date endDate, Date createDate, String createUserId, String createUserName, String updateUserId, String updateUserName, Date deleteDate) {
		this.companyId = companyId;
		this.userId = userId;
		this.userName = userName;
		this.speechLogNo = speechLogNo;
		this.type = type;
		this.fileName = fileName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.createDate = createDate;
		this.createUserId = createUserId;
		this.createUserName = createUserName;
		this.updateUserId = updateUserId;
		this.updateUserName = updateUserName;
		this.deleteDate = deleteDate;
	}

	// * 音声解析ログ ID */
	/**
	 * @return speechLogId (音声解析ログ ID)
	 */
	public Long getSpeechLogId() {
		return this.speechLogId;
	}

	/**
	 * @param speechLogId 音声解析ログ ID
	 */
	public void setSpeechLogId(Long speechLogId) {
		this.speechLogId = speechLogId;
	}

	// * 更新日時 */
	/**
	 * @return updateDate (更新日時)
	 */
	public Date getUpdateDate() {
		return this.updateDate;
	}

	/**
	 * @param updateDate 更新日時
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	// * 企業 ID */
	/**
	 * @return companyId (企業 ID)
	 */
	public String getCompanyId() {
		return this.companyId;
	}

	/**
	 * @param companyId 企業 ID
	 */
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	// * ユーザ ID */
	/**
	 * @return userId (ユーザ ID)
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 * @param userId ユーザ ID
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	// * ユーザ名 */
	/**
	 * @return userName (ユーザ名)
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * @param userName ユーザ名
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	// * 音声解析ログ番号 */
	/**
	 * @return speechLogNo (音声解析ログ番号)
	 */
	public Long getSpeechLogNo() {
		return this.speechLogNo;
	}

	/**
	 * @param speechLogNo 音声解析ログ番号
	 */
	public void setSpeechLogNo(Long speechLogNo) {
		this.speechLogNo = speechLogNo;
	}

	// * 種別 */
	/**
	 * @return type (種別)
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type 種別
	 */
	public void setType(String type) {
		this.type = type;
	}

	// * ファイル名 */
	/**
	 * @return fileName (ファイル名)
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * @param fileName ファイル名
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	// * 開始日時 */
	/**
	 * @return startDate (開始日時)
	 */
	public Date getStartDate() {
		return this.startDate;
	}

	/**
	 * @param startDate 開始日時
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	// * 終了日時 */
	/**
	 * @return endDate (終了日時)
	 */
	public Date getEndDate() {
		return this.endDate;
	}

	/**
	 * @param endDate 終了日時
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	// * 作成日時 */
	/**
	 * @return createDate (作成日時)
	 */
	public Date getCreateDate() {
		return this.createDate;
	}

	/**
	 * @param createDate 作成日時
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	// * 作成ユーザ ID */
	/**
	 * @return createUserId (作成ユーザ ID)
	 */
	public String getCreateUserId() {
		return this.createUserId;
	}

	/**
	 * @param createUserId 作成ユーザ ID
	 */
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	// * 作成ユーザ名 */
	/**
	 * @return createUserName (作成ユーザ名)
	 */
	public String getCreateUserName() {
		return this.createUserName;
	}

	/**
	 * @param createUserName 作成ユーザ名
	 */
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	// * 更新ユーザ ID */
	/**
	 * @return updateUserId (更新ユーザ ID)
	 */
	public String getUpdateUserId() {
		return this.updateUserId;
	}

	/**
	 * @param updateUserId 更新ユーザ ID
	 */
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	// * 更新ユーザ名 */
	/**
	 * @return updateUserName (更新ユーザ名)
	 */
	public String getUpdateUserName() {
		return this.updateUserName;
	}

	/**
	 * @param updateUserName 更新ユーザ名
	 */
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	// * 削除日時 */
	/**
	 * @return deleteDate (削除日時)
	 */
	public Date getDeleteDate() {
		return this.deleteDate;
	}

	/**
	 * @param deleteDate 削除日時
	 */
	public void setDeleteDate(Date deleteDate) {
		this.deleteDate = deleteDate;
	}


	/**
	 * toString
	 * @return String
	*/
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
		buffer.append("speechLogId").append("='").append(getSpeechLogId()).append("' ");
		buffer.append("updateDate").append("='").append(getUpdateDate()).append("' ");
		buffer.append("companyId").append("='").append(getCompanyId()).append("' ");
		buffer.append("userId").append("='").append(getUserId()).append("' ");
		buffer.append("speechLogNo").append("='").append(getSpeechLogNo()).append("' ");
		buffer.append("type").append("='").append(getType()).append("' ");
		buffer.append("fileName").append("='").append(getFileName()).append("' ");
		buffer.append("startDate").append("='").append(getStartDate()).append("' ");
		buffer.append("endDate").append("='").append(getEndDate()).append("' ");
		buffer.append("createDate").append("='").append(getCreateDate()).append("' ");
		buffer.append("createUserId").append("='").append(getCreateUserId()).append("' ");
		buffer.append("updateUserId").append("='").append(getUpdateUserId()).append("' ");
		buffer.append("]");

		return buffer.toString();
	}

	@Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( (other == null ) ) return false;
		if ( !(other instanceof SpeechLog) ) return false;
		SpeechLog castOther = ( SpeechLog ) other;

		return ( (this.getSpeechLogId()==castOther.getSpeechLogId()) || ( this.getSpeechLogId()!=null && castOther.getSpeechLogId()!=null && this.getSpeechLogId().equals(castOther.getSpeechLogId()) ) )
 && ( (this.getUpdateDate()==castOther.getUpdateDate()) || ( this.getUpdateDate()!=null && castOther.getUpdateDate()!=null && this.getUpdateDate().equals(castOther.getUpdateDate()) ) )
 && ( (this.getCompanyId()==castOther.getCompanyId()) || ( this.getCompanyId()!=null && castOther.getCompanyId()!=null && this.getCompanyId().equals(castOther.getCompanyId()) ) )
 && ( (this.getUserId()==castOther.getUserId()) || ( this.getUserId()!=null && castOther.getUserId()!=null && this.getUserId().equals(castOther.getUserId()) ) )
 && ( (this.getUserName()==castOther.getUserName()) || ( this.getUserName()!=null && castOther.getUserName()!=null && this.getUserName().equals(castOther.getUserName()) ) )
 && ( (this.getSpeechLogNo()==castOther.getSpeechLogNo()) || ( this.getSpeechLogNo()!=null && castOther.getSpeechLogNo()!=null && this.getSpeechLogNo().equals(castOther.getSpeechLogNo()) ) )
 && ( (this.getType()==castOther.getType()) || ( this.getType()!=null && castOther.getType()!=null && this.getType().equals(castOther.getType()) ) )
 && ( (this.getFileName()==castOther.getFileName()) || ( this.getFileName()!=null && castOther.getFileName()!=null && this.getFileName().equals(castOther.getFileName()) ) )
 && ( (this.getStartDate()==castOther.getStartDate()) || ( this.getStartDate()!=null && castOther.getStartDate()!=null && this.getStartDate().equals(castOther.getStartDate()) ) )
 && ( (this.getEndDate()==castOther.getEndDate()) || ( this.getEndDate()!=null && castOther.getEndDate()!=null && this.getEndDate().equals(castOther.getEndDate()) ) )
 && ( (this.getCreateDate()==castOther.getCreateDate()) || ( this.getCreateDate()!=null && castOther.getCreateDate()!=null && this.getCreateDate().equals(castOther.getCreateDate()) ) )
 && ( (this.getCreateUserId()==castOther.getCreateUserId()) || ( this.getCreateUserId()!=null && castOther.getCreateUserId()!=null && this.getCreateUserId().equals(castOther.getCreateUserId()) ) )
 && ( (this.getCreateUserName()==castOther.getCreateUserName()) || ( this.getCreateUserName()!=null && castOther.getCreateUserName()!=null && this.getCreateUserName().equals(castOther.getCreateUserName()) ) )
 && ( (this.getUpdateUserId()==castOther.getUpdateUserId()) || ( this.getUpdateUserId()!=null && castOther.getUpdateUserId()!=null && this.getUpdateUserId().equals(castOther.getUpdateUserId()) ) )
 && ( (this.getUpdateUserName()==castOther.getUpdateUserName()) || ( this.getUpdateUserName()!=null && castOther.getUpdateUserName()!=null && this.getUpdateUserName().equals(castOther.getUpdateUserName()) ) )
 && ( (this.getDeleteDate()==castOther.getDeleteDate()) || ( this.getDeleteDate()!=null && castOther.getDeleteDate()!=null && this.getDeleteDate().equals(castOther.getDeleteDate()) ) );
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + ( getSpeechLogId() == null ? 0 : this.getSpeechLogId().hashCode() );
		result = 37 * result + ( getUpdateDate() == null ? 0 : this.getUpdateDate().hashCode() );
		result = 37 * result + ( getCompanyId() == null ? 0 : this.getCompanyId().hashCode() );
		result = 37 * result + ( getUserId() == null ? 0 : this.getUserId().hashCode() );
		result = 37 * result + ( getUserName() == null ? 0 : this.getUserName().hashCode() );
		result = 37 * result + ( getSpeechLogNo() == null ? 0 : this.getSpeechLogNo().hashCode() );
		result = 37 * result + ( getType() == null ? 0 : this.getType().hashCode() );
		result = 37 * result + ( getFileName() == null ? 0 : this.getFileName().hashCode() );
		result = 37 * result + ( getStartDate() == null ? 0 : this.getStartDate().hashCode() );
		result = 37 * result + ( getEndDate() == null ? 0 : this.getEndDate().hashCode() );
		result = 37 * result + ( getCreateDate() == null ? 0 : this.getCreateDate().hashCode() );
		result = 37 * result + ( getCreateUserId() == null ? 0 : this.getCreateUserId().hashCode() );
		result = 37 * result + ( getCreateUserName() == null ? 0 : this.getCreateUserName().hashCode() );
		result = 37 * result + ( getUpdateUserId() == null ? 0 : this.getUpdateUserId().hashCode() );
		result = 37 * result + ( getUpdateUserName() == null ? 0 : this.getUpdateUserName().hashCode() );
		result = 37 * result + ( getDeleteDate() == null ? 0 : this.getDeleteDate().hashCode() );
		return result;
	}

  // The following is extra code specified in the hbm.xml files

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;
		
  // end of extra code specified in the hbm.xml files

}

