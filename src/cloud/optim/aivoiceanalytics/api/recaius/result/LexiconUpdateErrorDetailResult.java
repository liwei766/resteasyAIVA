/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：AuthResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.recaius.result;

/**
 * リカイアス認証処理結果クラス
 */
public class LexiconUpdateErrorDetailResult {
	/** データ番号 */
	private int no;

	/** エラーメッセージ */
	private String errmsg;

	/**
	 * @return no
	 */
	public int getNo() {
		return no;
	}

	/**
	 * @param no セットする no
	 */
	public void setNo(int no) {
		this.no = no;
	}

	/**
	 * @return errmsg
	 */
	public String getErrmsg() {
		return errmsg;
	}

	/**
	 * @param errmsg セットする errmsg
	 */
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
}
