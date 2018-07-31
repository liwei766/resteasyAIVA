/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：RequestStatusHolder.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest;

import org.springframework.context.annotation.Scope ;
import org.springframework.context.annotation.ScopedProxyMode ;
import org.springframework.stereotype.Component ;

/**
 * エラー発生時に追加で表示／返送するメッセージを格納するためのクラス.
 *
 * @author itsukaha
 */
@Scope( value="request", proxyMode=ScopedProxyMode.TARGET_CLASS )
@Component
public class RequestStatusHolder
{
	/** 応答メッセージの末尾に追加するメッセージ */
	private String additionalMessage ;

	/** ログ出力メッセージの末尾に追加するメッセージ */
	private String addtionalLogMessage ;

	// -------------------------------------------------------------------------

	/**
	 * additionalMessage 取得.
	 *
	 * @return additionalMessage
	 */
	public String getAdditionalMessage()
	{
		return additionalMessage ;
	}

	/**
	 * additionalMessage 設定.
	 *
	 * @param additionalMessage additionalMessage への設定値.
	 */
	public void setAdditionalMessage( String additionalMessage )
	{
		this.additionalMessage = additionalMessage ;
	}

	/**
	 * addtionalLogMessage 取得.
	 *
	 * @return addtionalLogMessage
	 */
	public String getAddtionalLogMessage()
	{
		return addtionalLogMessage ;
	}

	/**
	 * addtionalLogMessage 設定.
	 *
	 * @param addtionalLogMessage addtionalLogMessage への設定値.
	 */
	public void setAddtionalLogMessage( String addtionalLogMessage )
	{
		this.addtionalLogMessage = addtionalLogMessage ;
	}
}
