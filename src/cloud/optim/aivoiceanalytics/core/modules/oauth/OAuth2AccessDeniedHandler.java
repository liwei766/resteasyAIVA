package cloud.optim.aivoiceanalytics.core.modules.oauth;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * エラーページに遷移するためのエラーハンドラ
 *
 * @author itsukaha
 */
public class OAuth2AccessDeniedHandler implements AccessDeniedHandler, AuthenticationFailureHandler
{
	private Log log = LogFactory.getLog( this.getClass() );

	private String errorPage = "/";

	/**
	 * errorPage 設定.
	 *
	 * @param errorPage errorPage に設定する値.
	 */
	public void setErrorPage( String errorPage )
	{
		this.errorPage = errorPage;
	}

	/**
	 * @see org.springframework.security.web.access.AccessDeniedHandler#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.access.AccessDeniedException)
	 */
	@Override
	public void handle( HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException authException ) throws IOException {

		if ( log.isDebugEnabled() ) log.debug( "AccessDenied" );
		goErrorPage( request, response );
	}

	/**
	 * @see org.springframework.security.web.authentication.AuthenticationFailureHandler#onAuthenticationFailure(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.AuthenticationException)
	 */
	@Override
	public void onAuthenticationFailure( HttpServletRequest request, HttpServletResponse response,
			 AuthenticationException exception) throws IOException
	{
		if ( log.isDebugEnabled() ) log.debug( "AuthenticationFailure" );
		goErrorPage( request, response );
	}

	/**
	 * エラーページに遷移する.
	 *
	 * @param request HTTP リクエスト
	 * @param response HTTP レスポンス
	 *
	 * @throws IOException
	 */
	private void goErrorPage( HttpServletRequest request, HttpServletResponse response ) throws IOException
	{
		String uri = request.getRequestURI(); // ルートコンテキスト名を含む
		String path = request.getServletPath(); // ルートコンテキスト名を含まない

		String root = "";

		try
		{
			int idx = uri.indexOf( path );
			if ( idx > 0 ) root = uri.substring( 0, idx );
		}
		catch ( Exception ex ) { /* NOOP : ルートコンテキストは使用しない */ }

		response.sendRedirect( root + errorPage );
	}
}
