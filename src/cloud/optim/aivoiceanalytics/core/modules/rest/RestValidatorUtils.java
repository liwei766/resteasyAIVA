/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：RestValidatorUtils.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest;

import java.math.BigDecimal ;
import java.util.Arrays ;
import java.util.Date ;
import java.util.Iterator ;
import java.util.List ;
import java.util.regex.Pattern ;

import org.apache.commons.lang3.StringUtils ;

import cloud.optim.aivoiceanalytics.core.modules.rest.SortForm.SortElement;
import cloud.optim.aivoiceanalytics.core.modules.validator.ValidatorUtils;

/**
 * REST API 用入力チェック処理
 *
 * @author itsukaha
 */
public class RestValidatorUtils
{
	// -------------------------------------------------------------------------
	// 基本のチェック項目（必須／長さ）
	// -------------------------------------------------------------------------

	/**
	 * 文字列フィールドの必須／長さチェック
	 *
	 * @param name フィールド名
	 * @param value チェックする値
	 *
	 * @param required 必須フラグ
	 * @param minLen 許容する最小文字数（-1 のとき、チェクしない）
	 * @param maxLen 許容する最大文字数（-1 のとき、チェクしない）
	 */
	public static void fieldValidate( String name, String value,
		boolean required, Integer minLen, Integer maxLen )
	{
		if ( ! ValidatorUtils.required( value ) ) // 入力なし
		{
			if ( required )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_REQUIRED, null, name ) ) ;
			}

			return ; // チェック終了
		}

		// ----- 入力あり

		if ( minLen == null ) { minLen = -1 ; }
		if ( maxLen == null ) { maxLen = -1 ; }

		if ( ( minLen >= 0 ) || ( maxLen >= 0 ) )
		{
			if ( ! ValidatorUtils.length( value, minLen, maxLen ) )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_LENGTH, null, name ) ) ;
			}
		}
	}

	/**
	 * 整数フィールドの必須／長さチェック
	 *
	 * @param name フィールド名
	 * @param value チェックする値
	 *
	 * @param required 必須フラグ
	 * @param min 許容する最小値（null のとき、チェックしない）
	 * @param max 許容する最大値（null のとき、チェックしない）
	 */
	public static void fieldValidate( String name, Number value,
		boolean required, Long min, Long max )
	{
		if ( value == null ) // 入力なし
		{
			if ( required )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_REQUIRED, null, name ) ) ;
			}

			return ; // チェックしない
		}

		// ----- 入力あり

		if ( min != null )
		{
			if ( ! ValidatorUtils.min( value, min ) )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_RANGE, null, name ) ) ;
			}
		}

		if ( max != null )
		{
			if ( ! ValidatorUtils.max( value, max ) )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_RANGE, null, name ) ) ;
			}
		}
	}

	/**
	 * 浮動小数点値フィールドの必須／長さチェック
	 *
	 * @param name フィールド名
	 * @param value チェックする値
	 *
	 * @param required 必須フラグ
	 * @param min 許容する最小値（null のとき、チェックしない）
	 * @param max 許容する最大値（null のとき、チェックしない）
	 */
	public static void fieldValidate( String name, Double value,
		boolean required, Double min, Double max )
	{
		if ( value == null ) // 入力なし
		{
			if ( required )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_REQUIRED, null, name ) ) ;
			}

			return ; // チェックしない
		}

		// ----- 入力あり

		if ( min != null )
		{
			if ( ! ValidatorUtils.minDouble( value, min ) )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_RANGE, null, name ) ) ;
			}
		}

		if ( max != null )
		{
			if ( ! ValidatorUtils.maxDouble( value, max ) )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_RANGE, null, name ) ) ;
			}
		}
	}

	/**
	 * 固定小数点値フィールドの必須／長さチェック
	 *
	 * @param name フィールド名
	 * @param value チェックする値
	 *
	 * @param required 必須フラグ
	 * @param min 許容する最小値（null のとき、チェックしない）
	 * @param max 許容する最大値（null のとき、チェックしない）
	 */
	public static void fieldValidate( String name, BigDecimal value,
		boolean required, BigDecimal min, BigDecimal max )
	{
		if ( value == null ) // 入力なし
		{
			if ( required )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_REQUIRED, null, name ) ) ;
			}

			return ; // チェックしない
		}

		// ----- 入力あり

		if ( min != null )
		{
			if ( ! ValidatorUtils.minBigDecimal( value, min ) )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_RANGE, null, name ) ) ;
			}
		}

		if ( max != null )
		{
			if ( ! ValidatorUtils.maxBigDecimal( value, max ) )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_RANGE, null, name ) ) ;
			}
		}
	}

	/**
	 * 日付フィールドの必須／過去未来チェック
	 *
	 * @param name フィールド名
	 * @param value チェックする値
	 *
	 * @param required 必須フラグ
	 * @param basisTime 過去／未来の基準日時（＝現在日時）
	 * @param pastNg 過去の日時を許容しない（null のとき、チェックしない）
	 * @param futureNg 未来の日時を許容しない（null のとき、チェックしない）
	 */
	public static void fieldValidate( String name, Date value,
		boolean required, Date basisTime, Boolean pastNg, Boolean futureNg )
	{
		if ( value == null ) // 入力なし
		{
			if ( required )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_REQUIRED, null, name ) ) ;
			}

			return ; // チェックしない
		}

		if ( pastNg == null ) pastNg = Boolean.FALSE;
		if ( futureNg == null ) futureNg = Boolean.FALSE;

		// ----- 入力あり

		if ( pastNg && ( value != null ) )
		{
			if ( basisTime == null ) basisTime = new Date() ;

			if ( value.before( basisTime ) )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_CONFLICT_RANGE, null, name ) ) ;
			}
		}

		if ( futureNg && ( value != null ) )
		{
			if ( basisTime == null ) basisTime = new Date() ;

			if ( value.after( basisTime ) )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_RANGE, null, name ) ) ;
			}
		}
	}

	/**
	 * 論理値フィールドの必須チェック
	 *
	 * @param name フィールド名
	 * @param value チェックする値
	 *
	 * @param required 必須フラグ
	 * @param min 許容する最小値（使用しない）
	 * @param max 許容する最大値（使用しない）
	 */
	public static void fieldValidate( String name, Boolean value,
		boolean required, Integer min, Integer max )
	{
		if ( value == null ) // 入力なし
		{
			if ( required )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_REQUIRED, null, name ) ) ;
			}

			return ; // チェックしない
		}
	}

	// -------------------------------------------------------------------------
	// 固定の既定値との一致チェック
	// -------------------------------------------------------------------------

	/**
	 * 既定値と一致するかどうかチェック（null は無視）
	 *
	 * @param name フィールド名
	 * @param value チェックする値
	 * @param list 許容値の配列
	 */
	@SafeVarargs
	public static <V, I> void in( String name, V value, I ... list )
	{
		in( name, value, Arrays.asList( list ) ) ;
	}

	/**
	 * 既定値と一致するかどうかチェック（null は無視）
	 *
	 * @param name フィールド名
	 * @param value チェックする値
	 * @param list 許容値リスト
	 */
	public static <V, I> void in( String name, V value, Iterable<I> list )
	{
		// 入力されていなければチェックしない

		if ( ! ValidatorUtils.required( value ) )
		{
			return ;
		}

		// ----- 入力あり

		Iterator<I> ite = list.iterator() ;

		while ( ite.hasNext() )
		{
			I item = ite.next();

			if ( item == null ) continue ;
			if ( String.valueOf( item ).equals( String.valueOf( value ) ) )  return ;
		}

		throw new RestException( new RestResult(
			ResponseCode.INPUT_ERROR_ENUM, null, name ) ) ;
	}

	/**
	 * カンマで区切られた項目全てが既定値と一致するかどうかチェック
	 *
	 * @param name フィールド名
	 * @param value チェックする値（カンマ区切り）
	 * @param list 許容値リスト
	 */
	@SafeVarargs
	public static <I extends Comparable<String>> void allIn( String name, String value, I ... list )
	{
		allIn( name, value, Arrays.asList( list ) ) ;
	}

	/**
	 * カンマで区切られた項目全てが既定値と一致するかどうかチェック
	 *
	 * @param name フィールド名
	 * @param value チェックする値（カンマ区切り）
	 * @param list 許容値リスト
	 */
	public static <I extends Comparable<String>> void allIn( String name, String value, Iterable<I> list )
	{
		if ( value == null ) return ;

		String[] values = value.split( "," ) ;

		for ( String v : values )
		{
			in( name, v, list ) ;
		}
	}

	// -------------------------------------------------------------------------
	// ソートフォームのチェック
	// -------------------------------------------------------------------------

	/**
	 * ソートキーの文字種チェックと補完
	 *
	 * @param form ソート条件
	 */
	public static void sortValidate( SortForm form )
	{
		String name ;
		Long lvalue ;

		// ----- 検索件数

		name = "#sortForm" ;

		if ( form == null )
		{
			throw new NullPointerException( name ) ;
		}

		// ----- 取得条件

		// 取得件数が負数の場合は指定なし（全件取得）とする

		name = "#maxResult" ;
		lvalue = form.getMaxResult() ;

		if ( ( lvalue != null ) && ( lvalue.compareTo( 1L ) < 0 ) )
		{
			lvalue = null ;
			form.setMaxResult( lvalue ) ;
		}

		// ソート項目が指定されていない場合は null を設定しておく

		name = "#sortElement" ;
		List<SortElement> sortElement = form.getSortElement() ;

		if ( sortElement != null )
		{
			Iterator<SortElement> ite = sortElement.iterator() ;

			while( ite.hasNext() )
			{
				SortElement element = ite.next() ;

				if ( ( element == null ) || ( StringUtils.isEmpty( element.getName() ) ) )
				{
					ite.remove() ;
					continue ;
				}

				if ( ! ValidatorUtils.regexp( element.getName(), "^[\\p{Alnum}_\\.]+$" ) )
				{
					throw new RestException( new RestResult(
						ResponseCode.INPUT_ERROR_REGEXP, null, name ) ) ;
				}
			}

			if ( form.getSortElement().size() < 1 )
			{
				form.setSortElement( null ) ;
			}
		}
	}

	/**
	 * ソートキーの項目名をカラム名に変換
	 *
	 * @param form ソート条件（入力チェック済みであること）
	 */
	public static void sortConvert( SortForm form )
	{
		String name ;

		// ----- 検索件数

		name = "#sortForm" ;

		if ( form == null )
		{
			throw new NullPointerException( name ) ;
		}

		// ----- 取得条件

		// ソート項目が指定されていない場合は null を設定しておく

		name = "#sortElement" ;
		List<SortElement> sortElement = form.getSortElement() ;

		if ( sortElement != null )
		{
			for ( SortElement elem : sortElement )
			{
				String tableName = "" ;
				String fieldName = elem.getName() ;

				if ( fieldName.contains( "." ) )
				{
					tableName = fieldName.substring( 0, fieldName.lastIndexOf( "." ) + 1 ) ;
					fieldName = fieldName.substring( fieldName.lastIndexOf( "." ) + 1 ) ;
				}

				StringBuilder sb = new StringBuilder( tableName ) ;

				for ( int i = 0 ; i < fieldName.length() ; i++ )
				{
					CharSequence c = fieldName.subSequence( i, i + 1 ) ;
					if ( Pattern.matches( "[A-Z]", c ) )
					{
						sb.append( "_" ) ;
					}
					sb.append( c ) ;
				}

				elem.setName( sb.toString().toLowerCase() ) ;
			}
		}
	}
}
