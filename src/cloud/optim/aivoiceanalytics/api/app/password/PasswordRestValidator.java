/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：PasswordRestValidator.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.password;

import cloud.optim.aivoiceanalytics.api.app.password.PasswordRequest.InputForm;
import cloud.optim.aivoiceanalytics.api.app.password.PasswordRequest.EditForm;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.CustomUser;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.LoginUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestValidatorUtils;
import cloud.optim.aivoiceanalytics.core.modules.rest.SortForm;
import cloud.optim.aivoiceanalytics.core.modules.rest.SortForm.SortElement;
import cloud.optim.aivoiceanalytics.core.modules.validator.ValidatorUtils;
import java.util.ArrayList;
import javax.annotation.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * PasswordRestService のバリデータクラス.
 * <p>入力チェックと入力内容の補完を行う.</p>
 */
@Component
class PasswordRestValidator {

	/** Commons Logging instance. */
	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass());

	/** LoginUtility. */
	@Resource
	private LoginUtility loginUtility;

	/** 一回当たりの最大取得件数 */
	@Value( "${password.max.result.count}" )
	private long maxResultCount;

	/** 最大オフセット */
	@Value( "${password..max.offset}" )
	private long maxOffset;

	// -------------------------------------------------------------------------

	/**
	 * 認証 入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForXauth(PasswordRequest req) {

		String name = "#request";
		if (req == null) {
			throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
		}

		name = "#inputForm";
		InputForm inputForm = req.getInputForm();
		if (inputForm == null) {
			throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
		}

		// ********** 個別項目
		{
			// ----- 認証ユーザーID
			name = "#userId";
			String userId = inputForm.getUserId();
			// トリムする
			userId = ValidatorUtils.trim( userId );

			RestValidatorUtils.fieldValidate( name, userId, true, null, null );

			// 認証ユーザーIDとユーザ情報のユーザIDの一致チェック
			CustomUser customUser = loginUtility.getCustomUser();
			String customUserId = customUser.getUserId();

			if ( !userId.equals( customUserId ) ) {
				throw new RestException(new RestResult(ResponseCode.PASSWORD_BIZ_AUTH_INPUT_ERROR, null, name));
			}
		}

		{
			// ----- 認証パスワード
			name = "#password";
			String password = inputForm.getPassword();

			// トリムする
			password = ValidatorUtils.trim( password );

			RestValidatorUtils.fieldValidate( name, password, true, null, null );
		}
	}


	/**
	 * 検索の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForSearch(PasswordRequest req) {

		String name = "#request";
		if (req == null) {
			throw new NullPointerException(name);
		}

		// ----- 取得条件（指定がなければ全検索）
		name = "#searchForm";
		SearchForm searchForm = req.getSearchForm();
		if (searchForm == null) {
			searchForm = new SearchForm();
		}
		req.setSearchForm(searchForm);

		// ----- ソート条件
		name = "#sortForm";
		SortForm sortForm = searchForm.getSortForm();
		if (sortForm == null) {
			sortForm = new SortForm();
		}
		// デフォルトソート条件の設定
		if (sortForm.getSortElement() == null || sortForm.getSortElement().isEmpty()) {
			sortForm.setSortElement(new ArrayList<>());
		}
		searchForm.setSortForm(sortForm);

		// 最大取得件数
//		Long limit = sortForm.getMaxResult();
//		if (limit == null || limit < 0L || limit > maxResultCount) {
//			sortForm.setMaxResult(maxResultCount);
//		}
//		sortForm.setMaxResult(sortForm.getMaxResult());
		sortForm.setMaxResult(maxResultCount);

		// オフセット
//		sortForm.setOffset(null);
		Long offset = sortForm.getOffset();
		if (offset == null || offset < 0L || offset > maxOffset) {
			sortForm.setOffset( 0L );
		}
		sortForm.setOffset(sortForm.getOffset());

		RestValidatorUtils.sortValidate(sortForm);
		RestValidatorUtils.sortConvert(sortForm);
	}

	// -------------------------------------------------------------------------

	/**
	 * 更新の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 *
	 * @throws SQLException 例外発生時
	 */
	public void validateForUpdate(PasswordRequest req) {
		// ********** 更新内容
		String name = "#request";
		if (req == null) {
			throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
		}

		name = "#editForm";
		EditForm editForm = req.getEditForm();
		if (editForm == null) {
			throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
		}

		// ********** 個別項目
		{
			// ----- ユーザーGUID
			name = "#userGuid";
			String userGuid = editForm.getUserGuid();
			// トリムする
			userGuid = ValidatorUtils.trim( userGuid );

			RestValidatorUtils.fieldValidate( name, userGuid, true, null, null );
		}

		String password = "";
		{
			// ----- パスワード
			name = "#password";
			password = editForm.getPassword();

			// トリムする
			password = ValidatorUtils.trim( password );

			RestValidatorUtils.fieldValidate( name, password, true, null, null );
		}

		{
			// ----- 確認用パスワード
			name = "#confirmPassword";
			String confirmPassword = editForm.getConfrimPassword();
			// トリムする
			confirmPassword = ValidatorUtils.trim( confirmPassword );

			RestValidatorUtils.fieldValidate( name, confirmPassword, true, null, null );

			// パスワードと確認用パスワードの入力一致チェック
			if ( !confirmPassword.equals( password ) ) {
				throw new RestException(new RestResult(ResponseCode.PASSWORD_INPUT_CONFIRM_PASSWORD_DIFFERENT_ERROR, null, name));
			}
		}

	}

}
