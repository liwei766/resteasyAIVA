/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechLogRequest.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.speechlog;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import cloud.optim.aivoiceanalytics.api.entity.SpeechLog;
import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;

/**
 * SpeechLog API リクエストクラス.<br/>
 */
@XmlRootElement( name="restRequest" )
public class SpeechLogRequest implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** 検索条件 */
	private SearchForm searchForm;

	// -------------------------------------------------------------------------

	/** 1 エンティティ情報 */
	private EditForm editForm;

	// -------------------------------------------------------------------------

	/** 一括処理情報 */
	private List<SearchResult> bulkFormList;

	// -------------------------------------------------------------------------

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString()
	{
		return ToStringHelper.toString( this );
	}

	// -------------------------------------------------------------------------
	// 内部クラス
	// -------------------------------------------------------------------------

	/** 編集用 */
	public static final class EditForm implements java.io.Serializable
	{
		/** serialVersionUID  */
		private static final long serialVersionUID = 1L;

		/** 1 エンティティ情報 */
		private SpeechLog speechLog;

		/**
		 * 文字列表現への変換
		 *
		 * @return 文字列表現
		 */
		@Override
		public String toString() { return ToStringHelper.toString( this ); }

		/**
		 * speechLog 取得.
		 *
		 * @return speechLog
		 */
		public SpeechLog getSpeechLog() { return speechLog; }

		/**
		 * speechLog 設定.
		 *
		 * @param speechLog speechLog に設定する値.
		 */
		public void setSpeechLog( SpeechLog speechLog ) { this.speechLog = speechLog; }
	}

	// -------------------------------------------------------------------------
	// アクセサメソッド
	// -------------------------------------------------------------------------

	/**
	 * searchForm 取得.
	 *
	 * @return searchForm
	 */
	public SearchForm getSearchForm() {

		return searchForm;
	}

	/**
	 * searchForm 設定.
	 *
	 * @param searchForm searchForm に設定する値.
	 */
	public void setSearchForm( SearchForm searchForm ) {

		this.searchForm = searchForm;
	}

	/**
	 * editForm 取得.
	 *
	 * @return editForm
	 */
	public EditForm getEditForm() {

		return editForm;
	}

	/**
	 * editForm 設定.
	 *
	 * @param entity editForm に設定する値.
	 */
	public void setEditForm( EditForm entity ) {

		this.editForm = entity;
	}

	/**
	 * bulkFormList 取得.
	 *
	 * @return bulkFormList
	 */
	public List<SearchResult> getBulkFormList() {

		return bulkFormList;
	}

	/**
	 * bulkFormList 設定.
	 *
	 * @param bulkFormList bulkFormList に設定する値.
	 */
	public void setBulkFormList( List<SearchResult> bulkFormList ) {

		this.bulkFormList = bulkFormList;
	}
}
