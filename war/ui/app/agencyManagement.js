"use strict";

/*
 * 代理店管理画面
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

			searchMsgParent : {

				selector : "#searchMsgParent",
			},

			searchMsg : {

				selector : "#searchMsg",
			},

			searchKey : {			// ----- 検索キー

				selector : "#inputSearch",
			},

			search : {				// ----- 検索ボタン

				selector : "#search",
				handler : [
					[ "click", search ]
				]
			},

			dispRegist : {			// ----- 新規登録ボタン

				selector : "#dispRegist",
				handler : [
					[ "click", dispRegist ]
				]
			},

			agencyParent : {		// ----- 代理店一覧

				selector : "#main-table tbody",	// <tbody>
			},

			agencyList : {		// ----- 代理店リスト

				selector : ".ccs-agency-management",		// 全ての行 <tr>：一覧コンテナからの相対
			},

			listId : {

				selector : ".ccs-agency-management-listId",		// ID <td>：一覧コンテナからの相対
			},

			editParent : {	// ----- 編集エリア

				selector : "#editParent",
			},

			dispNo : {		// ----- 表示番号(ID)

				selector : "#dispNo"
			},

			dispModeNameRegist : {		// ----- 新規登録

				selector : "#dispModeNameRegist"
			},

			dispModeNameEdit : {		// ----- 編集

				selector : "#dispModeNameEdit"
			},

			agencyCompanyId : {		// ----- 代理企業ID

				selector : "#inputAgencyCompanyId",

			},

			regist : {		// ----- 登録ボタン

				selector : "#regist",
				handler : [
					[ "click", regist ]
				],
			},

			del : {		// ----- 削除ボタン

				selector : "#delete",
				handler : [
					[ "click", del ]
				],
			},

			//-- 代理店一覧のソート
			sortId : {

				selector : "#sortId",
				handler : [
					[ "click", sort ]
				],
			},

			sortAgencyCompanyId : {

				selector : "#sortAgencyCompanyId",
				handler : [
					[ "click", sort ]
				],
			},

			sortTime : {

				selector : "#sortTime",
				handler : [
					[ "click", sort ]
				],
			}
			//--

		}, // end of component

		lastSelectedSortItem : null,	// 最後に選択されたソート項目
		maxResult : 300,	// 代理店一覧の最大表示件数
		agency : {}	// 編集中の代理店情報

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

	// ----- 代理店 click

	data.component.agencyParent.$element.on( "click", data.component.agencyList.selector, edit );


	// -------------------------------------------------------------------------
	// 初期表示処理
	// -------------------------------------------------------------------------

	init();

	/**
	 * 初期表示処理.
	 */
	function init() {

		_initRegist();

		data.component.sortTime.$element.click();	// 代理店一覧取得
	}


	// -------------------------------------------------------------------------
	// 共通の表示制御
	// -------------------------------------------------------------------------

	/**
	 * 編集エリア非活性化.
	 */
	function _disableForm() {

		data.component.editParent.$element.find( "form *").prop( "disabled", true );

	}

	/**
	 * 編集エリア活性化.
	 */
	function _enableForm() {

		data.component.editParent.$element.find( "form *").prop( "disabled", false );

	}

	/**
	 * 編集エリア 登録表示.
	 */
	function _initRegist() {

		// モード名 登録表示
		data.component.dispModeNameRegist.$element.prop( "hidden", false );
		data.component.dispModeNameEdit.$element.prop( "hidden", true );

		// 代理企業ID
		data.component.agencyCompanyId.$element.prop( "readonly", false );

		// 登録ボタン表示、更新/削除ボタン非表示
		data.component.regist.$element.prop( "hidden", false );
		data.component.del.$element.prop( "hidden", true );

		_clearForm();

		_enableForm();

	}

	/**
	 * 編集エリア 編集表示.
	 */
	function _initEdit() {

		// モード名 編集表示
		data.component.dispModeNameRegist.$element.prop( "hidden", true );
		data.component.dispModeNameEdit.$element.prop( "hidden", false );

		// 代理企業ID
		data.component.agencyCompanyId.$element.prop( "readonly", true );

		// 登録ボタン非表示、更新/削除ボタン表示
		data.component.regist.$element.prop( "hidden", true );
		data.component.del.$element.prop( "hidden", false );

		_enableForm();

	}

	/**
	 * 代理店一覧の選択解除.
	 */
	function _clearSelect() {

		data.component.agencyParent.$element.find( data.component.agencyList.selector ).removeClass( "bg-primary" );
	}

	/**
	 * 代理店一覧の指定行を選択状態にする.
	 *
	 * @param {Element|jQuery} target 選択状態にする <tr> 要素
	 */
	function _applySelect( target ) {

		$( target ).addClass( "bg-primary" );
	}

	// -------------------------------------------------------------------------
	// イベントハンドラ：代理店一覧
	// -------------------------------------------------------------------------

	// 新規登録表示
	function dispRegist( event ) {

		_clearSelect();

		_initRegist();

	}

	/**
	 * フォーム入力内容から代理店更新 API への送信オブジェクトを作成する.
	 *
	 * @param asc true = 昇順, false = 降順
	 * @return {Object} 代理店更新 API への送信オブジェクト
	 */
	function _createSortForm(id, asc) {

		var input = data.component.searchKey.$element.val();

		var sortElementName;
		switch (id) {
		case "sortId":
			sortElementName = "agency.agencyId";
			break;

		case "sortAgencyCompanyId":
			sortElementName = "agency.agencyCompanyId";
			break;

		case "sortTime":
			sortElementName = "agency.updateDate";
			break;
		}

		var form = { searchForm : {
			agency : {
				agencyCompanyId : input,
				agencyCompanyIdOption : "3"	// タイトル部分一致
			},
			sortForm : {
				maxResult : data.maxResult,
				sortElement : [
					{
						"name" : sortElementName,
						"asc" : asc
					}
				]
			}
		}};

		return form;
	}


	/**
	 * ソート方向を取得.
	 *
	 * @return ソート方向のboolean
	 */
	function _getOrder( event ) {

		var $target = $( event.target );

		if ( _util.isEmpty( $target ) ) return false;

		data.lastSelectedSortItem = $target;

		$target.closest(":button").blur(); // クリック時の青枠対策のためフォーカス外す

		if ( $target.hasClass("fa-sort-desc") ) {

			return true;

		} else {

			return false;
		}

	}

	/**
	 * ▼の状態を反転する.
	 * ※HTML側の「fa-sort～」クラス属性は「fa」とセットで使用すること
	 *
	 */
	function _flipOrder() {

		var $target = data.lastSelectedSortItem;
		data.lastSelectedSortItem = null;

		if ( _util.isEmpty( $target ) ) {
			// ソート項目クリア
			$("#t1").find('i')
			.removeClass("fa-sort-asc fa-sort-desc")
			.addClass("fa-sort");

			return;
		}

		// ソート項目クリア(選択項目以外)
		$("#t1").find('i').not( $target )
		.removeClass("fa-sort-asc fa-sort-desc")
		.addClass("fa-sort");

		if ( $target.hasClass("fa-sort") ) {

			$target
			.removeClass("fa-sort fa-sort-asc")
			.addClass("fa-sort-desc");

		} else {

			if ( $target.hasClass("fa-sort-desc") ) {

				$target
				.removeClass("fa-sort fa-sort-desc")
				.addClass("fa-sort-asc");

			} else if ($target.hasClass("fa-sort-asc") ) {

				$target
				.removeClass("fa-sort fa-sort-asc")
				.addClass("fa-sort-desc");
			}
		}
	}

	/**
	 * ソート.
	 *
	 * @param {Event} event イベント
	 */
	function sort(event) {

		var asc = _getOrder(event);

		var id = event.target.id;
		var form = _createSortForm(id, asc);

		var url = _prop.getApiMap("agency.search");
		var json = JSON.stringify(form);

		var option = {
			handleError : _searchError,
			handleSuccess : _searchSuccess
		};

		_api.postJSON(url, json, option);
	}
	// -------------------------------------------------------------------------

	/**
	 * 検索ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function search( event ) {

		data.component.sortTime.$element
		.removeClass("fa-sort-asc fa-sort-desc")
		.addClass("fa-sort")
		.click(); // 一覧取得（更新日時降順）

	}

	/**
	 * 再検索
	 *
	 */
	function _searchRe() {

		var sortId = "sortTime";
		var asc = false;

		var $target = $("#t1").find("i.fa-sort-asc");
		if (_util.isNotEmpty( $target[0] )) {

			sortId = $target[0].id;
			asc = true;

		} else {

			$target = $("#t1").find("i.fa-sort-desc");
			if (_util.isNotEmpty( $target[0] )) {
				sortId = $target[0].id;
			}
		}

		var form = _createSortForm(sortId, asc);

		var url = _prop.getApiMap( "agency.search" );

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

		data.lastSelectedSortItem = null;

		var msgList = [ _prop.getMessage( "agencyManagement.error.listError" ) ];

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

			_searchError( xhr, status, null, option );
			return;
		}

		// ----- 正常終了

		data.component.searchMsg.$element.text( "" );

		// 件数オーバーしている場合はメッセージを表示

		if ( response.resultList[0].code === _prop.getProperty( "common.apiResponse.overflow" ) ) {

			var max = _util.isNotEmpty( response.resultList[0].detailList ) ?
				response.resultList[0].detailList[0] : data.maxResult;

			data.component.searchMsg.$element.text(
				_util.format( _prop.getMessage( "agencyManagement.overflow" ), max ) );

			data.component.searchMsgParent.$element.prop('hidden', false);

		} else {

			data.component.searchMsgParent.$element.prop('hidden', true);

		}

		// 一覧表示

		if ( _util.isNotEmpty( data.lastSelectedSortItem ) ) {

			_flipOrder(); // ソート項目表示変更

		}

		var $parent = data.component.agencyParent.$element;

		$parent.empty();

		$.each( response.searchResultList, function ( i, val ) {

			var agency = val.agency;

			var id = agency.agencyId;

			var agencyCompanyId = agency.agencyCompanyId;

			var date = agency.updateDate.substr( 0, 19 );


			var $tr = $( "<tr></tr>" )
				.addClass( "ccs-agency-management" );

			var $id = $( "<th></th>" )
				.addClass( "ccs-agency-management-listId" )
				.attr( "data-ccs-id", id )
				.text( id )
				.appendTo( $tr );

			var $agencyCompanyId = $( "<td></td>" )
				.addClass( "ccs-agency-listAgencyCompanyId" )
				.text( agencyCompanyId )
				.appendTo( $tr );

			var $date = $( "<td></td>" ).text( date ).appendTo( $tr );


			$tr.appendTo( $parent );
		});

		$( "#t1 tbody" ).empty().append( $parent.children().clone() );

		_initRegist();

	}

	// -------------------------------------------------------------------------

	/**
	 * 一覧上の代理店選択処理：代理店内容取得／表示
	 *
	 * @param {Event} event イベント
	 */
	function edit( event ) {

		var id = $( event.target ).closest( data.component.agencyList.selector )
					.find( data.component.listId.selector ).attr( "data-ccs-id" );


		if ( _util.isEmpty( id ) ) return false;

		// ----- 編集エリアをクリア＆非活化

		_disableForm();
		_clearSelect();
		_applySelect( $( event.target ).closest( "tr" ) );

		data.component.editParent.$element.prop( "disabled", true );
		data.agency = {};

		// ----- API 呼び出し

		var form = { editForm : { agency : {

			agencyId : id,

		} } };

		var url = _prop.getApiMap( "agency.get" );
		var json = JSON.stringify( form );

		var option = {

			handleError : _getError,
			handleSuccess : _getSuccess
		};

		_api.postJSON( url, json, option );
	}

	/**
	 * 代理店内容取得 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _getError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "agencyManagement.error.getError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
		_clearSelect();
		_initRegist();
	}

	/**
	 * 代理店内容取得 API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _getSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_getError( xhr, status, null, option );
			return;
		}

		// 正常終了

		data.agency = response.editResult.agency;

		var agency = response.editResult.agency;


		data.component.dispNo.$element.text( "#" + agency.agencyId );

		data.component.agencyCompanyId.$element.val( agency.agencyCompanyId );

		_initEdit();

	}

	// -------------------------------------------------------------------------
	// イベントハンドラ：登録
	// -------------------------------------------------------------------------

	/**
	 * 登録ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function regist( event ) {

		_view.confirmDialog( _prop.getMessage( "common.confirm.regist" ), { ok : _registOk } );
	}

	/**
	 * 登録確認ダイアログの OK ボタン押下処理
	 *
	 * @param {Event} event イベント
	 */
	function _registOk( event ) {

		// 入力値取得

		var form = _createRegistForm();

		// API 送信

		var url = _prop.getApiMap( "agency.put" );
		var json = JSON.stringify( form );
		var option = {

			handleError : _registError,
			handleSuccess : _registSuccess
		};

		_api.postJSONSync( url, json, option );

		return false;
	}

	/**
	 * フォーム入力内容から代理店登録 API への送信オブジェクトを作成する.
	 *
	 * @return {Object} 代理店登録 API への送信オブジェクト
	 */
	function _createRegistForm() {

		var objAgency = {};

		objAgency.agencyCompanyId = data.component.agencyCompanyId.$element.val();

		var ret = { editForm : {

				agency : objAgency
			}
		};

		return ret;
	}

	/**
	 * 代理店登録 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _registError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "common.error.regist" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
	}

	/**
	 * 代理店登録 API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _registSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_registError( xhr, status, null, option );
			return;
		}

		// 正常終了

		_view.infoDialog( _prop.getMessage( "common.complete.regist" ) );

		_disableForm();
		_clearForm();
		_clearSelect();

		// 再検索
		_searchRe();
	}

	// -------------------------------------------------------------------------
	// イベントハンドラ：削除
	// -------------------------------------------------------------------------

	/**
	 * 削除ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function del( event ) {

		_view.confirmDialog( _prop.getMessage( "common.confirm.del" ), { ok : _delOk } );
	}

	/**
	 * 削除確認ダイアログの OK ボタン押下処理
	 *
	 * @param {Event} event イベント
	 */
	function _delOk( event ) {

		var objAgency = data.agency;
		var id = objAgency.agencyId;

		if ( _util.isEmpty( id ) ) return false;

		var form = {
				"bulkFormList" : [ {
					"agency" : {
						"agencyId" : id
					}
				} ]
			};


		// API 送信
		var url = _prop.getApiMap( "agency.del" );

		var json = JSON.stringify( form );
		var option = {

			handleError : _delError,
			handleSuccess : _delSuccess
		};

		_api.postJSONSync( url, json, option );

		return false;
	}


	/**
	 * 代理店削除 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _delError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "common.error.del" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
	}

	/**
	 * 代理店削除 API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _delSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_delError( xhr, status, null, option );
			return;
		}

		// 正常終了

		if ( response.resultList[0].code === _prop.getProperty( "common.apiResponse.success" ) ) {

			_view.infoDialog( _prop.getMessage( "common.complete.del" ) );

			_disableForm();
			_clearForm();
			_clearSelect();

			// 再検索
			_searchRe();

			return;
		}

		// 部分エラーの場合はエラーメッセージを表示

		_delError( xhr, status, null, option );

	}


	// -------------------------------------------------------------------------

	/**
	 * フォームクリア.
	 */
	function _clearForm() {

		data.component.editParent.$element.find( "form" )[0].reset();

		data.component.dispNo.$element.text( _prop.getMessage( "agencyManagement.noSelect" ) );

	}

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
