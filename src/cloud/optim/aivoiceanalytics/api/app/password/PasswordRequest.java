/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：PasswordRequest.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.password;

import javax.xml.bind.annotation.XmlRootElement;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;

/**
 * agnecy API リクエストクラス.<br/>
 */
@XmlRootElement(name = "restRequest")
public class PasswordRequest implements java.io.Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	/** 認証入力情報 */
	private InputForm inputForm;

	/** ユーザー一覧検索条件. */
	private SearchForm searchForm;

	/** パスワード変更情報. */
	private EditForm editForm;

	// -------------------------------------------------------------------------

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
	// 内部クラス
	// -------------------------------------------------------------------------
	/** 認証入力情報用. */
	public static final class InputForm implements java.io.Serializable {
		/** serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** エンティティ情報. */
		private String userId;
		private String password;

		/**
		 * 文字列表現への変換.
		 *
		 * @return 文字列表現
		 */
		@Override
		public String toString() {
			return ToStringHelper.toString(this);
		}

		/**
		 * userId 取得.
		 *
		 * @return userId
		 */
		public String getUserId() {
			return userId;
		}

		/**
		 * userId 設定.
		 *
		 * @param userId userId に設定する値.
		 */
		public void setUserId(String userId) {
			this.userId = userId;
		}

		/**
		 * password 取得.
		 *
		 * @return password
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * password 設定.
		 *
		 * @param password password に設定する値.
		 */
		public void setPassword(String password) {
			this.password = password;
		}

	}

	/** パスワード変更編集用. */
	public static final class EditForm implements java.io.Serializable {
		/** serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** エンティティ情報. */
		private String userGuid;
		private String password;
		private String confirmPassword;

		/**
		 * 文字列表現への変換.
		 *
		 * @return 文字列表現
		 */
		@Override
		public String toString() {
			return ToStringHelper.toString(this);
		}

		/**
		 * userGuid 取得.
		 *
		 * @return userGuid
		 */
		public String getUserGuid() {
			return userGuid;
		}

		/**
		 * userGuid 設定.
		 *
		 * @param userGuid userGuid に設定する値.
		 */
		public void setUserGuid(String userGuid) {
			this.userGuid = userGuid;
		}

		/**
		 * password 取得.
		 *
		 * @return password
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * password 設定.
		 *
		 * @param password password に設定する値.
		 */
		public void setPassword(String password) {
			this.password = password;
		}

		/**
		 * confirmPassword 取得.
		 *
		 * @return confirmPassword
		 */
		public String getConfrimPassword() {
			return confirmPassword;
		}

		/**
		 * confirmPassword 設定.
		 *
		 * @param confirmPassword confirmPassword に設定する値.
		 */
		public void setConfirmPassword(String confirmPassword) {
			this.confirmPassword = confirmPassword;
		}
	}

	// -------------------------------------------------------------------------
	// アクセサメソッド
	// -------------------------------------------------------------------------
	/**
	 * inputForm 取得.
	 *
	 * @return InputForm
	 */
	public InputForm getInputForm() {
		return inputForm;
	}

	/**
	 * searchorm 設定.
	 *
	 * @param inputForm inputForm に設定する値.
	 */
	public void setInputForm(InputForm inputForm) {
		this.inputForm = inputForm;
	}

	/**
	 * searchForm 取得.
	 *
	 * @return searchForm
	 */
	public SearchForm getSearchForm() {
		return searchForm;
	}

	/**
	 * searchForm 設定.
	 *
	 * @param searchForm searchForm に設定する値.
	 */
	public void setSearchForm(SearchForm searchForm) {
		this.searchForm = searchForm;
	}

	/**
	 * editForm 取得.
	 *
	 * @return editForm
	 */
	public EditForm getEditForm() {
		return editForm;
	}

	/**
	 * editForm 設定.
	 *
	 * @param entity editForm に設定する値.
	 */
	public void setEditForm(EditForm entity) {
		this.editForm = entity;
	}

}
