/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：ValidatorUtility.java
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.validator;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

/**
 * 入力項目チェックのためのクラス.
 * 全てのチェックメソッドは非 なので、クラスを拡張してカスタマイズ可能です.
 * 入力値が null の場合の判定結果は allowNull 値に依存します.
 *
 */
public class ValidatorUtility
{
	// -------------------------------------------------------------------------
	// プロパティ
	// -------------------------------------------------------------------------

	/**
	 * 必須以外のチェックで null が入力された場合の戻り値.
	 * null をチェックしない（＝許可する）場合は true に設定して下さい.
	 */
	private boolean allowNull = false ;

	/**
	 * allowNull 取得.
	 *
	 * @return allowNull
	 */
	public boolean getAllowNull() { return allowNull ; }

	/**
	 * allowNull 取得.
	 *
	 * @return allowNull
	 */
	public boolean isAllowNull() { return getAllowNull() ; }

	/**
	 * allowNull 設定.
	 *
	 * @param allowNull allowNull に設定する値.
	 */
	public void setAllowNull( boolean allowNull ) { this.allowNull = allowNull ; }

	// -------------------------------------------------------------------------

	/**
	 * 見た目のバイト数チェックで使用するデフォルトエンコーディング.
	 */
	private String defaultEncode = "UTF-8" ;

	/**
	 * defaultEncode 取得.
	 *
	 * @return defaultEncode
	 */
	public String getDefaultEncode()
	{
		return defaultEncode ;
	}

	/**
	 * defaultEncode 設定.
	 *
	 * @param defaultEncode defaultEncode に設定する値.
	 */
	public void setDefaultEncode( String defaultEncode )
	{
		this.defaultEncode = defaultEncode ;
	}

	// -------------------------------------------------------------------------
	// コンストラクタ
	// -------------------------------------------------------------------------

	/**
	 * デフォルトコンストラクタ.
	 */
	public ValidatorUtility() {}

	/**
	 * コンストラクタ.
	 *
	 * @param allowNull allowNull に設定する値
	 * @param defaultEncode defaultEncode に設定する値（null、空白の場合は無視）
	 */
	public ValidatorUtility( boolean allowNull, String defaultEncode )
	{
		this.allowNull = allowNull ;
		if ( ( defaultEncode != null ) && ! defaultEncode.equals( "" ) ) this.defaultEncode = defaultEncode ;
	}

	// サロゲートペア対応のための共通処理
	// -------------------------------------------------------------------------

	/**
	 * サロゲートペアを考慮した文字数取得
	 *
	 * @param str 文字数を取得する文字列
	 *
	 * @return 文字数
	 */
	public int codePointCount( String str )
	{
		return str.codePointCount( 0, str.length() ) ;
	}

	/**
	 * サロゲートペアを考慮した文字配列取得
	 *
	 * @param str 文字配列を取得する文字列
	 *
	 * @return 文字の配列
	 */
	public int[] getInts( String str )
	{
		int[] ret = new int[ codePointCount( str ) ] ;

		int idx = 0 ;
		for ( int i = 0 ; i < str.length() ; )
		{
			int ch = str.codePointAt( i ) ;

			i += Character.charCount( ch );

			ret[ idx++ ] = ch ;
		}

		return ret ;
	}

	// -------------------------------------------------------------------------
	// 必須チェック
	// -------------------------------------------------------------------------

	/**
	 * 必須チェック.<br />
	 * 判定条件：チェック対象が null または空文字列（""）の場合にエラー.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean required( String str )
	{
		if ( ( str == null ) || "".equals( str ) ) return false ;
		return true ;
	}

	/**
	 * 必須チェック.<br />
	 * 判定条件：チェック対象が null の場合にエラー.
	 *
	 * @param obj チェック対象オブジェクト
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean required( Object obj )
	{
		if ( obj == null ) return false ;
		return true ;
	}

	// -------------------------------------------------------------------------
	// 文字数チェック
	// -------------------------------------------------------------------------

	/**
	 * 文字数チェック（固定長）.<br />
	 * 判定条件：チェック対象の文字数が length でない場合にエラー.
	 * 　ただし、空文字列は無条件で OK.
	 *
	 * @param str チェック対象文字列
	 * @param length 許可する文字数
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean length( String str, int length )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		if ( codePointCount( str ) != length ) return false ;

		return true ;
	}

	/**
	 * 文字数チェック（範囲）.<br />
	 * 判定条件：チェック対象の文字数が min 未満または max を超える場合にエラー.
	 * 　ただし、空文字列は無条件で OK.<br />
	 * 　min が負数の場合、最小文字数のチェックは行わない.<br />
	 * 　max が負数の場合、最大文字数のチェックは行わない.
	 *
	 * @param str チェック対象文字列
	 * @param min 許可する最小文字数. 負数の時、最小文字数はチェックしない.
	 * @param max 許可する最大文字数. 負数の時、最大文字数はチェックしない.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean length( String str, int min, int max )
	{
		if ( str == null )	    return allowNull;
		if ( "".equals( str ) ) return true;

		if ( ( min >= 0 ) && ( codePointCount( str ) < min ) ) return false ;
		if ( ( max >= 0 ) && ( codePointCount( str ) > max ) ) return false ;

		return true;
	}

	/**
	 * 最大文字数チェック.<br />
	 * 判定条件：チェック対象の文字数が max を超える場合にエラー.
	 * 　ただし、空文字列は無条件で OK.
	 *
	 * @param str チェック対象文字列
	 * @param max 許可する最大文字数.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean maxlength( String str, int max )
	{
		return length( str, -1, max ) ;
	}

	/**
	 * 最小文字数チェック.<br />
	 * 判定条件：チェック対象の文字数が min 未満の場合にエラー.
	 * 　ただし、空文字列は無条件で OK.
	 *
	 * @param str チェック対象文字列
	 * @param min 許可する最小文字数.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean minlength( String str, int min )
	{
		return length( str, min, -1 ) ;
	}

	// -------------------------------------------------------------------------
	// 見た目のバイト数チェック
	// -------------------------------------------------------------------------

	/**
	 * 見た目のバイト数チェック（主に固定長ファイル向け）.<br />
	 * 判定条件：チェック対象の見た目のバイト数が min 未満または max を超える場合にエラー.
	 * 　ただし、空文字列は無条件で OK.<br />
	 * 　min が負数の場合、最小文字数のチェックは行わない.<br />
	 * 　max が負数の場合、最大文字数のチェックは行わない.<br />
	 * 　主に CSV ファイルがターゲットのため、日本語は Windows-31J として扱う.
	 *
	 * @param str チェック対象文字列
	 * @param min 許可する最小文字数. 負数の時、最小文字数はチェックしない.
	 * @param max 許可する最大文字数. 負数の時、最大文字数はチェックしない.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean bytelength( String str, int min, int max )
	{
		return bytelength( str, min, max, defaultEncode ) ;
	}

	/**
	 * 見た目のバイト数チェック（主に固定長ファイル向け）.<br />
	 * 判定条件：チェック対象の見た目のバイト数が min 未満または max を超える場合にエラー.
	 * 　ただし、空文字列は無条件で OK.<br />
	 * 　min が負数の場合、最小文字数のチェックは行わない.<br />
	 * 　max が負数の場合、最大文字数のチェックは行わない.<br />
	 *	 指定されたエンコード名が無効の場合は、文字数でチェックする.
	 *
	 * @param str	 チェック対象文字列
	 * @param min	 許可する最小文字数. 負数の時、最小文字数はチェックしない.
	 * @param max	 許可する最大文字数. 負数の時、最大文字数はチェックしない.
	 * @param encode チェック対象文字列の文字列エンコード名.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean bytelength(
		String str, int min, int max, String encode )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		int len = 0 ;
		try
		{
			len = str.getBytes( encode ).length ;
		}
		catch ( UnsupportedEncodingException ex )
		{
			len = str.length() ;
		}

		if ( ( min >= 0 ) && ( len < min ) ) return false ;
		if ( ( max >= 0 ) && ( len > max ) ) return false ;

		return true;
	}

	// -------------------------------------------------------------------------
	// 整数値の範囲チェック
	// -------------------------------------------------------------------------

	/**
	 * 整数値の範囲チェック.<br />
	 * 判定条件：チェック対象が以下の何れかに該当する場合にエラー
	 * <ul>
	 * 　<li>数値として解釈できない</li>
	 * 　<li>数値が min 未満または max を超える</li>
	 * </ul>
	 *
	 * @param obj チェック対象
	 * @param min 許可する最小値.
	 * @param max 許可する最大値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean range( Object obj, long min, long max )
	{
		if ( obj == null ) return allowNull ;

		Long val = objToLong( obj ) ;
		if ( val == null ) return false ;

		return ( ( val.longValue() >= min ) && ( val.longValue() <= max ) ) ;
	}

	/**
	 * 整数値の最小値チェック. <br />
	 * 判定条件：チェック対象が以下の何れかに該当する場合にエラー
	 * <ul>
	 * 　<li>数値として解釈できない</li>
	 * 　<li>数値が min 未満</li>
	 * </ul>
	 *
	 * @param obj チェック対象（Long、Integer、Number、文字）
	 * @param min 許可する最小値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean min( Object obj, long min )
	{
		if ( obj == null ) return allowNull ;

		Long val = objToLong( obj ) ;
		if ( val == null ) return false ;

		return ( val.longValue() >= min ) ;
	}

	/**
	 * 整数値の最大値チェック. <br />
	 * 判定条件：チェック対象が以下の何れかに該当する場合にエラー
	 * <ul>
	 * 　<li>数値として解釈できない</li>
	 * 　<li>数値が max を超える</li>
	 * </ul>
	 *
	 * @param obj チェック対象（Long、Integer、Number、文字）
	 * @param max 許可する最大値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean max( Object obj, long max )
	{
		if ( obj == null ) return allowNull ;

		Long val = objToLong( obj ) ;
		if ( val == null ) return false ;

		return ( val.longValue() <= max ) ;
	}

	/**
	 * オブジェクトを Long 型に変換する.
	 * 変換できない（null を含む）場合は null を返す.
	 *
	 * @param obj 変換対象オブジェクト
	 *
	 * @return 数値への変換後の Long オブジェクト.変換できない場合は null.
	 */
	protected Long objToLong( Object obj ) {

		Long val = null ;
		try
		{
			if ( obj instanceof Number ) val = ((Number)obj).longValue() ;
			else val = Long.valueOf( obj.toString() ) ;
		}
		catch ( Exception ex ) {}

		return val ;
	}

	// -------------------------------------------------------------------------
	// 浮動小数点数値の範囲チェック
	// -------------------------------------------------------------------------

	/**
	 * 浮動小数点数値の範囲チェック.<br />
	 * 判定条件：チェック対象が以下の何れかに該当する場合にエラー
	 * <ul>
	 * 　<li>数値として解釈できない</li>
	 * 　<li>数値が min 未満または max を超える</li>
	 * </ul>
	 *
	 * @param obj チェック対象
	 * @param min 許可する最小値.
	 * @param max 許可する最大値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean rangeDouble( Object obj, double min, double max )
	{
		if ( obj == null ) return allowNull ;

		Double val = objToDouble( obj ) ;
		if ( val == null ) return false ;

		return ( ( val.doubleValue() >= min ) && ( val.doubleValue() <= max ) ) ;
	}

	/**
	 * 浮動小数点数値の最小値チェック. <br />
	 * 判定条件：チェック対象が以下の何れかに該当する場合にエラー
	 * <ul>
	 * 　<li>数値として解釈できない</li>
	 * 　<li>数値が min 未満</li>
	 * </ul>
	 *
	 * @param obj チェック対象（Long、Integer、Number、文字）
	 * @param min 許可する最小値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean minDouble( Object obj, double min )
	{
		if ( obj == null ) return allowNull ;

		Double val = objToDouble( obj ) ;
		if ( val == null ) return false ;

		return ( val.doubleValue() >= min ) ;
	}

	/**
	 * 浮動小数点数値の最大値チェック. <br />
	 * 判定条件：チェック対象が以下の何れかに該当する場合にエラー
	 * <ul>
	 * 　<li>数値として解釈できない</li>
	 * 　<li>数値が max を超える</li>
	 * </ul>
	 *
	 * @param obj チェック対象（Long、Integer、Number、文字）
	 * @param max 許可する最大値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean maxDouble( Object obj, double max )
	{
		if ( obj == null ) return allowNull ;

		Double val = objToDouble( obj ) ;
		if ( val == null ) return false ;

		return ( val.doubleValue() <= max ) ;
	}

	/**
	 * オブジェクトを Long 型に変換する.
	 * 変換できない（null を含む）場合は null を返す.
	 *
	 * @param obj 変換対象オブジェクト
	 *
	 * @return 数値への変換後の Long オブジェクト.変換できない場合は null.
	 */
	protected Double objToDouble( Object obj ) {

		Double val = null ;
		try
		{
			if ( obj instanceof Number ) val = ((Number)obj).doubleValue() ;
			else val = Double.valueOf( obj.toString() ) ;
		}
		catch ( Exception ex ) {}

		return val ;
	}

	// -------------------------------------------------------------------------
	// 固定小数点数値の範囲チェック
	// -------------------------------------------------------------------------

	/**
	 * 固定小数点数値の範囲チェック.<br />
	 * 判定条件：チェック対象が以下の何れかに該当する場合にエラー
	 * <ul>
	 * 　<li>数値として解釈できない</li>
	 * 　<li>数値が min 未満または max を超える</li>
	 * </ul>
	 *
	 * @param obj チェック対象
	 * @param min 許可する最小値.
	 * @param max 許可する最大値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean rangeBigDecimal( Object obj, BigDecimal min, BigDecimal max )
	{
		if ( obj == null ) return allowNull ;

		BigDecimal val = objToBigDecimal( obj ) ;
		if ( val == null ) return false ;

		return ( ( val.compareTo( min ) >= 0 ) && ( val.compareTo( max ) <= 0 ) ) ;
	}

	/**
	 * 固定小数点数値の最小値チェック. <br />
	 * 判定条件：チェック対象が以下の何れかに該当する場合にエラー
	 * <ul>
	 * 　<li>数値として解釈できない</li>
	 * 　<li>数値が min 未満</li>
	 * </ul>
	 *
	 * @param obj チェック対象（Long、Integer、Number、文字）
	 * @param min 許可する最小値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean minBigDecimal( Object obj, BigDecimal min )
	{
		if ( obj == null ) return allowNull ;

		BigDecimal val = objToBigDecimal( obj ) ;
		if ( val == null ) return false ;

		return ( val.compareTo( min ) >= 0 ) ;
	}

	/**
	 * 固定小数点数値の最大値チェック. <br />
	 * 判定条件：チェック対象が以下の何れかに該当する場合にエラー
	 * <ul>
	 * 　<li>数値として解釈できない</li>
	 * 　<li>数値が max を超える</li>
	 * </ul>
	 *
	 * @param obj チェック対象（Long、Integer、Number、文字）
	 * @param max 許可する最大値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean maxBigDecimal( Object obj, BigDecimal max )
	{
		if ( obj == null ) return allowNull ;

		BigDecimal val = objToBigDecimal( obj ) ;
		if ( val == null ) return false ;

		return ( val.compareTo( max ) <= 0 ) ;
	}

	/**
	 * オブジェクトを BigDecimal 型に変換する.
	 * 変換できない（null を含む）場合は null を返す.
	 *
	 * @param obj 変換対象オブジェクト
	 *
	 * @return 数値への変換後の Long オブジェクト.変換できない場合は null.
	 */
	protected BigDecimal objToBigDecimal( Object obj ) {

		BigDecimal val = null ;
		try
		{
			if ( obj instanceof BigDecimal )
			{
				val = (BigDecimal)obj ;
			}
			else if ( obj instanceof Number )
			{
				val = BigDecimal.valueOf( objToDouble( obj ) ) ;
			}
			else
			{
				val = new BigDecimal( obj.toString() ) ;
			}
		}
		catch ( Exception ex ) {}

		return val ;
	}

	// -------------------------------------------------------------------------
	// 文字種チェック
	// -------------------------------------------------------------------------

	/** チェックパターン：半角数字 */
	protected Pattern PATTERN_NUMERIC = Pattern.compile( "^[0-9]+$" ) ;

	/**
	 * 半角数字チェック.<br />
	 * 判定条件：半角数字でない文字が含まれる場合にエラー.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean numeric( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		return PATTERN_NUMERIC.matcher( str ).matches() ;
	}

	/** チェックパターン：半角整数値（数字、正負符号を許可） */
	protected Pattern PATTERN_NUMBER_INTEGER = Pattern.compile( "^[\\+\\-]?[0-9]+$" ) ;

	/**
	 * 半角整数値チェック.<br />
	 * 判定条件：半角整数値でない文字が含まれる場合にエラー（数字、正負符号を許可）.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean numberInt( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		return PATTERN_NUMBER_INTEGER.matcher( str ).matches() ;
	}

	/** チェックパターン：半角数値(数字、正負符号、小数点符号を許可) */
	protected Pattern PATTERN_NUMBER = Pattern.compile( "^[\\+\\-]?[0-9]+(\\.[0-9]+)?$" ) ;

	/**
	 * 半角数値チェック.<br />
	 * 判定条件：半角数値(数字、正負符号、小数点符号を許可)でない文字が含まれる場合にエラー.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean number( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		return PATTERN_NUMBER.matcher( str ).matches() ;
	}

	/** チェックパターン：半角英数字 */
	protected Pattern PATTERN_ALPHA_NUMERIC = Pattern.compile( "^[0-9A-Za-z]+$" ) ;

	/**
	 * 半角英数字チェック.<br />
	 * 判定条件：半角英数字でない文字が含まれる場合にエラー.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean alphaNumeric( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		return PATTERN_ALPHA_NUMERIC.matcher( str ).matches() ;
	}

	/** チェックパターン：ASCII */
	protected Pattern PATTERN_ASCII = Pattern.compile( "^[ -~]+$" ) ;

	/**
	 * 半角英数記号チェック.<br />
	 * 判定条件：半角英数記号でない文字が含まれる場合にエラー.
	 *
	 * @param c チェック対象文字
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean ascii( char c )
	{
		return c >= ' ' && c <= '~' ;
	}

	/**
	 * 半角英数記号チェック.<br />
	 * 判定条件：半角英数記号でない文字が含まれる場合にエラー.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean ascii( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		return PATTERN_ASCII.matcher( str ).matches() ;
	}

	/** チェックパターン：半角カナ */
	protected Pattern PATTERN_HANKAKU_KANA = Pattern.compile( "^[ｦ-ﾟ]+$" ) ;

	/**
	 * 半角カタカナチェック.<br />
	 * 判定条件：半角カタカナでない文字が含まれる場合にエラー.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean hankakuKana( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		return PATTERN_HANKAKU_KANA.matcher( str ).matches() ;
	}

	/**
	 * すべての半角チェック.<br />
	 * 判定条件：半角でない文字の場合にエラー.
	 *
	 * @param c チェック対象文字
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean hankakuAll( char c )
	{
		if ( ( 0x0020 <= c ) && ( c <= 0x007E ) ) return true ;
		if ( ( 0xFF61 <= c ) && ( c <= 0xFF9F ) ) return true ;

		return false ;
	}

	/**
	 * すべての半角チェック.<br />
	 * 判定条件：半角でない文字が含まれる場合にエラー.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean hankakuAll( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		for ( int i = 0; i < str.length(); i++ ) {

			if ( ! hankakuAll( str.charAt( i ) ) ) return false ;
		}

		return true ;
	}

	/** チェックパターン：全角カナ */
	protected Pattern PATTERN_KATAKANA = Pattern.compile( "^[ァ-ヶー]+$" ) ;

	/**
	 * 全角カタカナチェック.<br />
	 * 判定条件：全角カタカナでない文字が含まれる場合にエラー.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean katakana( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		return PATTERN_KATAKANA.matcher( str ).matches() ;
	}

	/** チェックパターン：全角かな */
	protected Pattern PATTERN_HIRAKANA = Pattern.compile( "^[ぁ-んー]+$" ) ;

	/**
	 * 全角ひらがなチェック.<br />
	 * 判定条件：全角ひらがなでない文字が含まれる場合にエラー.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean hirakana( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		return PATTERN_HIRAKANA.matcher( str ).matches() ;
	}

	/**
	 * 指定された文字が全角かどうかチェックする.<br />
	 * 全角文字コードとして CP932 を使用する.詳細については以下の URL を参照.
	 * http://www.unicode.org/Public/MAPPINGS/VENDORS/MICSFT/WINDOWS/CP932.TXT
	 *
	 * @param c チェック対象文字
	 *
	 * @return true : 全角文字	false : 全角文字でない
	 */
	public boolean zenkakuAll( char c )
	{
		if ( c <= 0x007F ) return false ;
		if ( ( 0xFF61 <= c ) && ( c <= 0xFF9F ) ) return false ;
		return true ;
	}

	/**
	 * 全角チェック.<br />
	 * 判定条件：全角でない文字が含まれる場合にエラー.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean zenkakuAll( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		for ( int i = 0 ; i < str.length() ; i++ )
		{
			if ( ! zenkakuAll( str.charAt( i ) ) ) return false ;
		}
		return true;
	}

	/**
	 * 全角および改行コードチェック.<br />
	 * 判定条件：全角、改行コードのどちらでもでない文字が含まれる場合にエラー.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean zenkakuAndNewLine( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		for ( int i = 0 ; i < str.length() ; i++ )
		{
			char c = str.charAt( i ) ;

			if ( c == 0x000A ) continue ;
			if ( c == 0x000D ) continue ;
			if ( zenkakuAll( c ) ) continue ;

			return false ;
		}
		return true ;
	}

	/**
	 * 全角、半角および改行コードチェック.<br />
	 * 判定条件：全角、半角、改行コードのいずれでもでない文字が含まれる場合にエラー.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean zenkakuHankakuAndNewLine( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		for ( int i = 0; i < str.length(); i++ )
		{
			char c = str.charAt( i ) ;

			if ( c == 0x000A ) continue ;
			if ( c == 0x000D ) continue ;
			if ( zenkakuAll( c ) ) continue ;
			if ( hankakuAll( c ) ) continue ;

			return false ;
		}
		return true ;
	}

	/**
	 * 指定された文字が JIS0208 範囲内であるかチェックする.
	 *
	 * @param c チェック対象の文字
	 * @param encoder JIS0208 エンコーダ
	 *
	 * @return true : JIS0208 範囲内の文字	false : 範囲外の文字
	 *
	 * @see href http://d.hatena.ne.jp/Kazuhira/20140329/1396114212
	 * @see href http://docs.oracle.com/javase/jp/7/technotes/guides/intl/encoding.doc.html
	 * @see href http://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html
	 */
	protected boolean jis0208( char c, CharsetEncoder encoder )
	{
		if ( ascii( c ) ) return true;

		// 以下の文字はチェックしない
		// ～：FF5E FULLWIDTH TILDE
		// －：FF0D FULLWIDTH HYPHEN-MINUS
		if ( ( c != 0xFF5E ) && ( c != 0xFF0D ) )
		{
			if ( ! encoder.canEncode( c ) )
			{
				return false ;
			}
		}

		return true ;
	}

	/**
	 * 指定された文字が JIS0208 範囲内であるかチェックする.
	 *
	 * @param c チェック対象の文字
	 *
	 * @return true : JIS0208 範囲内の文字	false : 範囲外の文字
	 *
	 * @see href http://d.hatena.ne.jp/Kazuhira/20140329/1396114212
	 * @see href http://docs.oracle.com/javase/jp/7/technotes/guides/intl/encoding.doc.html
	 * @see href http://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html
	 */
	public boolean jis0208( char c )
	{
		return jis0208( c, Charset.forName( "x-JIS0208" ).newEncoder() );
	}

	/**
	 * JIS0208 範囲チェック.<br />
	 * 判定条件：JIS0208 範囲外の文字が含まれる場合にエラー.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean jis0208( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		CharsetEncoder encoder = Charset.forName( "x-JIS0208" ).newEncoder();

		for ( int i = 0 ; i < str.length() ; i++ )
		{
			if ( ! jis0208( str.charAt( i ), encoder ) ) return false ;
		}
		return true ;
	}

	// -------------------------------------------------------------------------
	// 特定フォーマットのチェック
	// -------------------------------------------------------------------------

	// -------------------- URL --------------------

	/** チェックパターン：URL */
	protected Pattern PATTERN_URL = Pattern.compile( "https?:\\/\\/.*" ) ;

	/** チェックパターン：URL */
	/*
		日本語ドメインを考慮しなくてよい場合はこちらのパターンを使用できます。

		RFC-1738 より：
			英数文字(a-z-A-Z0-9)
			予約文字(;/?:,@=&)
			特殊文字($-_.+!*'(),)
			符号化すべき文字(空白<>"#%{}|\^~[]`) ← ~%#のみ許可

	protected Pattern PATTERN_URL = Pattern.compile( "https?:\\/\\/[-_.!~*'()a-zA-Z0-9;/?:@&=+$,%#]+" ) ;
	*/

	/**
	 * URLチェック.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean url( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		return PATTERN_URL.matcher( str ).matches() ;
	}

	// -------------------- 電話番号 --------------------

	/** チェックパターン：電話番号（12 桁） */
	protected Pattern PATTERN_TELEPHONE1 = Pattern.compile( "^\\d{2,5}-\\d{1,4}-\\d{4}$" ) ;

	/** チェックパターン：電話番号（11 桁） */
	protected Pattern PATTERN_TELEPHONE2 = Pattern.compile( "^\\d{6}-\\d{4}$" ) ;

	/** チェックパターン：電話番号（11 桁） */
	protected Pattern PATTERN_TELEPHONE3 = Pattern.compile( "^\\d{4}-\\d{1}-\\d{4}$" ) ;

	/** チェックパターン：電話番号（13 桁） */
	protected Pattern PATTERN_TELEPHONE4 = Pattern.compile( "^0[5]0-\\d{4}-\\d{4}$" ) ;

	/** チェックパターン：電話番号（12 桁 フリーダイヤル） */
	protected Pattern PATTERN_TELEPHONE5 = Pattern.compile( "^\\d{4}-\\d{3}-\\d{3}$" ) ;

	/** チェックパターン：携帯電話番号 */
	protected Pattern PATTERN_CELLULARPHONE = Pattern.compile( "^0[7-9]0-\\d{4}-\\d{4}$" ) ;

	/**
	 * 電話番号チェック.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean telephone( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		if ( ! "0".equals( str.substring( 0, 1 ) ) ) return false ;

		switch ( str.length() )
		{
		case 12 :

			return
				PATTERN_TELEPHONE1.matcher( str ).matches() ||
				PATTERN_TELEPHONE5.matcher( str ).matches() ;

		case 11 :

			return
				PATTERN_TELEPHONE2.matcher( str ).matches() ||
				PATTERN_TELEPHONE3.matcher( str ).matches() ;

		case 13 :

			return
				PATTERN_TELEPHONE4.matcher( str ).matches() ||
				PATTERN_CELLULARPHONE.matcher( str ).matches() ;

		default :

			return false ;
		}
	}

	// -------------------- 郵便番号 --------------------

	/** 桁数：郵便番号（左） */
	protected int ZIP_HIGH_LENGTH = 3 ;

	/** 桁数：郵便番号（右） */
	protected int ZIP_LOW_LENGTH = 4 ;

	/**
	 * 郵便番号チェック（3 桁と 4 桁を個別指定）.
	 *
	 * @param first チェック対象文字列（前半 3 桁分）
	 * @param last	チェック対象文字列（後半 4 桁分）
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean zip( String first, String last )
	{
		if ( ( first == null ) && ( last == null ) ) return allowNull ;

		// 片方だけ null は NG

		if ( first == null ) return false ;
		if ( last  == null ) return false ;

		if ( "".equals( first ) && "".equals( last ) ) return true ;

		// 片方だけ空白は NG

		if ( "".equals( first ) ) return false ;
		if ( "".equals( last  ) ) return false ;

		if ( first.length() != ZIP_HIGH_LENGTH ) return false;
		if ( last.length()	!= ZIP_LOW_LENGTH ) return false;

		return true;
	}

	/** チェックパターン：郵便番号 */
	protected Pattern PATTERN_ZIP = Pattern.compile( "\\d{3}-\\d{4}" ) ;

	/**
	 * 郵便番号チェック（ハイフン込みでまとめて指定）.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean zip( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		return PATTERN_ZIP.matcher( str ).matches() ;
	}

	// -------------------- クレジットカード番号 --------------------

	/** チェックパターン：クレジットカード番号 */
	protected Pattern[] PATTERN_CREDITCARD = new Pattern[]
	{
		  Pattern.compile( "^35\\d{14}$" )	// JCB
		, Pattern.compile( "^36\\d{12}$" )	// DINERS
		, Pattern.compile( "^37\\d{13}$" )	// AMEX
		, Pattern.compile( "^4\\d{15}$" )	// VISA
		, Pattern.compile( "^5\\d{15}$" )	// MASTER
	} ;

	/**
	 * クレジットカード番号チェック.<br />
	 * 【注意】<br />
	 * 　日本国内で一般的なチェックを行います.<br />
	 * 　プロジェクトごとに仕様を確認してください.<br />
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean creditCard( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		for ( Pattern pattern : PATTERN_CREDITCARD )
		{
			if ( pattern.matcher( str ).matches() ) return true ;
		}

		return false;
	}

	// -------------------- メールアドレス --------------------

	/** 最大文字数：メールアドレス（ローカル名）. ( RFC2822 ) */
	protected int MAXLEN_EMAIL_LOCAL = 64;

	/** チェックパターン：メールアドレス（ローカル名） */
	protected Pattern PATTERN_EMAIL_LOCAL = Pattern
			.compile( "^([a-zA-Z0-9_-][a-zA-Z0-9_\\.-]{0,62}[a-zA-Z0-9_-])$" );

	/** 最大文字数：メールアドレス（ドメイン）. ( RFC2822 ) */
	protected int MAXLEN_EMAIL_DOMAIN = 255;

	/** チェックパターン：メールアドレス（ドメイン） */
	protected Pattern PATTERN_EMAIL_DOMAIN = Pattern
			.compile( "^([a-zA-Z0-9][a-zA-Z0-9_\\.-]{0,253}[a-zA-Z0-9])$" );

	/**
	 * 最大文字数：メールアドレス.
	 * localname@domain : localname + '@' + domain
	 */
	protected int MAXLEN_EMAIL = MAXLEN_EMAIL_LOCAL + 1 + MAXLEN_EMAIL_DOMAIN ;

	/** 携帯メールアドレスのドメイン名（未使用） */
	protected final String[] MOBILE_DOMAINS = {

		"docomo.ne.jp",

		"ezweb.ne.jp",

		"c.vodafone.ne.jp", "d.vodafone.ne.jp", "h.vodafone.ne.jp",
		"k.vodafone.ne.jp", "n.vodafone.ne.jp", "q.vodafone.ne.jp",
		"r.vodafone.ne.jp", "s.vodafone.ne.jp", "t.vodafone.ne.jp",

		"jp-c.ne.jp", "jp-d.ne.jp", "jp-h.ne.jp", "jp-k.ne.jp",
		"jp-n.ne.jp", "jp-q.ne.jp", "jp-r.ne.jp", "jp-s.ne.jp",
		"jp-t.ne.jp",

		"ido.ne.jp",
		"sky.tu-ka.ne.jp", "sky.tkk.ne.jp", "sky.tkc.ne.jp"
	};

	/**
	 * メールアドレスチェック.<br />
	 * 【注意】<br />
	 * 　比較的一般的なチェックを行います.<br />
	 * 　プロジェクトごとに仕様を確認してください.<br />
	 *
	 * @param str メールアドレス
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean email( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		// ----- 全体のチェック -----

		// 長さチェック
		if ( maxlength( str, MAXLEN_EMAIL ) == false ) return false ;

		// '@'は一個だけ
		String token[] = str.split( "@" ) ;
		if ( token.length != 2 ) return false ;

		String local = token[0] ;
		String domain = token[1] ;

		// ----- ローカル名のチェック -----

		// 長さチェック
		if ( ! length( local, 1, MAXLEN_EMAIL_LOCAL ) ) return false ;

		// パターンチェック
		if ( ! PATTERN_EMAIL_LOCAL.matcher( local ).matches() ) return false ;

		// ピリオドは連続しない
		if ( local.contains( ".." ) ) return false ;

		// ----- ドメインのチェック -----

		// 長さチェック
		if ( ! length( domain, 1, MAXLEN_EMAIL_DOMAIN ) ) return false ;

		// パターンチェック
		if ( ! PATTERN_EMAIL_DOMAIN.matcher( domain ).matches()) return false ;

		// '.'は一個以上
		if ( ! domain.contains( "." ) ) return false ;

		return true;
	}

	// -------------------- 日付 --------------------

	/**
	 * 日付の実在チェック（年月日をそれぞれ整数で指定）.
	 *
	 * @param year	西暦年
	 * @param month 月（1～）
	 * @param day	日
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean date( int year, int month, int day )
	{
		// GregorianCalendarを利用して行う
		GregorianCalendar calendar = new GregorianCalendar() ;

		// 厳密な日付チェックを行う
		calendar.setLenient( false ) ;

		try {

			calendar.set( year, month - 1, day ) ;
			calendar.getTime() ;

			return true ;

		} catch ( IllegalArgumentException e ) { return false; }
	}

	/**
	 * 日時の実在チェック（文字列指定）.
	 *
	 * @param str	  チェック対象文字列
	 * @param format 想定する書式（DateFormat で扱える形式：yyyy/MM/dd_hh:mm:ss など）
	 *
	 * @return true : OK  false : エラーあり
	 *
	 * @see DateFormat#parse( String )
	 */
	public boolean date( String str ,String format )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		DateFormat df = new SimpleDateFormat( format ) ;

		// 厳密な日付チェックを行う
		df.setLenient( false ) ;

		try
		{
			df.parse( str ) ;
			return true ;
		}
		catch ( Exception ex )
		{
			return false ;
		}
	}

	// -------------------------------------------------------------------------
	// その他
	// -------------------------------------------------------------------------

	/**
	 * 正規表現との一致チェック.<br />
	 * 判定条件：正規表現にマッチしない場合にエラー.<br />
	 *
	 * @param str	  チェック対象文字列
	 * @param pattern 正規表現
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean regexp( String str, String pattern )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		return str.matches( pattern ) ;
	}

	/**
	 * 文字列としての同値チェック（2 オブジェクトの文字列表現を比較）.<br />
	 * 判定条件：2 つのオブジェクトの文字列表現が異なる場合にエラー.<br />
	 * 2 つのオブジェクトを toString() によって文字列表現に変換し、
	 * その結果を String#equals により比較します.<br />
	 * 例：
	 * <ul>
	 * <li>null 同士は「同じ」と判定されます.</li>
	 * <li>通常の equals() で同じと判定されるものは「同じ」と判定されます.</li>
	 * <li>Integer の 1 と Long の 1 も「同じ」と判定されます.</li>
	 * <li>さらに String の "1" と数値の 1 も「同じ」です.</li>
	 * </ul>
	 *
	 * @param str	比較対象オブジェクト１
	 * @param other 比較対象オブジェクト２
	 *
	 * @return true : OK（同値と判定）	false : エラーあり（同値でない）
	 */
	public boolean equals( Object str, Object other )
	{
		if ( str == other ) return true ;

		if ( str == null ) return false ;//( other == null ) ;
		if ( other == null ) return false ;

		return str.toString().equals( other.toString() ) ;
	}

	/** チェックパターン：半角英文字を含む */
	protected Pattern PATTERN_INCLUDE_ALPHA = Pattern.compile(".*[A-Za-z]+.*");

	/** チェックパターン：半角数字を含む */
	protected Pattern PATTERN_INCLUDE_NUMBER = Pattern.compile(".*[0-9]+.*");

	/**
	 * 半角英文字と半角数字が両方含まれているかチェック.<br />
	 * 判定条件：半角英文字と半角数字のどちらかが含まれない場合にエラー.<br />
	 * 乱数を元にパスワード等を生成するケースを想定.
	 * 空文字列、null の扱いが他と異なるので注意.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public boolean includeAlphaNumeric( String str )
	{
		if ( str == null )	    return allowNull ;
		if ( "".equals( str ) ) return true ;

		return
			PATTERN_INCLUDE_NUMBER.matcher( str ).matches() &&
			PATTERN_INCLUDE_ALPHA.matcher( str ).matches() ;
	}
}
