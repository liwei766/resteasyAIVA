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
package cloud.optim.aivoiceanalytics.api.app.usetime;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;
import cloud.optim.aivoiceanalytics.core.modules.rest.SortForm ;

/**
 * 検索条件.
 */
public class SearchForm implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** 取得条件 */
	private SortForm sortForm;

	/** 検索条件 */
	private UseTimeSearchForm useTime;

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() { return ToStringHelper.toString( this ); }

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
	public void setSortForm( SortForm sortForm ) {

		this.sortForm = sortForm;
	}

	/**
	 * useTime 取得.
	 *
	 * @return useTime
	 */
	public UseTimeSearchForm getUseTime() { return useTime; }

	/**
	 * useTime 設定.
	 *
	 * @param useTime useTime に設定する値.
	 */
	public void setUseTime( UseTimeSearchForm useTime ) { this.useTime = useTime; }
}
