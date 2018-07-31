package cloud.optim.aivoiceanalytics.core.modules.bot;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import cloud.optim.aivoiceanalytics.core.common.utility.IPAddressChecker;
import cloud.optim.aivoiceanalytics.core.common.utility.Network;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.CustomUser;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.CustomUserHolder;

/**
 * BOT 認証.
 *
 * @author itsukaha
 * @see AnonymousAuthenticationFilter
 */
public class HttpRequestAuthenticationProcessingFilter extends GenericFilterBean implements InitializingBean
{
	/**
	 * 拡張ログイン情報 .
	 * 認証成功イベントを発行できないのでここで登録する.
	 */
	@Resource private CustomUserHolder customUserHolder ;

	/** Log */
	private final Log log = LogFactory.getLog( this.getClass() );

	// -------------------------------------------------------------------------
	// プロパティ
	// -------------------------------------------------------------------------

	/**
	 * ユーザ ID.
	 */
	private String userId;

	/**
	 * ユーザ ID 設定.
	 *
	 * @param userId userId への設定値.
	 */
	public void setUserId( String userId )
	{
		if ( userId != null ) this.userId = userId.trim();
	}

	// -------------------------------------------------------------------------

	/**
	 * 権限名.
	 */
	private String authName;

	/**
	 * 権限名設定
	 *
	 * @param authName authName への設定値.
	 */
	public void setAuthName( String authName )
	{
		if ( authName != null ) this.authName = authName.trim();
	}

	// -------------------------------------------------------------------------

	/**
	 * 企業 ID ヘッダ名.
	 */
	private String companyIdHeaderName;

	/**
	 * 権限名設定
	 *
	 * @param companyIdHeaderName companyIdHeaderName への設定値.
	 */
	public void setCompanyIdHeaderName( String companyIdHeaderName )
	{
		if ( companyIdHeaderName != null ) this.companyIdHeaderName = companyIdHeaderName.trim();
	}

	// -------------------------------------------------------------------------

	/**
	 * ユーザエージェント.
	 *
	 * この値と一致する UserAgent リクエストヘッダがある場合に「認証 OK」.
	 * 設定しない場合はチェックしない（IP アドレスの判定のみ）.
	 */
	private String userAgent;

	/**
	 * ユーザエージェント設定
	 *
	 * @param userAgent userAgent への設定値.
	 */
	public void setUserAgent( String userAgent )
	{
		if ( userAgent != null ) this.userAgent = userAgent.trim();
	}

	// -------------------------------------------------------------------------

	/**
	 * ネットワークアドレスリスト（文字列） .
	 *
	 * 設定された IP アドレスからのリクエストを「認証 OK」と判定する.
	 * 設定しない場合はチェックしない（ユーザエージェントの判定のみ）.
	 */
	private List<String> networkAddress;

	/**
	 * ネットワークアドレス設定
	 *
	 * @param networkAddress networkAddress への設定値.
	 */
	public void setNetworkAddress( List<String> networkAddress )
	{
		this.networkAddress = networkAddress;
	}

	// -------------------------------------------------------------------------

	/** ネットワークアドレスリスト */
	private List<Network> networkList;

	// -------------------------------------------------------------------------
	// 初期処理
	// -------------------------------------------------------------------------

	/**
	 * 初期処理.
	 * 文字列で指定されたネットワークアドレスリストを Network のリストに変換する
	 * @throws ServletException
	 *
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws ServletException {

		super.afterPropertiesSet();

		Assert.notNull( userId, "'userId' property must be set." );
		Assert.notNull( authName, "'authName' property must be set." );
		Assert.notNull( companyIdHeaderName, "'companyIdHeaderName' property must be set." );

		if ( StringUtils.isEmpty( userAgent ) ) userAgent = null;

		networkList = new ArrayList<>( networkAddress.size() );

		for ( String address : networkAddress ) {

			if ( ( address == null ) || StringUtils.isEmpty( address ) )  continue;

			try {

				Network network = Network.getInstance( address );
				networkList.add( network );
			}
			catch ( Exception ex ) {

				log.warn( MessageFormat.format(
					"Invalid network address(ignore). : [{0}] - {1}", address, ex ) );
			}
		}

		Assert.isTrue( ( userAgent != null ) || ( ! networkList.isEmpty() ),
			"ユーザエージェントと IP アドレスリストの両方を省略することはできません." );
	}

	// -------------------------------------------------------------------------
	// 認証処理
	// -------------------------------------------------------------------------

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter( ServletRequest req, ServletResponse res, FilterChain chain )
		throws IOException, ServletException
	{
		boolean isAuth = auth( req );

		chain.doFilter(req, res);

		if ( isAuth ) clearAuth(); // このフィルタでの認証は今回限り（リクエスト毎）
	}

	/**
	 * @param req HTTP リクエスト
	 * @return BOT と判定して認証情報を設定した時 true
	 *
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean auth( ServletRequest req ) throws IOException, ServletException
	{
		// OAuth 認証済みなら何もしない

		Authentication prev = SecurityContextHolder.getContext().getAuthentication();
		if ( prev != null ) return false;

		// BOT 判定

		if ( ! ( req instanceof HttpServletRequest ) ) return false;

		HttpServletRequest request = (HttpServletRequest)req;

		if ( userAgent != null )
		{
			String clientAgent = request.getHeader( "User-Agent" );

			if ( ! userAgent.equals( clientAgent ) ) return false; // ユーザエージェント不一致
		}

		if ( ! networkList.isEmpty() )
		{
			String clientAddr = request.getRemoteAddr();

			if ( ! isNetwork( clientAddr, networkList, false ) ) return false; // IP アドレス不一致
		}

		String companyId = request.getHeader( companyIdHeaderName );

		if ( StringUtils.isBlank( companyId ) ) return false; // 企業 ID なし

		Authentication auth = new PreAuthenticatedAuthenticationToken(
			userId, "N/A", AuthorityUtils.createAuthorityList( authName ) );

		SecurityContextHolder.getContext().setAuthentication( auth );

		// リカイアスライセンス情報設定しないのでパスワード復号化キーは設定しない
		CustomUser customUser = new CustomUser( companyId, userId, userId, Arrays.asList( new String[] { authName } ), null, null, null ) ;

		customUserHolder.setCustomUser( customUser ) ;

		return true;
	}

	/**
	 * 認証情報削除
	 */
	private void clearAuth()
	{
		SecurityContextHolder.getContext().setAuthentication( null );
		customUserHolder.setCustomUser( null );
	}

	// -------------------------------------------------------------------------

	/**
	 * 許可されたネットワークアドレスかどうか調べる
	 * @param invocation HTTP フィルタ
	 * @param config 設定属性コレクション
	 * @return チェック結果(true:許可、false:不許可)
	 */
	protected boolean isAllowedNetworkAddress(
		FilterInvocation invocation, Collection<ConfigAttribute> config ) {

		return isNetwork( invocation.getHttpRequest().getRemoteAddr(), networkList, false );
	}

	/**
	 * 指定したIPアドレスが，指定したネットワークの内部にあるかどうかを検査する．
	 *
	 * @param ipAddress IP アドレス
	 * @param networks ネットワークアドレス一覧
	 * @param canBorder ネットワークアドレスとブロードキャストアドレスも許可する場合に true
	 * @return ネットワークの内部にある場合は true，そうでなければ false
	 */
	protected boolean isNetwork( String ipAddress, Collection<Network> networks, boolean canBorder ) {

		if ( ( ipAddress == null ) || ( networks == null ) || networks.isEmpty() ) return false ;

		InetAddress addr = null;

		// 文字列の ipAddress をそのまま IPAddressChecker.isNetwork() に渡すと
		// for() のループごとに InetAddress.getByName() が呼び出しされてしまう
		// getByName() は場合によっては重い処理なので先に InetAddress に変換しておく.

		try {
			addr = InetAddress.getByName( ipAddress );
		} catch (UnknownHostException ex) {
			return false; // 扱えないアドレス
		}

		boolean ret = false;

		for ( Network network : networks ) {

			if ( ( ret = IPAddressChecker.isNetwork( addr, network, false ) ) ) { break; }
		}

		return ret;
	}
}
