/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：FileUploadUtility.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest.app.fileupload;

import java.io.File ;
import java.io.FileOutputStream ;
import java.io.IOException ;
import java.io.InputStream ;
import java.io.Serializable ;
import java.text.Normalizer ;
import java.text.Normalizer.Form ;
import java.util.HashMap ;
import java.util.Map ;
import java.util.regex.Pattern ;

import javax.annotation.Resource ;
import javax.servlet.http.HttpServletRequest ;

import org.apache.commons.fileupload.FileItemIterator ;
import org.apache.commons.fileupload.FileItemStream ;
import org.apache.commons.fileupload.FileUploadBase ;
import org.apache.commons.fileupload.FileUploadException ;
import org.apache.commons.fileupload.servlet.ServletFileUpload ;
import org.apache.commons.io.IOUtils ;
import org.apache.commons.lang3.StringUtils ;
import org.apache.commons.logging.Log ;
import org.apache.commons.logging.LogFactory ;
import org.springframework.beans.factory.annotation.Value ;
import org.springframework.stereotype.Component ;

import cloud.optim.aivoiceanalytics.core.common.fileupload.FileRegistHandler;
import cloud.optim.aivoiceanalytics.core.common.utility.FileHelper;
import cloud.optim.aivoiceanalytics.core.common.utility.SystemUniqNo;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.PasswordUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.MessageUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestLog;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;

/**
 * ファイルアップロード機能を実現するためのユーティリティクラス.
 * 想定するリクエストの例は以下の通り.
 *
 *<code>
 * Content-Type: multipart/form-data; boundary=-----------------------------7d0332938c0382
 *
 * -------------------------------7d0332938c0382
 * Content-Disposition: form-data; name="uploadId"
 *
 * AE0045338_20120309_123455_3_00001
 * -------------------------------7d0332938c0382
 * Content-Disposition: form-data; name="file"; filename="（ファイル名）"
 * Content-Type: text/plain
 *
 * This is a sample file....
 * （ファイルの中身）
 * -------------------------------7d0332938c0382--
 *</code>
 *
 * @author itsukaha
 */
@Component
public class FileUploadUtility
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() ) ;

	// -------------------------------------------------------------------------

	/**
	 * アップロードされたファイルの、一時格納先ルートディレクトリ.
	 * この下に要求 ID のディレクトリを作成してファイルを格納する.
	 */
	@Value( "${path.dir.upload}" )
	private String uploadRootDirPath ;

	/**
	 * アップロードされたファイルの最終格納先ルートディレクトリ
	 * この下に「エンティティ名/ PK / フィールド名」のディレクトリを作成してファイルを格納する
	 */
	@Value( "${path.dir.data.root}" )
	private String dataRootDirPath ;

	/**
	 * アップロードファイルの上限サイズ（単位：バイト）
	 * 負数を設定した場合は制限しない
	 * 2147483647 = 2 * 1024 ^ 3 - 1 = 2G 未満
	 */
	@Value( "${fileupload.max.size}" )
	private long maxFileSize ;

	/**
	 * メモリ上で操作するファイルの最大サイズ（単位：バイト）
	 * このサイズを超える場合はディスク上に一時ファイルが作成される
	 * 1048576 = 1024 ^ 2 = 1M
	 */
	@Value( "${fileupload.mem.size}" )
	private int memCacheSize ;

	/** マルチパートのヘッダ部分の文字エンコード */
	@Value( "${fileupload.header.encoding}" )
	private String headerEncoding ;

	// -------------------------------------------------------------------------

	/** RestLog */
	@Resource private RestLog restlog ;

	/** MessageUtility */
	@Resource private MessageUtility messageUtility ;

	/** PassowrdUtility */
	@Resource private PasswordUtility passwordUtility ;

	// -------------------------------------------------------------------------

	/**
	 * uploadRootDirPath 取得.
	 *
	 * @return uploadRootDirPath
	 */
	public String getUploadRootDirPath()
	{
		return uploadRootDirPath ;
	}

	/**
	 * dataRootDirPath 取得.
	 *
	 * @return dataRootDirPath
	 */
	public String getDataRootDirPath()
	{
		return dataRootDirPath ;
	}

	/**
	 * uploadMaxSize 取得.
	 *
	 * @return uploadMaxSize
	 */
	public long getUploadMaxSize()
	{
		return maxFileSize ;
	}

	// -------------------------------------------------------------------------
	// その他の共通処理
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
	public String savedDirPath( String entityName, Serializable id, String fieldName )
	{
		String path = FileHelper.pathConcat( getDataRootDirPath(),
			entityName, String.valueOf( id ), fieldName ) ;

		return path ;
	}

	// -------------------------------------------------------------------------
	// 要求 ID の処理
	// -------------------------------------------------------------------------

	/** 妥当とみなす要求 ID の正規表現 */
	private static final Pattern uploadIdPattern =
		Pattern.compile( "^\\d{8}_\\d{6}_\\d+_\\d{3}_[\\p{Alnum}]+$" ) ;

	/**
	 * 要求 ID 発行.
	 * サーバ間でのユニーク保証は phrase がユニークであることに依存する.
	 *
	 * @param phrase 複数サーバで運用する場合にサーバを識別できるフレーズ
	 *
	 * @return 要求 ID（全 AP サーバ間でユニークな値）
	 */
	public String getNextUploadId( String phrase )
	{
		// {システムユニーク ID}_{phrase のハッシュ} 形式

		String hashcode = hash( phrase ) ;
		StringBuilder sb = new StringBuilder() ;

		sb.append( SystemUniqNo.getNextUniqId() ).append( "_" ).append( hashcode ) ;

		return sb.toString() ;
	}

	/**
	 * 指定された文字列をハッシュ化する.
	 *
	 * @param phrase ハッシュ化する値
	 *
	 * @return ハッシュコード
	 */
	private String hash( String phrase )
	{
		return passwordUtility.encode( phrase, phrase ) ;
	}

	// -------------------------------------------------------------------------

	/**
	 * 指定された文字列が妥当な要求 ID かどうか判定する.
	 *
	 * @param uploadId 判定対象の文字列
	 *
	 * @return true：妥当な要求 ID 形式である　　false：要求 ID でない
	 */
	public boolean isValidUploadId( String uploadId )
	{
		if ( uploadId == null ) return false ;

		return uploadIdPattern.matcher( uploadId ).matches() ;
	}

	// -------------------------------------------------------------------------
	// ファイルアップロード処理（アップロードされたファイルの格納処理）
	// -------------------------------------------------------------------------

	/**
	 * マルチパート形式でアップロードされたファイルを、
	 * 一時ディレクトリ上に保管する.<br />
	 * （アップロード先のディレクトリ指定なし）
	 *
	 * @param request HTTP リクエスト
	 * @param uploadId 要求 ID
	 *
	 * @return	アップロードファイル
	 */
	public File storeTemporary( HttpServletRequest request, String uploadId )
	{
		return storeTemporary( request, uploadId, getUploadRootDirPath() ) ;
	}

	/**
	 * マルチパート形式でアップロードされたファイルを、
	 * 一時ディレクトリ上に保管する.<br />
	 * 同時に複数ファイルのアップロードは行えない
	 * （1 要求 ID に対してアップロードできるファイルは 1 つだけ）.<br />
	 *
	 *	ファイルの格納先は、
	 *	アップロードルートディレクトリ / 要求 ID / 元のファイル名
	 *
	 * @param request HTTP リクエスト
	 * @param uploadId 要求 ID
	 * @param rootPath アップロードファイル格納先ルートディレクトリ
	 *
	 * @return	アップロードファイル
	 */
	public File storeTemporary(
		HttpServletRequest request, String uploadId, String rootPath )
	{
		final String MNAME = "storeTemporary" ;
		if ( log.isDebugEnabled() ) log.debug( "### START : " + MNAME + " : " + uploadId + ", " + rootPath ) ;

		File storeFile = null ;

		try
		{
			// ----- 入力情報チェック

			// ファイル情報抽出

			FileItemStream item = getFirstUploadItem( request ) ;

			if ( item == null )
			{
				throw new RestException( new RestResult(
					ResponseCode.UPLOAD_ERROR_NO_CONTENT, null, uploadId ) ) ;
			}

			// ファイルサイズチェック

//			if ( item.getSize() > getUploadMaxSize() )
//			{
//				throw new RestException( new RestResult(
//					ResponseCode.UPLOAD_ERROR_SIZE_EXCEED,
//					new Object[] { getUploadMaxSize(), item.getSize() }, uploadId ) ) ;
//			}

			// ファイル名抽出

			String fileName = item.getName() ;

			if ( StringUtils.isEmpty( fileName ) )
			{
				throw new RestException( new RestResult(
					ResponseCode.UPLOAD_ERROR_NO_FILENAME, null, uploadId ) ) ;
			}

			fileName = ( new File( fileName ) ).getName() ;

			// ファイル名整形

			fileName = sanitaizeFileName( fileName ) ;

			// ----- 出力先チェック

			storeFile = new File( temporaryPath( rootPath, uploadId, fileName ) ) ;

			File parentFile = storeFile.getParentFile() ;

			checkUploadDir( uploadId, parentFile ) ;

			// ----- 保存とサイズチェック

			int size = 0 ;
			long totalsize = 0 ;

			byte[] buf = new byte[ 5120 ] ;

			InputStream in = null ;
			FileOutputStream out = null ;

			try
			{
				in = item.openStream() ;
				out = new FileOutputStream( storeFile ) ;

				while ( ( size = in.read( buf ) ) != -1 )
				{
					totalsize += size ;

					if ( totalsize > getUploadMaxSize() )
					{
						throw new RestException( new RestResult(
							ResponseCode.UPLOAD_ERROR_SIZE_EXCEED,
							new Object[] { getUploadMaxSize() }, uploadId ) ) ;
					}

					out.write( buf, 0, size ) ;
				}

				out.flush() ; out.close() ;
			}
			finally
			{
				IOUtils.closeQuietly( in ) ;
				IOUtils.closeQuietly( out ) ;
			}

			if ( log.isDebugEnabled() ) log.debug( "### END : " + MNAME + " : " + storeFile.getAbsolutePath() ) ;

			return storeFile ;
		}
		catch ( RestException ex ) { throw ex ; }
		catch ( FileUploadBase.FileSizeLimitExceededException ex )
		{
			throw new RestException( new RestResult(
				ResponseCode.UPLOAD_ERROR_SIZE_EXCEED,
				new Object[] { -1, ex.getActualSize() }, uploadId ), ex ) ;
		}
		catch ( FileUploadException ex )
		{
			throw new RestException( new RestResult(
				ResponseCode.UPLOAD_ERROR_PARSE_REQUEST, null, uploadId ), ex ) ;
		}
		catch ( Exception ex )
		{
			throw new RestException( new RestResult(
				ResponseCode.UPLOAD_ERROR_STORE_FILE, null, uploadId ), ex ) ;
		}
	}

	/**
	 * 処理対象ブロック（最初のファイルアップロードブロック）を抽出
	 *
	 * @param request HTTP リクエスト
	 *
	 * @return 処理対象ブロック
	 * @throws FileUploadException エラー発生時
	 * @throws IOException エラー発生時
	 */
	private FileItemStream getFirstUploadItem( HttpServletRequest request )
		throws FileUploadException, IOException
	{
		ServletFileUpload upload = new ServletFileUpload( /* factory */ ) ;

		upload.setFileSizeMax( getUploadMaxSize() ) ;
		upload.setHeaderEncoding( headerEncoding ) ;

		FileItemIterator ite = upload.getItemIterator( request );

		while ( ite.hasNext() )
		{
			FileItemStream item = ite.next() ;
			if ( ! item.isFormField() ) return item ;
		}

		return null ;
	}

	/** ファイル名中で全角に変換する文字 */
	private static final Map<Integer, String> toZenkakuChar = new HashMap<Integer, String>( 9 ) ;
	static
	{
		toZenkakuChar.put( "\\".codePointAt( 0 ), "￥" ) ;
		toZenkakuChar.put( "/".codePointAt( 0 ),  "／" ) ;
		toZenkakuChar.put( ":".codePointAt( 0 ),  "：" ) ;
		toZenkakuChar.put( "*".codePointAt( 0 ),  "＊" ) ;
		toZenkakuChar.put( "?".codePointAt( 0 ),  "？" ) ;
		toZenkakuChar.put( "\"".codePointAt( 0 ), "”" ) ;
		toZenkakuChar.put( "<".codePointAt( 0 ),  "＜" ) ;
		toZenkakuChar.put( ">".codePointAt( 0 ),  "＞" ) ;
		toZenkakuChar.put( "|".codePointAt( 0 ),  "｜" ) ;
	}

	/**
	 * ファイル名補整.<br/>
	 * <ul>
	 * <li>ファイル名に使用できない文字を全角文字に置き換える</li>
	 * <li>制御文字は半角アンダースコアに置き換える</li>
	 * </ul>
	 *
	 * @param fileName 補正するファイル名
	 *
	 * @return 補正後のファイル名
	 */
	private String sanitaizeFileName( String fileName )
	{
		StringBuilder sb = new StringBuilder() ;

		fileName = Normalizer.normalize( fileName, Form.NFC );

		for ( int i = 0 ; i < fileName.length() ; )
		{
			int ch = fileName.codePointAt( i ) ;

			i += Character.charCount( ch );

			String sanitized = toZenkakuChar.get( ch ) ;

			if ( sanitized != null ) // 全角に変換する文字
			{
				sb.append( sanitized ) ;
			}
			else if ( Character.isISOControl( ch ) ) // ASCII 制御文字
			{
				sb.append( '_' ) ;
			}
			else if ( ch == 0x202E ) // RIGHT-TO-LEFT OVERRIDE
			{
				sb.append( '_' ) ;
			}
			else if ( ( ch == 0x3099 ) || ( ch == 0x309A ) ) // 単独の結合文字（濁点／半濁点）
			{
				sb.append( '_' ) ;
			}
			else if ( ( ch >= 0xE000 ) && ( ch <= 0xF8FF ) ) // Unicode 私用領域
			{
				sb.append( '_' ) ;
			}
			else // そのまま
			{
				sb.append( Character.toChars( ch ) ) ;
			}
		}

		if ( isValidUploadId( sb.toString() ) )
		{
			// 要求 ID と重複しないように調整する

			sb.append( "_1" ) ;
		}

		return sb.toString() ;
	}

	/**
	 * アップロードされたファイルの格納先パスを算出
	 *
	 * @param uploadId 要求 ID
	 * @param fileName ファイル名
	 *
	 * @return アップロードされたファイルの格納先パス
	 */
	public String temporaryPath( String uploadId, String fileName )
	{
		if ( fileName == null ) fileName = "" ;

		return FileHelper.pathConcat( getUploadRootDirPath(), uploadId, fileName ) ;
	}

	/**
	 * アップロードされたファイルの格納先パスを算出
	 *
	 * @param rootDirPath 親ディレクトリ（アップロードルートディレクトリ）
	 * @param uploadId 要求 ID
	 * @param fileName ファイル名
	 *
	 * @return アップロードされたファイルの格納先パス
	 */
	private String temporaryPath( String rootDirPath, String uploadId, String fileName )
	{
		if ( fileName == null ) fileName = "" ;

		return FileHelper.pathConcat( rootDirPath, uploadId, fileName ) ;
	}

	/**
	 * 要求 ID ディレクトリチェック（存在していて空であること）.
	 *
	 * @param uploadId 要求 ID
	 * @param uploadDir 要求 ID ディレクトリ
	 */
	public void checkUploadDir( String uploadId, File uploadDir )
	{
		// 要求 ID ディレクトリ存在チェック（要求 ID 発行時に作成されているはず）

		if ( ! uploadDir.exists() )
		{
			// 不正な要求 ID

			throw new RestException( new RestResult(
				ResponseCode.UPLOAD_ERROR_INVALID_ID, null, uploadId ) ) ;
		}

		// 要求 ID ディレクトリ空チェック（要求 ID は使い回し不可＝1 回使いきり）

		String[] children = uploadDir.list() ;

		if ( ( children == null ) || ( children.length != 0 ) )
		{
			// 要求 ID は使用済み（既にアップロードされている）

			throw new RestException( new RestResult(
				ResponseCode.UPLOAD_ERROR_DUPLICATE_ID, null, uploadId ) ) ;
		}
	}

	/**
	 * マルチパート形式でアップロードされたファイルの InputStream を取得.<br />
	 * 同時に複数ファイルのアップロードは行えない
	 * （1 要求 ID に対してアップロードできるファイルは 1 つだけ）.<br />
	 * 取得したストリームは呼び出し側でクローズすること.
	 *
	 * @param request HTTP リクエスト
	 *
	 * @return	アップロードファイル
	 */
	public InputStream getUploadInputStream( HttpServletRequest request )
	{
		final String MNAME = "getUploadInputStream" ;
		if ( log.isDebugEnabled() ) log.debug( "### START : " + MNAME ) ;

		InputStream is = null ;

		boolean complete = false ;
		String uploadId = "***" ;

		try
		{
			// ----- 入力情報チェック

			// ファイル情報抽出

			FileItemStream item = getFirstUploadItem( request ) ;

			if ( item == null )
			{
				throw new RestException( new RestResult(
					ResponseCode.UPLOAD_ERROR_NO_CONTENT, null, uploadId ) ) ;
			}

			// ファイルサイズチェック

//			if ( item.getSize() > getUploadMaxSize() )
//			{
//				throw new RestException( new RestResult(
//					ResponseCode.UPLOAD_ERROR_SIZE_EXCEED,
//					new Object[] { getUploadMaxSize(), item.getSize() }, uploadId ) ) ;
//			}

			is = item.openStream() ;
			complete = true ;

			if ( log.isDebugEnabled() ) log.debug( "### END : " + MNAME ) ;

			return is ;
		}
		catch ( RestException ex ) { throw ex ; }
		catch ( FileUploadBase.FileSizeLimitExceededException ex )
		{
			throw new RestException( new RestResult(
				ResponseCode.UPLOAD_ERROR_SIZE_EXCEED,
				new Object[] { -1, ex.getActualSize() }, uploadId ), ex ) ;
		}
		catch ( FileUploadException ex )
		{
			throw new RestException( new RestResult(
				ResponseCode.UPLOAD_ERROR_PARSE_REQUEST, null, uploadId ), ex ) ;
		}
		catch ( Exception ex )
		{
			throw new RestException( new RestResult(
				ResponseCode.UPLOAD_ERROR_GET_STREAM, null, uploadId ), ex ) ;
		}
		finally
		{
			if ( ! complete ) IOUtils.closeQuietly( is ) ;
		}
	}

	/**
	 * マルチパート形式でアップロードされたファイルの InputStream を取得.<br />
	 * 同時に複数ファイルのアップロードは行えない
	 * （1 要求 ID に対してアップロードできるファイルは 1 つだけ）.<br />
	 * 取得したストリームは呼び出し側でクローズすること.
	 * 
	 * メソッド内で抽出したファイル名をinputFileNameにセットする
	 *
	 * @param request HTTP リクエスト
	 * @param inputFileName ファイル名
	 *
	 * @return	アップロードファイル
	 */
	public InputStream getUploadInputStreamAndInputFileName( HttpServletRequest request , StringBuilder inputFileName)
	{
		final String MNAME = "getUploadInputStreamAndInputFileName" ;
		if ( log.isDebugEnabled() ) log.debug( "### START : " + MNAME ) ;

		InputStream is = null ;

		boolean complete = false ;
		String uploadId = "***" ;

		try
		{
			// ----- 入力情報チェック

			// ファイル情報抽出

			FileItemStream item = getFirstUploadItem( request ) ;

			if ( item == null )
			{
				throw new RestException( new RestResult(
					ResponseCode.UPLOAD_ERROR_NO_CONTENT, null, uploadId ) ) ;
			}

			// ファイル名抽出

			String fileName = item.getName() ;

			if ( StringUtils.isEmpty( fileName ) )
			{
				throw new RestException( new RestResult(
					ResponseCode.UPLOAD_ERROR_NO_FILENAME, null, uploadId ) ) ;
			}

			fileName = ( new File( fileName ) ).getName();
			inputFileName.append(fileName);


			is = item.openStream() ;
			complete = true ;

			if ( log.isDebugEnabled() ) log.debug( "### END : " + MNAME ) ;

			return is ;
		}
		catch ( RestException ex ) { throw ex ; }
		catch ( FileUploadBase.FileSizeLimitExceededException ex )
		{
			throw new RestException( new RestResult(
				ResponseCode.UPLOAD_ERROR_SIZE_EXCEED,
				new Object[] { -1, ex.getActualSize() }, uploadId ), ex ) ;
		}
		catch ( FileUploadException ex )
		{
			throw new RestException( new RestResult(
				ResponseCode.UPLOAD_ERROR_PARSE_REQUEST, null, uploadId ), ex ) ;
		}
		catch ( Exception ex )
		{
			throw new RestException( new RestResult(
				ResponseCode.UPLOAD_ERROR_GET_STREAM, null, uploadId ), ex ) ;
		}
		finally
		{
			if ( ! complete ) IOUtils.closeQuietly( is ) ;
		}
	}

	// -------------------------------------------------------------------------
	// アップロードファイルの処理
	// -------------------------------------------------------------------------

	/**
	 * 指定された要求 ID でアップロードされたファイルを取得
	 *
	 * @param uploadId 要求 ID
	 *
	 * @return 指定された要求 ID でアップロードされたファイル
	 */
	public File getUploadFile( String uploadId )
	{
		return getUploadFile( getUploadRootDirPath(), uploadId ) ;
	}

	/**
	 * 指定された要求 ID でアップロードされたファイルを取得
	 *
	 * @param rootDirPath 親ディレクトリ（アップロードルートディレクトリ）
	 * @param uploadId 要求 ID
	 *
	 * @return 指定された要求 ID でアップロードされたファイル
	 */
	public File getUploadFile( String rootDirPath, String uploadId )
	{
		String temporaryDir = temporaryPath( rootDirPath, uploadId, "" ) ;

		try
		{
			return FileHelper.getChildFile( temporaryDir ) ;
		}
		catch ( RestException ex ) { throw ex ; }
		catch ( Exception ex )
		{
			throw new RestException( new RestResult(
				ResponseCode.FILE_ERROR, null, uploadId,
				messageUtility.getMessage( "msg.fileupload.get.error", temporaryDir ) ), ex ) ;
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 指定された ID からファイルのパスを算出する.
	 *
	 * @param uploadId 算出対象の文字列
	 * @return ファイルパス
	 */
	public String getPathFromUploadId( String uploadId )
	{
		String path = null ;

		if ( isValidUploadId( uploadId ) )
		{
			File file  = getUploadFile( uploadId ) ;

			if ( file != null )
			{
				path = file.getAbsolutePath() ;
			}
		}

		return path ;
	}

	// -------------------------------------------------------------------------
	// ファイルハンドラ関連処理
	// -------------------------------------------------------------------------

	/**
	 * ファイルハンドラ生成
	 * （アップロード先のディレクトリ指定なし）
	 *
	 * @param action レコードに対する操作種別
	 * @param fieldName テーブル上の項目名
	 * @param fieldValue 要求 ID またはファイル名
	 * @param targetDirPath 最終格納先ディレクトリのパス
	 *
	 * @return 生成したファイルハンドラ
	 */
	public FileRegistHandler createFileRegistHandler(
		FileRegistHandler.Action action, String fieldName, String fieldValue,
		String targetDirPath )
	{
		return createFileRegistHandler(
			action, fieldName, fieldValue, targetDirPath, getUploadRootDirPath() ) ;
	}

	/**
	 * ファイルハンドラ生成.
	 * 生成したファイルハンドラは、登録／更新／削除時のファイル操作に使用する.
	 *
	 * @param action レコードに対する操作種別
	 * @param fieldName テーブル上の項目名
	 * @param fieldValue 要求 ID またはファイル名
	 * @param targetDirPath 最終格納先ディレクトリのパス
	 * 		（レコード登録前で不明の場合は null を指定する）
	 * @param uploadRootPath アップロードファイル格納先ルートディレクトリのパス
	 *
	 * @return	生成したファイルハンドラ
	 */
	public FileRegistHandler createFileRegistHandler(
		FileRegistHandler.Action action, String fieldName, String fieldValue,
		String targetDirPath, String uploadRootPath )
	{
		if ( action == null ) throw new NullPointerException( "action" ) ;
		if ( fieldName == null ) throw new NullPointerException( "fieldName" ) ;

		FileRegistHandler handler = new FileRegistHandler( restlog, messageUtility, this ) ;

		handler.setAction( action ) ;
		handler.setFieldName( fieldName ) ;
		handler.setFieldValue( fieldValue ) ;

		if ( StringUtils.isNotEmpty( fieldValue ) && isValidUploadId( fieldValue )  )
		{
			String path = getPathFromUploadId( fieldValue ) ;

			handler.setTemporaryFile( path == null ? null : new File( path ) ) ;
		}

		if ( StringUtils.isNotEmpty( targetDirPath ) )
		{
			handler.calcTargetFile( targetDirPath ) ;
		}

		return handler ;
	}
}
