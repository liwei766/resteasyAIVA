/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：SortForm.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
/*
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest;

import java.io.Serializable ;
import java.util.ArrayList ;
import java.util.List ;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;


/**
 * ソート／検索件数指定ビーン.
 *
 * @author itsukaha
 */
public class SortForm implements java.io.Serializable
{
	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** ソート項目 */
	private List<SortElement> sortElement ;

	/** 最大取得件数 */
	private Long maxResult ;

	/** 取得開始位置 */
	private Long offset = 0L ;

	// -------------------------------------------------------------------------
	// 処理
	// -------------------------------------------------------------------------

	/**
	 * ソード項目追加
	 *
	 * @param element 追加するソート項目
	 */
	public void addSortElement( SortElement element )
	{
		if ( sortElement == null )
		{
			sortElement = new ArrayList<SortElement>();
		}

		sortElement.add( element ) ;
	}

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() { return ToStringHelper.toString( this ) ; }

	// -------------------------------------------------------------------------
	// 内部クラス
	// -------------------------------------------------------------------------

	/** 1 ソート項目指定 */
	public static class SortElement implements Serializable
	{
		/** serialVersionUID  */
		private static final long serialVersionUID = 1L;

		/** ソート項目名 */
		String name ;

		/** ソート方向（true：昇順　false：降順） */
		boolean asc = false ;

		/**
		 * 文字列表現への変換
		 *
		 * @return 文字列表現
		 */
		@Override
		public String toString() { return ToStringHelper.toString( this ) ; }

		// ---------------------------------------------------------------------
		// コンストラクタ
		// ---------------------------------------------------------------------

		/**
		 * デフォルトコンストラクタ.
		 */
		public SortElement() {}

		/**
		 * コンストラクタ.
		 *
		 * @param name ソート項目名
		 */
		public SortElement( String name )
		{
			this.name = name ;
		}

		/**
		 * コンストラクタ.
		 *
		 * @param name ソート項目名
		 * @param asc ソート方向（true のとき昇順）
		 */
		public SortElement( String name, boolean asc )
		{
			this.name = name ;
			this.asc = asc ;
		}

		// ---------------------------------------------------------------------
		// アクセサメソッド
		// ---------------------------------------------------------------------

		/**
		 * name 取得.
		 *
		 * @return name
		 */
		public String getName()
		{
			return name ;
		}

		/**
		 * name 設定.
		 *
		 * @param name name に設定する値.
		 */
		public void setName( String name )
		{
			this.name = name ;
		}

		/**
		 * asc 取得.
		 *
		 * @return asc
		 */
		public boolean isAsc()
		{
			return asc ;
		}

		/**
		 * asc 設定.
		 *
		 * @param asc asc に設定する値.
		 */
		public void setAsc( boolean asc )
		{
			this.asc = asc ;
		}
	}

	// -------------------------------------------------------------------------
	// アクセサメソッド
	// -------------------------------------------------------------------------

	/**
	 * sortElement 取得.
	 *
	 * @return sortElement
	 */
	public List<SortElement> getSortElement()
	{
		return sortElement ;
	}

	/**
	 * sortElement 設定.
	 *
	 * @param sortElement sortElement に設定する値.
	 */
	public void setSortElement( List<SortElement> sortElement )
	{
		this.sortElement = sortElement ;
	}

	/**
	 * maxResult 取得.
	 *
	 * @return maxResult
	 */
	public Long getMaxResult()
	{
		return maxResult ;
	}

	/**
	 * maxResult 設定.
	 *
	 * @param maxResult maxResult に設定する値.
	 */
	public void setMaxResult( Long maxResult )
	{
		this.maxResult = maxResult ;
	}

	/**
	 * offset 取得.
	 *
	 * @return offset
	 */
	public Long getOffset()
	{
		return offset ;
	}

	/**
	 * offset 設定.
	 *
	 * @param offset offset に設定する値.
	 */
	public void setOffset( Long offset )
	{
		this.offset = offset ;
	}
}
