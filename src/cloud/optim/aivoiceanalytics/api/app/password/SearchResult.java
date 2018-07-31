/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SearchResult.java
 * 概要：
 *
 * 修正履歴：
 *	 編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.password;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;

/**
 * ユーザー 検索結果.<br/>
 */
public class SearchResult implements java.io.Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() { return ToStringHelper.toString( this ); }

	/** ユーザーGID. */
	private String userGuid;

	/** ユーザー名称. */
	private String userName;

	/**
	 * userGuid 取得.
	 *
	 * @return userGuid
	 */
	public String getUserGuid() {
		return this.userGuid;
	}

	/**
	 * userGuid 設定.
	 *
	 * @param userGuid ユーザーGID
	 */
	public void setUserGuid(String userGuid) {
		this.userGuid = userGuid;
	}

	/**
	 * userName 取得.
	 *
	 * @return userName
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * userName 設定.
	 *
	 * @param userName ユーザー名称
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

}
