package cloud.optim.aivoiceanalytics.core.common.fileupload;

import java.io.File ;

import org.apache.commons.io.FileUtils ;
import org.apache.commons.lang3.StringUtils ;
import org.apache.commons.logging.Log ;
import org.apache.commons.logging.LogFactory ;

import cloud.optim.aivoiceanalytics.core.common.utility.FileHelper;
import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;
import cloud.optim.aivoiceanalytics.core.modules.rest.MessageUtility;
import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestException;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestLog;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;
import cloud.optim.aivoiceanalytics.core.modules.rest.app.fileupload.FileUploadUtility;

/**
 * ファイル登録ハンドラ.<br />
 * <p>
 * レコード登録／更新／削除時に、
 * アップロードファイルや登録済みファイルを処理します
 * </p><p>
 * 1 つのアップロードファイル（1 カラム分）につき、
 * 本クラスのインスタンスを 1 つ用意して使用します.
 * </p>
 *
 * @author itsukaha
 */
public class FileRegistHandler
{
	/** Commons Logging instance.  */
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/** RestLog */
	private RestLog restlog ;

	/** MessageUtility */
	private MessageUtility messageUtility ;

	/** FileUploadUtility */
	private FileUploadUtility fileUploadUtility ;

	/**
	 * action への設定値.
	 */
	public static enum Action
	{
		/** 登録 */ INSERT,
		/** 更新 */ UPDATE,
		/** 削除 */ DELETE
	} ;

	// -------------------------------------------------------------------------

	/**
	 * レコードに対する操作内容
	 */
	private Action action ;

	/**
	 * 項目名（≒カラム名）
	 */
	private String fieldName ;

	/**
	 * 項目値（項目に設定された値.要求 ID またはファイル名のいずれか）
	 *
	 * ファイルがアップロードされている場合は要求 ID.<br />
	 * 前回の登録内容から変更されていない場合はファイル名.<br />
	 * その他の場合は null<br />
	 * <br />
	 * 設定方法の詳細は以下の通り.<br />
	 *
	 * <pre>
	 *
	 * 【新規登録時】
	 * 　・ファイルアップロードあり→要求 ID
	 * 　・ファイルアップロードなし→null
	 *
	 * 【削除時】
	 * 　常に null を設定
	 *
	 * 【更新時】
	 *
	 * 　＜ファイルアップロードあり＞
	 * 　　要求 ID
	 *
	 * 　＜ファイルアップロードなし＞
	 * 　　・既存の登録ファイルなし／変更せず→null
	 * 　　・既存の登録ファイルあり／変更せず→ファイル名
	 * 　　・（同上）　　　　　　　／削除する→null
	 *
	 * </pre>
	 */
	private String fieldValue ;

	/**
	 * アップロード時の格納ファイル.
	 *
	 * ファイルがアップロードされている（＝fieldValue に要求 ID が格納されている）場合は
	 * アップロードファイル.
	 * ファイルがアップロードされていない合は null
	 */
	private File temporaryFile ;

	/**
	 * 最終的な格納ファイル
	 * （レコード更新時は格納先ファイル、削除時は削除するディレクトリ）.
	 */
	private File targetFile ;

	/** ファイル操作後に親ディレクトリを削除するフラグ */
	private boolean deleteAfterMove = true ;

	/**
	 * 文字列表現への変換.
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString()
	{
		return ToStringHelper.toString( this ) ;
	}

	/**
	 * コンストラクタ（FileUploadUtility クラスから呼び出し）.
	 *
	 * @param restlog RestLog
	 * @param mess メッセージユーティリティ
	 * @param fileUtil ファイルアップロードユーティリティ
	 */
	public FileRegistHandler( RestLog restlog, MessageUtility mess, FileUploadUtility fileUtil )
	{
		this.restlog = restlog ;
		this.messageUtility = mess ;
		this.fileUploadUtility = fileUtil ;
	}

	// -------------------------------------------------------------------------

	/**
	 * ファイルの最終格納先を設定
	 * （必要な場合は先に setTemporaryFile() しておくこと）.
	 *
	 * @param parentDirPath 最終格納ディレクトリパス.
	 */
	public void calcTargetFile( String parentDirPath )
	{
		final String MNAME = "calcTargetFile" ;
		if ( log.isDebugEnabled() ) log.debug( "### START : " +  MNAME + " : " + parentDirPath ) ;

		if ( temporaryFile != null )
		{
			// ファイルがアップロードされているので移動先ファイルパスを設定

			targetFile = new File( parentDirPath, getTemporaryFile().getName() ) ;
		}
		else if ( fieldValue == null )
		{
			// 削除された（既存ファイルの有無は不明）：格納ディレクトリを設定

			targetFile = new File( parentDirPath ) ;
		}

		// その他の場合、既存ファイルが存在して変更されていない＝targetFile なし

		if ( log.isDebugEnabled() ) log.debug( "### END : " + MNAME + " : " +
			( ( targetFile == null ) ? null : targetFile.getAbsolutePath() ) ) ;
	}

	// -------------------------------------------------------------------------

	/**
	 * レコード処理に伴うファイル操作実行.
	 */
	public void execute()
	{
		switch ( action )
		{
			case INSERT : onSave() ; break ;
			case UPDATE : onUpdate() ; break ;
			case DELETE : onDelete() ; break ;
		}
	}

	/**
	 * レコード新規登録時の処理
	 *
	 * ファイルがアップロードされている場合、一時ディレクトリに存在するファイルを
	 * 最終格納先に移動する.
	 *
	 * @throws RestException 移動元ファイルが存在しない／移動先ファイルが
	 */
	private void onSave()
	{
		final String MNAME = "onSave" ;
		if ( log.isDebugEnabled() ) log.debug( "### START : " + MNAME + " : " + this ) ;

		// ファイルアップロードされていない→何もしない

		if ( temporaryFile == null ) return ;

		if ( ! temporaryFile.exists() )
		{
			// 移動元ファイルが存在しない

			throw new RestException( new RestResult(
				ResponseCode.FILE_REGIST_ERROR_ACCESS_ERROR, null, getFieldName(),
				messageUtility.getMessage( "msg.fileupload.regist.src.error",
					getFieldName(), temporaryFile.getPath()
				)
			) ) ;
		}

		/*
		 * 2014.01 修正（yomu フィードバック）
		 * 	MySQL でオートナンバーの PK 値が切り戻された場合や
		 * 	手動の PK を使いまわされた場合に
		 * 	新規登録時でも既存ディレクトリが存在するケースが発生するため、
		 * 	エラーにせず、旧ディレクトリを削除するように修正

		if ( targetFile.exists() )
		{
			// 移動先ファイルが存在する

			throw new RestException( new RestResult(
				ResponseCode.FILE_REGIST_ERROR_MOVE_ERROR, null, getFieldName(),
				messageUtility.getMessage( "msg.fileupload.onsave.dest.error",
					getFieldName(), targetFile.getPath()
				)
			) ) ;
		}
		*/

		// ----- 元のファイルが万一存在する場合はを親ディレクトリ（カラムディレクトリ）ごと削除

		File parent = targetFile.getParentFile() ;

		try
		{
			if ( parent.exists() )
			{
				FileUtils.deleteDirectory( parent ) ;
			}
		}
		catch ( Exception ex )
		{
			throw new RestException( new RestResult(
				ResponseCode.FILE_REGIST_ERROR_MOVE_ERROR, null, getFieldName(),
				messageUtility.getMessage( "msg.fileupload.onupdate.dest.error",
					getFieldName(), parent.getPath()
				)
			), ex ) ;
		}

//		if ( ! targetFile.getParentFile().mkdirs() )
		if ( ! parent.mkdirs() )
		{
			// 移動先ディレクトリ作成失敗（既に存在する場合を含む）

			throw new RestException( new RestResult(
				ResponseCode.FILE_REGIST_ERROR_MOVE_ERROR, null, getFieldName(),
				messageUtility.getMessage( "msg.fileupload.regist.dir.error",
					getFieldName(), parent.getPath()
				)
			) ) ;
		}

		// 移動

		try
		{
			FileUtils.moveFile( temporaryFile, targetFile ) ;
		}
		catch ( Exception ex )
		{
			// 移動失敗

			throw new RestException( new RestResult(
				ResponseCode.FILE_REGIST_ERROR_MOVE_ERROR, null, getFieldName(),
				messageUtility.getMessage( "msg.fileupload.regist.move.error",
					getFieldName(), temporaryFile.getPath(),  targetFile.getPath()
				)
			), ex ) ;
		}

		// 移動元削除（エラーは無視）

		try
		{
			// 移動元の親ディレクトリ削除

			if ( isDeleteAfterMove() )
			{
				FileUtils.cleanDirectory( temporaryFile.getParentFile() ) ;
				FileHelper.delEmptyAncestor(
					temporaryFile.getParentFile(),
					fileUploadUtility.getUploadRootDirPath() ) ;
			}
		}
		catch ( Exception ex ) {}

		if ( log.isDebugEnabled() ) log.debug( "### END : " + MNAME ) ;
	}

	/**
	 * レコード更新時の処理
	 *
	 * ファイルがアップロードされている場合、元のファイルを削除して、
	 * 一時ディレクトリに存在するファイルを最終格納先に移動する.
	 *
	 * @throws RestException 移動元ファイルが存在しない／移動先ファイルが
	 */
	private void onUpdate()
	{
		final String MNAME = "onUpdate" ;
		if ( log.isDebugEnabled() ) log.debug( "### START : " + MNAME + " : " + this ) ;

		if ( targetFile == null ) return ; // 変更なし

		// ----- 移動元ファイルの存在チェック

		if ( ( temporaryFile != null ) && ! temporaryFile.exists() )
		{
			// 移動元ファイルが存在しない

			throw new RestException( new RestResult(
				ResponseCode.FILE_REGIST_ERROR_ACCESS_ERROR, null, getFieldName(),
				messageUtility.getMessage( "msg.fileupload.regist.src.error",
					getFieldName(), temporaryFile.getPath()
				)
			) ) ;
		}

		// ----- 元のファイルを親ディレクトリ（カラムディレクトリ）ごと削除

		File parent = targetFile.getParentFile() ;

		// ファイルが削除されている→targetFile には親ディレクトリが設定されている

		if ( StringUtils.isEmpty( fieldValue ) ) parent = targetFile ;

		try
		{
			if ( parent.exists() )
			{
				FileUtils.cleanDirectory( parent ) ;
			}

			FileHelper.delEmptyAncestor(
				parent,
				fileUploadUtility.getDataRootDirPath() ) ;
		}
		catch ( Exception ex )
		{
			throw new RestException( new RestResult(
				ResponseCode.FILE_REGIST_ERROR_MOVE_ERROR, null, getFieldName(),
				messageUtility.getMessage( "msg.fileupload.onupdate.dest.error",
					getFieldName(), parent.getPath()
				)
			), ex ) ;
		}

		// ----- テンポラリを登録先に移動

		if ( temporaryFile != null )
		{
			if ( ( ! parent.exists() ) && ! parent.mkdirs() )
			{
				// 親ディレクトリ作成失敗

				throw new RestException( new RestResult(
					ResponseCode.FILE_REGIST_ERROR_MOVE_ERROR, null, getFieldName(),
					messageUtility.getMessage( "msg.fileupload.regist.dir.error",
						getFieldName(), parent.getPath()
					)
				) ) ;
			}

			try
			{
				FileUtils.moveFile( temporaryFile, targetFile ) ;
			}
			catch ( Exception ex )
			{
				// 移動失敗

				throw new RestException( new RestResult(
					ResponseCode.FILE_REGIST_ERROR_MOVE_ERROR, null, getFieldName(),
					messageUtility.getMessage( "msg.fileupload.regist.move.error",
						getFieldName(), temporaryFile.getPath(),  targetFile.getPath()
					)
				), ex ) ;
			}

			try
			{
				// 移動元の親ディレクトリ削除

				if ( isDeleteAfterMove() )
				{
					FileUtils.cleanDirectory( temporaryFile.getParentFile() ) ;
					FileHelper.delEmptyAncestor(
						temporaryFile.getParentFile(),
						fileUploadUtility.getUploadRootDirPath() ) ;
				}
			}
			catch ( Exception ex ) {}	// 親ディレクトリの削除失敗は無視
		}

		if ( log.isDebugEnabled() ) log.debug( "### END : " + MNAME ) ;
	}

	/**
	 * レコード削除時の処理
	 *
	 * 最終格納先のファイルと格納ディレクトリを削除する.
	 * （削除失敗時は警告ログ出力のみ.）
	 */
	private void onDelete()
	{
		final String MNAME = "onDelete" ;
		if ( log.isDebugEnabled() ) log.debug( "### START : " + MNAME + " : " + this ) ;

		try
		{
			// 移動元の親ディレクトリ削除

			if ( isDeleteAfterMove() )
			{
				if ( targetFile.exists() )
				{
					FileUtils.cleanDirectory( targetFile ) ;
				}
				FileHelper.delEmptyAncestor(
					targetFile,
					fileUploadUtility.getDataRootDirPath() ) ;
			}
		}
		catch ( Exception ex )
		{
			log.warn( messageUtility.getMessage(
				"msg.fileupload.ondelete.dest.error",
				getFieldName(), targetFile.getAbsolutePath()
			), ex ) ;
		}

		if ( log.isDebugEnabled() ) log.debug( "### END : " + MNAME ) ;
	}

	// -------------------------------------------------------------------------
	// アクセサメソッド
	// -------------------------------------------------------------------------

	/**
	 * action 取得.
	 *
	 * @return action
	 */
	public Action getAction()
	{
		return action ;
	}

	/**
	 * action 設定.
	 *
	 * @param action action に設定する値.
	 */
	public void setAction( Action action )
	{
		this.action = action ;
	}

	/**
	 * fieldName 取得.
	 *
	 * @return fieldName
	 */
	public String getFieldName()
	{
		return fieldName ;
	}
	/**
	 * fieldName 設定.
	 *
	 * @param fieldName fieldName に設定する値.
	 */
	public void setFieldName( String fieldName )
	{
		this.fieldName = fieldName ;
	}

	/**
	 * fieldValue 取得.
	 *
	 * @return fieldValue
	 */
	public String getFieldValue()
	{
		return fieldValue ;
	}

	/**
	 * fieldValue 設定.
	 *
	 * @param fieldValue fieldValue に設定する値.
	 */
	public void setFieldValue( String fieldValue )
	{
		this.fieldValue = fieldValue ;
	}

	/**
	 * temporaryFile 取得.
	 *
	 * @return temporaryFile
	 */
	public File getTemporaryFile()
	{
		return temporaryFile ;
	}

	/**
	 * temporaryFile 設定.
	 *
	 * @param temporaryFile temporaryFile に設定する値.
	 */
	public void setTemporaryFile( File temporaryFile )
	{
		this.temporaryFile = temporaryFile ;
	}

	/**
	 * targetFile 取得.
	 *
	 * @return targetFile
	 */
	public File getTargetFile()
	{
		return targetFile ;
	}

	/**
	 * targetFile 設定.
	 *
	 * @param targetFile targetFile に設定する値.
	 */
	public void setTargetFile( File targetFile )
	{
		this.targetFile = targetFile ;
	}

	/**
	 * restlog 取得.
	 *
	 * @return restlog
	 */
	public RestLog getRestlog()
	{
		return restlog ;
	}

	/**
	 * restlog 設定.
	 *
	 * @param restlog restlog に設定する値.
	 */
	public void setRestlog( RestLog restlog )
	{
		this.restlog = restlog ;
	}

	/**
	 * deleteAfterMove 取得.
	 *
	 * @return deleteAfterMove
	 */
	public boolean isDeleteAfterMove()
	{
		return deleteAfterMove ;
	}

	/**
	 * deleteAfterMove 設定.
	 *
	 * @param deleteAfterMove deleteAfterMove に設定する値.
	 */
	public void setDeleteAfterMove( boolean deleteAfterMove )
	{
		this.deleteAfterMove = deleteAfterMove ;
	}
}
