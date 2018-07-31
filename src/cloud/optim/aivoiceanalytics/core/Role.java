/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：Role.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core;

/**
 * ユーザロール列挙型
 */
public enum Role {
	/** 未ログイン時に適用する権限名 */
	ANONYMOUS("ROLE_ANONYMOUS"),

	/** 一般ユーザ権限名 */
	USER("ROLE_USER"),

	/** 管理者権限名 */
	ADMIN("ROLE_ADMIN"),

	/** 代理店権限名 */
	AGENCY("ROLE_AGENCY"),

	/** システム管理者権限名 */
	SYS_ADMIN("ROLE_SYS_ADMIN");

	/** 権限名 */
	private String role;

	/**
	 * コンストラクタ
	 * @param role 権限名
	 */
	Role (String role) {
		this.role = role;
	}

	/**
	 * @return value
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role セットする role
	 */
	public void setRole(String role) {
		this.role = role;
	}
}
