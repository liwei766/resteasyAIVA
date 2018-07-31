/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：PasswordResponse.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.password;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;
import cloud.optim.aivoiceanalytics.core.modules.xauth.OptimalBizUserInfo;

/**
 * Password API レスポンスクラス.<br/>
 */
@XmlRootElement(name = "restResponse")
public class PasswordResponse implements java.io.Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** 処理結果. */
	private List<RestResult> resultList = new ArrayList<RestResult>();

	// -------------------------------------------------------------------------

	/** 検索結果. */
	private List<SearchResult> searchResultList;

	// -------------------------------------------------------------------------

	/** ページ情報. */
	private PageInfo pageInfo;

	// -------------------------------------------------------------------------

	/** 1 エンティティ情報 */
	private EditResult editResult;

	/**
	 * 文字列表現への変換.
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() {
		return ToStringHelper.toString(this);
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
		if (resultList == null) {
			return 0;
		}
		return resultList.size();
	}

	/**
	 * 処理結果を登録する.
	 *
	 * @param result 登録する処理結果
	 */
	public void addResult(RestResult result) {
		if (resultList == null) {
			resultList = new ArrayList<RestResult>();
		}
		resultList.add(result);
	}

	/**
	 * 処理結果を登録する.
	 *
	 * @param result 登録する処理結果
	 */
	public void setResult(RestResult result) {
		if (resultList == null) {
			resultList = new ArrayList<RestResult>();
		}
		resultList.clear();
		resultList.add(result);
	}

	// -------------------------------------------------------------------------
	// 内部クラス
	// -------------------------------------------------------------------------

	/** 一括処理結果. */
	public static final class BulkResult extends SearchResult {

		/** serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** 処理結果. */
		private List<RestResult> resultList = new ArrayList<RestResult>();

		/**
		 * 処理結果を登録する.
		 *
		 * @param result 登録する処理結果
		 */
		public void addResult(RestResult result) {
			if (resultList == null) {
				resultList = new ArrayList<RestResult>();
			}
			resultList.add(result);
		}

		/**
		 * 処理結果を登録する.
		 *
		 * @param result 登録する処理結果
		 */
		public void setResult(RestResult result) {
			if (resultList == null) {
				resultList = new ArrayList<RestResult>();
			}
			resultList.clear();
			resultList.add(result);
		}

		/**
		 * resultList 取得.
		 *
		 * @return resultList
		 */
		@XmlElementWrapper(name = "resultList")
		@XmlElement(name = "result")
		@JsonProperty("resultList")
		public List<RestResult> getResultList() {
			return resultList;
		}

		/**
		 * resultList 設定.
		 *
		 * @param resultList resultList に設定する値.
		 */
		public void setResultList(List<RestResult> resultList) {
			this.resultList = resultList;
		}
	}

	/** 1 件返却用 */
	public static final class EditResult implements java.io.Serializable {

		/** serialVersionUID */
		private static final long serialVersionUID = 1L;

		/** 1 エンティティ情報 */
		private PageInfo pageInfo;

		/** OpitmalBiz ユーザ情報一覧 */
		private ArrayList<OptimalBizUserInfo> userListInfo;

		/**
		 * 文字列表現への変換
		 *
		 * @return 文字列表現
		 */
		@Override
		public String toString() { return ToStringHelper.toString( this ); }

		/**
		 * pageInfo 取得.
		 *
		 * @return pageInfo
		 */
		public PageInfo getPageInfo() { return pageInfo; }

		/**
		 * pageInfo 設定.
		 *
		 * @param pageInfo pageInfo に設定する値.
		 */
		public void setPageInfo( PageInfo pageInfo ) { this.pageInfo = pageInfo; }


		/**
		 * userListInfo 取得.
		 *
		 * @return userListInfo
		 */
		public ArrayList<OptimalBizUserInfo> getUserListInfo() { return userListInfo; }

		/**
		 * userListInfo 取得.
		 *
		 * @return userListInfo
		 */
		public void setUserListInfo(ArrayList<OptimalBizUserInfo> userListInfo ) {	this.userListInfo = userListInfo; }

	}

	// -------------------------------------------------------------------------
	// アクセサメソッド
	// -------------------------------------------------------------------------

	/**
	 * resultList 取得.
	 *
	 * @return resultList
	 */
	@XmlElementWrapper(name = "resultList")
	@XmlElement(name = "result")
	@JsonProperty("resultList")
	public List<RestResult> getResultList() {
		return resultList;
	}

	/**
	 * resultList 設定.
	 *
	 * @param resultList resultList に設定する値.
	 */
	public void setResultList(List<RestResult> resultList) {
		this.resultList = resultList;
	}

	/**
	 * searchResultList 取得.
	 *
	 * @return searchResultList
	 */
	@XmlElementWrapper(name = "searchResultList")
	@XmlElement(name = "searchResult")
	@JsonProperty("searchResultList")
	public List<SearchResult> getSearchResultList() {
		return searchResultList;
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
	 * @param editResult2 editResult に設定する値.
	 */
	public void setEditResult( EditResult editResult ) {

	this.editResult = editResult;
	}

	/**
	 * searchResultList 設定.
	 *
	 * @param searchResultList searchResultList に設定する値.
	 */
	public void setSearchResultList(List<SearchResult> searchResultList) {
		this.searchResultList = searchResultList;
	}

	/**
	 * pageInfo 取得.
	 *
	 * @return pageInfo
	 */
	public PageInfo getPageInfo() {
		return pageInfo;
	}

	/**
	 * pageInfo 設定.
	 *
	 * @param pageInfo pageInfo に設定する値.
	 */
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

}
