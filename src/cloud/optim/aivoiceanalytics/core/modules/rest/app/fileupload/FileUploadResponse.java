/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：FileUploadResponse.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest.app.fileupload;

import java.util.ArrayList ;
import java.util.List ;

import javax.xml.bind.annotation.XmlElement ;
import javax.xml.bind.annotation.XmlElementWrapper ;
import javax.xml.bind.annotation.XmlRootElement ;

import com.fasterxml.jackson.annotation.JsonIgnore ;
import com.fasterxml.jackson.annotation.JsonProperty;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;

/**
 * Contents ファイルアップロード関連 API レスポンスクラス.
 *
 * @author itsukaha
 */
@XmlRootElement( name="restResponse" )
public class FileUploadResponse
{
	/** 処理結果 */
	private List<RestResult> resultList = new ArrayList<RestResult>() ;

	// -------------------------------------------------------------------------

	/** 要求 ID */
	private String uploadId ;

	/** アップロードしたファイル名 */
	private String uploadFileName ;

	/** サムネールの要求 ID */
	private String thumbId ;

	/** サムネールのファイル名 */
	private String thumbFileName ;

	// -------------------------------------------------------------------------

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString()
	{
		return ToStringHelper.toString( this ) ;
	}

	// -------------------------------------------------------------------------
	// 処理結果を扱う処理
	// -------------------------------------------------------------------------

	/**
	 * 処理結果数を取得する.
	 *
	 * @return 登録されている処理結果数
	 */
	@JsonIgnore
	public int getResultLength()
	{
		if ( resultList == null ) return 0 ;

		return resultList.size() ;
	}

	/**
	 * 処理結果を登録する.
	 *
	 * @param result 登録する処理結果
	 */
	public void addResult( RestResult result )
	{
		if ( resultList == null )
		{
			resultList = new ArrayList<RestResult>() ;
		}

		resultList.add( result ) ;
	}

	// -------------------------------------------------------------------------
	// アクセサメソッド
	// -------------------------------------------------------------------------

	/**
	 * resultList 取得.
	 *
	 * @return resultList
	 */
	@XmlElementWrapper( name="resultList" )
	@XmlElement( name="result" )
	@JsonProperty( "resultList" )
	public List<RestResult> getResultList()
	{
		return resultList ;
	}

	/**
	 * resultList 設定.
	 *
	 * @param resultList resultList に設定する値.
	 */
	public void setResultList( List<RestResult> resultList )
	{
		this.resultList = resultList ;
	}

	/**
	 * uploadId 取得.
	 *
	 * @return uploadId
	 */
	public String getUploadId()
	{
		return uploadId ;
	}

	/**
	 * uploadId 設定.
	 *
	 * @param uploadId uploadId に設定する値.
	 */
	public void setUploadId( String uploadId )
	{
		this.uploadId = uploadId ;
	}

	/**
	 * uploadFileName 取得.
	 *
	 * @return uploadFileName
	 */
	public String getUploadFileName()
	{
		return uploadFileName ;
	}

	/**
	 * uploadFileName 設定.
	 *
	 * @param uploadFileName uploadFileName に設定する値.
	 */
	public void setUploadFileName( String uploadFileName )
	{
		this.uploadFileName = uploadFileName ;
	}

	/**
	 * thumbId 取得.
	 *
	 * @return thumbId
	 */
	public String getThumbId()
	{
		return thumbId ;
	}

	/**
	 * thumbId 設定.
	 *
	 * @param thumbId thumbId に設定する値.
	 */
	public void setThumbId( String thumbId )
	{
		this.thumbId = thumbId ;
	}

	/**
	 * thumbFileName 取得.
	 *
	 * @return thumbFileName
	 */
	public String getThumbFileName()
	{
		return thumbFileName ;
	}

	/**
	 * thumbFileName 設定.
	 *
	 * @param thumbFileName thumbFileName に設定する値.
	 */
	public void setThumbFileName( String thumbFileName )
	{
		this.thumbFileName = thumbFileName ;
	}
}
