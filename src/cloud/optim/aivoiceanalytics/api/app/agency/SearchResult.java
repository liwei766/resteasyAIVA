/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SearchResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.agency;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;

/**
 * 検索結果 1 行分のデータ.
 */
public class SearchResult implements java.io.Serializable {

  /** serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** Agency. */
  private AgencySearchResult agency;

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
   * agency 取得.
   *
   * @return agency
   */
  public AgencySearchResult getAgency() {
    return agency;
  }

  /**
   * agency 設定.
   *
   * @param agency agency に設定する値.
   */
  public void setAgency(AgencySearchResult agency) {
    this.agency = agency;
  }

}
