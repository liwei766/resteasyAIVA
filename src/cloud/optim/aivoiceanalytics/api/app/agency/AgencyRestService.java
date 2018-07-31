/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：AgencyRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.agency;

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

import cloud.optim.aivoiceanalytics.api.app.agency.AgencyResponse.BulkResult;
import cloud.optim.aivoiceanalytics.api.app.agency.AgencyResponse.EditResult;
import cloud.optim.aivoiceanalytics.api.entity.Agency;
import cloud.optim.aivoiceanalytics.core.modules.rest.ExceptionUtil;
import cloud.optim.aivoiceanalytics.core.modules.rest.MessageUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestLog;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;

/**
 * AgencyRestService 実装.<br/>
 */
@Path("/agency")
@Consumes({ "application/json", "application/xml" })
@Produces({ "application/json", "application/xml" })
@Component
public class AgencyRestService {
	/** Commons Logging instance. */
	private Log log = LogFactory.getLog(this.getClass());

	/** PKの項目名. */
	private static final String NAME_PK = "#agency.agencyId";

	// -------------------------------------------------------------------------

	/** バリデータ. */
	@Resource
	private AgencyRestValidator validator;

	/** AgencyService. */
	@Resource
	private AgencyService agencyService;

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
	 * @param req
	 *            検索条件
	 *
	 * @return 検索結果
	 */
	@POST
	@Path("/search")
	public AgencyResponse search(AgencyRequest req) {

		final String mName = "search";
		restlog.start(log, mName, req);

		try {
			// 検索条件の指定がない場合は全検索として扱う
			if (req == null) {
				req = new AgencyRequest();
			}

			// ----- 入力チェック
			validator.validateForSearch(req);

			// ----- 検索
			SearchForm form = req.getSearchForm();
			List<SearchResult> list = agencyService.search(form);

			// ----- レスポンス作成
			AgencyResponse res = new AgencyResponse();

			// 検索結果件数超過チェック
			Long limit = form.getSortForm().getMaxResult();
			if (list.size() > limit) {
				res.setResult(
						new RestResult(ResponseCode.TOO_MANY_SEARCH_RESULT, new Object[] { limit }, limit.toString()));
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
	 * @param req
	 *            取得条件（PK 項目のみ使用）
	 *
	 * @return 取得エンティティ
	 */
	@POST
	@Path("/get")
	public AgencyResponse get(AgencyRequest req) {
		final String mName = "get";
		restlog.start(log, mName, req);

		try {
			// ----- 入力チェック
			validator.validateForGet(req);

			// ----- 入力データ取得
			Agency agency = req.getEditForm().getAgency();

			// ----- 取得
			Agency entity = agencyService.getAgency(agency.getAgencyId());
			if (entity == null) {
				throw new RestException(new RestResult(ResponseCode.NOT_FOUND, null, NAME_PK, agency.getAgencyId()));
			}

			// ----- レスポンス作成
			AgencyResponse res = new AgencyResponse();
			res.setResult(new RestResult(ResponseCode.OK));
			res.setEditResult(new EditResult());
			res.getEditResult().setAgency(entity);

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
	 * @param req
	 *            登録内容
	 *
	 * @return 処理結果と登録内容
	 */
	@POST
	@Path("/put")
	public AgencyResponse put(AgencyRequest req) {
		final String mName = "put";
		restlog.start(log, mName, req);

		try {
			// ----- 入力チェック
			validator.validateForPut(req);

			// ----- 入力データ取得
			Agency agency = req.getEditForm().getAgency();

			// ----- 登録
			agency = agencyService.save(agency);

			// ----- レスポンス作成
			AgencyResponse res = new AgencyResponse();
			res.setResult(new RestResult(ResponseCode.OK));
			res.setEditResult(new EditResult());
			res.getEditResult().setAgency(agency);

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
	 * @param req
	 *            取得条件（PK 項目のみ使用）
	 *
	 * @return 処理結果ステータスのみ
	 */
	@POST
	@Path("/delete")
	public AgencyResponse delete(AgencyRequest req) {

		final String mName = "delete";
		restlog.start(log, mName, req);

		try {
			// ----- 入力チェック
			validator.validateForDelete(req);

			// ----- 1 件ずつ削除
			boolean error = false;
			AgencyResponse res = new AgencyResponse();
			res.setBulkResultList(new ArrayList<BulkResult>());
			for (SearchResult form : req.getBulkFormList()) {
				BulkResult result = new BulkResult();
				try {
					deleteOne(form);

					// ----- レスポンス作成
					result.setResult(new RestResult(ResponseCode.OK));
					result.setAgency(form.getAgency());

					messageUtility.fillMessage(result.getResultList());
					restlog.endOne(log, mName, result, result.getResultList());
				} catch (Exception ex) {
					error = true;

					// 応答結果を作成
					result.setResultList(ExceptionUtil
							.handleException(log, ResponseCode.SYS_ERROR, null, null, null, ex).getRestResultList());
					result.setAgency(form.getAgency());

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
	 * @param form
	 *            削除する 1 コンテンツの情報
	 *
	 * @throws Exception
	 *             エラー発生時
	 */
	private void deleteOne(SearchResult form) throws Exception {

		// ----- 入力チェック
		validator.validateForDeleteOne(form);

		// ----- 削除
		agencyService.delete(form.getAgency().getAgencyId());
	}

}
