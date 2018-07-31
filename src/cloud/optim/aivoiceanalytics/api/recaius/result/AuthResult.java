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
public class AuthResult {

	/** トークン */
	private String token;

	/** セッション有効時間 */
	private int expiry_sec;

	/**
	 * @return token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token セットする token
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return expiry_sec
	 */
	public int getExpiry_sec() {
		return expiry_sec;
	}

	/**
	 * @param expiry_sec セットする expiry_sec
	 */
	public void setExpiry_sec(int expiry_sec) {
		this.expiry_sec = expiry_sec;
	}

}
