/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：RecaiusResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.recaius.result;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import cloud.optim.aivoiceanalytics.api.util.JsonUtil;

/**
 * リカイアス通信結果クラス
 */
public class RecaiusResult {

	/** レスポンスコード */
	private int responseCode;

	/** 利用時間(ms) */
	private long time;

	/** レスポンスボディ */
	private String responseBody;

	/**
	 * @return responseCode
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode セットする responseCode
	 */
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @return time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time セットする time
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * @return responseBody
	 */
	public String getResponseBody() {
		return responseBody;
	}

	/**
	 * @param responseBody セットする responseBody
	 */
	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public <T> T getResponse(Class<T> type) throws Exception {
		return JsonUtil.toObject(this.responseBody, type);
	}

	public <T> T getResponse(TypeReference<T> type) throws Exception {
		if (StringUtils.isEmpty(this.responseBody)) return null;
		return JsonUtil.toObject(this.responseBody, type);
	}

	/**
	 * レスポンスコードが正常系のコードかチェックする
	 * @return 正常系のコードの場合：true、それ以外の場合：false
	 */
	public boolean isSuccess() {
		return responseCode >= HttpURLConnection.HTTP_OK && responseCode <= HttpURLConnection.HTTP_PARTIAL;
	}

	/**
	 * レスポンスコードが異常系のコードかチェックする
	 * @return 異常系のコードの場合：true、それ以外の場合：false
	 */
	public boolean isError() {
		return responseCode < HttpURLConnection.HTTP_OK || responseCode > HttpURLConnection.HTTP_PARTIAL;
	}


	/**
	 * レスポンスコードが復帰不能な異常系ののコードかチェックする
	 * @return 復帰不能な異常系のコードの場合：true、それ以外の場合：false
	 */
	public boolean isAbendError() {
		if (HttpURLConnection.HTTP_FORBIDDEN == responseCode) return true;
		if (HttpURLConnection.HTTP_INTERNAL_ERROR == responseCode) return true;
		return false;
	}


	public List<String> getErrorDetails () throws Exception {
		ErrorResult errorResult = getResponse(ErrorResult.class);
		List<String> result = new ArrayList<>();
		result.add(String.valueOf(responseCode));
		result.add(String.valueOf(errorResult.getCode()));
		result.add(errorResult.getMessage());
		result.add(errorResult.getMore_info());
		result.add(errorResult.getTimestamp());
		return result;
	}
}
