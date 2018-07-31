/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：MappedDiagnosticContextLogFilter.java
 *
 * 概要：拡張情報をログに出力するためのフィルタ（複数の情報を個別に出力可能）
 */
package cloud.optim.aivoiceanalytics.core.modules.filter ;

import java.io.IOException ;

import javax.servlet.Filter ;
import javax.servlet.FilterChain ;
import javax.servlet.FilterConfig ;
import javax.servlet.ServletException ;
import javax.servlet.ServletRequest ;
import javax.servlet.ServletResponse ;
import javax.servlet.http.HttpServletRequest ;

import org.apache.log4j.MDC ;
import org.springframework.context.ApplicationContext ;
import org.springframework.web.context.support.WebApplicationContextUtils ;

import cloud.optim.aivoiceanalytics.core.modules.loginutil.LoginUtility;

/**
 * ログ出力用フィルター（セッション ID を設定）.<br />
 *
 * NDC に HTTP セッション ID / ログインユーザ ID  / IP アドレスを設定する.<br/>
 * NDC への設定内容は、出力フォーマットに %X{(name)} と記述することでログに出力可能.
 *
 * ※本クラスは NDC の利用サンプルです.
 *
 */
public final class MappedDiagnosticContextLogFilter implements Filter
{
	/** MDC コンテキストのキー名：リモート IP アドレス */
	public static final String KEY_IP_ADDR = "ipAddress" ;

	/** MDC コンテキストのキー名：HTTP セッション ID */
	public static final String KEY_SESSION_ID = "sessionId" ;

	/** MDC コンテキストのキー名：ログインユーザ ID */
	public static final String KEY_USER_ID = "userId" ;

	/** ログイン情報取得用 */
	private LoginUtility util;

	@Override
	public void init( FilterConfig arg ) throws ServletException
	{
		// LoginUtility 取得

		ApplicationContext ac =
			WebApplicationContextUtils.getWebApplicationContext( arg.getServletContext() );

		if ( ac == null )
		{
			throw new ServletException( "ApplicationContext not found." ) ;
		}

		try
		{
			util = ac.getBean( LoginUtility.class );
		}
		catch( Exception ex ) { /* エラー処理は下でまとめて実施 */ }

		if ( util == null )
		{
			throw new ServletException( "LoginUtility not found." ) ;
		}
	}

	@Override
	public void doFilter( ServletRequest req, ServletResponse res, FilterChain chain )
		throws IOException, ServletException
	{
		String ipAddress = "" ;
		String userId = "" ;
		String sessionId = "" ;

		ipAddress = req.getRemoteAddr() ;
		if ( util != null ) userId = util.getUsername() ;

		if ( req instanceof HttpServletRequest )
		{
			sessionId = ((HttpServletRequest)req).getSession( true ).getId() ;
		}

		MDC.put( KEY_IP_ADDR, ipAddress ) ;
		MDC.put( KEY_USER_ID, userId ) ;
		MDC.put( KEY_SESSION_ID, sessionId ) ;

		try
		{
			chain.doFilter( req, res ) ;
		}
		finally
		{
			MDC.remove( KEY_IP_ADDR ) ;
			MDC.remove( KEY_USER_ID ) ;
			MDC.remove( KEY_SESSION_ID ) ;
		}
	}

	@Override
	public void destroy() {}
}
