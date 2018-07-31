/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * ソースファイル名：FileHelper.java
 */
package cloud.optim.aivoiceanalytics.core.common.utility;

import java.io.File ;
import java.io.FileNotFoundException ;
import java.io.IOException ;
import java.util.HashSet ;
import java.util.Set ;

import org.apache.commons.lang3.StringUtils ;

/**
 * ファイルアクセスヘルパークラス
 *
 * @author itsukaha
 */
public class FileHelper
{
	/** ディレクトリ名／ファイル名の区切り文字 */
	private static final String DIR_SEPARATOR = "/" ;

	// -------------------------------------------------------------------------

	/**
	 * 指定された文字列をパスの区切り文字（/）で連結
	 *
	 * @param names ディレクトリ名／ファイル名
	 *
	 * @return 連結後の文字列
	 */
	public static String pathConcat( String ... names )
	{
		return StringUtils.join( names, DIR_SEPARATOR ) ;
	}

	/**
	 * 指定されたパスの拡張子（最後のピリオドより後の文字列）を取得.
	 * ピリオドが存在しない場合等は空文字列を返す.
	 *
	 * @param path 拡張子を取得するパス
	 * @return 拡張子
	 */
	public static String getExtension( String path )
	{
		String ret = "" ;

		if ( path == null ) return ret ;

		int index = path.lastIndexOf( "." ) ;

		if ( ( index < 0 ) || ( index >= path.length() - 1 ) ) return ret ;

		ret = path.substring( index + 1 ) ;

		return ret ;
	}

	/**
	 * 指定されたパスのファイル名から拡張子を削除した名前を取得.
	 *
	 * @param path ファイル名を取得するパス
	 * @return 拡張子を除いたファイル名
	 */
	public static String getBasename( String path )
	{
		File file = new File( path ) ;

		StringBuilder ret = new StringBuilder( file.getName() ) ;

		int index = path.lastIndexOf( "." ) ;

		if ( index >= 0 ) ret.delete( index, ret.length() ) ;

		return ret.toString() ;
	}

	// -------------------------------------------------------------------------

	/**
	 * 指定されたディレクトリから、空でなくなるまでさかのぼって削除.
	 * 例外は全て無視する.
	 *
	 * @param dir 削除対象ディレクトリ
	 * @param stopName 指定した名前のディレクトリ以上は削除しない（そこでストップ）
	 */
	public static void delEmptyAncestor( File dir, String stopName )
	{
		if ( dir == null ) return ;

		try
		{
			String stopFileName = ( new File( stopName ) ).getAbsolutePath();

			while( dir.exists() )
			{
				if ( dir.getName().equals( stopName ) || dir.getAbsolutePath().equals( stopFileName ) )
				{
					break ;
				}

				File[] children = dir.listFiles() ;

				if ( ( children == null ) || ( children.length > 0 ) ) break ;

				if ( ! dir.delete() ) break ;

				dir = dir.getParentFile() ;
			}
		}
		catch ( Exception ex ) {} // 無視
	}

	// -------------------------------------------------------------------------

	/** ファイル取得対象外のファイル名 */
	private static final Set<String> ignoreFileNameList ;
	static
	{
		ignoreFileNameList = new HashSet<String>( 1 ) ;
		ignoreFileNameList.add( "Thumbs.db" ) ;
	}

	/**
	 * 指定されたディレクトリ内の（唯一の）ファイルを取得.
	 * 複数ファイルが含まれる場合は例外をスローする
	 *
	 * @param dirPath ファイルを取得するディレクトリのパス
	 *
	 * @return ディレクトリに含まれるファイル. 空ディレクトリの場合は null
	 *
	 * @throws IOException エラー発生時
	 */
	public static File getChildFile( String dirPath ) throws IOException
	{
		if ( dirPath == null ) return null ;

		// 親ディレクトリ（格納ディレクトリ）の存在確認

		File dir = new File( dirPath ) ;

		if ( ! dir.exists() )
		{
			throw new FileNotFoundException( dirPath ) ;
		}

		// 格納ディレクトリ内のファイルを取得（1 つしか存在しない前提）

		File[] files = dir.listFiles() ;

		if ( files == null )
		{
			throw new IOException( dirPath + " is not a directory." ) ;
		}

		if ( files.length < 1 )
		{
			return null ; // 空ディレクトリ
		}

		if ( files.length > ignoreFileNameList.size() + 1 )
		{
			throw new IOException( dirPath + " contains too many files." ) ;
		}

		File found = null ;
		for ( File file : files )
		{
			if ( ignoreFileNameList.contains( file.getName() ) ) continue ;

			if ( found != null )
			{
				throw new IOException( dirPath + " contains too many files." ) ;
			}

			found = file ;
		}

		return found ;
	}
}
