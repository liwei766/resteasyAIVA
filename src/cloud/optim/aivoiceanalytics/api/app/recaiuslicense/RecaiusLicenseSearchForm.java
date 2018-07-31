/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：RecaiusLicenseSearchForm.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.recaiuslicense;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import cloud.optim.aivoiceanalytics.core.common.utility.QueryHelper;

/**
 * RecaiusLicense 検索フォーム.<br/>
 *
 */
public class RecaiusLicenseSearchForm implements java.io.Serializable {

  /** serialVersionUID. */
  private static final long serialVersionUID = 1L; // TODO 新しく値をジェネレートすべき

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

  /** 代理店企業ID. */
  private String agencyCompanyId = null;

  /** 代理店企業ID検索オプション項目. */
  private String agencyCompanyIdOption = null;

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

  /**
   * 代理店企業ID検索オプション項目取得.
   *
   * @return agencyCompanyIdOption 代理店企業ID検索オプション項目
   */
  public String getAgencyCompanyIdOption() {
    return this.agencyCompanyIdOption;
  }

  /**
   * 代理店企業ID検索オプション項目設定.
   *
   * @param agencyCompanyIdOption 代理店企業ID検索オプション項目
   */
  public void setAgencyCompanyIdOption(String agencyCompanyIdOption) {
    this.agencyCompanyIdOption = agencyCompanyIdOption;
  }

  /**
   * 代理店企業IDのクエリでの使用値取得.
   *
   * @return agencyCompanyIdQuery 代理店企業IDQuery
   */
  public String getAgencyCompanyIdQuery() {
    String query = QueryHelper.escape(this.agencyCompanyId);
    if (this.agencyCompanyIdOption.equals("1")) {
      return query + "%";
    } else if (this.agencyCompanyIdOption.equals("2")) {
      return "%" + query;
    } else if (this.agencyCompanyIdOption.equals("3")) {
      return "%" + query + "%";
    }
    return query;
  }

  // ------------------------------------------------------------

}
