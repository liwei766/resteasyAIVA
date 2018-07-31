/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：SessionManager.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.session;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.springframework.stereotype.Component;

/**
 * 多重ログイン制御用の情報を持つクラス
 */
@Component
public class SessionManager {

	/** セッション管理マップ キー：企業ID-ユーザID、値：セッションID */
	private final BidiMap sessionMap = new DualHashBidiMap();

	/**
	 * ログイン中のユーザかチェックする
	 * ログイン中でない場合はセッション管理マップに登録する
	 * @param companyId 企業ID
	 * @param userId ユーザID
	 * @param session セッション
	 * @return 異なるセッションIDでログイン中の場合true、それ以外false
	 */
	public boolean isLogined(String companyId, String userId, HttpSession session) {
		// 企業IDとユーザIDでキーを生成する
		String key = String.join("-", companyId, userId);

		// 生成したキーでセッションIDを取得する
		HttpSession loginedSession = (HttpSession) sessionMap.get(key);
		String loginedSessionId = loginedSession == null ? null : loginedSession.getId();
		String sessionId = session == null ? null : session.getId();

		// ----- ログイン済みかチェックする

		// keyで取得したセッションIDが引数のセッションIDと異なる場合はログイン中
		if (loginedSessionId != null && !loginedSessionId.equals(sessionId)) return true;

		// 同一セッションIDでログイン中の場合はマップへの追加処理を行わない
		if (loginedSessionId != null && loginedSessionId.equals(sessionId)) return false;

		// セッション管理マップへログイン情報を追加する
		sessionMap.put(key, session);
		return false;
	}

	/**
	 * ユーザ情報よりセッション情報を破棄し新しいセッションをセットする
	 * @param companyId
	 * @param userId
	 * @return
	 */
	public void forceLogin(String companyId, String userId, HttpSession session) {
		// 企業IDとユーザIDでキーを生成する
		String key = String.join("-", companyId, userId);

		// 古いセッションを取得する
		HttpSession oldSession = (HttpSession) sessionMap.get(key);

		// 新しいセッションをセットする
		sessionMap.put(key, session);

		// 古いセッションを破棄する
		if (oldSession != null) oldSession.invalidate();
	}

	/**
	 * セッションIDでセッション管理マップからログイン情報を削除する
	 * (セッション破棄時のイベントで呼ばれる)
	 * @param session セッション
	 */
	public void removeBySession(HttpSession session) {
		sessionMap.removeValue(session);
	}
}
