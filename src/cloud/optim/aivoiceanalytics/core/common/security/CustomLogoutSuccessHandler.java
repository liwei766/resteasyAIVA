package cloud.optim.aivoiceanalytics.core.common.security ;

import java.io.IOException ;

import javax.servlet.ServletException ;
import javax.servlet.http.HttpServletRequest ;
import javax.servlet.http.HttpServletResponse ;

import org.apache.commons.logging.Log ;
import org.apache.commons.logging.LogFactory ;
import org.springframework.security.core.Authentication ;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler ;
import org.springframework.stereotype.Component ;

/**
 * ログアウト成功後の処理（200 OK を返送）.
 *
 * @author itsukaha
 */
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler
{
	/** Commons Logging instance. */
	private Log log = LogFactory.getLog( this.getClass() ) ;

	@Override
	public void onLogoutSuccess(
		HttpServletRequest request, HttpServletResponse response,
		Authentication authentication )
		throws IOException, ServletException
	{
		if ( log.isDebugEnabled() )
		{
			log.debug( "onLogoutSuccess" ) ;
		}

		HttpServletResponse httpResponse = response ;
		httpResponse.setStatus( HttpServletResponse.SC_OK ) ;
	}
}
