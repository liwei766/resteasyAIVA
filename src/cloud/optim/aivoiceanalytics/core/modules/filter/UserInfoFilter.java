/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：MappedDiagnosticContextLogFilter.java
 *
 * 概要：拡張情報をログに出力するためのフィルタ（複数の情報を個別に出力可能）
 */
package cloud.optim.aivoiceanalytics.core.modules.filter ;

import java.io.IOException ;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.Filter ;
import javax.servlet.FilterChain ;
import javax.servlet.FilterConfig ;
import javax.servlet.ServletException ;
import javax.servlet.ServletRequest ;
import javax.servlet.ServletResponse ;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext ;
import org.springframework.web.context.support.WebApplicationContextUtils ;

import com.fasterxml.jackson.databind.ObjectMapper;

import cloud.optim.aivoiceanalytics.core.common.utility.HankakuKanaConverter;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.CustomUser;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.LoginUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestValidatorUtils;

/**
 * リクエストヘッダよりユーザ情報を取得しリクエストスコープのユーザ情報に設定すrフィルター.<br />
 *
 */
public final class UserInfoFilter implements Filter
{
	/** ヘッダーフィールド名 */
	public static final String HEADER_FIELD_NAME = "X-USER-INFO" ;

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
		// HTTPヘッダからユーザ情報を取得する
		String undecodeHeader = ((HttpServletRequest) req).getHeader(HEADER_FIELD_NAME);
		// ヘッダーが取得できない場合はエラー
		if (undecodeHeader == null || undecodeHeader.isEmpty()) {
			((HttpServletResponse)res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid http header." ) ;
			return;
		}

		try {
			// URLデコードする
			String header = URLDecoder.decode(undecodeHeader, StandardCharsets.UTF_8.name());

			// ヘッダ文字列内に含まれる半角カナ文字を全角カナ文字に変換する
			header = HankakuKanaConverter.convert(header);

			// パースする
			ObjectMapper mapper = new ObjectMapper();
			CustomUser customUser = mapper.readValue(header, CustomUser.class);

			// 入力チェック
			// 企業ID
			RestValidatorUtils.fieldValidate( "companyId", customUser.getCompanyId(), true, null, 32 );
			// ユーザID
			RestValidatorUtils.fieldValidate( "userId", customUser.getUserId(), true, null, 32 );
			// ユーザ名
			RestValidatorUtils.fieldValidate( "userName", customUser.getUserName(), true, null, 100 );

			// リクエストスコープにユーザ情報を設定する
			if ( util != null ) util.setCustomUser(customUser);

			chain.doFilter( req, res ) ;
		}catch(Exception e) {
			((HttpServletResponse)res).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid http header." ) ;
			return;
		}
	}

	@Override
	public void destroy() {}
}
