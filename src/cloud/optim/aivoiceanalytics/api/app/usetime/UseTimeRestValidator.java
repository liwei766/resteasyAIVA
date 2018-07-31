/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：UseTimeRestValidator.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.usetime;


import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.api.entity.dao.UseTimeDao;
import cloud.optim.aivoiceanalytics.api.util.AuthUtil;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.LoginUtility ;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestValidatorUtils;
import cloud.optim.aivoiceanalytics.core.modules.rest.SortForm;
import cloud.optim.aivoiceanalytics.core.modules.rest.SortForm.SortElement;
import cloud.optim.aivoiceanalytics.core.modules.validator.ValidatorUtils;

/**
 * UseTimeRestService のバリデータクラス
 * （入力チェックと入力内容の補完を行う）
 */
@Component
class UseTimeRestValidator
{
	/** Commons Logging instance.  */
	@SuppressWarnings( "unused" )
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility ;

	/** AuthUtil */
	@Resource
	private AuthUtil authUtil;

	/** UseTimeDao */
	@Resource private UseTimeDao useTimeDao;

	/**
	 * 企業毎利用時間検索の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForSearchByCompanyId( UseTimeRequest req ) {

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

		// ----- ソート条件（指定がなければ利用時間降順を設定）

		name = "#sortForm";

		SortForm sortForm = searchForm.getSortForm();

		if ( sortForm == null ) sortForm = new SortForm();

		searchForm.setSortForm( sortForm );


		RestValidatorUtils.sortValidate( sortForm );

		// デフォルトは 利用時間 の降順
		List<SortElement> list = sortForm.getSortElement();
		if (list == null || list.isEmpty())
		{
			sortForm.addSortElement( new SortElement( "useTime", false ) );
		}

		RestValidatorUtils.sortConvert( sortForm );

		// ----- 検索条件

		name = "#useTimeForm";

		UseTimeSearchForm useTimeForm = searchForm.getUseTime();

		if ( useTimeForm == null ) useTimeForm = new UseTimeSearchForm();

		searchForm.setUseTime( useTimeForm );

		// ----- 年

		{
			name = "#useTime.year" ;
			String value = useTimeForm.getYear() ;

			// 月が設定されている場合または企業IDが設定されていない場合は必須
			boolean required = ValidatorUtils.required( useTimeForm.getMonth() ) || !ValidatorUtils.required( useTimeForm.getCompanyId() );
			RestValidatorUtils.fieldValidate( name, value, required, 4, 4 );

			// 入力時のみチェック
			if ( ValidatorUtils.required( value )) {
				if (!ValidatorUtils.regexp(value, "^[0-9]+$")) {
					throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_FORMAT, null, name ));
				}
			}
		}

		// ----- 月

		{
			name = "#useTime.month" ;
			String svalue = useTimeForm.getMonth() ;

			// 年が設定されている場合または企業IDが設定されていない場合は必須
			boolean required = ValidatorUtils.required( useTimeForm.getYear() ) || !ValidatorUtils.required( useTimeForm.getCompanyId() );
			RestValidatorUtils.fieldValidate( name, svalue, required, 1, 2 );

			// 入力時のみチェック
			if ( ValidatorUtils.required( svalue )) {
				// 形式チェック
				if (!ValidatorUtils.regexp(svalue, "^[0-9]+$")) {
					throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_FORMAT, null, name ));
				}

				// 数値範囲チェック
				Integer ivalue = Integer.parseInt(svalue);
				if (!ValidatorUtils.range(ivalue, 1, 12)) {
					throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_RANGE, null, name ));
				}
			}
		}

		// ----- 代理店企業ID
		{
			useTimeForm.setAgencyCompanyId(null); // 外部からは検索条件にはしない

			// システム管理者権限を持っている場合は代理店企業IDを設定しない(全件検索)
			if (!this.authUtil.isSysAdmin() && this.authUtil.isAgency()) {
				useTimeForm.setAgencyCompanyId(this.loginUtility.getCustomUser().getCompanyId());
			}
		}
	}

	/**
	 * ユーザ毎利用時間検索の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForSearchByUserId( UseTimeRequest req ) {

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

		// ----- ソート条件（指定がなければ利用時間降順を設定）

		name = "#sortForm";

		SortForm sortForm = searchForm.getSortForm();

		if ( sortForm == null ) sortForm = new SortForm();

		searchForm.setSortForm( sortForm );

		RestValidatorUtils.sortValidate( sortForm );

		// デフォルトは 利用時間 の降順
		List<SortElement> list = sortForm.getSortElement();
		if (list == null || list.isEmpty())
		{
			sortForm.addSortElement( new SortElement( "useTime", false ) );
		}

		RestValidatorUtils.sortConvert( sortForm );

		// ----- 検索条件

		name = "#useTimeForm";

		UseTimeSearchForm useTimeForm = searchForm.getUseTime();

		if ( useTimeForm == null ) useTimeForm = new UseTimeSearchForm();

		searchForm.setUseTime( useTimeForm );

		// ----- 企業ID

		{
			name = "#useTime.companyId";
			String value = useTimeForm.getCompanyId();

			RestValidatorUtils.fieldValidate( name, value, true, null, null );
		}

		// ----- 年

		{
			name = "#useTime.year" ;
			String value = useTimeForm.getYear() ;

			// 月が設定されている場合またはユーザIDが設定されていない場合は必須
			boolean required = ValidatorUtils.required( useTimeForm.getMonth() ) || !ValidatorUtils.required( useTimeForm.getUserId() );
			RestValidatorUtils.fieldValidate( name, value, required, 4, 4 );

			// 入力時のみチェック
			if ( ValidatorUtils.required( value )) {
				if (!ValidatorUtils.regexp(value, "^[0-9]+$")) {
					throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_FORMAT, null, name ));
				}
			}
		}

		// ----- 月

		{
			name = "#useTime.month" ;
			String svalue = useTimeForm.getMonth() ;

			// 年が設定されている場合またはユーザIDが設定されていない場合は必須
			boolean required = ValidatorUtils.required( useTimeForm.getYear() ) || !ValidatorUtils.required( useTimeForm.getUserId() );
			RestValidatorUtils.fieldValidate( name, svalue, required, 1, 2 );

			// 入力時のみチェック
			if ( ValidatorUtils.required( svalue )) {
				// 形式チェック
				if (!ValidatorUtils.regexp(svalue, "^[0-9]+$")) {
					throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_FORMAT, null, name ));
				}

				// 数値範囲チェック
				Integer ivalue = Integer.parseInt(svalue);
				if (!ValidatorUtils.range(ivalue, 1, 12)) {
					throw new RestException( new RestResult(ResponseCode.INPUT_ERROR_RANGE, null, name ));
				}
			}
		}

		// ----- 代理店企業ID
		{
			useTimeForm.setAgencyCompanyId(null); // 外部からは検索条件にはしない

			// システム管理者権限を持っている場合は代理店企業IDを設定しない(全件検索)
			if (!this.authUtil.isSysAdmin() && this.authUtil.isAgency()) {
				useTimeForm.setAgencyCompanyId(this.loginUtility.getCustomUser().getCompanyId());
			}
		}
	}
}
