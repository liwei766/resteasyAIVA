/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：ValidatorUtils.java
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.validator;

import java.math.BigDecimal ;

/**
 * 入力項目チェックのためのクラス.
 * 全てのチェックメソッドは static です.
 * 実際のチェック処理は入力チェック用のオブジェクトで行うので、
 * このオブジェクトを差し替えることでチェック内容を変更可能です.
 *
 */
public class ValidatorUtils {

	/**
	 * 必須以外のチェックで null が入力された場合の戻り値.
	 * null をチェックする（＝許可しない）場合は false に変更して下さい.
	 */
	private static boolean allowNull = true ;

	/**
	 * 見た目のバイト数チェックで使用するデフォルトエンコーディング.
	 */
	private static String defaultEncode = "Windows-31J" ;

	/**
	 * 入力チェック用オブジェクト
	 */
	protected static ValidatorUtility util = new ValidatorUtility( allowNull, defaultEncode ) ;

	/**
	 * サロゲートペアを考慮した文字数取得
	 *
	 * @param str 文字数を取得する文字列
	 *
	 * @return 文字数
	 */
	public int codePointCount( String str )
	{
		return util.codePointCount( str ) ;
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
		return util.getInts( str ) ;
	}

	// -------------------- 必須チェック --------------------

	/**
	 * 必須チェック.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean required( String str )
	{
		return util.required( str ) ;
	}

	/**
	 * 必須チェック.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean required( Object str )
	{
		return util.required( str ) ;
	}

	// -------------------- 文字数チェック --------------------

	/**
	 * 文字数チェック（固定長）.
	 *
	 * @param str チェック対象文字列
	 * @param length 許可する文字数
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean length( String str, int length )
	{
		return util.length( str, length ) ;
	}

	/**
	 * 文字数チェック（範囲）.
	 *
	 * @param str チェック対象文字列
	 * @param min 許可する最小文字数. 負数の時、最小文字数はチェックしない.
	 * @param max 許可する最大文字数. 負数の時、最大文字数はチェックしない.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean length( String str, int min, int max )
	{
		return util.length( str, min, max ) ;
	}

	/**
	 * 最大文字数チェック.
	 *
	 * @param str チェック対象文字列
	 * @param max 許可する最大文字数.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean maxlength( String str, int max )
	{
		return util.maxlength( str, max ) ;
	}

	/**
	 * 最小文字数チェック.
	 *
	 * @param str チェック対象文字列
	 * @param min 許可する最小文字数.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean minlength( String str, int min )
	{
		return util.minlength( str, min ) ;
	}

	/**
	 * 見た目のバイト数チェック（主に固定長ファイル向け）.<br />
	 * 日本語は defaultEncode として扱う.
	 *
	 * @param str チェック対象文字列
	 * @param min 許可する最小文字数. 負数の時、最小文字数はチェックしない.
	 * @param max 許可する最大文字数. 負数の時、最大文字数はチェックしない.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean bytelength( String str, int min, int max )
	{
		return util.bytelength( str, min, max ) ;
	}

	/**
	 * 見た目のバイト数チェック（主に固定長ファイル向け）.
	 *
	 * @param str チェック対象文字列
	 * @param min 許可する最小文字数. 負数の時、最小文字数はチェックしない.
	 * @param max 許可する最大文字数. 負数の時、最大文字数はチェックしない.
	 * @param encode チェック対象文字列のエンコード名.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean bytelength( String str, int min, int max, String encode )
	{
		return util.bytelength( str, min, max, encode ) ;
	}

	// -------------------- 整数値の範囲チェック --------------------

	/**
	 * 整数値の範囲チェック.
	 *
	 * @param obj チェック対象
	 * @param min 許可する最小値.
	 * @param max 許可する最大値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean range( Object obj, long min, long max )
	{
		return util.range( obj, min, max ) ;
	}

	/**
	 * 整数値の最小値チェック.
	 *
	 * @param obj チェック対象（Long、Integer、Number、文字）
	 * @param min 許可する最小値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean min( Object obj, long min )
	{
		return util.min( obj, min ) ;
	}

	/**
	 * 整数値の最大値チェック.
	 *
	 * @param obj チェック対象（Long、Integer、Number、文字）
	 * @param max 許可する最大値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean max( Object obj, long max )
	{
		return util.max( obj, max ) ;
	}

	// -------------------- 浮動小数点数値の範囲チェック --------------------

	/**
	 * 浮動小数点数値の範囲チェック.
	 *
	 * @param obj チェック対象
	 * @param min 許可する最小値.
	 * @param max 許可する最大値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean rangeDouble( Object obj, double min, double max )
	{
		return util.rangeDouble( obj, min, max ) ;
	}

	/**
	 * 浮動小数点数値の最小値チェック.
	 *
	 * @param obj チェック対象（Long、Integer、Number、文字）
	 * @param min 許可する最小値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean minDouble( Object obj, double min )
	{
		return util.minDouble( obj, min ) ;
	}

	/**
	 * 浮動小数点数値の最大値チェック.
	 *
	 * @param obj チェック対象（Long、Integer、Number、文字）
	 * @param max 許可する最大値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean maxDouble( Object obj, double max )
	{
		return util.maxDouble( obj, max ) ;
	}

	// -------------------- 固定小数点数値の範囲チェック --------------------

	/**
	 * 固定小数点数値の範囲チェック.
	 *
	 * @param obj チェック対象
	 * @param min 許可する最小値.
	 * @param max 許可する最大値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean rangeBigDecimal( Object obj, BigDecimal min, BigDecimal max )
	{
		return util.rangeBigDecimal( obj, min, max ) ;
	}

	/**
	 * 固定小数点数値の最小値チェック.
	 *
	 * @param obj チェック対象（Long、Integer、Number、文字）
	 * @param min 許可する最小値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean minBigDecimal( Object obj, BigDecimal min )
	{
		return util.minBigDecimal( obj, min ) ;
	}

	/**
	 * 固定小数点数値の最大値チェック.
	 *
	 * @param obj チェック対象（Long、Integer、Number、文字）
	 * @param max 許可する最大値.
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean maxBigDecimal( Object obj, BigDecimal max )
	{
		return util.maxBigDecimal( obj, max ) ;
	}

	// -------------------- 文字種チェック --------------------

	/**
	 * 半角数字チェック.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean numeric( String str )
	{
		return util.numeric( str ) ;
	}

	/**
	 * 半角整数値チェック（符号 OK）.<br />
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean numberInt( String str )
	{
		return util.numberInt( str ) ;
	}

	/**
	 * 半角数値チェック（符号・小数点 OK）.<br />
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean number( String str )
	{
		return util.number( str ) ;
	}

	/**
	 * 半角英数字チェック.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean alphaNumeric( String str )
	{
		return util.alphaNumeric( str ) ;
	}

	/**
	 * 半角英数記号チェック.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean ascii( String str )
	{
		return util.ascii( str ) ;
	}

	/**
	 * 半角カタカナチェック.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean hankakuKana( String str )
	{
		return util.hankakuKana( str ) ;
	}

	/**
	 * すべての半角チェック.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean hankakuAll( String str )
	{
		return util.hankakuAll( str ) ;
	}

	/**
	 * 全角カタカナチェック.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean katakana( String str )
	{
		return util.katakana( str ) ;
	}

	/**
	 * 全角ひらがなチェック.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean hirakana( String str )
	{
		return util.hirakana( str ) ;
	}

	/**
	 * 全角チェック.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean zenkakuAll( String str )
	{
		return util.zenkakuAll( str ) ;
	}

	/**
	 * 全角および改行コードチェック.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean zenkakuAndNewLine( String str )
	{
		return util.zenkakuAndNewLine( str ) ;
	}

	/**
	 * 全角、半角および改行コードチェック.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean zenkakuHankakuAndNewLine( String str )
	{
		return util.zenkakuHankakuAndNewLine( str ) ;
	}

	/**
	 * JIS0208 範囲チェック.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean jis0208( String str )
	{
		return util.jis0208( str ) ;
	}

	// -------------------- 特定フォーマットのチェック --------------------

	/**
	 * URLチェック.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean url( String str )
	{
		return util.url( str ) ;
	}

	/**
	 * 電話番号チェック.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean telephone( String str )
	{
		return util.telephone( str ) ;
	}

	/**
	 * 郵便番号チェック（3 桁と 4 桁を個別指定）.
	 *
	 * @param first チェック対象文字列（前半 3 桁分）
	 * @param last  チェック対象文字列（後半 4 桁分）
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean zip( String first, String last )
	{
		return util.zip( first, last ) ;
	}

	/**
	 * 郵便番号チェック（ハイフン込みでまとめて指定）.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean zip( String str )
	{
		return util.zip( str ) ;
	}

	/**
	 * クレジットカード番号チェック.<br />
	 * 【注意】<br />
	 * 　日本国内で一般的なチェックを行います.<br />
	 * 　プロジェクトごとに仕様を確認してください.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean creditCard( String str )
	{
		return util.creditCard( str ) ;
	}

	/**
	 * メールアドレスチェック.<br />
	 * 【注意】<br />
	 * 　比較的一般的なチェックを行います.<br />
	 * 　プロジェクトごとに仕様を確認してください.
	 *
	 * @param str メールアドレス
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean email( String str )
	{
		return util.email( str ) ;
	}

	// -------------------- 日付チェック --------------------

	/**
	 * 日付の実在チェック（年月日をそれぞれ整数で指定）.
	 *
	 * @param year	西暦年
	 * @param month 月（1～）
	 * @param day	日
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean date( int year, int month, int day )
	{
		return util.date( year, month, day ) ;
	}

	/**
	 * 日時の実在チェック（文字列指定）.
	 *
	 * @param str	  チェック対象文字列
	 * @param format 想定する書式（DateFormat で扱える形式：yyyy/MM/dd_hh:mm:ss など）
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean date( String str, String format )
	{
		return util.date( str, format ) ;
	}

	// -------------------- その他チェック --------------------

	/**
	 * 正規表現との一致チェック.
	 *
	 * @param str	   チェック対象文字列
	 * @param pattern 正規表現
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean regexp( String str, String pattern )
	{
		return util.regexp( str, pattern ) ;
	}

	/**
	 * 文字列としての同値チェック（2 オブジェクトの文字列表現を比較）.
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
	public static boolean equals( Object str, Object other )
	{
		return util.equals( str, other ) ;
	}

	/**
	 * 半角英文字と半角数字が両方含まれているかチェック.
	 * 判定条件：半角英文字と半角数字のどちらかが含まれない場合にエラー.
	 * 乱数を元にパスワード等を生成するケースを想定.
	 * 空文字列、null の扱いが他と異なるので注意.
	 *
	 * @param str チェック対象文字列
	 *
	 * @return true : OK  false : エラーあり
	 */
	public static boolean includeAlphaNumeric( String str )
	{
		return util.includeAlphaNumeric( str ) ;
	}

	/**
	 * 前後の空白文字(java正規表現の\sと全角スペース)を取り除く
	 * @param text 文字列
	 * @return 前後の空白文字を取り除いた文字列
	 */
	public static String trim(String text) {
		if ( text == null )	return null ;
		return text.replaceAll("(^[\\s　]+|[\\s　]+$)", "");
	}
}
