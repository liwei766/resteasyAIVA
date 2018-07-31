"use strict";

/**
 * プロパティ情報アクセス用ユーティリティ（CCS.prop）
 *
 * ※util.js より後
 */

( function () {

	var _util = CCS.util;
	var _data = CCS.data;

	// -------------------------------------------------------------------------
	// 内部処理
	// -------------------------------------------------------------------------

	/**
	 * 全てのジャンルから取得
	 * （キーは property, message などのトップレベルから指定する）
	 *
	 * @param {String} key キー（未設定の場合は全データを取得）
	 * @return {String} プロパティ値
	 */
	function _get( key ) {

		if ( _util.isUndefined( key ) ) { return _data; }
		return _util.resolve( _data, key );
	}

	// -------------------------------------------------------------------------

	/**
	 * プロパティ取得
	 *
	 * @param {String} key キー（未設定の場合は全プロパティを取得）
	 * @return {String} プロパティ値
	 */
	function _getProperty( key ) {

		if ( _util.isUndefined( key ) ) { return _data.property; }
		return _util.resolve( _data.property, key );
	}

	/**
	 * プロパティ設定
	 * @param {String} key キー
	 * @param {String} value プロパティへの設定値
	 */
	function _setProperty( key, value ) {  _data.property[ key ] = value; }

	// -------------------------------------------------------------------------

	/**
	 * リダイレクトマッピング取得
	 *
	 * @param {String} key キー（未設定の場合は全リダイレクト情報を取得）
	 * @return {String} URL
	 */
	function _getRedirectMap( key ) {

		if ( _util.isUndefined( key ) ) { return _data.redirectMap; }
		return _util.resolve( _data.redirectMap, key );
	}

	/**
	 * リダイレクトマッピングを追加する
	 *
	 * @param {String} key キー
	 * @param {String} value URL
	 */
	function _setRedirectMap( key, value ) { _data.redirectMap[ key ] = value; }

	// -------------------------------------------------------------------------

	/**
	 * API マッピング取得
	 *
	 * @param {String} key キー（未設定の場合は全 API 情報を取得）
	 * @return {String} URL
	 */
	function _getApiMap( key ) {

		if ( _util.isUndefined( key ) ) { return _data.apiMap; }
		return _util.resolve( _data.apiMap, key );
	}

	/**
	 * API マッピングを追加する
	 *
	 * @param {String} key キー
	 * @param {String} value URL
	 */
	function _setApiMap( key, value ) { _data.apiMap[ key ] = value; }

	// -------------------------------------------------------------------------

	/**
	 * ラベル取得
	 *
	 * @param {String} key キー（未設定の場合は全ラベルを取得）
	 * @return {String} ラベル
	 */
	function _getLabel( key ) {

		if ( _util.isUndefined( key ) ) { return _data.label; }
		return _util.resolve( _data.label, key );
	}

	/**
	 * ラベル設定
	 *
	 * @param {String} key キー
	 * @param {String} value ラベル
	 */
	function _setLabel( key, value ) { _data.label[ key ] = value; }

	// -------------------------------------------------------------------------

	/**
	 * メッセージ取得
	 *
	 * @param {String} key キー（未設定の場合は全メッセージを取得）
	 * @return {String} メッセージ
	 */
	function _getMessage( key ) {

		if ( _util.isUndefined( key ) ) { return _data.message; }
		return _util.resolve( _data.message, key );
	}

	/**
	 * メッセージ設定
	 *
	 * @param {String} key キー
	 * @param {String} value メッセージ
	 */
	function _setMessage( key, value ) { _data.message[ key ] = value; }

	// -------------------------------------------------------------------------

	/**
	 * validator メッセージ取得（全て common 下に定義）
	 *
	 * @param {String} key キー（未設定の場合は全 validator メッセージを取得）
	 * @return {String} validatorメッセージ
	 */
	function _getValidatorMessage( key ) {

		if ( _util.isUndefined( key ) ) { return _data.validatorMessage; }
		return _util.resolve( _data.validatorMessage, key );
	}

	/**
	 * validator メッセージ設定
	 *
	 * @param {String} key キー
	 * @param {String} value validatorメッセージ
	 */
	function _setValidatorMessage( key, value ) { _data.validatorMessage[ key ] = value; }

	// -------------------------------------------------------------------------
	// 公開オブジェクト
	// -------------------------------------------------------------------------

	var ret = {

		name : "CCS.prop",

		/**
		 * 全てのジャンルから取得
		 */
		get : _get,

		/**
		 * プロパティ取得
		 */
		getProperty : _getProperty,

		/**
		 * プロパティ設定
		 */
		setProperty : _setProperty,

		/**
		 * リダイレクトマッピング取得
		 */
		getRedirectMap : _getRedirectMap,

		/**
		 * リダイレクトマッピング設定
		 */
		setRedirectMap : _setRedirectMap,

		/**
		 * API マッピング取得
		 */
		getApiMap : _getApiMap,

		/**
		 * API マッピング設定
		 */
		setApiMap : _setApiMap,

		/**
		 * ラベル取得
		 */
		getLabel : _getLabel,

		/**
		 * ラベル設定
		 */
		setLabel : _setLabel,

		/**
		 * メッセージ取得
		 */
		getMessage : _getMessage,

		/**
		 * メッセージ設定
		 */
		setMessage : _setMessage,

		/**
		 * validator メッセージ取得
		 */
		getValidatorMessage : _getValidatorMessage,

		/**
		 * validator メッセージ設定
		 */
		setValidatorMessage : _setValidatorMessage,

	}; // end of ret

	CCS.prop = ret;

})( CCS );
