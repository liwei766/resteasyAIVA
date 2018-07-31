/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CompanyManagementSearchForm.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.companymanagement;

import org.apache.commons.lang3.builder.ToStringBuilder ;
import org.apache.commons.lang3.builder.ToStringStyle ;

import cloud.optim.aivoiceanalytics.core.common.utility.QueryHelper;

/**
 * CompanyManagement 検索フォーム.<br/>
 *
 */
public class CompanyManagementSearchForm implements java.io.Serializable {

  /** serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * 文字列表現への変換.
   *
   * @return このインスタンスの文字列表現
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
  }

  // ------------------------------------------------------------
  // 検索条件
  // ------------------------------------------------------------

  // ------------------------------------------------------------

  /** 企業名. */
  private String companyName = null;

  /** 企業名検索オプション項目. */
  private String companyNameOption = null;

  /**
   * 企業名取得.
   *
   * @return companyName 企業名
   */
  public String getCompanyName() {
    return this.companyName;
  }

  /**
   * 企業名設定.
   *
   * @param companyName 企業名
   */
  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  /**
   * 企業名検索オプション項目取得.
   *
   * @return companyNameOption 企業名検索オプション項目
   */
  public String getCompanyNameOption() {
    return this.companyNameOption;
  }

  /**
   * 企業名検索オプション項目設定.
   *
   * @param companyNameOption 企業名検索オプション項目
   */
  public void setCompanyNameOption(String companyNameOption) {
    this.companyNameOption = companyNameOption;
  }

  /**
   * 企業名のクエリでの使用値取得.
   *
   * @return companyNameQuery 企業名Query
   */
  public String getCompanyNameQuery() {
    String query = QueryHelper.escape(this.companyName);
    if (this.companyNameOption.equals("1")) {
      return query + "%";
    } else if (this.companyNameOption.equals("2")) {
      return "%" + query;
    } else if (this.companyNameOption.equals("3")) {
      return "%" + query + "%";
    }
    return query;
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
