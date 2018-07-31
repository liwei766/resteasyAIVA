/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：MessageUtility.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest;

import java.util.List ;
import java.util.Locale ;

import javax.annotation.Resource ;

import org.apache.commons.lang3.StringUtils ;
import org.apache.commons.logging.Log ;
import org.apache.commons.logging.LogFactory ;
import org.springframework.context.MessageSource ;
import org.springframework.stereotype.Component ;

/**
 * REST 用ログ出力クラス
 *
 * @author itsukaha
 */
@Component
public class MessageUtility
{
	/** Commons Logging instance.  */
	@SuppressWarnings( "unused" )
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/** メッセージソース */
	@Resource private MessageSource messageSource ;

	/**
	 * メッセージが未設定の場合に、応答コードごとの既定メッセージを設定する.
	 *
	 * @param result REST API 処理結果
	 */
	public void fillMessage( RestResult result )
	{
		if ( StringUtils.isEmpty( result.getMessage() ) )
		{
			result.setMessage(
				getMessage( result.getCode(), result.getMessageParam() ) ) ;
		}
	}

	/**
	 * メッセージが未設定の処理結果があれば、応答コードごとの既定メッセージを設定する.
	 *
	 * @param results REST API 処理結果リスト
	 */
	public void fillMessage( List<RestResult> results )
	{
		for ( RestResult result : results )
		{
			fillMessage( result ) ;
		}
	}

	// -------------------------------------------------------------------------
	// ログメッセージ取得
	// -------------------------------------------------------------------------

	/**
	 * 現在のロケールを取得
	 *
	 * @return 現在のロケール
	 */
	public Locale getCurrentLocale()
	{
		// TODO ユーザが選択中の言語を返却する。現在はデフォルトロケールを返却する。
		return Locale.getDefault() ;
	}

	/**
	 * メッセージリソースのキー名から対応するメッセージを取得
	 *
	 * @param key キー名
	 * @param params 可変部分（配列／可変長パラメータ）
	 * @return 取得したメッセージ
	 */
	public String getMessage( String key, Object ... params )
	{
		return messageSource.getMessage( key, params, key, getCurrentLocale() ) ;
	}

	/**
	 * メッセージリソースのキー名から対応するメッセージを取得
	 *
	 * @param key キー名
	 * @param locale ロケール
	 * @param params 可変部分（配列／可変長パラメータ）
	 * @return 取得したメッセージ
	 */
	public String getMessage( String key, Locale locale, Object ... params )
	{
		return messageSource.getMessage( key, params, key, locale ) ;
	}

	// -------------------------------------------------------------------------

	/**
	 * 応答結果に対応する実行結果メッセージを取得
	 *
	 * @param result 応答結果
	 * @param params 可変部分（配列／可変長パラメータ）
	 * @return 取得したメッセージ
	 */
	public String getMessage( RestResult result, Object ... params )
	{
		return getMessage( result.getCode(), params ) ;
	}

	/**
	 * 応答結果コードに対応する実行結果メッセージを取得
	 *
	 * @param code	応答結果コード
	 * @param params 可変部分（配列／可変長パラメータ）
	 * @return 取得したメッセージ
	 */
	public String getMessage( ResponseCode code, Object ... params )
	{
		return getMessage( "msg." + code.getCode(), params ) ;
	}
}
