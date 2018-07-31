/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：FileUploadRestValidator.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest.app.fileupload;

import javax.annotation.Resource ;
import javax.servlet.http.HttpServletRequest ;

import org.springframework.stereotype.Component ;

import cloud.optim.aivoiceanalytics.core.modules.loginutil.LoginUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestValidatorUtils;

/**
 * FileUploadRestService のバリデータクラス
 * （入力チェックと入力内容の補完を行う）
 *
 * @author itsukaha
 */
@Component
class FileUploadRestValidator
{
	/** ファイルアップロードユーティリティ */
	@Resource private FileUploadUtility fileUploadUtility ;

	/** LoginUtility */
	@Resource private LoginUtility loginUtility ;

	// -------------------------------------------------------------------------

	/**
	 * ファイルアップロードの入力チェックと入力内容の補完.
	 *
	 * @param request HTTP リクエスト情報
	 * @param uploadId 要求 ID
	 * @param token 認証トークン
	 */
	public void validateForUpload( HttpServletRequest request, String uploadId, String token )
	{
		// ----- 要求 ID

		{
			String name = "#uploadId" ;
			String svalue = uploadId ;

			RestValidatorUtils.fieldValidate( name, svalue, true, null, null ) ;

			// 書式チェック

			if ( ! fileUploadUtility.isValidUploadId( svalue ) )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_FORMAT, null, name ) ) ;
			}
		}
	}
}
