/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：OptimalBizService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.xauth;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpStatus;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;
import cloud.optim.aivoiceanalytics.core.modules.xauth.HttpClientWrapper.ResponseData;

public class OptimalBizService extends XAuth{

	/** コンストラクタ */
	public OptimalBizService(String url, String method, String xAuthConsumerToken, String xAuthConsumerSecret, String xAuthToken, String xAuthTokenKey) {

		this.url  = url;
		this.method = method;

		this.oAuthConsumerToken = xAuthConsumerToken;
		this.oAuthConsumerSecret = xAuthConsumerSecret;

		this.oAuthToken = xAuthToken;
		this.oAuthTokenSecret = xAuthTokenKey;
	}

	/** コンストラクタ For Password Update */
	public OptimalBizService(String url, String method, String xAuthConsumerToken, String xAuthConsumerSecret, String xAuthToken, String xAuthTokenKey, String xmldata) {

		this.url  = url;
		this.method = method;

		this.oAuthConsumerToken = xAuthConsumerToken;
		this.oAuthConsumerSecret = xAuthConsumerSecret;

		this.oAuthToken = xAuthToken;
		this.oAuthTokenSecret = xAuthTokenKey;

		this.xmlString = xmldata;
	}

	private String companyGuid;

	private String pageSize;
	private String pageNo;
	private String total;
	private ArrayList<OptimalBizUserInfo> userList;

	/**
	 * Get Auth token
	 */
	public String getOAuthToken() {
		return this.oAuthToken;
	}

	/**
	 * Get Auth token Secret
	 */
	public String getOAuthTokenSecret() {
		return this.oAuthTokenSecret;
	}

	/**
	 * Get companyGuid
	 */
	public String getOptimalBizCompanyGuid() {
		return this.companyGuid;
	}

	/**
	 * Get pageSize
	 */
	public String getPageSize() {
		return this.pageSize;
	}

	/**
	 * Get pageNo
	 */
	public String getPageNo() {
		return this.pageNo;
	}

	/**
	 * Get total
	 */
	public String getTotal() {
		return this.total;
	}

	/**
	 * Get userList
	 */
	public ArrayList<OptimalBizUserInfo> getUserList() {
		return this.userList;
	}

	/**
	 * Post OptimalBiz token Info
	 */
	public int getOptimalBizTokenInfo() {
		String apiName = "authenticate-xauth-access_token";

		int responseStatus = 0;
		ResponseData responseData = null;

		responseData = request();
		responseStatus = responseData.status;

		if ( responseStatus == HttpStatus.SC_OK ) {
			//HTTPステータス:200 成功
			dissloveTokenString(responseData.entityBody.toString());
		} else if ( responseStatus == HttpStatus.SC_UNAUTHORIZED ) {
			//HTTPステータス:401 認証失敗
			throw new RestException( new RestResult(
				ResponseCode.PASSWORD_BIZ_AUTH_ERROR , null, "", getErrorDetails(
					apiName, responseStatus, "Unauthorized 認証失敗 Authorization ヘッダーの値が適切でない パスワード間違い含む" ) ) );
		} else {
			// HTTPステータス:その他 エラー
			throw new RestException( new RestResult(
				ResponseCode.PASSWORD_BIZ_API_ERROR , null, "", getErrorDetails( apiName, responseStatus, getErrorName( responseStatus ) ) ) );
		}

		return responseStatus;
	}

	/**
	 * Disslove Response Token String
	 */
	private void dissloveTokenString (String str) {

		if (str != null && str.length() != 0) {

			int len = str.length();

			int i= str.indexOf(OAuthTokenKey);
			int j= str.indexOf(OAuthTokenSecretKey);

			this.oAuthToken = str.substring(i + OAuthTokenKey.length() + 1, j-1);
			this.oAuthTokenSecret = str.substring(j  +OAuthTokenSecretKey.length() + 1);
		}
	}

	/**
	 * Get OptimalBiz Company Info Request
	 */
	public int getOptimalBizCompanyInfo() {
		String apiName = "current-company-read";

		ResponseData responseData = null;
		responseData = request();
		int responseStatus = responseData.status;

		if ( responseStatus == HttpStatus.SC_OK ) {
			// HTTPステータス:200 成功
			String strXml = responseData.entityBody.toString();
			parseXmlForCompanyInfo(strXml);
		} else if ( responseStatus == HttpStatus.SC_UNAUTHORIZED ) {
			// HTTPステータス:401 認証失敗
			throw new RestException( new RestResult(
				ResponseCode.PASSWORD_BIZ_AUTH_ERROR , null, "", getErrorDetails( apiName, responseStatus, getErrorName( responseStatus ) ) ) );
		} else {
			// HTTPステータス:その他 APIエラー
			throw new RestException( new RestResult(
				ResponseCode.PASSWORD_BIZ_API_ERROR , null, "", getErrorDetails( apiName, responseStatus, getErrorName( responseStatus ) ) ) );
		}

		return responseStatus;
	}

	/**
	 * Set CompanyGuid
	 * @param strXml
	 * @return companyGuid
	 */
	private void parseXmlForCompanyInfo(String strXml) {

		Document document = null;

		try {
			document = DocumentHelper.parseText(strXml);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }

		Element root = document.getRootElement();

//		String  companyId=  root.elementText("code");
//		String  companyName=  root.elementText("name");

		String  companyGuid=  root.elementText("guid");

		this.companyGuid = companyGuid;
	}

	/**
	 * Get OptimalBiz UserList Info
	 */
	public int getOptimalUserListInfo() {
		String apiName = "company-user-list";

		ResponseData responseData = null;
		responseData = request();

		int responseStatus = responseData.status;

		if ( responseStatus == HttpStatus.SC_OK ) {
			//HTTPステータス:200
			String strXml = responseData.entityBody;
			parseXmlForUserList(strXml);
		} else if ( responseStatus == HttpStatus.SC_UNAUTHORIZED ) {
			// HTTPステータス:401 認証失敗
			throw new RestException( new RestResult(
				ResponseCode.PASSWORD_BIZ_AUTH_ERROR , null, "", getErrorDetails( apiName, responseStatus, getErrorName( responseStatus ) ) ) );
		} else {
			// HTTPステータス:その他 APIエラー
			throw new RestException( new RestResult(
				ResponseCode.PASSWORD_BIZ_API_ERROR , null, "", getErrorDetails( apiName, responseStatus, getErrorName( responseStatus ) ) ) );
		}

		return responseStatus;
	}

	/**
	 * set UserList
	 * @param strXml
	 */
	private void parseXmlForUserList(String strXml) {

		Document doc = null;
		if(this.userList != null) {
			this.userList.clear();
		} else {
			this.userList = new ArrayList<OptimalBizUserInfo>();
		}

		try {
			doc = DocumentHelper.parseText(strXml);

			Element rootElt = doc.getRootElement();

			this.pageSize = rootElt.attributeValue("per");
			this.pageNo = rootElt.attributeValue("page");
			this.total=  rootElt.attributeValue("total");

			Iterator iter = rootElt.elementIterator("user");

			while (iter.hasNext()) {

				Element recordEle = (Element) iter.next();
				String guid = recordEle.elementTextTrim("guid");
				String name = recordEle.elementTextTrim("name");

				OptimalBizUserInfo user = new OptimalBizUserInfo();
				user.setUserGuid(guid);
				user.setUserName(name);
				this.userList.add(user);
			}

		} catch(DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Put Update OptimalBiz UserPassword
	 */
	public int updateOptimalBizUserPassword() {
		String apiName = "company-user-update";
		ResponseData responseData = null;
		responseData = request();

		int responseStatus = responseData.status;

		if ( responseStatus == HttpStatus.SC_NO_CONTENT ) {
			// HTTPステータス:204 成功
		} else if ( responseStatus == HttpStatus.SC_UNPROCESSABLE_ENTITY ) {
			// HTTPステータス:422 入力値エラー
			String name = "#password";
			throw new RestException( new RestResult(
				ResponseCode.PASSWORD_BIZ_API_VALIDATE_ERROR, null, name ) ) ;
		} else if ( responseStatus == HttpStatus.SC_UNAUTHORIZED ) {
			// HTTPステータス:401 認証失敗
			throw new RestException( new RestResult(
				ResponseCode.PASSWORD_BIZ_AUTH_ERROR , null, "", getErrorDetails( apiName, responseStatus, getErrorName( responseStatus ) ) ) );
		} else {
			// HTTPステータス:その他 APIエラー
			throw new RestException( new RestResult(
				ResponseCode.PASSWORD_BIZ_API_ERROR , null, "", getErrorDetails( apiName, responseStatus, getErrorName( responseStatus ) ) ) );
		}

		return responseStatus;
	}

	/**
	 * エラーログ出力用の情報を取得
	 *
	 * @param apiName API名
	 * @param responseStatus bizAPIレスポンスステータス
	 * @param errorName エラー名称
	 *
	 * @return エラーログ出力用情報
	 */
	public String getErrorDetails ( String apiName, int responseStatus , String errorName ) {

		StringBuilder sb = new StringBuilder();

		sb.append(" [");
		sb.append("apiName").append("='").append( apiName ).append("' ");
		sb.append("responseStatus").append("='").append( String.valueOf( responseStatus ) ).append("' ");
		sb.append("errorName").append("='").append( errorName ).append("' ");
		sb.append("url").append("='").append( this.url ).append("' ");
		sb.append("method").append("='").append( this.method ).append("' ");
		sb.append("oAuthConsumerToken").append("='").append( this.oAuthConsumerToken ).append("' ");
		sb.append("oAuthConsumerSecret").append("='").append( this.oAuthConsumerSecret ).append("' ");
		sb.append("oAuthToken").append("='").append( this.oAuthToken ).append("' ");
		sb.append("oAuthTokenSecret").append("='").append( this.oAuthTokenSecret ).append("' ");
		sb.append("xmldata").append("='").append( this.xmlString ).append("' ");
		sb.append("]");

		return sb.toString();
	}

	/**
	 * bizAPIレスポンスステータスからbizAPIエラー名称取得
	 *
	 * @param responseStatus bizAPIエラー名称
	 *
	 * @return bizAPIエラー名称
	 */
	public String getErrorName( int responseStatus ) {
		String errorName = "";
		switch ( responseStatus ) {
			case HttpStatus.SC_BAD_REQUEST:
				// 400
				errorName = "Bad Request リクエストが不正";
				break;
			case HttpStatus.SC_UNAUTHORIZED:
				// 401
				errorName = "Unauthorized 認証失敗 Authorizationヘッダーの値が適切でない。OAuthConsumerのkey又はsecretが正しくない。Content-Typeが適切に設定されていない。";
				break;
			case HttpStatus.SC_FORBIDDEN:
				// 403
				errorName = "Forbidden 認証はされているが、アクセス権限がない";
				break;
			case HttpStatus.SC_NOT_FOUND:
				// 404
				errorName = "Not found アクセス権限はあるが、該当リソースが見つからない";
				break;
			case HttpStatus.SC_NOT_ACCEPTABLE:
				// 406
				errorName = "Not Acceptable 対応していない拡張子または Accept ヘッダー";
				break;
			case HttpStatus.SC_UNPROCESSABLE_ENTITY:
				// 422
				errorName = "Unprocessable Entity バリデーションエラー 入力値が適切でない。";
				break;
			case HttpStatus.SC_INTERNAL_SERVER_ERROR:
				// 500
				errorName = "Internal Server Error サーバでエラーが発生";
				break;
			default:
				errorName = "その他";
		}

		return errorName;
	}

}
