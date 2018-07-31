/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：UseTimeMapper.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.usetime;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * MyBatis UseTimeMapper I/F.<br/>
 */
@Component
public interface UseTimeMapper {

	/**
	 * 企業毎の利用時間検索
	 *
	 * @param form 検索条件
	 * @return 検索結果
	 */
	List<SearchResult> searchByCompanyId( SearchForm form );

	/**
	 * ユーザ毎の利用時間検索
	 *
	 * @param form 検索条件
	 * @return 検索結果
	 */
	List<SearchResult> searchByUserId( SearchForm form );

	/**
	 * ユーザの指定つきの利用時間合計を取得する
	 * @param companyId 企業ID
	 * @param userId ユーザID
	 * @param date 日時
	 * @return 利用時間合計
	 */
	Long getUsersUseTimeSummary(@Param("companyId") String companyId, @Param("userId") String userId);
}
