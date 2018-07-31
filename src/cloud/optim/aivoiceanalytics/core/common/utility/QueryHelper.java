/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * ソースファイル名：QueryHelper.java
 */
package cloud.optim.aivoiceanalytics.core.common.utility ;

import java.util.ArrayList;
import java.util.List ;

/**
 * 検索機能用クエリ作成ヘルパークラス
 *
 * @author itsukaha
 */
public class QueryHelper
{
	// -------------------------------------------------------------------------
	// エスケープ処理
	// 検索クエリ内に記述する場合にエスケープが必要な文字列の処理
	// -------------------------------------------------------------------------

	/** エスケープ対象文字：エスケープ処理が必要な文字 */
	private static final List<String> ESCAPE_TARGET_CHAR ;
	static
	{
		List<String> temp = new ArrayList<String>() ;
		temp.add( "%" ) ;
		temp.add( "_" ) ;
		ESCAPE_TARGET_CHAR = temp ;
	}

	/** エスケープ文字：エスケープ対象文字はこの文字を直前に置いてエスケープする */
	private static final String ESCAPE_CHAR = "\\" ;

	/** エスケープ文字のエスケープ後の記述 */
	private static final String ESCAPE_AFTER = "\\\\" ;

	/**
	 * 検索クエリに記述する文字列をエスケープする.
	 *
	 * @param src 処理対象文字列
	 * @return エスケープ済み文字列
	 */
	public static String escape( String src )
	{
		if ( (src == null ) || (src.isEmpty() ) ) return "";

		StringBuilder sb = new StringBuilder( src ) ;

		/** エスケープ文字を変換 */
		int pos = 0 ;
		while ( (pos = (sb.indexOf( ESCAPE_CHAR, pos ))) >= 0 )
		{
			sb.replace( pos, pos + 1, ESCAPE_AFTER ) ;
			pos += ESCAPE_AFTER.length() ;
		}

		/** エスケープ対象文字を変換 */
		for ( String target : ESCAPE_TARGET_CHAR )
		{
			String after = null ;
			pos = 0 ;

			while ( ( pos = sb.indexOf( target, pos ) ) >= 0 )
			{
				if ( after == null ) after = ESCAPE_CHAR + target ;

				sb.replace( pos, pos + 1, after ) ;
				pos += after.length() ;
			}
		}

		return sb.toString() ;
	}

	// -------------------------------------------------------------------------
	// クエリ作成
	// -------------------------------------------------------------------------

	/** 一致種別：完全一致 */
	public static final String MATCH_ALL = "0" ;

	/** 一致種別：前方一致 */
	public static final String MATCH_FORWARD = "1" ;

	/** 一致種別：後方一致 */
	public static final String MATCH_BACKWARD = "2" ;

	/** 一致種別：部分一致 */
	public static final String MATCH_PARTIAL = "3" ;
}