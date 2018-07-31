/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：ServletContextListenerImpl.java
 *
 */
package cloud.optim.aivoiceanalytics.core.common.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * サーブレットコンテキストリスナー実装クラス
 * web.xmlで以下のような設定を行うことで、Webアプリケーションの起動／シャットダウンのタイミングをコンソール上で確認できる<br/>
 * <pre>
 * 	<listener>
 * 		<listener-class>cloud.optim.aivoiceanalytics.core.common.listener.ServletContextListenerImpl</listener-class>
 * 	</listener>
 * </pre>
 * @author ynishino
 */
public class ServletContextListenerImpl implements ServletContextListener {

	/** ログ */
	private static final Log log = LogFactory.getLog(ServletContextListenerImpl.class);

	/**
	 * デフォルトコンストラクタ.
	 */
	public ServletContextListenerImpl() {}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		log.info("contextInitialized : serverInfo        =" + context.getServerInfo());
		log.info("contextInitialized : majorVersion      =" + context.getMajorVersion());
		log.info("contextInitialized : minorVersion      =" + context.getMinorVersion());
		log.info("contextInitialized : servletContextName=" + context.getServletContextName());
		log.info("contextInitialized : contextPath       =" + context.getContextPath());
		log.info("contextInitialized : realPath          =" + context.getRealPath("/"));
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		log.info("contextDestroyed : " + context.getServletContextName());
	}
}
