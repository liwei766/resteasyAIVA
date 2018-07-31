/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CompanyManagementRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.companymanagement;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.api.app.companymanagement.CompanyManagementResponse.BulkResult;
import cloud.optim.aivoiceanalytics.api.app.companymanagement.CompanyManagementResponse.EditResult;
import cloud.optim.aivoiceanalytics.api.app.recaiuslicense.RecaiusLicenseService;
import cloud.optim.aivoiceanalytics.api.entity.CompanyManagement;
import cloud.optim.aivoiceanalytics.api.entity.RecaiusLicense;
import cloud.optim.aivoiceanalytics.api.recaius.service.RecaiusAuthService;
import cloud.optim.aivoiceanalytics.api.recaius.service.RecaiusSpeechService;
import cloud.optim.aivoiceanalytics.api.util.AuthUtil;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.LoginUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ExceptionUtil;
import cloud.optim.aivoiceanalytics.core.modules.rest.MessageUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestLog;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;

/**
 * CompanyManagementRestService 実装.
 */
@Path("/companymanagement")
@Consumes({"application/json", "application/xml"})
@Produces({"application/json", "application/xml"})
@Component
public class CompanyManagementRestService {

  /** Commons Logging instance. */
  private Log log = LogFactory.getLog(this.getClass());

  /** PK の項目名. */
  private static final String NAME_PK = "#companyManagement.companyManagementId";

  // -------------------------------------------------------------------------

  /** バリデータ. */
  @Resource
  private CompanyManagementRestValidator validator;

  /** CompanyManagementService. */
  @Resource
  private CompanyManagementService companyManagementService;

  @Resource
  private LoginUtility loginUtility;

  @Resource
  private AuthUtil authUtil;

  /** RestLog. */
  @Resource
  private RestLog restlog;

  /** MessageUtility. */
  @Resource
  private MessageUtility messageUtility;

  // -------------------------------------------------------------------------

  // リカイアス音声解析サービスタイプ.
  @Value("${recaius.service.speech.type}")
  private String serviceType;

  // リカイアスベースモデルID.
  @Value("${recaius.service.speech.base.model.id}")
  private int baseModelId;

  @Resource
  private RecaiusLicenseService recaiusLicenseService;

  @Resource
  private RecaiusAuthService authService;

  @Resource
  private RecaiusSpeechService recaiusSpeechService;

//  @Resource
//  private ExtractUtil extractor;

  // -------------------------------------------------------------------------


  /**
   * 検索.
   *
   * @param req 検索条件
   *
   * @return 検索結果
   */
  @POST
  @Path("/search")
  public CompanyManagementResponse search(CompanyManagementRequest req) {
    final String mName = "search";
    restlog.start(log, mName, req);

    try {
      // ----- 入力チェック
      // 検索条件の指定がない場合は全検索として扱う
      if (req == null) {
        req = new CompanyManagementRequest();
      }

      validator.validateForSearch(req);

      // ----- 検索
      SearchForm form = req.getSearchForm();
      List<SearchResult> list = companyManagementService.search(form);

      // ----- レスポンス作成
      CompanyManagementResponse res = new CompanyManagementResponse();

      // 検索結果件数超過チェック
      Long limit = form.getSortForm().getMaxResult();
      if (list.size() > limit) {
        res.setResult(new RestResult(ResponseCode.TOO_MANY_SEARCH_RESULT, new Object[] {limit},
            limit.toString()));
        list = list.subList(0, list.size() - 1);
      } else {
        res.setResult(new RestResult(ResponseCode.OK));
      }
      res.setSearchResultList(list);

      messageUtility.fillMessage(res.getResultList());
      restlog.end(log, mName, req, res, res.getResultList());

      return res;
    } catch (Exception ex) {

      throw ExceptionUtil.handleException(log, ResponseCode.SYS_ERROR, null, null, null, ex);
    }
  }

  // -------------------------------------------------------------------------

  /**
   * 取得.
   *
   * @param req 取得条件（PK 項目のみ使用）
   *
   * @return 取得エンティティ
   */
  @POST
  @Path("/get")
  public CompanyManagementResponse get(CompanyManagementRequest req) {
    final String mName = "get";
    restlog.start(log, mName, req);

    try {
      // ----- 入力チェック
      validator.validateForGet(req);

      // ----- 入力データ取得
      CompanyManagement companyManagement = req.getEditForm().getCompanyManagement();

      // ----- 取得
      CompanyManagement entity =
          companyManagementService.getCompanyManagement(companyManagement.getCompanyManagementId());

      if (entity == null) {
        throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, NAME_PK,
            companyManagement.getCompanyManagementId()));
      }

      // システム管理者でない場合は代理店企業IDがログインユーザの企業IDと一致するかチェックする
      if (!this.validator.validateAcgencyId(entity)) {
        throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, NAME_PK,
            companyManagement.getCompanyManagementId()));
      }

      // ----- レスポンス作成
      CompanyManagementResponse res = this.createInquiryFormResponse(entity);

      messageUtility.fillMessage(res.getResultList());
      restlog.end(log, mName, req, res, res.getResultList());

      return res;
    } catch (Exception ex) {
      throw ExceptionUtil.handleException(log, ResponseCode.SYS_ERROR, null, null, null, ex);
    }
  }

  // -------------------------------------------------------------------------

  /**
   * 登録.
   *
   * @param req 登録内容
   *
   * @return 処理結果と登録内容
   */
  @POST
  @Path("/put")
  public CompanyManagementResponse put(CompanyManagementRequest req) {
    final String mName = "put";
    restlog.start(log, mName, req);

    try {
      // ----- 入力チェック
      validator.validateForPut(req);

      // ----- 入力データ取得
      CompanyManagement companyManagement = req.getEditForm().getCompanyManagement();

      // リカイアスモデルID生成
      RecaiusLicense recaiusLicense =
          this.selectRecaiusLicenseForCreateModelId(companyManagement.getAgencyCompanyId());
      //     this.selectRecaiusLicenseForCreateModelId("2"); // UT で通過させるために埋め込む
      companyManagement.setRecaiusLicenseId(recaiusLicense.getRecaiusLicenseId());
      int recaiusModelId = this.createModel(recaiusLicense);
      companyManagement.setRecaiusModelId(recaiusModelId);

      // ----- 登録
      companyManagement = companyManagementService.save(companyManagement);

//      // Kuromoji 形態素解析インスタンス追加
//      this.extractor.addAnalyzer(companyManagement.getCompanyManagementId());

      // ----- レスポンス作成
      CompanyManagementResponse res = new CompanyManagementResponse();
      res.setResult(new RestResult(ResponseCode.OK));
      res.setEditResult(new EditResult());
      res.getEditResult().setCompanyManagement(companyManagement);

      messageUtility.fillMessage(res.getResultList());
      restlog.end(log, mName, req, res, res.getResultList());

      return res;
    } catch (Exception ex) {
      throw ExceptionUtil.handleException(log, ResponseCode.SYS_ERROR, null, null, null, ex);
    }
  }

  // -------------------------------------------------------------------------

  /**
   * 更新.
   *
   * @param req 更新内容
   *
   * @return 処理結果と更新内容
   */
  @POST
  @Path("/update")
  public CompanyManagementResponse update(CompanyManagementRequest req) {
    final String mName = "update";
    restlog.start(log, mName, req);

    try {
      // ----- 入力チェック
      validator.validateForUpdate(req);

      // ----- 入力データ取得
      CompanyManagement companyManagement = req.getEditForm().getCompanyManagement();

      // ----- 更新
      companyManagement = companyManagementService.update(companyManagement);

      // ----- レスポンス作成
      CompanyManagementResponse res = new CompanyManagementResponse();
      res.setResult(new RestResult(ResponseCode.OK));
      res.setEditResult(new EditResult());
      res.getEditResult().setCompanyManagement(companyManagement);

      messageUtility.fillMessage(res.getResultList());
      restlog.end(log, mName, req, res, res.getResultList());

      return res;
    } catch (Exception ex) {
      throw ExceptionUtil.handleException(log, ResponseCode.SYS_ERROR, null, null, null, ex);
    }
  }

  // -------------------------------------------------------------------------

  /**
   * 一括削除.
   *
   * @param req 取得条件（PK 項目のみ使用）
   *
   * @return 処理結果ステータスのみ
   */
  @POST
  @Path("/delete")
  public CompanyManagementResponse delete(CompanyManagementRequest req) {
    final String mName = "delete";
    restlog.start(log, mName, req);

    try {
      // ----- 入力チェック
      validator.validateForDelete(req);

      // ----- 1 件ずつ削除
      boolean error = false;
      CompanyManagementResponse res = new CompanyManagementResponse();
      res.setBulkResultList(new ArrayList<BulkResult>());
      for (SearchResult form : req.getBulkFormList()) {
        BulkResult result = new BulkResult();

        try {
          deleteOne(form);

          // ----- レスポンス作成
          result.setResult(new RestResult(ResponseCode.OK));
          result.setCompanyManagement(form.getCompanyManagement());

          messageUtility.fillMessage(result.getResultList());
          restlog.endOne(log, mName, result, result.getResultList());
        } catch (Exception ex) {
          error = true;

          // 応答結果を作成
          result.setResultList(
              ExceptionUtil.handleException(log, ResponseCode.SYS_ERROR, null, null, null, ex)
                  .getRestResultList());
          result.setCompanyManagement(form.getCompanyManagement());

          messageUtility.fillMessage(result.getResultList());
          restlog.abortOne(log, mName, result, result.getResultList(), ex);
        }

        res.getBulkResultList().add(result);
      }

      // ----- レスポンス作成
      if (error) {
        res.setResult(new RestResult(ResponseCode.PARTIAL));
      } else {
        res.setResult(new RestResult(ResponseCode.OK));
      }

      messageUtility.fillMessage(res.getResultList());
      restlog.end(log, mName, req, res, res.getResultList());

      return res;
    } catch (Exception ex) {
      throw ExceptionUtil.handleException(log, ResponseCode.SYS_ERROR, null, null, null, ex);
    }
  }

  /**
   * 1 件削除.
   *
   * @param form 削除する 1 コンテンツの情報
   *
   * @throws Exception エラー発生時
   */
  private void deleteOne(SearchResult form) throws Exception {
    // ----- 入力チェック
    validator.validateForDeleteOne(form);

    final long companyManagementId = form.getCompanyManagement().getCompanyManagementId();
    this.destroyModel(companyManagementId);
    // TODO 複数削除は利用されない想定だが、利用されるとトークンの作成破棄が複数実行されるから遅いかも？

    // ----- 削除
    companyManagementService.delete(companyManagementId);
  }


  /**
   * リカイアスモデル生成に利用できるリカイアスライセンスを取得する.
   * @param agencyCompanyId 代理店企業ID
   * @return リカイアスライセンス
   * @throws Exception リカイアスAPIの呼び出しに失敗した、利用できるライセンスがない
   */
  private RecaiusLicense selectRecaiusLicenseForCreateModelId(final String agencyCompanyId)
      throws Exception {
    //log.info("selectRecaiusLicenseForCreateModelId: enter. agencyCompanyId = " + agencyCompanyId);
    List<RecaiusLicense> recaiusLicenseList =
        this.recaiusLicenseService.selectByAgencyCompanyId(agencyCompanyId);
    //log.info("selectRecaiusLicenseForCreateModelId: recaiusLicenseList size = " + recaiusLicenseList.size());
    for (RecaiusLicense recaiusLicense : recaiusLicenseList) {
      final String serviceId = recaiusLicense.getServiceId();
      final String password = recaiusLicense.getPassword();
      //log.info("selectRecaiusLicenseForCreateModelId: serviceId = " + serviceId + " ,password = " + password);
      final String token = this.authService.auth(this.serviceType, serviceId, password);

      try {
    	  if (!this.recaiusSpeechService.isOverflowModelId(token)) {
          //log.info("selectRecaiusLicenseForCreateModelId: exit. service_id = " + recaiusLicense.getServiceId());
          return recaiusLicense;
        }
      } finally {
        this.authService.disconnect(token);
        // これ以上の例外処理は意味がないのでそのままスロー
      }
    }
    //log.info("selectRecaiusLicenseForCreateModelId: exit.");
    // すべての対象ライセンスが10個使われてしまっている
    throw new RestException(
        new RestResult(ResponseCode.RECAIUS_SPEECH_OVER_FLOW_MODEL_ID_ERROR, null, ""));
  }

  /**
   * リカイアスモデル生成.
   * @param recaiusLicense 作成に利用するリカイアスライセンス
   * @return 作成されたモデルID
   * @throws Exception リカイアスAPIの呼び出しに失敗した
   */
  private int createModel(final RecaiusLicense recaiusLicense) throws Exception {
    final String serviceId = recaiusLicense.getServiceId();
    final String password = recaiusLicense.getPassword();

    final String token = this.authService.auth(this.serviceType, serviceId, password);

    try {
      return this.recaiusSpeechService.createModelId(token, this.baseModelId);
    } finally {
      this.authService.disconnect(token);
      // これ以上の例外処理は意味がないのでそのままスロー
    }
  }

  /**
   * リカイアスモデル破棄.
   * @param companyManagementId 企業管理ID
   * @throws Exception リカイアスAPIの呼び出しに失敗した。
   */
  private void destroyModel(final long companyManagementId) throws Exception {
    CompanyManagement companyManagement =
        this.companyManagementService.getCompanyManagement(companyManagementId);
    // Validator で存在チェックが済んでいるのでここではしない！
    final long RecaiusLicenseId = companyManagement.getRecaiusLicenseId();
    final RecaiusLicense recaiusLicense = recaiusLicenseService.getRecaiusLicense(RecaiusLicenseId);
    final String serviceId = recaiusLicense.getServiceId();
    final String password = recaiusLicense.getPassword();

    final String token = this.authService.auth(this.serviceType, serviceId, password);

    final int recaiusModelId = companyManagement.getRecaiusModelId();
    try {
      this.recaiusSpeechService.destroyModelId(token, recaiusModelId);
    } finally {
      this.authService.disconnect(token);
      // これ以上の例外処理は意味がないのでそのままスロー
    }
  }

  /**
   * レスポンスを作成する.
   * @param companyManagement 企業管理情報
   * @return レスポンス
   */
  private final CompanyManagementResponse createInquiryFormResponse(
      final CompanyManagement companyManagement) {
    CompanyManagementResponse res = new CompanyManagementResponse();
    res.setResult(new RestResult(ResponseCode.OK));
    EditResult editResult = new EditResult();
    editResult.setCompanyManagement(companyManagement);
    res.setEditResult(editResult);
    return res;
  }

	// -------------------------------------------------------------------------

	/**
	 * 企業設定を取得
	 *
	 * @param ヌル
	 *
	 * @return 企業管理情報設定値
	 */
	@POST
	@Path( "/getCompanySettings" )
	public CompanySettingsResponse getCompanySettings( ) {

		String MNAME = "getCompanySettings";
		restlog.start( log, MNAME, null );

		try {

			CompanyManagement entity = companyManagementService.getCompanySettingsByCompanyId(loginUtility.getCustomUser().getCompanyId());

			// ----- レスポンス作成

			CompanySettingsResponse res = new CompanySettingsResponse();
			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setCompanyManagement(entity);

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, null, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}


	// -------------------------------------------------------------------------

	  /**
	   * 企業設定を更新.
	   *
	   * @param req 更新内容
	   *
	   * @return 処理結果と更新内容
	   */
	  @POST
	  @Path("/updateCompanySettings")
	  public CompanySettingsResponse updateCompanySettings(CompanySettingsRequest req) {
	    final String mName = "update";
	    restlog.start(log, mName, req);

	    try {
	      // ----- 入力チェック
	      validator.validateForUpdateCompanySettings(req);

	      // ----- 入力データ取得
	      CompanyManagement companyManagement = req.getCompanyManagement();

	      // ----- 更新
	      companyManagement = companyManagementService.updateCompanySettings(companyManagement);

	      // ----- レスポンス作成
	      CompanySettingsResponse res = new CompanySettingsResponse();
	      res.setResult(new RestResult(ResponseCode.OK));
	      res.setCompanyManagement(companyManagement);

	      messageUtility.fillMessage(res.getResultList());
	      restlog.end(log, mName, req, res, res.getResultList());

	      return res;
	    } catch (Exception ex) {
	      throw ExceptionUtil.handleException(log, ResponseCode.SYS_ERROR, null, null, null, ex);
	    }
	  }


}
