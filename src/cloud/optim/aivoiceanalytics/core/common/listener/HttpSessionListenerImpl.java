/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：HttpSessionListenerImpl.java
 *
 */
package cloud.optim.aivoiceanalytics.core.common.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * HTTPセッションリスナー実装クラス
 * web.xmlで以下のような設定を行うことで、セッション切れのタイミングをコンソール上で確認できる<br/>
 * <pre>
 * 	<listener>
 * 		<listener-class>cloud.optim.aivoiceanalytics.core.common.listener.HttpSessionListenerImpl</listener-class>
 * 	</listener>
 * </pre>
 * @author ynishino
 */
public class HttpSessionListenerImpl implements HttpSessionListener {

	/** ログ */
	private static final Log log = LogFactory.getLog(HttpSessionListenerImpl.class);

	/**
	 * デフォルトコンストラクタ.
	 */
	public HttpSessionListenerImpl() {}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		log.info("session created : " + event.getSession().getId());
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		// get the destroying session...
		HttpSession session = event.getSession();
		log.info("session destroyed :" + session.getId() + " Logging out user...");

	}
}
