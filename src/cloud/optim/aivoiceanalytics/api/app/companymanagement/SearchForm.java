/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SearchForm.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.companymanagement;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;
import cloud.optim.aivoiceanalytics.core.modules.rest.SortForm ;

/**
 * 検索条件.
 */
public class SearchForm implements java.io.Serializable {

  /** serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** 取得条件. */
  private SortForm sortForm;

  /** 検索条件. */
  private CompanyManagementSearchForm companyManagement;

  /**
   * 文字列表現への変換.
   *
   * @return 文字列表現
   */
  @Override
  public String toString() {
    return ToStringHelper.toString(this);
  }

  /**
   * sortForm 取得.
   *
   * @return sortForm
   */
  public SortForm getSortForm() {
    return sortForm;
  }

  /**
   * sortForm 設定.
   *
   * @param sortForm sortForm に設定する値.
   */
  public void setSortForm(SortForm sortForm) {
    this.sortForm = sortForm;
  }

  /**
   * companyManagement 取得.
   *
   * @return companyManagement
   */
  public CompanyManagementSearchForm getCompanyManagement() {
    return companyManagement;
  }

  /**
   * companyManagement 設定.
   *
   * @param companyManagement companyManagement に設定する値.
   */
  public void setCompanyManagement(CompanyManagementSearchForm companyManagement) {
    this.companyManagement = companyManagement;
  }

}
