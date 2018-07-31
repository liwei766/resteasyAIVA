/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：Util.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.recaius.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.http.HttpMethod;

import cloud.optim.aivoiceanalytics.api.recaius.SpeechResultType;
import cloud.optim.aivoiceanalytics.api.recaius.result.RecaiusResult;
import cloud.optim.aivoiceanalytics.api.recaius.result.SpeechNBestResult;
import cloud.optim.aivoiceanalytics.api.recaius.result.SpeechNBestResultDetail;

/**
 * リカイアス認証処理結果クラス
 */
public class Util {

	/** Boundary生成用文字列 */
	private static final char[] CHRACTARS = {
			'1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
	};

	/**
	 * バウンダリ文字列を生成する
	 * @return バウンダリ文字列
	 */
	public static String generateBoundary() {
			StringBuilder result = new StringBuilder();
			Random rand = new Random();
			// 36文字のランダムな英数記号の文字列を生成する
			for(int i = 0; i < 36; i++) {
				result.append(CHRACTARS[rand.nextInt(CHRACTARS.length)]);
	    }
	    return result.toString();
	}

	/**
	 * XXX 未使用
	 * リカイアスの解析結果から結果を抽出する
	 * @param body リカイアス解析結果
	 * @return
	 */
	public static List<SpeechNBestResultDetail> extractResult(List<SpeechNBestResult> speechResult) {
		List<SpeechNBestResultDetail> result = new ArrayList<>();
		if (speechResult == null || speechResult.isEmpty()) return result;
		for(SpeechNBestResult each : speechResult) {
			if (!SpeechResultType.RESULT.toString().equals(each.getType())) continue;
			result.add(each.getResultDetail());
		}
		return result;
	}

	/**
	 * 指定されたURLに対して指定メソッド、リクエストヘッダ、リクエストボディを設定してHTTP通信を行いレスポンスを返す
	 * @param method HTTPメソッド
	 * @param url URL
	 * @param headers リクエストヘッダ
	 * @param body リクエストボディ
	 * @return 通信結果のオブジェクト
	 * @throws Exception
	 */
	public static RecaiusResult communicate(HttpMethod method, String url, Map<String, String> headers, byte[] body) throws Exception {
		HttpURLConnection connection = null;
		try {

			// リカイアス利用時間計測開始
			Long time = System.currentTimeMillis();

			// コネクションを開く
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod(method.toString());

			// ヘッダーの設定
			for(Map.Entry<String, String> e : headers.entrySet()) {
				connection.setRequestProperty(e.getKey(), e.getValue());
			}

			// ボディがあれば書き込む
			if(body != null) {
				try(DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
					writer.write(body);
					writer.flush();
				}
			}

			// レスポンスを取得する
			int responseCode = connection.getResponseCode();

			// リカイアス利用時間計測終了
			time = System.currentTimeMillis() - time;

			// 正常終了の場合はレスポンスボディを取得する
			RecaiusResult result = new RecaiusResult();
			result.setResponseCode(responseCode);
			result.setTime(time);
			result.setResponseBody(readResponse(result.isSuccess() ?  connection.getInputStream() : connection.getErrorStream()));

			return result;

		} finally {
			// URLコネクションを閉じる
			if (connection != null) connection.disconnect();
		}
	}

	/**
	 * 入力ストリームから文字列を読む
	 * @param input 入力ストリーム
	 * @return 読みだした文字列
	 * @throws Exception
	 */
	private static String readResponse(InputStream input) throws Exception {
		StringBuilder result = new StringBuilder();
		// 正常終了の場合はInputStreamとって、異常終了の場合はErrorStreamを取得する？
		try (
			InputStreamReader inputReader = new InputStreamReader(input, StandardCharsets.UTF_8);
			BufferedReader reader = new BufferedReader(inputReader)) {
			String line = null;
			while ((line = reader.readLine()) != null) result.append(line);
		}
		return result.toString();
	}
}
