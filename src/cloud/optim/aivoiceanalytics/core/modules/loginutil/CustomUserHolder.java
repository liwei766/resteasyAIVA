/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：CustomUserHolder.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.loginutil;

import java.io.Serializable ;

import org.springframework.context.annotation.Scope ;
import org.springframework.context.annotation.ScopedProxyMode ;
import org.springframework.stereotype.Component ;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper ;

/**
 * ログインユーザ拡張情報保持クラス.
 * ログイン成功時に追加で取得し、セッション上に保持する情報
 *
 * @author itsukaha
 */
@Component
@Scope( proxyMode=ScopedProxyMode.TARGET_CLASS, value="session" )
public class CustomUserHolder implements Serializable
{
	/** serialVersionUID */
	private static final long serialVersionUID = 1L ;

	/**
	 * カスタムユーザ
	 */
	private CustomUser customUser ;

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() { return ToStringHelper.toString( this ) ; }

	/**
	 * customUser 取得.
	 *
	 * @return customUser
	 */
	public CustomUser getCustomUser()
	{
		return customUser ;
	}

	/**
	 * customUser 設定.
	 *
	 * @param customUser customUser に設定する値.
	 */
	public void setCustomUser( CustomUser customUser )
	{
		this.customUser = customUser ;
	}
}