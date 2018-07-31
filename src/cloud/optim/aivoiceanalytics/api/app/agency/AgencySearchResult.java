/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：AgencySearchResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.agency;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement ;

import org.apache.commons.beanutils.PropertyUtils ;

import cloud.optim.aivoiceanalytics.api.entity.Agency ;

/**
 * Agency 検索結果.<br/>
 */
@XmlRootElement
public class AgencySearchResult implements java.io.Serializable {

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
    sb.append("agencyId").append("='").append(getAgencyId()).append("' ");
    sb.append("agencyCompanyId").append("='").append(getAgencyCompanyId()).append("' ");
    sb.append("]");
    return sb.toString();
  }

  /**
   * エンティティオブジェクトへの変換.
   *
   * @return 新たに作成した Agency オブジェクト
   */
  public Agency toEntity() {
    Agency ret = new Agency();

    try {
      PropertyUtils.copyProperties(ret, this);
    } catch (Exception e) {
      ret = null;
    }

    return ret;
  }

  /** 代理店 ID. */
  private Long agencyId;

  /** 代理店企業ID. */
  private String agencyCompanyId;

  /** 更新日時. */
  private Date updateDate;

  /**
   * agencyId 取得.
   *
   * @return agencyId
   */
  public Long getAgencyId() {
    return this.agencyId;
  }

  /**
   * agencyId 設定.
   *
   * @param agencyId 代理店 ID
   */
  public void setAgencyId(Long agencyId) {
    this.agencyId = agencyId;
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
