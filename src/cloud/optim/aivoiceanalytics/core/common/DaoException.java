/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：DaoException.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.common;

import org.springframework.dao.DataAccessException ;

/**
 *
 *
 * @author itsukaha
 */
public class DaoException extends DataAccessException
{
	/**
	 * DAO 内で発生した例外から DataAccessException を生成する
	 *
	 * @param ex 原因となった例外
	 *
	 * @return 生成した DataAccessException インスタンス
	 */
	public static DataAccessException handleDBException( Throwable ex )
	{
		if ( ex instanceof DaoException ) return (DaoException)ex ;

		return new DaoException(ex.toString(), ex)  ;
	}

	/** serialVersionUID */
	private static final long serialVersionUID = 1L ;

	/**
	 * コンストラクタ.
	 *
	 * @param msg メッセージ
	 */
	public DaoException( String msg )
	{
		super( msg ) ;
	}

	/**
	 * コンストラクタ.
	 *
	 * @param msg メッセージ
	 * @param cause 原因となった例外
	 */
	public DaoException( String msg, Throwable cause )
	{
		super( msg, cause ) ;
	}
}
