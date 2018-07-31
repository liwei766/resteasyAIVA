/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：ImageResizeUtility.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.image;

import java.awt.Color ;
import java.awt.Graphics ;
import java.awt.Graphics2D ;
import java.awt.Image ;
import java.awt.RenderingHints ;
import java.awt.Toolkit ;
import java.awt.image.AreaAveragingScaleFilter ;
import java.awt.image.BufferedImage ;
import java.awt.image.ConvolveOp ;
import java.awt.image.FilteredImageSource ;
import java.awt.image.ImageFilter ;
import java.awt.image.ImageProducer ;
import java.awt.image.Kernel ;
import java.awt.image.RasterFormatException ;
import java.io.ByteArrayInputStream ;
import java.io.File ;
import java.io.InputStream ;
import java.io.OutputStream ;
import java.util.Map.Entry ;

import javax.imageio.IIOImage ;
import javax.imageio.ImageIO ;
import javax.imageio.ImageWriteParam ;
import javax.imageio.ImageWriter ;
import javax.imageio.spi.ImageWriterSpi ;
import javax.imageio.stream.ImageOutputStream ;

import org.apache.commons.io.IOUtils ;
import org.apache.commons.logging.Log ;
import org.apache.commons.logging.LogFactory ;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;

/**
 * アスペクト比を維持して画像の解像度を変換する.<br/>
 * <p>
 * 指定可能なオプションは最終的に全て ResizeOption クラスに格納します.
 * オプション内容による処理の変化については
 * ResiziOption クラスのドキュメントを参照してください.
 * </p>
 * <p>
 * 通常、管理画面に表示するサムネールを作成するような用途では
 * 幅／高さと背景色を指定するだけで問題なく利用できるはずです.<br/>
 * 生成する画像の画質をより詳細に調整する場合には
 * 圧縮品質（quality オプション）やシャープネス（sharpness オプション）を
 * 使用してください<br/>
 * </p>
 *
 * <p>
 * 本クラスで使用する用語
 * <dl>
 * <dt>元画像</dt><dd>変換対象となるオリジナルの画像.</dd>
 * <dt>変更後の画像</dt><dd>オリジナルを変換した後の画像.背景（余白）を含まない.</dd>
 * <dt>背景サイズ</dt><dd>背景（余白）を含む変換後の画像のサイズ</dd>
 * </dl>
 * </p>
 *
 * @see ResizeOption
 */
public class ImageResizeUtility
{
	/** Commons Logging instance.  */
	private static final Log log = LogFactory.getLog( ImageResizeUtility.class ) ;

	/**
	 * カラーモードが CMYK か調べる（CMYK 画像はリサイズ不可）.
	 * <p><b>
	 * ※固定で false を返却する.
	 * 正しく判定するためには JAR の追加とメソッド本体のアンコメントが必要
	 * </b></p>
	 *
	 * @param file ファイル.
	 * @return true：CMYK　　false：それ以外
	 * @throws Exception 判定エラー
	 */
	public static boolean isCmykMode( File file ) throws Exception
	{
		boolean ret = false ;

//		java.nio.file.Path path = file.toPath() ;
//
//		try ( InputStream is = java.nio.file.Files.newInputStream( path ) )
//		{
//			org.apache.sanselan.ImageInfo info =
//				org.apache.sanselan.Sanselan.getImageInfo( is, null ) ;
//
//			if ( ( info != null ) &&
//				( info.getColorType() == org.apache.sanselan.ImageInfo.COLOR_TYPE_CMYK ) )
//			{
//				ret = true ;
//			}
//
//		}

		return ret ;
	}

	/**
	 * ICC プロファイルが埋め込まれているか調べる（ICC プロファイル埋め込み画像はリサイズ不可）.
	 * <p><b>
	 * ※固定で false を返却する.
	 * 正しく判定するためには JAR の追加とメソッド本体のアンコメントが必要
	 * </b></p>
	 *
	 * @param file ファイル.
	 * @return true：ICC プロファイルが埋め込まれている　　false：それ以外
	 * @throws Exception 判定エラー
	 */
	public static boolean isIncludeIccProfile( File file ) throws Exception
	{
		boolean ret = false ;

//		java.nio.file.Path path = file.toPath() ;
//
//		try ( java.io.InputStream is = java.nio.file.Files.newInputStream( path ) )
//		{
//			ret = org.apache.sanselan.Sanselan.getICCProfile( is, null ) != null ;
//		}

		return ret ;
	}

	// -------------------------------------------------------------------------

	/**
	 * 画像のリサイズ.
	 *
	 * @param input 入力画像
	 * @param output 出力先
	 *
	 * @throws Exception エラー
	 */
	public static void resize( InputStream input, OutputStream output ) throws Exception
	{
		resize( input, output, ResizeOption.getStandardOption() ) ;
	}

	/**
	 * 画像のリサイズ（サイズ指定）.
	 *
	 * @param input 入力画像
	 * @param output 出力先
	 * @param width リサイズ後の幅（単位：ピクセル）
	 * @param height リサイズ後の高さ（単位：ピクセル）
	 *
	 * @throws Exception エラー
	 */
	public static void resize( InputStream input, OutputStream output,
		Integer width, Integer height ) throws Exception
	{
		resize( input, output, ResizeOption.getStandardOption()
			.setWidth( width )
			.setHeight( height )
		) ;
	}

	/**
	 * 画像のリサイズ（サイズと背景色指定）.
	 *
	 * @param input 入力画像
	 * @param output 出力先
	 * @param width リサイズ後の幅（単位：ピクセル）
	 * @param height リサイズ後の高さ（単位：ピクセル）
	 * @param bgColor 背景色（RGB 値）
	 *
	 * @throws Exception エラー
	 */
	public static void resize( InputStream input, OutputStream output,
		Integer width, Integer height, Integer bgColor ) throws Exception
	{
		resize( input, output, ResizeOption.getStandardOption()
			.setWidth( width )
			.setHeight( height )
			.setBgColor( bgColor )
		) ;
	}

	/**
	 * 画像のリサイズ.
	 *
	 * @param input 入力画像
	 * @param output 出力先
	 * @param width リサイズ後の幅（単位：ピクセル）
	 * @param height リサイズ後の高さ（単位：ピクセル）
	 * @param bgColor 背景色（RGB 値）
	 * @param quality 圧縮品質
	 * @param sharpness シャープネス
	 *
	 * @throws Exception エラー
	 */
	public static void resize( InputStream input, OutputStream output,
		Integer width, Integer height, Integer bgColor, Float quality, Float sharpness )
		throws Exception
	{
		resize( input, output, ResizeOption.getStandardOption()
			.setWidth( width )
			.setHeight( height )
			.setBgColor( bgColor )
			.setQuality( quality )
			.setSharpness( sharpness )
		) ;
	}

	// -------------------------------------------------------------------------
	// リサイズ処理本体
	// -------------------------------------------------------------------------

	/**
	 * 画像のリサイズ.
	 *
	 * @param input 入力画像
	 * @param output 出力先
	 * @param option リサイズオプション
	 *
	 * @throws Exception エラー
	 */
	public static void resize( InputStream input, OutputStream output, ResizeOption option ) throws Exception
	{
		TemporaryData data = new TemporaryData() ;

		ByteArrayInputStream bis = null ;
		BufferedImage img = null ;
		BufferedImage newimg = null ;

		try
		{
									if ( log.isTraceEnabled() ) log.trace( "option : " + ToStringHelper.toString( option ) ) ;

			// ----- ストリームの複製（リサイズしない場合に再ロードが必要なため）

			if ( option.isKeepJustImage() || option.isKeepSmallImage() )
			{
				bis = new ByteArrayInputStream( IOUtils.toByteArray( input ) ) ;

				input = bis ;
			}

			// ----- 元画像ロード

			img = ImageIO.read( input ) ;

			if ( img == null )	// 書式不正
			{
				throw new RasterFormatException( "Input image is not readable." ) ;
			}

									if ( log.isTraceEnabled() ) log.trace( "init : " + data ) ;

			prepare( img, option, data ) ;

									if ( log.isTraceEnabled() ) log.trace( "prepare : " + data ) ;

			// ----- 画像サイズ計算

			calcImageSize( img, option, data ) ;

									if ( log.isTraceEnabled() ) log.trace( "calcImageSize : " + data ) ;

			// ----- 背景サイズ計算

			calcBgSize( img, option, data ) ;

									if ( log.isTraceEnabled() ) log.trace( "calcBgSize : " + data ) ;

			// ----- 画像配置座標計算

			calcOffset( img, option, data ) ;

									if ( log.isTraceEnabled() ) log.trace( "calcOffset : " + data ) ;

			// ----- リサイズ（背景とマージ）

			if ( data.doResize )
			{
				newimg = resizeImage( img, option, data ) ;

									if ( log.isTraceEnabled() ) log.trace( "resizeImage : " + data ) ;
			}
			else
			{
				newimg = img ;
			}

			// シャープネス処理

			if ( option.getSharpness() != null )
			{
				BufferedImage oldimg = newimg ;

				newimg = sharpness( newimg, option, data ) ;
				data.doResize = true ;

									if ( log.isTraceEnabled() ) log.trace( "sharpness : " + data ) ;

				try { oldimg.flush(); } catch ( Exception ex ) {}
			}

			// ----- 出力

			if ( data.doResize || ! input.markSupported() ) // reset() できない場合は copy できない
			{
				outputImage( newimg, output, option, data ) ;

									if ( log.isTraceEnabled() ) log.trace( "outputImage : " + data ) ;
			}
			else
			{
				input.reset() ;
				IOUtils.copy( input, output ) ; // 変換しない（＝コピー）

									if ( log.isTraceEnabled() ) log.trace( "copy : " + data ) ;
			}
		}
		finally
		{
			if ( bis != null ) try { bis.close() ; } catch ( Exception ex ) {}
			try { img.flush(); } catch ( Exception ex ) {}
			try { newimg.flush(); } catch ( Exception ex ) {}
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 計算結果を持ちまわるための構造体.
	 * 途中の計算結果を格納する目的で使用するので、
	 * 内部の値は処理の進捗に合わせて随時変更する.
	 */
	private static class TemporaryData
	{
		// 元画像のサイズ
		/** 元画像のサイズ Width */
		Integer orgW ;
		/** 元画像のサイズ Height */
		Integer orgH ;

		// 指定サイズ（指定なしのとき制限なし（＝最大））
		/** 指定サイズ Width */
		int afterW ;
		/** 指定サイズ Height */
		int afterH ;

		// 変換後の画像サイズ（背景を含まない）
		/** 変換後の画像サイズ Width */
		int newW = -1 ;
		/** 変換後の画像サイズ Height */
		int newH = -1 ;

		// 元画像と変更後のサイズ比較結果：元のサイズ.compareTo(変更後サイズ) の結果
		/** 元画像と変更後のサイズ比較結果 Width */
		int compareW = 0 ;
		/** 元画像と変更後のサイズ比較結果 Height */
		int compareH = 0 ;


		/** 変換する／しないフラグ */
		boolean doResize = false ;


		// 背景サイズ
		/** 背景サイズ Width */
		int bgW = -1 ;
		/** 背景サイズ Height */
		int bgH = -1 ;


		// 背景上での変換後画像の配置座標
		/** 配置座標 DX */
		int dx = 0 ;
		/** 配置座標 DY */
		int dy = 0 ;

		/** 配置座標 SX */
		int sx = 0 ;
		/** 配置座標 SY */
		int sy = 0 ;

		/** 配置座標 SW */
		int sw = 0 ;
		/** 配置座標 SH */
		int sh = 0 ;

		@Override
		public String toString()
		{
			return "TemporaryData ["
				+ "orgW=" + orgW + ", orgH=" + orgH
				+ ", afterW=" + afterW + ", afterH=" + afterH
				+ ", newW=" + newW + ", newH=" + newH
				+ ", compareW=" + compareW + ", compareH=" + compareH
				+ ", doResize=" + doResize
				+ ", bgW=" + bgW + ", bgH=" + bgH + ", dx=" + dx + ", dy=" + dy
				+ ", sx=" + sx + ", sy=" + sy + ", sw=" + sw + ", sh=" + sh
				+ "]" ;
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 計算結果の初期化.
	 *
	 * @param img 元画像
	 * @param option リサイズオプション
	 * @param data 途中の計算結果
	 */
	private static void prepare( BufferedImage img, ResizeOption option, TemporaryData data )
	{
		// 元画像サイズ取得

		data.orgW = img.getWidth() ;
		data.orgH = img.getHeight() ;

		// 指定サイズ（指定がないときは制限なしとして扱う）

		data.afterW = option.getWidth() != null ? option.getWidth() : Integer.MAX_VALUE ;
		data.afterH = option.getHeight() != null ? option.getHeight() : Integer.MAX_VALUE ;

		// 変換後の画像サイズの初期設定（元画像と同じサイズ）

		data.newW = data.orgW ;
		data.newH = data.orgH ;

		// サイズ比較

		data.compareW = data.orgW.compareTo( data.afterW ) ;
		data.compareH = data.orgH.compareTo( data.afterH ) ;
	}

	/**
	 * 変換後の画像サイズを計算する.
	 *
	 * @param img 元画像
	 * @param option リサイズオプション
	 * @param data 途中の計算結果
	 */
	private static void calcImageSize(
		BufferedImage img, ResizeOption option, TemporaryData data  )
	{
		// ----- 変換する／しない判定

		if ( option.isKeepJustImage() ) // 同じサイズなら変換しない
		{
			if ( ( data.compareW == 0 ) && ( data.compareH == 0 ) ) return ;
		}

		if ( option.isKeepSmallImage() ) // 小さい画像は変換しない
		{
			if (
				( data.compareW <= 0 ) && ( data.compareH < 0 ) ||
				( data.compareW < 0 ) && ( data.compareH <= 0 )
			)
			{
				return ;
			}
		}

		// 幅も高さも指定なしなら変換しない

		if ( ( data.afterW == Integer.MAX_VALUE ) && ( data.afterH == Integer.MAX_VALUE ) ) return ;

		// ----- 倍率を計算

		double rateW = Double.MAX_VALUE ;
		double rateH = Double.MAX_VALUE ;
		boolean baseIsWidth = false ; // 拡大／縮小の基準が幅／高さのどちらなのか

		if ( data.afterW != Integer.MAX_VALUE ) rateW = (double)data.afterW / data.orgW ;
		if ( data.afterH != Integer.MAX_VALUE ) rateH = (double)data.afterH / data.orgH ;

		switch ( option.getResizeType() )
		{
		case INSET :
		{
			// 長辺（倍率の小さい方）を指定サイズに合わせる
			// 短辺側に余白が発生する

			if ( rateW < rateH )  baseIsWidth = true ; // 幅を基準に合わせる
			break ;
		}
		case TRIMMING :
		{
			// 短辺（倍率の大きい方）を指定サイズに合わせる
			// 長辺側ははみ出す

			if ( rateW > rateH ) baseIsWidth = true ; // 幅を基準に合わせる
			break ;
		}
		}

							if ( log.isTraceEnabled() )
								log.trace( "baseIsWidth=" + baseIsWidth + ", rateW=" + rateW + ", rateH=" + rateH ) ;

		// ----- 変換後のサイズを計算

		int width  = data.newW ; // 出力サイズ
		int height = data.newH ;

		if ( baseIsWidth ) // 幅を指定サイズに合わせる
		{
			if ( option.isKeepJustImage() && ( data.orgW == data.afterW ) ) ;
			else if ( option.isKeepSmallImage() && ( data.orgW < data.afterW )) ;
			else
			{
				data.doResize = true ;

				width = data.afterW ;
				height = (int)( (double)data.orgH * rateW ) ;

				if ( data.orgW.equals( data.orgH ) ) height = width ; // 元画像は正方形
			}
		}
		else	// 高さを指定サイズに合わせる
		{
			if ( option.isKeepJustImage() && ( data.orgH == data.afterH ) ) ;
			else if ( option.isKeepSmallImage() && ( data.orgH < data.afterH )) ;
			else
			{
				data.doResize = true ;

				height = data.afterH ;
				width = (int)( (double)data.orgW * rateH ) ;

				if ( data.orgW.equals( data.orgH ) ) width = height ; // 元画像は正方形
			}
		}

		data.newW = width ;
		data.newH = height ;
	}

	/**
	 * 背景サイズを計算する.
	 *
	 * @param img 元画像
	 * @param option リサイズオプション
	 * @param data 途中の計算結果
	 */
	private static void calcBgSize(
		BufferedImage img, ResizeOption option, TemporaryData data  )
	{
		// 背景サイズ＝指定サイズ

		data.bgW = data.afterW ;
		data.bgH = data.afterH ;

		// サイズが指定されていない場合は変換後の画像サイズ（＝背景なし）

		if ( data.bgW == Integer.MAX_VALUE ) data.bgW = data.newW ;
		if ( data.bgH == Integer.MAX_VALUE ) data.bgH = data.newH ;

		// 背景色指定がない場合、余白分は作成しない

		if ( option.getBgColor() == null )
		{
			if ( data.newW < data.bgW ) data.bgW = data.newW ;
			if ( data.newH < data.bgH ) data.bgH = data.newH ;
		}
		else
		{
			data.doResize = true ; // 背景色指定がある場合は必ず変換処理が必要
		}
	}

	/**
	 * 変換後のイメージの背景上での配置場所（オフセット）を計算する.
	 *
	 * @param img 元画像
	 * @param option リサイズオプション
	 * @param data 途中の計算結果
	 */
	private static void calcOffset( BufferedImage img, ResizeOption option, TemporaryData data  )
	{
		int dw = data.bgW - data.newW ;
		int dh = data.bgH - data.newH ;

		data.sw = data.newW ;
		data.sh = data.newH ;

		if ( ( dw != 0 ) || ( dh != 0 ) ) data.doResize = true ;

		if ( dw > 0 ) // 余白
		{
			data.dx = dw / 2 ;
		}
		else if ( dw < 0 ) // はみ出し
		{
			data.sx = -dw / 2 ;
			data.sw = data.bgW ;
		}

		if ( dh > 0 ) // 余白
		{
			data.dy = dh / 2 ;
		}
		else if ( dh < 0 ) // はみ出し
		{
			data.sy = -dh / 2 ;
			data.sh = data.bgH ;
		}
	}

	/**
	 * 変換.
	 *
	 * @param img 元画像
	 * @param option リサイズオプション
	 * @param data 途中の計算結果
	 * @return 変換後の画像
	 */
	private static BufferedImage resizeImage(
		BufferedImage img, ResizeOption option, TemporaryData data )
	{
		BufferedImage newimg = null ;
		Graphics2D newgra = null ;

		BufferedImage tmpimg = null ;
		Graphics2D tmpgra = null ;

		boolean allok = false ;

		try
		{
			// 拡大／縮小

			tmpimg = new BufferedImage( data.newW, data.newH, img.getType() ) ;
			tmpgra = tmpimg.createGraphics() ;

			for ( Entry<RenderingHints.Key, Object> hint : option.getRederingHint().entrySet() )
			{
				tmpgra.setRenderingHint( hint.getKey(), hint.getValue() ) ;
			}

			tmpgra.drawImage( img, 0, 0, data.newW, data.newH, null ) ;

			// 描画領域作成

			newimg = new BufferedImage( data.bgW, data.bgH, img.getType() ) ;
			newgra = newimg.createGraphics() ;

			for ( Entry<RenderingHints.Key, Object> hint : option.getRederingHint().entrySet() )
			{
				newgra.setRenderingHint( hint.getKey(), hint.getValue() ) ;
			}

			// 背景色

			if ( option.getBgColor() != null )
			{
				newgra.setColor( new Color( option.getBgColor() ) ) ;
				newgra.fillRect( 0,  0, data.bgW, data.bgH ) ;
			}

			// 描画

			newgra.drawImage(
				tmpimg.getSubimage( data.sx, data.sy, data.sw, data.sh ),
				data.dx, data.dy, null ) ;

			allok = true ;
			return newimg ;
		}
		finally
		{
			try { newgra.dispose() ; } catch ( Exception ex ) {}
			try { tmpgra.dispose() ; } catch ( Exception ex ) {}
			try { tmpimg.flush() ; } catch ( Exception ex ) {}
			if ( ! allok ) try { newimg.flush() ; } catch ( Exception ex ) {}
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * シャープネス処理.
	 *
	 * @param img 変換後の画像（処理対象の画像）
	 * @param option リサイズオプション
	 * @param data 途中の計算結果
	 * @return 変換後の画像
	 */
	private static BufferedImage sharpness(
		BufferedImage img, ResizeOption option, TemporaryData data )
	{
		Image newimg = null ;
		BufferedImage sprite = null ;
		Graphics gra = null ;

		boolean allok = false ;

		try
		{
			ImageFilter filter = new AreaAveragingScaleFilter(
				img.getWidth(), img.getHeight() ) ;
			ImageProducer ip = new FilteredImageSource( img.getSource(), filter ) ;

			newimg = Toolkit.getDefaultToolkit().createImage( ip ) ;

			// シャープネス効果

			sprite = new BufferedImage( newimg.getWidth( null ),
				newimg.getHeight( null ), BufferedImage.TYPE_INT_RGB ) ;

			gra = sprite.getGraphics() ;
			gra.drawImage( newimg, 0, 0, null ) ;

			float center = option.getSharpness() ; // この値がシャープの強さになる

			float around = ( 1 - center ) / 4 ;
			float[] elements = {
				0.0f, around, 0.0f, around, center, around, 0.0f, around, 0.0f
			} ;

			Kernel kernel = new Kernel( 3, 3, elements ) ;
			ConvolveOp convolveop = new ConvolveOp(
				kernel, ConvolveOp.EDGE_NO_OP, null ) ;

			sprite = convolveop.filter( sprite, null ) ;

			allok = true ;
			return sprite ;
		}
		finally
		{
			try { newimg.flush() ; } catch ( Exception ex ) {}
			try { gra.dispose() ; } catch ( Exception ex ) {}
			if ( ! allok ) try { sprite.flush() ; } catch ( Exception ex ) {}
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * 変換結果を出力.
	 *
	 * @param img 変換後の画像（出力対象の画像）
	 * @param output 出力先
	 * @param option リサイズオプション
	 * @param data 途中の計算結果
	 *
	 * @throws Exception エラー
	 */
	protected static void outputImage(
		BufferedImage img, OutputStream output, ResizeOption option, TemporaryData data ) throws Exception
	{
		if ( option.getQuality() != null )
		{
			ImageWriter iw = null ;
			ImageOutputStream iout = null ;

			try
			{
				ImageWriterSpi spi = (ImageWriterSpi)Class.forName(
					"com.sun.imageio.plugins.jpeg.JPEGImageWriterSpi" )
					.newInstance() ;
				iw = spi.createWriterInstance() ;

				ImageWriteParam iwParam = iw.getDefaultWriteParam() ;

				iout = ImageIO.createImageOutputStream( output );

				iwParam.setCompressionMode( ImageWriteParam.MODE_EXPLICIT ) ;
				iwParam.setCompressionQuality( option.getQuality() ) ;

				iw.setOutput( iout ) ;
				iw.write( null, new IIOImage( img, null, null ), iwParam ) ;

				return ;
			}
			catch ( ClassNotFoundException ex ) {} // 通常出力する
			finally
			{
				if ( iout != null ) try { iout.close() ; } catch ( Exception ex ) {}
				if ( iw != null ) try { iw.dispose(); } catch ( Exception ex ) {}
			}
		}

		// 通常出力

		ImageIO.write( img, option.getFormat(), output ) ;
	}
}
