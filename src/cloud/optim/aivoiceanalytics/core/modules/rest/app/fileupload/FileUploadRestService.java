/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：FileUploadRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest.app.fileupload;

import java.io.ByteArrayOutputStream ;
import java.io.File ;

import javax.annotation.Resource ;
import javax.servlet.http.HttpServletRequest ;
import javax.servlet.http.HttpSession ;
import javax.ws.rs.Consumes ;
import javax.ws.rs.HeaderParam ;
import javax.ws.rs.MatrixParam ;
import javax.ws.rs.POST ;
import javax.ws.rs.Path ;
import javax.ws.rs.PathParam ;
import javax.ws.rs.Produces ;
import javax.ws.rs.core.Context ;

import org.apache.commons.io.FileUtils ;
import org.apache.commons.logging.Log ;
import org.apache.commons.logging.LogFactory ;
import org.springframework.stereotype.Component ;

import cloud.optim.aivoiceanalytics.core.common.utility.FileHelper;
import cloud.optim.aivoiceanalytics.core.modules.image.ImageResizeUtility;
import cloud.optim.aivoiceanalytics.core.modules.image.ResizeOption;
import cloud.optim.aivoiceanalytics.core.modules.image.ResizeOption.ResizeType;
import cloud.optim.aivoiceanalytics.core.modules.rest.CustomJsonProvider;
import cloud.optim.aivoiceanalytics.core.modules.rest.ExceptionUtil;
import cloud.optim.aivoiceanalytics.core.modules.rest.MessageUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestLog;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;

/**
 * FileUploadRestService 実装.<br/>
 */
@Path( "/upload" )
@Consumes( { "application/json" } )
@Produces( { "application/json" } )
@Component
public class FileUploadRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() ) ;

	// -------------------------------------------------------------------------

	/** バリデータ */
	@Resource private FileUploadRestValidator validator ;

	/** ファイルアップロードユーティリティ */
	@Resource private FileUploadUtility fileUploadUtility ;

	/** サムネール作成ユーティリティ */
	@Resource private ThumbnailUtility thumbnailUtility ;

	/** RestLog */
	@Resource private RestLog restlog ;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility ;

	/** JsonProvider */
	@Resource private CustomJsonProvider provider;

	// -------------------------------------------------------------------------

	/**
	 * 要求 ID 取得
	 *
	 * @param req リクエスト
	 *
	 * @return 要求 ID
	 */
	@POST
	@Path( "/nextUploadId" )
	public FileUploadResponse nextUploadId( @Context HttpServletRequest req )
	{
		String MNAME = "nextUploadId" ;
		restlog.start( log, MNAME, req ) ;

		try
		{
			FileUploadResponse res = new FileUploadResponse() ;

			// 要求 ID 発行

			HttpSession sess = req.getSession() ;

			String uploadId = fileUploadUtility.getNextUploadId( sess.getId() ) ;

			// ディレクトリ作成

			String dirPath = FileHelper.pathConcat(
				fileUploadUtility.getUploadRootDirPath(), uploadId ) ;

			File dir = new File( dirPath ) ;

			if ( ! dir.mkdirs() )
			{
				// 要求 ID ディレクトリ作成失敗

				throw new RestException( new RestResult(
					ResponseCode.FILE_ERROR_CREATE, null, uploadId,
					messageUtility.getMessage( "msg.common.dir.create.error", dir.getAbsolutePath() ) ) ) ;
			}

			// ----- レスポンス作成

			res.addResult( new RestResult( ResponseCode.OK ) ) ;
			res.setUploadId( uploadId ) ;

			messageUtility.fillMessage( res.getResultList() ) ;
			restlog.end( log, MNAME, req, res, res.getResultList() ) ;

			return res ;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );

		}
	}

	/**
	 * 要求 ID 破棄
	 *
	 * @param uploadId 要求 ID
	 *
	 * @return 要求 ID
	 */
	@POST
	@Path( "/discardUploadId/{uploadId}" )
	public FileUploadResponse discardUploadId( @PathParam( "uploadId" ) String uploadId )
	{
		String MNAME = "discardUploadId" ;
		restlog.start( log, MNAME, uploadId ) ;

		try
		{
			FileUploadResponse res = new FileUploadResponse() ;

			// ディレクトリ削除

			String dirPath = FileHelper.pathConcat(
				fileUploadUtility.getUploadRootDirPath(), uploadId ) ;

			FileUtils.deleteQuietly( new File( dirPath ) ) ;

			// ----- レスポンス作成

			res.addResult( new RestResult( ResponseCode.OK ) ) ;
			res.setUploadId( uploadId ) ;

			messageUtility.fillMessage( res.getResultList() ) ;
			restlog.end( log, MNAME, uploadId, res, res.getResultList() ) ;

			return res ;
		}
		catch ( Exception ex ) {

			throw ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex );
		}
	}

	// -------------------------------------------------------------------------
	// ファイルアップロード関連処理
	// -------------------------------------------------------------------------

	/**
	 * ファイルアップロード（テキスト形式で返却）.<br/>
	 * ※本メソッドは、IE8 などの File API を実装していないブラウザのために必要
	 * （form 要素からの submit 結果を ifreme に設定する際に、
	 * Content-Type が text/plain|html でないとダウンロード扱いされてしまうため）
	 *
	 * @param req HTTP リクエスト
	 * @param uploadId 要求 ID
	 * @param token 認証トークン（CSRF 対策）
	 *
	 * @return 処理結果
	 */
	@POST
	@Path( "/up/{uploadId}/{token}" )
	@Consumes( "multipart/form-data" )
	@Produces( { "text/plain", "text/html" } )
	public byte[] uploadSubmit( @Context HttpServletRequest req,
		@PathParam( "uploadId" ) String uploadId,
		@PathParam( "token" ) String token )
	{
		return uploadSubmit( req, uploadId, token,
			null, null, null, null, null, null, null, null, null ) ;
	}

	/**
	 * ファイルアップロード（サムネール作成／テキスト形式で返却）.<br/>
	 * ※本メソッドは、IE8 などの File API を実装していないブラウザのために必要
	 * （form 要素からの submit 結果を ifreme に設定する際に、
	 * Content-Type が text/plain|html でないとダウンロード扱いされてしまうため）
	 *
	 * @param req  HTTP リクエスト
	 * @param uploadId 要求 ID
	 * @param token 認証トークン（CSRF 対策）
	 *
	 * @param width サムネールの幅（ピクセル）
	 * @param height サムネールの高さ（ピクセル）
	 * @param color 余白の色（0xRRGGBB）
	 * @param format サムネールのフォーマット指定
	 * @param type 縮小方式（INSET／TRIMMING）
	 * @param sharpness シャープネス指定
	 * @param quality 画質指定
	 * @param keepSmall 小さい画像の処理方法（拡大する／しない）
	 * @param keepJustSize 同一サイズ画像の処理方法（コピーする／変換する）
	 *
	 * @return 処理結果
	 *
	 * @see ResizeOption
	 */
	@POST
	@Path( "/up/{uploadId}/{token}/opt/{option}" )
	@Consumes( "multipart/form-data" )
	@Produces( { "text/plain", "text/html" } )
	public byte[] uploadSubmit( @Context HttpServletRequest req,
		@PathParam( "uploadId" ) String uploadId,
		@PathParam( "token" ) String token,

		@MatrixParam( "width" ) Integer width,
		@MatrixParam( "height" ) Integer height,
		@MatrixParam( "color" ) String color,
		@MatrixParam( "format" ) String format,
		@MatrixParam( "type" ) ResizeType type,
		@MatrixParam( "sharpness" ) Float sharpness,
		@MatrixParam( "quality" ) Float quality,
		@MatrixParam( "keepSmall" ) Boolean keepSmall,
		@MatrixParam( "keepJustSize" ) Boolean keepJustSize
	)
	{
		final String MNAME = "uploadSubmit" ;
		restlog.start( log, MNAME, uploadId ) ;

		try
		{
			try ( ByteArrayOutputStream baos = new ByteArrayOutputStream() )
			{
				FileUploadResponse res = upload( req, uploadId, token,
					width, height, color, format, type, sharpness, quality, keepSmall, keepJustSize ) ;

				provider.writeTo( res, res.getClass(), null, null, null, null, baos ) ;

				return baos.toByteArray() ;
			}
			catch ( Exception ex )
			{
				throw ExceptionUtil.handleException( log,
					ResponseCode.SYS_ERROR, null, null, null, ex );
			}
		}
		catch ( RestException ex )
		{
			// 本節は content-type が JSON 系の場合の CustomExceptionMapper に相当する.
			// そのため、本節で扱えない例外はそのままスローする（＝InternalServerError）

			FileUploadResponse res = new FileUploadResponse() ;

			res.setResultList( ex.getRestResultList() ) ;
			messageUtility.fillMessage( res.getResultList() ) ;

			try ( ByteArrayOutputStream baos = new ByteArrayOutputStream() )
			{
				provider.writeTo( res, res.getClass(), null, null, null, null, baos ) ;

				return baos.toByteArray() ;
			}
			catch ( Exception ex2 )
			{
				ex.addSuppressed( ex2 ) ;
				throw ex;
			}
		}
	}

	/**
	 * ファイルアップロード
	 *
	 * @param req HTTP リクエスト
	 * @param uploadId 要求 ID
	 * @param token 認証トークン（CSRF 対策）
	 *
	 * @return 処理結果
	 */
	@POST
	@Path( "/up/{uploadId}" )
	@Consumes( "multipart/form-data" )
	@Produces( { "application/json", "text/json" } )
	public FileUploadResponse upload( @Context HttpServletRequest req,
		@PathParam( "uploadId" ) String uploadId,
		@HeaderParam( "X-SESSIONID" ) String token )
	{
		return upload( req, uploadId, token,
			null, null, null, null, null, null, null, null, null ) ;
	}

	/**
	 * ファイルアップロード（サムネール作成）
	 *
	 * @param req HTTP リクエスト
	 * @param uploadId 要求 ID
	 * @param token 認証トークン（CSRF 対策）
	 *
	 * @param width サムネールの幅（ピクセル）
	 * @param height サムネールの高さ（ピクセル）
	 * @param color 余白の色（0xRRGGBB）
	 * @param format サムネールのフォーマット指定
	 * @param type 縮小方式（INSET／TRIMMING）
	 * @param sharpness シャープネス指定
	 * @param quality 画質指定
	 * @param keepSmall 小さい画像の処理方法（拡大する／しない）
	 * @param keepJustSize 同一サイズ画像の処理方法（コピーする／変換する）
	 *
	 * @return 処理結果
	 *
	 * @see ResizeOption
	 */
	@POST
	@Path( "/up/{uploadId}/opt/{option}" )
	@Consumes( "multipart/form-data" )
	@Produces( { "application/json", "text/json" } )
	public FileUploadResponse upload( @Context HttpServletRequest req,
		@PathParam( "uploadId" ) String uploadId,
		@HeaderParam( "X-SESSIONID" ) String token,

		@MatrixParam( "width" ) Integer width,
		@MatrixParam( "height" ) Integer height,
		@MatrixParam( "color" ) String color,
		@MatrixParam( "format" ) String format,
		@MatrixParam( "type" ) ResizeType type,
		@MatrixParam( "sharpness" ) Float sharpness,
		@MatrixParam( "quality" ) Float quality,
		@MatrixParam( "keepSmall" ) Boolean keepSmall,
		@MatrixParam( "keepJustSize" ) Boolean keepJustSize
	)
	{
		final String MNAME = "upload" ;
		restlog.start( log, MNAME, uploadId ) ;

		try
		{
			FileUploadResponse res = new FileUploadResponse() ;

			// ----- 入力チェック

			validator.validateForUpload( req, uploadId, token ) ;

			// ----- アップロード

			File uploadFile = fileUploadUtility.storeTemporary( req, uploadId ) ;

			// ----- サムネール作成

			// サムネール不要の場合はここをコメントアウトしてください

			if ( thumbnailUtility.thumbnailAvailable( uploadFile.getName() ) )
			{
				ResizeOption option = createResizeOption(
					width, height, color, format, type,
					sharpness, quality, keepSmall, keepJustSize ) ;

				if ( option != null )
				{
					if ( ImageResizeUtility.isCmykMode( uploadFile ) )
					{
						throw new RestException( new RestResult(
							ResponseCode.UPLOAD_ERROR_RESIZE_CMYK, null, uploadId ) ) ;
					}

					if ( ImageResizeUtility.isIncludeIccProfile( uploadFile ) )
					{
						throw new RestException( new RestResult(
							ResponseCode.UPLOAD_ERROR_RESIZE_ICC, null, uploadId ) ) ;
					}

					String thumbId = fileUploadUtility.getNextUploadId( "thumb" ) ;
					String thumbDirPath = fileUploadUtility.temporaryPath( thumbId, "" ) ;

					File thumbFile = thumbnailUtility.createThumbnail( uploadFile, thumbDirPath, option ) ;

					res.setThumbId( thumbId ) ;
					res.setThumbFileName( thumbFile.getName() ) ;
				}
			}

			// ----- レスポンス作成

			res.addResult( new RestResult( ResponseCode.OK ) ) ;
			res.setUploadId( uploadId ) ;
			res.setUploadFileName( uploadFile.getName() ) ;

			messageUtility.fillMessage( res.getResultList() ) ;
			restlog.end( log, MNAME, uploadId, res, res.getResultList() ) ;

			return res ;
		}
		catch ( Exception ex )
		{
			throw ExceptionUtil.handleException( log,
				ResponseCode.UPLOAD_ERROR, null, null, null, ex );
		}
	}

	/**
	 * リクエスト内容から ResizeOption インスタンスを作成する.
	 *
	 * @param width サムネールの幅（ピクセル）
	 * @param height サムネールの高さ（ピクセル）
	 * @param color 余白の色（0xRRGGBB）
	 * @param format サムネールのフォーマット指定
	 * @param type 縮小方式（INSET／TRIMMING）
	 * @param sharpness シャープネス指定
	 * @param quality 画質指定
	 * @param keepSmall 小さい画像の処理方法（拡大する／しない）
	 * @param keepJustSize 同一サイズ画像の処理方法（コピーする／変換する）
	 *
	 * @return ResizeOption インスタンス
	 */
	private ResizeOption createResizeOption(
		Integer width,
		Integer height,
		String color,
		String format,
		ResizeType type,
		Float sharpness,
		Float quality,
		Boolean keepSmall,
		Boolean keepJustSize
	)
	{
		boolean hasOption = false ;
		ResizeOption option = ResizeOption.getStandardOption() ;

		if ( width != null )
		{
			hasOption = true ;
			option.setWidth( width ).setHeight( height ) ;
		}

		if ( height != null )
		{
			hasOption = true ;
			option.setWidth( width ).setHeight( height ) ;
		}

		if ( color != null )
		{
			hasOption = true ;
			option.setBgColor( color ) ;
		}

		if ( format != null )
		{
			hasOption = true ;
			option.setFormat( format ) ;
		}

		if ( type != null )
		{
			hasOption = true ;
			option.setResizeType( type ) ;
		}

		if ( sharpness != null )
		{
			hasOption = true ;
			option.setSharpness( sharpness ) ;
		}

		if ( quality != null )
		{
			hasOption = true ;
			option.setQuality( quality ) ;
		}

		if ( keepSmall != null )
		{
			hasOption = true ;
			option.setKeepSmallImage( keepSmall ) ;
		}

		if ( keepJustSize != null )
		{
			hasOption = true ;
			option.setKeepJustImage( keepJustSize ) ;
		}

		return hasOption ? option : null ;
	}
}
