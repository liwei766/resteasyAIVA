/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：NestedDiagnosticContextLogFilter.java
 *
 * 概要：拡張情報をログに出力するためのフィルタ
 */
package cloud.optim.aivoiceanalytics.core.modules.filter;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.NDC;
import org.springframework.context.ApplicationContext ;
import org.springframework.web.context.support.WebApplicationContextUtils ;

import cloud.optim.aivoiceanalytics.core.modules.loginutil.LoginUtility;

/**
 * NDC に HTTP セッション ID とログインユーザ ID を設定する.<br/>
 * NDC への設定内容は、出力フォーマットに %x と記述することでログに出力可能.
 *
 * ※本クラスは NDC の利用サンプルです.
 *
 * @author kidakoji
 */
public final class NestedDiagnosticContextLogFilter implements Filter {

	/** Commons Logging instance.  */
	@SuppressWarnings("unused")
	private Log log = LogFactory.getFactory().getInstance(
			NestedDiagnosticContextLogFilter.class);

	/** ログイン情報取得用 */
	private LoginUtility util;

	/** ログフォーマット */
	private String logFormat = "{0};{1}" ;

	/**
	 * コンストラクタ
	 */
	public NestedDiagnosticContextLogFilter() {
		super();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig arg) throws ServletException {

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

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		if (request instanceof HttpServletRequest) {

			// 2009.07.01 ログイン ID を出力できるように修正

			Object obj[] = {
				util != null ? util.getUsername() : "",
				((HttpServletRequest)request).getSession().getId()
			};

			NDC.push(MessageFormat.format(logFormat,obj));

//			NDC.push( ((HttpServletRequest) request).getSession().getId() );
			try {
				chain.doFilter(request, response);
			} finally {
				NDC.remove();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {

	}

}
