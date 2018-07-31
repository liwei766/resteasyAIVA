"use strict";

/**
 * ログインユーザ情報管理クラス（CCS.user）
 */

( function( CCS ) {

	var _prop = CCS.prop;
	var _util = CCS.util;
	var _auth = CCS.auth;
	var _api  = CCS.api;

	var MAX_LEN_COMPANY_ID = 32;
	var MAX_LEN_USER_ID = 32;
	var MAX_LEN_USER_NAME = 100;

	var COMPANY_ID = "companyId";
	var USER_ID = "userId";
	var USER_NAME = "userName";
	var SAVE_VOICE = "saveVoice";
	var AUTH_LIST = "authList";

	// -------------------------------------------------------------------------
	// 初期処理
	// -------------------------------------------------------------------------

	// ---------- ユーザ情報取得

	var userInfo = _refreshData() || _prop.getProperty( "common.anonymousUserInfo" );

	var msg = [];
	var companyId = userInfo.companyId || "";
	if ( companyId.length > MAX_LEN_COMPANY_ID ) msg.push(
		_util.format( _prop.getMessage( "common.accounterror.companyId" ), MAX_LEN_COMPANY_ID ) );

	var uid = userInfo.userId || "";
	if ( uid.length > MAX_LEN_USER_ID ) msg.push(
		_util.format( _prop.getMessage( "common.accounterror.userId" ), MAX_LEN_USER_ID ) );

	var userName = userInfo.userName || "";
	if ( userName.length > MAX_LEN_USER_NAME ) msg.push(
		_util.format( _prop.getMessage( "common.accounterror.userName" ), MAX_LEN_USER_NAME ) );

	if ( _util.isNotEmpty( msg ) ) {

		msg.unshift( _prop.getMessage( "common.accounterror.title" ) );
		_util.redirectError( "error", msg );
	}

	// ---------- 権限チェック

	_auth.authList( _getData( AUTH_LIST ) );

	var urlInfo = _prop.getRedirectMap( _util.currentModuleName() );
	var defaultInfo = _prop.getRedirectMap( "*" );

	var authOk =  false;

	if ( urlInfo ) {
		authOk =
			_auth.authCheck( _util.resolve( urlInfo, "rule" ) ) &&
			_auth.authCheckReverse( _util.resolve( urlInfo, "notrule" ) );
	}
	else if ( defaultInfo ) {
		authOk =
			_auth.authCheck( _util.resolve( defaultInfo, "rule" ) ) &&
			_auth.authCheckReverse( _util.resolve( defaultInfo, "notrule" ) );
	}

	// 権限チェックエラーの場合はエラー画面に遷移
	if ( ! authOk ) {

		if ( _auth.authCheck( "ROLE_ANONYMOUS" ) ) {
			_util.redirectError( "error", _prop.getMessage( "common.systemerror.unauthError" ) ); // 未ログイン
		}
		else {
			_util.redirectError( "error", _prop.getMessage( "common.systemerror.authError" ) ); // 権限不足
		}
	}

	// -------------------------------------------------------------------------
	// 内部処理
	// -------------------------------------------------------------------------

	/**
	 * ログイン情報取得
	 *
	 * @param {String} key キー（指定されない場合は全ての情報を返す）
	 * @return {Object} 設定値
	 */
	function _getData( key ) {

		if ( _util.isUndefined( key ) ) {
			return userInfo;
		}

		return _util.resolve( userInfo, key );
	}

	/**
	 * ユーザ情報取得
	 */
	function _refreshData() {

		var info = null;

		// ユーザ情報取得

		var form = {};

		// API 送信
		var url = _prop.getApiMap( "login.info" );
		var json = JSON.stringify( form );

		var option = {
			handleSuccess : function ( retData, status, xhr, option ) {

				var userInfo = retData.loginInfo || _prop.getProperty( "common.anonymousUserInfo" );

				info = {
					companyId : userInfo.companyId,
					userId : userInfo.userId,
					userName : userInfo.userName,
					saveVoice : userInfo.saveVoice,
					authList : userInfo.authIdList,
				};
			},
		};

		_api.postJSONSync( url, json, option );

		return info;
	}

	// -----------------------------------------------------------------------------
	// 公開情報
	// -----------------------------------------------------------------------------

	var ret = {

		name : "CCS.user",

		COMPANY_ID : "companyId",
		USER_ID : "userId",
		USER_NAME : "userName",
		SAVE_VOICE : "saveVoice",
		AUTH_LIST : "authList",

		/**
		 * ユーザ情報取得
		 * 引数に値を設定しなければ全て返す
		 * 例）
		 * 	var _user = CCS.user;
		 *	_user.getData( _user.COMPANY_ID ); ←企業 ID 取得
		 *	_user.getData( _user.AUTH_LIST ); ←権限リスト取得
		 *
		 * @param {String} 取得情報名（COOP_ID, USER_ID, ...）
		 * @return {String} ユーザ名
		 */
		getData : _getData,

	}; // end of ret

	CCS.user = ret;

})( CCS );
