/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：Network.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.common.utility;

import java.net.Inet4Address ;
import java.net.Inet6Address ;
import java.net.InetAddress;
import java.net.UnknownHostException ;
import java.text.MessageFormat ;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * IPアドレス、ネットワークアドレス、ネットマスク、ブロードキャストアドレス
 * @author imakoto
 */
public class Network {

	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/** 本クラスの文字列表現 */
	private String  toString;

	/** IPアドレス */
	private String   ipAddrString;
	/** ネットワークアドレス or ブロードキャストアドレスでないフラグ */
	private boolean isHost;

	/** ネットワークアドレス */
	private byte[]  networkArray;
	/** ネットマスク */
	private byte[]  netmaskArray;
	/** ブロードキャストアドレス */
	private byte[]  broadcastArray;

	/**
	 * CIDR 表記のアドレスから Network オブジェクトを生成する.
	 *
	 * @param cidrStr CIDR 表記のネットワークアドレス
	 * @return Network オブジェクト
	 *
	 * @throws UnknownHostException FQDN が指定されたが名前解決できない場合
	 * @throws IllegalArgumentException ネットワークアドレスが IPv4、IPv6 のどちらでもない
	 */
	public static Network getInstance( String cidrStr )
		throws IllegalArgumentException, UnknownHostException {

		// ネットワーク部のビット数指定の有無
		boolean cidrFound = ( cidrStr.indexOf( "/" ) >= 0 );

		// ----- 指定された CIDR 表記を IP アドレスとネットワーク部のビット数に分割

		String address, netBitsStr;

		if ( cidrFound ) {

			// ビット数指定あり→ネットワークとネットワーク部のビット数に分ける

			address = cidrStr.substring( 0, cidrStr.indexOf( "/" ) );
			netBitsStr = cidrStr.substring( cidrStr.indexOf( "/" ) + 1 );
		}
		else {

			// ビット数指定なし→ネットワーク、ネットマスクは未定義

			address = cidrStr;
			netBitsStr = "";
		}

		// ----- アドレス判定（IPv4／IPv6）とビット数判定

		InetAddress ipAddress = InetAddress.getByName( address );

		int netBits, bits;

		if ( ipAddress instanceof Inet4Address ) {

			// IPv4 アドレス(CIDR = 0～32)

			bits = 32;
			netBits = ( cidrFound ) ? Integer.parseInt( netBitsStr ) : bits;

			if ( netBits < 0 || netBits > 32 ) {
				throw new IllegalArgumentException(
					MessageFormat.format(
						"CIDR is invalid. ( number of bits of network part : {0,number,#} )", netBits ) );
			}
		}
		else if ( ipAddress instanceof Inet6Address ) {

			// IPv6 アドレス(CIDR = 0～128)

			bits = 128;
			netBits = ( cidrFound ) ? Integer.parseInt( netBitsStr ) : bits;
			if ( netBits < 0 || netBits > 128 ) {
				throw new IllegalArgumentException(
					MessageFormat.format(
						"CIDR is invalid ( number of bits of network part : {0,number,#} )", netBits ) );
			}
		}
		else {

			// 未対応（以後の Java バージョンにより新たな型を検出）

			throw new IllegalArgumentException(
				"Specified address is not supported. : " + cidrStr );
		}

		return new Network( ipAddress.getAddress(), netBits, bits );
	}

	/**
	 * コンストラクタ
	 * @param networkArray	IPアドレス値
	 * @param cidr			CIDR値
	 * @param bits			アドレスbit数
	 */
	public Network(byte[] networkArray, int cidr, int bits) {
		this(networkArray, getCIDRNumber(cidr, bits));
		this.isHost = (cidr == bits);
	}

	/**
	 * コンストラクタ
	 * @param addr			IPアドレス(InetAddress)
	 * @param original		複製元Network
	 */
	public Network(InetAddress addr, Network original) {
		this(addr.getAddress(), original.netmaskArray);
		this.isHost = original.isHost;
		this.toString = addr.toString();
	}

	/**
	 * コンストラクタ
	 * @param networkArray	IPアドレス
	 * @param netmaskArray	ネットマスク
	 */
	private Network(byte[] networkArray, byte[] netmaskArray) {
		if (networkArray.length != netmaskArray.length) { throw new IllegalArgumentException("Network bits and Netmask bits are different."); }

		// 引数は最初に保持しておく
		this.ipAddrString = Hex.encodeHexString(networkArray);

		byte[] broadcastArray = new byte[networkArray.length];
		for (int i = 0; i < networkArray.length; i++) {
			broadcastArray[i] = (byte)(networkArray[i] | (netmaskArray[i] ^ 0xFF));
			networkArray[i] &= netmaskArray[i];
		}

		this.broadcastArray = broadcastArray;
		this.networkArray = networkArray;
		this.netmaskArray = netmaskArray;

		try {
			this.toString =
				InetAddress.getByAddress( networkArray ).getHostAddress() + "/" +
				InetAddress.getByAddress( netmaskArray ).getHostAddress();
		}
		catch( Exception ex ) { if( log.isWarnEnabled() ) log.warn(ex); }
	}

	/**
	 * CIDRをIPアドレス値に変換
	 * 例：24 ⇒ [-1, -1, -1, 0] or [-1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
	 * @param cidr		CIDR値
	 * @param bits		アドレスbit数
	 * @return			IPアドレス値
	 */
	private static byte[] getCIDRNumber(int cidr, int bits) {
		byte[] ret = new byte[bits / 8];
		StringBuffer sb = new StringBuffer(StringUtils.repeat("0", bits));
		for (int i = 0; i < cidr; i++) {
			sb.setCharAt(i, '1');
		}
		for (int i = 0, j = 0; i < ret.length; i++, j += 8) {
			ret[i] = Short.valueOf(sb.substring(j, j + 8), 2).byteValue();
		}
		return ret;
	}

	/**
	 * ネットワークアドレスを取得
	 * @return		ネットワークアドレス
	 */
	public byte[] getNetworkArray() {
		return networkArray;
	}

	/**
	 * 指定した Network が同一ネットワークにいるかどうか
	 * @param network Network
	 * @return	同一ネットワークにいる場合は true
	 */
	public boolean isSameNetwork(Network network) {
		String networkStr1 = Hex.encodeHexString(this.networkArray);
		String networkStr2 = Hex.encodeHexString(network.getNetworkArray());
		return networkStr1.equals(networkStr2);
	}

	/**
	 * この Network がIPアドレス指定として不当かどうか
	 * @param canBorder		ネットワークアドレス or ブロードキャストアドレスも指定可の場合は true
	 * @return				許可されたアドレスの場合は false
	 */
	public boolean isInvalidAddress(boolean canBorder) {
		if (canBorder || isHost) { return false; }

		String networkStr1 = Hex.encodeHexString(this.networkArray);
		String networkStr2 = Hex.encodeHexString(this.broadcastArray);
		return networkStr1.equals(ipAddrString) || networkStr1.equals(networkStr2);
	}

	/**
	 * 指定されたアドレスがこの Network と同じプロトコルかどうか
	 * @param addr		IPアドレス(InetAddress)
	 * @return			同じプロトコルの場合は true
	 */
	public boolean isCompatible(InetAddress addr) {
		byte[] addrArray;
		return (addr != null && (addrArray = addr.getAddress()) != null && addrArray.length == this.networkArray.length);
	}

	/**
	 * 文字列表現
	 * @return ネットワークアドレス / ネットマスク
	 */
	@Override
	public String toString() {
		if (this.toString != null) {
			return this.toString;
		}
		return Hex.encodeHexString(this.networkArray) + "/" + Hex.encodeHexString(this.netmaskArray);
	}
}
