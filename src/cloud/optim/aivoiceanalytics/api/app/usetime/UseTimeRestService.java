/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：UseTimeRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.usetime;


import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.core.modules.loginutil.CustomUser;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.LoginUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ExceptionUtil;
import cloud.optim.aivoiceanalytics.core.modules.rest.MessageUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestLog;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;

/**
 * UseTimeRestService 実装.<br/>
 */
@Path( "/usetime" )
@Consumes( { "application/json", "application/xml" } )
@Produces( { "application/json", "application/xml" } )
@Component
public class UseTimeRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() );

	/** PK の項目名 */
	@SuppressWarnings("unused")
	private static final String NAME_PK = "#useTime.useTimeId";

	// -------------------------------------------------------------------------

	/** バリデータ */
	@Resource private UseTimeRestValidator validator;

	/** UseTimeService */
	@Resource private UseTimeService useTimeService;

	/** RestLog */
	@Resource private RestLog restlog;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility;

	// -------------------------------------------------------------------------

	/**
	 * 企業毎ユーザ時間検索
	 *
	 * @param req 検索条件
	 *
	 * @return 検索結果
	 */
	@POST
	@Path( "/searchByCompanyId" )
	public UseTimeResponse searchByCompanyId( UseTimeRequest req ) {

		String MNAME = "searchByCompanyId";
		restlog.start( log, MNAME, req );

		try {

			UseTimeResponse res = new UseTimeResponse();

			// ----- 入力チェック

			// 検索条件の指定がない場合は全検索として扱う

			if ( req == null ) req = new UseTimeRequest();

			validator.validateForSearchByCompanyId( req );

			// ----- 検索

			SearchForm form = req.getSearchForm();

			List<SearchResult> list = useTimeService.searchByCompanyId( form );

			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setSearchResultList( list );

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
	 * ユーザ毎利用時間検索
	 *
	 * @param req 検索条件
	 *
	 * @return 検索結果
	 */
	@POST
	@Path( "/searchByUserId" )
	public UseTimeResponse searchByUserId( UseTimeRequest req ) {

		String MNAME = "searchByUserId";
		restlog.start( log, MNAME, req );

		try {

			UseTimeResponse res = new UseTimeResponse();

			// ----- 入力チェック

			// 検索条件の指定がない場合は全検索として扱う

			if ( req == null ) req = new UseTimeRequest();

			validator.validateForSearchByUserId( req );

			// ----- 検索

			SearchForm form = req.getSearchForm();

			List<SearchResult> list = useTimeService.searchByUserId( form );

			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setSearchResultList( list );

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
	 * ログインユーザの当月の利用時間を取得する
	 *
	 * @return 取得結果
	 */
	@POST
	@Path( "/get" )
	public UseTimeResponse get() {

		String MNAME = "get";
		restlog.start( log, MNAME, null );

		try {

			UseTimeResponse res = new UseTimeResponse();

			// ----- 検索
			CustomUser customUser = loginUtility.getCustomUser();


			Long useTime = useTimeService.get( customUser.getCompanyId(), customUser.getUserId() );

			// ----- レスポンス作成

			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setUseTime( useTime );

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, null, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}
}