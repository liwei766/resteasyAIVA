/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：RecaiusLicenseRestValidator.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.recaiuslicense;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.api.app.companymanagement.CompanyManagementService;
import cloud.optim.aivoiceanalytics.api.app.recaiuslicense.RecaiusLicenseRequest.EditForm;
import cloud.optim.aivoiceanalytics.api.entity.RecaiusLicense;
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
 * RecaiusLicenseRestService のバリデータクラス.
 * <p>入力チェックと入力内容の補完を行う.</p>
 */
@Component
class RecaiusLicenseRestValidator {

  /** Commons Logging instance. */
  @SuppressWarnings("unused")
  private Log log = LogFactory.getLog(this.getClass());

  @Resource
  private LoginUtility loginUtility;

  @Resource
  private RecaiusLicenseService recaiusLicenseService;

  @Resource
  private CompanyManagementService companyManagementService;

  /** 最大取得件数. */
  @Value("${recaiuslicense.max.result.count}")
  private long maxResultCount;

  /**
   * 検索の入力チェックと入力内容の補完.
   *
   * @param req 入力内容
   */
  public void validateForSearch(RecaiusLicenseRequest req) {
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
      sortForm.addSortElement(new SortElement("recaiuslicense.recaiusLicenseId", false));
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
    name = "#recaiusLicenseForm";
    RecaiusLicenseSearchForm recaiusLicenseForm = searchForm.getRecaiusLicense();
    if (recaiusLicenseForm == null) {
      recaiusLicenseForm = new RecaiusLicenseSearchForm();
    }
    searchForm.setRecaiusLicense(recaiusLicenseForm);

    // ----- 代理店企業ID
    {
      name = "#recaiusLicense.agencyCompanyIdOption";
      String value = recaiusLicenseForm.getAgencyCompanyIdOption();
      RestValidatorUtils.fieldValidate(name, value,
          ValidatorUtils.required(recaiusLicenseForm.getAgencyCompanyId()), null, null);
      RestValidatorUtils.in(name, value, "0", "1", "2", "3");
    }

  }

  // -------------------------------------------------------------------------

  /**
   * RecaiusLicense 取得の入力チェックと入力内容の補完.
   *
   * @param req 入力内容
   */
  public void validateForGet(RecaiusLicenseRequest req) {
    String name = "#request";
    if (req == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    // ----- 取得条件
    name = "#editForm";
    EditForm editForm = req.getEditForm();
    if (editForm == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    name = "#recaiusLicense";
    RecaiusLicense recaiusLicense = editForm.getRecaiusLicense();
    if (recaiusLicense == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    name = "#recaiusLicense.recaiusLicenseId"; // PK
    Long pkvalue = recaiusLicense.getRecaiusLicenseId();

    RestValidatorUtils.fieldValidate(name, pkvalue, true, 1L, Long.MAX_VALUE);
  }

  // -------------------------------------------------------------------------

  /**
   * 登録の入力チェックと入力内容の補完.
   *
   * @param req 入力内容
   *
   * @throws SQLException 例外発生時
   */
  public void validateForPut(RecaiusLicenseRequest req) throws SQLException {
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

    name = "#recaiusLicense";
    RecaiusLicense recaiusLicense = editForm.getRecaiusLicense();
    if (recaiusLicense == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    CustomUser customUser = loginUtility.getCustomUser();
    final String userId = customUser.getUserId();
    final String userName = customUser.getUserName();
    final Date now  = new Date();

    // ********** 個別項目

    // ----- リカイアスライセンス ID
    recaiusLicense.setRecaiusLicenseId(null); // 自動採番

    // ----- 更新日時
    recaiusLicense.setUpdateDate(now);

    // ----- サービス利用ID
    {
      name = "#recaiusLicense.serviceId";
      String value = ValidatorUtils.trim(recaiusLicense.getServiceId());
      RestValidatorUtils.fieldValidate(name, value, true, null, 100);

      if (!ValidatorUtils.ascii(value)) {
        throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_LETTER_TYPE, null, name));
      }

      if (this.recaiusLicenseService.isDuplicateServiceId(recaiusLicense.getServiceId())) {
        throw new RestException(new RestResult(ResponseCode.RECAIUS_LICENSE_DUPLICATE_SERVICE_ID,
            null, "#recaiusLicense.serviceId"));
      }

      recaiusLicense.setServiceId(value);
    }

    // ----- パスワード
    {
      name = "#recaiusLicense.password";
      String value = recaiusLicense.getPassword();
      RestValidatorUtils.fieldValidate(name, value, true, null, 100);

      if (!ValidatorUtils.ascii(value)) {
        throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_LETTER_TYPE, null, name));
      }
    }

    // ----- 代理店企業ID
    {
      name = "#recaiusLicense.agencyCompanyId";
      String value = ValidatorUtils.trim(recaiusLicense.getAgencyCompanyId());
      RestValidatorUtils.fieldValidate(name, value, false, null, 32);

      if (!ValidatorUtils.ascii(value)) {
        throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_LETTER_TYPE, null, name));
      }

      recaiusLicense.setAgencyCompanyId(value);
    }

    // ----- 作成日時
    recaiusLicense.setCreateDate(now);

    // ----- 作成ユーザ ID
    recaiusLicense.setCreateUserId(userId);

    // ----- 作成ユーザ名
    recaiusLicense.setCreateUserName(userName);

    // ----- 更新ユーザ ID
    recaiusLicense.setUpdateUserId(userId);

    // ----- 更新ユーザ名
    recaiusLicense.setUpdateUserName(userName);
  }

  // -------------------------------------------------------------------------

  /**
   * 更新の入力チェックと入力内容の補完.
   *
   * @param req 入力内容
   *
   * @throws SQLException 例外発生時
   */
  public void validateForUpdate(RecaiusLicenseRequest req) throws SQLException {
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

    name = "#recaiusLicense";
    RecaiusLicense recaiusLicense = editForm.getRecaiusLicense();
    if (recaiusLicense == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    CustomUser customUser = loginUtility.getCustomUser();
    final String userId = customUser.getUserId();
    final String userName = customUser.getUserName();

    // ********** 個別項目

    // ----- リカイアスライセンス ID
    name = "#recaiusLicense.recaiusLicenseId";
    Long pkValue = recaiusLicense.getRecaiusLicenseId();
    RestValidatorUtils.fieldValidate(name, pkValue, true, null, null);
    RestValidatorUtils.fieldValidate(name, String.valueOf(pkValue), false, null, 19);
    // 存在チェック
    RecaiusLicense prevRecaiusLicense =
        recaiusLicenseService.getRecaiusLicense(pkValue);
    if (prevRecaiusLicense == null) {
      throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, name, pkValue));
    }

    // ----- 更新日時
    // 楽観ロックチェックを行うためリクエストの値のままにする

    // ----- サービス利用ID
    {
      name = "#recaiusLicense.serviceId";
      String value = ValidatorUtils.trim(recaiusLicense.getServiceId());
      RestValidatorUtils.fieldValidate(name, value, true, null, 100);

      if (!ValidatorUtils.ascii(value)) {
        throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_LETTER_TYPE, null, name));
      }

      // サービスIDが変更されている場合は企業管理で使用されているかと重複チェックをする
      if (!recaiusLicense.getServiceId().equals(prevRecaiusLicense.getServiceId())) {
        // 利用中の企業有無チェック
        if (companyManagementService.isInUseRecaiusLicense(recaiusLicense.getRecaiusLicenseId())) {
          throw new RestException(new RestResult(ResponseCode.RECAIUS_LICENSE_IN_USE_ERROR, null,
              "#recaiusLicense.serviceId"));
        }

        // 重複チェック
        if (this.recaiusLicenseService.isDuplicateServiceId(recaiusLicense.getServiceId())) {
          throw new RestException(new RestResult(ResponseCode.RECAIUS_LICENSE_DUPLICATE_SERVICE_ID,
              null, "#recaiusLicense.serviceId"));
        }
      }

      recaiusLicense.setServiceId(value);
    }

    // ----- パスワード
    {
      name = "#recaiusLicense.password";
      String value = recaiusLicense.getPassword();

      // パスワードの入力がある場合のみチェックする
      if (ValidatorUtils.required(value)) {

        RestValidatorUtils.fieldValidate(name, value, true, null, 100);

        if (!ValidatorUtils.ascii(value)) {
          throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_LETTER_TYPE, null, name));
        }
      } else {
        // 未入力の場合は元の値で補完する
        recaiusLicense.setPassword(prevRecaiusLicense.getPassword());
      }
    }

    // ----- 代理店企業ID
    {
      name = "#recaiusLicense.agencyCompanyId";
      String value = ValidatorUtils.trim(recaiusLicense.getAgencyCompanyId());
      RestValidatorUtils.fieldValidate(name, value, false, null, 32);

      if (!ValidatorUtils.ascii(value)) {
        throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_LETTER_TYPE, null, name));
      }

      recaiusLicense.setAgencyCompanyId(value);
    }

    // ----- 作成日時
    // フレームワークが処理するからなにもしない！

    // ----- 作成ユーザ ID
    // フレームワークが処理するからなにもしない！

    // ----- 作成ユーザ名
    // フレームワークが処理するからなにもしない！

    // ----- 更新ユーザ ID
    recaiusLicense.setUpdateUserId(userId);

    // ----- 更新ユーザ名
    recaiusLicense.setUpdateUserName(userName);
  }

  // -------------------------------------------------------------------------

  /**
   * 削除の入力チェックと入力内容の補完.
   *
   * @param req 入力内容
   */
  public void validateForDelete(RecaiusLicenseRequest req) {
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
   * @param req 入力内容
   */
  public void validateForDeleteOne(SearchResult req) {
    // ----- 削除条件
    String name = "#bulkForm";
    if (req == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    name = "#recaiusLicense";
    RecaiusLicenseSearchResult recaiusLicense = req.getRecaiusLicense();
    if (recaiusLicense == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    // ----- PK
    name = "#recaiusLicense.recaiusLicenseId";
    Long pkvalue = recaiusLicense.getRecaiusLicenseId();
    RestValidatorUtils.fieldValidate(name, pkvalue, true, null, null);

    // 存在チェック
    if (!recaiusLicenseService.contains(pkvalue)) {
      throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, name));
    }

    // 利用中の企業有無チェック
    if (companyManagementService.isInUseRecaiusLicense(recaiusLicense.getRecaiusLicenseId())) {
      throw new RestException(
          new RestResult(ResponseCode.RECAIUS_LICENSE_IN_USE_ERROR, null, name));
    }
  }

}
