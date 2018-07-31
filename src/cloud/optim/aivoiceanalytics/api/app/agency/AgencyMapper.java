/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：AgencyMapper.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.agency;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * MyBatis AgencyMapper I/F.<br/>
 */
@Component
public interface AgencyMapper {

  /**
   * 検索.
   *
   * @param form 検索条件
   * @return 検索結果
   */
  List<SearchResult> search(SearchForm form);
}
