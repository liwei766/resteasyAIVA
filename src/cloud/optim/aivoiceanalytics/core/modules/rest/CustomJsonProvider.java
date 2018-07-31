/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：CustomJsonProvider.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest;

import javax.ws.rs.Consumes ;
import javax.ws.rs.Produces ;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider ;

import org.apache.commons.logging.Log ;
import org.apache.commons.logging.LogFactory ;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.springframework.stereotype.Component ;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 拡張 JSON プロバイダ.<br />
 * <ul>
 * <li>API 上でやりとりする日付の書式を、
 * すべてサーバの現地時刻として扱うように変更する.</li>
 * <li>クライアントから受信する日付の書式を複数許容する</li>
 *
 * @author itsukaha
 */
@Component
@Provider
@Consumes( { "application/*+json", "text/json" } )
@Produces( { "application/*+json", "text/json" } )
public class CustomJsonProvider extends ResteasyJackson2Provider implements ContextResolver<ObjectMapper>
{
	/** Commons Logging instance. */
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/**  */
	private ObjectMapper mapper;

	@Override
    public ObjectMapper getContext( Class<?> objectType )
    {
        return mapper;
    }

	/**
	 * Constructor.
	 */
	public CustomJsonProvider()
	{
		super();

		log.trace( "Constructor " + this.getClass().getSimpleName() );

		mapper = _mapperConfig.getConfiguredMapper() ;
		if ( mapper == null )
		{
			mapper = _mapperConfig.getDefaultMapper() ;
			_mapperConfig.setMapper( mapper ) ;
		}

		ObjectMapperFactory.setupObjectMapper( mapper );
	}
}