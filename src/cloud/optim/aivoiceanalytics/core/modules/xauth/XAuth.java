/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：XAuth.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.xauth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import cloud.optim.aivoiceanalytics.core.modules.xauth.HttpClientWrapper.ResponseData;

public abstract class XAuth {

	public static String OAuthVersion = "1.0";
	public static final String HMACSHA1SignatureType = "HMAC-SHA1";
	public static final String SignatureAlgorithm = "HmacSHA1";

	public static final String OAuthConsumerKeyKey = "oauth_consumer_key";
	public static final String OAuthVersionKey = "oauth_version";
	public static final String OAuthSignatureMethodKey = "oauth_signature_method";
	public static final String OAuthSignatureKey = "oauth_signature";
	public static final String OAuthTimestampKey = "oauth_timestamp";
	public static final String OAuthNonceKey = "oauth_nonce";
	public static final String XAuthUsername = "x_auth_login";
	public static final String XAuthPassword = "x_auth_password";

	public static final String OAuthTokenKey = "oauth_token";
	public static final String OAuthTokenSecretKey = "oauth_token_secret";;
	public static final String XAuthCompany = "x_auth_company";

	public int connectionTimeoutMs = 10 * 1000;
	public int responseTimeoutMs = 10 * 1000;

	protected String url = null;
	protected String method = "POST";

	protected String oAuthConsumerToken;
	protected String oAuthConsumerSecret;

	protected String xauthUsername = null;
	protected String xauthPassword = null;
	protected String xauthCompanyCode = null;

	protected String oAuthToken;
	protected String oAuthTokenSecret;

	protected Map<String, String> headers = new HashMap<String, String>();
	protected List<NameValuePair> params = new ArrayList<NameValuePair>();;
	protected List<NameValuePair> data = new ArrayList<NameValuePair>();
	//send entity
	protected String xmlString;

	/** Authorization Request Header */
	static final String Authorization = "OAuth oauth_consumer_key=\"%s\", oauth_nonce=\"%s\", oauth_signature=\"%s\", oauth_signature_method=\"%s\", oauth_timestamp=\"%s\", oauth_version=\"%s\"";
	static final String AuthorizationWithToken = "OAuth oauth_consumer_key=\"%s\", oauth_nonce=\"%s\", oauth_signature=\"%s\", oauth_signature_method=\"%s\", oauth_timestamp=\"%s\", oauth_token=\"%s\", oauth_version=\"%s\"";

	//static final String PATH_FORMAT = "%s%s";

	/**
	 * Set xAuth Pamateters
	 * @param params
	 */
	public void setParameter(List<NameValuePair> params) {
		this.params = params;
	}

	/**
	 * Send Request
	 * @return {@link ResponseData}
	 */
	public ResponseData request() {
		String nonce = UUID.randomUUID().toString();
		String timestamp = String.valueOf(new Date().getTime() / 1000);

		params.add(new BasicNameValuePair(OAuthConsumerKeyKey, oAuthConsumerToken));
		params.add(new BasicNameValuePair(OAuthNonceKey, nonce));
		params.add(new BasicNameValuePair(OAuthSignatureMethodKey, HMACSHA1SignatureType));
		params.add(new BasicNameValuePair(OAuthTimestampKey, timestamp));
		params.add(new BasicNameValuePair(OAuthVersionKey, OAuthVersion));

		if ((oAuthToken != null && !("").equals(oAuthToken) )) {
			params.add(new BasicNameValuePair(OAuthTokenKey, oAuthToken));
		}

		params.addAll(data);
		List<String> list = urlVariablesToList(params);
		String signature = generateSignature(method, url, list, oAuthConsumerToken, oAuthConsumerSecret);
		params.add(new BasicNameValuePair(OAuthSignatureKey, signature));

		String authorization = null;

		if ((oAuthToken == null || ("").equals(oAuthToken) )) {
			authorization = String.format(Authorization, oAuthConsumerToken, nonce, encodeURL(signature),
					HMACSHA1SignatureType, timestamp, OAuthVersion);
		} else {
			authorization = String.format(AuthorizationWithToken, oAuthConsumerToken, nonce, encodeURL(signature),
					HMACSHA1SignatureType, timestamp, oAuthToken, OAuthVersion);
		}
		headers.put("Authorization", authorization);
		headers.put("Connection", "close");

		HttpClientWrapper wrapper = new HttpClientWrapper();
		wrapper.connectionTimeoutMs = connectionTimeoutMs;
		wrapper.responseTimeoutMs = responseTimeoutMs;

		ResponseData responseData = null;

		if("POST".equals(method)) {
			responseData = wrapper.post(url, headers, data);
		} else if("PUT".equals(method)){
			headers.put("Content-type", "application/xml");
			responseData = wrapper.put(url, headers, xmlString);
		} else {
			headers.put("Accept", "application/xml");
			responseData = wrapper.get(url, headers, data);
		}
		return responseData;
	}

	/**
	 * Generate Signature
	 * @param method
	 * @param url
	 * @param list
	 * @param consumerKey
	 * @param consumerSceret
	 * @return Sigunature
	 */
	private String generateSignature(String method,
			String url,
			List<String> list,
			String consumerKey, String consumerSceret) {

		String sigBase = new StringBuilder()
				.append(encodeURL(method))
				.append("&")
				.append(encodeURL(url))
				.append("&")
				.append(encodeURL(XAuthUtil.join(list, "&")))
				.toString();
		String sigKeyBase = new StringBuilder()
				.append(encodeURL(consumerSceret))
				.append("&")
				.append(encodeURL(oAuthTokenSecret)).toString();

		byte[] signature = null;

			try {
				SecretKeySpec key = new SecretKeySpec(sigKeyBase.getBytes(),
						SignatureAlgorithm);
				Mac mac = Mac.getInstance(SignatureAlgorithm);
				mac.init(key);
				signature = mac.doFinal(sigBase.getBytes());
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return Base64.encode(signature);
	}

	/**
	 *
	 * @param params
	 * @return
	 */
	private static List<String> urlVariablesToList(List<NameValuePair> params) {
		List<String> arr = new ArrayList<String>(params.size());
		for (NameValuePair pair : params) {
			String queryParameter = new StringBuilder()
					.append(pair.getName())
					.append("=")
					.append(encodeURL(pair.getValue()))
					.toString();
			arr.add(queryParameter);
		}
		Collections.sort(arr);
		return arr;
	}

	/**
	*
	* @param str
	* @return
	*/
	private static String encodeURL(String str) {
		String encord = null;
		try {
			if (str != null && !("").equals(str)) {
				encord = URLEncoder.encode(str, "UTF-8");
			}
		} catch (UnsupportedEncodingException ignore) {

		}
		return encord;
	}
}

