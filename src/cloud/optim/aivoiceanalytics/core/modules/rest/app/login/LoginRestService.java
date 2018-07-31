package cloud.optim.aivoiceanalytics.core.modules.rest.app.login;

import javax.annotation.Resource ;
import javax.servlet.http.HttpServletRequest ;
import javax.ws.rs.Consumes ;
import javax.ws.rs.GET ;
import javax.ws.rs.POST ;
import javax.ws.rs.Path ;
import javax.ws.rs.Produces ;
import javax.ws.rs.core.Context ;

import org.apache.commons.logging.Log ;
import org.apache.commons.logging.LogFactory ;
import org.springframework.stereotype.Component ;

import cloud.optim.aivoiceanalytics.core.modules.loginutil.CustomUser;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.CustomUserHolder;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.LoginUtility;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.PasswordUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ExceptionUtil;
import cloud.optim.aivoiceanalytics.core.modules.rest.MessageUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestLog;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;
import cloud.optim.aivoiceanalytics.core.modules.session.SessionManager;

/**
 * LoginRestSevice.<br/>
 */
@Path( "/login" )
@Consumes( { "application/json", "application/xml" } )
@Produces( { "application/json", "application/xml" } )
@Component
public class LoginRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/** RestLog */
	@Resource private RestLog restlog ;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility ;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility ;

	/** PassowrdUtility（ハッシュ値計算用） */
	@Resource private PasswordUtility passwordUtility ;

	/** 拡張情報 */
	@Resource private CustomUserHolder customUserHolder ;

	/** セッションマネージャー */
	@Resource private SessionManager sessionManager;

	/**
	 * セキュリティコンテキスト格納セッション変数名
	 */
	@SuppressWarnings( "unused" )
	private static final String KEY_CONTEXT = "SPRING_SECURITY" ;

	/**
	 * カレントセッションのログインユーザ情報取得
	 * @param httpRequest HTTPリクエスト
	 *
	 * @return このセッションのログインユーザ情報
	 */
	@GET
	@POST
	@Path( "/info" )
	public LoginResponse info( @Context HttpServletRequest httpRequest )
	{
		String MNAME = "info" ;
		restlog.start( log, MNAME, null ) ;

		try
		{
			LoginResponse res = new LoginResponse() ;

			// ----- ログインユーザ情報取得

			CustomUser cu = loginUtility.getCustomUser() ;

			// ----- レスポンス作成

			res.addResult( new RestResult( ResponseCode.OK ) ) ;
			res.setCustomUser( cu ) ;

			messageUtility.fillMessage( res.getResultList() ) ;
			restlog.end( log, MNAME, null, res, res.getResultList() ) ;

			return res ;
		}
		catch ( Exception ex )
		{
			throw ExceptionUtil.handleException( log,
				ResponseCode.AUTH_ERROR, null, null, null, ex );
		}
	}


	/**
	 * カレントセッションのログインユーザ情報取得
	 * @param httpRequest HTTPリクエスト
	 *
	 * @return このセッションのログインユーザ情報
	 */
	@POST
	@Path( "/forceLogin" )
	public LoginResponse forceLogin( @Context HttpServletRequest httpRequest)
	{
		String MNAME = "forceLogin" ;
		restlog.start( log, MNAME, null ) ;

		try
		{
			// ----- ログインユーザ情報取得
			CustomUser cu = loginUtility.getCustomUser() ;

			// 多重ログイン情報上書き
			sessionManager.forceLogin(cu.getCompanyId(), cu.getUserId(), httpRequest.getSession());

			// ----- レスポンス作成
			LoginResponse res = new LoginResponse() ;
			res.addResult( new RestResult( ResponseCode.OK ) ) ;

			messageUtility.fillMessage( res.getResultList() ) ;
			restlog.end( log, MNAME, null, res, res.getResultList() ) ;

			return res ;

		}
		catch ( Exception ex )
		{
			throw ExceptionUtil.handleException( log,
				ResponseCode.AUTH_ERROR, null, null, null, ex );
		}
	}
}
