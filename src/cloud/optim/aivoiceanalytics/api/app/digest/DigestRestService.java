/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：DigestRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.digest;


import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.api.recaius.result.DigestResult;
import cloud.optim.aivoiceanalytics.api.recaius.result.RecaiusResult;
import cloud.optim.aivoiceanalytics.api.recaius.service.RecaiusAuthService;
import cloud.optim.aivoiceanalytics.api.recaius.service.RecaiusDigestService;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.LoginUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ExceptionUtil;
import cloud.optim.aivoiceanalytics.core.modules.rest.MessageUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestLog;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;

/**
 * DigestRestService 実装.<br/>
 */
@Path( "/digest" )
@Consumes( { "application/json", "application/xml" } )
@Produces( { "application/json", "application/xml" } )
@Component
public class DigestRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() );

	// -------------------------------------------------------------------------

	/** バリデータ */
	@Resource private DigestRestValidator validator;

	/** RecaiusAuthService */
	@Resource private RecaiusAuthService authService;

	/** RecaiusDigestService */
	@Resource private RecaiusDigestService recaiusDigestService;

	/** DigestService */
	@Resource private DigestService digestService;

	/** RestLog */
	@Resource private RestLog restlog;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility;

	// -------------------------------------------------------------------------

	/** リカイアス音声解析サービスID */
	@Value( "${recaius.service.digest.type}" )
	private String serviceType;

	/** リカイアス音声解析サービスID */
	@Value( "${recaius.service.digest.service.id}" )
	private String serviceId;

	/** リカイアス音声解析サービスID */
	@Value( "${recaius.service.digest.password}" )
	private String password;

	// -------------------------------------------------------------------------


	/**
	 * リカイアス音声認識認証処理
	 *
	 * @param req
	 *
	 * @return 認証結果
	 */
	@POST
	@Path( "/digest" )
	public DigestResponse digest(DigestRequest req) {

		String MNAME = "digest";
		restlog.start( log, MNAME, req );

		String token = null;

		try {

			// -----  バリデート
			validator.validateForDigest(req);

			// ----- 認証処理でトークンを取得する
			token = authService.auth(serviceType, serviceId, password);

			// ----- 要約を行う
			RecaiusResult result = recaiusDigestService.digest(token, req.getText());
			String digest = getDigest(result);

			// ----- レスポンス作成
			DigestResponse res = new DigestResponse();
			res.setResult( new RestResult( ResponseCode.OK ) );
			res.setDigest(digest);

			messageUtility.fillMessage( res.getResultList() );
			restlog.end( log, MNAME, req, res, res.getResultList() );

			return res;
		}
		catch ( Exception ex ) {
			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		} finally {
			if (token != null) {
				try {
					authService.disconnect(token);
				} catch(Exception e) {
					log.error(e);
				}
			}
		}
	}

	/**
	 * リカイアスの結果から要約されたテキストを取得する
	 * @param recaiusResult リカイアスの結果
	 * @return 要約テキスト
	 * @throws Exception
	 */
	private String getDigest(RecaiusResult recaiusResult) throws Exception {
		// リカイアスの結果から要約結果を取得する
		DigestResult digestRsult = recaiusResult.getResponse(DigestResult.class);

		// 要約テキストを改行で連結して返す
		return digestRsult.getSentences().stream()
				.map(each -> each.getText())
				.collect(Collectors.joining("\r\n"));
	}
}