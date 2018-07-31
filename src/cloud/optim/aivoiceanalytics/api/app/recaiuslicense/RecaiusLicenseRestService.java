/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：RecaiusLicenseRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.recaiuslicense;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.api.app.recaiuslicense.RecaiusLicenseResponse.BulkResult;
import cloud.optim.aivoiceanalytics.api.app.recaiuslicense.RecaiusLicenseResponse.EditResult;
import cloud.optim.aivoiceanalytics.api.entity.RecaiusLicense;
import cloud.optim.aivoiceanalytics.core.modules.rest.ExceptionUtil;
import cloud.optim.aivoiceanalytics.core.modules.rest.MessageUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestLog;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;

/**
 * RecaiusLicenseRestService 実装.<br/>
 */
@Path("/recaiuslicense")
@Consumes({"application/json", "application/xml"})
@Produces({"application/json", "application/xml"})
@Component
public class RecaiusLicenseRestService {

  /** Commons Logging instance. */
  private Log log = LogFactory.getLog(this.getClass());

  /** PKの項目名. */
  private static final String NAME_PK = "#recaiusLicense.recaiusLicenseId";

  // -------------------------------------------------------------------------

  /** バリデータ. */
  @Resource
  private RecaiusLicenseRestValidator validator;

  /** RecaiusLicenseService. */
  @Resource
  private RecaiusLicenseService recaiusLicenseService;

  /** RestLog. */
  @Resource
  private RestLog restlog;

  /** MessageUtility. */
  @Resource
  private MessageUtility messageUtility;

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
  public RecaiusLicenseResponse search(RecaiusLicenseRequest req) {
    final String mName = "search";
    restlog.start(log, mName, req);

    try {
      // 検索条件の指定がない場合は全検索として扱う
      if (req == null) {
        req = new RecaiusLicenseRequest();
      }

      // ----- 入力チェック
      validator.validateForSearch(req);

      // ----- 検索
      SearchForm form = req.getSearchForm();
      List<SearchResult> list = recaiusLicenseService.search(form);


      // ----- レスポンス作成

      // パスワードにnullを設定する
      for(SearchResult each : list) {
        each.getRecaiusLicense().setPassword(null);
      }

      RecaiusLicenseResponse res = new RecaiusLicenseResponse();

      // 検索結果件数超過チェック
      Long limit = form.getSortForm().getMaxResult();
      if (list.size() > limit) {
        res.setResult( new RestResult(ResponseCode.TOO_MANY_SEARCH_RESULT, new Object[]{limit}, limit.toString()));
        list = list.subList(0, list.size() - 1);
      } else {
        res.setResult( new RestResult( ResponseCode.OK ) );
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
  public RecaiusLicenseResponse get(RecaiusLicenseRequest req) {
    final String mName = "get";
    restlog.start(log, mName, req);

    try {
      // ----- 入力チェック
      validator.validateForGet(req);

      // ----- 入力データ取得
      RecaiusLicense recaiusLicense = req.getEditForm().getRecaiusLicense();

      // ----- 取得
      RecaiusLicense entity =
          recaiusLicenseService.getRecaiusLicense(recaiusLicense.getRecaiusLicenseId());
      if (entity == null) {
        throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, NAME_PK,
            recaiusLicense.getRecaiusLicenseId()));
      }

      // ----- レスポンス作成

      // パスワードにnullを設定する
      entity.setPassword(null);

      RecaiusLicenseResponse res = new RecaiusLicenseResponse();
      res.setResult(new RestResult(ResponseCode.OK));
      res.setEditResult(new EditResult());
      res.getEditResult().setRecaiusLicense(entity);

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
  public RecaiusLicenseResponse put(RecaiusLicenseRequest req) {
    final String mName = "put";
    restlog.start(log, mName, req);

    try {
      // ----- 入力チェック
      validator.validateForPut(req);

      // ----- 入力データ取得
      RecaiusLicense recaiusLicense = req.getEditForm().getRecaiusLicense();

      // ----- 登録
      recaiusLicense = recaiusLicenseService.save(recaiusLicense);

      // ----- レスポンス作成
      RecaiusLicenseResponse res = new RecaiusLicenseResponse();
      res.setResult(new RestResult(ResponseCode.OK));
      res.setEditResult(new EditResult());

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
  public RecaiusLicenseResponse update(RecaiusLicenseRequest req) {
    final String mName = "update";
    restlog.start(log, mName, req);

    try {
      // ----- 入力チェック
      validator.validateForUpdate(req);

      // ----- 入力データ取得
      RecaiusLicense recaiusLicense = req.getEditForm().getRecaiusLicense();

      // ----- 更新
      recaiusLicense = recaiusLicenseService.update(recaiusLicense);

      // ----- レスポンス作成
      RecaiusLicenseResponse res = new RecaiusLicenseResponse();
      res.setResult(new RestResult(ResponseCode.OK));
      res.setEditResult(new EditResult());

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
  public RecaiusLicenseResponse delete(RecaiusLicenseRequest req) {
    final String mName = "delete";
    restlog.start(log, mName, req);

    try {
      // ----- 入力チェック
      validator.validateForDelete(req);

      // ----- 1 件ずつ削除
      boolean error = false;
      RecaiusLicenseResponse res = new RecaiusLicenseResponse();
      res.setBulkResultList(new ArrayList<BulkResult>());
      for (SearchResult form : req.getBulkFormList()) {
        BulkResult result = new BulkResult();
        try {
          deleteOne(form);

          // ----- レスポンス作成
          result.setResult(new RestResult(ResponseCode.OK));
          result.setRecaiusLicense(form.getRecaiusLicense());

          messageUtility.fillMessage(result.getResultList());
          restlog.endOne(log, mName, result, result.getResultList());
        } catch (Exception ex) {
          error = true;

          // 応答結果を作成
          result.setResultList(
              ExceptionUtil.handleException(log, ResponseCode.SYS_ERROR, null, null, null, ex)
                  .getRestResultList());
          result.setRecaiusLicense(form.getRecaiusLicense());

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

    // ----- 削除
    recaiusLicenseService.delete(form.getRecaiusLicense().getRecaiusLicenseId());
  }

}
