/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：RecaiusLicenseSearchResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.recaiuslicense;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement ;

import org.apache.commons.beanutils.PropertyUtils ;

import cloud.optim.aivoiceanalytics.api.entity.RecaiusLicense ;

/**
 * RecaiusLicense 検索結果.<br/>
 */
@XmlRootElement
public class RecaiusLicenseSearchResult implements java.io.Serializable {

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
    sb.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode()))
        .append(" [");
    sb.append("recaiusLicenseId").append("='").append(getRecaiusLicenseId()).append("' ");
    sb.append("serviceId").append("='").append(getServiceId()).append("' ");
    sb.append("agencyCompanyId").append("='").append(getAgencyCompanyId()).append("' ");
    sb.append("]");
    return sb.toString();
  }

  /**
   * エンティティオブジェクトへの変換.
   *
   * @return 新たに作成した RecaiusLicense オブジェクト
   */
  public RecaiusLicense toEntity() {
    RecaiusLicense ret = new RecaiusLicense();

    try {
      PropertyUtils.copyProperties(ret, this);
    } catch (Exception e) {
      ret = null;
    }

    return ret;
  }

  /** リカイアスライセンス ID. */
  private Long recaiusLicenseId;

  /** サービス利用ID. */
  private String serviceId;

  /** パスワード. */
  private String password;

  /** 代理店企業ID. */
  private String agencyCompanyId;

  /** 更新日時. */
  private Date updateDate;

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
   * @param recaiusLicenseId リカイアスライセンス ID
   */
  public void setRecaiusLicenseId(Long recaiusLicenseId) {
    this.recaiusLicenseId = recaiusLicenseId;
  }

  /**
   * serviceId 取得.
   *
   * @return serviceId
   */
  public String getServiceId() {
    return this.serviceId;
  }

  /**
   * serviceId 設定.
   *
   * @param serviceId サービス利用ID
   */
  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  /**
   * password 取得.
   *
   * @return password
   */
  public String getPassword() {
    return this.password;
  }

  /**
   * password 設定.
   *
   * @param password パスワード
   */
  public void setPassword(String password) {
    this.password = password;
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
