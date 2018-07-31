/**
 * Copyright (C) 2006-2013, FUJISOFT. All rights reserved.
 * システム名：
 * ソースファイル名：NetworkAddressChannelProcessor.java
 *
 */
package cloud.optim.aivoiceanalytics.core.common.security;

import java.text.MessageFormat ;
import java.util.ArrayList ;
import java.util.Collection ;
import java.util.List ;

import org.apache.commons.lang3.StringUtils ;
import org.apache.commons.logging.Log ;
import org.apache.commons.logging.LogFactory ;
import org.springframework.security.access.ConfigAttribute ;
import org.springframework.security.web.FilterInvocation ;
import org.springframework.util.Assert ;

import cloud.optim.aivoiceanalytics.core.common.utility.Network;

/**
 * ネットワークアドレス チャンネルプロセッサ
 *
 * IPアドレスチェックを行い、許可されたIP以外であればリダイレクトを行う
 */
public class NetworkAddressChannelProcessor extends BaseNetworkAddressChannelProcessor {

	/** Log */
	private final Log log = LogFactory.getLog( this.getClass() );

	// -------------------------------------------------------------------------
	// プロパティ
	// -------------------------------------------------------------------------

	/** ネットワークアドレスリスト（文字列） */
	private List<String> networkAddress;

	/**
	 * ネットワークアドレス設定
	 * @param networkAddress The networkAddress to set.
	 */
	public void setNetworkAddress( List<String> networkAddress ) {
		this.networkAddress = networkAddress;
	}

	// -------------------------------------------------------------------------

	/** ネットワークアドレスリスト */
	private List<Network> networkList;

	// -------------------------------------------------------------------------

	/**
	 * 初期処理.
	 * 文字列で指定されたネットワークアドレスリストを Network のリストに変換する
	 *
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

		super.afterPropertiesSet();
		Assert.notNull( networkAddress, "networkAddress required." );

		networkList = new ArrayList<>( networkAddress.size() );

		for ( String address : networkAddress ) {

			if ( ( address == null ) || StringUtils.isEmpty( address ) ) {

				log.warn( MessageFormat.format(
					"Empty address(ignore). : [{0}]", address ) );
				continue;
			}

			try {

				Network network = Network.getInstance( address );
				networkList.add( network );
			}
			catch ( Exception ex ) {

				log.warn( MessageFormat.format(
					"Invalid network address(ignore). : [{0}] - {1}", address, ex ) );
			}
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 許可されたネットワークアドレスかどうかチェック
	 * @param invocation HTTP フィルタ
	 * @param config 設定属性コレクション
	 * @return チェック結果(true:許可、false:不許可)
	 */
	@Override
	protected boolean isAllowedNetworkAddress(
		FilterInvocation invocation, Collection<ConfigAttribute> config ) {

		return isNetwork( invocation.getHttpRequest().getRemoteAddr(), networkList );
	}
}
