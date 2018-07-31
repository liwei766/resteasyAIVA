/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：CustomExceptionMapper.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest ;

import java.util.ArrayList ;
import java.util.List ;

import javax.annotation.PostConstruct ;
import javax.annotation.Resource ;
import javax.ws.rs.Consumes ;
import javax.ws.rs.Produces ;
import javax.ws.rs.core.Response ;
import javax.ws.rs.ext.ExceptionMapper ;
import javax.ws.rs.ext.Provider ;
import javax.xml.bind.annotation.XmlElement ;
import javax.xml.bind.annotation.XmlElementWrapper ;
import javax.xml.bind.annotation.XmlRootElement ;

import org.apache.commons.logging.Log ;
import org.apache.commons.logging.LogFactory ;
import org.springframework.stereotype.Component ;

import com.fasterxml.jackson.annotation.JsonProperty;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;

/**
 * RestException 例外クラスをレスポンスに変換する
 */
@Component
@Provider
@Consumes( { "application/*+json", "text/json", "application/*+xml", "text/xml" } )
@Produces( { "application/*+json", "text/json", "application/*+xml", "text/xml" } )
public class CustomExceptionMapper implements ExceptionMapper<RestException>
{
	/** Commons Logging instance. */
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/** RestLog */
	@Resource
	private RestLog restlog ;

	/** MessageUtility */
	@Resource
	private MessageUtility messageUtility ;

	/** 追加メッセージ格納ビーン */
	@Resource private RequestStatusHolder statusHolder;

	/**
	 * 初期処理.
	 * ResponseCode のコード定義チェック.
	 */
	@PostConstruct
	public void init()
	{
		@SuppressWarnings( "unused" )
		ResponseCode rc = ResponseCode.OK ; // クラスロード時にチェックが実行される
	}

	/**
	 * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
	 */
	@Override
	public Response toResponse( RestException ex )
	{
		RestResponse res = new RestResponse() ;

		res.setResultList( ex.getRestResultList() ) ;
		messageUtility.fillMessage( res.getResultList() ) ;

		if ( statusHolder.getAdditionalMessage() != null )
		{
			res.getResultList().get( 0 ).setMessage(
				res.getResultList().get( 0 ).getMessage() +
				statusHolder.getAdditionalMessage() ) ;
		}

		restlog.abort( ex.getLogger() != null ? ex.getLogger() : log,
			res, res.getResultList(), ex ) ;

		return Response.ok( res ).build() ;
	}

	/**
	 * 返送するレスポンス.
	 */
	@XmlRootElement( name = "restResponse" )
	public static class RestResponse
	{
		/** 処理結果 */
		private List<RestResult> resultList = new ArrayList<RestResult>() ;

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
		 * @param resultList resultList への設定値.
		 */
		public void setResultList( List<RestResult> resultList )
		{
			this.resultList = resultList ;
		}
	}
}
