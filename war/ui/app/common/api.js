"use strict";

/**
 * API 呼び出しユーティリティ（CCS.api）
 */

( function( CCS ) {

	var _prop = CCS.prop;
	var _util = CCS.util;
	var _view = CCS.view;

	/**
	 * AJAX 呼び出し時に利用する option のデフォルト値
	 *
	 * @return {Object} option のデフォルト値
	 * @return {Object} ajaxOption $.ajax() に渡すオプション
	 *
	 * @return {boolean} ignoreErrorHandler true のとき、デフォルトのエラーハンドラを実行しない
	 * @return {Function} handleError エラーハンドラ
	 *
	 * @return {boolean} ignoreSuccessHandler true のとき、デフォルトのエラーハンドラを実行しない
	 * @return {Function} handleSuccess サクセスハンドラ
	 *
	 * @return {boolean} ignoreCompleteHandler true のとき、デフォルトのエラーハンドラを実行しない
	 * @return {Function} handleComplete コンプリートハンドラ
	 *
	 * @return {Object} result レスポンスの処理結果を格納する
	 * @return {boolean} result.ok 応答コードが正常終了系（＝00_00_xxx）のとき true
	 * @return {Object[]} result.msgList レスポンス．処理結果から作成したメッセージ情報
	 * @return {string} result.msgList[].code 結果コード（code）
	 * @return {string} result.msgList[].message メッセージ（message）※項目名マージ済み
	 * @return {string[]} result.msgList[].params 詳細（detailList）
	 * @return {Object[]} result.msgList[].sublist 一部エラーの場合、一括処理結果（bulkResultList）のメッセージリスト
	 * 	内部構成は result と同じ
	 */
	function _defaultOption() {

		var ret = {

			ajaxOption : {},

			ignoreErrorHandler : false,
			handleError : null,

			ignoreSuccessHandler : false,
			handleSuccess : null,

			ignoreCompleteHandler : false,
			handleComplete : null,

			/*
			 *	result の構成
			 *	{
			 *		ok : true | false,
			 *		msgList : [{
			 *			code : ,
			 *			message : ,
			 *			params : [],
			 *			sublist : [],
			 *		}]
			 *	}
			 */
			result : null,
		};

		return ret;
	}

	// -------------------------------------------------------------------------
	// 通信処理
	// -------------------------------------------------------------------------

	/**
	 * JSON 文字列を非同期 POST する.
	 *
	 * @param {string} url POST先URL
	 * @param {string} json 送信するJSON文字列
	 * @param {Object} [option] オプション（コールバック関数の context（＝this）としても使用）
	 */
	function _postJSON( url, json, option ) {

		option = $.extend( true, _defaultOption(), option || {}, { ajaxOption : { async : true } } );

		_callAjax( url, json, option );
	}

	/**
	 * JSON 文字列を同期 POST する.
	 *
	 * @param {string} url POST先URL
	 * @param {string} json 送信するJSON文字列
	 * @param {Object} [option] オプション（コールバック関数の context（＝this）としても使用）
	 */
	function _postJSONSync( url, json, option ) {

		option = $.extend( true, _defaultOption(), option || {}, { ajaxOption : { async : false } } );

		_callAjax( url, json, option );
	}

	/**
	 * JSON 文字列を POST する.
	 *
	 * @param {string} url POST先URL
	 * @param {string} json 送信するJSON文字列
	 * @param {Object} [option] オプション（コールバック関数の context（＝this）としても使用）
	 */
	function _callAjax( url, json, option ) {

		// オプション処理

		option.url = url;
		option.json = json;

		// AJAX 呼び出し

		var ajaxOption = {
			headers : {

				pragma : "no-cache",
				"Cache-Control" : "no-cache",
			},
			url : url,
			data: json,

			cache: false,
			type: "POST",
			contentType: "application/json",
			dataType: "json",

			success: _ajaxOk,
			error: _ajaxError,
			complete: _ajaxComplete,
			context : option // ハンドラの this は option
		};

		ajaxOption.headers[ _prop.getProperty( "common.extHeaderName.userInfo" ) ] = _util.getToken();

		ajaxOption = $.extend( ajaxOption, option.ajaxOption || {} ); // 指定されたオプションが優先
		_ajaxStart();
		$.ajax( ajaxOption );
	}

	// -------------------------------------------------------------------------
	// Ajax 開始処理
	// -------------------------------------------------------------------------

	/**
	 * 通信開始処理
	 */
	function _ajaxStart() {}

	// -------------------------------------------------------------------------
	// Ajax 終了処理
	// -------------------------------------------------------------------------

	//
	/**
	 * Ajax 完了時処理（Ajax Complete コールバック）
	 *
	 * @this {Object} option Ajax コール時に使用した option
	 *
	 * @param {jqXHR} xhr
	 * @param {string} status
	 *
	 * @see http://api.jquery.com/jQuery.ajax/
	 */
	function _ajaxComplete( xhr, status ) {

		var option = this;
		var cont = true; // 処理続行フラグ

		if ( ! option.ignoreCompleteHandler ) {
			cont = _handleComplete( xhr, status, option );
		}

		if ( cont ) {
			if ( _util.isFunction( option.handleComplete ) ) {
				option.handleComplete( xhr, status, option );
			}
		}
	}

	/**
	 * Ajax 完了ハンドラ
	 *
	 * @param {jqXHR} xhr
	 * @param {string} status
	 * @param {Object} option Ajax コール時に使用した option
	 *
	 * @return {boolean} この関数が false を返した時、呼び出し元が指定した完了ハンドラは呼ばれない
	 */
	function _handleComplete( xhr, status, option ) {

		return true; // NOOP
	}

	// -------------------------------------------------------------------------
	// Ajax エラー処理
	// -------------------------------------------------------------------------

	/**
	 * Ajax エラー処理（Ajax Error コールバック）
	 *
	 * @this {Object} option Ajax コール時に使用した option
	 *
	 * @param {jqXHR} xhr
	 * @param {string} status
	 * @param {string} errorThrown HTTP エラーごとのエラー文字列
	 *
	 * @see http://api.jquery.com/jQuery.ajax/
	 */
	function _ajaxError( xhr, status, errorThrown ) {

		var option = this;
		var cont = true; // 処理続行フラグ

		if ( ! option.ignoreErrorHandler ) {
			cont = _handleError( xhr, status, errorThrown, option );
		}

		if ( cont && _util.isFunction( option.handleError ) ) {
			option.handleError( xhr, status, errorThrown, option );
		}
	}

	/**
	 * Ajax エラーハンドラ
	 *
	 * @param {jqXHR} xhr
	 * @param {string} status
	 * @param {Object} option Ajax コール時に使用した option（option.result を設定する）
	 *
	 * @param {boolean} option.result.ok false を設定する
	 * @param {number} option.result.msgList[0].code HTTP 応答コード
	 * @param {string} option.result.msgList[0].message エラーメッセージ
	 * @param {array} option.result.msgList[0].params （空の配列）
	 *
	 * @return {boolean} この関数が false を返した時、呼び出し元が指定したエラーハンドラは呼ばれない
	 */
	function _handleError( xhr, status, errorThrown, option ) {

		var httpStatus = xhr.status;
		var errorPage = "";
		var code = "";

		// 遷移するエラー画面とエラーコードを指定する(ここではエラーコードはhttpステータス)
		switch ( httpStatus ) {

		case 200: // parser エラーなど
			errorPage = "systemError";
			code = _prop.getMessage( "common.systemerror.parseError" );
			break;

		case 403: // 認証エラー

			/*
				セッションタイムアウト／未認証状態の場合
				この分岐に遷移します.
				システムエラー画面に遷移させたくない場合は
				ここに処理を記述してください
				例）ログインしていないか不正なアクセスです。
			*/

			errorPage = "systemError";
			code = _prop.getMessage( "common.reload" ) + "(" +  httpStatus.toString() + ")";
			break;

		default:
			errorPage = "systemError";
			code = _util.format(
				_prop.getMessage( "common.systemerror.httpStatusError" ), [ httpStatus.toString() ] );
			break;
		}

		// 処理結果オブジェクト作成

		option.result = {

			ok : false,
			msgList : [{
				code : httpStatus,
				message : code,
				params : []
			}]
		};

		return true;
	}

	// -------------------------------------------------------------------------
	// Ajax 成功処理
	// -------------------------------------------------------------------------

	/**
	 * Ajax 正常終了処理（Ajax Success コールバック）
	 *
	 * @this {Object} option Ajax コール時に使用した option
	 *
	 * @param {Object} data レスポンス JSON をパースしたオブジェクト
	 * @param {string} status
	 * @param {jqXHR} xhr
	 *
	 * @see http://api.jquery.com/jQuery.ajax/
	 */
	function _ajaxOk( data, status, xhr ) {

		var option = this;
		var cont = true; // 処理続行フラグ

		if ( ! option.ignoreSuccessHandler ) {
			option.result = _handleSuccess( data, status, xhr, option );
		}

		if ( cont && _util.isFunction( option.handleSuccess ) ) {
			option.handleSuccess( data, status, xhr, option );
		}
	}

	/**
	 * Ajax 正常終了ハンドラ
	 *
	 * @param {Object} data レスポンス JSON をパースしたオブジェクト
	 * @param {string} status
	 * @param {jqXHR} xhr
	 * @param {Object} option Ajax コール時に使用した option
	 *
	 * @return {Object} option.result に設定するためのオブジェクト
	 *
	 * @return {boolean} option.result.ok エラーのとき false を設定する
	 * @return {Object[]} option.result.msgList 処理結果メッセージの配列（正常終了時は空）
	 * @return {number} option.result.msgList[].code API の処理結果コード
	 * @return {string} option.result.msgList[].message API の処理結果メッセージ（項目名マージ済み）
	 * @return {string[]} option.result.msgList[].params API の処理結果詳細（detailList）
	 *
	 * <ul>
	 * <li>option.result.ok === false → API でエラーと判定されている（msgList にエラーメッセージあり）</li>
	 * <li>
	 * option.result.ok === true
	 * 	<ul>
	 * 		<li>msgList が空 → 正常終了</li>
	 * 		<li>msgList にメッセージがある → 一部エラーなどの、ユーザへの通知事項がある</li>
	 * 	</ul>
	 * </li>
	 * </ul>
	 */
	function _handleSuccess( data, status, xhr, option ) {

		return _checkApiResult( data, status, xhr, option );
	}

	/**
	 * API からの返り値チェックと処理結果判定
	 * エラーコードに応じたエラーメッセージを設定する
	 *
	 * @param {Object} data APIからのレスポンス
	 * @param {string} status
	 * @param {jqXHR} xhr
	 * @param {Object} option Ajax コール時に使用した option（option.result を設定する）
	 *
	 * @return {Object} 処理結果（option.result に設定するためのオブジェクト）
	 *
	 * @return {boolean} option.result.ok エラーのとき false を設定する
	 * @return {Object[]} option.result.msgList 処理結果メッセージの配列（正常終了時は空）
	 * @return {number} option.result.msgList[].code API の処理結果コード
	 * @return {string} option.result.msgList[].message API の処理結果メッセージ（項目名マージ済み）
	 * @return {string[]} option.result.msgList[].params API の処理結果詳細（detailList）
	 */
	function _checkApiResult( data, status, xhr, option ) {

		var syserror = _prop.getProperty( "common.apiResponse.sysErrorPrefix" );
		var success = _prop.getProperty( "common.apiResponse.success" );
		var partial = _prop.getProperty( "common.apiResponse.partial" );
		var overflow = _prop.getProperty( "common.apiResponse.overflow" );

		var ret = { ok : true, msgList : [] };

		try {

			// null と undefined を除去
			var resultList = $.map( data.resultList, function( value ) { return value; } );

			$.each( resultList, function ( i, result ) {

				var msgObj;
				var code = result.code;

				if ( code !== success ) {	// 成功時はなにもしない

					msgObj = _createMsgObj( result );

					if ( code === partial ) {

						// 一部エラーの場合は一括処理結果も表示

						msgObj.sublist =
							$.map( data.bulkResultList, function( bulkResult ) {
								return _createMsgObj( bulkResult.resultList[0] );
							} );
					}
					else if ( code !== overflow ) {

						// success, partial, overflow のどれでもない場合は ok を false にする
						ret.ok = false;
					}

					ret.msgList.push( msgObj );
				}
			}); // end of "each"
		}
		catch ( ex ) {

			ret.ok = false;
			ret.msgList = [ {
				code : "-1",
				message : _prop.getMessage( "common.systemError.parseError" ),
				params : []
			} ];
		}

		// ---------- 判定結果に対応した処理を実行

		return ret;
	}

	/**
	 * エラーメッセージ作成.
	 *
	 * @param {Object} result サーバから受信した 1 件分の処理結果（resultList）
	 * @return {Object} 1 件分の処理結果（option.result.msgList）
	 */
	function _createMsgObj( result ) {

		var msgObj = {
			code : result.code,
			message : result.message,
			params : result.detailList
		};

		var nameCount = 0;
		var nameLabel;
		var addMessage = "";

		// 詳細情報が「#」で始まる値の場合、label から引き当てる

		$.each( result.detailList, function ( i, detail ){

			if ( _util.startsWith( detail, "#" ) ) {

				nameLabel =
					_prop.getLabel( _util.currentModuleName() + "." + detail.substring( 1 ) ) ||
					_prop.getLabel( detail.substring( 1 ) ) ||
					_prop.getLabel( "common." + detail.substring( 1 ) );

				if ( nameLabel ) {
					if ( nameCount === 0 ) addMessage += "(";
					else addMessage += ", ";
					addMessage += nameLabel;
					nameCount++;
				}

				result.detailList[i] = nameLabel || detail;
			}
		});

		if ( addMessage !== "" ) {
			addMessage += ")";
			msgObj.message += addMessage;
		}

		return msgObj;
	}

	/**
	 * ダウンロード
	 *
	 * @param {String} ダウンロードAPIのURL
	 */
	function _download( url ) {
//		// ダイアログが閉じていないとうまくいかない場合があるので0.5秒ぐらい待機してから実行する
// 		setTimeout(function() {
// 			// 見えないifrmeを生成する
//  			$('#download-frame').remove();
//  			$("<iframe/>").attr({
//  					src : url,
//  					style : "display : none;",
//  					id : "download-frame"}
//  			).appendTo($('body'));
//  		}, 500);

		// Microsoft Edge ブラウザ対応
    	let link = document.createElement('a');
		link.href = url;
		link.target = "_blank";
		link.click();
	}

	// -------------------------------------------------------------------------
	// 公開オブジェクト
	// -------------------------------------------------------------------------

	var ret = {

		name : "CCS.api",

		defaultOption : _defaultOption,

		/**
		 * JSON 文字列を非同期 POST
		 */
		postJSON : _postJSON,

		/**
		 * JSON 文字列を同期 POST
		 */
		postJSONSync : _postJSONSync,

		/**
		 * API からの返り値チェックとエラー表示
		 */
		checkApiResult : _checkApiResult,

		/**
		 * ファイルダウンロードを行う
		 */
		download : _download

	}; // end of ret

	CCS.api = ret;

})( CCS );
