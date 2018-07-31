/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：BinaryImageRestService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest.app.image;

import java.io.File ;
import java.io.FileInputStream ;
import java.io.IOException ;
import java.io.InputStream ;
import java.io.OutputStream ;
import java.net.URLEncoder ;
import java.util.ArrayList ;
import java.util.Calendar ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;
import java.util.regex.Matcher ;
import java.util.regex.Pattern ;

import javax.annotation.Resource ;
import javax.servlet.http.HttpServletRequest ;
import javax.ws.rs.Consumes ;
import javax.ws.rs.GET ;
import javax.ws.rs.POST ;
import javax.ws.rs.Path ;
import javax.ws.rs.PathParam ;
import javax.ws.rs.Produces ;
import javax.ws.rs.WebApplicationException ;
import javax.ws.rs.core.Context ;
import javax.ws.rs.core.Response ;
import javax.ws.rs.core.Response.ResponseBuilder ;
import javax.ws.rs.core.Response.Status ;
import javax.ws.rs.core.StreamingOutput ;

import org.apache.commons.io.IOUtils ;
import org.apache.commons.logging.Log ;
import org.apache.commons.logging.LogFactory ;
import org.springframework.stereotype.Component ;

import cloud.optim.aivoiceanalytics.core.common.utility.FileHelper;
import cloud.optim.aivoiceanalytics.core.modules.rest.ExceptionUtil;
import cloud.optim.aivoiceanalytics.core.modules.rest.MessageUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestLog;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;
import cloud.optim.aivoiceanalytics.core.modules.rest.app.fileupload.FileUploadUtility;


/**
 * BinaryImageRestService 実装.<br/>
 * 各種ファイル内容をバイナリで取得するための API クラス.
 */
@Path( "/image" )
@Consumes( { "application/json" } )
@Component
public class BinaryImageRestService
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/** REST ログ出力 */
	@Resource private RestLog restlog ;

	// -------------------------------------------------------------------------

	/** バリデータ */
	@Resource BinaryimageRestValidator validator ;

	/** FileUploadUtility */
	@Resource private FileUploadUtility fileUploadUtility ;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility ;

	/**
	 * HTTP リクエスト情報（UserAgent 情報取得用）
	 *
	 * ※@Context はスレッドごと（リクエストごと？）に
	 * 個別のインスタンスが取得できるのでインスタンス変数として保持しても安全
	 */
	@Context HttpServletRequest httpRequest ;

	// -------------------------------------------------------------------------

	/**
	 * アップロードファイルの最終格納先ディレクトリパスを算出.
	 *
	 * @param entityName エンティティ名
	 * @param id PK 値
	 * @param fieldName 項目名
	 *
	 * @return アップロードファイルの最終格納先ディレクトリパス
	 */
	public String getSavedDirPath( String entityName, String id, String fieldName )
	{
		if ( ( entityName == null ) || ( id == null ) || ( fieldName == null ) ) return null ;

		return fileUploadUtility.savedDirPath( entityName, id, fieldName ) ;
	}

	// -------------------------------------------------------------------------
	// ダウンロードのためのファイル取得 API
	// -------------------------------------------------------------------------

	/**
	 * アップロードファイル関連のファイル内容取得（ダウンロード）
	 *
	 * @param id 要求 ID または生成されたファイルの ID（例：サムネール ID）
	 *
	 * @return 指定されたファイルの内容
	 */
	@GET
	@POST
	@Path( "/download/{id}" )
	@Produces( { "application/octet-stream" } )
	public Response download( @PathParam( "id" ) String id )
	{
		String MNAME = "download" ;
		if ( log.isDebugEnabled() ) log.debug( "Enter : " + MNAME + " : " + id ) ;

		return binaryImage( id, true ) ;
	}

	/**
	 * 登録済みファイル内容取得（ダウンロード）
	 *
	 * @param entityName エンティティ名
	 * @param pk PK 値（要求 ID）
	 * @param fieldName 項目名
	 *
	 * @return ファイル内容
	 */
	@GET
	@POST
	@Path( "/download/{entityName}/{pk}/{fieldName}" )
	@Produces( { "application/octet-stream" } )
	public Response download(
		@PathParam( "entityName") String entityName,
		@PathParam( "pk" ) String pk,
		@PathParam( "fieldName" ) String fieldName )
	{
		String MNAME = "download" ;
		if ( log.isDebugEnabled() )
			log.debug( "Enter : " + MNAME + " : " + entityName + ", " + pk + ", " + fieldName ) ;

		return binaryImage( entityName, pk, fieldName, true, -1 ) ;
	}

	// -------------------------------------------------------------------------
	// インライン表示するためのファイル取得 API
	// -------------------------------------------------------------------------

	/**
	 * アップロードファイル関連のファイル内容取得（インライン表示用）
	 *
	 * @param id 要求 ID または生成されたファイルの ID（例：サムネール ID）
	 *
	 * @return 指定されたファイルの内容
	 */
	@GET
	@POST
	@Path( "/inline/{id}" )
	public Response image( @PathParam( "id" ) String id )
	{
		String MNAME = "image" ;
		if ( log.isDebugEnabled() ) log.debug( "Enter : " + MNAME + " : " + id ) ;

		return binaryImage( id, false ) ;
	}

	/**
	 * 登録済みファイル内容取得（インライン表示用）
	 *
	 * @param entityName エンティティ名
	 * @param pk PK 値（要求 ID）
	 * @param fieldName 項目名
	 *
	 * @return ファイル内容
	 */
	@GET
	@POST
	@Path( "/inline/{entityName}/{pk}/{fieldName}" )
	public Response image(
		@PathParam( "entityName") String entityName,
		@PathParam( "pk" ) String pk,
		@PathParam( "fieldName" ) String fieldName )
	{
		String MNAME = "image" ;
		if ( log.isDebugEnabled() )
			log.debug( "Enter : " + MNAME + " : " + entityName + ", " + pk + ", " + fieldName ) ;

		return binaryImage( entityName, pk, fieldName, false, httpRequest.getDateHeader( "If-Modified-Since" ) ) ;
	}

	// -------------------------------------------------------------------------
	// ファイル取得 API 共通処理
	// -------------------------------------------------------------------------

	/**
	 * アップロードファイル内容取得
	 *
	 * @param id 要求 ID または生成されたファイルの ID（例：サムネール ID）
	 * @param isDownload ダウンロード用として返却する場合に true
	 *
	 * @return ファイル内容
	 */
	protected Response binaryImage( String id, boolean isDownload )
	{
		BinaryImageRequest req = new BinaryImageRequest() ;

		req.setId( id ) ;

		return binaryImage( req, isDownload, -1 ) ;
	}

	/**
	 * 登録済みファイル内容取得
	 *
	 * @param entityName エンティティ名
	 * @param pk PK 値（要求 ID）
	 * @param fieldName 項目名
	 * @param isDownload ダウンロード用として返却する場合に true
	 * @param modifiedSince 更新日時
	 *
	 * @return ファイル内容
	 */
	protected Response binaryImage(
		String entityName, String pk, String fieldName, boolean isDownload, long modifiedSince )
	{
		BinaryImageRequest req = new BinaryImageRequest() ;

		req.setEntityName( entityName ) ;
		req.setPk( pk ) ;
		req.setFieldName( fieldName ) ;

		return binaryImage( req, isDownload, modifiedSince ) ;
	}

	/**
	 * ファイル内容取得
	 *
	 * @param req ファイル取得対象の指定
	 * @param isDownload ダウンロード用として返却する場合に true
	 * @param modifiedSince 更新日時
	 *
	 * @return サムネールファイルの内容
	 */
	protected Response binaryImage(
		BinaryImageRequest req, boolean isDownload, long modifiedSince )
	{
		String MNAME = "binaryImage" ;
		restlog.start( log, MNAME, req, isDownload ) ;

		List<RestResult> result = new ArrayList<RestResult>( 1 ) ;
		result.add( new RestResult( ResponseCode.OK ) ) ;

		Response response = null ;

		InputStream is = null ;

		try
		{
			File targetFile = null ;

			// ----- 入力チェック

			validator.validateForImage( req ) ;

			// ----- 入力データ取得

			String id = req.getId() ;

			String entityName = req.getEntityName() ;
			String pk = req.getPk() ;
			String fieldName = req.getFieldName() ;

			if ( id != null )
			{
				targetFile = fileUploadUtility.getUploadFile( id ) ;
			}
			else
			{
				targetFile = FileHelper.getChildFile(
					getSavedDirPath( entityName, pk, fieldName ) ) ;
			}

			// ----- ファイル内容取得

			// 取得対象ファイルのチェック

			if ( targetFile == null )
			{
				throw new RestException( new RestResult(
					ResponseCode.FILE_ERROR_NOT_FOUND, null, "",
					"File not found. null" ) ) ;
			}

			if ( ! targetFile.exists() )
			{
				throw new RestException( new RestResult(
					ResponseCode.FILE_ERROR_NOT_FOUND, null, "",
					"File not found. " + targetFile.getAbsolutePath() ) ) ;
			}

			if ( ! targetFile.isFile() )
			{
				throw new RestException( new RestResult(
					ResponseCode.FILE_ERROR_NOT_FILE, null, "",
					"Path is not file. " + targetFile.getAbsolutePath() ) ) ;
			}

			// 更新日時のチェック

			boolean modified = true ;

			if ( modifiedSince > 0 )
			{
				// ミリ秒は切り捨て
				modified = targetFile.lastModified() / 1000 * 1000 > modifiedSince ;
			}

			// ----- レスポンス作成

			if ( ! modified )
			{
				response = Response.notModified().build() ;
			}
			else
			{
				is = new FileInputStream( targetFile ) ;

				ResponseBuilder builder = Response.ok( ( new StreamingOutput() {

					private InputStream is ;
					public StreamingOutput setInputStream( InputStream is )
					{
						this.is = is ;
						return this ;
					}

					@Override
					public void write( OutputStream os ) throws IOException, WebApplicationException
					{
						try
						{
							int size ;
							byte[] buf = new byte[ 5120 ] ;

							while ( ( size = is.read( buf ) ) != -1 ) os.write( buf, 0, size );

							os.flush();
						}
						finally
						{
							IOUtils.closeQuietly( is ) ;
							IOUtils.closeQuietly( os ) ;
						}
					}
				} ).setInputStream( is ) ) ;

				if ( isDownload )
				{
					String fileName = targetFile.getName() ;

					fileName = URLEncoder.encode( fileName, "UTF-8" ).replace( "+", "%20" ) ;

					 builder.header( "Content-Disposition",
						"attachment; " + encodeFileName( httpRequest, fileName ) + "\n\n" ) ;
				}
				else
				{
					builder.type( mediaType( targetFile.getName() ) ) ;
				}

				Calendar cal = Calendar.getInstance() ;
				cal.setTimeInMillis( targetFile.lastModified() ) ;
				builder.lastModified( cal.getTime() ) ;
//				builder.type( mediaType( targetFile.getName() ) ) ;

				response = builder.build() ;
			}

			messageUtility.fillMessage( result ) ;
			restlog.end( log, MNAME, req, response, result ) ;

			return response ;
		}
		catch ( Exception ex )
		{
			response = Response.status( Status.NOT_FOUND ).build() ;

			result = ExceptionUtil.handleException( log,
				ResponseCode.SYS_ERROR, null, null, null, ex ).getRestResultList() ;

			messageUtility.fillMessage( result ) ;
			restlog.abort( log, MNAME, req, response, result, ex ) ;

			return response ;
		}
	}

	/**
	 * @param req HTTP リクエスト
	 * @param fileName ファイル名
	 * @return ファイル名
	 */
	private String encodeFileName( HttpServletRequest req, String fileName )
	{
		if( underIE8( req ) )
		{
			return "filename=" + fileName ;
		}
		return "filename*=" + "utf-8'ja'" + fileName ;
	}

	/**
	 * IE8 以前のブラウザからのリクエストか調べる
	 *
	 * @param req HTTP リクエスト
	 * @return true：IE8 以前のブラウザからのリクエストである　false：それ以外
	 */
	private boolean underIE8( HttpServletRequest req )
	{
		boolean ret = false ;

		String ua = req.getHeader( "user-agent" );

		String regex = "(MSIE)\\s[0-9]{1,}\\.{0,}[0-9]{0,}" ;
		Pattern p = Pattern.compile( regex ) ;
		Matcher m = p.matcher( ua ) ;

		if( m.find() )
		{
			String ieVer = m.group().substring(5) ;
			Double ver = Double.parseDouble( ieVer ) ;
			if( ver.compareTo( 9.0 ) < 0 )
			{
				ret = true ;
			}
		}

		return ret;
	}

	/**
	 * メディアタイプMAP
	 */
	private static Map<String, String> MEDIA_TYPE = new HashMap<String, String>() ;
	static
	{
		MEDIA_TYPE.put( "gif", "image/gif" ) ;
		MEDIA_TYPE.put( "png", "image/png" ) ;
		MEDIA_TYPE.put( "jpg", "image/jpeg" ) ;
		MEDIA_TYPE.put( "jpeg", "image/jpeg" ) ;
		MEDIA_TYPE.put( "jpe", "image/jpeg" ) ;
	}

	/**
	 * メディアタイプ取得
	 * @param fileName ファイル名
	 * @return メディアタイプ
	 */
	private String mediaType( String fileName )
	{
		String ret = MEDIA_TYPE.get(
			FileHelper.getExtension( fileName ) ) ;

		return ret != null ? ret : "image/jpeg" ;
	}
}