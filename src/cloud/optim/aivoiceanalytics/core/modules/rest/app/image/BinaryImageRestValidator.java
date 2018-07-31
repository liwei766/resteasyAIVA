/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：BinaryimageRestValidator.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest.app.image;

import javax.annotation.Resource ;

import org.springframework.stereotype.Component ;

import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestValidatorUtils;
import cloud.optim.aivoiceanalytics.core.modules.rest.app.fileupload.FileUploadUtility;
import cloud.optim.aivoiceanalytics.core.modules.validator.ValidatorUtils;

/**
 * ContentsRestService のバリデータクラス
 *
 * @author itsukaha
 */
@Component
class BinaryimageRestValidator
{
	/** ファイルアップロードユーティリティ */
	@Resource private FileUploadUtility fileUploadUtility ;

	// -------------------------------------------------------------------------

	/**
	 * 入力チェック.
	 *
	 * @param req 入力内容
	 */
	public void validateForImage( BinaryImageRequest req )
	{
		String name = "" ;

		String svalue ;

		name = "#request" ;
		if ( req == null )
		{
			throw new RestException( new RestResult(
				ResponseCode.INPUT_ERROR_REQUIRED, null, name ) ) ;
		}

		if ( ValidatorUtils.required( req.getId() ) )
		{
			// ----- ID

			name = "#id" ;
			svalue = req.getId() ;

			RestValidatorUtils.fieldValidate( name, svalue, true, null, null ) ;

			if ( ! fileUploadUtility.isValidUploadId( svalue ) )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_FORMAT, null, name ) ) ;
			}
		}
		else
		{
			// ----- エンティティ名

			name = "#entityName" ;
			svalue = req.getEntityName() ;

			RestValidatorUtils.fieldValidate( name, svalue, true, null, null ) ;

			if ( ! ValidatorUtils.alphaNumeric( svalue ) )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_LETTER_TYPE, null, name ) ) ;
			}

			// ----- PK

			name = "#pk" ;
			svalue = req.getPk() ;

			RestValidatorUtils.fieldValidate( name, svalue, true, null, null ) ;

			if ( ! ValidatorUtils.alphaNumeric( svalue ) )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_LETTER_TYPE, null, name ) ) ;
			}

			// ----- 項目名

			name = "#fieldName" ;
			svalue = req.getFieldName() ;

			RestValidatorUtils.fieldValidate( name, svalue, true, null, null ) ;

			if ( ! ValidatorUtils.alphaNumeric( svalue ) )
			{
				throw new RestException( new RestResult(
					ResponseCode.INPUT_ERROR_LETTER_TYPE, null, name ) ) ;
			}
		}
	}
}
