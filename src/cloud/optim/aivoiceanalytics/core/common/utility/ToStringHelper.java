/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * ソースファイル名：ToStringHelper.java
 */
package cloud.optim.aivoiceanalytics.core.common.utility;

import java.util.Map ;

import org.apache.commons.beanutils.PropertyUtils ;

/**
 * ビーンの文字列表現への変換ヘルパークラス
 *
 * @author itsukaha
 */
public class ToStringHelper
{
	/**
	 * 指定されたオブジェクトの文字列表現を取得する（指定したフィールドの値はマスクする）.
	 * 指定されたフィールドの値は固定文字列に置き換える.
	 * ただし、null の場合は "null" に置き換える
	 *
	 * @param target 文字列表現を取得するオブジェクト
	 * @param ignoreList マスクするフィールド名
	 *
	 * @return 指定されたオブジェクトの文字列表現
	 */
	public static String toString( Object target, String ... ignoreList )
	{
		if ( target == null ) return String.valueOf( target ) ;

		StringBuilder sb = new StringBuilder() ;

		sb
			.append( target.getClass().getName() )
			.append("@")
			.append( Integer.toHexString( target.hashCode() ) );

		try
		{
			Map<String, Object> map = PropertyUtils.describe( target ) ;

			if ( ignoreList != null ) // 指定された項目をマスクする
			{
				for ( String name : ignoreList )
				{
					if ( ! map.containsKey( name ) ) continue ;

					map.put( name, ( map.get( name ) == null ) ? "null" : "*****" ) ;
				}
			}

			map.remove( "class" ) ; // クラス名は変換済み

			sb.append( map.toString() ) ;

			return sb.toString() ;
		}
		catch ( Exception ex )
		{
			return  target.getClass().getName() + "( cannot describe )" ;
		}
	}
}
