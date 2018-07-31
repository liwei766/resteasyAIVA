/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechLogDetail.java
 */

package cloud.optim.aivoiceanalytics.api.entity;
// Generated by Hibernate Tools 3.2.2.CR1


import java.util.Date;

/**

 */
public class SpeechLogDetail  implements java.io.Serializable {


	/**
	 * 音声解析ログ詳細 ID
	 */
	private Long speechLogDetailId;

	/**
	 * 更新日時
	 */
	private Date updateDate;

	/**
	 * 企業 ID
	 */
	private String companyId;

	/**
	 * 音声解析ログ ID
	 */
	private Long speechLogId;

	/**
	 * 内容
	 */
	private String log;

	/**
	 * 開始時間
	 */
	private Integer begin;

	/**
	 * 終了時間
	 */
	private Integer end;

	/**
	 * 音声有無フラグ
	 */
	private Boolean voiceExistence;

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
	 * SpeechLogDetail デフォルトコンストラクター
	 */
	public SpeechLogDetail() {
	}

	/**
	 * SpeechLogDetail 最小コンストラクター
	 * @param companyId 企業 ID
	 * @param speechLogId 音声解析ログ ID
	 * @param log 内容
	 * @param voiceExistence 音声有無フラグ
	 * @param createDate 作成日時
	 * @param createUserId 作成ユーザ ID
	 * @param createUserName 作成ユーザ名
	 * @param updateUserId 更新ユーザ ID
	 * @param updateUserName 更新ユーザ名
	 */
	public SpeechLogDetail(String companyId, Long speechLogId, String log, Boolean voiceExistence, Date createDate, String createUserId, String createUserName, String updateUserId, String updateUserName) {
		this.companyId = companyId;
		this.speechLogId = speechLogId;
		this.log = log;
		this.voiceExistence = voiceExistence;
		this.createDate = createDate;
		this.createUserId = createUserId;
		this.createUserName = createUserName;
		this.updateUserId = updateUserId;
		this.updateUserName = updateUserName;
    }
	/**
	 * SpeechLogDetail フルコンストラクター
	 * @param companyId 企業 ID
	 * @param speechLogId 音声解析ログ ID
	 * @param log 内容
	 * @param begin 開始時間
	 * @param end 終了時間
	 * @param voiceExistence 音声有無フラグ
	 * @param createDate 作成日時
	 * @param createUserId 作成ユーザ ID
	 * @param createUserName 作成ユーザ名
	 * @param updateUserId 更新ユーザ ID
	 * @param updateUserName 更新ユーザ名
	 * @param deleteDate 削除日時
	 */
	public SpeechLogDetail(String companyId, Long speechLogId, String log, Integer begin, Integer end, Boolean voiceExistence, Date createDate, String createUserId, String createUserName, String updateUserId, String updateUserName, Date deleteDate) {
		this.companyId = companyId;
		this.speechLogId = speechLogId;
		this.log = log;
		this.begin = begin;
		this.end = end;
		this.voiceExistence = voiceExistence;
		this.createDate = createDate;
		this.createUserId = createUserId;
		this.createUserName = createUserName;
		this.updateUserId = updateUserId;
		this.updateUserName = updateUserName;
		this.deleteDate = deleteDate;
	}

	// * 音声解析ログ詳細 ID */
	/**
	 * @return speechLogDetailId (音声解析ログ詳細 ID)
	 */
	public Long getSpeechLogDetailId() {
		return this.speechLogDetailId;
	}

	/**
	 * @param speechLogDetailId 音声解析ログ詳細 ID
	 */
	public void setSpeechLogDetailId(Long speechLogDetailId) {
		this.speechLogDetailId = speechLogDetailId;
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

	// * 内容 */
	/**
	 * @return log (内容)
	 */
	public String getLog() {
		return this.log;
	}

	/**
	 * @param log 内容
	 */
	public void setLog(String log) {
		this.log = log;
	}

	// * 開始時間 */
	/**
	 * @return begin (開始時間)
	 */
	public Integer getBegin() {
		return this.begin;
	}

	/**
	 * @param begin 開始時間
	 */
	public void setBegin(Integer begin) {
		this.begin = begin;
	}

	// * 終了時間 */
	/**
	 * @return end (終了時間)
	 */
	public Integer getEnd() {
		return this.end;
	}

	/**
	 * @param end 終了時間
	 */
	public void setEnd(Integer end) {
		this.end = end;
	}

	// * 音声有無フラグ */
	/**
	 * @return voiceExistence (音声有無フラグ)
	 */
	public Boolean getVoiceExistence() {
		return this.voiceExistence;
	}

	/**
	 * @param voiceExistence 音声有無フラグ
	 */
	public void setVoiceExistence(Boolean voiceExistence) {
		this.voiceExistence = voiceExistence;
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
		buffer.append("speechLogDetailId").append("='").append(getSpeechLogDetailId()).append("' ");
		buffer.append("updateDate").append("='").append(getUpdateDate()).append("' ");
		buffer.append("companyId").append("='").append(getCompanyId()).append("' ");
		buffer.append("speechLogId").append("='").append(getSpeechLogId()).append("' ");
		buffer.append("log").append("='").append(getLog()).append("' ");
		buffer.append("begin").append("='").append(getBegin()).append("' ");
		buffer.append("end").append("='").append(getEnd()).append("' ");
		buffer.append("voiceExistence").append("='").append(getVoiceExistence()).append("' ");
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
		if ( !(other instanceof SpeechLogDetail) ) return false;
		SpeechLogDetail castOther = ( SpeechLogDetail ) other;

		return ( (this.getSpeechLogDetailId()==castOther.getSpeechLogDetailId()) || ( this.getSpeechLogDetailId()!=null && castOther.getSpeechLogDetailId()!=null && this.getSpeechLogDetailId().equals(castOther.getSpeechLogDetailId()) ) )
 && ( (this.getUpdateDate()==castOther.getUpdateDate()) || ( this.getUpdateDate()!=null && castOther.getUpdateDate()!=null && this.getUpdateDate().equals(castOther.getUpdateDate()) ) )
 && ( (this.getCompanyId()==castOther.getCompanyId()) || ( this.getCompanyId()!=null && castOther.getCompanyId()!=null && this.getCompanyId().equals(castOther.getCompanyId()) ) )
 && ( (this.getSpeechLogId()==castOther.getSpeechLogId()) || ( this.getSpeechLogId()!=null && castOther.getSpeechLogId()!=null && this.getSpeechLogId().equals(castOther.getSpeechLogId()) ) )
 && ( (this.getLog()==castOther.getLog()) || ( this.getLog()!=null && castOther.getLog()!=null && this.getLog().equals(castOther.getLog()) ) )
 && ( (this.getBegin()==castOther.getBegin()) || ( this.getBegin()!=null && castOther.getBegin()!=null && this.getBegin().equals(castOther.getBegin()) ) )
 && ( (this.getEnd()==castOther.getEnd()) || ( this.getEnd()!=null && castOther.getEnd()!=null && this.getEnd().equals(castOther.getEnd()) ) )
 && ( (this.getVoiceExistence()==castOther.getVoiceExistence()) || ( this.getVoiceExistence()!=null && castOther.getVoiceExistence()!=null && this.getVoiceExistence().equals(castOther.getVoiceExistence()) ) )
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

		result = 37 * result + ( getSpeechLogDetailId() == null ? 0 : this.getSpeechLogDetailId().hashCode() );
		result = 37 * result + ( getUpdateDate() == null ? 0 : this.getUpdateDate().hashCode() );
		result = 37 * result + ( getCompanyId() == null ? 0 : this.getCompanyId().hashCode() );
		result = 37 * result + ( getSpeechLogId() == null ? 0 : this.getSpeechLogId().hashCode() );
		result = 37 * result + ( getLog() == null ? 0 : this.getLog().hashCode() );
		result = 37 * result + ( getBegin() == null ? 0 : this.getBegin().hashCode() );
		result = 37 * result + ( getEnd() == null ? 0 : this.getEnd().hashCode() );
		result = 37 * result + ( getVoiceExistence() == null ? 0 : this.getVoiceExistence().hashCode() );
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


