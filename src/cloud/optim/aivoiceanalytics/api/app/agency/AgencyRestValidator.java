/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：AgencyRestValidator.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.agency;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.api.app.agency.AgencyRequest.EditForm;
import cloud.optim.aivoiceanalytics.api.entity.Agency;
import cloud.optim.aivoiceanalytics.api.entity.dao.AgencyDao;
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
 * AgencyRestService のバリデータクラス.
 * <p>入力チェックと入力内容の補完を行う.</p>
 */
@Component
class AgencyRestValidator {

  /** Commons Logging instance. */
  @SuppressWarnings("unused")
  private Log log = LogFactory.getLog(this.getClass());

  /** LoginUtility. */
  @Resource
  private LoginUtility loginUtility;

  /** AgencyDao. */
  @Resource
  private AgencyDao agencyDao;

  /** 最大取得件数. */
  @Value("${agency.max.result.count}")
  private long maxResultCount;

  /**
   * 検索の入力チェックと入力内容の補完.
   *
   * @param req 入力内容
   */
  public void validateForSearch(AgencyRequest req) {
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
      sortForm.addSortElement(new SortElement("agency.agencyId", false));
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
    name = "#agencyForm";
    AgencySearchForm agencyForm = searchForm.getAgency();
    if (agencyForm == null) {
      agencyForm = new AgencySearchForm();
    }
    searchForm.setAgency(agencyForm);

    // ----- 代理店企業ID
    {
      name = "#agency.agencyCompanyIdOption";
      String value = agencyForm.getAgencyCompanyIdOption();
      RestValidatorUtils.fieldValidate(name, value,
          ValidatorUtils.required(agencyForm.getAgencyCompanyId()), null, null);
      RestValidatorUtils.in(name, value, "0", "1", "2", "3");
    }

  }

  // -------------------------------------------------------------------------

  /**
   * Agency 取得の入力チェックと入力内容の補完.
   *
   * @param req 入力内容
   */
  public void validateForGet(AgencyRequest req) {
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

    name = "#agency";
    Agency agency = editForm.getAgency();
    if (agency == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    name = "#agency.agencyId"; // PK
    Long pkvalue = agency.getAgencyId();

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
  public void validateForPut(AgencyRequest req) throws SQLException {
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

    name = "#agency";
    Agency agency = editForm.getAgency();
    if (agency == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    // ********** 個別項目

    // ----- 代理店企業ID
    {
      name = "#agency.agencyCompanyId";
      String value = ValidatorUtils.trim(agency.getAgencyCompanyId());
      if (StringUtils.isEmpty(value)) {
        throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
      }

      RestValidatorUtils.fieldValidate(name, value, false, null, 32);

      if (!ValidatorUtils.ascii(value)) {
        throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_LETTER_TYPE, null, name));
      }

      agency.setAgencyCompanyId(value);

      this.validateDuplicateAgencyCompanyId(agency);
    }

    CustomUser customUser = loginUtility.getCustomUser();
    final String userId = customUser.getUserId();
    final String userName = customUser.getUserName();

    Date now  = new Date();

    // ----- 代理店ID
    agency.setAgencyId(null);

    // ----- 更新日時
    agency.setUpdateDate(now);

    // ----- 作成日時
    agency.setCreateDate(now);

    // ----- 作成ユーザ ID
    agency.setCreateUserId(userId);

    // ----- 作成ユーザ名
    agency.setCreateUserName(userName);

    // ----- 更新ユーザ ID
    agency.setUpdateUserId(userId);

    // ----- 更新ユーザ名
    agency.setUpdateUserName(userName);
  }

  // -------------------------------------------------------------------------

  /**
   * 削除の入力チェックと入力内容の補完.
   *
   * @param req 入力内容
   */
  public void validateForDelete(AgencyRequest req) {
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

    name = "#agency";
    AgencySearchResult agency = req.getAgency();
    if (agency == null) {
      throw new RestException(new RestResult(ResponseCode.INPUT_ERROR_REQUIRED, null, name));
    }

    // ----- PK
    name = "#agency.agencyId";
    Long pkvalue = agency.getAgencyId();
    RestValidatorUtils.fieldValidate(name, pkvalue, true, null, null);

    // 存在チェック
    Agency entity = agencyDao.get(pkvalue);
    if (entity == null) {
      throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, name));
    }
  }


  /**
   * 代理店管理情報の重複チェックをおこなう.
   * <p>同じ代理企業IDは登録できない.</p>
   * @param agency 代理店情報
   */
  private void validateDuplicateAgencyCompanyId(Agency agency) {
    String agencyCompanyId = agency.getAgencyCompanyId();
    Agency condition = new Agency();
    condition.setAgencyCompanyId(agencyCompanyId);
    List<Agency> agencyList = agencyDao.findByExample(condition);
    if (agencyList != null && !agencyList.isEmpty()) {
      throw new RestException(new RestResult(ResponseCode.AGENCY_DUPLICATE_COMPANY_ID,
          null, "#agency.agencyCompanyId"));
		}
	}

}
