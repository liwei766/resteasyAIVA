/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：LoginUtility.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.loginutil;

import java.io.Serializable ;
import java.util.ArrayList ;
import java.util.HashSet;
import java.util.List;
import java.util.Set ;

import javax.annotation.Resource ;

import org.springframework.stereotype.Component ;

import cloud.optim.aivoiceanalytics.core.Role;

/**
 * 現在のログインユーザ情報を取得するためのユーティリティクラス.
 *
 * @author mayon
 */
@Component
public class LoginUtility implements Serializable {

	/** シリアルバージョン番号 */
	private static final long serialVersionUID = 1L ;

	/** UNKNOWN  */
	public static final String UNKNOWN = "unknown" ;

	/** UNKNOWN_OBJECT	*/
	public static final Object UNKNOWN_OBJECT = "unknown" ;

	/** 拡張ユーザ情報 */
	@Resource private CustomUserHolder customUserHolder ;

	// -------------------------------------------------------------------------
	// ログインユーザ名関連
	// -------------------------------------------------------------------------

	/**
	 * 現在ログイン中のユーザ名を取得する.
	 *
	 * @return 現在のログインユーザ名.
	 */
	public String getUsername() {
		return getCustomUser().getUserId();
	}

	/**
	 * 現在ログイン中のユーザ名を取得する（getUsername() の別名です）.
	 *
	 * @return 現在のログインユーザ名.
	 */
	public String getLoginName() {

		return getUsername() ;
	}


	// -------------------------------------------------------------------------
	// ログインユーザの権限関連
	// -------------------------------------------------------------------------

	/**
	 * 保有している権限 ID の Set を取得する.
	 *
	 * @return 保有権限 ID の一覧
	 */
	public Set<String> getAuthorities()
	{
		Set<String> authSet = new HashSet<String>() ;
		CustomUser customUser = getCustomUser() ;

		if (customUser.getAuthList() == null || customUser.getAuthList().isEmpty()) {
			authSet.add( Role.ANONYMOUS.getRole() ) ;
		} else {
			authSet.addAll(customUser.getAuthList());
		}
		return authSet ;
	}

	// -------------------------------------------------------------------------
	// プロジェクトごとの追加情報関連
	// -------------------------------------------------------------------------

	/**
	 * 拡張ユーザ情報取得
	 *
	 * @return 拡張ユーザ情報
	 */
	public CustomUser getCustomUser() {

		CustomUser customUser = customUserHolder.getCustomUser() ;

		if ( customUser == null ) {

			// 未ログイン状態での初回アクセス
			List<String> authList = new ArrayList<String>() ;
			authList.add( Role.ANONYMOUS.getRole() ) ;
			// リカイアスライセンス情報設定しないのでパスワード復号化キーは設定しない
			customUser = new CustomUser(UNKNOWN, UNKNOWN, UNKNOWN, authList, null, null);
			customUserHolder.setCustomUser( customUser ) ;
		}

		return customUser ;
	}

	/**
	 * 拡張ユーザ情報設定
	 * @param customUser 拡張ユーザ情報
	 */
	public void setCustomUser(CustomUser customUser) {
		customUserHolder.setCustomUser(customUser) ;
	}
}
