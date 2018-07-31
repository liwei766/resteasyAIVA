/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechLogRestValidator.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.speechlog;


import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.api.app.speechlog.SpeechLogRequest.EditForm;
import cloud.optim.aivoiceanalytics.api.entity.SpeechLog;
import cloud.optim.aivoiceanalytics.api.entity.dao.SpeechLogDao;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.CustomUser;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.LoginUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestValidatorUtils;
import cloud.optim.aivoiceanalytics.core.modules.rest.SortForm;
import cloud.optim.aivoiceanalytics.core.modules.rest.SortForm.SortElement;
import cloud.optim.aivoiceanalytics.core.modules.validator.ValidatorUtils;

/**
 * SpeechLogRestService のバリデータクラス
 * （入力チェックと入力内容の補完を行う）
 */
@Component
class SpeechLogRestValidator
{
	/** Commons Logging instance.  */
	@SuppressWarnings( "unused" )
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility ;

	/** SpeechLogDao */
	@Resource private SpeechLogDao speechLogDao;

	/**
	 * 検索の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForSearchByUser( SpeechLogRequest req ) {

		String name = "";

		name = "#request";

		if ( req == null ) {

			throw new NullPointerException( name );
		}

		// ----- 取得条件（指定がなければ全検索）

		name = "#searchForm";

		SearchForm searchForm = req.getSearchForm();

		if ( searchForm == null ) searchForm = new SearchForm();

		req.setSearchForm( searchForm );

		// ----- ソート条件

		name = "#sortForm";

		SortForm sortForm = searchForm.getSortForm();

		if ( sortForm == null ) {
			sortForm = new SortForm();

			searchForm.setSortForm( sortForm );
			sortForm.addSortElement( new SortElement( "speechLog.startDate", true ) ); // デフォルトは 音声解析開始日時 の昇順

		}

		// TODO 上限値を設定？
		// sortForm.setMaxResult(maxResult);;

		RestValidatorUtils.sortValidate( sortForm );
		RestValidatorUtils.sortConvert( sortForm );

		// ----- 検索条件

		name = "#speechLogForm";

		SpeechLogSearchForm speechLogForm = searchForm.getSpeechLog();

		if ( speechLogForm == null ) speechLogForm = new SpeechLogSearchForm();

		searchForm.setSpeechLog( speechLogForm );

		// ユーザ情報取得
		CustomUser customUser = loginUtility.getCustomUser();

		// ----- 企業 ID
		// ユーザ情報の企業IDを設定する
		speechLogForm.setCompanyId(customUser.getCompanyId());

		// ----- ユーザ ID
		// ユーザ情報のユーザIDを設定する
		speechLogForm.setUserId(customUser.getUserId());

		// ----- 開始日時From
		{
			name = "#speechLog.startDateFrom" ;
			Date value = speechLogForm.getStartDateFrom() ;

			RestValidatorUtils.fieldValidate( name, value, false, null, null, null );
		}

		// ----- 開始日時To
		{
			name = "#speechLog.startDateTo" ;
			Date value = speechLogForm.getStartDateTo() ;

			RestValidatorUtils.fieldValidate( name, value, false, null, null, null );
		}
	}


	// -------------------------------------------------------------------------

	/**
	 * 検索の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForSearchByCompany( SpeechLogRequest req ) {

		String name = "";

		name = "#request";

		if ( req == null ) {

			throw new NullPointerException( name );
		}

		// ----- 取得条件（指定がなければ全検索）

		name = "#searchForm";

		SearchForm searchForm = req.getSearchForm();

		if ( searchForm == null ) searchForm = new SearchForm();

		req.setSearchForm( searchForm );

		// ----- ソート条件

		name = "#sortForm";

		SortForm sortForm = searchForm.getSortForm();

		if ( sortForm == null ) {
			sortForm = new SortForm();

			searchForm.setSortForm( sortForm );
			sortForm.addSortElement( new SortElement( "speechLog.startDate", true ) ); // デフォルトは 音声解析開始日時 の昇順

		}

		// TODO 上限値を設定？
		// sortForm.setMaxResult(maxResult);;

		RestValidatorUtils.sortValidate( sortForm );
		RestValidatorUtils.sortConvert( sortForm );

		// ----- 検索条件

		name = "#speechLogForm";

		SpeechLogSearchForm speechLogForm = searchForm.getSpeechLog();

		if ( speechLogForm == null ) speechLogForm = new SpeechLogSearchForm();

		searchForm.setSpeechLog( speechLogForm );

		// ユーザ情報取得
		CustomUser customUser = loginUtility.getCustomUser();

		// ----- 企業 ID
		// ユーザ情報の企業IDを設定する
		speechLogForm.setCompanyId(customUser.getCompanyId());

		// ----- 開始日時From
		{
			name = "#speechLog.startDateFrom" ;
			Date value = speechLogForm.getStartDateFrom() ;

			RestValidatorUtils.fieldValidate( name, value, false, null, null, null );
		}

		// ----- 開始日時To
		{
			name = "#speechLog.startDateTo" ;
			Date value = speechLogForm.getStartDateTo() ;

			RestValidatorUtils.fieldValidate( name, value, false, null, null, null );
		}

		// ----- ユーザ名
		{
			name = "#speechLog.userNameOption";
			String value = speechLogForm.getUserNameOption();

			RestValidatorUtils.fieldValidate( name, value,
				ValidatorUtils.required( speechLogForm.getUserName() ), null, null );
			RestValidatorUtils.in( name, value, "0", "1", "2", "3" );
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * SpeechLog 取得の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForGet( SpeechLogRequest req ) {

		String name = "";

		// ----- 取得条件

		name = "#request";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#editForm";

		EditForm editForm = req.getEditForm();

		if ( editForm == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#speechLog";

		SpeechLog speechLog = editForm.getSpeechLog();

		if ( speechLog == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ----- PK

		name = "#speechLog.speechLogId";
		Long pkvalue = speechLog.getSpeechLogId();

		RestValidatorUtils.fieldValidate( name, pkvalue, true, null, null );



	}


	// -------------------------------------------------------------------------

	/**
	 * SpeechLog 音声生成の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForGenerateVoice( SpeechLogRequest req ) {

		String name = "";

		// ----- 取得条件

		name = "#request";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#editForm";

		EditForm editForm = req.getEditForm();

		if ( editForm == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#speechLog";

		SpeechLog speechLog = editForm.getSpeechLog();

		if ( speechLog == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ----- PK

		name = "#speechLog.speechLogId";
		Long pkvalue = speechLog.getSpeechLogId();

		RestValidatorUtils.fieldValidate( name, pkvalue, true, null, null );
	}

	// -------------------------------------------------------------------------

	/**
	 * 削除の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForDelete( SpeechLogRequest req ) {

		String name = "";

		// ----- 削除条件

		name = "#request";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#bulkFormList";

		List<SearchResult> bulkFormList = req.getBulkFormList();

		if ( bulkFormList == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}
	}

	/**
	 * 削除の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public SpeechLog validateForDeleteOne( SearchResult req ) {

		String name = "";

		// ----- 削除条件

		name = "#bulkForm";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#speechLog";

		SpeechLogSearchResult speechLog = req.getSpeechLog();

		if ( speechLog == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ----- PK

		name = "#speechLog.speechLogId";
		Long pkvalue = speechLog.getSpeechLogId();

		RestValidatorUtils.fieldValidate( name, pkvalue, true, null, null );

		// 存在チェック

		SpeechLog entity = speechLogDao.get( pkvalue );

		if ( entity == null ) {

			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name ) );
		}

		// 論理削除されている
		if ( entity.getDeleteDate() != null ) { 
			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name ) );
		}

		// ユーザ情報取得
		CustomUser customUser = loginUtility.getCustomUser() ;

		// 企業IDのチェック
		if ( !customUser.getCompanyId().equals(entity.getCompanyId()) ) {
			 // 存在しない
			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name ) );
		}

		// ----- 更新ユーザ ID
		entity.setUpdateUserId( customUser.getUserId() );

		// ----- 更新ユーザ名
		entity.setUpdateUserName( customUser.getUserName() );

		return entity;
	}

}
