"use strict";

/*
 * 音声処理画面
 */

$(function() {

	var _util = CCS.util;
	var _prop = CCS.prop;
	var _api  = CCS.api;
	var _view = CCS.view;

	var data = {

		// 画面構成要素の定義
		// 初期処理で各要素の jQuery オブジェクトを $element として追加する

		component : {

			selectFile : {				// ----- ファイル

				selector : "#file_select",
				handler : [ [ "change", setFile ] ],
			},

			startFileAnalyze : {		// ----- （ファイル）解析開始ボタン

				selector : "#startFileAnalyze",
				handler : [ [ "click", startFileAnalyze ] ],
			},

			startSpeech : {				// ----- （マイク）解析開始ボタン

				selector : "#startSpeech",
				handler : [
					[ "click", startSpeech ],
					[ "_startSpeech", _startSpeech ]	// startCallDisplay から起動
				],
			},

			endSpeech : {				// ----- （マイク）解析終了ボタン

				selector : "#endSpeech",
				handler : [
					[ "click", endSpeechDisplay ],
					[ "_endSpeech", _endSpeech ]		// endSpeechDisplay から起動
				],
			},

			speechStatus : {			// ----- 解析状況

				selector : "#speechStatus",
			},

			speech : {					// ----- 解析中

				selector : "#speech",
			},

			starting : {				// ----- 準備中

				selector : "#starting",
			},

			ending : {					// ----- 終了中

				selector : "#ending",
			},

			fileName : {				// ----- 解析ファイル名

				selector : "#fileName",
			},

			log : {						// ----- 音声解析履歴内容

				selector : "#dispLog",
				handler : [
					[ "mousedown", dispLogScroll ],
					[ "wheel", dispLogScroll ]
				],
			},

			play : {					// ----- 再生ボタン

				selector : ".play",
			},

			detailTxt : {				// ----- ログ詳細（文節）内容

				selector : ".detail-txt",
			},

			detailLog : {				// ----- ログ詳細（文節）

				selector : ".detail-log"
			},

			detailTxtInput : {			// ----- ログ詳細（文節）内容 入力フォーム

				selector : ".detail-txt > textarea",
			},

			exportFile : {				// ----- ファイル出力ボタン

				selector : "#exportFile",
				handler : [ [ "click", _exportFile ] ],
			},

			update : {					// ----- 更新ボタン

				selector : "#update",
				handler : [ [ "click", updateLog ] ],
			},

			countClock : {				// ----- 利用時間（カウントタイマー）

				selector : "#countClock",
			},

			rennzoku : {				// ----- 連続再生ボタン

				selector : ".rennzoku",
				handler : [ [ "click", rennzoku ] ],
			},

			stop : {					// ----- 停止ボタン

				selector : ".stop",
				handler : [ [ "click", stop ] ],
			},

			overlay : {					// ----- 音声ファイル解析中表示

				selector : "#overlay",
			},

			loading : {					// ----- 音声ファイル生成中表示

				selector : "#loading-bar-spinner",
			},

			progressMsg : {					// ----- 進捗メッセージ

				selector : "#progressMsg",
			},

			progressRate : {				// ----- 進捗率

				selector : "#progressRate",
			},

			progressBar : {					// ----- 進捗バー

				selector : "#progressbar",
			},

			pauseSpeech : {                  // ----- 一時停止ボタン
				selector : "#pauseSpeech",
				handler : [
					[ "click", pauseSpeech ],
				],
			},

			// ---------- 一時停止ダイアログ

			// 一時停止ダイアログ
			pauseSpeechDialog : {

				selector : "#pauseSpeechDialog",
			},

			// 一時停止ダイアログ 再開ボタン
			pauseSpeechDialogResume : {

				selector : "#pauseSpeechDialog .modal-footer .btn1-button",
				handler : [
					[ "click", _dialogBtn1Handler ],
				],
			},

			// 一時停止ダイアログ 終了ボタン
			pauseSpeechDialogEndSpeech : {

				selector : "#pauseSpeechDialog .modal-footer .btn2-button",
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
		},

		mode : "file",
		beginDate : null,

		buffersize : 16384, // 256, 512, 1024, 2048, 4096, 8192, 16384

		mic : null,

		speechLogId : null,				// 音声解析ログID
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
		fileId : null,			// 音声ファイルID

		inProgress : false,  // （マイク）解析準備中／終了処理中は true にする

		resumeErrFlg : false  // 再開エラー

	}; // end of data

	// -------------------------------------------------------------------------
	// イベントハンドラ登録
	// -------------------------------------------------------------------------

	// 1. data.component の selector がある項目について、
	// jquery オブジェクトを作成して $element として格納する
	// 2. data.component の handler がある項目について、
	// 指定されたイベントのイベントハンドラを登録する

	$.each(data.component, function(i, component) {

		if (_util.isEmpty(component.selector))
			return true; // continue

		var $component = $(component.selector);

		if ($component.length < 1)
			return true; // continue

		component.$element = $component;

		if (component.handler && _util.isArray(component.handler)) {

			$.each(component.handler, function(j, handler) {

				var event = handler[0];
				var func = handler[1];

				if (event && _util.isFunction(func)) {

					$component.on(event, func);
				}
			});
		}

	});

	// ----- ログ文節再生
	data.component.log.$element.on( "click", data.component.play.selector,
		onPlay );

	// ----- ログ文節クリック
	data.component.log.$element.on( "click", data.component.detailLog.selector,
		editTxt );

	// ----- ログテキストフォーム
	data.component.log.$element.on( "blur",
		data.component.detailTxtInput.selector, editTxtEnd );

	// 一時停止ダイアログ関連 初期値セット
	_setComponentOp(data.component.pauseSpeechDialog.selector);

	// -------------------------------------------------------------------------
	// 初期表示処理
	// -------------------------------------------------------------------------

	// 初期画面表示用に各フォームをクリア
	_prepareDefaultDisp();

	_view.setComponent( $( "body" ) );

	/**
	 * 初期画面表示用に各フォームをクリア
	 */
	function _prepareDefaultDisp() {
		// 音声ファイル利用設定セット
		_setSaveVoice();

		// （ファイル）解析開始ボタンを非活性にする
		data.component.startFileAnalyze.$element.prop( "disabled", true )
			.addClass( "cursor-not-allowed" );

		_disableLogForm();	// 履歴関連ボタン非活性化
	}

	/**
	 * 音声ファイル利用設定セット
	 */
	function _setSaveVoice() {

		var _user = CCS.user;
		data.saveVoice = _user.getData( _user.SAVE_VOICE );
	}

	// -------------------------------------------------------------------------
	// イベントハンドラ
	// -------------------------------------------------------------------------
	/**
	 * 履歴関連ボタン非活性化.
	 */
	function _disableLogForm() {

		_disableVoiceLogForm();

		data.component.exportFile.$element.prop( "disabled", true ).addClass( "cursor-not-allowed" );

		data.component.update.$element.prop( "disabled", true ).addClass( "cursor-not-allowed" );

		if ( ! data.saveVoice ) {

			data.component.rennzoku.$element.prop( "hidden", true );
			data.component.stop.$element.prop( "hidden", true );
		}
	}

	/**
	 * 履歴関連ボタン活性化.
	 */
	function _enableLogForm() {

		if ( data.saveVoice && data.voiceExistenceFlg ) {

			_enableVoiceLogForm();
		}

		if ( data.editLogContentsFlg ) {

			data.component.exportFile.$element.prop( "disabled", false ).removeClass( "cursor-not-allowed" );
			data.component.update.$element.prop( "disabled", false ).removeClass( "cursor-not-allowed" );
		}
	}

	/**
	 * 音声ボタン 非活性化.
	 */
	function _disableVoiceLogForm() {

		data.component.rennzoku.$element.prop( "disabled", true ).addClass( "cursor-not-allowed" );
		data.component.stop.$element.prop( "disabled", true ).addClass( "cursor-not-allowed" );
	}

	/**
	 * 音声ボタン 活性化.
	 */
	function _enableVoiceLogForm() {

		data.component.rennzoku.$element.prop( "disabled", false ).removeClass( "cursor-not-allowed" );
		data.component.stop.$element.prop( "disabled", false ).removeClass( "cursor-not-allowed" );
	}

	// -------------------------------------------------------------------------

	/**
	 * ファイル選択
	 */
	function setFile() {
		// ファイルの取得
		var fileList = data.component.selectFile.$element[0].files;

		// 取得できない場合は（ファイル）解析開始ボタンを非活性にする
		if ( _util.isUndefined(fileList) || fileList.length < 1 ) {
			// （ファイル）解析開始ボタンを非活性にする
			data.component.startFileAnalyze.$element.prop( "disabled", true ).addClass( "cursor-not-allowed" );

			return false;
		}

		// （ファイル）解析開始ボタンを活性にする
		data.component.startFileAnalyze.$element.prop( "disabled", false ).removeClass( "cursor-not-allowed" );

		return false;
	}

	// -------------------------------------------------------------------------

	/**
	 * （ファイル）解析開始ボタン押下
	 */
	function startFileAnalyze(event) {

		var logContents = $( data.component.log.selector ).text();

		// ログ内容の確認
		if ( !_util.isEmpty( logContents ) ) {

			_view.confirmDialog(_prop.getMessage( "speech.confirm" ), {
				ok : startFileAnalyzeOK
			});

		} else {
			// 編集でログ内容を空にした場合の確認
			var $edit = $( data.component.log.selector ).find( ".change" );

			if ( $edit && $edit.length > 0) {

				_view.confirmDialog(_prop.getMessage( "speech.confirm" ), {
					ok : startFileAnalyzeOK
				});

			} else {

				startFileAnalyzeOK(event);
			}
		}
	}

	function startFileAnalyzeOK(event) {

		data.speechLogId = null;

		data.resumeErrFlg = false;

		// 履歴関連ボタン非活性化
		_disableLogForm();

		data.component.log.$element.find( "form" ).empty();

		data.component.countClock.$element.html( new Date(0).toISOString().slice(11, -5) );

		data.component.fileName.$element.text("　");

		data.component.progressMsg.$element.text( _prop.getLabel( "speech.fileAnalyze" ) );
		data.component.progressRate.$element.text( "0%" );
		data.component.progressBar.$element.css("width", "0%");

		// ファイルの取得
		var fileList = data.component.selectFile.$element[0].files;

		// 取得できない場合はエラー
		if ( _util.isUndefined(fileList) || fileList.length < 1 ) {

			_view.errorDialog(_prop.getMessage( "speech.error.noFile" ));
			return false;
		}

		var file = fileList[0];

		if ( _util.isUndefined(file) || file.size < 1 ) {

			_view.errorDialog( _prop.getMessage( "speech.error.emptyFile" ) );
			return false;
		}

		data.component.startFileAnalyze.$element.prop( "disabled", true );

		// 送信データ生成
		var fd = new window.FormData();
		fd.append("fileName", file);

		// API 送信準備
		var url = _prop.getApiMap( "speech.fileAnalyze" );

		var option = {
			handleError : _fileAnalyzeError,
			handleSuccess : _fileAnalyzeSuccess,

			ajaxOption : {
				processData : false,
				contentType : false,
			}
		};

		// ダイアログを閉じる → 画面フィルタ適用
		_view.dialogClose();
		data.component.overlay.$element.show();

		// API 送信
		_api.postJSON(url, fd, option);

		data.updateProgressBar = setInterval(updateProgressBar, 500);

		return false;
	}

	let updateProgressBar = function () {

		let url = _prop.getApiMap( "speech.getProgressRate" );

		let option = {

				handleError : _getProgressRateError,
				handleSuccess : _getProgressRateSuccess
		};

		_api.postJSON( url, {}, option );

	};

	function _getProgressRateError( xhr, status, errorThrown, option ) {

		let msgList = [ _prop.getMessage( "speech.error.getError" ) ];

		let msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( `( ${msg} )` );
		}

	}

	function _getProgressRateSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_getProgressRateError( xhr, status, null, option );
			return;
		}

		// ----- 正常終了

		let progressRate = parseFloat( response.progressRate );

		if ( progressRate >= 80 ) {

			if ( progressRate < 90) {

				data.component.progressMsg.$element.text( _prop.getLabel( "speech.endFileAnalyze" ) );

			} else {

				data.component.progressMsg.$element.text( _prop.getLabel( "speech.saveFileAnalyze" ) );
			}

		} else {

			data.component.progressMsg.$element.text( _prop.getLabel( "speech.fileAnalyze" ) );
		}

		data.component.progressRate.$element.text( `${response.progressRate}%` );

		data.component.progressBar.$element.css("width", `${response.progressRate}%`);

		if ( 100 == progressRate ) {
			clearInterval(data.updateProgressBar);
		}

	}

	function _fileAnalyzeError(xhr, status, errorThrown, option) {

		clearInterval(data.updateProgressBar);

		data.component.overlay.$element.hide();

		data.component.startFileAnalyze.$element.prop( "disabled", false ).removeClass( "cursor-not-allowed" );

		let msg = _prop.getMessage( "speech.error.analyzeFailed" );
		_dispError(msg);
	}

	function _fileAnalyzeSuccess(retData, status, xhr, option) {

		if ( !option.result.ok || _util.isEmpty(retData.speechLogId) ) {
			// エラー表示
			_fileAnalyzeError(xhr, status, null, option);

			return false;
		}

		clearInterval(data.updateProgressBar);

		data.component.fileName.$element.text( $( "input[type=file]" )[0].files[0].name + "　");

		data.component.startFileAnalyze.$element.prop( "disabled", false ).removeClass( "cursor-not-allowed" );

		data.speechLogId = retData.speechLogId;

		// 利用時間表示
		if ( !_util.isEmpty(retData.time) ) {
			data.component.countClock.$element.html(new Date(retData.time).toISOString().slice(11, -5));
		}

		// 音声解析ログを取得して表示
		_refreshLog();
	}

	// -------------------------------------------------------------------------

	/**
	 * （マイク）音声解析開始
	 */
	function startSpeech(event) {

		var logContents = $( data.component.log.selector ).text();

		// ログ内容の確認
		if (!_util.isEmpty( logContents ) ) {

			_view.confirmDialog(_prop.getMessage( "speech.confirm" ), {
				ok : _startSpeechDisplay
			});

		} else {
			// 編集でログ内容を空にした場合の確認
			var $edit = $( data.component.log.selector ).find( ".change" );

			if ( $edit && $edit.length > 0) {

				_view.confirmDialog(_prop.getMessage( "speech.confirm" ), {
					ok : _startSpeechDisplay
				});

			} else {

				_startSpeechDisplay(event);
			}
		}
	}

	/**
	 * 準備中表示
	 */
	function _startSpeechDisplay() {

		if ( data.inProgress ) return true; // 開始中／終了中なので何もしない ダイアログを閉じる

		$( data.component.starting.selector ).prop( "hidden", false );
		$( data.component.speech.selector ).prop( "hidden", true );
		$( data.component.ending.selector ).prop( "hidden", true );

		setTimeout( function() {
			$( data.component.startSpeech.selector ).trigger( "_startSpeech" );
		}, 0);

		// ダイアログを閉じる
		return true;
	}

	function _startSpeech(event) {

		if ( data.inProgress ) return false; // 開始中／終了中なので何もしない

		data.inProgress = true;

		try {
			data.speechLogId = null;

			$( ".switch--button" ).removeAttr( "href" ).addClass( "cursor-not-allowed" );

			data.component.startSpeech.$element.prop( "disabled", true ).prop( "hidden", true );
			data.component.endSpeech.$element.prop( "disabled", false ).prop( "hidden", false );

			data.component.fileName.$element.text("　");
			data.component.log.$element.find( "form" ).empty();

			_disableLogForm();

			data.component.countClock.$element.html( new Date(0).toISOString().slice(11, -5) );
			data.beginDate = new Date();
			data.timer = setInterval( _updateSpeechTime, 1000 );

			data.analyzeInfo = {
				token : null,
				uuid : null,
				voiceId : 1,
			};

			// API 送信
			var url = _prop.getApiMap( "speech.start" );

			var option = {
				handleError : _startSpeechError,
				handleSuccess : _startSpeechSuccess
			};

			_api.postJSONSync(url, null, option);

			if (_util.isEmpty(data.speechLogId)) {
				// 処理を終了
				return false;
			}

			// マイクオブジェクト生成
			var mic = new Mic(data.buffersize);
			data.mic = mic;

			// 解析準備完了
			data.inProgress = false;
			$( data.component.starting.selector ).prop( "hidden", true );
			$( data.component.speech.selector ).prop( "hidden", false );
			$( data.component.ending.selector ).prop( "hidden", true );

			$(data.component.pauseSpeech.selector).removeClass( 'invisible' );
			// 解析処理
			_analyze(mic);
		}
		catch ( ex ) {

			data.inProgress = false;
		}
	}

	/**
	 * 音声解析開始API用エラーハンドラ
	 */
	function _startSpeechError(xhr, status, errorThrown, option) {

		_view.errorDialog(_prop.getMessage( "speech.error.speechStartError" ));

		// 音声解析時間表示更新を停止
		clearInterval(data.timer);

		// 音声解析時間クリア
		data.component.countClock.$element.html( new Date(0).toISOString().slice(11, -5) );

		_resetSpeechDisp();

		data.inProgress = false;
	}

	/**
	 * 音声解析終了後 各ボタン表示を戻す
	 */
	function _resetSpeechDisp() {

		// 音声解析時 各ボタン表示／非表示
		$( data.component.startSpeech.selector ).prop( "disabled", false ).prop( "hidden", false );
		$( data.component.endSpeech.selector ).prop( "disabled", true ).prop( "hidden", true);

		$( ".button--group-1" ).attr( "href", "#switch-mode-1" );
		$( ".button--group-2" ).attr( "href", "#switch-mode-2" );
		$( ".switch--button" ).removeClass( "cursor-not-allowed" );

		// 終了処理完了
		$( data.component.starting.selector ).prop( "hidden", true );
		$( data.component.speech.selector ).prop( "hidden", true );
		$( data.component.ending.selector ).prop( "hidden", true );
		$(data.component.pauseSpeech.selector).addClass( 'invisible' );
	}

	/**
	 * 音声解析開始API用成功ハンドラ
	 */
	function _startSpeechSuccess(retData, status, xhr, option) {

		if ( !option.result.ok || _util.isEmpty(retData.speechLogId) ) {
			// エラー表示
			_startSpeechError(xhr, status, null, option);

			return false;
		}

		data.speechLogId = retData.speechLogId;
		data.analyzeInfo.token = retData.token;
		data.analyzeInfo.uuid = retData.uuid;
	}

	// -------------------------------------------------------------------------

	function _updateSpeechTime() {

		data.component.countClock.$element.html(
			new Date(new Date() - data.beginDate).toISOString().slice(11,-5));
	}

	/**
	 * 解析処理
	 */
	function _analyze(mic) {

		mic.start(function(blob, ex) {

			if (blob === false) { // エラー
// エラー内容によっては処理を続ける_analyzeError内で判定
//				data.mic.stop();
//				clearInterval(data.timer);
//				data.component.countClock.$element.html( new Date(0).toISOString().slice(11, -5) );
				_analyzeError(ex);
				return;
			}

			if ( $( data.component.speech.selector ).prop( "hidden" ) ) {
				// 連打すると止まらないことがある
				mic.stop();
				return;
			}

			// 送信データ生成
			var fd = new window.FormData();
			fd.append( "voice", blob );

			// API 送信
			var url = _prop.getApiMap( "speech.update" ) +
						"/" + data.analyzeInfo.token +
						"/" + data.analyzeInfo.uuid +
						"/" + data.analyzeInfo.voiceId;

			var option = {
				handleError : function() {}, // 何もしない
				handleSuccess : _onResultProcess,

				ajaxOption : {
					processData : false,
					contentType : "multipart/form-data",
				}
			};

			_api.postJSONSync(url, blob, option);
			data.analyzeInfo.voiceId++;
		});
	}

	/**
	 * 解析エラー処理
	 */
	function _analyzeError(ex) {

		// 解析開始時にマイクが接続されていない場合は解析開始を取り消す
		// 解析途中なら続行する

		if ( ex.name &&
			 _util.isEmpty( $( data.component.log.selector ).text().trim() ) &&
				( ex.name === "DevicesNotFoundError" ||
				  ex.name === "PermissionDeniedError" ||
				  ex.name === "NotFoundError") ) {

			_endSpeech();
			_view.errorDialog( _prop.getMessage( "speech.error.noMic" ), { ok : _refreshLog } );

		} else {

// 上記エラー以外は処理を続ける
//			let responseText = JSON.parse(ex.responseText);

			//			let msg = responseText.resultList[0].code + ":"
			//					+ responseText.resultList[0].message;

//			let msg = _prop.getMessage( "speech.error.analyzeBeginFailed" );


//			_dispError(msg);

			_util.log( "Analyze error" );
			_util.log(ex);
		}
	}

	/**
	 * 音声解析結果出力
	 */
	function _onResultProcess(result) {

		data.component.overlay.$element.hide();

		// ここではエラーコードは判定しない、解析結果が無い場合はreturn
		if (result === "" || _util.isEmpty(result.analyzeResult))
			return;

		let json = result.analyzeResult;
		let _result = [];
		let _time = [];

		// 更新前の高さ
		var $log = $( data.component.log.selector );
		var prevHeight = $log[0].scrollHeight;

		var $parent = $log.find( "form" );

		json.forEach(function(each) {
			var type = each.type;

			if (type === "SOS") {
				var $div = $( "<div></div>" ).addClass( "detail-log cursor-not-allowed" );

				if ( data.saveVoice ) {
					// 音声ファイル利用
							$div.append($( "<span></span>" ).addClass(
								"fa fa-play-circle play disabled-class" ), $( "<span></span>" ).addClass(
								"detail-txt no-edit mx-1" ) );
				} else {

					$div.append($( "<span></span>" ).addClass(
						"detail-txt no-edit mr-1"));
				}

				$div.appendTo($parent);

			} else if (type === "TMP_RESULT") {

				$("#dispLog div:last span:last").html(nl2brDetailLog(each.str));

			} else if (type === "RESULT") {

				$( "#dispLog div:last span:first" ).attr( "currTime", each.time );
				$( "#dispLog div:last span:last" ).html( nl2brDetailLog( each.str ) );
				$( "#dispLog div:last" ).after( "<br>" );

				_time.push(each.time);

			} else if (type === "REJECT") {

				$("#dispLog div:last").remove();
			}

		});

		// 必要に応じて、スクロールバーを末尾に移動
		if ( $log[0].scrollHeight <= $( $log[0] ).innerHeight() ) return;

		if ( $log[0].scrollTop >= ( prevHeight - $( $log[0] ).innerHeight() - 5 ) ) {

			// 末尾を表示しているときだけ、末尾に移動する
			// （ユーザがスクロールして上の方を見ているときは移動しない）
			// （末尾条件に5px分の余裕を追加）

			$log[0].scrollTop = $log[0].scrollHeight;
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * （マイク）音声解析終了中
	 */
	function endSpeechDisplay() {

		if ( data.inProgress ) return false; // 開始中／終了中なので何もしない

		$( data.component.starting.selector ).prop( "hidden", true );
		$( data.component.speech.selector ).prop( "hidden", true );
		$( data.component.ending.selector ).prop( "hidden", false );

		setTimeout( function() {
			$( data.component.endSpeech.selector ).triggerHandler( "_endSpeech" );
			_refreshLog();
		}, 0);
	}

	/**
	 * （マイク）音声解析終了
	 */
	function _endSpeech() {

		if ( data.inProgress ) return false; // 開始中／終了中なので何もしない

		data.inProgress = true;

		try {
			// 解析ストップ
			_stopAnalyze();

			// 利用時間表示更新を停止
			clearInterval(data.timer);
		}
		catch ( ex ) {}
	}

	/**
	 * 解析ストップ
	 */
	function _stopAnalyze() {

		data.mic.stop();

		// 送信データ生成
		var form = {
			token : data.analyzeInfo.token,
			uuid : data.analyzeInfo.uuid,
			voiceId : data.analyzeInfo.voiceId
		};

		// API 送信
		var url = _prop.getApiMap("speech.end");
		var json = JSON.stringify(form);

		var option = {
			handleError : function() {},
			handleSuccess : _onResultProcess
		};

		_api.postJSONSync(url, json, option);
	}

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

	// -------------------------------------------------------------------------
	// イベントハンドラ：音声解析履歴
	// -------------------------------------------------------------------------

	/**
	 * 音声解析ログを取得して表示.
	 *
	 */
	function _refreshLog() {

		if ( _util.isEmpty( data.speechLogId ) ) return; // 音声解析ログ ID 未指定なら何もしない

		_disableLogForm();

		data.speechLog = {};

		var form = { editForm : { speechLog : {

			speechLogId : data.speechLogId,

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
	 * 音声解析ログ取得 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _logError( xhr, status, errorThrown, option ) {

		if ( data.inProgress ) {
			// 音声解析時
			_resetSpeechDisp();

			data.inProgress = false;

		} else {
			// 音声ファイル解析時
			data.component.overlay.$element.hide();
		}

		var msgList = [ _prop.getMessage( "speech.error.logError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
	}

	/**
	 * 音声解析ログ取得 API 正常終了処理：ログ表示エリアに音声解析ログ内容を表示.
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

		var $parent = $( data.component.log.selector ).find( "form" );

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
							attr({"data-speech-log-detail-date" : log.updateDate,
									"data-speech-log-detail-id": log.speechLogDetailId
							});

						if ( !log.voiceExistence ) {
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
										"data-speech-log-detail-id-v": log.speechLogDetailId
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
							attr({"data-speech-log-detail-date" : log.updateDate,
									"data-speech-log-detail-id": log.speechLogDetailId
							});

						// 音声無し
						$div.append(
								$( "<span></span>" ).addClass( "detail-txt mr-1" ).html( nl2brDetailLog( log.log ) )
							);

						data.editLogContentsFlg = true;

						$div.appendTo( $parent ).after( "<br>" );
					}
				});
			}
		}

		_view.dialogClose();
		_enableLogForm();

		data.index = 0;
		data.audios = $( "audio" );

		if ( _util.isNotEmpty( data.audios ) ) {

			data.audios.each(function(i, v){
				v.addEventListener("ended", ended, false);
				v.addEventListener("error", audioError, false);
			});
		}

		if ( data.inProgress ) {
			// 音声解析時
			_resetSpeechDisp();

			data.inProgress = false;

		} else {
			// 音声ファイル解析時
			data.component.overlay.$element.hide();
		}

	}

	// -------------------------------------------------------------------------
	// イベントハンドラ：音声再生
	// -------------------------------------------------------------------------

	/**
	 * 音声ファイル読み取りエラー時処理.
	 *
	 * @param {Event} event イベント
	 */
	function audioError( event ) {

		var $target = $( event.target );

		var detailId = $target.attr( "data-speech-log-detail-id-v" );

		var $detailTarget = $( data.component.log.selector ).find( "[data-speech-log-detail-id='" + detailId + "']" );

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

		var $audios = $( "audio" );

		if ( $audios.length > 0 ) {

			data.audios = $audios;

			var detailId = "";

			detailId = $( data.audios[data.index] ).attr( "data-speech-log-detail-id-v" );

			var $target = $( data.component.log.selector ).find( "[data-speech-log-detail-id='" +detailId + "']" );

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

		var $audios = $( "audio" );

		data.audios = $audios;

		var audiosLength = $audios.length;

		if ( audiosLength > 0 ) {

			var audio = "";

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

						var detailId = $parent.attr( "data-speech-log-detail-id" );

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

		if (data.mic !== null && data.mic.is_recoding) {
			return;
		}

		var $target = $( event.target );
		if ( $target.hasClass( "disabled-class" ) ) {
			return;
		}

		stop();

		data.playFlg = false;
		data.logScrollManualUseFlg = false;

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

			var oldDetailId = $( data.audios[data.index] ).attr( "data-speech-log-detail-id-v" );
			$oldTarget = $( data.component.log.selector ).find( "[data-speech-log-detail-id='" + oldDetailId + "']" );
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

				detailId = $( data.audios[data.index] ).attr( "data-speech-log-detail-id-v" );

				$target = $( data.component.log.selector ).find( "[data-speech-log-detail-id='" +detailId + "']" );

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

	// -------------------------------------------------------------------------
	// イベントハンドラ： ログ編集
	// -------------------------------------------------------------------------

	/**
	 * ログ編集テキストフォーム表示.
	 *
	 * @param {Event}
	 *            event イベント
	 */
	function editTxt(event) {
		if (data.mic !== null && data.mic.is_recoding) {
			return;
		}

		var $target = $( event.target );

		if ($target.hasClass( "no-edit" ) || !$target.hasClass( "detail-txt" )) {

			$target = $target.find( data.component.detailTxt.selector );
		}

		if ($target.hasClass( "no-edit" ) || !$target.hasClass( "detail-txt" )) {

			return false;
		}

		if (!$target.hasClass( "on" )) {

			$target.addClass( "on" );
			var txt = $target.text();

			$target.closest( data.component.detailLog.selector ).addClass(
				"d-flex");
			$target
				.html("")
				.append(
					'<textarea class="form-control w-100" cols="200%" data-value=""></textarea>')
				.addClass("w-input-form");

			$target.find( ".form-control" ).attr( "data-value", txt );
			$target.find( ".form-control" ).val( txt );

			var textarea = $(data.component.detailTxtInput.selector)["0"];

			if (textarea.scrollHeight > textarea.offsetHeight) {
				textarea.style.height = textarea.scrollHeight + "px";
			}

			$(data.component.detailTxtInput.selector).focus();
		}

	}

	/**
	 * ログ編集テキストフォーム閉じる.
	 *
	 * @param {Event}
	 *            event イベント
	 */
	function editTxtEnd(event) {

		var $target = $(event.target);
		var inputVal = $target.val();

		var defaultValue = $target.attr( "data-value" );

		if (inputVal.replace(/\r\n/g, "\n") !== defaultValue.replace(/\r\n/g,
				"\n")) {

			$target.closest( data.component.detailLog.selector ).addClass(
				"change bg-warning" ).removeClass( "bg-primary" );
		}

		$target.parents().find( data.component.detailLog.selector ).removeClass(
			"d-flex" );
		$target.parents().find( ".w-input-form" ).removeClass( "w-input-form" );

		$target.parent().removeClass( "on" ).html( nl2brDetailLog( inputVal ) );

	}

	/**
	 * テキストをBRタグ付きHTMLに変換
	 *
	 * @param {String}
	 *            txt テキスト
	 * @return {String} HTML
	 */
	function nl2brDetailLog(txt) {
		var $tmp = $('<p></p>').text(txt);
		var addHtml = $tmp.html();

		addHtml = addHtml.replace(/\r?\n/g, "<br>\n");

		return addHtml;
	}

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
			log.speechLogDetailId = $( element ).attr( "data-speech-log-detail-id" );
			log.updateDate = $( element ).attr( "data-speech-log-detail-date" );
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
			_view.infoDialog( _prop.getMessage( "speech.error.partialError" ) );

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

					var $target = $( data.component.log.selector ).find( "[data-speech-log-detail-id='" +detailId + "']" );

					if ( $target ) {

						$target.removeClass( "change bg-warning" );
						$target.attr({"data-speech-log-detail-date" : log.updateDate});
						$target.find( data.component.detailTxt.selector ).html( nl2brDetailLog( log.log ) );
					}
				}
			});
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * ファイル出力ボタン押下
	 */
	function _exportFile( event ) {

		var $edit = $( data.component.log.selector ).find( ".change" );

		if ( $edit && $edit.length > 0 ) {

			_view.confirmDialog( _prop.getMessage( "speech.exportEditingConfirm" ), { ok : _downloadOk} );

		} else {

			_view.confirmDialog( _prop.getMessage( "speech.exportConfirm" ), { ok : _downloadOk} );
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

		data.speechLogId = speechLogId;

		// ダイアログ
		var dialog = _prop.getProperty( "layout.dialog.selector" );

		// ダイアログ閉じたイベント
		$( dialog ).on( "hidden.bs.modal", function () {

			$( dialog ).off( "hidden.bs.modal" );

			data.component.loading.$element.show();

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
	 *  音声解析ログ音声ファイル生成 API エラー処理.
	 *
	 * @param {jqXHR} xhr
	 * @param {String} status エラー理由
	 * @param {String} errorThrown HTTP エラー内容
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _generateFileError( xhr, status, errorThrown, option ) {

		$( ".modal-backdrop" ).remove();
		data.component.loading.$element.hide();

		data.fileId = null;

		var msgList = [ _prop.getMessage( "speech.error.generateError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( "(" + msg + ")" );
		}

		_dispError( msgList );
	}

	/**
	 * 音声解析ログ音声ファイル生成 API 正常終了処理.
	 *
	 * @param {Object} response レスポンス内容（JSON をパースしたオブジェクト）
	 * @param {String} status 通信結果
	 * @param {jqXHR} xhr
	 * @param {Object} API 呼び出し時に使用した option オブジェクト
	 */
	function _generateFileSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok || _util.isEmpty( response.fileId ) ) {

			// ファイルマージエラー時はダイアログ表示後、ダウンロードへ進む
			if ( response.resultList[0].code === _prop.getProperty( "common.apiResponse.speech_log_dwonload_fail_merge" ) ) {

				$( ".modal-backdrop" ).remove();
				data.component.loading.$element.hide();

				data.fileId = response.fileId;

				// 再度確認ダイアログ表示
				_view.confirmDialog( _prop.getMessage( "speech.error.generateMergeError" ), { ok : _download } );

			} else {

				_generateFileError( xhr, status, null, option );
			}

			return;
		}

		$( ".modal-backdrop" ).remove();
		data.component.loading.$element.hide();

		data.fileId = response.fileId;

		// 正常終了
		if ( response.resultList[0].code === _prop.getProperty( "common.apiResponse.success" ) ) {

			_download();

			return;
		}

		// 部分エラーの場合は再度確認ダイアログ表示
		_view.confirmDialog( _prop.getMessage( "speech.error.generatePartialError" ), { ok : _download } );
	}

	/**
	 * / ダウンロード
	 */
	function _download() {

		var url = _prop.getApiMap( "speechLog.downloadFileByUser" );

		if ( _util.isNotEmpty( url ) && _util.isNotEmpty( data.speechLogId ) && _util.isNotEmpty( data.fileId ) ) {

			url += "/" + data.speechLogId + "/" + data.fileId;

			_api.download( url );

			data.fileId = null;
		}

		// ダイアログを閉じる
		return true;
	}

	/**
	 * 一時停止
	 */
	function pauseSpeech() {

		if ( data.inProgress ) return false; // 開始中／終了中なので何もしない

		if ( $( data.component.speech.selector ).prop( 'hidden' ) ) {
			// マイク入力中でない場合、何もしない
			return false;
		}

		if ( _util.isEmpty( data.analyzeInfo )){
			// IDが取れない場合、何もしない
			return false;
		}

		// 一時停止ダイアログ表示
		_pauseSpeechDialog( _prop.getMessage( "speech.pauseSpeech" ),
							 {	btn1 : _resumeSpeechFromPause,
								btn2 : _endSpeechFromPause});

		// 一時停止API
		_pause();

		data.inProgress = false;
	}

	/**
	 * 一時停止から再開
	 */
	function _resumeSpeechFromPause() {

	if ( data.inProgress ) return false; // 開始中／終了中なので何もしない

		_resume();

		data.inProgress = true;
	}

	/**
	 * 一時停止から終了
	 */
	function _endSpeechFromPause() {
		_dialogCloseOp(data.component.pauseSpeechDialog.selector); // すぐ閉じる

		if ( !_util.isEmpty( data.analyzeInfo.token ) ) {
			data.analyzeInfo.token = null;
		}
		if ( !_util.isEmpty( data.analyzeInfo.uuid ) ) {
			data.analyzeInfo.uuid = null;
		}

		endSpeechDisplay();

		return false;
	}

	/**
	 * 一時停止API
	 */
	function _pause() {
		data.mic.stop();

		// 送信データ生成
		var form = {
			token : data.analyzeInfo.token,
			uuid: data.analyzeInfo.uuid,
			voiceId : data.analyzeInfo.voiceId
		};

		// API 送信
		var url = _prop.getApiMap( "speech.end" );
		var json = JSON.stringify( form );

		var option = {
			handleError : function () {}, // 何もしない
			handleSuccess : _onResultProcess
		};

		_api.postJSONSync( url, json, option );

	}

	/**
	 * 音声解析再開
	 */
	function _resume() {

		if ( _util.isEmpty( data.analyzeInfo ) || _util.isEmpty( data.speechLogId )) {
			return false;
		}

		// 送信データ生成
		var form = {
				speechLogId: data.speechLogId
		};

		var url = _prop.getApiMap( "speech.resume" );
		var json = JSON.stringify( form );

		var option = {
			handleError : _resumeError,
			handleSuccess : _resumeSuccess
		};

		_api.postJSON( url, json, option );
	}

	/**
	 * (3)	音声解析再開API用エラーハンドラ
	 */
	function _resumeError( xhr, status, errorThrown, option ) {

		data.inProgress = false;
		data.resumeErrFlg = true;

		var $dialog = $( data.component.pauseSpeechDialog.selector );
		var $msg = $dialog.find( _prop.getProperty( "layout.dialogMessage.selector" ) );
		$msg.html( _prop.getMessage( "speech.error.resumeError" ) );
		$msg.addClass( "text-error" );

	}

	/**
	 * (3)	音声解析再開API用成功ハンドラ
	 */
	function _resumeSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {
			_resumeError( xhr, status, null, option );
			return;
		}

		data.analyzeInfo.token = response.token;
		data.analyzeInfo.uuid = response.uuid;
		data.analyzeInfo.voiceId = 1;

		// 音声解析再開
		_resumeAnalyze();

	}

	/**
	 * 解析再開
	 */
	function _resumeAnalyze(){

		var mic = null;
		if (_util.isEmpty( data.mic )) {
			mic = new Mic(data.buffersize);
		} else {
			mic = data.mic;
		}

		if (_util.isEmpty( data.arm )) {
			data.arm = new AnalyzeResultManager();
			var speechContents = $(data.component.log.selector).val().trim();
			if (!_util.isEmpty( speechContents )) {
				data.arm.result.push( speechContents );
			}
		}

		data.inProgress = false;

		_dialogCloseOp(data.component.pauseSpeechDialog.selector);

		// 送信データ生成
		var form = {
		};

		// 解析処理
		_analyze(mic, form);

	}

	// -------------------------------------------------------------------------
	// ダイアログ
	// -------------------------------------------------------------------------
	/**
	 * @param message {String} 表示メッセージ
	 * @param option {Object} 表示オプション
	 *
	 * option は、ハンドラ内で $dialog.data( "option" ) として取得可能
	 */
	function _pauseSpeechDialog( message, option ) {

		return _dialogOp( "pauseSpeech", message, option );
	}

	/**
	 * @private
	 * @param message {String} 表示メッセージ
	 * @param option {Object} 表示オプション
	 *
	 * @return {Element} ダイアログ要素
	 */
	var _dialogOptionName = "option";
	function _dialogOp( type, message, option ) {

		var $dialog = "";
		if ( type === "pauseSpeech" ) {
			$dialog = $( data.component.pauseSpeechDialog.selector );
		} else {
			return false;
		}

		var $msg = $dialog.find( data.component.dialogMessage.selector );
		message = _util.escapeHTML( message );
		message = message.replace( /\n|\r\n|\r/, "<br/>" );
		$msg.html( message );

		if ( type === "pauseSpeech" ) {
			$msg.removeClass( "text-error" );
		}

		$dialog.data( _dialogOptionName, option || null );

		if ( ! _dialogIsOpenOp($dialog) ) $dialog.modal();

		return $dialog[0];
	}

	function _dialogIsOpenOp( selector ) {

		var $dialog = _getDialogOp( selector );

		return $dialog.hasClass( "show" );
	}

	function _dialogCloseOp( selector ) {

		if ( _dialogIsOpenOp(selector) ) {

			var $dialog = _getDialogOp(selector);
			$dialog.modal( "hide" );
		}
	}

	function _getDialogOp( selector ) {
		return $( selector );
	}

	function _setComponentOp( selector ) {

		// ダイアログ
		var $dialog = $( selector );

		$dialog.data( "keyboard", false );			// ESC キーによるクローズを禁止
		$dialog.data( "backdrop", "static" );		// 余白のクリックで閉じない
		$dialog.data( _dialogOptionName, null );
	}

	/**
	 * @private
	 */
	function _dialogBtn1Handler( event ) {

		var $dialog = $( event.target ).closest( data.component.dialog.selector );

		var close = true;

		if ( $dialog.data( _dialogOptionName ) && $dialog.data( _dialogOptionName ).btn1 &&
			_util.isFunction( $dialog.data( _dialogOptionName ).btn1 ) ) {

			close = $dialog.data( _dialogOptionName ).btn1( event );
		}

		if ( close && _dialogIsOpenOp($dialog) ) {

			$dialog.modal( "hide" );
		}

	}

	/**
	 * @private
	 */
	function _dialogBtn2Handler( event ) {

		var $dialog = $( event.target ).closest( data.component.dialog.selector );

		var close = true;

		if ( $dialog.data( _dialogOptionName ) && $dialog.data( _dialogOptionName ).btn2 &&
			_util.isFunction( $dialog.data( _dialogOptionName ).btn2 ) ) {

			close = $dialog.data( _dialogOptionName ).btn2( event );
		}

		if ( close && _dialogIsOpenOp($dialog) ) {

			$dialog.modal( "hide" );

			// 再開エラー発生しない場合
			if ( data.resumeErrFlg == false ) {
				endSpeechDisplay();
			} else {
				// 利用時間表示更新を停止
				clearInterval(data.timer);
			}
		}
	}

}); // end of ready-handler