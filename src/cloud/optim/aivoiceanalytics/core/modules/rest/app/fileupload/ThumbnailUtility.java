/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：ThumbnailUtility.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest.app.fileupload;

import java.io.File ;
import java.io.FileInputStream ;
import java.io.FileOutputStream ;
import java.util.HashSet ;
import java.util.Set ;

import org.springframework.stereotype.Component ;

import cloud.optim.aivoiceanalytics.core.common.utility.FileHelper;
import cloud.optim.aivoiceanalytics.core.modules.image.ImageResizeUtility;
import cloud.optim.aivoiceanalytics.core.modules.image.ResizeOption;

/**
 * アスペクト比を維持して画像の解像度を変換する.<br/>
 * <p>
 * 指定可能なオプションは最終的に全て ResizeOption クラスに格納します.
 * オプション内容による処理の変化については
 * ResiziOption クラスのドキュメントを参照してください.
 * </p>
 * <p>
 * 通常、管理画面に表示するサムネールを作成するような用途では
 * 幅／高さと背景色を指定する（またはデフォルト値を変更する）だけで
 * 問題なく利用できるはずです.<br/>
 * 生成する画像の画質をより詳細に調整する場合には
 * 圧縮品質（quality オプション）やシャープネス（sharpness オプション）を
 * 使用してください<br/>
 * </p>
 *
 * @see ResizeOption
 */
@Component
public class ThumbnailUtility
{
	/** サムネール作成可能なファイル種別（拡張子） */
	private static Set<String> EXT = new HashSet<String>() ;

	/**
	 * サムネール作成可能なファイル種別構築.
	 * <ul>
	 * <li>gif の透過部分は黒くなる</li>
	 * <li>gif のアニメーション部分は白くなる</li>
	 * <li>bmp は変換できない（真っ黒になる）ものがある</li>
	 * </ul>
	 */
	static
	{
		EXT.add( "jpeg" ) ;
		EXT.add( "jpg" ) ;
		EXT.add( "png" ) ;
	}

	// -------------------------------------------------------------------------

	/**
	 * サムネール作成可能なファイルか確認する.
	 * ファイル名の拡張子から判定する（ファイル内容は参照しない）.
	 *
	 * @param filename オリジナルファイル名.
	 * @return true：作成可能　　false：それ以外
	 */
	public boolean thumbnailAvailable( String filename ) {

		String ext = FileHelper.getExtension( filename ).toLowerCase() ;

		return EXT.contains( ext ) ;
	}

	// -------------------------------------------------------------------------

	/**
	 * サムネール画像作成（オプション指定なし）
	 *
	 * @param orgFile オリジナル画像ファイル
	 * @param parentDirPath サムネール画像出力先ディレクトリパス
	 * @return 出力ファイル
	 *
	 * @throws Exception エラー発生時
	 */
	public File createThumbnail( File orgFile, String parentDirPath ) throws Exception
	{
		return createThumbnail(
			orgFile, parentDirPath, ResizeOption.getStandardOption() ) ;
	}

	/**
	 * サムネール画像作成
	 *
	 * @param orgFile オリジナル画像ファイル
	 * @param parentDirPath サムネール画像出力先ディレクトリパス
	 * @param option リサイズオプション
	 * @return 出力ファイル
	 *
	 * @throws Exception エラー発生時
	 */
	public File createThumbnail( File orgFile, String parentDirPath, ResizeOption option ) throws Exception
	{
		File outFile = null ;

		// ----- 作成可能か判定（可能でない場合は何もしない）

		if ( ! thumbnailAvailable( orgFile.getName() ) ) return outFile ;

		FileInputStream fis = null ;
		FileOutputStream fos = null ;

		try
		{
			// ----- 入力ファイルの準備

			fis = new FileInputStream( orgFile ) ;

			// ----- 出力先の整備

			// ファイル名作成

			String filename = FileHelper.pathConcat(
				parentDirPath, FileHelper.getBasename( orgFile.getName() ) ) ;

			filename += "." + option.getFormat() ;

			// 出力ファイル

			outFile = new File( filename ) ;

			outFile.getParentFile().mkdirs() ;

			fos = new FileOutputStream( outFile ) ;

			// ----- サムネール作成

			ImageResizeUtility.resize( fis, fos, option ) ;

			return outFile ;
		}
		finally
		{
			if ( fis != null ) try { fis.close() ; } catch ( Exception ex ) {}
			if ( fos != null ) try { fos.flush() ; } catch ( Exception ex ) {}
			if ( fos != null ) try { fos.close() ; } catch ( Exception ex ) {}
		}
	}
}
