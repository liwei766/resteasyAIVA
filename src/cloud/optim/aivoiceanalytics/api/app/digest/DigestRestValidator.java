/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：DigestRestValidator.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.digest;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestValidatorUtils;

/**
 * DigestRestValidator のバリデータクラス
 * （入力チェックと入力内容の補完を行う）
 */
@Component
class DigestRestValidator
{
	/** Commons Logging instance.  */
	@SuppressWarnings( "unused" )
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/**
	 * 通話開始の入力チェック.
	 *
	 * @param req 入力内容
	 */
	public void validateForDigest( DigestRequest req ) {

		String name = "";

		// ********** リクエスト

		name = "#request";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) ) ;
		}

		// ----- 通話ログ ID

		{
			name = "#text" ;
			String value = req.getText() ;

			RestValidatorUtils.fieldValidate( name, value, true, null, null );

			// 通話ログ区切り文字列を除去する
			req.setText(value.replaceAll("----- [0-9/: ].* .* : .* (start|end) -----", ""));
		}
	}
}
