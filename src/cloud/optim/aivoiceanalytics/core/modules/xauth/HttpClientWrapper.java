/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：HttpClientWrapper.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.xauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class HttpClientWrapper {

	private static final String ENCODE = "UTF-8";
	public int connectionTimeoutMs = 10 * 1000;
	public int responseTimeoutMs = 10 * 1000;

	/**
	 * Send Request
	 * @param url
	 * @param headers
	 * @param params
	 * @return
	 */
	public ResponseData post(String url, Map<String, String> headers, List<NameValuePair> params) {
		HttpPost method = new HttpPost(url);

		for (Entry<String, String> entry : headers.entrySet()) {
			method.addHeader(entry.getKey(), entry.getValue());
		}
		try {
			method.setEntity(new UrlEncodedFormEntity(params, ENCODE));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return sendRequest(method);
	}

	/**
	 *
	 * @param url
	 * @param headers
	 * @param params
	 * @return
	 */
	public ResponseData get(String url, Map<String, String> headers, List<NameValuePair> params) {
		HttpGet method = new HttpGet();

		for (Entry<String, String> entry : headers.entrySet()) {
			method.addHeader(entry.getKey(), entry.getValue());
		}
		try {
			URIBuilder builder = new URIBuilder(url);
			for (NameValuePair pair : params) {
				builder.addParameter(pair.getName(), pair.getValue());
			}
			method.setURI(builder.build());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return sendRequest(method);
	}

	/**Add
	 * Request with PUT method
	 * @param url
	 * @param headers
	 * @param xmlString
	 * @return
	 */
	public ResponseData put(String url, Map<String, String> headers, String xmlString) {
		HttpPut method = new HttpPut(url);

		for (Entry<String, String> entry : headers.entrySet()) {
			method.addHeader(entry.getKey(), entry.getValue());
		}
		try {
			method.setEntity(new StringEntity(xmlString, ENCODE));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return sendRequest(method);
	}

	/**
	 *
	 * @param method
	 * @return
	 */
	private ResponseData sendRequest(HttpRequestBase method) {

		// set timeout
		DefaultHttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeoutMs);
		HttpConnectionParams.setSoTimeout(httpParams, responseTimeoutMs);
		// set Retry times
		client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(3, false));

		ResponseData result = null;
		try {
			result = client.execute(method, new OAuthResponseHandler());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			method.releaseConnection();
		}
		return result;
	}

	/**
	 *
	 *
	 */
	public class ResponseData {
		/** Entity Body */
		public String entityBody;
		/** HTTP Status Code */
		public int status;

		public ResponseData(int status) {
			this.status = status;
		}
	}

	/**
	 * Response Handler
	 *
	 */
	public class OAuthResponseHandler implements ResponseHandler<ResponseData> {

		@Override
		public ResponseData handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {

			int status = response.getStatusLine().getStatusCode();
			ResponseData result = new ResponseData(status);
			if (status == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				result.entityBody = EntityUtils.toString(entity);
			}
			return result;
		}
	}
}
