/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechLogDetailResponse.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.speechlogdetail;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import cloud.optim.aivoiceanalytics.api.entity.SpeechLogDetail;
import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;

/**
 * SpeechLogDetail API レスポンスクラス.<br/>
 */
@XmlRootElement( name="restResponse" )
public class SpeechLogDetailResponse implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	/** 処理結果 */
	private List<RestResult> resultList = new ArrayList<RestResult>();

	// -------------------------------------------------------------------------

	/** 1 エンティティ情報 */
	private EditResult editResult;

	// -------------------------------------------------------------------------

	/** 一括処理結果 */
	private List<BulkResult> bulkResultList;

	// -------------------------------------------------------------------------

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString()
	{
		return ToStringHelper.toString( this );
	}

	// -------------------------------------------------------------------------
	// 処理結果を扱う処理
	// -------------------------------------------------------------------------

	/**
	 * 処理結果数を取得する.
	 *
	 * @return 登録されている処理結果数
	 */
	@XmlTransient
	@JsonIgnore
	public int getResultLength() {

		if ( resultList == null ) return 0;

		return resultList.size();
	}

	/**
	 * 処理結果を登録する.
	 *
	 * @param result 登録する処理結果
	 */
	public void addResult( RestResult result ) {

		if ( resultList == null )
		{
			resultList = new ArrayList<RestResult>();
		}

		resultList.add( result );
	}

	/**
	 * 処理結果を登録する.
	 *
	 * @param result 登録する処理結果
	 */
	public void setResult( RestResult result ) {

		if ( resultList == null )
		{
			resultList = new ArrayList<RestResult>();
		}

		resultList.clear();
		resultList.add( result );
	}

	// -------------------------------------------------------------------------
	// 内部クラス
	// -------------------------------------------------------------------------

	/** 一括処理結果 */
	public static final class BulkResult implements java.io.Serializable {

		/** serialVersionUID  */
		private static final long serialVersionUID = 1L;

		/** 1 エンティティ情報 */
		private EditResult editResult;

		/** 処理結果 */
		private List<RestResult> resultList = new ArrayList<RestResult>();

		/**
		 * 処理結果を登録する.
		 *
		 * @param result 登録する処理結果
		 */
		public void addResult( RestResult result ) {

			if ( resultList == null ) {

				resultList = new ArrayList<RestResult>();
			}

			resultList.add( result );
		}

		/**
		 * 処理結果を登録する.
		 *
		 * @param result 登録する処理結果
		 */
		public void setResult( RestResult result ) {

			if ( resultList == null )
			{
				resultList = new ArrayList<RestResult>();
			}

			resultList.clear();
			resultList.add( result );
		}

		/**
		 * resultList 取得.
		 *
		 * @return resultList
		 */
		@XmlElementWrapper( name="resultList" )
		@XmlElement( name="result" )
		@JsonProperty( "resultList" )
		public List<RestResult> getResultList() {

			return resultList;
		}

		/**
		 * resultList 設定.
		 *
		 * @param resultList resultList に設定する値.
		 */
		public void setResultList( List<RestResult> resultList ) {

			this.resultList = resultList;
		}

		/**
		 * @return editResult
		 */
		public EditResult getEditResult() {
			return editResult;
		}

		/**
		 * @param editResult セットする editResult
		 */
		public void setEditResult(EditResult editResult) {
			this.editResult = editResult;
		}
	}

	/** 1 件返却用 */
	public static final class EditResult implements java.io.Serializable {

		/** serialVersionUID  */
		private static final long serialVersionUID = 1L;

		/** 1 エンティティ情報 */
		private SpeechLogDetail speechLogDetail;

		/**
		 * 文字列表現への変換
		 *
		 * @return 文字列表現
		 */
		@Override
		public String toString() { return ToStringHelper.toString( this ); }

		/**
		 * speechLogDetail 取得.
		 *
		 * @return speechLogDetail
		 */
		public SpeechLogDetail getSpeechLogDetail() { return speechLogDetail; }

		/**
		 * speechLogDetail 設定.
		 *
		 * @param speechLogDetail speechLogDetail に設定する値.
		 */
		public void setSpeechLogDetail( SpeechLogDetail speechLogDetail ) { this.speechLogDetail = speechLogDetail; }
	}

	// -------------------------------------------------------------------------
	// アクセサメソッド
	// -------------------------------------------------------------------------

	/**
	 * resultList 取得.
	 *
	 * @return resultList
	 */
	@XmlElementWrapper( name="resultList" )
	@XmlElement( name="result" )
	@JsonProperty( "resultList" )
	public List<RestResult> getResultList() {

		return resultList;
	}

	/**
	 * resultList 設定.
	 *
	 * @param resultList resultList に設定する値.
	 */
	public void setResultList( List<RestResult> resultList ) {

		this.resultList = resultList;
	}

	/**
	 * editResult 取得.
	 *
	 * @return editResult
	 */
	public EditResult getEditResult() {

		return editResult;
	}

	/**
	 * editResult 設定.
	 *
	 * @param editResult editResult に設定する値.
	 */
	public void setEditResult( EditResult editResult ) {

		this.editResult = editResult;
	}

	/**
	 * bulkResultList 取得.
	 *
	 * @return bulkResultList
	 */
	@XmlElementWrapper( name="bulkResultList" )
	@XmlElement( name="bulkResult" )
	@JsonProperty( "bulkResultList" )
	public List<BulkResult> getBulkResultList() {

		return bulkResultList;
	}

	/**
	 * bulkResultList 設定.
	 *
	 * @param bulkResultList bulkResultList に設定する値.
	 */
	public void setBulkResultList( List<BulkResult> bulkResultList ) {

		this.bulkResultList = bulkResultList;
	}
}
