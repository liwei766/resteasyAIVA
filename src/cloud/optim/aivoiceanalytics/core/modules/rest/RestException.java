/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：RestException.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest;

import java.util.ArrayList ;
import java.util.List ;

import org.apache.commons.logging.Log ;

/**
 * REST 用例外クラス.
 *
 * コンストラクタのパラメータと例外クラスの状態
 *
 * <dt>restResult<dt>
 * <dd>restResult に設定される</dd>
 *
 * <dt>level<dt>
 * <dd>restResult の level に設定される</dd>
 *
 * <dt>message<dt>
 * <dd>
 * 常に例外メッセージに設定される（super(message)）.
 * errorCode が一緒に指定された場合は restResult の detail にも設定される.
 * </dd>
 *
 * <dt>cause<dt>
 * <dd>
 * errorCode が一緒に指定された場合は restResult の detail に
 * 文字列表現（toString()）が設定される.
 * </dd>
 *
 * @author itsukaha
 */
public class RestException extends RuntimeException
{
	/** エラー内容 */
	private List<RestResult> restResultList = new ArrayList<RestResult>() ;

	/** serialVersionUID */
	private static final long serialVersionUID = 1L ;

	/** ロガーを指定する場合に作成元で設定する  */
	private Log logger ;

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder() ;

		for ( RestResult result : restResultList )
		{
			sb.append( "\n{ " ) ;
			sb.append( "code=" ).append( result.getCode().getCode() ) ;
			sb.append( ", name=" ).append( result.getCode().name() ) ;
			sb.append( ", detailList=" ).append( result.getDetailList() ) ;
			sb.append( ", message=" ).append( result.getMessage() ) ;
			sb.append( ", logmsg=" ).append( result.getLogMessage() ) ;
			sb.append( " }" ) ;
		}

		if ( sb.length() > 0 )
		{
			sb.delete( 0, 1 ) ; // 先頭の改行を削除
		}

		sb.insert( 0, "RestResult[ " ).append( " ] : " ) ;
		sb.append( super.toString() ) ;

		return sb.toString() ;
	}

	// -------------------------------------------------------------------------
	// コンストラクタ
	// -------------------------------------------------------------------------

	/**
	 * コンストラクタ（実行結果リスト指定）.
	 *
	 * @param restResultList エラー内容
	 */
	public RestException( List<RestResult> restResultList )
	{
		super() ;
		this.restResultList = restResultList ;
	}

	/**
	 * コンストラクタ（実行結果リスト指定）.
	 *
	 * @param restResultList エラー内容
	 * @param message エラーメッセージ
	 */
	public RestException( List<RestResult> restResultList, String message )
	{
		super( message ) ;
		this.restResultList.addAll( restResultList ) ;
	}

	/**
	 * コンストラクタ（実行結果リスト指定）.
	 *
	 * @param restResultList エラー内容
	 * @param cause エラー原因（例外）
	 */
	public RestException( List<RestResult> restResultList, Throwable cause )
	{
		super( cause ) ;
		this.restResultList.addAll( restResultList ) ;
	}

	/**
	 * コンストラクタ（実行結果リスト指定）.
	 *
	 * @param restResult エラー内容
	 * @param message エラーメッセージ
	 * @param cause エラー原因（例外）
	 */
	public RestException( List<RestResult> restResult, String message, Throwable cause )
	{
		super( message, cause ) ;
		this.restResultList.addAll( restResult ) ;
	}

	// -------------------------------------------------------------------------

	/**
	 * コンストラクタ（実行結果×1）.
	 *
	 * @param restResult エラー内容
	 */
	public RestException( RestResult restResult )
	{
		super() ;
		this.restResultList.add( restResult ) ;
	}

	/**
	 * コンストラクタ（実行結果×1）.
	 *
	 * @param restResult エラー内容
	 * @param message エラーメッセージ
	 */
	public RestException( RestResult restResult, String message )
	{
		super( message ) ;
		this.restResultList.add( restResult ) ;
	}

	/**
	 * コンストラクタ（実行結果×1）.
	 *
	 * @param restResult エラー内容
	 * @param cause エラー原因（例外）
	 */
	public RestException( RestResult restResult, Throwable cause )
	{
		super( cause ) ;
		this.restResultList.add( restResult ) ;
	}

	/**
	 * コンストラクタ（実行結果×1）.
	 *
	 * @param restResult エラー内容
	 * @param message エラーメッセージ
	 * @param cause エラー原因（例外）
	 */
	public RestException( RestResult restResult, String message, Throwable cause )
	{
		super( message, cause ) ;
		this.restResultList.add( restResult ) ;
	}

	// -------------------------------------------------------------------------

	/**
	 * コンストラクタ（結果コードのみ）.
	 *
	 * @param code 処理結果コード
	 */
	public RestException( ResponseCode code )
	{
		super() ;
		this.restResultList.add( new RestResult( code ) ) ;
	}

	/**
	 * コンストラクタ（結果コードのみ）.
	 *
	 * @param code 処理結果コード
	 * @param message エラーメッセージ
	 */
	public RestException( ResponseCode code, String message )
	{
		super( message ) ;
		this.restResultList.add( new RestResult( code ) ) ;
	}

	/**
	 * コンストラクタ（結果コードのみ）.
	 *
	 * @param code 処理結果コード
	 * @param cause エラー原因（例外）
	 */
	public RestException( ResponseCode code, Throwable cause )
	{
		super( cause ) ;
		this.restResultList.add( new RestResult( code ) ) ;
	}

	/**
	 * コンストラクタ（結果コードのみ）.
	 *
	 * @param code 処理結果コード
	 * @param message エラーメッセージ
	 * @param cause エラー原因（例外）
	 */
	public RestException( ResponseCode code, String message, Throwable cause )
	{
		super( message, cause ) ;
		this.restResultList.add( new RestResult( code ) ) ;
	}

	// -------------------------------------------------------------------------

	/**
	 * エラー内容取得.
	 *
	 * @return エラー内容
	 */
	public List<RestResult> getRestResultList() { return restResultList ; }

	/**
	 * エラー内容設定.
	 *
	 * @param restResultList エラー内容への設定値
	 */
	public void setRestResultList( List<RestResult> restResultList )
	{
		this.restResultList = restResultList ;
	}

	/**
	 * logger 取得.
	 *
	 * @return logger
	 */
	public Log getLogger()
	{
		return logger ;
	}

	/**
	 * logger 設定.
	 *
	 * @param logger logger への設定値.
	 */
	public void setLogger( Log logger )
	{
		this.logger = logger ;
	}
}
