"use strict";

/*
 * パスワード管理画面
 */

$( function () {

	var _util = CCS.util;
	var _prop = CCS.prop;
	var _api  = CCS.api;
	var _view = CCS.view;
	var _user = CCS.user;

	var data = {

		// 画面構成要素の定義
		// 初期処理で各要素の jQuery オブジェクトを $element として追加する

		component : {

			userParent : {		// ----- ユーザ一覧

				selector : "#main-table tbody",	// <tbody>
			},

			userList : {		// ----- ユーザリスト

				selector : ".ccs-password-user",		// 全ての行 <tr>：一覧コンテナからの相対
			},

			listId : {

				selector : ".ccs-password-user-listId",		// ID <td>：一覧コンテナからの相対
			},

			listUserName : {

				selector : ".ccs-password-user-listUserName",		// Name <td>：一覧コンテナからの相対
			},

			editParent : {	// ----- 編集エリア

				selector : "#editParent",
			},

			dispNo : {		// ----- 表示番号(ID)

				selector : "#dispNo"
			},

			userName : {		// ----- ユーザ名

				selector : "#inputUserName",
			},

			password : {		// ----- パスワード

				selector : "#inputPassword",
			},

			confirmPassword : {		// ----- パスワード（確認用）

				selector : "#inputConfirmPassword",
			},

			update : {		// ----- 更新ボタン

				selector : "#update",
				handler : [
					[ "click", update ]
				],
			},

			// ---------- ページング

			// 前ページボタン
			prevPage : {

				selector : "#prevPage",
				handler : [
					[ "click", _prevPage ],
				],
			},

			// 次ページボタン
			nextPage : {

				selector : "#nextPage",
				handler : [
					[ "click", _nextPage ],
				],
			},

			// ページ情報
			page : {

				selector : "#page",
			},

			// 総件数
			totalNumber : {

				selector : "#totalNumber",
			},

			// ---------- 認証ダイアログ

			// 認証ダイアログ
			authDialog : {

				selector : "#authDialog",
				handler : [
					[ "shown.bs.modal", _authDialogFocusInput ],
				],
			},

			// 認証ユーザID
			inputAuthUserId : {

				selector : "#inputAuthUserId",
			},

			// 認証パスワード 入力フォーム
			inputAuthPassword : {

				selector : "#inputAuthPassword",
			},

			// 認証ダイアログ OKボタン
			authDialogOK : {

				selector : "#authDialog .modal-footer .btn1-button",
				handler : [
					[ "click", _dialogBtn1Handler ],
				],
			},

			//  認証ダイアログ キャンセルボタン
			authDialogCancel : {

				selector : "#authDialog .modal-footer .btn2-button",
				handler : [
					[ "click", _dialogBtn2Handler ],
				],
			},

			// ---------- ダイアログ共通

			// dialog 共通
			dialog : {

				selector : ".modal[role=opdialog]",
			},

			// dialog 共通 メッセージ
			dialogMessage : {

				selector : ".modal-body p", // dialog からの相対位置
			},

		}, // end of component

		firstDispFlg : true, // 画面初期表示フラグ
		offset : 0, // オフセット
		maxResult : 100,	// 最大表示件数
		totalNumber : null, // 総件数

		user : {}	// 編集中のユーザ情報

	}; // end of data

	// -------------------------------------------------------------------------
	// イベントハンドラ登録
	// -------------------------------------------------------------------------

	// 1. data.component の selector がある項目について、
	//    jquery オブジェクトを作成して $element として格納する
	// 2. data.component の handler がある項目について、
	//    指定されたイベントのイベントハンドラを登録する

	$.each( data.component, function ( i, component ) {

		if ( _util.isEmpty( component.selector ) ) return true; // continue

		var $component = $( component.selector );

		if ( $component.length < 1 ) return true; // continue

		component.$element = $component;

		if ( component.handler && _util.isArray( component.handler ) ) {

			$.each( component.handler, function( j, handler ) {

				var selector = handler[0];
				var func = handler[1];

				if ( selector && _util.isFunction( func ) ) {

					$component.on( selector, func );
				}
			});
		}
	});

	// ----- ユーザ click

	data.component.userParent.$element.on( "click", data.component.userList.selector, edit );


	// -------------------------------------------------------------------------
	// 初期表示処理
	// -------------------------------------------------------------------------

	init();

	/**
	 * 初期表示処理.
	 */
	function init() {

		var userId = _user.getData( _user.USER_ID );

		data.component.inputAuthUserId.$element.val( userId );

		_initForm();

		_search( data.offset );
	}

	// ダイアログ関連 初期値セット
	_setComponentPsw( data.component.authDialog.selector );

	// -------------------------------------------------------------------------
	// 共通の表示制御
	// -------------------------------------------------------------------------

	/**
	 * 編集エリア初期化.
	 */
	function _initForm() {

		_disableForm();
		_clearForm();
	}

	/**
	 * 編集エリア非活性化.
	 */
	function _disableForm() {

		data.component.editParent.$element.find( "form *").prop( "disabled", true );
		data.component.update.$element.addClass( "cursor-default" );

	}

	/**
	 * 編集エリア活性化.
	 */
	function _enableForm() {

		data.component.editParent.$element.find( "form *").prop( "disabled", false );
		data.component.update.$element.removeClass( "cursor-default" );
	}

	/**
	 * フォームクリア.
	 */
	function _clearForm() {

		data.user = {};

		data.component.editParent.$element.find( "form" )[0].reset();
		data.component.userName.$element.val( "" );
		data.component.password.$element.val( "" );
		data.component.confirmPassword.$element.val( "" );

		data.component.dispNo.$element.text( _prop.getMessage( "password.noSelect" ) );
	}

	/**
	 * ユーザ一覧の選択解除.
	 */
	function _clearSelect() {

		data.component.userParent.$element.find( data.component.userList.selector ).removeClass( "bg-primary" );
	}

	/**
	 * ユーザ一覧の指定行を選択状態にする.
	 *
	 * @param {Element|jQuery} target 選択状態にする <tr> 要素
	 */
	function _applySelect( target ) {

		$( target ).addClass( "bg-primary" );
	}

	/**
	 * 認証ダイアログ上フォーム非活性化.
	 */
	function _disableDialogForm() {

		data.component.authDialog.$element.find( "form *").prop( "disabled", true );
		data.component.authDialogOK.$element.addClass( "cursor-default" );
		data.component.authDialogCancel.$element.addClass( "cursor-default" );
	}

	/**
	 * 認証ダイアログ上フォーム活性化.
	 */
	function _enableDialogForm() {

		data.component.authDialog.$element.find( "form *").prop( "disabled", false );
		data.component.authDialogOK.$element.removeClass( "cursor-default" );
		data.component.authDialogCancel.$element.removeClass( "cursor-default" );
	}

	// -------------------------------------------------------------------------
	// イベントハンドラ：ユーザ一覧
	// -------------------------------------------------------------------------

	// 前ページ表示.
	function _prevPage() {

		var sendOffset = data.offset - data.maxResult;

		if ( sendOffset < 0 ) {

			sendOffset = 0;
		}

		_search( sendOffset );
	}

	// 次ページ表示.
	function _nextPage() {

		var sendOffset = data.offset + data.maxResult;

		if ( data.totalNumber && sendOffset >= data.totalNumber ) {

			sendOffset = data.offset;
		}

		_search( sendOffset );
	}

	/**
	 * 検索.
	 *
	 * @param {String} sendOffset
	 */
	function _search( sendOffset ) {

		var form = { searchForm : {

			sortForm : {
				offset : sendOffset,
			}
		}};

		var url = _prop.getApiMap( "password.search" );

		var json = JSON.stringify( form );

		var option = {

			handleError : _searchError,
			handleSuccess : _searchSuccess
		};

		_api.postJSON( url, json, option );
	}

	/**
	 * 検索 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _searchError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "password.error.listError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
	}

	/**
	 * 検索 API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _searchSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			if ( response.resultList[0].code === _prop.getProperty( "common.apiResponse.bizNoAuthError" ) ) {
				// 未認証
				_authDialog( _prop.getMessage( "password.inputAuthPassword" ), { btn1 : _doAuth, btn2 : _redirectPage});
				return;
			} else if ( response.resultList[0].code === _prop.getProperty( "common.apiResponse.bizAuthError" ) ) {
				// 認証失敗
				_authDialog( _prop.getMessage( "password.error.authError" ), { btn1 : _doAuth, btn2 : _redirectPage});
				return;
			}

			_searchError( xhr, status, null, option );
			return;
		}

		// ----- 正常終了

		// 一覧表示

		var $parent = data.component.userParent.$element;

		$parent.empty();
		var cntUser = 0;
		$.each( response.searchResultList, function ( i, val ) {

			var user = val;

			var id = user.userGuid;

			var userName = user.userName;

			var $tr = $( "<tr></tr>" )
				.addClass( "ccs-password-user" );

			var $userName = $( "<td></td>" )
				.addClass( "ccs-password-user-listUserName" )
				.attr( "data-ccs-userName", userName )
				.attr( "data-ccs-id", id )
				.text( userName )
				.appendTo( $tr );

			$tr.appendTo( $parent );

			cntUser++;
		});

		$( "#t1 tbody" ).empty().append( $parent.children().clone() );

		// ページ表示

		var pageInfo = response.pageInfo;

		data.offset = pageInfo.offset;
		data.totalNumber = pageInfo.totalNumber;

		var pageDisp = "0";
		var totalNumber = "0";

		if ( cntUser > 0 ) {

			pageDisp = data.offset + 1;

			if ( cntUser > 1) {

				pageDisp += " - " + ( data.offset + cntUser );
			}

			totalNumber = data.totalNumber;
		}

		data.component.page.$element.text( pageDisp );
		data.component.totalNumber.$element.text( totalNumber );

		// 初期表示フラグ更新
		if ( data.firstDispFlg ) {

			data.firstDispFlg = false;
		}

		_initForm();

		_dialogClosePsw( data.component.authDialog.selector );
	}

	/**
	 * 一覧上のユーザ選択処理：ユーザ内容取得／表示.
	 *
	 * @param {Event} event イベント
	 */
	function edit( event ) {

		var id = $( event.target ).closest( data.component.userList.selector )
					.find( data.component.listUserName.selector ).attr( "data-ccs-id" );

		if ( _util.isEmpty( id ) ) return false;

		_initForm();

		_clearSelect();
		_applySelect( $( event.target ).closest( "tr" ) );

		var userName = $( event.target ).closest( data.component.userList.selector )
							.find( data.component.listUserName.selector ).attr( "data-ccs-userName" );

		data.user.guid = id;
		data.user.userName = userName;

		data.component.dispNo.$element.text( "" );
		data.component.userName.$element.val( data.user.userName );

		_enableForm();
	}

	// -------------------------------------------------------------------------
	// イベントハンドラ：更新
	// -------------------------------------------------------------------------

	/**
	 * 更新ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function update( event ) {

		_view.confirmDialog( _prop.getMessage( "common.confirm.update" ), { ok : _updateOk } );
	}

	/**
	 * 更新確認ダイアログの OK ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function _updateOk( event ) {

		// 入力値取得

		var form = _createUpdateForm();

		// API 送信

		var url = _prop.getApiMap( "password.update" );

		var json = JSON.stringify( form );
		var option = {

			handleError : _updateError,
			handleSuccess : _updateSuccess
		};

		_api.postJSONSync( url, json, option );

		return false;
	}

	/**
	 * フォーム入力内容からユーザ更新 API への送信オブジェクトを作成する.
	 *
	 * @return {Object} ユーザ更新 API への送信オブジェクト
	 */
	function _createUpdateForm() {

		var guid = data.user.guid;
		var password = data.component.password.$element.val();
		var confirmPassword = data.component.confirmPassword.$element.val();

		var ret = { editForm : {
				"userGuid" : guid,
				"password" : password,
				"confirmPassword" : confirmPassword,
			}
		};

		return ret;
	}

	/**
	 * ユーザ情報更新 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _updateError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "common.error.update" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
	}

	/**
	 * ユーザ情報更新 API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _updateSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			if ( response.resultList[0].code === _prop.getProperty( "common.apiResponse.bizNoAuthError" ) ) {
				// 未認証
				_authDialog( _prop.getMessage( "password.inputAuthPassword" ), { btn1 : _doAuth, btn2 : _redirectPage});
				return;
			} else if ( response.resultList[0].code === _prop.getProperty( "common.apiResponse.bizAuthError" ) ) {
				// 認証失敗
				_authDialog( _prop.getMessage( "password.error.authError" ), { btn1 : _doAuth, btn2 : _redirectPage});
				return;
			}

			_updateError( xhr, status, null, option );
			return;
		}

		// 正常終了

		_view.infoDialog( _prop.getMessage( "common.complete.update" ) );

		_initForm();

		_clearSelect();

		// 再検索
		_search( 0 );
	}

	// -------------------------------------------------------------------------
	// イベントハンドラ：認証
	// -------------------------------------------------------------------------

	/**
	 * トップページへ遷移.
	 */
	function _redirectPage(){

		_util.redirect( _view.getTopUrl() );
	}

	/**
	 * 認証ダイアログの OK ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function _doAuth( event ) {

		_disableDialogForm();

		// 入力値取得

		var form = _createAuthForm();

		// API 送信

		var url = _prop.getApiMap( "password.xauth" );

		var json = JSON.stringify( form );

		var option = {

			handleError : _authError,
			handleSuccess : _authSuccess
		};

		_api.postJSONSync( url, json, option );

		return false;
	}

	/**
	 * フォーム入力内容から認証 API への送信オブジェクトを作成する.
	 *
	 * @return {Object} 認証 API への送信オブジェクト
	 */
	function _createAuthForm() {

		var authUserId = data.component.inputAuthUserId.$element.val();
		var authPassword = data.component.inputAuthPassword.$element.val();

		var ret = { inputForm : {

				"userId" : authUserId,
				"password" : authPassword,
			}
		};

		return ret;
	}

	/**
	 * 認証 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _authError( xhr, status, errorThrown, option ) {

		var message = _prop.getMessage( "password.error.authError" );

		var opMessage = option.result.msgList[0].message;

		if ( opMessage ) {

			message += "\n" + "(" + opMessage + ")";
		}

		var $dialog = $( data.component.authDialog.selector );

		if ( $dialog.hasClass( "show" ) ) {

			_enableDialogForm();

			var $msg = $dialog.find( _prop.getProperty( "layout.dialogMessage.selector" ) );
			message = _util.escapeHTML( message );
			message = message.replace( /\n|\r\n|\r/, "<br/>" );
			$msg.html( message );
			$msg.addClass( "text-error" );

		} else {

			_authErrorDialog( message, { btn1 : _doAuth , btn2 : _redirectPage});
		}

		return;
	}

	/**
	 * 認証 API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _authSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			var message = "";
			if (response.resultList[0].code === _prop.getProperty( "common.apiResponse.bizAuthError" )) {
				// 認証失敗
				var message = _prop.getMessage( "password.error.failAuthError" );
			}

			if ( message ) {

				var $dialog = $( data.component.authDialog.selector );

				if ( $dialog.hasClass( "show" ) ) {

					_enableDialogForm();

					var $msg = $dialog.find( _prop.getProperty( "layout.dialogMessage.selector" ) );
					$msg.html( message );
					$msg.addClass( "text-error" );

				} else {

					_authErrorDialog( message, { btn1 : _doAuth , btn2 : _redirectPage});
				}

				return;
			}

			_authError( xhr, status, null, option );
			return;
		}

		// 正常終了

		// 最初の画面表示時の認証成功後は検索
		if ( data.firstDispFlg ) {

			_search( 0 );

		} else {

			_dialogClosePsw( data.component.authDialog.selector );
		}

	}

	// -------------------------------------------------------------------------
	// ダイアログ
	// -------------------------------------------------------------------------

	/**
	 * ダイアログ表示.
	 *
	 * @param message {String} 表示メッセージ
	 * @param option {Object} 表示オプション
	 *
	 * option は、ハンドラ内で $dialog.data( "option" ) として取得可能
	 */
	function _authDialog( message, option ) {
		_enableDialogForm();
		return _dialogPsw( "auth", message, option );
	}

	/**
	 * エラーダイアログ表示.
	 *
	 * @param message {String} 表示メッセージ
	 * @param option {Object} 表示オプション
	 *
	 * option は、ハンドラ内で $dialog.data( "option" ) として取得可能
	 */
	function _authErrorDialog( message, option ) {
		_enableDialogForm();
		return _dialogPsw( "authError", message, option );
	}

	/**
	 * ダイアログ表示共通処理.
	 * 
	 * @param type {String} ダイアログ種別
	 * @param message {String} 表示メッセージ
	 * @param option {Object} 表示オプション
	 *
	 * @return {Element} ダイアログ要素
	 */
	var _dialogOptionName = "option";
	function _dialogPsw( type, message, option ) {

		var $dialog = "";
		if ( type === "auth" || type === "authError" ) {
			$dialog = $( data.component.authDialog.selector );
		} else {
			return false;
		}

		var $msg = $dialog.find( data.component.dialogMessage.selector );
		message = _util.escapeHTML( message );
		message = message.replace( /\n|\r\n|\r/, "<br/>" );
		$msg.html( message );

		if ( type === "auth" ) {
			$msg.removeClass( "text-error" );
		} else if ( type === "authError" ) {
			$msg.addClass( "text-error" );
		}

		$dialog.data( _dialogOptionName, option || null );

		if ( ! _dialogIsOpenPsw($dialog) ) $dialog.modal();

		return $dialog[0];
	}

	/**
	 * ダイアログが表示中か調べる.
	 *
	 * @param {Object} selector JQueryセレクタ
	 *
	 * @return {boolean} true : 表示中 false : 表示していない
	 */
	function _dialogIsOpenPsw( selector ) {

		var $dialog = _getDialogPsw( selector );

		return $dialog.hasClass( "show" );
	}

	/**
	 * ダイアログを閉じる（ダイアログが表示されていなければ何もしない）.
	 *
	 * @param {Object} selector JQueryセレクタ
	 */
	function _dialogClosePsw( selector ) {

		if ( _dialogIsOpenPsw(selector) ) {

			var $dialog = _getDialogPsw(selector);
			$dialog.modal( "hide" );
		}
	}

	/**
	 * ダイアログ要素を取得.
	 *
	 * @param {Object} selector JQueryセレクタ
	 *
	 * @return {jQuery} ダイアログ要素.
	 */
	function _getDialogPsw( selector ) {
		return $( selector );
	}

	/**
	 * 指定されたセレクタの子要素について以下の処理を行う.
	 * ダイアログ設定
	 * 
	 * @param {Object} selector JQueryセレクタ
	 */
	function _setComponentPsw( selector ) {

		// ダイアログ
		var $dialog = $( selector );

		$dialog.data( "keyboard", false );			// ESC キーによるクローズを禁止
		$dialog.data( "backdrop", "static" );		// 余白のクリックで閉じない
		$dialog.data( _dialogOptionName, null );
	}

	/**
	 * ダイアログの OK ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function _dialogBtn1Handler( event ) {

		var $dialog = $( event.target ).closest( data.component.dialog.selector );

		var close = true;

		if ( $dialog.data( _dialogOptionName ) && $dialog.data( _dialogOptionName ).btn1 &&
			_util.isFunction( $dialog.data( _dialogOptionName ).btn1 ) ) {

			close = $dialog.data( _dialogOptionName ).btn1( event );
		}

		if ( close && _dialogIsOpenPsw($dialog) ) {

			$dialog.modal( "hide" );
		}
	}

	/**
	 * ダイアログの キャンセル ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function _dialogBtn2Handler( event ) {

		var $dialog = $( event.target ).closest( data.component.dialog.selector );

		var close = true;

		if ( $dialog.data( _dialogOptionName ) && $dialog.data( _dialogOptionName ).btn2 &&
			_util.isFunction( $dialog.data( _dialogOptionName ).btn2 ) ) {

			close = $dialog.data( _dialogOptionName ).btn2( event );
		}

		if ( close && _dialogIsOpenPsw($dialog) ) {

			$dialog.modal( "hide" );
		}
	}

	/**
	 * 認証ダイアログの入力フォームにフォーカスをセット.
	 */
	function _authDialogFocusInput() {

		$( data.component.inputAuthPassword.selector ).focus();
	}

	/**
	 * エラー表示.
	 * 複数メッセージが指定された場合は改行で区切って表示する.
	 *
	 * @param {String|String[]} message 表示するエラーメッセージ
	 */
	function _dispError( message ) {

		if ( _util.isArray( message ) ) {

			if ( message.length < 1 ) return; // 空の配列
		}
		else {

			if ( _util.isEmpty( message ) ) return; // 空のメッセージ
			message = [ message ];
		}

		var msg = "";

		for ( var i = 0 ; i < message.length ; i++ ) {

			if ( i !== 0 ) msg += "\n";
			msg += message[i];
		}

		_view.errorDialog( msg );
	}

}); // end of ready-handler
