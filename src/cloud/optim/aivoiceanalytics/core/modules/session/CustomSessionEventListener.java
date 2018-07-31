/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：CustomSessionEventListener.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.context.ApplicationContext;
import org.springframework.security.web.context.support.SecurityWebApplicationContextUtils;
import org.springframework.security.web.session.HttpSessionCreatedEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * セッションイベントリスナー
 * セッション破棄時にに多重ログイン管理情報の削除を行う
 */
public class CustomSessionEventListener implements HttpSessionListener {
	// ~ Static fields/initializers
	// =====================================================================================

	private static final String LOGGER_NAME = HttpSessionEventPublisher.class.getName();

	// ~ Methods
	// ========================================================================================================

	ApplicationContext getContext(ServletContext servletContext) {
		return SecurityWebApplicationContextUtils.findRequiredWebApplicationContext(servletContext);
	}

	/**
	 * Handles the HttpSessionEvent by publishing a {@link HttpSessionCreatedEvent} to the
	 * application appContext.
	 *
	 * @param event HttpSessionEvent passed in by the container
	 */
	public void sessionCreated(HttpSessionEvent event) {
	}

	/**
	 * Handles the HttpSessionEvent by publishing a {@link HttpSessionDestroyedEvent} to
	 * the application appContext.
	 *
	 * @param event The HttpSessionEvent pass in by the container
	 */
	public void sessionDestroyed(HttpSessionEvent event) {
		ApplicationContext context = getContext(event.getSession().getServletContext());
		HttpSession session = event.getSession();

		if ( context == null ) return;
		SessionManager manager = context.getBean( SessionManager.class );
		manager.removeBySession(session);
	}
}
