/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：ExceptionUtil.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest;

import java.util.ArrayList ;
import java.util.List ;

import javax.persistence.OptimisticLockException;

import org.apache.commons.logging.Log ;
import org.hibernate.StaleObjectStateException ;
import org.springframework.dao.DataAccessException ;
import org.springframework.transaction.TransactionException ;

import cloud.optim.aivoiceanalytics.core.common.DaoException;


/**
 * 例外に関する共通処理
 */
public class ExceptionUtil
{
	/**
	 * 発生した例外と処理結果メッセージから RestException を生成する.
	 *
	 * @param logger 出力先のロガー
	 * @param code デフォルトの結果コード
	 * @param messageParam メッセージの可変部分
	 * @param detail 応答詳細
	 * @param logMessage ログ出力メッセージ
	 * @param cause 発生した例外
	 *
	 * @return RestException
	 */
	public static RestException handleException( Log logger,
		ResponseCode code, Object[] messageParam, String[] detail,
		Object logMessage, Exception cause )
	{
		RestException ret = null ;

		if ( cause instanceof RestException )
		{
			ret = (RestException)cause ;
		}
		else
		{
			ret = new RestException(
				handleRestResult(
					code, messageParam, detail, logMessage, cause ),
				cause != null ? cause.getMessage() : "",
				cause ) ;
		}

		ret.setLogger( logger ) ;

		return ret ;
	}

	/**
	 * 発生した例外と処理結果メッセージから RestResult を生成する.
	 *
	 * @param code デフォルトの結果コード
	 * @param messageParam メッセージの可変部分
	 * @param detail 応答詳細
	 * @param logMessage ログ出力メッセージ
	 * @param cause 発生した例外
	 *
	 * @return 生成した RestResult リスト（要素数は常に 1）
	 */
	private static List<RestResult> handleRestResult(
		ResponseCode code, Object[] messageParam, String[] detail,
		Object logMessage, Exception cause )
	{
		List<RestResult> ret = new ArrayList<RestResult>( 1 ) ;

		if ( cause == null )
		{
			// デフォルトの実行結果を作成

			ret.add( new RestResult( code, messageParam, detail, logMessage ) ) ;
		}
		else if ( cause instanceof RestException )
		{
			// アプリ内で判定済み

			ret = ((RestException)cause).getRestResultList() ;
		}
		else if ( cause instanceof DaoException )
		{
			// DAO 内部エラー

			Throwable inner = cause.getCause() ;

			if ( inner instanceof StaleObjectStateException )
			{
				ret.add( new RestResult( ResponseCode.OPTIMISTIC_LOCK, messageParam, detail, logMessage ) ) ;
			}
			else if ( inner instanceof OptimisticLockException )
			{
				ret.add( new RestResult( ResponseCode.OPTIMISTIC_LOCK, messageParam, detail, logMessage ) ) ;
			}
			else
			{
				ret.add( new RestResult( ResponseCode.DB_ERROR, messageParam, detail, logMessage ) ) ;
			}
		}
		else if ( ( cause instanceof DataAccessException ) ||
// TODO itsukaha MyBatis 版は？			 ( cause instanceof NestedSQLException ) ||
			 ( cause instanceof TransactionException )
			 )
		{
			// その他の DB エラー

			ret.add( new RestResult( ResponseCode.DB_ERROR, messageParam, detail, logMessage ) ) ;
		}
		else if ( cause instanceof StaleObjectStateException )
		{
			// 楽観ロックエラー

			ret.add( new RestResult( ResponseCode.OPTIMISTIC_LOCK, messageParam, detail, logMessage ) ) ;
		}
		else if ( cause instanceof OptimisticLockException )
		{
			ret.add( new RestResult( ResponseCode.OPTIMISTIC_LOCK, messageParam, detail, logMessage ) ) ;
		}
		else
		{
			// デフォルトの実行結果を作成

			ret.add( new RestResult( code, messageParam, detail, logMessage ) ) ;
		}

		return ret ;
	}
}
