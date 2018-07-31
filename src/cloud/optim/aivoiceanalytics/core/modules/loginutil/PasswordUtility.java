/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：PasswordUtility.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.loginutil;

import java.io.Serializable ;
import java.util.Arrays ;

import org.apache.commons.codec.digest.DigestUtils ;
import org.apache.commons.logging.Log ;
import org.apache.commons.logging.LogFactory ;
import org.springframework.stereotype.Component ;

/**
 * パスワードを扱うためのクラス.以下の機能を提供します.<br />
 * <ul>
 * <li>パスワードのチェックとハッシュ化.</li>
 * <li>ランダムパスワードの生成.</li>
 *
 * @author kidakoji
 */
@Component
public class PasswordUtility implements Serializable {

	/**
	 * シリアルバージョン番号
	 */
	private static final long serialVersionUID = 1L ;

	/** log  */
	private transient Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

	/**
	 * パスワードが正しいかチェックする.
	 *
	 * @param encPass ハッシュ化されたパスワード文字列
	 * @param rawPass チェックする文字列
	 * @param salt    ソルト
	 *
	 * @return true：パスワードは正しい　　false：不正
	 *
	 * @see org.springframework.security.authentication.encoding.BaseDigestPasswordEncoder#isPasswordValid
	 */
	public boolean isPasswordValid(
		String encPass, String rawPass, Object salt) {

		String MNAME = "isPasswordValid" ;
									log.trace( "### START : " + MNAME + ", " +
										encPass + ", " + salt ) ;
		String pass1 = "" + encPass;
		String pass2 = encode(rawPass, salt);

										log.trace( pass2 ) ;
		return pass1.equals(pass2);
	}

	/**
	 * パスワードをハッシュ化する.
	 *
	 * @param rawPass ハッシュ化する文字列
	 * @param salt    ソルト
	 *
	 * @return ハッシュ化した文字列
	 *
	 * @see org.springframework.security.authentication.encoding.BaseDigestPasswordEncoder#encodePassword
	 */
	public String encodePassword(String rawPass, Object salt) {

		String MNAME = "encodePassword" ;
									log.trace( "### START : " + MNAME + ", " + salt ) ;

//TODO SPRING SECURITY依存
//		return encodeInternal(mergePasswordAndSalt(rawPass, salt, false));

		return encodeInternal(rawPass);
	}

	/**
	 * パスワードをハッシュ化する.
	 *
	 * @param rawPass ハッシュ化する文字列
	 * @param salt    ソルト
	 *
	 * @return ハッシュ化した文字列
	 */
	public String encode(String rawPass, Object salt) {

		String MNAME = "encode" ;
									log.trace( "### START : " + MNAME + ", " + salt ) ;

//TODO SPRING SECURITY依存
//		return encodeInternal(mergePasswordAndSalt(rawPass, salt, false));


		return encodeInternal(rawPass);
	}

	/**
	 * パスワードを SHA-512 でハッシュ化する（処理本体）.
	 *
	 * @param input ハッシュ化する文字列
	 *
	 * @return ハッシュ化した文字列
	 */
	private String encodeInternal(String input) {

		String output = null ;

// TODO
//		if (getEncodeHashAsBase64()) {
//
//			byte[] encoded = Base64.encodeBase64(DigestUtils.sha512(input));
//
//			output = new String(encoded);
//		}
//
//		else {

			output = DigestUtils.sha512Hex(input);
//		}

		return output;
	}

	// -------------------------------------------------------------------------
	// ランダムパスワード生成機能
	// -------------------------------------------------------------------------

	/**
	 * コンストラクタ.
	 * 文字種別ごとのランダム文字候補を連結してランダム候補文字列に設定.
	 */
	public PasswordUtility()
	{
		StringBuilder sb = new StringBuilder() ;
		for ( String str : KeyChars )
		{
			sb.append( str );
		}
		KeyString = sb.toString() ;
	}

	/** ランダムキー長デフォルト値 */
	private int KeyLength = 8;

	/** ランダム文字候補（文字種別ごと） */
	private String[] KeyChars = new String[] {
		"ABCDEFGHIJKLMNOPQRSTUVWXYZ",
		"abcdefghijklmnopqrstuvwxyz",
		"123456790",
		"=-_."
	};

	/** ランダム候補文字列 */
	private String KeyString = "";

	/**
	 * デフォルトの長さのランダムキーを発行する.
	 *
	 * @return String ランダムな文字列
	 */
	public String getRandomKey() {
		return getRandomKey(KeyLength);
	}

	/**
	 * 長さを指定してランダムキーを発行する.
	 * 単一の文字種別だけで構成されている場合は作り直しを行う.
	 *
	 * @param length キーの長さ
	 *
	 * @return String ランダムな文字列
	 */
	public String getRandomKey(int length) {

		char[] keyValue = new char[ length ];
		boolean[] used = new boolean[ KeyChars.length ];

		Arrays.fill( used, false ) ;

		for (int i = 0; i < length; i++) {

			int n = (int)(Math.random() * KeyString.length());
			keyValue[i] = KeyString.charAt( n );

			for ( int j = 0 ; j < KeyChars.length ; j++ ) {

				if ( KeyChars[j].indexOf( keyValue[i] ) >= 0 ) {
					used[j] = true ;
					break ;
				}
			}
		}

		// 文字種チェック

		int typeCount = 0 ;
		for ( boolean use : used ) {

			if ( use ) typeCount++ ;
		}

		// 単一の文字種で構成されている場合は、もう一回生成しなおす
		return ( typeCount < 2 ) ? getRandomKey(length) : new String(keyValue);
	}

	/**
	 * 手動でパスワードを発行するためのアプリケーション
	 * >java PasswordUtils <username> <password>
	 *
	 * @param args <username> <password>
	 * @throws java.io.IOException エラー
	 */
	public static void main( String[] args ) throws java.io.IOException {

		PasswordUtility util = new PasswordUtility();

		String username = "" ;
		String password = "" ;

		if ( args.length > 0 ) {

			username = args[0] ;
		}
		else {

			username = input( "Enter user-id : " ) ;
			password = input( "Enter password(blank to generate) : " ) ;
		}

		if ( args.length > 1 ) {

			password = args[1] ;
		}
		else if ( "".equals( password ) ) {

			password = util.getRandomKey();
		}

		System.out.println( "USERNAME         : " + username );
		System.out.println( "PASSWORD         : " + password );
		System.out.println( "PASSWORD(Encode) : " + util.encode( password, username ) );
	}

	/**
	 * 指定されたプロンプトを表示して標準入力を 1 行読み取る.
	 *
	 * @param prompt 入力前に表示するプロンプト文字列
	 * @return 1 行分の入力内容
	 * @throws java.io.IOException エラー
	 */
	private static String input( String prompt ) throws java.io.IOException
	{
		System.out.print( prompt ) ;

		java.io.BufferedReader reader =
			new java.io.BufferedReader( new java.io.InputStreamReader( System.in ) ) ;

		return reader.readLine() ;
	}

	// -------------------------------------------------------------------------
	// アクセサメソッド
	// -------------------------------------------------------------------------

	/**
	 * KeyChars 取得.
	 *
	 * @return KeyChars
	 */
	public String[] getKeyChars() { return KeyChars; }

	/**
	 * KeyChars 設定. KeyString にも反映.
	 *
	 * @param keyChars keyChars に設定する値
	 */
	protected void setKeyChars(String[] keyChars) {

		if ( keyChars == null ) throw new NullPointerException( "keyChars" );

		KeyChars = keyChars;

		StringBuilder sb = new StringBuilder() ;
		for ( String str : KeyChars )
		{
			sb.append( str );
		}

		if ( sb.length() <= 0 ) throw new IllegalArgumentException( "Too few characters." );

		KeyString = sb.toString() ;
	}

	/**
	 * KeyString 取得.
	 *
	 * @return KeyString
	 */
	public String getKeyString() {
		return KeyString;
	}

	/**
	 * KeyLength 取得.
	 *
	 * @return KeyLength
	 */
	public int getKeyLength() {
		return KeyLength;
	}

	/**
	 * KeyLength 設定.
	 *
	 * @param keyLength KeyLength
	 */
	public void setKeyLength(int keyLength) {
		KeyLength = keyLength;
	}
}
