/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：ConcurrentSessionManagemntFilter.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.session;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import cloud.optim.aivoiceanalytics.api.util.AuthUtil;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.CustomUser;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.CustomUserHolder;

/**
 * 同一ユーザによる多重ログインの制御をするフィルタ
 * ログイン中に他のセッションIDでログインしようとした場合はエラーにする(先勝ち)
 *
 * @author reiff
 */
public class ConcurrentSessionManagemntFilter extends GenericFilterBean implements InitializingBean
{
	/** Log */
	@SuppressWarnings("unused")
	private final Log log = LogFactory.getLog( this.getClass() );

	/** 拡張ログイン情報 */
	@Resource private CustomUserHolder customUserHolder ;

	/** セッションマネジャー */
	@Resource private SessionManager sessionManager;

	@Resource AuthUtil authUtil;

	/** エラー時のリダイレクト先 */
	private String redirectUrl;

	// -------------------------------------------------------------------------
	// 多重ログインチェック処理
	// -------------------------------------------------------------------------

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter( ServletRequest req, ServletResponse res, FilterChain chain ) throws IOException, ServletException
	{

		HttpServletRequest request = (HttpServletRequest)req;

		boolean logined = isLogined(request);

		// ログイン済みの場合はエラーページへ遷移する
		if ( logined ) {
			// clearAuth(request);
			((HttpServletResponse)res).sendRedirect(request.getContextPath() + redirectUrl);
		} else {
			chain.doFilter(req, res);
		}
	}

	/**
	 * 既にログイン中か確認する
	 * @param req HTTP リクエスト
	 * @return 同一企業、ユーザでログイン中の場合はtrue、それ以外はfalse
	 *
	 * @throws IOException
	 * @throws ServletException
	 */
	private boolean isLogined( HttpServletRequest req ) throws IOException, ServletException
	{
		// OAuth 認証認証情報が取得できない場合は何もしない
		Authentication prev = SecurityContextHolder.getContext().getAuthentication();
		if ( prev == null ) return false;

		// セッションとユーザ情報の取得
		HttpSession session = req.getSession(false);
		CustomUser customUser = customUserHolder.getCustomUser();

		if (session == null) return false;
		if (customUser == null) return false;
		if (authUtil.isAnonymous()) return false;

		return sessionManager.isLogined(customUser.getCompanyId(), customUser.getUserId(), session);
	}

//	/**
//	 * 認証情報削除
//	 */
//	private void clearAuth(HttpServletRequest req)
//	{
//		SecurityContextHolder.getContext().setAuthentication( null );
//		customUserHolder.setCustomUser( null );
//		HttpSession session = req.getSession(false);
//		if(session != null) session.invalidate();
//	}

	// -------------------------------------------------------------------------

	/**
	 * @param redirectUrl セットする redirectUrl
	 */
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

}