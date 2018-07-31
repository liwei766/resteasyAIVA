"use strict";

/*
 * 企業管理画面
 */

$( function () {

	var _auth = CCS.auth;
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

			companyParent : {		// ----- 企業一覧

				selector : "#main-table tbody",	// <tbody>
			},

			companyList : {		// ----- 企業スリスト

				selector : ".ccs-company-management",		// 全ての行 <tr>：一覧コンテナからの相対
			},

			listId : {

				selector : ".ccs-company-management-listId",		// ID <td>：一覧コンテナからの相対
			},

			listCompanyName : {

				selector : ".ccs-company-management-listCompanyName",	// 企業名 <td>：一覧コンテナからの相対
			},

			//-- 企業一覧のソート

			sortCompanyId : {

				selector : "#sortCompanyId",
				handler : [
					[ "click", sort ]
				],
			},

			sortCompanyName : {

				selector : "#sortCompanyName",
				handler : [
					[ "click", sort ]
				],
			},

			sortEnergyThreshold : {

				selector : "#sortEnergyThreshold",
				handler : [
					[ "click", sort ]
				],
			},

			sortSaveVoice : {

				selector : "#sortSaveVoice",
				handler : [
					[ "click", sort ]
				],
			},

			sortTime : {

				selector : "#sortTime",
				handler : [
					[ "click", sort ]
				],
			},

			// ----- 編集エリア

			editParent : {	// ----- 編集エリア

				selector : "#editParent",
			},

			dispNo : {		// ----- 表示番号(企業ID)

				selector : "#dispNo"
			},

			dispModeNameRegist : {		// ----- 新規登録

				selector : "#dispModeNameRegist"
			},

			dispModeNameEdit : {		// ----- 編集

				selector : "#dispModeNameEdit"
			},

			companyId : {		// ----- 企業ID

				selector : "#inputCompanyId",

			},

			companyName : {		// ----- 企業名

				selector : "#inputCompanyName",

			},

			energyThreshold : {		// ----- 音声判断レベルしきい値

				selector : "#inputEnergyThreshold",

			},

			saveVoice : {		// ----- 音声保存設定

				selector : "#inputSaveVoice",

			},

			permitIpAddress : {		// ----- 接続元制限IPアドレス

				selector : "#inputPermitIpAddress",

			},

			regist : {		// ----- 登録ボタン

				selector : "#regist",
				handler : [
					[ "click", regist ]
				],
			},

			update : {		// ----- 更新ボタン

				selector : "#update",
				handler : [
					[ "click", update ]
				],
			},

			del : {		// ----- 削除ボタン

				selector : "#delete",
				handler : [
					[ "click", del ]
				],
			}

		}, // end of component

		lastSelectedSortItem : null,	// 最後に選択されたソート項目
		maxResult : 300,	// 企業一覧の最大表示件数
		company : {},	// 編集中の企業情報
		registFlg : true,

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

	// ----- 企業 click

	data.component.companyParent.$element.on( "click", data.component.companyList.selector, edit );


	// -------------------------------------------------------------------------
	// 初期表示処理
	// -------------------------------------------------------------------------

	init();

	/**
	 * 初期表示処理.
	 */
	function init() {

		if ( _auth.authCheck( "ROLE_SYS_ADMIN" ) ) {

			data.registFlg = false;

			data.component.dispRegist.$element.closest("span").prop( "hidden", true );
			data.component.dispRegist.$element.closest(".input-group").addClass( "w-75" );

			_clearForm();

			_disableForm();

			_initEdit();

		} else {

			_initRegist();

		}

		data.component.sortTime.$element.click();	// 企業一覧取得
	}


	// -------------------------------------------------------------------------
	// 共通の表示制御
	// -------------------------------------------------------------------------

	/**
	 * フォームクリア.
	 */
	function _clearForm() {

		data.component.editParent.$element.find( "form" )[0].reset();

		data.component.dispNo.$element.text( _prop.getMessage( "companyManagement.noSelect" ) );

	}

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

		// 登録ボタン表示、更新/削除ボタン非表示
		data.component.regist.$element.prop( "hidden", false );
		data.component.update.$element.prop( "hidden", true );
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

		// 登録ボタン非表示、更新/削除ボタン表示
		data.component.regist.$element.prop( "hidden", true );
		data.component.update.$element.prop( "hidden", false );
		data.component.del.$element.prop( "hidden", false );

	}

	/**
	 * 企業一覧の選択解除.
	 */
	function _clearSelect() {

		data.component.companyParent.$element.find( data.component.companyList.selector ).removeClass( "bg-primary" );
	}

	/**
	 * 企業一覧の指定行を選択状態にする.
	 *
	 * @param {Element|jQuery} target 選択状態にする <tr> 要素
	 */
	function _applySelect( target ) {

		$( target ).addClass( "bg-primary" );
	}

	// -------------------------------------------------------------------------
	// イベントハンドラ：企業一覧
	// -------------------------------------------------------------------------

	// 新規登録
	function dispRegist( event ) {

		_clearSelect();

		_initRegist();

	}

	/**
	 * フォーム入力内容から企業更新 API への送信オブジェクトを作成する.
	 *
	 * @param asc true = 昇順, false = 降順
	 * @return {Object} 企業更新 API への送信オブジェクト
	 */
	function _createSortForm(id, asc) {

		var input = data.component.searchKey.$element.val();

		var sortElementName;
		switch (id) {
		case "sortCompanyId":
			sortElementName = "companyManagement.companyId";
			break;
		case "sortCompanyName":
			sortElementName = "companyManagement.companyName";
			break;
		case "sortEnergyThreshold":
			sortElementName = "companyManagement.energyThreshold";
			break;
		case "sortSaveVoice":
			sortElementName = "companyManagement.saveVoice";
			break;
		case "sortTime":
			sortElementName = "companyManagement.updateDate";
			break;
		}

		var form = { searchForm : {
			companyManagement : {
				companyName : input,
				companyNameOption : "3"	// タイトル部分一致
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

		var url = _prop.getApiMap("companyManagement.search");

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

		var url = _prop.getApiMap( "companyManagement.search" );

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

		var msgList = [ _prop.getMessage( "companyManagement.error.listError" ) ];

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
				_util.format( _prop.getMessage( "companyManagement.overflow" ), max ) );

			data.component.searchMsgParent.$element.prop('hidden', false);

		} else {

			data.component.searchMsgParent.$element.prop('hidden', true);

		}

		// 一覧表示

		if ( _util.isNotEmpty( data.lastSelectedSortItem ) ) {

			_flipOrder(); // ソート項目表示変更

		}

		var $parent = data.component.companyParent.$element;

		$parent.empty();

		$.each( response.searchResultList, function ( i, val ) {

			var company = val.companyManagement;

			var id = company.companyManagementId;

			var companyId = company.companyId;

			var companyName = company.companyName;

			var energyThreshold = company.energyThreshold;

			var saveVoice = company.saveVoice;

			var recaiusLicenseId = company.recaiusLicenseId;

			var recaiusModelId = company.recaiusModelId;

			var date = company.updateDate.substr( 0, 19 );

			var $tr = $( "<tr></tr>" )
				.addClass( "ccs-company-management" );

			var $companyId = $( "<th></th>" )
				.addClass( "ccs-company-management-listId" )
				.attr( "data-ccs-id", id )
				.text( companyId )
				.appendTo( $tr );

			var $companyName = $( "<td></td>" )
				.addClass( "ccs-company-management-listCompanyName" )
				.text( companyName )
				.appendTo( $tr );

			var $energyThreshold = $( "<td></td>" ).text( energyThreshold ).appendTo( $tr );

			var saveVoiceMark = "";
			if ( saveVoice ) {
				saveVoiceMark = "○";
			}
			var $saveVoice = $( "<td></td>" ).text( saveVoiceMark ).appendTo( $tr );

			var $date = $( "<td></td>" ).text( date ).appendTo( $tr );

			$tr.appendTo( $parent );
		});

		$( "#t1 tbody" ).empty().append( $parent.children().clone() );

		if ( data.registFlg ) {

			_initRegist();

		} else {

			_clearForm();

			_disableForm();

			_initEdit();

		}

	}

	// -------------------------------------------------------------------------

	/**
	 * 一覧上の企業選択処理：企業内容取得／表示
	 *
	 * @param {Event} event イベント
	 */
	function edit( event ) {

		var id = $( event.target ).closest( data.component.companyList.selector )
					.find( data.component.listId.selector ).attr( "data-ccs-id" );


		if ( _util.isEmpty( id ) ) return false;

		// ----- 編集エリアをクリア＆非活化

		_disableForm();
		_clearSelect();
		_applySelect( $( event.target ).closest( "tr" ) );

		data.component.editParent.$element.prop( "disabled", true );
		data.company = {};

		// ----- API 呼び出し

		var form = { editForm : { companyManagement : {

			companyManagementId : id,

		} } };

		var url = _prop.getApiMap( "companyManagement.get" );

		var json = JSON.stringify( form );

		var option = {

			handleError : _getError,
			handleSuccess : _getSuccess
		};

		_api.postJSON( url, json, option );
	}

	/**
	 * 企業内容取得 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _getError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "companyManagement.error.getError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
		_clearSelect();

		if ( data.registFlg ) {

			_initRegist();

		} else {

			_clearForm();

			_disableForm();

			_initEdit();

		}

	}

	/**
	 * 企業内容取得 API 正常終了処理.
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

		// 企業URL取得APIへ
		data.company = response.editResult.companyManagement;

		var company = response.editResult.companyManagement;

		data.component.dispNo.$element.text( "#" + company.companyId );
		data.component.companyId.$element.val( company.companyId );
		data.component.companyName.$element.val( company.companyName );
		data.component.energyThreshold.$element.val( company.energyThreshold );

		if ( company.saveVoice) {

			$( data.component.saveVoice.selector ).prop("checked", true);

		} else {

			$( data.component.saveVoice.selector ).prop("checked", false);
		}

		data.component.permitIpAddress.$element.val( company.permitIpAddress );

		_enableForm();

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

		if ( !data.registFlg ) {
			return false;
		}

		_view.confirmDialog( _prop.getMessage( "common.confirm.regist" ), { ok : _registOk } );
	}

	/**
	 * 登録確認ダイアログの OK ボタン押下処理
	 *
	 * @param {Event} event イベント
	 */
	function _registOk( event ) {

		// 音声判断レベルの入力値チェック
		var energyThreshold = data.component.energyThreshold.$element.val();
		if ( _util.isNotEmpty(energyThreshold) && !energyThreshold.match(/^[0-9]+$/)) {

			var msgList = [ _prop.getMessage( "common.error.regist" ),
							"(" + _prop.getValidatorMessage("common.digits") + ")"
							];

			_dispError( msgList );

			return false;
		}

		// 入力値取得

		var form = _createRegistForm();

		// API 送信

		var url = _prop.getApiMap( "companyManagement.put" );
		var json = JSON.stringify( form );
		var option = {

			handleError : _registError,
			handleSuccess : _registSuccess
		};

		_api.postJSONSync( url, json, option );

		return false;
	}

	/**
	 * フォーム入力内容から企業登録 API への送信オブジェクトを作成する.
	 *
	 * @return {Object} 企業登録 API への送信オブジェクト
	 */
	function _createRegistForm() {

		var company = {};

		company.companyId = data.component.companyId.$element.val();
		company.companyName = data.component.companyName.$element.val();
		company.energyThreshold = data.component.energyThreshold.$element.val();

		if ( data.component.saveVoice.$element.prop( "checked" ) ) {
			
			company.saveVoice = true;
			
		} else {
			
			company.saveVoice = false;
		}

		company.permitIpAddress = data.component.permitIpAddress.$element.val();

		var ret = { editForm : {

				companyManagement : company
			}
		};


		return ret;
	}

	/**
	 * 企業登録 API エラー処理.
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
	 * 企業登録 API 正常終了処理.
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
	 * 更新確認ダイアログの OK ボタン押下処理
	 *
	 * @param {Event} event イベント
	 */
	function _updateOk( event ) {

		// 音声判断レベルの入力値チェック
		var energyThreshold = data.component.energyThreshold.$element.val();
		if ( _util.isNotEmpty(energyThreshold) && !energyThreshold.match(/^[0-9]+$/)) {

			var msgList = [ _prop.getMessage( "common.error.update" ),
							"(" + _prop.getValidatorMessage("common.digits") + ")"
							];

			_dispError( msgList );

			return false;
		}

		// 入力値取得

		var form = _createUpdateForm();

		// API 送信

		var url = _prop.getApiMap( "companyManagement.update" );
		var json = JSON.stringify( form );
		var option = {

			handleError : _updateError,
			handleSuccess : _updateSuccess
		};

		_api.postJSONSync( url, json, option );

		return false;
	}

	/**
	 * フォーム入力内容から企業更新 API への送信オブジェクトを作成する.
	 *
	 * @return {Object} 企業更新 API への送信オブジェクト
	 */
	function _createUpdateForm() {

		var company = data.company;

		company.companyId = data.component.companyId.$element.val();
		company.companyName = data.component.companyName.$element.val();
		company.energyThreshold = data.component.energyThreshold.$element.val();

		if ( data.component.saveVoice.$element.prop( "checked" ) ) {

			company.saveVoice = true;

		} else {

			company.saveVoice = false;
		}

		company.permitIpAddress = data.component.permitIpAddress.$element.val();

		var ret = { editForm : {

				companyManagement : company
			}
		};

		return ret;
	}

	/**
	 * 企業更新 API エラー処理.
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
	 * 企業更新 API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _updateSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_updateError( xhr, status, null, option );
			return;
		}

		// 正常終了

		_view.infoDialog( _prop.getMessage( "common.complete.update" ) );

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

		var company = data.company;
		var id = company.companyManagementId;

		if ( _util.isEmpty( id ) ) return false;

		var form = {
				"bulkFormList" : [ {
					"companyManagement" : {
						"companyManagementId" : id
					}
				} ]
			};


		// API 送信
		var url = _prop.getApiMap( "companyManagement.del" );

		var json = JSON.stringify( form );
		var option = {

			handleError : _delError,
			handleSuccess : _delSuccess
		};

		_api.postJSONSync( url, json, option );

		return false;
	}


	/**
	 * 企業削除 API エラー処理.
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
	 * 企業削除 API 正常終了処理.
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
