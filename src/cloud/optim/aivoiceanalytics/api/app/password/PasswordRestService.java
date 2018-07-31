/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：PasswordRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.password;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.lang3.StringUtils ;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.api.app.password.SearchResult;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.CustomUser;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.LoginUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ExceptionUtil;
import cloud.optim.aivoiceanalytics.core.modules.rest.MessageUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestLog;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;
import cloud.optim.aivoiceanalytics.core.modules.xauth.OptimalBizService;
import cloud.optim.aivoiceanalytics.core.modules.xauth.OptimalBizUserInfo;

/**
 * PasswordRestService 実装.<br/>
 */
@Path( "/password" )
@Consumes( { "application/json", "application/xml" } )
@Produces( { "application/json", "application/xml" } )
@Component
public class PasswordRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() );

	/** LoginUtility */
	@Resource private LoginUtility loginUtility ;

	// -------------------------------------------------------------------------

	/** バリデータ */
	@Resource private PasswordRestValidator validator;

	/** RestLog */
	@Resource private RestLog restlog;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility;

	// -------------------------------------------------------------------------

	/** OptimalBiz URL */
	@Value( "${optimalbiz.url}" )
	private String optimalBizUrl;

	/** OptimalBiz 認証コンシューマキー */
	@Value( "${optimalbiz.oAuthConsumer.Token}" )
	private String oAuthConsumerToken;

	/** OptimalBiz 認証コンシューマシークレット */
	@Value( "${optimalbiz.oAuthConsumer.Secret}" )
	private String oAuthConsumerSecret;

	/** OptimalBiz 認証API */
	@Value( "${optimalbiz.accessToken.api}" )
	private String tokenApi;

	/** OptimalBiz 企業情報取得API */
	@Value( "${optimalbiz.companyInfo.api}" )
	private String companyInfoApi;

	/** OptimalBiz ユーザ一覧情報取得API */
	@Value( "${optimalbiz.userListInfo.api}" )
	private String userListInfoApi;

	/** OptimalBiz ユーザパスワード更新API */
	@Value( "${optimalbiz.updateUserPassword.api}" )
	private String updateUserPasswordApi;

	/** OptimalBiz ユーザパスワード更新用データフォーマット */
	@Value( "${optimalbiz.updateUserPassword.xmlStrFormat}" )
	private String updatePasswordFormat;

//-------------------------------------------------------------------------

	/**
	 * 認証
	 *
	 * @param req 認証条件
	 *
	 * @return 認証結果
	 */
	@POST
	@Path( "/xauth" )
	public PasswordResponse xauth( PasswordRequest req ) {

		String MNAME = "xauth";
		restlog.start( log, MNAME, req );

		try {

			PasswordResponse res = new PasswordResponse();

			// ----- 入力チェック

			validator.validateForXauth( req );

			// ----- 入力データ取得
			String xauthPassword = req.getInputForm().getPassword();

			CustomUser customUser = loginUtility.getCustomUser();
			String xauthUserId = customUser.getUserId();

			// ----- 企業ID取得
			String xauthCompanyId = customUser.getCompanyId();

			if ( StringUtils.isEmpty( xauthCompanyId ) ) {
				// biz認証用(bizAPI通信前)情報取得エラー
				res.setResult( new RestResult( ResponseCode.PASSWORD_BIZ_AUTH_PARAM_NOT_FOUND_ERROR ) );
				messageUtility.fillMessage( res.getResultList() );
				restlog.end( log, MNAME, req, res, res.getResultList() );

				return res;
			}

			// ----- 認証＆トークン取得
			String url = optimalBizUrl  + String.format(tokenApi, xauthCompanyId, xauthUserId, xauthPassword);
			String method = "POST";

			OptimalBizService xauthToken = new OptimalBizService(url, method, oAuthConsumerToken, oAuthConsumerSecret, null, null);

			xauthToken.getOptimalBizTokenInfo();

			// セッション上のbiz認証情報クリア
			customUser.setOptimalBizToken(null);
			customUser.setOptimalBizTokenSecret(null);
			customUser.setCompanyGuid(null);

			String oauthToken = xauthToken.getOAuthToken();
			String oauthTokenSecret = xauthToken.getOAuthTokenSecret();

			// ----- 企業Guid取得
			String companyUrl  = String.format("%s%s", optimalBizUrl, companyInfoApi);
			String companyMethod = "GET";

			// ----- トークン情報取得

			OptimalBizService companyInfo = new OptimalBizService(companyUrl, companyMethod, oAuthConsumerToken, oAuthConsumerSecret, oauthToken, oauthTokenSecret);
			companyInfo.getOptimalBizCompanyInfo();

			String company_guid = "";

			// biz企業情報取得 成功
			company_guid = companyInfo.getOptimalBizCompanyGuid();

			// ユーザ情報にトークン情報、企業GUIDを設定
			customUser.setOptimalBizToken(oauthToken);
			customUser.setOptimalBizTokenSecret(oauthTokenSecret);
			customUser.setCompanyGuid(company_guid);

			// ----- レスポンス作成
			res.setResult( new RestResult( ResponseCode.OK ) );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 検索 ページ番号での検索
	 *
	 * @param req 検索条件
	 *
	 * @return 検索結果
	 */
	@POST
	@Path( "/search" )
	public PasswordResponse search( PasswordRequest req ) {

		String MNAME = "search";
		restlog.start( log, MNAME, req );

		try {

			PasswordResponse res = new PasswordResponse();

			// ----- 入力チェック

			validator.validateForSearch( req );

			// ----- 入力データ取得
			Long pageSize = req.getSearchForm().getSortForm().getMaxResult();
			Long offset = req.getSearchForm().getSortForm().getOffset();

			// bizAPI送信用ページ番号
			Long pageNo = 1L;

			BigDecimal BdPagesize = BigDecimal.valueOf( pageSize );
			BigDecimal BdOffset = BigDecimal.valueOf( offset );

			if ( BdPagesize.compareTo( BigDecimal.ZERO ) == 1 ) {
				BigDecimal BdPageNo = BdOffset.divide( BdPagesize, 0, RoundingMode.UP );
				pageNo = BdPageNo.longValue() + 1L;
			}

			// ----- 取得

			CustomUser customUser = loginUtility.getCustomUser();

			String oauthToken = customUser.getOptimalBizToken();
			String oauthTokenSecret = customUser.getOptimalBizTokenSecret();
			String company_guid = customUser.getCompanyGuid();

			if ( StringUtils.isEmpty( oauthToken )
				|| StringUtils.isEmpty( oauthTokenSecret )
				|| StringUtils.isEmpty( company_guid ) ) {

				// biz未認証エラー
				res.setResult( new RestResult( ResponseCode.PASSWORD_BIZ_NO_AUTH_ERROR ) );
				messageUtility.fillMessage( res.getResultList() );
				restlog.end( log, MNAME, req, res, res.getResultList() );

				return res;
			}

			String url = optimalBizUrl  + String.format(userListInfoApi, company_guid, pageSize, pageNo);
			String method = "GET";

			OptimalBizService userList = new OptimalBizService(url, method, oAuthConsumerToken, oAuthConsumerSecret, oauthToken, oauthTokenSecret);

			userList.getOptimalUserListInfo();

			// bizAPI返却ページ情報からページング情報取得
			PageInfo pageinfo = getPageInfo( userList );

			ArrayList<OptimalBizUserInfo> userListInfo = userList.getUserList();

			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );

			List<SearchResult> list = getSearchList( userListInfo );
			res.setSearchResultList( list );
			res.setPageInfo( pageinfo );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	// -------------------------------------------------------------------------
	/**
	 * ユーザーにのパスワード更新
	 *
	 * @param req 更新条件
	 *
	 * @return 更新結果
	 */
	@POST
	@Path( "/update" )
	public PasswordResponse searchByName( PasswordRequest req ) {

		String MNAME = "update";
		restlog.start( log, MNAME, req );

		try {

			PasswordResponse res = new PasswordResponse();

			// ----- 入力チェック

			validator.validateForUpdate( req );

			// ----- 入力データ取得
			String userGuid = req.getEditForm().getUserGuid();
			String password = req.getEditForm().getPassword();

			// ----- 取得
			// ユーザ情報取得
			CustomUser customUser = loginUtility.getCustomUser();

			String oauthToken = customUser.getOptimalBizToken();
			String oauthTokenSecret = customUser.getOptimalBizTokenSecret();
			String company_guid = customUser.getCompanyGuid();

			if ( StringUtils.isEmpty( oauthToken )
				|| StringUtils.isEmpty( oauthTokenSecret )
				|| StringUtils.isEmpty( company_guid ) ) {
				// biz未認証エラー
				res.setResult( new RestResult( ResponseCode.PASSWORD_BIZ_NO_AUTH_ERROR ) );
				messageUtility.fillMessage( res.getResultList() );
				restlog.end( log, MNAME, req, res, res.getResultList() );

				return res;
			}

			String url = optimalBizUrl + String.format(updateUserPasswordApi, company_guid, userGuid);
			String method = "PUT";
			String xmldata =  String.format(updatePasswordFormat, password);

			OptimalBizService passupdate = new OptimalBizService(url, method, oAuthConsumerToken, oAuthConsumerSecret, oauthToken, oauthTokenSecret, xmldata);

			passupdate.updateOptimalBizUserPassword();

			// ----- レスポンス作成
			res.setResult( new RestResult( ResponseCode.OK ) );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	/**
	 * OptimalBizUserInfoのリスト形式のユーザー一覧から
	 * SearchResultのリスト形式のユーザー一覧を取得
	 *
	 * @param userListInfo OptimalBizUserInfoのリスト形式のユーザー一覧
	 *
	 * @return SearchResultのリスト形式のユーザー一覧
	 */
	private List<SearchResult> getSearchList( ArrayList<OptimalBizUserInfo> userListInfo ) {

		List<SearchResult> list = new ArrayList<SearchResult>();

		for ( OptimalBizUserInfo each : userListInfo ) {
			list.add( convertUserListInfo( each ) );
		}

		return list;
	}

	/**
	 * OptimalBizUserInfo形式のユーザー情報を
	 * SearchResult形式のユーザー情報に変換
	 *
	 * @param entity OptimalBizUserInfo形式の1ユーザーエンティティ
	 *
	 * @return SearchResult形式の1ユーザーエンティティ
	 */
	private SearchResult convertUserListInfo( OptimalBizUserInfo entity ) {

		SearchResult ret = new SearchResult();

		ret.setUserGuid( entity.getUserGuid() );
		ret.setUserName( entity.getUserName() );

		return ret;
	}

	/**
	 * bizAPIユーザー一覧取得結果からページ情報を取得
	 *
	 * @param userList bizAPIユーザー一覧取得結果
	 *
	 * @return ページ情報
	 */
	private PageInfo getPageInfo( OptimalBizService userList ) {

		PageInfo pageinfo = new PageInfo();

		Long pageNo = Long.parseLong( userList.getPageNo(), 10);
		Long pageSize = Long.parseLong( userList.getPageSize(), 10);
		Long total = Long.parseLong( userList.getTotal(), 10);

		pageinfo.setPageNo( pageNo );
		pageinfo.setPageSize( pageSize );
		pageinfo.setTotalNumber( total );


		BigDecimal BdPagesize = BigDecimal.valueOf( pageSize );
		BigDecimal BdTotal = BigDecimal.valueOf( total );
		BigDecimal BdTotalPage = BigDecimal.valueOf( 1 );

		if ( BdPagesize.compareTo( BigDecimal.ZERO ) == 1 ) {
			BdTotalPage = BdTotal.divide(BdPagesize, 0, RoundingMode.UP);
		}

		pageinfo.setTotalPage( BdTotalPage.longValue() );

		Long offsetPageNo = pageNo - 1L;

		if ( offsetPageNo < 0L ) {
			offsetPageNo = 0L;
		}

		pageinfo.setOffset( ( offsetPageNo * pageSize ) );

		return pageinfo;
	}

}