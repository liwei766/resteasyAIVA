/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：BinaryImageRequest.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest.app.image;

import javax.xml.bind.annotation.XmlRootElement ;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;


/**
 * BinaryImage API リクエストクラス.
 *
 * @author itsukaha
 */
@XmlRootElement( name="restRequest" )
public class BinaryImageRequest
{
	// ----- アップロードファイル取得時に使用

	/** ファイル取得対象の要求 ID */
	private String id ;

	// ----- 登録ファイル取得時に使用

	/** ファイル取得対象のエンティティ名 */
	private String entityName ;

	/** ファイル取得対象の PK */
	private String pk ;

	/** ファイル取得対象の項目名 */
	private String fieldName ;

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
	// アクセサメソッド
	// -------------------------------------------------------------------------

	/**
	 * id 取得.
	 *
	 * @return id
	 */
	public String getId()
	{
		return id ;
	}

	/**
	 * id 設定.
	 *
	 * @param id id に設定する値.
	 */
	public void setId( String id )
	{
		this.id = id ;
	}

	/**
	 * pk 取得.
	 *
	 * @return pk
	 */
	public String getPk()
	{
		return pk ;
	}

	/**
	 * pk 設定.
	 *
	 * @param pk pk に設定する値.
	 */
	public void setPk( String pk )
	{
		this.pk = pk ;
	}

	/**
	 * fieldName 取得.
	 *
	 * @return fieldName
	 */
	public String getFieldName()
	{
		return fieldName ;
	}

	/**
	 * fieldName 設定.
	 *
	 * @param fieldName fieldName に設定する値.
	 */
	public void setFieldName( String fieldName )
	{
		this.fieldName = fieldName ;
	}

	/**
	 * entityName 取得.
	 *
	 * @return entityName
	 */
	public String getEntityName()
	{
		return entityName ;
	}

	/**
	 * entityName 設定.
	 *
	 * @param entityName entityName に設定する値.
	 */
	public void setEntityName( String entityName )
	{
		this.entityName = entityName ;
	}
}
