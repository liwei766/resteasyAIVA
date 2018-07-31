"use strict";

/*
 * 共通の画面制御処理（CCS.view）
 *
 * ※prop.js、util.js の後
 */

( function( CCS ) {

	var _prop = CCS.prop;
	var _util = CCS.util;

	var _dialogOptionName = "option";

	// -------------------------------------------------------------------------
	// 初期表示
	// -------------------------------------------------------------------------

	function _getTopUrl( loginInfo ) {

		return( _prop.getRedirectMap( "top.url" ) );
	}

	/**
	 * 指定されたセレクタの子要素について以下の処理を行う
	 * <ul>
	 * <li>権限反映
	 * </ul>
	 *
	 * @param {Object} selector JQueryセレクタ
	 */
	function _setComponent( selector ) {

		// 権限
		var $auth = CCS.auth;

		$auth.authApply( $( selector ) );

		// ダイアログHTMLパーツ適用
		var dialog = $( _prop.getProperty( "layout.dialog.selector" ) );

		if ( $( dialog ).length  > 0 ) {

			var $root = _prop.getProperty( "common.root" );

			$( dialog ).load( $root + "/ui/app/parts/dialog.html", null,

				function() {

				// ダイアログ

				var $dialog = $( _prop.getProperty( "layout.dialog.selector" ) );
				var $ok = $dialog.find( _prop.getProperty( "layout.dialogOk.selector" ) );
				var $cancel = $dialog.find( _prop.getProperty( "layout.dialogCancel.selector" ) );

				$dialog.data( "keyboard", false );			// ESC キーによるクローズを禁止
				$dialog.data( "backdrop", "static" );		// 余白のクリックで閉じない
				$dialog.data( _dialogOptionName, null );

				$ok.on( "click", _dialogOkHandler );
				$cancel.on( "click", _dialogCancelHandler );

			});
		}

	}

	// -------------------------------------------------------------------------
	// ダイアログ
	// -------------------------------------------------------------------------

	/**
	 * エラーダイアログ表示.
	 *
	 * @param message {String|String[]} エラーメッセージ
	 * @param option {Object} 表示オプション
	 * 		ok : OK ハンドラ（「閉じる」押下時の処理）
	 *
	 * option は、ハンドラ内で $dialog.data( "option" ) として取得可能
	 */
	function _errorDialog( message, option ) {

		return _dialog( "error", message, option );
	}

	/**
	 * 通知ダイアログ表示.
	 *
	 * @param message {String|String[]} エラーメッセージ
	 * @param option {Object} 表示オプション
	 * 		ok : OK ハンドラ（「閉じる」押下時の処理）
	 *
	 * option は、ハンドラ内で $dialog.data( "option" ) として取得可能
	 */
	function _infoDialog( message, option ) {

		return _dialog( "info", message, option );
	}

	/**
	 * 確認ダイアログ表示.
	 *
	 * @param message {String|String[]} エラーメッセージ
	 * @param option {Object} 表示オプション
	 * 		ok : OK ハンドラ
	 * 		cancel : キャンセルハンドラ
	 *
	 * option は、ハンドラ内で $dialog.data( "option" ) として取得可能
	 */
	function _confirmDialog( message, option ) {

		return _dialog( "confirm", message, option );
	}

	/**
	 * ダイアログ準備.
	 *
	 * @private
	 * @param message {String|String[]} 表示メッセージ
	 * 	<ul>
	 * 		<li>メッセージ内の改行は <br/> として表示する</li>
	 * 		<li>配列の場合、2 つめ以降のメッセージは箇条書きとして表示する</li>
	 * 	</ul>
	 * @param option {Object} 表示オプション
	 * 		ok : OK ハンドラ（ハンドラが true を返すとダイアログを閉じる. false を返すと閉じない）
	 * 		cancel : キャンセルハンドラ（ハンドラが true を返すとダイアログを閉じる. false を返すと閉じない）
	 *
	 */
	function _dialog( type, message, option ) {

		var dialogParts = _prop.getProperty( "layout.dialogParts.selector" );

		if ( $( dialogParts ).length > 0 ) {

			_openDialog( type, message, option );

		} else {

			var $dialog = $( _prop.getProperty( "layout.dialog.selector" ) );

			if ( $dialog.length > 0 && _util.isNotEmpty( $dialog[0] ) ) {

				var $target = $dialog[0];

				var mo = new MutationObserver(

					function () {

						this.disconnect();

						_openDialog( type, message, option );
					}

				);

				mo.observe( $target, { childList: true } );
			}

		}

	}

	/**
	 * ダイアログ表示共通処理.
	 *
	 * @private
	 * @param message {String|String[]} 表示メッセージ
	 * 	<ul>
	 * 		<li>メッセージ内の改行は <br/> として表示する</li>
	 * 		<li>配列の場合、2 つめ以降のメッセージは箇条書きとして表示する</li>
	 * 	</ul>
	 * @param option {Object} 表示オプション
	 * 		ok : OK ハンドラ（ハンドラが true を返すとダイアログを閉じる. false を返すと閉じない）
	 * 		cancel : キャンセルハンドラ（ハンドラが true を返すとダイアログを閉じる. false を返すと閉じない）
	 *
	 * @return {Element} ダイアログ要素
	 */
	function _openDialog( type, message, option ) {

		// コンポーネント取得

		var $dialog = $( _prop.getProperty( "layout.dialog.selector" ) );

		$dialog.data( _dialogOptionName, option || null );

		var $ok = $dialog.find( _prop.getProperty( "layout.dialogOk.selector" ) );
		var $msg = $dialog.find( _prop.getProperty( "layout.dialogMessage.selector" ) );
		var $title = $dialog.find( _prop.getProperty( "layout.dialogTitle.selector" ) );
		var $cancel = $dialog.find( _prop.getProperty( "layout.dialogCancel.selector" ) );

		// メッセージ設定

		var msgList = []; // 箇条書きで表示するメッセージ

		if ( ! _util.isArray( message ) ) {
			message = [ message ];
		}
		else {
			if ( message.length > 1 ) {

				msgList = message.slice( 1, message.length );
			}
		}

		message = _util.escapeHTML( message[0] );
		message = message.replace( /\n|\r\n|\r/g, "<br/>" );
		$msg.html( message );

		if ( msgList.length > 0 ) {
			var $list = $( "<ul></ul>" );
			$.each( msgList, function ( i, msg ) {
				$( "<li></li>" ).html( msg.replace( /\n|\r\n|\r/g, "<br/>" ) ).appendTo( $list );
			});
			$list.appendTo( $msg );
		}

		// ダイアログ種別ごとの設定

		if ( type === "confirm" ) {

			$title.text( "確認" );
			$msg.removeClass( "text-error" );
			$ok.removeClass( "btn-danger" ).text( "OK" );
			$cancel.removeAttr( "hidden" );
		}
		else if ( type === "info" ) {

			$title.text( "お知らせ" );
			$msg.removeClass( "text-error" );
			$ok.removeClass( "btn-danger" ).text( "閉じる" );
			$cancel.attr( "hidden", "" );
		}
		else { // エラーとみなす

			$title.text( "エラー" );
			$msg.addClass( "text-error" );
			$ok.addClass( "btn-danger" ).text( "閉じる" );
			$cancel.attr( "hidden", "" );
		}

		// 表示

		if ( ! _dialogIsOpen() ) $dialog.modal();

		return $dialog[0];
	}

	/**
	 * ダイアログの OK ボタン押下処理.
	 *
	 * @private
	 * @param {Event} event イベント
	 */
	function _dialogOkHandler( event ) {

		var $dialog = $( event.target ).closest( _prop.getProperty( "layout.dialog.selector" ) );

		var close = true;

		if ( $dialog.data( _dialogOptionName ) && $dialog.data( _dialogOptionName ).ok &&
			_util.isFunction( $dialog.data( _dialogOptionName ).ok ) ) {

			close = $dialog.data( _dialogOptionName ).ok( event );
		}

		if ( close && _dialogIsOpen() ) {

			$dialog.modal( "hide" );
		}
	}

	/**
	 * ダイアログのキャンセルボタン押下処理.
	 *
	 * @private
	 * @param {Event} event イベント
	 */
	function _dialogCancelHandler( event ) {

		var $dialog = $( event.target ).closest( _prop.getProperty( "layout.dialog.selector" ) );

		var close = true;

		if ( $dialog.data( _dialogOptionName ) && $dialog.data( _dialogOptionName ).cancel &&
			_util.isFunction( $dialog.data( _dialogOptionName ).cancel ) ) {

			close = $dialog.data( _dialogOptionName ).cancel( event );
		}

		if ( close && _dialogIsOpen() ) {

			$dialog.modal( "hide" );
		}
	}

	/**
	 * ダイアログが表示中か調べる.
	 *
	 * @private
	 * @return {boolean} true : 表示中　　false : 表示していない
	 */
	function _dialogIsOpen() {

		var $dialog = _getDialog();

		return $dialog.hasClass( "show" );
	}

	/**
	 * ダイアログを閉じる（ダイアログが表示されていなければ何もしない）.
	 */
	function _dialogClose() {

		if ( _dialogIsOpen() ) {

			var $dialog = _getDialog();
			$dialog.modal( "hide" );
		}
	}

	/**
	 * ダイアログ要素を取得.
	 *
	 * @return {jQuery} ダイアログ要素.
	 */
	function _getDialog() {

		return $( _prop.getProperty( "layout.dialog.selector" ) );
	}

	// -------------------------------------------------------------------------
	// フォーム操作処理
	// -------------------------------------------------------------------------

	/**
	 * 指定したセレクタのフォーム値をオブジェクトで取得する
	 * キー名は対象入力フォームのname属性の値。「.」で区切られていれば前半を親プロパティ名、後半を子プロパティ名としたオブジェクト階層を掘る。
	 * ※2階層までを対応しており、3階層以上は未対応
	 * name=xxx value=aaaの場合
	 * {xxx : "aaa"}
	 * name=xxx.yyy value=aaaの場合
	 * {xxx : {yyy : "aaa"}}
	 *
	 * @param {String} selector セレクタ
	 * @return {Object} フォームオブジェクト
	 */
	function _getFormObject( selector ) {

		// ----- フォームの値をオブジェクトに格納

		var fields = $(selector).serializeArray();

		// ----- 未取得の項目に null を設定

		/*
		 * serializeArray() では以下の項目ついては取得できない
		 * ・checkbox,radio,multiple の select の値が未設定のもの
		 * ・disabled の項目
		 * これらについてはオブジェクトに null を設定する
		 */

		// 取得済み項目の項目名の配列を作成（重複を排除）

		var fieldNames = {};
		$.each( fields, function () {
			fieldNames[ this.name ] = true;
		});

		// 未取得（かもしれない）項目の配列を取得

		var elem = $(selector).find(
			"input[type=checkbox],input[type=radio],select[multiple]," +
			"input:disabled,select:disabled,textarea:disabled" );

		// 未取得項目をフォームオブジェクトに null として追加

		$.each( elem, function () {
			var name = $(this).attr( "name" );
			if ( fieldNames[ name ] ) return true ; // 取得済みなので continue
			fields.push( { name : name, value : null } );
		});

		var obj = {};
		$.each( fields, function ( i, field ) {

			// 対象フォームのname
			var name = field.name;

			// 対象フォームのvalue
			var value = field.value;

			// nameの「.」のindex
			var dotIndex = name.lastIndexOf(".");

			// nameの「.」より前
			var parentObj;

			// nameの「.」より後
			var childName;

			// 配列として扱うか
			var arrayFlag = false;

			// 対象フォームの JQuery エレメント
			var inputElement = $(selector).find( ":input[name='" + name + "']" ); //:inputでselect,textarea取得可能

			// 対象フォームのデータタイプ
			var datatype = inputElement.attr( "data-ns-datatype" );

			/*
				デフォルトでは trim 処理は行いません。
				必要な場合は個別に作成してください。（以下のコードは参考です。）

			// type=password, テキストエリア 以外はtrimする
			if ( inputElement.attr( "type" ) !== "password" && inputElement[0].type !== "textarea" ) {
				value = $.trim( value );
			}
			*/

			// ----- 配列判定

			// isArrayが設定されていたら、タグに関係なく配列とみなす
			if ( inputElement.is("input[isArray='true'],textarea[isArray='true'],select[isArray='true']" ) ) {
				arrayFlag = true;
			}
			//isArrayにfalseが設定されていたらradioでもmultipleなselectであっても配列としない
			else if ( inputElement.is("input[isArray='false'],textarea[isArray='false'],select[isArray='false']" ) ) {
				arrayFlag = false;
			}

			// ラジオボタン以外で同じ name が複数存在するか、multiple の select ならば配列とみなす
			else if ( !inputElement.is("input[type=radio]") &&
				inputElement.length > 1 ||
				inputElement.is( "select" ) && inputElement.attr( "multiple" ) ) {
				arrayFlag = true;
			}

			// ----- データタイプごとの変換

			value = _convertForm2API( datatype, value );

			// datatype=disabled時はundefinedが返るのでループをcontinueする
			if ( value === undefined ) {
				return true; //continue
			}

			// 値が入力されていない場合、nullとして扱いたいため、nullを設定する
			if ( _util.isEmpty( value ) ) {
				value = null;
			}

			//「.」で区切られていた場合、親プロパティ名を付加する

			parentObj = obj;

			if (dotIndex > -1) {
				parentObj = _util.createParents( parentObj, name );
				childName = name.substring(dotIndex + 1);
			} else {
				childName = name;
			}

			_add( parentObj, childName, value, arrayFlag );
		});

		return obj;
	}

	// objへ追加する関数定義
	function _add( o, prop, val, isArg ) {
		//配列であり、かつnullでなければpushする(初回は配列を生成)
		if ( isArg ) {
			if ( _util.isArray( o[ prop ] ) ) {
				if ( val !== null ) {
					o[prop].push(val);
				}
			//まだ配列が生成されていない場合(初回)
			} else {
				if ( val !== null ) {
					o[ prop ] = [ val ];
				} else {
					o[ prop ] = null;
				}
			}
		} else {
			o[ prop ] = val;
		}
	}

	/**
	 * 指定されたデータタイプにあわせ、値を変換する
	 * 変換できない場合はnullを返す(booleanは1以外はかならずfalse, disabledはundefined)
	 * @param {String} datatype データタイプ
	 * @param {String} value 変換前データ
	 * @return {String|Number}変換後の値※型はdatatypeによってそれぞれ
	 */
	function _convertForm2API( datatype, value ) {
		if (_util.isEmpty(datatype)) {
			return value;
		}
		try {
			switch (datatype) {
			case "num":
				var num = parseInt(value.replace( /,/g, "" ), 10);
				if (!isNaN(num)) { //数字であれば
					return num;
				} else {
					return null;
				}
				break;

			case "boolean":
				if (value === "1" || value === "true") {
					return true;
				} else {
					return false;
				}
				break;

			case "second":
				var sec = _util.time2Number( value );
				if (!isNaN(sec)) { //数字であれば
					return sec;
				} else {
					return null;
				}
				break;

			case "date":
				var date = _util.dateFormat( value, "yyyy/mm/dd HH:MM:ss" );
				return !_util.isEmpty(date) ? date : null;
				// break;

			case "dateFrom":
				var dateFrom = _util.dateFormat( _util.startOfDay( value ), "yyyy/mm/dd HH:MM:ss" );
				return _util.isNotEmpty(dateFrom) ? dateFrom : null;
				// break;

			case "dateTo":
				var dateLast = _util.dateFormat( _util.endOfDay( value ), "yyyy/mm/dd HH:MM:ss" );
				return _util.isNotEmpty(dateLast) ? dateLast : null;
				// break;

			case "disabled":
				return undefined;
				// break;

			default:
				return value;
				// break;
			}
		} catch (e) {
			return null;
		}
	}

	/**
	 * フォームの内容をリセットする
	 * @param {JQuery} formElement 対象フォームのJQueryオブジェクト
	 */
	function _formReset( formElement ) {
		window.document.forms[formElement.attr("name")].reset();
	}

	// -----------------------------------------------------------------------------
	// 公開情報
	// -----------------------------------------------------------------------------

	var ret = {

		name : "CCS.view",

		// -------------------------------------------------------------------------
		// 初期表示
		// -------------------------------------------------------------------------

		getTopUrl : _getTopUrl,

		setComponent : _setComponent,

		// -------------------------------------------------------------------------
		// ダイアログ
		// -------------------------------------------------------------------------

		errorDialog : _errorDialog,

		confirmDialog : _confirmDialog,

		infoDialog : _infoDialog,

		dialogClose : _dialogClose,

		getDialog : _getDialog,

		// -------------------------------------------------------------------------
		// フォーム操作処理
		// -------------------------------------------------------------------------

		getFormObject : _getFormObject,

		convertForm2API : _convertForm2API,

		formReset : _formReset,

	};	// end of ret

	CCS.view = ret;

} )( CCS ) ;

