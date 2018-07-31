"use strict";

/*
 * リカイアス音声解析フィラー情報登録画面
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

			updateForm : {

				selector : "#updateForm",
			},

			fileName : {		// ----- 日付

				selector : "#fileName",
				handler : [
					[ "change", changeFileName ]
				],
			},

			message : {			// ----- 処理結果メッセージ

				selector : "#message",
			},

			fillerUpdate : {		// ----- ユーザ登録辞書更新

				selector : "#update",
				handler : [
					[ "click", fillerUpdate ]
				],
			},

			exportFile : {

				selector : "#exportFile",
				handler : [
					[ "click", exportFile ],
				],
			},

		}, // end of component

	}; // end of data

	// -------------------------------------------------------------------------
	// イベントハンドラ登録
	// -------------------------------------------------------------------------

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

	// -------------------------------------------------------------------------
	// 初期表示処理
	// -------------------------------------------------------------------------

	function _enableForm() {

		data.component.updateForm.$element.find( "*" ).prop( "disabled", false );
	}

	function _disableForm() {

		data.component.updateForm.$element.find( "*" ).prop( "disabled", true );
	}

	// -------------------------------------------------------------------------
	// イベントハンドラ
	// -------------------------------------------------------------------------

	function _clearMessage() {

		data.component.message.$element.empty().attr( "hidden", "" ).removeClass( "bg-error text-info" );
	}

	/**
	 * メッセージをクリアする
	 */
	function changeFileName( event ) {

		_clearMessage();

		$('#photoCover').val($(this).val().replace(/\\/g, '/').replace(/.*\//, ''));

	}

	/**
	 * 保存ボタン
	 *
	 * @returns
	 */
	function fillerUpdate( event ) {

		_view.confirmDialog(_prop.getMessage("filler.confirm"), {
			ok : _fillerUpdateOk
		});
	}

	/**
	 * 出力ボタン
	 *
	 * @returns
	 */
	function exportFile( event ) {

		_clearMessage();

		_view.confirmDialog(_prop.getMessage("filler.exportConfirm"), {
			ok : _downloadCSV
		});

		return false;
	}

	/**
	 * フィラー情報更新
	 */
	function _fillerUpdateOk( event ) {

		_clearMessage();	// メッセージクリア

		var fileList = data.component.fileName.$element[0].files;

		if ( _util.isUndefined( fileList ) || fileList.length < 1 ) {

			_view.errorDialog( _prop.getMessage( "filler.error.noFile" ) );
			return false;
		}

		var file = fileList[0];

		if ( _util.isUndefined( file ) || file.size < 1 ) {

			_view.errorDialog( _prop.getMessage( "filler.error.emptyFile" ) );
			return false;
		}

		var fd = new FormData();

		fd.append( "fileName", file );

		var url = _prop.getApiMap( "filler.update" ) ;

		var option = {

			handleError : _updateError,
			handleSuccess : _updateSuccess,

			ajaxOption : {
				processData : false,
				contentType : false,
			}
		};

		_disableForm();
		_api.postJSON( url, fd, option );
		// ここでダイアログは閉じない
		return false;
	}

	function _updateError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "filler.error.updateError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );

		_enableForm();
		_clearMessage();
	}

	function _updateSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_updateError( xhr, status, null, option );
			return;
		}

		// ----- 正常終了

		_view.dialogClose();
		_enableForm();

		var $parent = data.component.message.$element;
		var msg = null;

		// 取り込み完了

		if ( response.resultList[0].code === _prop.getProperty( "common.apiResponse.success" ) ) {

			msg = _util.format( _prop.getMessage( "filler.complete" ), response.dataCount );
			$parent.addClass( "text-info" ).text( msg );
			$parent.removeAttr( "hidden" );
			return;
		}

		// 部分エラーの場合はエラーメッセージを表示

		$parent.addClass( "bg-error" );

		msg = _util.format( _prop.getMessage( "filler.error.partialError" ), response.errorCount );

		$( "<p></p>" ).text( msg ).appendTo( $parent );

		var msgList = option.result.msgList[0].sublist;

		if ( msgList.length < response.errorCount ) {

			msg = _util.format(
				_prop.getMessage( "filler.error.tooManyError" ), msgList.length );

			$( "<p></p>" ).text( msg ).appendTo( $parent );
		}

		var $list = $( "<ul></ul>" );
		$.each( msgList, function( i, msg ) {

			$( "<li></li>" ).text( response.bulkResultList[i].number + "件目：" + msg.message ).appendTo( $list );
		});

		$list.appendTo( $parent );
		$parent.removeAttr( "hidden" );
	}

	function _downloadCSV(event) {

		let ext="/csv";
		let fileId="/FILLER_DICTIONARY";
		let apiURL = _prop.getApiMap( "filler.file" ) + ext + fileId;

		_api.download( apiURL );

		// ダイアログを閉じる
		return true;
	}

	// -------------------------------------------------------------------------
	// 共通処理
	// -------------------------------------------------------------------------

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


function downloadError () {
	var _prop = CCS.prop;
	var _view = CCS.view;
	_view.errorDialog(_prop.getMessage( "filler.error.exportError"));
}
