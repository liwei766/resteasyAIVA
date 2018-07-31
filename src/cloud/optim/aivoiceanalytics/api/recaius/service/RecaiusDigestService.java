/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：RecaiusDigestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.recaius.service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.api.recaius.result.RecaiusResult;
import cloud.optim.aivoiceanalytics.api.recaius.util.Util;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;


/**
 * RecaiusDigestService実装.<br/>
 */
@Component
public class RecaiusDigestService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

	/** リカイアス要約URL */
	@Value( "${recaius.url.digest}" )
	private String recaiusDigestUrl;

	/** リカイアス要約ユーザ名 */
	@Value( "${recaius.digest.user.name}" )
	private String digestUserName;

	/** リカイアス要約結果件数？ */
	@Value( "${recaius.digest.count}" )
	private String count;


	/**
	 * 音声データをリカイアスへ送信する
	 * @return 解析結果がある場合は解析結果を返す
	 * @throws Exception
	 */
	public RecaiusResult digest(String token, String text) throws Exception {
		// ヘッダーの生成
		String boundary = Util.generateBoundary();
		Map<String, String> header = new HashMap<>();
		header.put("X-Token", token);
		header.put("X-User", digestUserName);
		header.put("X-Http-Method-Override", "GET");
		header.put("Content-Type", "multipart/form-data; boundary=" + boundary);

		// ボディ部の生成
		boundary = "--" + boundary + "\r\n";

		StringBuilder body = new StringBuilder();
		body
			.append(boundary)
			.append("Content-Disposition: form-data; name=\"text\"\r\n\r\n")
			.append(getBodyData(text))
			.append(boundary)
			.append("Content-Disposition: form-data; name=\"count\"\r\n")
			.append(count).append("\r\n")
			.append(boundary);

		// リカイアス要約APIを呼ぶ
		RecaiusResult response = Util.communicate(
			HttpMethod.GET, recaiusDigestUrl + "/texts/ranks", header, body.toString().getBytes());

		// エラーの場合は例外をスローする
		if(response.isAbendError()) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_DIGEST_ABEND_ERROR, null, response.getErrorDetails() ));
		}
		if(response.isError()) {
			throw new RestException(new RestResult(ResponseCode.RECAIUS_DIGEST_ERROR, null, response.getErrorDetails() ));
		}

		return response;
}

	/**
	 *
	 * @param text 要約対象テキスト
	 * @return URLエンコードした文字列
	 * @throws Exception
	 */
	private StringBuilder getBodyData(String text) throws Exception {
		StringBuilder result = new StringBuilder();

		// 200文字づつ切り出して連結する
		Matcher matcher = Pattern.compile("[\\s\\S]{1,200}").matcher(text);
		while (matcher.find()) {
			result.append(URLEncoder.encode(matcher.group(), "UTF-8"));
			result.append("\r\n");
		}

		return result;
	}
}