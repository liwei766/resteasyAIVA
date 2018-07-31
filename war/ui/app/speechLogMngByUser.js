"use strict";

/*
 * 履歴管理画面
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

			// ---------- 音声解析履歴 一覧

			speechLogParent : {		// ----- 一覧

				selector : "#main-table tbody",	// <tbody>
			},

			logList : {		// ----- 音声解析履歴リスト

				selector : ".ccs-speechlog-management",		// 全ての行 <tr>：一覧コンテナからの相対
			},

			listId : {

				selector : ".ccs-speechlog-management-listId",		// ID <td>：一覧コンテナからの相対
			},

			logDate : {		// ----- 日付

				selector : "#inputDate",
				handler : [
					[ "change", search ]
				],
			},

			//--音声解析履歴一覧のソート
			sortNo : {		// ----- 解析番号

				selector : "#sortNo",
				handler : [
					[ "click", sort ]
				],
			},

			sortDate : {		// ----- 利用日時

				selector : "#sortDate",
				handler : [
					[ "click", sort ]
				],
			},

			sortSpeechType : {		// ----- 種別

				selector : "#sortSpeechType",
				handler : [
					[ "click", sort ]
				],
			},

			sortUserName : {		// ----- 利用者名

				selector : "#sortUserName",
			},


			// ---------- 音声解析履歴 詳細

			editLogArea : {			// ----- 音声解析履歴詳細エリア

				selector : ".ccs-edit-log-area",
			},

			dispNo : {		// ----- 表示番号(解析番号)

				selector : "#dispNo"
			},

			fileName : {

				selector : "#fileName"
			},

			log : {			// ----- 音声解析履歴内容

				selector : "#dispLog",
				handler : [
					[ "mousedown", dispLogScroll ],
					[ "wheel", dispLogScroll ]
				],
			},

			rennzoku : {		// ----- 連続再生ボタン

				selector : ".rennzoku",
				handler : [
					[ "click", rennzoku ]
				],
			},

			stop : {		// ----- 停止ボタン

				selector : ".stop",
				handler : [
					[ "click", stop ]
				],
			},

			detailLog : {		// ----- ログ詳細（文節）

				selector : ".detail-log"
			},

			play : {		// ----- 再生ボタン

				selector : ".play",
			},

			detailTxt : {		// ----- ログ詳細（文節）内容

				selector : ".detail-txt",
			},

			detailTxtInput : {		// ----- ログ詳細（文節）内容 入力フォーム

				selector : ".detail-txt > textarea",
			},

			update : {		// ----- 更新ボタン

				selector : "#update",
				handler : [
					[ "click", updateLog ]
				],
			},

			exportFile : {		// ----- 音声ファイル出力

				selector : "#exportFile",
				handler : [
					[ "click", _exportFile ]
				],
			},

			loading : {		// ----- 処理中表示

				selector : "#loading-bar-spinner",
			},

		}, // end of component

//		maxResult : 300,	// 一覧の最大表示件数

//		speechLogId : null,				// 音声解析履歴ID
		speechLog : {},					// 編集中の音声解析履歴情報
		logScrollManualUseFlg : false,	// 音声解析履歴スクロール手動利用中フラグ
		scDispPos : 0,					// 音声解析履歴スクロール表示位置
		editLogContentsFlg : false,		// 編集ログ内容有無フラグ

		// ログ音声再生関連
		saveVoice : false,		// 音声ファイル利用
		playFlg : false,		// 連続再生フラグ
		index : 0,				// 音声ファイル位置
		audios : [],			// 音声ファイル
		speechLogDetailId : null,
		preGetNum : 9,			// 先行読み込み数 （選択位置 + 先行読み込み数）個を読む
		voiceExistenceFlg : false,	// 音声ファイル有無フラグ
		fileId : null // 音声ファイルID

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

	// ----- ログ一覧の1行クリック
	data.component.speechLogParent.$element.on( "click", data.component.logList.selector, dispLog );

	// ----- 再生
	data.component.log.$element.on( "click", data.component.play.selector, onPlay);

	// ----- ログ文節クリック
	data.component.log.$element.on( "click", data.component.detailLog.selector, editTxt);

	// ----- ログテキストフォーム
	data.component.log.$element.on( "blur", data.component.detailTxtInput.selector, editTxtEnd);

	// -------------------------------------------------------------------------
	// 初期表示処理
	// -------------------------------------------------------------------------

	init();

	/**
	 * 初期表示処理.
	 */
	function init() {

		// 処理中表示
		$( data.component.loading.selector ).hide();

		_setSaveVoice();	// 音声ファイル利用設定セット

		_disableLogForm();	// 音声解析履歴詳細エリア非活性化

		// 音声解析履歴 日付選択 初期値
		var today = _getToday();
		data.component.logDate.$element.val( today );
		data.component.logDate.$element.change();
	}

	/**
	 * 今日の日付取得(yyyy-MM-dd形式 0埋め).
	 */
	function _getToday() {

		var now = new Date();

		var yyyy = now.getFullYear();
		var MM   = ( "0" + (now.getMonth() + 1) ).slice(-2);
		var dd   = ( "0" + now.getDate() ).slice(-2);

		var dateString = yyyy + "-" + MM + "-" + dd;

		return dateString;
	}

	/**
	 * 音声ファイル利用設定セット
	 */
	function _setSaveVoice() {

		var _user = CCS.user;
		data.saveVoice = _user.getData( _user.SAVE_VOICE );
	}


	// -------------------------------------------------------------------------
	// 共通の表示制御
	// -------------------------------------------------------------------------

	/**
	 * フォーム入力内容からログ検索2 API への送信オブジェクトを作成する.
	 *
	 * @param asc true = 昇順, false = 降順
	 * @return {Object} ナレッジ更新 API への送信オブジェクト
	 */
	function _createSortForm(id, asc) {

		var sortElementName;
		switch (id) {
		case "sortNo":
			sortElementName = "speechLogNo";
			break;
		case "sortSpeechType":
			sortElementName = "type";
			break;
		case "sortDate":
			sortElementName = "startDate";
			break;
		}


		var inputStartDateFrom = null;
		var inputStartDateTo   = null;

		var inputDate = data.component.logDate.$element.val();

		if ( _util.isNotEmpty( inputDate ) ) {

			inputStartDateFrom = inputDate + "T00:00:00.000";
			inputStartDateTo   = inputDate + "T23:59:59.999";
		}

		var form = { searchForm : {
			speechLog : {
				"userNameOption" : "3",	// タイトル部分一致
				"startDateFrom" : inputStartDateFrom,
				"startDateTo"   : inputStartDateTo,
			},
			sortForm : {
//				maxResult : data.maxResult,
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

		var url = _prop.getApiMap( "speechLog.searchByUser" );

		var json = JSON.stringify( form );

		var option = {

			handleError : _searchError,
			handleSuccess : _searchSuccess
		};

		_api.postJSON(url, json, option);
	}

	// -------------------------------------------------------------------------

	/**
	 * 音声解析履歴詳細エリア非活性化.
	 */
	function _disableLogForm() {

		_disableVoiceLogForm();

		data.component.editLogArea.$element.find( "form" ).prop( "disabled", true );
		data.component.update.$element.prop( "disabled", true ).addClass( "cursor-default" );
		data.component.exportFile.$element.prop( "disabled", true ).addClass( "cursor-default" );

		if ( ! data.saveVoice ) {
			data.component.rennzoku.$element.prop( "hidden", true );
			data.component.stop.$element.prop( "hidden", true );
			// 更新ボタン左寄せ対策
			// data.component.exportFile.$element.css("visibility", "hidden");
		}

	}

	/**
	 * 音声解析履歴詳細エリア活性化.
	 */
	function _enableLogForm() {

		if ( data.saveVoice && data.voiceExistenceFlg) {

			_enableVoiceLogForm();
		}

		data.component.editLogArea.$element.find( "form" ).prop( "disabled", false );

		if ( data.editLogContentsFlg ) {

			data.component.update.$element.prop( "disabled", false ).removeClass( "cursor-default" );
			data.component.exportFile.$element.prop( "disabled", false ).removeClass( "cursor-default" );
		}

	}

	/**
	 * 音声解析履歴詳細エリア 音声関連 非活性化.
	 */
	function _disableVoiceLogForm() {

		data.component.rennzoku.$element.prop( "disabled", true ).addClass( "cursor-default" );
		data.component.stop.$element.prop( "disabled", true ).addClass( "cursor-default" );
		//data.component.exportFile.$element.prop( "disabled", true ).addClass( "cursor-default" );
	}

	/**
	 * 音声解析履歴詳細エリア 音声関連 活性化.
	 */
	function _enableVoiceLogForm() {

		data.component.rennzoku.$element.prop( "disabled", false ).removeClass( "cursor-default" );
		data.component.stop.$element.prop( "disabled", false ).removeClass( "cursor-default" );
		//data.component.exportFile.$element.prop( "disabled", false ).removeClass( "cursor-default" );
	}

	/**
	 * ログ一覧の選択状態解除.
	 */
	function _clearSelect() {

		data.component.speechLogParent.$element.find( data.component.logList.selector ).removeClass( "bg-primary" );
	}

	/**
	 * ログ一覧の指定行を選択状態にする.
	 *
	 * @param {Element|jQuery} target 選択状態にする
	 */
	function _applySelect( target ) {

		$( target ).addClass( "bg-primary" );
	}

	/**
	 * ログ内容表示をクリア.
	 */
	function _clearLog() {

		data.component.dispNo.$element.text( _prop.getMessage( "speechLogManagement.noSelect" ) );
		data.component.fileName.$element.text( "" );
		$("#fileName").hide();

		data.component.log.$element.find( "form" ).empty();
		data.component.log.$element.prop('hidden', false);

	}

	// -------------------------------------------------------------------------
	// イベントハンドラ：音声解析履歴 一覧
	// -------------------------------------------------------------------------

	/**
	 * 検索ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function search( event ) {

		data.component.sortNo.$element
		.removeClass("fa-sort-asc fa-sort-desc")
		.addClass("fa-sort")
		.click(); // 一覧取得（問い合せ番号降順）
	}


	/**
	 * 音声解析検索 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _searchError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "speechLogManagement.error.listError" ) ];

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
//
//			var max = _util.isNotEmpty( response.resultList[0].detailList ) ?
//				response.resultList[0].detailList[0] : data.maxResult;
//
//			data.component.searchMsg.$element.text(
//				_util.format( _prop.getMessage( "speechLogManagement.overflow" ), max ) );
//
//			data.component.searchMsgParent.$element.prop('hidden', false);
//
		} else {

			data.component.searchMsgParent.$element.prop('hidden', true);

		}

		// 一覧表示

		if ( _util.isNotEmpty( data.lastSelectedSortItem ) ) {

			_flipOrder(); // ソート項目表示変更

		}

		var $parent = data.component.speechLogParent.$element;

		$parent.empty();

		if ( !_util.isEmpty( response.searchResultList ) ) {

			$.each( response.searchResultList, function ( i, val ) {

				let log  = val.speechLog;

				let id = log.speechLogId;
				let speechLogNo = log.speechLogNo; // 解析番号

				let speechType = "";
				switch (log.type) { // 種別
				case "0":
					speechType = "マイク";
					break;
				case "1":
					speechType = "ファイル";
					break;
				}

				let date = ''; // 利用日時

				if ( !_util.isEmpty( log.startDate ) ) {

					date = log.startDate.substr( 0, 19 );

				}

				let $tr = $( "<tr></tr>" )
					.addClass( "ccs-speechlog-management" );

				let $id = $( "<th></th>" )
						.addClass( "ccs-speechlog-management-listId" )
						.attr( "data-ccs-id", id )
						.text( speechLogNo ).appendTo( $tr );

				let $date = $( "<td></td>" ).text( speechType ).appendTo( $tr ); // 種別
				let $user = $( "<td></td>" ).text( date ).appendTo( $tr ); // 利用日時


				$tr.appendTo( $parent );
			});

		}

		$( "#t1 tbody" ).empty().append( $parent.children().clone() );

		_clearLog();

		_disableLogForm();

	}


	/**
	 * ログリスト選択（クリック）処理：ログ内容表示.
	 *
	 * @param {Event} event イベント
	 */
	function dispLog( event ) {

		var logId = $( event.target ).closest( data.component.logList.selector )
					.find( data.component.listId.selector ).attr( "data-ccs-id" );

		if ( _util.isEmpty( logId ) ) {

			_clearLog();
			_disableLogForm();

			return false;
		}

		_clearSelect();

		_applySelect( $( event.target ).closest( "tr" ) );

		_refreshLog( logId );
	}

	// -------------------------------------------------------------------------
	// イベントハンドラ：音声解析履歴 詳細
	// -------------------------------------------------------------------------

	/**
	 * 音声解析を取得して表示.
	 *
	 * @param {string|number} speechLogId 音声解析 ID
	 */
	function _refreshLog( speechLogId ) {

		if ( _util.isEmpty( speechLogId ) ) return; // 音声解析 ID 未指定なら何もしない

		_disableLogForm();

		data.speechLog = {};

		var form = { editForm : { speechLog : {

			speechLogId : speechLogId,

		} } };

		var url = _prop.getApiMap( "speechLog.get" );
		var json = JSON.stringify( form );

		var option = {

			handleError : _logError,
			handleSuccess : _logSuccess
		};

		_api.postJSON( url, json, option );
	}

	/**
	 * 音声解析取得 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _logError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "speechLogManagement.error.logError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
		_clearSelect();
		_clearLog();
		_disableLogForm();

	}

	/**
	 * 音声解析取得 API 正常終了処理：ログ表示エリアに音声解析内容を表示.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _logSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_logError( xhr, status, null, option );
			return;
		}

		// 正常終了

		data.speechLog = response.editResult.speechLog;

		data.component.dispNo.$element.text( _prop.getLabel( "speechLogManagement.speechLogNo" ) + " : " + response.editResult.speechLog.speechLogNo );

		if(response.editResult.speechLog.type === "1"){//種類は「ファイル」の場合
			data.component.fileName.$element.text( _prop.getLabel( "speechLogManagement.fileName" ) + " : " + getDisplayName(response.editResult.speechLog.fileName) );
			$('#fileName').prop('title',response.editResult.speechLog.fileName);
			$("#fileName").show();
		}else{
			data.component.fileName.$element.text( "" );
			$('#fileName').prop('title',"");
			$("#fileName").hide();
		}

		var $parent = data.component.log.$element.find( "form" );
		$parent.empty();

		data.voiceExistenceFlg = false;
		data.editLogContentsFlg = false;

		if ( !_util.isEmpty( response.editResult.speechLogDetails ) ) {

			var url = _prop.getApiMap( "speechlogdetail.voice" );
			url = url + "/";

			if ( data.saveVoice ) {
				// 音声ファイル利用

				$.each( response.editResult.speechLogDetails, function ( i, log ) {

					if ( !_util.isEmpty( log.speechLogDetailId ) ) {

						// 共通
						var $div = $( "<div></div>" ).
							addClass( "detail-log" ).
							attr({"data-call-log-detail-date" : log.updateDate,
									"data-call-log-detail-id": log.speechLogDetailId
							});

						if ( log.border ) {
							// 区切行用
							$div.append(
									$( "<span></span>" ),
									$( "<span></span>" ).addClass( "detail-txt no-edit mx-1" ).html(  nl2brDetailLog( log.log ) )
								);

						} else if ( !log.voiceExistence ) {
							// 音声無し
							$div.append(
									$( "<span></span>" ).addClass( "fa fa-play-circle no-play" ),
									$( "<span></span>" ).addClass( "detail-txt mx-1" ).html( nl2brDetailLog( log.log ) )
								);

							data.editLogContentsFlg = true;

						} else {
							// 音声あり
							$div.append(
									$( "<span></span>" ).addClass( "fa fa-play-circle play" ),
									$( "<audio></audio>" ).attr({
										"data-call-log-detail-id-v": log.speechLogDetailId
									}),
									$( "<span></span>" ).addClass( "detail-txt mx-1" ).html( nl2brDetailLog( log.log ) )
								);

							data.editLogContentsFlg = true;
							data.voiceExistenceFlg = true;
						}

						$div.appendTo( $parent ).after( "<br>" );

					}

				});

			} else {
				// 音声ファイル利用しない

				$.each( response.editResult.speechLogDetails, function ( i, log ) {

					if ( !_util.isEmpty( log.speechLogDetailId ) ) {

						// 共通
						var $div = $( "<div></div>" ).
							addClass( "detail-log" ).
							attr({"data-call-log-detail-date" : log.updateDate,
									"data-call-log-detail-id": log.speechLogDetailId
							});

						if ( log.border ) {
							// 区切行用
							$div.append(
									$( "<span></span>" ).addClass( "detail-txt no-edit mr-1" ).html(  nl2brDetailLog( log.log ) )
								);

						} else {
							// 音声無し
							$div.append(
									$( "<span></span>" ).addClass( "detail-txt mr-1" ).html( nl2brDetailLog( log.log ) )
								);

							data.editLogContentsFlg = true;
						}

						$div.appendTo( $parent ).after( "<br>" );
					}

				});
			}
		}

		_enableLogForm();

		data.index = 0;
		data.audios = $( "audio" );

		if ( _util.isNotEmpty( data.audios ) ) {

			data.audios.each(function(i, v){
				v.addEventListener("ended", ended, false);
				v.addEventListener("error", audioError, false);
			});

		}

	}


	function getDisplayName(name) {
        if (name) {
          if (name.length < 9) {
            return name;
          } else {
            return name.substr(0, 5) + '...' + name.substr(-5);
          }
        } else {
          return '';
        }
     }

	/**
	 * テキストをBRタグ付きHTMLに変換
	 *
	 * @param {String} txt テキスト
	 * @return {String} HTML
	 */
	function nl2brDetailLog ( txt ) {

		var $tmp = $( '<p></p>' ).text( txt );
		var addHtml = $tmp.html();

		addHtml = addHtml.replace(/\r?\n/g, "<br>\n");

		return addHtml;
	}

	/**
	 * 音声ファイル読み取りエラー時処理.
	 *
	 * @param {Event} event イベント
	 */
	function audioError( event ) {

		var $target = $( event.target );

		var detailId = $target.attr( "data-call-log-detail-id-v" );

		var $detailTarget = $( data.component.log.selector ).find( "[data-call-log-detail-id='" + detailId + "']" );

		$detailTarget.find( data.component.play.selector ).removeClass( "fa-play-circle" ).addClass( "fa-exclamation-triangle" );

		return false;
	}

	/**
	 * 連続再生ボタン押下時処理.
	 *
	 * @param {Event} event イベント
	 */
	function rennzoku( event ) {

		stop();

		data.playFlg = true;
		data.logScrollManualUseFlg = false;

		data.audios = [];

		var $audios = $( 'audio' );

		if ( $audios.length > 0 ) {

			data.audios = $audios;

			var detailId = '';

			detailId = $( data.audios[data.index] ).attr( "data-call-log-detail-id-v" );

			var $target = $( data.component.log.selector ).find( "[data-call-log-detail-id='" +detailId + "']" );

			if ( !$target.hasClass("bg-warning") ) {

				$target.addClass("bg-primary");
			}

			preSetVoiceSrc( data.index );

			if ( !$target.find( data.component.play.selector ).hasClass( "fa-exclamation-triangle" ) ) {

				var playPromise = data.audios[data.index].play();

				if ( playPromise !== undefined ) {

					playPromise.then( function() {

						$target.find( data.component.play.selector ).addClass( "play-now" );

						if ( !data.logScrollManualUseFlg ) {

							// 現在の表示位置をセット
							data.scDispPos = $target.prop( "offsetTop" );

							// 音声解析履歴スクロール位置取得
							var pos = _getLogScrollPosition( $target );

							data.component.log.$element.animate( { scrollTop: pos }, 300 );

						}

					} ).catch( function(error) {

					} );
				}

			} else {

				ended();
			}

		}

	}

	/**
	 * 音声ファイル事前読み込み処理.
	 *
	 * @param {number} index 読み込み開始位置
	 */
	function preSetVoiceSrc( index ) {

		if ( _util.isEmpty( index ) ) {

			index = 0;
		}

		var $audios = $( 'audio' );

		data.audios = $audios;

		var audiosLength = $audios.length;

		if ( audiosLength > 0 ) {

			var audio = '';

			var maxSetIndex = index + data.preGetNum;

			if ( maxSetIndex > ( audiosLength - 1 ) ) {

				maxSetIndex = audiosLength - 1;

			}

			for ( var i = index ; i <= maxSetIndex ; i++ ) {

				if ( _util.isNotEmpty( $audios[i] ) ) {

					var $audio = $( $audios[i] );

					// srcのチェック、無いなら追加
					var src = $audio.attr( "src" );
					if ( _util.isUndefined( src ) ) {

						var $parent = $audio.closest( data.component.detailLog.selector );

						var detailId = $parent.attr( "data-call-log-detail-id" );

						var url = _prop.getApiMap( "speechlogdetail.voice" );
							src = url + "/" + detailId;

						$audio.attr({"src" : src});

					}

				}

			}
		}

	}

	/**
	 *  停止ボタン押下時処理.
	 *
	 * @param {Event} event イベント
	 */
	function stop( event ) {

		_stop();

		$( data.component.log.selector ).find( data.component.play.selector ).removeClass( "play-now" );

	}

	/**
	 *  再生中のaudioを全て停止.
	 *
	 * @param {Event} event イベント
	 */
	function _stop( event ) {

		if ( _util.isNotEmpty( data.audios ) ) {

			// 再生中のaudioを全て停止する
			data.audios.each(function(i, audio){
				audio.pause();
				audio.currentTime = 0;
			});

		}

	}

	/**
	 *  再生ボタン押下時の処理.
	 *
	 * @param {Event} event イベント
	 */
	function onPlay( event ) {

		stop();

		data.playFlg = false;
		data.logScrollManualUseFlg = false;

		var $target = $( event.target );

		var $parent = $target.closest( data.component.detailLog.selector );
		var $audio  = $parent.find( "audio" );


		var index = $( "audio" ).index( $audio );

		data.index = index;

		$( data.component.log.selector ).find( data.component.detailLog.selector ).removeClass( "bg-primary" );
		$( data.component.log.selector ).find( data.component.play.selector ).removeClass( "play-now" );

		if ( !$parent.hasClass( "bg-warning" ) ) {

			$parent.addClass( "bg-primary" );
		}

		// 音声ファイル無し
		if ( $target.hasClass( "fa-exclamation-triangle" ) ) {

			return false;

		}

		preSetVoiceSrc( data.index );

		var playPromise = $audio[0].play();

		if ( playPromise !== undefined ) {

			playPromise.then( function() {

				$parent.find( data.component.play.selector ).addClass( "play-now" );

			} ).catch( function( error ) {

			} );
		}

	}

	/**
	 *  再生終了したら次の音声を選んでまた再生.
	 *
	 * @param {Event} event イベント
	 */
	function ended( event ) {

		var $oldTarget = $( data.component.log.selector + " .play-now:first" ).closest( data.component.detailLog.selector );

		if ( $oldTarget.length < 1 && data.audios.length > 0 ) {

			var oldDetailId = $( data.audios[data.index] ).attr( "data-call-log-detail-id-v" );
			$oldTarget = $( data.component.log.selector ).find( "[data-call-log-detail-id='" + oldDetailId + "']" );
		}

		$( data.component.log.selector ).find( data.component.play.selector ).removeClass( "play-now" );

		if ( data.playFlg ) {

			$( data.component.log.selector ).find( data.component.detailLog.selector ).removeClass( "bg-primary" );

			var detailId = "";
			var $target = {};

			var nextFlg = false;

			for ( var i = 0 ; i < data.audios.length ; i++ ) {

				data.index++;

				if ( data.index >= data.audios.length ) {

					break;

				}

				detailId = $( data.audios[data.index] ).attr( "data-call-log-detail-id-v" );

				$target = $( data.component.log.selector ).find( "[data-call-log-detail-id='" +detailId + "']" );

				if ( $target.find( data.component.play.selector ).hasClass( "fa-play-circle" ) ) {
					nextFlg = true;
					break;
				}

			}

			if ( !nextFlg ) {

				data.index = 0;
				return;
			}

			// テキスト編集中の黄色が無いなら青くする
			if ( !$target.hasClass( "bg-warning" ) ) {

				$target.addClass( "bg-primary" );
			}

			preSetVoiceSrc( data.index );

			var playPromise = data.audios[ data.index ].play();

			if ( playPromise !== undefined ) {

				playPromise.then( function() {

					$target.find( data.component.play.selector ).addClass( "play-now" );

					if ( !data.logScrollManualUseFlg ) {

						// 現在の表示位置をセット
						if ( $oldTarget.length > 0 ) {

							data.scDispPos = $oldTarget.prop( "offsetTop" );
						}

						// 音声解析履歴スクロール位置取得
						var pos = _getLogScrollPosition( $target );

						data.component.log.$element.animate( { scrollTop: pos }, 750 );

					}

				} ).catch( function(error) {

					$target.removeClass( "bg-primary" );

				} );
			}

		}

	}

	/**
	 *  音声解析履歴スクロール時.
	 *
	 * @param {Event} event イベント
	 */
	function dispLogScroll( event ) {

		if ( data.saveVoice ) {

			if ( !data.logScrollManualUseFlg ) {

				data.logScrollManualUseFlg = true;
			}

		}

	}

	/**
	 *  音声解析履歴スクロール位置取得.
	 *
	 * @param {Object} $target 移動先対象オブジェクト
	 *
	 * @return {Number} スクロール位置
	 */
	function _getLogScrollPosition( $target ) {

		var targePos = $target.prop( "offsetTop" );

		// 現在のスクロール位置を取得
		var scTop = data.component.log.$element.scrollTop();

		if ( scTop > 5 ) {
			// スクロールしている場合は調整
			data.scDispPos = data.scDispPos - scTop;
		}

		var dspHeight = data.component.log.$element.innerHeight();

		// 非表示位置の場合は初期位置を再セット
		if ( data.scDispPos <= 0 || targePos >= ( scTop + dspHeight ) ) {

			data.scDispPos = $( data.component.log.selector + " div:first" ).prop( "offsetTop" );
		}

		var pos = targePos - data.scDispPos;

		return pos;
	}

	/**
	 *  ログ編集テキストフォーム表示.
	 *
	 * @param {Event} event イベント
	 */
	function editTxt( event ) {

		var $target = $( event.target );

		if ( $target.hasClass( "no-edit" ) || !$target.hasClass( "detail-txt" ) ) {

			$target = $target.find( data.component.detailTxt.selector );

		}

		if ( $target.hasClass( "no-edit" ) || !$target.hasClass( "detail-txt" ) ) {

			return false;
		}

		if ( !$target.hasClass( "on" ) ) {

			$target.addClass( "on" );
			var txt = $target.text();

			$target.closest( data.component.detailLog.selector ).addClass( "d-flex" );
			$target.html( "" ).append( '<textarea class="form-control w-100" data-value=""></textarea>' ).addClass( "w-input-form" );

			$target.find( ".form-control" ).attr("data-value", txt).val( txt );

			var textarea = $( data.component.detailTxtInput.selector )["0"];

			if ( textarea.scrollHeight > textarea.offsetHeight ) {
				textarea.style.height = textarea.scrollHeight + 'px';
			}

			$( data.component.detailTxtInput.selector ).focus();

		}

	}

	/**
	 *  ログ編集テキストフォーム閉じる.
	 *
	 * @param {Event} event イベント
	 */
	function editTxtEnd( event ) {

		var $target = $( event.target );
		var inputVal = $target.val();

		var defaultValue = $target.attr( "data-value" );

		if ( inputVal.replace(/\r\n/g, "\n") !== defaultValue.replace(/\r\n/g, "\n") ) {

			$target.closest( data.component.detailLog.selector ).addClass( "change bg-warning" ).removeClass( "bg-primary" );

		}

		$target.parents().find( data.component.detailLog.selector ).removeClass( "d-flex" );
		$target.parents().find( ".w-input-form" ).removeClass( "w-input-form" );

		$target.parent().removeClass( "on" ).html( nl2brDetailLog( inputVal ) );

	}

	// -------------------------------------------------------------------------

	/**
	 * 更新ボタン押下処理.
	 *
	 * @param {Event} event イベント
	 */
	function updateLog( event ) {

		var $edit = $( data.component.log.selector ).find( ".change" );

		if ( !$edit || $edit.length < 1) {

			return false;
		}

		_view.confirmDialog( _prop.getMessage( "common.confirm.update" ), { ok : _updateLogOk } );
	}

	/**
	 * 更新確認ダイアログの OK ボタン押下処理
	 *
	 * @param {Event} event イベント
	 */
	function _updateLogOk( event ) {


		// 入力値取得
		var form = _createUpdateLogForm();

		// API 送信
		var url = _prop.getApiMap( "speechlogdetail.update" );
		var json = JSON.stringify( form );
		var option = {

			handleError : _updateLogError,
			handleSuccess : _updateLogSuccess
		};

		_api.postJSONSync( url, json, option );

		return false;
	}

	/**
	 * フォーム入力内容からログ詳細更新 API への送信オブジェクトを作成する.
	 *
	 * @return {Object} ログ詳細更新 API への送信オブジェクト
	 */
	function _createUpdateLogForm() {

		var $edit = $( data.component.log.selector ).find( ".change" );

		var bulkFormList = [];

		data.component.log.$element.find( ".change" ).each( function( idx, element ){

			var log = {};
			log.speechLogDetailId = $( element ).attr( "data-call-log-detail-id" );
			log.updateDate = $( element ).attr( "data-call-log-detail-date" );
			log.log = $( element ).find( data.component.detailTxt.selector ).text();

			var speechLogDetail = {
					"speechLogDetail" : log
				};

			bulkFormList.push( speechLogDetail );

		});

		var form = {
				"bulkFormList" : bulkFormList
			};

		return form;

	}

	/**
	 * ログ詳細更新 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _updateLogError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "common.error.update" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
	}

	/**
	 * ログ詳細更新 API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _updateLogSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_updateLogError( xhr, status, null, option );
			return;
		}


		if ( response.resultList[0].code === _prop.getProperty( "common.apiResponse.success" ) ) {
			// 正常終了

			_view.infoDialog( _prop.getMessage( "common.complete.update" ) );

		} else {

			// 部分エラー
			_view.infoDialog( _prop.getMessage( "speechLogManagement.error.partialError" ) );

		}

		if ( !_util.isEmpty( response.bulkResultList ) ) {

			$.each( response.bulkResultList, function ( i, val ) {

				var resultList  = val.resultList;
				var editResult  = val.editResult;

				// 部分正常終了のみ表示更新
				if ( resultList[0].code === _prop.getProperty( "common.apiResponse.success" ) ) {

					var log = editResult.speechLogDetail;

					var detailId = log.speechLogDetailId;
					var updateDate = log.updateDate;

					var $target = $( data.component.log.selector ).find( "[data-call-log-detail-id='" +detailId + "']" );

					if ( $target ) {

						$target.removeClass("change bg-warning");
						$target.attr({"data-call-log-detail-date" : log.updateDate});
						$target.find( data.component.detailTxt.selector ).html( nl2brDetailLog( log.log ) );

					}

				}

			});
		}

	}

	// -------------------------------------------------------------------------

	/**
	 * 音声ファイル出力ボタン押下
	 */
	function _exportFile( event ) {

		var $edit = $( data.component.log.selector ).find( ".change" );

		if ( $edit && $edit.length > 0 ) {

			_view.confirmDialog( _prop.getMessage( "speechLogManagement.exportEditingConfirm" ), { ok : _downloadOk} );

		} else {

			_view.confirmDialog( _prop.getMessage( "speechLogManagement.exportConfirm" ), { ok : _downloadOk} );

		}

		return false;
	}

	/**
	 * 出力ダイアログOKボタン押下時
	 */
	function _downloadOk( event ) {

		var speechLogId = data.speechLog.speechLogId;

		if ( _util.isEmpty( speechLogId ) ) return true; // ダイアログを閉じる

		_prepareDownload( speechLogId );

		// ダイアログを閉じる
		return true;
	}

	/**
	 * ダウンロード準備
	 */
	function _prepareDownload( speechLogId ) {

		// ダイアログ
		var dialog = _prop.getProperty( "layout.dialog.selector" );

		// ダイアログ閉じたイベント
		$( dialog ).on( "hidden.bs.modal", function () {

			$( dialog ).off( "hidden.bs.modal" );

			$( data.component.loading.selector ).show();

			// 黒フィルタ
			var $backdrop = $( "<div></div>" )
				.addClass( "modal-backdrop fade show" );

			$backdrop.appendTo( document.body );

			var form = { "editForm" : { "speechLog" : {

				"speechLogId" : speechLogId,

			} } };

			var url = _prop.getApiMap( "speechLog.generateVoiceByUser" );
			var json = JSON.stringify( form );

			var option = {

				handleError : _generateFileError,
				handleSuccess : _generateFileSuccess
			};

			_api.postJSON( url, json, option );

		});
	}

	/**
	 *  音声解析音声ファイル生成 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _generateFileError( xhr, status, errorThrown, option ) {

		$( ".modal-backdrop" ).remove();
		$( data.component.loading.selector ).hide();

		data.fileId = null;

		var msgList = [ _prop.getMessage( "speechLogManagement.error.generateError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );

	}

	/**
	 * 音声解析音声ファイル生成 API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _generateFileSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok || _util.isEmpty( response.fileId ) ) {

			_generateFileError( xhr, status, null, option );

			return;
		}

		$( ".modal-backdrop" ).remove();
		$( "#loading-bar-spinner" ).hide();

		data.fileId = response.fileId;

		// 正常終了
		if ( response.resultList[0].code === _prop.getProperty( "common.apiResponse.success" ) ) {

			_download();

			return;
		}

		// 部分エラーの場合は再度確認ダイアログ表示
		_view.confirmDialog( _prop.getMessage( "speechLogManagement.error.generatePartialError" ), { ok : _download } );

	}

	/**
	 * / ダウンロード
	 */
	function _download(  ) {

		var url = _prop.getApiMap( "speechLog.downloadFileByUser" );

		url += "/" + data.speechLog.speechLogId + "/" + data.fileId;

		_api.download( url );

		data.fileId = null;

		// ダイアログを閉じる
		return true;
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