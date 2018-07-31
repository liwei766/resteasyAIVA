/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：KnowledgeRestValidator.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.lexicon;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestValidatorUtils;

/**
 * KnowledgeRestService のバリデータクラス
 * （入力チェックと入力内容の補完を行う）
 */
@Component
class LexiconRestValidator
{
	/** Commons Logging instance.  */
	@SuppressWarnings( "unused" )
	private Log log = LogFactory.getLog( this.getClass() ) ;

	// -------------------------------------------------------------------------

	/**
	 * Knowledge 取得の入力チェックと入力内容の補完.
	 *
	 * @param req 入力内容
	 */
	public void validateForGet( LexiconRequest req ) {

		String name = "";

		// ----- 取得条件

		name = "#request";

		if ( req == null ) {

			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) );
		}


		// ----- PK

		name = "#modelId";
		Integer pkvalue = req.getModelId();

		RestValidatorUtils.fieldValidate( name, pkvalue, true, null, null );
	}
}
