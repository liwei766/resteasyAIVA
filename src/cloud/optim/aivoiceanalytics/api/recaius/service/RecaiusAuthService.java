/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：RecaiusAuthService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.recaius.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.api.recaius.result.AuthResult;
import cloud.optim.aivoiceanalytics.api.recaius.result.RecaiusResult;
import cloud.optim.aivoiceanalytics.api.recaius.util.Util;
import cloud.optim.aivoiceanalytics.api.util.JsonUtil;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;


/**
 * RecaiusAuthService実装.<br/>
 */
@Component
public class RecaiusAuthService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

	/** リカイアス認証URL */
	@Value( "${recaius.url.auth}" )
	private String recaiusAuthUrl;


	/** リカイアスセッション有効時間 */
	@Value( "${recaius.session.expiry.sec}" )
	private int sessionExpirySec;

	/** リカイアスセッション最大有効時間(秒) */
	@Value( "${recaius.session.expiry.maxsec}" )
	private int maxExpirySec;


	/**
	 * リカイアス認証処理を行いトークンを取得する
	 * @param type サービス種別
	 * @param serviceId サービスID
	 * @param password パスワード
	 * @param maxFlg 最大有効時間利用有無（1:有り、1以外:無）
	 * @return 認証トークン
	 * @throws Exception
	 */
	public String auth(String type, String serviceId, String password, int...maxFlg) throws Exception {
		// ヘッダーの生成
		Map<String, String> header = new HashMap<>();
		header.put("Content-Type", "application/json");

		// ボディ部の生成
		Map<String, String> serviceInfoMap = new HashMap<>();
		serviceInfoMap.put("service_id", serviceId);
		serviceInfoMap.put("password", password);

		Map<String, Object> body = new HashMap<>();
		body.put(type, serviceInfoMap);
		if ( maxFlg.length > 0 && maxFlg[0] == 1) {
			body.put("expiry_sec", maxExpirySec);
		} else {
			body.put("expiry_sec", sessionExpirySec);
		}

		// リカイアス認証APIを呼ぶ
		RecaiusResult response = Util.communicate(
			HttpMethod.POST, recaiusAuthUrl + "/tokens", header, JsonUtil.toJsonBytes(body));

		// エラーの場合は例外をスローする
		if(response.isAbendError()) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_AUTH_ABEND_ERROR, null, response.getErrorDetails() ));
		}
		if(response.isError()) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_AUTH_ERROR, null, response.getErrorDetails() ));
		}

		// 認証トークンを返す
		AuthResult authResponse = response.getResponse(AuthResult.class);
		return authResponse.getToken();
    }

	/**
	 * リカイアス認証トークン有効期限を延長する
	 * @param type サービス種別
	 * @param serviceId サービスID
	 * @param password パスワード
	 * @param token 認証トークン
	 * @return 認証トークン
	 * @throws Exception
	 */
	public String extention(String type, String serviceId, String password, String token) throws Exception {
		// ヘッダーの生成
		Map<String, String> header = new HashMap<>();
		header.put("Content-Type", "application/json");
		header.put("X-Token", token);

		// ボディ部の生成
		Map<String, String> serviceInfoMap = new HashMap<>();
		serviceInfoMap.put("service_id", serviceId);
		serviceInfoMap.put("password", password);

		Map<String, Object> body = new HashMap<>();
		body.put(type, serviceInfoMap);
		body.put("expiry_sec", sessionExpirySec);

		// リカイアス認証APIを呼ぶ
		RecaiusResult response = Util.communicate(
			HttpMethod.PUT, recaiusAuthUrl + "/tokens", header, JsonUtil.toJsonBytes(body));

		// エラーの場合は例外をスローする
		if(response.isAbendError()) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_AUTH_ABEND_ERROR, null, response.getErrorDetails() ));
		}
		if(response.isError()) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_AUTH_EXTENTION_TOKEN_ERROR, null, response.getErrorDetails() ));
		}

		// 認証トークンを返す
		AuthResult authResponse = response.getResponse(AuthResult.class);
		return authResponse.getToken();
    }

	/**
	 * リカイアス認証を切断する
	 * @return 認証トークン
	 * @throws Exception
	 */
	public void disconnect(String token) throws Exception {
		// ヘッダーの生成
		Map<String, String> header = new HashMap<>();
		header.put("X-Token", token);

		// リカイアストークン削除APIを呼ぶ
		RecaiusResult response = Util.communicate(HttpMethod.DELETE, recaiusAuthUrl + "/tokens", header, null);

		// エラーの場合は例外をスローする
		if(response.isAbendError()) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_AUTH_ABEND_ERROR, null, response.getErrorDetails() ));
		}
		if(response.isError()) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_AUTH_DELETE_TOKEN_ERROR, null, response.getErrorDetails() ));
		}
	}
}