/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CompanyManagementSearchResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.companymanagement;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement ;

import org.apache.commons.beanutils.PropertyUtils ;

import cloud.optim.aivoiceanalytics.api.entity.CompanyManagement ;

/**
 * CompanyManagement 検索結果.<br/>
 */
@XmlRootElement
public class CompanyManagementSearchResult implements java.io.Serializable {

  /** serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * 文字列表現への変換.
   *
   * @return このインスタンスの文字列表現
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode()));
    sb.append(" [");
    sb.append("companyManagementId").append("='").append(getCompanyManagementId()).append("' ");
    sb.append("companyId").append("='").append(getCompanyId()).append("' ");
    sb.append("agencyCompanyId").append("='").append(getAgencyCompanyId()).append("' ");
    sb.append("recaiusModelId").append("='").append(getRecaiusModelId()).append("' ");
    sb.append("energyThreshold").append("='").append(getEnergyThreshold()).append("' ");
    sb.append("hashedCompanyId").append("='").append(getHashedCompanyId()).append("' ");
    sb.append("saveVoice").append("='").append(getSaveVoice()).append("' ");
    sb.append("permitIpAddress").append("='").append(getPermitIpAddress()).append("' ");
    sb.append("]");
    return sb.toString();
  }

  /**
   * エンティティオブジェクトへの変換.
   *
   * @return 新たに作成した CompanyManagement オブジェクト
   */
  public CompanyManagement toEntity() {
    CompanyManagement ret = new CompanyManagement();
    try {
      PropertyUtils.copyProperties(ret, this);
    } catch (Exception e) {
      ret = null;
    }
    return ret;
  }

  /** 企業管理 ID. */
  private Long companyManagementId;

  /** 企業 ID. */
  private String companyId;

  /** 企業名. */
  private String companyName;

  /** 代理店企業ID. */
  private String agencyCompanyId;

  /** リカイアスライセンスID. */
  private Long recaiusLicenseId;

  /** リカイアスモデルID. */
  private Integer recaiusModelId;

  /** 音声判断レベル閾値. */
  private Integer energyThreshold;

  /** 企業 ID. */
  private String hashedCompanyId;

  /** 音声保存設定 */
  private Boolean saveVoice;

  /** 接続元制限IPアドレス */
  private String permitIpAddress;

  /** 更新日時. */
  private Date updateDate;

  /**
   * companyManagementId 取得.
   *
   * @return companyManagementId
   */
  public Long getCompanyManagementId() {
    return this.companyManagementId;
  }

  /**
   * companyManagementId 設定.
   *
   * @param companyManagementId 企業管理 ID
   */
  public void setCompanyManagementId(Long companyManagementId) {
    this.companyManagementId = companyManagementId;
  }

  /**
   * companyId 取得.
   *
   * @return companyId
   */
  public String getCompanyId() {
    return this.companyId;
  }

  /**
   * companyId 設定.
   *
   * @param companyId 企業 ID
   */
  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  /**
   * companyName 取得.
   *
   * @return companyName
   */
  public String getCompanyName() {
    return this.companyName;
  }

  /**
   * companyName 設定.
   *
   * @param companyName 企業名
   */
  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  /**
   * agencyCompanyId 取得.
   *
   * @return agencyCompanyId
   */
  public String getAgencyCompanyId() {
    return this.agencyCompanyId;
  }

  /**
   * agencyCompanyId 設定.
   *
   * @param agencyCompanyId 代理店企業ID
   */
  public void setAgencyCompanyId(String agencyCompanyId) {
    this.agencyCompanyId = agencyCompanyId;
  }

  /**
   * recaiusLicenseId 取得.
   *
   * @return recaiusLicenseId
   */
  public Long getRecaiusLicenseId() {
    return this.recaiusLicenseId;
  }

  /**
   * recaiusLicenseId 設定.
   *
   * @param recaiusLicenseId リカイアスライセンスID
   */
  public void setRecaiusLicenseId(Long recaiusLicenseId) {
    this.recaiusLicenseId = recaiusLicenseId;
  }

  /**
   * recaiusModelId 取得.
   *
   * @return recaiusModelId
   */
  public Integer getRecaiusModelId() {
    return this.recaiusModelId;
  }

  /**
   * recaiusModelId 設定.
   *
   * @param recaiusModelId リカイアスモデルID
   */
  public void setRecaiusModelId(Integer recaiusModelId) {
    this.recaiusModelId = recaiusModelId;
  }

  /**
   * energyThreshold 取得.
   *
   * @return energyThreshold
   */
  public Integer getEnergyThreshold() {
    return this.energyThreshold;
  }

  /**
   * energyThreshold 設定.
   *
   * @param energyThreshold 音声判断レベル閾値
   */
  public void setEnergyThreshold(Integer energyThreshold) {
    this.energyThreshold = energyThreshold;
  }

  /**
   * hashedCompanyId 取得.
   *
   * @return hashedCompanyId
   */
  public String getHashedCompanyId() {
    return this.hashedCompanyId;
  }

  /**
   * hashedCompanyId 設定.
   *
   * @param hashedCompanyId 企業IDハッシュ
   */
  public void setHashedCompanyId(String hashedCompanyId) {
    this.hashedCompanyId = hashedCompanyId;
  }

  /**
   * saveVoice 取得
   * @return saveVoice
   */
  public Boolean getSaveVoice() {
    return this.saveVoice;
  }

  /**
   * saveVoice 設定
   * @param saveVoice 音声保存設定
   */
  public void setSaveVoice(Boolean saveVoice) {
    this.saveVoice = saveVoice;
  }

  /**
   * permitIpAddress 取得
   * @return permitIpAddress
   */
  public String getPermitIpAddress() {
    return this.permitIpAddress;
  }

  /**
   * permitIpAddress 設定
   * @param permitIpAddress 接続元制限IPアドレス
   */
  public void setPermitIpAddress(String permitIpAddress) {
    this.permitIpAddress = permitIpAddress;
  }

  /**
   * updateDate 取得.
   *
   * @return updateDate 更新日時
   */
  public Date getUpdateDate() {
    return this.updateDate;
  }

  /**
   * updateDate 設定.
   *
   * @param updateDate 更新日時
   */
  public void setUpdateDate(Date updateDate) {
    this.updateDate = updateDate;
  }

}
