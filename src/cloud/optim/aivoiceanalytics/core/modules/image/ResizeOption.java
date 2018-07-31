/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：ResizeOption.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.image;

import java.awt.RenderingHints ;
import java.awt.RenderingHints.Key ;
import java.util.LinkedHashMap ;
import java.util.Map ;

import org.apache.commons.lang3.StringUtils ;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;

/**
 * リサイズ処理のオプションを格納するクラス
 *
 * <p>幅だけ指定された時、指定された幅になるように拡大／縮小する.</p>
 * <p>高さだけ指定された時、指定された高さになるように拡大／縮小する.</p>
 * <p>幅と高さが指定された時、指定された枠内に収まるように拡大／縮小する.</p>
 * <p>どちらも指定されない時、リサイズは行わない.</p>
 * <ul>
 * <li>
 * 指定よりも小さい画像を拡大するかどうかは keepSmallImage オプションで指定する.
 * デフォルトは拡大する.
 * </li>
 * <li>
 * 指定と同じサイズの画像を処理（コピー）するかどうかは keepJustImage オプションで指定する.
 * デフォルトは処理する.
 * </li>
 * <li>
 * 幅と高さの両方が指定された時の縮小方法は resizeType オプションで指定する.
 * デフォルトでは、指定された枠内に収まるように縮小する.
 * （INSET＝短辺の両側に余白ができる）
 * </li>
 * <li>
 * 背景色は幅と高さの両方が指定された場合のみ有効.
 * </li>
 * </ul>
 *
 * @see ResizeType
 */
public class ResizeOption
{
	// -------------------------------------------------------------------------
	// デフォルト値（QualityOption で使用）
	// -------------------------------------------------------------------------

	/** デフォルトの圧縮品質 */
	private static final Float DEFAULT_QUALITY = 1.0f ;

	/** デフォルトのシャープネス */
	private static final Float DEFAULT_SHARPNESS = 1.2f ;

	// -------------------------------------------------------------------------
	// デフォルト値（StandardOption/QualityOption で使用）
	// -------------------------------------------------------------------------

	/** デフォルトの画像フォーマット */
	private static final String DEFAULT_FORMAT = "jpg" ;

	/** デフォルトのリサイズ後の幅（単位：ピクセル） */
	private static final Integer DEFAULT_WIDTH = 320 ;

	/** デフォルトのリサイズ後の高さ（単位：ピクセル） */
	private static final Integer DEFAULT_HEIGHT = 240 ;

	/** デフォルトの背景色（背景の塗りつぶしなし）：RGB 値 */
	private static final Integer DEFAULT_COLOR = null ;

	/** デフォルトのレンダリングヒント */
	private static final Map<RenderingHints.Key, Object> DEFAULT_HINTS =
		new LinkedHashMap<RenderingHints.Key, Object>() ;

	static
	{
		DEFAULT_HINTS.put(
			RenderingHints.KEY_ALPHA_INTERPOLATION,
			RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY ) ;
		DEFAULT_HINTS.put(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON ) ;
		DEFAULT_HINTS.put(
			RenderingHints.KEY_COLOR_RENDERING,
			RenderingHints.VALUE_COLOR_RENDER_QUALITY ) ;
		DEFAULT_HINTS.put(
			RenderingHints.KEY_DITHERING,
			RenderingHints.VALUE_DITHER_ENABLE ) ;
		DEFAULT_HINTS.put(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON ) ;
		DEFAULT_HINTS.put(
			RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY ) ;
		DEFAULT_HINTS.put(
			RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BICUBIC ) ;
		DEFAULT_HINTS.put(
			RenderingHints.KEY_FRACTIONALMETRICS,
			RenderingHints.VALUE_FRACTIONALMETRICS_ON ) ;
		DEFAULT_HINTS.put(
			RenderingHints.KEY_STROKE_CONTROL,
			RenderingHints.VALUE_STROKE_NORMALIZE ) ;
	}

	// -------------------------------------------------------------------------

	/**
	 * 縮小方式を表す enum.
	 */
	public static enum ResizeType
	{
		/**
		 * （デフォルト値）画像が指定された枠内に完全に収まるように縮小する.
		 * （短辺の両側には余白ができる.）
		 */
		INSET,

		/**
		 * どちらかの辺が枠内に収まるように縮小する.
		 * 長辺ははみだすので、真ん中に配置して両側をカット（トリミング）する.
		 * （どちらの辺にも余白はできない.）
		 */
		TRIMMING
	}

	// -------------------------------------------------------------------------
	// 処理方式オプション
	// -------------------------------------------------------------------------

	/**
	 * <p>
	 * true のとき、指定サイズよりも小さい画像はそのまま維持する（＝画像の拡大は行わない）.
	 * </p><p>
	 * false のとき、指定サイズよりも小さい画像は指定サイズに拡大する.
	 * </p>
	 * （デフォルト：false）
	 *
	 */
	private boolean keepSmallImage = false ;

	/**
	 * <p>
	 * true のとき、幅／高さの内の基準となる辺が指定と同サイズの画像は変換処理を行わない.
	 * </p><p>
	 * false のとき、幅／高さの内の基準となる辺が指定と同サイズの画像も変換処理を行う.
	 * </p><p>
	 * ここでの「変換処理」は読み込んだ画像を指定の書式で書きだすことを指し、
	 * 背景／圧縮品質／シャープネスが指定されていない状態で変換処理不要と判断した場合は
	 * 入力内容をそのまま出力先にコピーする.<br/>
	 * （通常は画像を読み込んで出力するだけでファイルサイズや画質は変化してしまうが、
	 * そのままコピーする場合は出力内容と入力内容は同一になる）
	 * </p>
	 * （デフォルト：変換する）.
	 *
	 */
	private boolean keepJustImage = false ;

	/**
	 * 縮小方式（デフォルト：はみ出さずに余白をつくる）.
	 */
	private ResizeType resizeType = ResizeType.INSET ;

	// -------------------------------------------------------------------------
	// リサイズ画像オプション
	// -------------------------------------------------------------------------

	/**
	 * リサイズ後の画像フォーマット.
	 */
	private String format ;

	/**
	 * リサイズ後の幅（単位：ピクセル）.
	 */
	private Integer width ;

	/**
	 * リサイズ語の高さ（単位：ピクセル）.
	 */
	private Integer height ;

	/**
	 * 背景色：RGB 形式.
	 * 値が指定された時、指定サイズの領域内を背景色で塗りつぶす
	 * （余白が背景色の色になる）.<br />
	 * 幅と高さの両方が指定されている場合のみ有効.
	 */
	private Integer bgColor ;

	/**
	 * レンダリングヒント：Graphics2D への設定内容
	 */
	private Map<RenderingHints.Key, Object> rederingHint = DEFAULT_HINTS ;

	/**
	 * 画質：0.0～1.0（1.0 が最高画質）.
	 * 値が指定された時だけ、圧縮率を指定して JPEGImageWriter での出力を行う.
	 *
	 * @see ImageWriterParam#setCompressionQuality()
	 */
	private Float quality ;

	/**
	 * シャープネス（1 より大きい＝シャープ／1 より小さい＝ぼかし／1＝変化なし）.
	 * 値が指定された時だけ、領域平均化アルゴリズムを利用してスケーリングを行う.
	 * @see
	 */
	private Float sharpness ;

	// -------------------------------------------------------------------------
	// ファクトリメソッド
	// -------------------------------------------------------------------------

	/**
	 * デフォルトのリサイズオプションを取得.
	 *
	 * @return デフォルトリサイズオプション（圧縮品質指定なし／シャープネス指定なし）
	 */
	public static ResizeOption getStandardOption()
	{
		return new ResizeOption(
			DEFAULT_FORMAT, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_COLOR,
			DEFAULT_HINTS, null, null ) ;
	}

	/**
	 * 詳細ありのリサイズオプションを取得.
	 *
	 * @return リサイズオプション（圧縮品質指定あり／シャープネス指定あり）
	 */
	public static ResizeOption getQualityOption()
	{
		return new ResizeOption(
			DEFAULT_FORMAT, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_COLOR,
			DEFAULT_HINTS, DEFAULT_QUALITY, DEFAULT_SHARPNESS ) ;
	}

	// -------------------------------------------------------------------------
	// コンストラクタ
	// -------------------------------------------------------------------------

	/**
	 * フルコンストラクタ.
	 *
	 * @param format リサイズ後の画像フォーマット.
	 * @param width リサイズ後の幅
	 * @param height リサイズ後の高さ
	 * @param bgColor 背景色
	 * @param rederingHint レンダリングヒント
	 * @param quality 圧縮品質
	 * @param sharpness シャープネス
	 */
	public ResizeOption( String format, Integer width, Integer height, Integer bgColor,
		Map<Key, Object> rederingHint, Float quality, Float sharpness )
	{
		this.format = format ;
		this.width = width ;
		this.height = height ;
		this.bgColor = bgColor ;
		this.rederingHint = rederingHint ;
		this.quality = quality ;
		this.sharpness = sharpness ;
	}

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() { return ToStringHelper.toString( this ); }

	// -------------------------------------------------------------------------
	// アクセサメソッド
	// -------------------------------------------------------------------------

	/**
	 * format 取得.
	 *
	 * @return format
	 */
	public String getFormat()
	{
		return format ;
	}

	/**
	 * format 設定.
	 *
	 * @param format format への設定値.
	 * @return このオブジェクト
	 */
	public ResizeOption setFormat( String format )
	{
		this.format = format ;
		return this ;
	}

	/**
	 * width 取得.
	 *
	 * @return width
	 */
	public Integer getWidth()
	{
		return width ;
	}

	/**
	 * width 設定.
	 *
	 * @param width width への設定値.
	 * @return このオブジェクト
	 */
	public ResizeOption setWidth( Integer width )
	{
		this.width = width ;
		return this ;
	}

	/**
	 * height 取得.
	 *
	 * @return height
	 */
	public Integer getHeight()
	{
		return height ;
	}

	/**
	 * height 設定.
	 *
	 * @param height height への設定値.
	 * @return このオブジェクト
	 */
	public ResizeOption setHeight( Integer height )
	{
		this.height = height ;
		return this ;
	}

	/**
	 * bgColor 取得.
	 *
	 * @return bgColor
	 */
	public Integer getBgColor()
	{
		return bgColor ;
	}

	/**
	 * bgColor 設定.
	 *
	 * @param bgColor bgColor への設定値.
	 * @return このオブジェクト
	 */
	public ResizeOption setBgColor( Integer bgColor )
	{
		this.bgColor = bgColor ;
		return this ;
	}

	/**
	 * bgColor 設定.
	 *
	 * @param bgColor bgColor への設定値.
	 * @return このオブジェクト
	 */
	public ResizeOption setBgColor( String bgColor )
	{
		Integer value = null ;

		if ( bgColor != null )
		{
			if ( StringUtils.isNumeric( bgColor ) )
			{
				value = Integer.valueOf( bgColor ) ;
			}
			else if ( bgColor.startsWith( "0x" ) )
			{
				value = Integer.valueOf( bgColor.substring( "0x".length() ), 16 ) ;
			}
		}

		this.bgColor = value ;
		return this ;
	}

	/**
	 * rederingHint 取得.
	 *
	 * @return rederingHint
	 */
	public Map<RenderingHints.Key, Object> getRederingHint()
	{
		return rederingHint ;
	}

	/**
	 * rederingHint 設定.
	 *
	 * @param rederingHint rederingHint への設定値.
	 * @return このオブジェクト
	 */
	public ResizeOption setRederingHint( Map<RenderingHints.Key, Object> rederingHint )
	{
		this.rederingHint = rederingHint ;
		return this ;
	}

	/**
	 * quality 取得.
	 *
	 * @return quality
	 */
	public Float getQuality()
	{
		return quality ;
	}

	/**
	 * quality 設定.
	 *
	 * @param quality quality への設定値.
	 * @return このオブジェクト
	 */
	public ResizeOption setQuality( Float quality )
	{
		this.quality = quality ;
		return this ;
	}

	/**
	 * sharpness 取得.
	 *
	 * @return sharpness
	 */
	public Float getSharpness()
	{
		return sharpness ;
	}

	/**
	 * sharpness 設定.
	 *
	 * @param sharpness sharpness への設定値.
	 * @return このオブジェクト
	 */
	public ResizeOption setSharpness( Float sharpness )
	{
		this.sharpness = sharpness ;
		return this ;
	}

	/**
	 * keepSmallImage 取得.
	 *
	 * @return keepSmallImage
	 */
	public boolean isKeepSmallImage()
	{
		return keepSmallImage ;
	}

	/**
	 * keepSmallImage 設定.
	 *
	 * @param resizeSmallImage keepSmallImage への設定値.
	 * @return このオブジェクト
	 */
	public ResizeOption setKeepSmallImage( boolean resizeSmallImage )
	{
		this.keepSmallImage = resizeSmallImage ;
		return this ;
	}

	/**
	 * keepJustImage 取得.
	 *
	 * @return keepJustImage
	 */
	public boolean isKeepJustImage()
	{
		return keepJustImage ;
	}

	/**
	 * keepJustImage 設定.
	 * @param resizeJustImage keepJustImage への設定値.
	 * @return このオブジェクト
	 */
	public ResizeOption setKeepJustImage( boolean resizeJustImage )
	{
		this.keepJustImage = resizeJustImage ;
		return this ;
	}

	/**
	 * resizeType 取得.
	 *
	 * @return resizeType
	 */
	public ResizeType getResizeType()
	{
		return resizeType ;
	}

	/**
	 * resizeType 設定.
	 *
	 * @param resizeType resizeType への設定値.
	 * @return このオブジェクト
	 */
	public ResizeOption setResizeType( ResizeType resizeType )
	{
		this.resizeType = resizeType ;
		return this ;
	}
}
