/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：IPAddressChecker.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.common.utility;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * IP アドレスチェッククラス
 * @author imakoto
 */
public class IPAddressChecker {

	/**
	 * 接続元のIPアドレスが許可されたIPアドレスであるかチェックする
	 *
	 * @param ipaddr 接続元IPアドレス
	 * @param permitIpAddress 許可されたIPアドレス(改行区切りで複数指定可能。空行は無視する)
	 * @param canBorder ネットワークアドレス or ブロードキャストアドレスを許可する場合は true
	 *
	 * @return 所属している場合、許可されたIPアドレスが未設定の場合は true、そうでない場合は false
	 * @throws IllegalArgumentException エラー
	 */
	public static boolean isNetwork( String ipAddress, String permitIpAddress, boolean canBorder ) throws IllegalArgumentException {

		if (permitIpAddress == null || permitIpAddress.isEmpty()) return true;
		if (ipAddress == null || ipAddress.isEmpty()) return false;

		// InetAddressクラスのインスタンスを生成
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName( ipAddress );
		}
		catch ( Exception ex ) {
			// 生成できない場合はエラー
			return false;
		}

		// 改行コードで文字列を分割する
		String[] rowData = permitIpAddress.split("(\\r\\n|\\n|\\r)");
		boolean result = true;
		for (String each : rowData) {
			String text = each == null ? "" :  each.trim();

			// 空行は飛ばす
			if (text.isEmpty()) continue;

			// 一行でも設定がある場合はチェック結果が全てfalseの場合はfalseを返す
			result = false;

			// チェックする
			try {
				// 1件でもマッチするアドレスがあればtrueを返す
				if(isNetwork(addr, Network.getInstance(text), canBorder)) return true;
			} catch (Exception e) {}
		}

		// 空行のみの場合はチェックしないのでtrueを返す
		return result;
	}

	/**
	 * 指定されたホストがネットワークに所属しているかどうか調べる.
	 *
	 * @param ipaddr ホストIPアドレス
	 * @param network ネットワークアドレス
	 * @param canBorder ネットワークアドレス or ブロードキャストアドレスを許可する場合は true
	 *
	 * @return 所属している場合は true、そうでない場合は false
	 * @throws IllegalArgumentException エラー
	 */
	public static boolean isNetwork( String ipaddr, Network network, boolean canBorder ) throws IllegalArgumentException {

		if ( ( ipaddr == null ) || ( network == null ) ) return false;

		InetAddress addr = null;

		try {
			addr = InetAddress.getByName( ipaddr );
		}
		catch ( UnknownHostException ex ) {
			throw new IllegalArgumentException( "Invalid IP Address. : " + ipaddr, ex );
		}

		boolean ret = isNetwork( addr, network, canBorder );
		return ret;
	}

	/**
	 * 指定されたホストがネットワークに所属しているかどうか調べる.
	 * @param addr ホストIPアドレス
	 * @param network ネットワークアドレス
	 * @param canBorder ネットワークアドレス or ブロードキャストアドレスを許可する場合は true
	 *
	 * @return 所属している場合は true、そうでない場合は false
	 * @throws IllegalArgumentException エラー
	 */
	public static boolean isNetwork( InetAddress addr, Network network, boolean canBorder ) throws IllegalArgumentException {

		if ( ( addr == null ) || ( network == null ) ) return false;

		// プロトコル互換がない(＝IPv4とIPv6)場合は、次のエントリ
		if ( ! network.isCompatible( addr ) ) return false;

		// 指定IPアドレスに、各要素のサブネットを付けてチェックする。
		boolean ret = false;

		Network check = new Network( addr, network );
		ret = isNetwork( check, network, canBorder );
		return ret;
	}

	/**
	 * 指定されたホストがネットワークに所属しているかどうか調べる.
	 *
	 * @param host ホスト指定
	 * @param network ネットワーク指定
	 * @param canBorder ネットワークアドレス or ブロードキャストアドレスを許可する場合は true
	 *
	 * @return 所属している場合は true、そうでない場合は false
	 */
	private static boolean isNetwork( Network host, Network network, boolean canBorder ) {

		if ( ( host == null ) || ( network == null ) ) return false;

		return host.isSameNetwork( network ) && ! host.isInvalidAddress( canBorder );
	}
}
