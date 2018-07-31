/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：RestResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest;

import java.io.Serializable ;
import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.List ;

import javax.xml.bind.annotation.XmlElement ;
import javax.xml.bind.annotation.XmlElementWrapper ;
import javax.xml.bind.annotation.XmlTransient ;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore ;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter ;

/**
 * REST API 処理結果.
 *
 * @author itsukaha
 */
public class RestResult implements Serializable
{
	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** 応答コード（必須） */
	private ResponseCode code = ResponseCode.OK ;

	/** 応答メッセージ（必須） */
	private String message ;

	/** 処理結果詳細（オプション） */
	private List<String> detailList = new ArrayList<String>() ;

	// ---------- 以下は内部情報（レスポンスとしては返却しない）

	/** ログ出力メッセージ（オプション） */
	private String logMessage ;

	/** 応答メッセージ生成時に使用するパラメータ */
	private Object[] messageParam ;

	// -------------------------------------------------------------------------
	// コンストラクタ
	// -------------------------------------------------------------------------

	/** デフォルトコンストラクタ */
	public RestResult() {}

	/**
	 * コンストラクタ（詳細なし）.
	 *
	 * @param code 処理結果コード
	 */
	public RestResult( ResponseCode code )
	{
		this( code, null, (String)null, null ) ;
	}

	// -------------------------------------------------------------------------

	/**
	 * コンストラクタ（詳細あり）.
	 *
	 * @param code 処理結果コード
	 * @param messageParam メッセージパラメータ
	 */
	public RestResult( ResponseCode code, Object[] messageParam )
	{
		this( code, messageParam, (String)null, "" ) ;
	}

	// -------------------------------------------------------------------------

	/**
	 * コンストラクタ（詳細／メッセージあり）.
	 *
	 * @param code 処理結果コード
	 * @param messageParam メッセージパラメータ
	 * @param detail 処理結果詳細
	 */
	public RestResult( ResponseCode code, Object[] messageParam, String detail )
	{
		this( code, messageParam, detail, "" ) ;
	}

	/**
	 * コンストラクタ（詳細リスト／メッセージあり）.
	 *
	 * @param code 処理結果コード
	 * @param messageParam メッセージパラメータ
	 * @param detailList 処理結果詳細リスト
	 */
	public RestResult( ResponseCode code, Object[] messageParam, List<String> detailList )
	{
		this( code, messageParam, detailList, "" ) ;
	}

	/**
	 * コンストラクタ（詳細の配列／メッセージあり）.
	 *
	 * @param code 処理結果コード
	 * @param messageParam メッセージパラメータ
	 * @param detailList 処理結果詳細リスト
	 */
	public RestResult( ResponseCode code, Object[] messageParam, String[] detailList )
	{
		this( code, messageParam, detailList, "" ) ;
	}

	// -------------------------------------------------------------------------

	/**
	 * コンストラクタ（詳細／メッセージあり）.
	 *
	 * @param code 処理結果コード
	 * @param messageParam メッセージパラメータ
	 * @param detail 処理結果詳細
	 * @param logMessage ログ出力メッセージ
	 */
	public RestResult( ResponseCode code, Object[] messageParam, String detail, Object logMessage )
	{
		if ( code != null ) this.code = code ;
		if ( detail != null ) this.detailList.add( detail ) ;

		this.messageParam = messageParam ;
		this.logMessage = String.valueOf( logMessage ) ;
	}

	/**
	 * コンストラクタ（詳細リスト／メッセージあり）.
	 *
	 * @param code 処理結果コード
	 * @param messageParam メッセージパラメータ
	 * @param detailList 処理結果詳細リスト
	 * @param logMessage ログ出力メッセージ
	 */
	public RestResult( ResponseCode code, Object[] messageParam, List<String> detailList, Object logMessage )
	{
		if ( code != null ) this.code = code ;
		if ( detailList != null ) this.detailList = detailList ;

		this.messageParam = messageParam ;
		this.logMessage = String.valueOf( logMessage ) ;
	}

	/**
	 * コンストラクタ（詳細の配列／メッセージあり）.
	 *
	 * @param code 処理結果コード
	 * @param messageParam メッセージパラメータ
	 * @param detailList 処理結果詳細リスト
	 * @param logMessage ログ出力メッセージ
	 */
	public RestResult( ResponseCode code, Object[] messageParam, String[] detailList, Object logMessage )
	{
		if ( code != null ) this.code = code ;
		if ( detailList != null ) this.detailList = Arrays.asList( detailList ) ;

		this.messageParam = messageParam ;
		this.logMessage = String.valueOf( logMessage ) ;
	}

	/**
	 * コンストラクタ（詳細の配列／メッセージあり）.
	 *
	 * @param code 処理結果コード
	 * @param messageParam メッセージパラメータ
	 * @param detailList 処理結果詳細リスト
	 * @param logMessage ログ出力メッセージ
	 */
	public RestResult( ResponseCode code, Object[] messageParam, String[] detailList, String logMessage )
	{
		if ( code != null ) this.code = code ;
		if ( detailList != null ) this.detailList = Arrays.asList( detailList ) ;

		this.messageParam = messageParam ;
		this.logMessage = logMessage ;
	}

	// -------------------------------------------------------------------------
	// 処理
	// -------------------------------------------------------------------------

	/**
	 * 文字列表現への変換.
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(
			this, ToStringStyle.DEFAULT_STYLE ) ;
	}

	// -------------------------------------------------------------------------
	// アクセサメソッド
	// -------------------------------------------------------------------------

	/**
	 * code 取得.
	 *
	 * @return code
	 */
	public ResponseCode getCode()
	{
		return code ;
	}

	/**
	 * code 設定.
	 *
	 * @param code code に設定する値.
	 */
	public void setCode( ResponseCode code )
	{
		this.code = code ;
	}

	/**
	 * code 設定（ClientFramework 専用）.
	 *
	 * @param codeString code に設定する値.
	 */
	@Deprecated
	@JsonSetter
	public void setCode( String codeString )
	{
		this.code = ResponseCode.valueOfResult( codeString ) ;
	}

	/**
	 * detailList 取得.
	 *
	 * @return detailList
	 */
	@XmlElementWrapper( name="detailList" )
	@XmlElement( name="detail" )
	@JsonProperty( "detailList" )
	public List<String> getDetailList()
	{
		return detailList ;
	}

	/**
	 * detailList 設定.
	 *
	 * @param detailList detailList に設定する値.
	 */
	public void setDetailList( List<String> detailList )
	{
		this.detailList = detailList ;
	}

	/**
	 * message 取得.
	 *
	 * @return message
	 */
	public String getMessage()
	{
		return message ;
	}

	/**
	 * message 設定.
	 *
	 * @param message message に設定する値.
	 */
	public void setMessage( String message )
	{
		this.message = message ;
	}

	/**
	 * logMessage 取得.
	 *
	 * @return logMessage
	 */
	@JsonIgnore
	@XmlTransient
	public String getLogMessage()
	{
		return logMessage ;
	}

	/**
	 * logMessage 設定.
	 *
	 * @param logMessage logMessage に設定する値.
	 */
	public void setLogMessage( String logMessage )
	{
		this.logMessage = logMessage ;
	}

	/**
	 * messageParam 取得.
	 *
	 * @return messageParam
	 */
	@JsonIgnore
	@XmlTransient
	public Object[] getMessageParam()
	{
		return messageParam ;
	}

	/**
	 * messageParam 設定.
	 *
	 * @param messageParam messageParam に設定する値.
	 */
	public void setMessageParam( Object[] messageParam )
	{
		this.messageParam = messageParam ;
	}
}
