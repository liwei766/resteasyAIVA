/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechLogDetailRestValidator.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.speechlogdetail;


import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.api.app.speechlog.SpeechLogService;
import cloud.optim.aivoiceanalytics.api.app.speechlogdetail.SpeechLogDetailRequest.EditForm;
import cloud.optim.aivoiceanalytics.api.entity.SpeechLog;
import cloud.optim.aivoiceanalytics.api.entity.SpeechLogDetail;
import cloud.optim.aivoiceanalytics.api.entity.dao.SpeechLogDetailDao;
import cloud.optim.aivoiceanalytics.api.util.AuthUtil;
import cloud.optim.aivoiceanalytics.core.common.utility.HankakuKanaConverter;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.CustomUser;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.LoginUtility ;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestValidatorUtils;

/**
 * SpeechLogDetailRestService のバリデータクラス
 * （入力チェックと入力内容の補完を行う）
 */
@Component
class SpeechLogDetailRestValidator
{
	/** Commons Logging instance.  */
	@SuppressWarnings( "unused" )
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility ;

	/** AuthUtil */
	@Resource private AuthUtil authUtil;

	/** SpeechLogDetailDao */
	@Resource private SpeechLogDetailDao speechLogDetailDao;

	/** SpeechLogService */
	@Resource private SpeechLogService speechLogService;


	// -------------------------------------------------------------------------

	/**
	 * 更新の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForUpdate( SpeechLogDetailRequest req ) {

		String name = "";

		// ----- 削除条件

		name = "#request";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#bulkFormList";

		List<EditForm> bulkFormList = req.getBulkFormList();

		if ( bulkFormList == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}
	}

	/**
	 * 更新の入力チェックと入力内容の補完.
	 * 内容と更新者、更新日時以外は更新しない
	 *
	 * @param req 入力内容
	 *
	 * @throws SQLException 例外発生時
	 */
	public void validateForUpdateOne( EditForm req ) throws SQLException {

		String name = "";

		// ********** 更新内容

		name = "#editForm";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#speechLogDetail";

		SpeechLogDetail speechLogDetail = req.getSpeechLogDetail();

		if ( speechLogDetail == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ********** 個別項目

		CustomUser customUser = loginUtility.getCustomUser() ;

		// ----- 音声解析ログ詳細 ID

		{
			name = "#speechLogDetail.speechLogDetailId" ;
			Long value = speechLogDetail.getSpeechLogDetailId() ;


			RestValidatorUtils.fieldValidate( name, value, true, null, null );
			RestValidatorUtils.fieldValidate( name, String.valueOf( value ), false, null, 19 ) ;

		}

		// 存在チェック
		SpeechLogDetail entity = speechLogDetailDao.get( speechLogDetail.getSpeechLogDetailId() ) ;
		{
			Long value = speechLogDetail.getSpeechLogDetailId() ;
			name = "#speechLogDetail.speechLogDetailId" ;

			if ( entity == null ) // 存在しない
			{
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, name, value ) ) ;
			}

			// 企業IDのチェック

			if ( !customUser.getCompanyId().equals(entity.getCompanyId()) ) // 存在しない
			{
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, name, value ) ) ;
			}

			// 削除日時
			if ( entity.getDeleteDate() != null ) // 存在しない
			{
				throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, name, value ) ) ;
			}

			// 参照可否チェック
			// 企業管理者権限を保持している場合には、ユーザ企業ID配下の全ユーザの音声解析ログを取得可能
			SpeechLog speechLog = null;
			if (this.authUtil.isAdmin()) {
				speechLog = speechLogService.getSpeechLogCompanyAllUser(entity.getSpeechLogId(), customUser.getCompanyId());
			} else {
				speechLog = speechLogService.getSpeechLog(entity.getSpeechLogId(), customUser.getCompanyId(), customUser.getUserId());
			}

			if (speechLog == null )
			{
				throw new RestException( new RestResult(
						ResponseCode.NOT_FOUND, null, name, value ) ) ;
			}
		}


		// ----- 更新日時

		{
			name = "#speechLogDetail.updateDate" ;
			Date value = speechLogDetail.getUpdateDate() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, null, null );
		}

		// ----- 企業 ID
		// 更新しないのでDBから取得した値で上書き
		speechLogDetail.setCompanyId(entity.getCompanyId());

		// ----- 音声解析ログ ID
		// 更新しないのでDBから取得した値で上書き
		speechLogDetail.setSpeechLogId(entity.getSpeechLogId());

		// ----- 内容

		{
			name = "#speechLogDetail.log" ;
			String value = speechLogDetail.getLog() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 2147483647 );

			// 半角カナを全角カナに変換
			speechLogDetail.setLog(HankakuKanaConverter.convert(value));
		}

		// ----- 開始秒数
		// 更新しないのでDBから取得した値で上書き
		speechLogDetail.setBegin(entity.getBegin());

		// ----- 終了秒数
		// 更新しないのでDBから取得した値で上書き
		speechLogDetail.setEnd(entity.getEnd());

		// ----- 音声有無フラグ
		// 更新しないのでDBから取得した値で上書き
		speechLogDetail.setVoiceExistence(entity.getVoiceExistence());

		// ----- 作成日時
		// 更新しないのでDBから取得した値で上書き
		speechLogDetail.setCreateDate(entity.getCreateDate());

		// ----- 作成ユーザ ID
		// 更新しないのでDBから取得した値で上書き
		speechLogDetail.setCreateUserId(entity.getCreateUserId());

		// ----- 作成ユーザ名
		// 更新しないのでDBから取得した値で上書き
		speechLogDetail.setCreateUserName(entity.getCreateUserName());


		// ----- 更新ユーザ ID

		{
			name = "#speechLogDetail.updateUserId" ;
			String value = speechLogDetail.getUpdateUserId() ;

			// 常にログインユーザ ID を設定
			value = customUser.getUserId() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 32 );

			// 補完内容を反映
			speechLogDetail.setUpdateUserId( value );
		}

		// ----- 更新ユーザ名

		{
			name = "#speechLogDetail.updateUserName" ;
			String value = speechLogDetail.getUpdateUserName() ;

			// 常にログインユーザ 名 を設定
			value = customUser.getUserName() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, 100 );

			// 補完内容を反映
			speechLogDetail.setUpdateUserName( value );
		}

		// ----- 削除日時
		// 更新しないのでDBから取得した値で上書き
		speechLogDetail.setDeleteDate(entity.getDeleteDate());

	}


	// -------------------------------------------------------------------------

	/**
	 * 削除の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForDelete( SpeechLogDetailRequest req ) {

		String name = "";

		// ----- 削除条件

		name = "#request";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#bulkFormList";

		List<EditForm> bulkFormList = req.getBulkFormList();

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
	public SpeechLogDetail validateForDeleteOne( EditForm req ) {

		String name = "";

		// ----- 削除条件

		name = "#editForm";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		name = "#speechLogDetail";

		SpeechLogDetail speechLogDetail = req.getSpeechLogDetail();

		if ( speechLogDetail == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		// ----- PK

		name = "#speechLogDetail.speechLogDetailId";
		Long pkvalue = speechLogDetail.getSpeechLogDetailId();

		RestValidatorUtils.fieldValidate( name, pkvalue, true, null, null );

		// 存在チェック
		CustomUser customUser = loginUtility.getCustomUser() ;

		SpeechLogDetail entity = speechLogDetailDao.get( pkvalue );

		if ( entity == null ) {

			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name ) );
		}

		// 企業IDのチェック
		if ( !customUser.getCompanyId().equals(entity.getCompanyId()) ) // 存在しない
		{
			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name ) ) ;
		}

		// 削除日時
		if ( entity.getDeleteDate() != null ) // 存在しない
		{
			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name ) ) ;
		}

		// 参照可否チェック
		// 企業管理者権限を保持している場合には、ユーザ企業ID配下の全ユーザの音声解析ログを取得可能
		SpeechLog speechLog = null;
		if (this.authUtil.isAdmin()) {
			speechLog = speechLogService.getSpeechLogCompanyAllUser(entity.getSpeechLogId(), customUser.getCompanyId());
		} else {
			speechLog = speechLogService.getSpeechLog(entity.getSpeechLogId(), customUser.getCompanyId(), customUser.getUserId());
		}

		if (speechLog == null )
		{
			throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, name ) ) ;
		}


		// ----- 更新ユーザ ID
		entity.setUpdateUserId( customUser.getUserId() );

		// ----- 更新ユーザ名
		entity.setUpdateUserName( customUser.getUserName() );

		return entity;
	}

	/**
	 * 音声ファイルダウンロードの入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public SpeechLogDetail validateForVoice( Long req ) {

		String name = "";

		// ----- 削除条件

		name = "#speechLogDetailId";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}

		CustomUser customUser = loginUtility.getCustomUser();

		// 存在チェック
		SpeechLogDetail entity = speechLogDetailDao.get( req );
		if ( entity == null ) {

			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name ) );
		}

		// 企業IDのチェック
		if (!customUser.getCompanyId().equals(entity.getCompanyId())) {

			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name, req ) );
		}

		// 削除日時のチェック
		if (entity.getDeleteDate() != null) {

			throw new RestException( new RestResult(
				ResponseCode.NOT_FOUND, null, name, req ) );
		}

		// 参照可否チェック
		// 企業管理者権限を保持している場合には、ユーザ企業ID配下の全ユーザの音声解析ログを取得可能
		SpeechLog speechLog = null;
		if (this.authUtil.isAdmin()) {
			speechLog = speechLogService.getSpeechLogCompanyAllUser(entity.getSpeechLogId(), customUser.getCompanyId());
		} else {
			speechLog = speechLogService.getSpeechLog(entity.getSpeechLogId(), customUser.getCompanyId(), customUser.getUserId());
		}

		if (speechLog == null )
		{
			throw new RestException( new RestResult(
					ResponseCode.NOT_FOUND, null, name, req ) ) ;
		}

		return entity;
	}

}
