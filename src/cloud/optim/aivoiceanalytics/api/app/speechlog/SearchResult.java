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
package cloud.optim.aivoiceanalytics.api.app.speechlog;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;

/**
 * 検索結果 1 行分のデータ.
 */
public class SearchResult implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** SpeechLog */
	private SpeechLogSearchResult speechLog;

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
	public SpeechLogSearchResult getSpeechLog() { return speechLog; }

	/**
	 * speechLog 設定.
	 *
	 * @param speechLog speechLog に設定する値.
	 */
	public void setSpeechLog( SpeechLogSearchResult speechLog ) { this.speechLog = speechLog; }
}
