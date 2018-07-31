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
package cloud.optim.aivoiceanalytics.api.app.usetime;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;

/**
 * 検索結果 1 行分のデータ.
 */
public class SearchResult implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** UseTime */
	private UseTimeSearchResult useTime;

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() { return ToStringHelper.toString( this ); }

	/**
	 * useTime 取得.
	 *
	 * @return useTime
	 */
	public UseTimeSearchResult getUseTime() { return useTime; }

	/**
	 * useTime 設定.
	 *
	 * @param useTime useTime に設定する値.
	 */
	public void setUseTime( UseTimeSearchResult useTime ) { this.useTime = useTime; }
}
