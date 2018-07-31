package cloud.optim.aivoiceanalytics.core.modules.loginutil;

import java.util.ArrayList ;
import java.util.Collection ;
import java.util.List ;
import java.util.Map;

import javax.annotation.Resource ;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log ;
import org.apache.commons.logging.LogFactory ;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener ;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent ;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority ;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component ;

import cloud.optim.aivoiceanalytics.api.entity.CompanyManagement;
import cloud.optim.aivoiceanalytics.api.entity.RecaiusLicense;
import cloud.optim.aivoiceanalytics.api.entity.dao.CompanyManagementDao;
import cloud.optim.aivoiceanalytics.api.entity.dao.RecaiusLicenseDao;
import cloud.optim.aivoiceanalytics.core.common.utility.IPAddressChecker;
import cloud.optim.aivoiceanalytics.core.modules.oauth.AuthMapUtil;

/**
 * ユーザ情報のキャッシュを作成するログイン成功イベントリスナ.
 *
 * @author itsukaha
 */
@Component
public class CustomLoginSuccessEventListener
	implements ApplicationListener<AuthenticationSuccessEvent>
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/** 拡張情報 */
	@Resource private CustomUserHolder customUserHolder ;

	/** CompanyManagementDao */
	@Resource private CompanyManagementDao companyManagementDao;

	/** RecaiusLicenseDao */
	@Resource private RecaiusLicenseDao recaiusLicenseDao;

	@Resource private HttpServletRequest request;

	/** 暗号ユーティリティの共通鍵. */
	@Value("${cryptor.key}")
	private String cryptorKey;

	/**
	 * ログイン成功イベント処理
	 *
	 * @param ev イベントオブジェクト
	 */
	@Override
	public void onApplicationEvent( AuthenticationSuccessEvent ev )
	{
		if ( log.isDebugEnabled() ) log.debug( "### START : LoginSuccessEvent" ) ;

		// ----- ログイン成功したユーザ情報を取得

		String companyId = null;
		String userId = null ;
		String userName = null;
		Collection<? extends GrantedAuthority> authList = null ;

		try
		{
			Authentication auth = ev.getAuthentication();

			userId = auth.getName() ;
			authList = auth.getAuthorities() ;

			OAuth2Authentication source = (OAuth2Authentication)auth;

			UsernamePasswordAuthenticationToken uauth = (UsernamePasswordAuthenticationToken)source.getUserAuthentication();

			@SuppressWarnings( "unchecked" )
			Map<String, Object> detail = (Map<String, Object>)uauth.getDetails();

			companyId = AuthMapUtil.companyId( detail );

			userName = AuthMapUtil.userName( detail );
		}
		catch ( Exception ex ) { /* NOOP : 認証情報の構成が想定と異なる場合 */ }

		if ( companyId == null )
		{
			if ( log.isDebugEnabled() ) log.debug( "companyId is null." ) ;
			return ;
		}

		if ( userId == null )
		{
			if ( log.isDebugEnabled() ) log.debug( "UserId is null." ) ;
			return ;
		}

		if ( userName == null )
		{
			if ( log.isDebugEnabled() ) log.debug( "userName is null." ) ;
			return ;
		}

		if ( authList == null )
		{
			if ( log.isDebugEnabled() ) log.debug( "authList is null." ) ;
			return ;
		}

		// ----- 拡張情報設定

		List<String> authIdList = new ArrayList<String>( authList.size() ) ;

		for ( GrantedAuthority ga : authList )
		{
			authIdList.add( ga.getAuthority() ) ;
		}

		// 企業管理情報とリカイアスライセンス情報の取得
		CompanyManagement example = new CompanyManagement();
		example.setCompanyId(companyId);
		List<CompanyManagement> list = companyManagementDao.findByExample(example);
		CompanyManagement companyInfo = (list == null || list.isEmpty() ) ? null : list.get(0);
		RecaiusLicense recaiusLicense = (companyInfo == null) ? null : recaiusLicenseDao.get(companyInfo.getRecaiusLicenseId());

		// 企業管理情報に接続元制限IPアドレスがが設定されている場合は接続元IPアドレスチェックを行う
		if (companyInfo != null) {
			// 接続元IPアドレスを取得する
			String ipAddress = request.getRemoteAddr();
			if(!IPAddressChecker.isNetwork(ipAddress, companyInfo.getPermitIpAddress(), false)) {
				log.error( "Invalid ip address : " +  ipAddress);
				return;
			}
		}

		CustomUser customUser = new CustomUser( companyId, userId, userName, authIdList, null, companyInfo, recaiusLicense ) ;
		customUser.setDecryptKey(cryptorKey);
		customUserHolder.setCustomUser( customUser ) ;
	}
}
