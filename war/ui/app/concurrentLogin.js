"use strict";

/*
 * 多重ログインエラー画面
 */

$( function () {

	var _util = CCS.util;
	var _prop = CCS.prop;
	var _api  = CCS.api;
	var _view = CCS.view;

	var data = {

		// 画面構成要素の定義
		// 初期処理で各要素の jQuery オブジェクトを $element として追加する

		component : {

			back : {		// ----- 強制ログインボタン

				selector : "#forceLogin",
				handler : [
					[ "click", forceLogin ]
				],
			},
		}, // end of component

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

	// ダイアログのボタンを効くようにする
	_view.setComponent( $( "body" ) );

	// -------------------------------------------------------------------------

	/**
	 * 強制ログインボタン押下処理
	 *
	 * @param {Event} event イベント
	 */
	function forceLogin( event ) {
		_view.confirmDialog( _prop.getMessage( "concurrentLogin.confirm" ), { ok : _forceLoginOk } );
	}

	/**
	 * 強制ログイン確認ダイアログの OK ボタン押下処理
	 *
	 * @param {Event} event イベント
	 */
	function _forceLoginOk( event ) {

		// API 送信

		var url = _prop.getApiMap( "login.forceLogin" );
		var option = {

			handleError : _forceLoginError,
			handleSuccess : _forceLoginSuccess
		};

		_api.postJSONSync( url, null, option );

		return false;
	}

	// -------------------------------------------------------------------------


	/**
	 * 強制ログイン API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _forceLoginError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "concurrentLogin.error.forceLoginError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
	}

	/**
	 * 強制ログイン API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _forceLoginSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_forceLoginError( xhr, status, null, option );
			return;
		}

		// 正常終了

		location.href = _prop.getRedirectMap( "top.url" );
	}


	// -------------------------------------------------------------------------
	// 共通処理
	// -------------------------------------------------------------------------

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