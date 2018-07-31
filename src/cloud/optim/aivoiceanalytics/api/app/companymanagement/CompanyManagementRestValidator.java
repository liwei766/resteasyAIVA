/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CompanyManagementRestValidator.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.companymanagement;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.api.app.companymanagement.CompanyManagementRequest.EditForm;
import cloud.optim.aivoiceanalytics.api.app.recaiuslicense.RecaiusLicenseService;
import cloud.optim.aivoiceanalytics.api.entity.CompanyManagement;
import cloud.optim.aivoiceanalytics.api.recaius.service.RecaiusAuthService;
import cloud.optim.aivoiceanalytics.api.util.AuthUtil;
import cloud.optim.aivoiceanalytics.core.common.utility.HankakuKanaConverter;
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
 * CompanyManagementRestService のバリデータクラス. （入力チェックと入力内容の補完を行う.）
 */
@Component
class CompanyManagementRestValidator {

	/** Commons Logging instance. */
	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass());

	@Resource
	private LoginUtility loginUtility;

	@Resource
	private AuthUtil authUtil;

	@Resource
	private RecaiusAuthService authService;

	@Resource
	private RecaiusLicenseService recaiusLicenseService;

	@Resource
	private CompanyManagementService companyManagementService;

	/** 最大取得件数. */
	@Value("${companymanagement.max.result.count}")
	private long maxResultCount;

	/** 最大制限IPアドレス登録件数. */
	@Value("${companymanagement.max.permitipaddress.count}")
	private Integer permitIpAddressMaxCount;

	/**
	 * 検索の入力チェックと入力内容の補完.
	 *
	 * @param req
	 *            入力内容
	 */
	public void validateForSearch(CompanyManagementRequest req) {
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

		// ----- ソート条件（指定がなければソートしない）
		name = "#sortForm";
		SortForm sortForm = searchForm.getSortForm();
		if (sortForm == null) {
			sortForm = new SortForm();
		}
		// デフォルトソート条件の設定
		if (sortForm.getSortElement() == null || sortForm.getSortElement().isEmpty()) {
			sortForm.setSortElement(new ArrayList<>());
			sortForm.addSortElement(new SortElement("companyManagement.companyManagementId", false));
		}
		searchForm.setSortForm(sortForm);

		// 最大取得件数
		Long limit = sortForm.getMaxResult();
		if (limit == null || limit < 0L || limit > maxResultCount) {
			sortForm.setMaxResult(maxResultCount);
		}
		sortForm.setMaxResult(sortForm.getMaxResult());

		// オフセットはとりあえずNULLを設定(今後使用する場合は要削除)
		sortForm.setOffset(null);

		RestValidatorUtils.sortValidate(sortForm);
		RestValidatorUtils.sortConvert(sortForm);

		// ----- 検索条件
		name = "#companyManagementForm";
		CompanyManagementSearchForm companyManagementForm = searchForm.getCompanyManagement();
		if (companyManagementForm == null) {
			companyManagementForm = new CompanyManagementSearchForm();
		}
		searchForm.setCompanyManagement(companyManagementForm);

		// ----- 企業名
		{
			name = "#companyManagement.companyNameOption";
			String value = companyManagementForm.getCompanyNameOption();

			RestValidatorUtils.fieldValidate(name, value,
					ValidatorUtils.required(companyManagementForm.getCompanyName()), null, null);
			RestValidatorUtils.in(name, value, "0", "1", "2", "3");
		}
		// ----- 代理店企業ID
		{
			companyManagementForm.setAgencyCompanyId(null); // 外部からは検索条件にはしない

			// システム管理者権限を持っている場合は代理店企業IDを設定しない(全件検索)
			if (!this.authUtil.isSysAdmin() && this.authUtil.isAgency()) {
				companyManagementForm.setAgencyCompanyId(this.loginUtility.getCustomUser().getCompanyId());
			}
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * CompanyManagement 取得の入力チェックと入力内容の補完.
	 *
	 * @param req
	 *            入力内容
	 */
	public void validateForGet(CompanyManagementRequest req) {
		// ----- 取得条件
		String name = "#request";
		if (req == null) {
			throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
		}

		name = "#editForm";
		EditForm editForm = req.getEditForm();
		if (editForm == null) {
			throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
		}

		name = "#companyManagement";
		CompanyManagement companyManagement = editForm.getCompanyManagement();
		if (companyManagement == null) {
			throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
		}

		// ----- PK
		name = "#companyManagement.companyManagementId";
		Long pkvalue = companyManagement.getCompanyManagementId();

		RestValidatorUtils.fieldValidate(name, pkvalue, true, null, null);
	}

	// -------------------------------------------------------------------------

	/**
	 * 登録の入力チェックと入力内容の補完.
	 *
	 * @param req
	 *            入力内容
	 *
	 * @throws SQLException
	 *             例外発生時
	 */
	public void validateForPut(CompanyManagementRequest req) throws SQLException {
		// ********** 登録内容
		String name = "#request";
		if (req == null) {
			throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
		}

		name = "#editForm";
		EditForm editForm = req.getEditForm();
		if (editForm == null) {
			throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
		}

		name = "#companyManagement";
		CompanyManagement companyManagement = editForm.getCompanyManagement();
		if (companyManagement == null) {
			throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
		}

		CustomUser customUser = loginUtility.getCustomUser();
		final String userId = customUser.getUserId();
		final String userName = customUser.getUserName();
		final Date now = new Date();

		// ********** 個別項目

		// ----- 更新日時
		companyManagement.setUpdateDate(now);

		// ----- 企業 ID
		{
			name = "#companyManagement.companyId";
			String value = companyManagement.getCompanyId();

			// トリムする
			value = ValidatorUtils.trim(value);

			RestValidatorUtils.fieldValidate(name, value, true, null, 32);

			if (!ValidatorUtils.ascii(value)) {
				throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_LETTER_TYPE, null, name));
			}

			if (companyManagementService.isDuplicateCompanyId(value)) {
				throw new RestException(new RestResult(ResponseCode.COMPANY_MANAGEMENT_DUPLICATE_COMPANY_ID, null,
						"#companyManagement.companyId"));
			}

			companyManagement.setCompanyId(value);
		}

		// ----- 企業名
		{
			name = "#companyManagement.companyName";
			String value = companyManagement.getCompanyName();

			RestValidatorUtils.fieldValidate(name, value, false, null, 100);

			companyManagement.setCompanyName(HankakuKanaConverter.convert(value));
		}

		// ----- 代理店企業ID
		String companyId = loginUtility.getCustomUser().getCompanyId();
		companyManagement.setAgencyCompanyId(companyId);

		// ----- リカイアスライセンスID
		// 後続処理で取得したものをセットするのでなにもしない

		// ----- リカイアスモデルID
		// 後続処理で取得したものをセットするのでなにもしない

		// ----- 音声判断レベル閾値
		{
			name = "#companyManagement.energyThreshold";
			Integer value = companyManagement.getEnergyThreshold();

			RestValidatorUtils.fieldValidate(name, value, true, 1L, 1000L);
		}

	    // ----- 音声保存設定
	    {
	      name = "#companyManagement.saveVoice" ;
	      Boolean value = companyManagement.getSaveVoice() ;

	      RestValidatorUtils.fieldValidate( name, value, false, null, null );
	    }

		// ----- 接続元制限IPアドレス
		{
			String value = companyManagement.getPermitIpAddress();

			this.checkPermitIpAddress(value);
		}

		// ----- 作成日時
		companyManagement.setCreateDate(now);

		// ----- 作成ユーザ ID
		companyManagement.setCreateUserId(userId);

		// ----- 作成ユーザ名
		companyManagement.setCreateUserName(userName);

		// ----- 更新ユーザ ID
		companyManagement.setUpdateUserId(userId);

		// ----- 更新ユーザ名
		companyManagement.setUpdateUserName(userName);
	}

	// -------------------------------------------------------------------------

	/**
	 * 更新の入力チェックと入力内容の補完.
	 *
	 * @param req
	 *            入力内容
	 *
	 * @throws SQLException
	 *             例外発生時
	 */
	public void validateForUpdate(CompanyManagementRequest req) throws SQLException {
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

		name = "#companyManagement";
		CompanyManagement companyManagement = editForm.getCompanyManagement();
		if (companyManagement == null) {
			throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
		}

		CustomUser customUser = loginUtility.getCustomUser();
		final String userId = customUser.getUserId();
		final String userName = customUser.getUserName();

		// ********** 個別項目

		// ----- 企業管理 ID
		name = "#companyManagement.companyManagementId";
		Long pkValue = companyManagement.getCompanyManagementId();
		RestValidatorUtils.fieldValidate(name, pkValue, true, null, null);
		RestValidatorUtils.fieldValidate(name, String.valueOf(pkValue), false, null, 19);
		// 存在チェック
		CompanyManagement prevCompanyManagement = companyManagementService.getCompanyManagement(pkValue);
		if (prevCompanyManagement == null) {
			throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, name, pkValue));
		}

		// システム管理者でない場合は代理店企業IDがログインユーザの企業IDと一致するかチェックする
		if (!this.validateAcgencyId(prevCompanyManagement)) {
			throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, name, pkValue));
		}

		// ----- 更新日時
		// 楽観ロックチェックを行うためリクエストの値のままにする

		// ----- 企業 ID
		{
			name = "#companyManagement.companyId";
			String value = companyManagement.getCompanyId();

			// トリムする
			value = ValidatorUtils.trim(value);

			RestValidatorUtils.fieldValidate(name, value, true, null, 32);

			if (!ValidatorUtils.ascii(value)) {
				throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_LETTER_TYPE, null, name));
			}

			companyManagement.setCompanyId(value);
		}

		// ----- 企業名
		{
			name = "#companyManagement.companyName";
			String value = companyManagement.getCompanyName();

			RestValidatorUtils.fieldValidate(name, value, false, null, 100);
		}

		// ----- 代理店企業ID
		{
			name = "#companyManagement.agencyCompanyId";
			String value = companyManagement.getAgencyCompanyId();

			RestValidatorUtils.fieldValidate(name, value, true, null, 32);
		}

		// ----- リカイアスライセンスID
		companyManagement.setRecaiusLicenseId(prevCompanyManagement.getRecaiusLicenseId()); // 更新禁止

		// ----- リカイアスモデルID
		companyManagement.setRecaiusModelId(prevCompanyManagement.getRecaiusModelId()); // 更新禁止

		// ----- 音声判断レベル閾値
		{
			name = "#companyManagement.energyThreshold";
			Integer value = companyManagement.getEnergyThreshold();

			RestValidatorUtils.fieldValidate(name, value, true, 1L, 1000L);
		}

	    // ----- 音声保存設定
	    {
	      name = "#companyManagement.saveVoice" ;
	      Boolean value = companyManagement.getSaveVoice() ;

	      RestValidatorUtils.fieldValidate( name, value, false, null, null );
	    }

		// ----- 接続元制限IPアドレス
		{
			String value = companyManagement.getPermitIpAddress();

			this.checkPermitIpAddress(value);
		}

		// ----- 作成日時
		// フレームワークが処理するからなにもしない！

		// ----- 作成ユーザ ID
		// フレームワークが処理するからなにもしない！

		// ----- 作成ユーザ名
		// フレームワークが処理するからなにもしない！

		// ----- 更新ユーザ ID
		companyManagement.setUpdateUserId(userId);

		// ----- 更新ユーザ名
		companyManagement.setUpdateUserName(userName);
	}

	// -------------------------------------------------------------------------

	/**
	 * 削除の入力チェックと入力内容の補完.
	 *
	 * @param req
	 *            入力内容
	 */
	public void validateForDelete(CompanyManagementRequest req) {
		// ----- 削除条件
		String name = "#request";
		if (req == null) {
			throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
		}

		name = "#bulkFormList";
		List<SearchResult> bulkFormList = req.getBulkFormList();

		if (bulkFormList == null) {
			throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
		}
	}

	/**
	 * 削除の入力チェックと入力内容の補完.
	 *
	 * @param req
	 *            入力内容
	 */
	public void validateForDeleteOne(SearchResult req) {
		// ----- 削除条件
		String name = "#bulkForm";
		if (req == null) {
			throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
		}

		name = "#companyManagement";
		CompanyManagementSearchResult companyManagement = req.getCompanyManagement();

		if (companyManagement == null) {
			throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
		}

		// ----- PK
		name = "#companyManagement.companyManagementId";
		Long pkValue = companyManagement.getCompanyManagementId();

		RestValidatorUtils.fieldValidate(name, pkValue, true, null, null);

		// 存在チェック
		CompanyManagement prevCompanyManagement = companyManagementService.getCompanyManagement(pkValue);
		if (prevCompanyManagement == null) {
			throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, name));
		}

		// システム管理者でない場合は代理店企業IDがログインユーザの企業IDと一致するかチェックする
		if (!this.validateAcgencyId(prevCompanyManagement)) {
			throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, name, pkValue));
		}
	}


	/**
	 * システム管理者でない場合は代理店企業IDがログインユーザの企業IDと一致するかチェックする.
	 *
	 * @param companyManagement
	 *            企業管理情報
	 * @return OK:true / NG:false
	 */
	public boolean validateAcgencyId(final CompanyManagement companyManagement) {
		/* UT: つぶさないと NOT_FOUND になる */
		CustomUser customUser = loginUtility.getCustomUser();
		if (!authUtil.isSysAdmin() && !customUser.getCompanyId().equals(companyManagement.getAgencyCompanyId())) {
			return false;
		}
		// */
		return true;
	}


	/**
	 * 企業設定更新の入力チェックと入力内容の補完.
	 *
	 * @param req
	 *            入力内容
	 *
	 * @throws SQLException
	 *             例外発生時
	 */
	public void validateForUpdateCompanySettings(CompanySettingsRequest req) throws SQLException {
		// ********** 更新内容
		String name = "#request";
		if (req == null) {
			throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
		}

		name = "#companyManagement";
		CompanyManagement companyManagement = req.getCompanyManagement();
		if (companyManagement == null) {
			throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
		}


		// ----- 更新日時
		// 楽観ロックチェックを行うためリクエストの値のままにする

		// ----- 企業 ID
		companyManagement.setCompanyId(loginUtility.getCustomUser().getCompanyId());


		// ----- 音声判断レベル閾値
		{
			name = "#companyManagement.energyThreshold";
			Integer value = companyManagement.getEnergyThreshold();

			RestValidatorUtils.fieldValidate(name, value, true, 1L, 1000L);
		}

		// ----- 作成日時
		// フレームワークが処理するからなにもしない！

		// ----- 作成ユーザ ID
		// フレームワークが処理するからなにもしない！

		// ----- 作成ユーザ名
		// フレームワークが処理するからなにもしない！

		CustomUser customUser = loginUtility.getCustomUser();

		// ----- 更新ユーザ ID
		companyManagement.setUpdateUserId(customUser.getUserId());

		// ----- 更新ユーザ名
		companyManagement.setUpdateUserName(customUser.getUserName());
	}

	/**
	 * 接続元制限IPアドレスを1行ずつ読込各行の形式が正しいかチェックする
	 *
	 * @param permitIpAddress
	 *            IPアドレス
	 * @throws RestException
	 *             入力エラーがある場合
	 */
	private void checkPermitIpAddress(String permitIpAddress) {
		// 空の場合はチェックしない
		if (permitIpAddress == null || permitIpAddress.isEmpty())
			return;

		// 文字数チェック(3000文字まで)
		RestValidatorUtils.fieldValidate("#companyManagement.permitIpAddress", permitIpAddress, false, 0, 3000);

		// 改行コードで文字列を分割する
		String[] rowData = permitIpAddress.split("(\\r\\n|\\n|\\r)");
		int rowCount = 0;
		for (String each : rowData) {
			String text = each == null ? "" : each.trim();

			// 空行は飛ばす
			if (text.isEmpty())
				continue;

			// 形式エラー
			if (!isIpAddresFormat(text)) {
				throw new RestException(ResponseCode.INPUT_ERROR_FORMAT, text);
			}

			// 設定件数以上IPアドレスが設定されている場合はエラー
			if (++rowCount > permitIpAddressMaxCount) {
				throw new RestException(
						new RestResult(ResponseCode.COMPANY_MANAGEMENT_PERMIT_IP_ADDRESS_MAX_COUNT_ERROR,
								new Object[] { permitIpAddressMaxCount }, ""));
			}
		}
	}

	/**
	 * IPアドレスの形式チェックを行う
	 *
	 * @param checkString
	 *            検査対象文字列
	 * @return IPアドレスのフォーマットの場合:true、それ以外：false
	 */
	private boolean isIpAddresFormat(String checkString) {
		boolean isSubnetMask = (checkString.indexOf("/") >= 0);

		String address;
		String subnet;
		// サブネットビット取得
		if (isSubnetMask) {
			address = checkString.substring(0, checkString.indexOf("/"));
			subnet = checkString.substring(checkString.indexOf("/") + 1);
		} else {
			address = checkString;
			subnet = "";
		}

		// IPv4チェック
		String[] addArray = address.split("\\.");
		if (addArray.length == 4) {
			try {
				// IPアドレスの各桁が0～255以内の数値であるかチェックする
				for (String each : addArray) {
					int val = Integer.parseInt(each);
					if (val < 0 || val > 255)
						return false;
				}

				// サブネットマスクが設定されている場合数値が範囲内かチェックする
				if (isSubnetMask) {
					int val = Integer.parseInt(subnet);
					if (val < 0 || val > 32)
						return false;
				}
				// Inet4Addressのインスタンスを生成できない場合はエラー
				if (!(InetAddress.getByName(address) instanceof Inet4Address)) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
			return true;
		}

		// IPv6チェック
		addArray = address.split(":");
		if (addArray.length >= 2 && addArray.length <= 8) {
			try {
				int emptyCnt = 0;
				for (String each : addArray) {
					// 空要素は一個のみ許容する
					if (each == null || each.isEmpty()) {
						emptyCnt++;
						continue;
					}

					// 空要素が二つ以上の場合はエラー
					if (emptyCnt > 1)
						return false;

					// IPアドレスの各要素がが0～FFFF以内の16進数値であるかチェックする
					int val = Integer.parseInt(each, 16);
					if (val < 0x0000 || val > 0xFFFF)
						return false;
				}

				// サブネットマスクが設定されている場合数値が範囲内かチェックする
				if (isSubnetMask) {
					int val = Integer.parseInt(subnet);
					if (val < 0 || val > 128)
						return false;
				}

				// Inet6Addressのインスタンスを生成できない場合はエラー
				if (!(InetAddress.getByName(address) instanceof Inet6Address)) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
			return true;
		}

		// IPv4、IPv6の形式以外はエラー
		return false;
	}
}
