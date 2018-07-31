/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：LexiconErrorCode.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.recaius;

/**
 * ユーザ辞書登録レスポンスステータスが400の時のエラーコード列挙型
 */
public enum LexiconErrorCode {
	INVALID_PARAMETER(2),
	JSON_FORMAT_ERROR(16),
	PARTIAL_ERROR(1022);

	/** エラーメッセージに対応するレスポンスコード */
	private int code;

	/**
	 * コンストラクタ
	 * @param errorMessage エラーメッセージ
	 * @param responseCode レスポンスコード
	 */
	LexiconErrorCode (int code) {
		this.code = code;
	}

	/**
	 * @return code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code セットする code
	 */
	public void setCode(int code) {
		this.code = code;
	}


}
