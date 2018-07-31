/**
 * Copyright (C) 2006-2011, FUJISOFT. All rights reserved.
 * システム名：
 * ソースファイル名：BaseNetworkAddressChannelProcessor.java
 *
 */
package cloud.optim.aivoiceanalytics.core.common.security;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse ;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.channel.ChannelProcessor;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.util.Assert;

import cloud.optim.aivoiceanalytics.core.common.utility.IPAddressChecker;
import cloud.optim.aivoiceanalytics.core.common.utility.Network;

/**
 * ネットワークアドレス チャンネルプロセッサ ベースクラス
 *
 * IPアドレスチェックを行い、許可されたIP以外であればリダイレクトを行う
 */
public abstract class BaseNetworkAddressChannelProcessor implements InitializingBean, ChannelProcessor {

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.hasLength(remoteipKeyword, "remoteipKeyword required");
		Assert.notNull(redirectUrl, "entryPoint required");
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.securechannel.ChannelProcessor#supports(org.springframework.security.ConfigAttribute)
	 */
	@Override
	public boolean supports(ConfigAttribute attribute) {

		return ((attribute != null) && (attribute.getAttribute() != null) && attribute.getAttribute().equals(getRemoteipKeyword()));
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.web.access.channel.ChannelProcessor#decide(org.springframework.security.web.FilterInvocation, java.util.Collection)
	 */
	@Override
	public void decide(FilterInvocation invocation,
			Collection<ConfigAttribute> config) throws IOException,
			ServletException {

		if( (invocation == null) || (config == null) ){
			throw new IllegalArgumentException("Nulls cannot be provided");
		}

		Iterator<ConfigAttribute> iter = config.iterator();

		while (iter.hasNext()) {
			ConfigAttribute attribute = iter.next();

			//リモートIPキーワードに一致するかどうかチェック
			if(!supports(attribute)){
				continue;//一致しなければ次を見に行く
			}

			//許可されたネットワークアドレスかどうかチェック
			boolean allowedNetworkAddressFlg = isAllowedNetworkAddress(invocation, config);
			if (!allowedNetworkAddressFlg) {
				//許可されていなければリダイレクトURLにジャンプさせる

				HttpServletRequest  request  = invocation.getHttpRequest();
				HttpServletResponse response = invocation.getHttpResponse();
				String url = response.encodeRedirectURL(request.getContextPath() + redirectUrl);
				response.sendRedirect(url);
			}
		}
	}

	/**
	 * 許可されたネットワークアドレスかどうかチェック(権限なし)
	 * @param invocation HTTP フィルタ
	 * @param config 設定属性コレクション
	 * @return チェック結果(true:許可、false:不許可)
	 */
	protected abstract boolean isAllowedNetworkAddress(
			FilterInvocation invocation,
			Collection<ConfigAttribute> config);

	// -------------------------------------------------------------------------
	// ユーティリティメソッド
	// -------------------------------------------------------------------------

	/**
	 * 指定したIPアドレスが，指定したネットワークの内部にあるかどうかを検査する．
	 *
	 * @param ipAddress IP アドレス
	 * @param networks ネットワークアドレス一覧
	 * @return ネットワークの内部にある場合は true，そうでなければ false
	 */
	protected boolean isNetwork( String ipAddress, Collection<Network> networks ) {

		return isNetwork( ipAddress, networks, false );
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

	// -------------------------------------------------------------------------

	/**
	 * 認証済み権限情報 取得.
	 * @param session セッション
	 * @return 認証済み権限情報.
	 */
	@SuppressWarnings( "unchecked" )
	protected Collection <GrantedAuthority> getGrantedAuthorityCollection(HttpSession session){
		Collection <GrantedAuthority> grAuthClct = null;

		if(session != null){
			SecurityContext sc = (SecurityContext)session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
			if(sc != null){
				grAuthClct = (Collection<GrantedAuthority>)sc.getAuthentication().getAuthorities();
			}
		}

		return grAuthClct;
	}

	// -------------------------------------------------------------------------
	// アクセサメソッド
	// -------------------------------------------------------------------------

	/** リダイレクトURL */
	private String redirectUrl = "";

	/**
	 * リダイレクトURL 設定.
	 * @param redirectUrl リダイレクトURL.
	 */
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	/**
	 * リダイレクトURL 取得.
	 * @return リダイレクトURL.
	 */
	public String getRedirectUrl() {
		return redirectUrl;
	}

	// -------------------------------------------------------------------------
	/** リモートIPキーワード */
	private String remoteipKeyword = "REQUIRES_NETWORK_ADDRESS_CHANNEL";

	/**
	 * リモートIPキーワード 取得.
	 * @param secureKeyword リモートIPキーワード.
	 */
	public void setRemoteipKeyword(String secureKeyword) {
		this.remoteipKeyword = secureKeyword;
	}

	/**
	 * リモートIPキーワード 設定.
	 * @return リモートIPキーワード.
	 */
	public String getRemoteipKeyword() {
		return remoteipKeyword;
	}
}
