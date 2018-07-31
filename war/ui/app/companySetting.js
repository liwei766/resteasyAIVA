"use strict";

/*
 * 企業設定画面
 */

$(function() {

	var _util = CCS.util;
	var _prop = CCS.prop;
	var _api = CCS.api;
	var _view = CCS.view;

	var data = {

		// 画面構成要素の定義
		// 初期処理で各要素の jQuery オブジェクトを $element として追加する

		component : {

			inputEnergyThreshold : { // ----- 音声判断レベル

				selector : "#inputEnergyThreshold",
				handler : [
					[ "keyup", inputEnergyThresholdCheck ] ,
					[ "blur", inputEnergyThresholdCheck ]
				],
			},

			companyUpdate : { // ----- 企業設定更新

				selector : "#update",
				handler : [ [ "click", companyUpdate ] ],
			},

		}, // end of component

		company : {},	// 編集中の企業情報

	}; // end of data

	// -------------------------------------------------------------------------
	// イベントハンドラ登録
	// -------------------------------------------------------------------------

	$.each(data.component, function(i, component) {

		if (_util.isEmpty(component.selector))
			return true; // continue

		let $component = $(component.selector);

		if ($component.length < 1)
			return true; // continue

		component.$element = $component;

		if (component.handler && _util.isArray(component.handler)) {

			$.each(component.handler, function(j, handler) {

				var selector = handler[0];
				var func = handler[1];

				if (selector && _util.isFunction(func)) {

					$component.on(selector, func);
				}
			});
		}
	});

	// -------------------------------------------------------------------------
	// 初期表示処理
	// -------------------------------------------------------------------------

	init();

	function init() {

		let url = _prop.getApiMap("companySetting.getCompanySettings");

		let option = {

			handleError : _getCompanySettingsError,
			handleSuccess : _getCompanySettingsSuccess
		};

		_api.postJSON(url, null, option);

		return true;
	}

	function _getCompanySettingsSuccess(response, status, xhr, option) {

		let objIdArr = ["inputEnergyThreshold","update"];

		try{

			data.company = response.companyManagement;

			$('#inputEnergyThreshold').val(data.company.energyThreshold);

			disableObj(objIdArr, false);

		}catch(err){

			_view.errorDialog( _prop.getMessage( "companySetting.error.getcompanySettingError" ) );

			disableObj(objIdArr, true);

		}

	}

	function disableObj(objIdArr,isDisabled){

		objIdArr.forEach(function(objId) {

			$(`#${objId}`).attr('disabled',isDisabled);

		});

	}

	function _getCompanySettingsError(xhr, status, errorThrown, option) {

		let msgList = [ _prop.getMessage("common.error.update") ];

		let msg = option.result.msgList[0].message;

		if (msg) {

			msgList.push("(" + msg + ")");
		}

		_dispError(msgList);

	}

	// -------------------------------------------------------------------------
	// イベントハンドラ
	// -------------------------------------------------------------------------

	function inputEnergyThresholdCheck(e){

		this.value=this.value.replace(/[^0-9]+/i,'');

	}

	/**
	 * 更新ボタン
	 *
	 * @returns
	 */
	function companyUpdate(event) {

		//音声判断レベル入力チェック：空白拒否
		if ( !$("#inputEnergyThreshold").val().trim() ) {

			_view.errorDialog( _prop.getMessage( "companySetting.error.noInputEnergyThreshold" ) );

			return false;

		}

		_view.confirmDialog(_prop.getMessage("common.confirm.update"), {

			ok : _companyUpdateOk

		});

	}

	/**
	 * 企業設定更新
	 */
	function _companyUpdateOk(event) {

		let form = {

			companyManagement : {

				energyThreshold : $("#inputEnergyThreshold").val().trim(),

				updateDate: data.company.updateDate

			}

		};

		let url = _prop.getApiMap("companySetting.updateCompanySettings");
		let json = JSON.stringify(form);

		let option = {

			handleError : _updateError,
			handleSuccess : _updateSuccess
		};

		_api.postJSON(url, json, option);

		return false;
	}

	function _updateError(xhr, status, errorThrown, option) {

		let msgList = [ _prop.getMessage("common.error.update") ];

		let msg = option.result.msgList[0].message;

		if (msg) {

			msgList.push("(" + msg + ")");
		}

		_dispError(msgList);

	}

	function _updateSuccess(response, status, xhr, option) {

		if (!option.result.ok) {

			_updateError(xhr, status, null, option);
			return;
		}

		// ----- 正常終了

		data.company = response.companyManagement;

		$('#inputEnergyThreshold').val(response.companyManagement.energyThreshold);

		_view.infoDialog(_prop.getMessage("common.complete.update"));

	}

	// -------------------------------------------------------------------------
	// 共通処理
	// -------------------------------------------------------------------------

	function _dispError(message) {

		if (_util.isArray(message)) {

			if (message.length < 1)
				return; // 空の配列
		} else {

			if (_util.isEmpty(message))
				return; // 空のメッセージ
			message = [ message ];
		}

		var msg = "";

		for (var i = 0; i < message.length; i++) {

			if (i !== 0)
				msg += "\n";
			msg += message[i];
		}

		_view.errorDialog(msg);
	}
}); // end of ready-handler

