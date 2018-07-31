/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：RestLog.java
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
import org.springframework.stereotype.Component ;

import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode.Level;

/**
 * REST 用ログ出力クラス
 *
 * @author itsukaha
 */
@Component
public class RestLog
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility ;

	// -------------------------------------------------------------------------
	// ログメッセージ取得
	// -------------------------------------------------------------------------

	/**
	 * メッセージリソースのキー名から対応するメッセージを取得
	 *
	 * @param key キー名
	 * @param params 可変部分（配列／可変長パラメータ）
	 * @return 取得したメッセージ
	 */
	public String getLogMessage( String key, Object ... params )
	{
		return messageUtility.getMessage( key, Locale.getDefault(), params ) ;
	}

	// -------------------------------------------------------------------------

	/**
	 * 応答結果に対応するログ出力メッセージを取得
	 *
	 * @param result 応答結果
	 * @param params その他の出力項目
	 * @return 取得したメッセージ
	 */
	public String getLogMessage( RestResult result, Object ... params )
	{
		return getLogMessage( result.getCode(), params ) ;
	}

	/**
	 * 応答結果コードに対応するログ出力メッセージを取得
	 *
	 * @param code	応答結果コード
	 * @param params 可変部分（配列／可変長パラメータ）
	 * @return 取得したメッセージ
	 */
	public String getLogMessage( ResponseCode code, Object ... params )
	{
		String key = "msg." + code.getCode() + ".log" ;
		String msg = getLogMessage( key, params ) ;

		if ( key.equals( msg ) ) // 該当なし
		{
			return null ;
		}
		return msg ;
	}

	// -------------------------------------------------------------------------
	// API のログ
	// -------------------------------------------------------------------------

	/**
	 * API 開始ログ
	 *
	 * @param log	出力先のログ
	 * @param methodName	処理開始するメソッド名
	 * @param request リクエスト
	 * @param params	メソッドパラメータ（配列）
	 */
	public void start( Log log, String methodName, Object request, Object ... params )
	{
		if ( log == null ) log = this.log ;
		if ( params == null ) params = new Object[] {} ;

		log.info( format( "### START", methodName, params ) ) ;
		log.info( format( "--- REQUEST ", methodName, request ) ) ;
	}

	/**
	 * API 終了ログ
	 *
	 * @param log	出力先のログ
	 * @param methodName	処理開始するメソッド名
	 * @param request リクエスト（使用しない）
	 * @param response	レスポンス
	 * @param result 処理結果
	 * @param params	その他の出力項目
	 */
	public void end( Log log, String methodName,
		Object request, Object response, List<RestResult> result, Object ... params )
	{
		if ( log == null ) log = this.log ;
		if ( params == null ) params = new Object[] {} ;

		log.info( format( "### END", methodName, params ) ) ;
		log.info( format( "--- RESPONSE ", methodName, response ) ) ;
	}

	/**
	 * API エラー終了ログ（共通例外ハンドラ用）.<br/>
	 * - メソッド名とリクエスト情報は利用できない
	 *
	 * @param log	出力先のログ
	 * @param response	レスポンス
	 * @param result 処理結果
	 * @param error	エラー終了のとき、発生した例外
	 * @param params	その他の出力項目
	 */
	public void abort( Log log,
		Object response, List<RestResult> result, Exception error, Object ... params )
	{
		String methodName = "-" ;

		if ( log == null ) log = this.log ;
		if ( params == null ) params = new Object[] {} ;

		String msg = format( "### ABORT", methodName + " : " +  error, params ) ;
		Level level = getLevel( result, error ) ;

		if ( level == Level.ERROR ) log.error( msg, error ) ;
		else if ( level == Level.WARN ) log.warn( msg, error ) ;
		else if ( level == Level.IGNORE ) log.debug( msg, error ) ;
		else log.info( msg, error );

		log.info( format( "--- RESPONSE ", methodName, response ) ) ;
	}

	/**
	 * API 終了ログ（個別出力用）.<br/>
	 * - メソッド名とリクエスト情報を利用可能
	 *
	 * @param log	出力先のログ
	 * @param methodName	処理開始するメソッド名
	 * @param request リクエスト（使用しない）
	 * @param response	レスポンス
	 * @param result 処理結果
	 * @param error	エラー終了のとき、発生した例外
	 * @param params	その他の出力項目
	 */
	public void abort( Log log, String methodName,
		Object request, Object response, List<RestResult> result, Exception error, Object ... params )
	{
		if ( log == null ) log = this.log ;
		if ( params == null ) params = new Object[] {} ;

		String msg = format( "### ABORT", methodName + " : " +  error, params ) ;
		Level level = getLevel( result, error ) ;

		if ( level == Level.ERROR ) log.error( msg, error ) ;
		else if ( level == Level.WARN ) log.warn( msg, error ) ;
		else if ( level == Level.IGNORE ) log.debug( msg, error ) ;
		else log.info( msg, error );

		log.info( format( "--- RESPONSE ", methodName, response ) ) ;
	}

	/**
	 * 一括更新系 API の 1 件終了ログ
	 *
	 * @param log	出力先のログ
	 * @param methodName	処理開始するメソッド名
	 * @param response	レスポンス
	 * @param result 処理結果
	 * @param params	その他の出力項目
	 */
	public void endOne( Log log, String methodName,
		Object response, List<RestResult> result, Object ... params )
	{
		if ( log == null ) log = this.log ;
		if ( params == null ) params = new Object[] {} ;

		if ( log.isDebugEnabled() )
		{
			log.debug( format( "### ONE-END", methodName, params ) ) ;
			log.debug( format( "--- RESULT ", methodName, response ) ) ;
		}

	}

	/**
	 * 一括更新系 API の 1 件エラー終了ログ
	 *
	 * @param log	出力先のログ
	 * @param methodName	処理開始するメソッド名
	 * @param response	レスポンス
	 * @param result 処理結果
	 * @param error	エラー終了のとき、発生した例外
	 * @param params	その他の出力項目
	 */
	public void abortOne( Log log, String methodName,
		Object response, List<RestResult> result, Exception error, Object ... params )
	{
		if ( log == null ) log = this.log ;
		if ( params == null ) params = new Object[] {} ;

		log.error( format( "### ONE-ABORT", methodName, error, params ), error ) ;
		log.info( format( "--- RESULT ", methodName, response ) ) ;
	}

	// -------------------------------------------------------------------------
	// 内部で使用する共通処理
	// -------------------------------------------------------------------------

	/**
	 * 見出し／メソッド名／その他の整形
	 *
	 * @param title 見出し
	 * @param methodName メソッド名
	 * @param params その他の出力パラメータ
	 *
	 * @return 整形後の文字列
	 */
	private String format( String title, String methodName, Object ... params )
	{
		return concat( title, " : ", methodName, " : ", ( params == null ) ? "null" : StringUtils.join( params, ";" ) ) ;
	}

	/**
	 * 文字列の連結処理.
	 * StringBuilder を使用して、指定された文字列を連結する
	 *
	 * @param strs 連結する文字列（配列）
	 * @return 連結後の文字列
	 */
	private String concat( Object ... strs )
	{
		StringBuilder sb = new StringBuilder() ;

		for ( Object str : strs )
		{
			sb.append( String.valueOf( str ) ) ;
		}

		return sb.toString() ;
	}

	/**
	 * 処理結果とエラー原因からログ出力レベルを算出する
	 *
	 * @param result 処理結果
	 * @param error エラー終了のとき、発生した例外
	 *
	 * @return ログ出力レベル
	 */
	private Level getLevel( List<RestResult> result, Exception error )
	{
		Level ret = null ;

		// result が未設定の場合は error から処理結果を取得

		if ( ( result == null ) || ( result.size() < 1 ) )
		{
			if ( error instanceof RestException )
			{
				result = ((RestException)error).getRestResultList() ;
			}
		}

		if ( result == null ) return Level.ERROR ; // 不明

		// 一番高いレベルを採用

		for ( RestResult res : result )
		{
			if ( res == null ) continue ;
			if ( res.getCode() == null ) continue ;
			Level level = res.getCode().getLevel() ;

			if ( ( ret == null ) || ( ret.ordinal() < level.ordinal() ) )
			{
				ret = level ;
			}
		}

		if ( ret == null ) ret = Level.ERROR ; // 不明

		return ret ;
	}
}
