"use strict";

/*
 * 共通処理（CCS.util）
 *
 * ※ccs.js より後
 * ※CCS.prop, CCS.user を使用している一部の処理は、関連スクリプトのロード後に利用可能
 *
 * 必須モジュール：jquery.validate.js, date.format.js
 */
( function( CCS ) {

	var _debug = CCS.debug;

	// グローバルオブジェクト参照

	var storage = window.sessionStorage;

	var storageNamespace = "ccs.";		// キー名のプリフィクス

	// ログ出力方式（IE とそれ以外）
	var normallog = window.console && ( typeof window.console.log === 'function' );

	function _log() {

		// IE は初回の判定で undefined になるようです
		if ( normallog === undefined ) normallog = window.console && ( typeof window.console.log === 'function' );

		if ( normallog ) window.console.log.apply( window.console, arguments );
		else if ( normallog === false ) Function.prototype.apply.call( window.console.log, window.console, arguments );
		else ;
	}

	// -------------------------------------------------------------------------
	// 各種判定関数
	// -------------------------------------------------------------------------

	/**
	 * 未定義値であるかチェックする
	 * nullの場合も未定義として扱う（true を返す）
	 *
	 * @param value チェックする値
	 * @return {boolean} true:未定義値である false:未定義値でない
	 */
	function _isUndefined( value ) {
		return ( ( typeof value === "undefined" ) || ( value === null ) );
	}

	/**
	 * 未定義値でないかチェックする（_isUndefined の逆）
	 *
	 * @param value チェックする値
	 * @return {boolean} true:未定義値である false:未定義値でない
	 */
	function _isNotUndefined( value ) { return ! _isUndefined( value ); }

	/**
	 * 数値であるかチェックする
	 *
	 * @param value チェックする値
	 * @return {boolean} true:数値である false:数値でない
	 */
	function _isNumber( value ) {
		return ( typeof value === "number" && isFinite( value ) );
	}

	/**
	 * 0 以上の整数であるかチェックする
	 *
	 * @param value チェックする値
	 * @return {boolean} true:0 以上の整数である false:負数
	 */
	function _isPositiveInt( value ) {
		var positiveIntPattern = new RegExp(/^(0|[1-9][0-9]*)$/);
		return _isNumber( value ) && positiveIntPattern.test( value );
	}

	/**
	 * Object であるかチェックする
	 * Array と null はオブジェクト扱いしない（false を返す）
	 *
	 * @param value チェックする値
	 * @return {boolean} true:Objectである false:Objectでない
	 */
	function _isObject(value) {
		return ( ! _isArray( value ) && ( value !== null ) && ( typeof value === "object" ) );
	}

	/**
	 * 配列であるかチェックする
	 *
	 * @param value チェックする値
	 * @return {boolean} true:配列である false:配列でない
	 */
	function _isArray( value ) { return $.isArray( value ); }

	/**
	 * 関数であるかチェックする
	 *
	 * @param value チェックする値
	 * @return {boolean} true:関数である false:関数でない
	 */
	function _isFunction( value ) { return $.isFunction( value ); }

	/**
	 * 空であるかチェックする
	 * ・undefine または null の場合に空と判定する
	 * ・文字列の場合、trim() の結果が空文字列の場合も空と判定する
	 * ・配列の場合、length が 1 未満の場合も空と判定する
	 *
	 * @param value チェックする値
	 * @return {boolean} true:空数である false:空でない
	 */
	function _isEmpty( value ) {
		if ( _isUndefined( value ) ) { return true; }
		if ( ( typeof value === "string" ) && $.trim( value ) === "" ) { return true; }
		if ( _isArray( value ) && value.length < 1 ) { return true; }
		return false;
	}

	/**
	 * 空でないかチェックする（isEmpty の逆）
	 *
	 * @param value チェックする値
	 * @return {boolean} true:空でない false:空である
	 */
	function _isNotEmpty( value ) { return ! _isEmpty( value ); }

	/**
	 * 文字列の先頭が指定した文字列であるかチェックする
	 *
	 * @param {String} str
	 * @param {String} prefix
	 * @return true:文字列の先頭が指定した文字列である  false:それ以外
	 */
	function _startsWith( str, prefix ) {

		if ( _isUndefined( str ) ) { return false; }
		return str.substring( 0, prefix.length ) === prefix;
	}

	/**
	 * 文字列の末尾が指定した文字列であるかチェックする
	 *
	 * @param {String} str
	 * @param {String} suffix
	 * @return true:文字列の末尾が指定した文字列である  false:それ以外
	 */
	function _endsWith( str, suffix ) {

		if ( _isUndefined( str ) ) { return false; }
		return str.substring( str.length - suffix.length ) === suffix;
	}

	// -------------------------------------------------------------------------
	// 文字列操作
	// -------------------------------------------------------------------------

	var _escapeRegex = new RegExp(/[&"<>'`]/g);

	var _escapeRules = {
		"&": "&amp;",
		'"': "&quot;",
		"<": "&lt;",
		">": "&gt;",
	    "'": '&#x27;',
	    '`': '&#x60;',
	};

	/**
	 * Unicode コードポイントの数を返す
	 *
	 * @param {String} str チェックする文字列
	 * @return {Number} コードポイント数
	 */
	function _codePointCount( str ) {

		var len = str.length;
		var nSurrogate = str.split( /[\uD800-\uDBFF][\uDC00-\uDFFF]/g ).length - 1;

		return len - nSurrogate;
	}

	// サロゲートペアを含むか調べる
	function _hasSurrogate( str ) {

		return ( /[\uD800-\uDBFF][\uDC00-\uDFFF]/ ).test( str );
	}

	// -------------------------------------------------------------------------

	/**
	 * 文字列の{n}を[params]で置き換える
	 * 例） xxx.format("{0},{1}です", ["A","B"]); //「A,Bです」が返る
	 * @param {Object} str 文字列
	 * @param {String[]} [params] 置き換え文字
	 */
	function _format( str, params ) {

		if ( _isUndefined( str ) ) { return ""; }

		if ( _isUndefined( params ) ) {
			return str;
		} else if ( ! this.isArray( params ) ) {
			params = [ params ];
		}

		$.each( params, function ( i, param ) {
			str = str.replace( new RegExp( "\\{" + i + "\\}", "g" ), function() {
				return _isUndefined( param ) ? "" : param;
			} );
		} );

		return str;
	}

	/**
	 * HTMLエスケープする
	 * @param {String} str 文字列
	 * @return {String} エスケープ後文字列
	 */
	function _escapeHTML( str ) {
		if ( _isEmpty( str ) ) { return ""; }

		return str.replace( _escapeRegex, function ( c ) {
			return _escapeRules[c];
		});
	}

	/**
	 * キャメルケース表記をスネークケース表記(小文字)に変換
	 * @param {String} str キャメルケース表記文字列
	 * @return {String} スネークケース表記
	 */
	function _camel2Snake(str) {
		var regex = new RegExp(/([a-z])([A-Z])/g); //「aaaBbb」を「aaa_Bbb」に変換
		var regex2 = new RegExp(/([A-Z]+)([A-Z][a-z])/g); //「DDDEee」を「DDD_Eee」に変換

		return str
			.replace( regex, "$1_$2" )
			.replace( regex2, "$1_$2" )
			.toLowerCase();
	}

	// -------------------------------------------------------------------------
	// 日付／時刻操作
	// -------------------------------------------------------------------------

	/**
	 * Date オブジェクトまたは yyyy/MM/dd hh:mm:ss 形式の文字列を Date に変換
	 * @private
	 * @param {String|Date} src
	 */
	function _asDate( src ) {

		var date = src;
		var msec = 0;
		var msecPatten = /\.(\d{3})$/;

		if ( typeof( src ) === "string" ) {

			if ( _isEmpty( src ) ) { return null; }

			if ( msecPatten.test( src ) ) msec = msecPatten.exec( src )[1] - 0;
			date = new Date( ( new Date( src.replace( msecPatten, "" ) ) ).getTime() + msec );
		}

		return date;
	}

	/**
	 * Date オブジェクトまたは yyyy/MM/dd hh:mm:ss 形式の文字列を
	 * 指定フォーマットに変換する（date.format.jsを使用）
	 *
	 * !!! CallCenterSolution では date.format.js を使用していないので
	 * !!! 変換結果のフォーマットは常に yyyy/MM/dd hh:mm:ss 形式
	 *
	 * @param {String} src Date オブジェクトまたは yyyy/MM/dd hh:mm:ss 形式の日付文字列
	 * @param {String} format 変換後のフォーマット
	 * @return {String} 指定フォーマットに変換した文字列
	 */
	function _dateFormat( src, format ) {

		var _prop = CCS.prop;

		var date = _asDate( src );

		if ( _isUndefined( date ) ) { return ""; }

		return date.toLocaleString();

		 // !!! CallCenterSolution では date.format.js を使用していないので
		 // !!! 変換結果のフォーマットは常に yyyy/MM/dd hh:mm:ss 形式

		/*
		if ( _isEmpty( format ) ) {
			format = _prop.getProperty( "common.dateFormat.default" );
		}

		try {
			return window.dateFormat( date, format );
		} catch ( ex ) {
			return "";
		}
		*/
	}

	/**
	 * 指定された日付の時刻を 00:00:00 に設定する
	 * @param {String|Date} src
	 * @return {Date} 指定された日の時刻を 00:00:00 に設定した Date オブジェクト
	 */
	function _startOfDay( src ) {

		var date = _asDate( src );

		if ( _isUndefined( date ) ) { return null; }

		date.setHours( 0 );
		date.setMinutes (0 );
		date.setSeconds( 0 );

		return date;
	}

	/**
	 * 指定された日付の時刻を 23:59:59 に設定する
	 * @param {String|Date} src
	 * @return {Date} 指定された日の時刻を 23:59:59 に設定した Date オブジェクト
	 */
	function _endOfDay( src ) {

		var date = _asDate( src );

		if ( _isUndefined( date ) ) { return null; }

		date.setHours( 23 );
		date.setMinutes (59 );
		date.setSeconds( 59 );

		return date;
	}

	/**
	 * 数値型(秒)をHH:MM:SS形式の文字列に変換する
	 * 数値型でない場合は空文字を返す
	 * @param {Number} value 0 時を 0 とした秒数
	 * @return {String} 変換した文字列
	 */
	function _timeFormat( value ) {
		if (!this.isNumber( value )) {
			return "";
		}
		var result =
			("0" + parseInt((value / 3600), 10) + ":").slice(-3) +
			("0" + parseInt(((value % 3600) / 60), 10) + ":").slice(-3) +
			("0" + (value % 60)).slice(-2);
		return result;
	}

	/**
	 * 数値型を(秒 ※小数点以下はミリ秒)HH:MM:SS.mmm形式の文字列に変換する
	 * 数値型でない場合は空文字を返す
	 * @param {Number} value 変換する数値
	 * @return {String} 変換した文字列
	 */
	function _timeMiliFormat( value ) {
		if (!this.isNumber( value )) {
			return "";
		}
		//小数点以下を3桁固定にした文字列を得る(不足分は0埋め、過多分は四捨五入)
		var valueStr = value.toFixed(3);
		var result =
			//整数にして前半のフォーマットを得る
			this.timeFormat(parseInt(valueStr, 10)) +
			//小数部のみを加える
			valueStr.substring(valueStr.lastIndexOf("."));
		return result;
	}

	/**
	 * HH:MM:SS形式の文字列を数値型に変換する
	 * @param {String} value 変換する文字列
	 * @return {Number} 変換した数値
	 */
	function _time2Number( value ) {

		var vals = value.split( ":" );
		return parseInt( vals[0], 10 ) * 3600 + parseInt( vals[1], 10 ) * 60 + parseInt( vals[2], 10 );
	}

	// -------------------------------------------------------------------------
	// オブジェクト操作処理
	// -------------------------------------------------------------------------

	/**
	 * ピリオドで連結された文字列で示されたプロパティを取得
	 * @param {Object} obj
	 * @param {String} name
	 */
	function _resolve( obj, name ) {

		if ( _isEmpty( name ) ) { return obj; }

		var array = name.split( "." );
		var ret = obj;

		try {
			$.each( array, function( i, value ) {
				ret = ret[ value ];
			});
		}
		catch ( ex ) {
			ret = undefined;
		}

		return ret;
	}

	/**
	 * ピリオドで連結された文字列で示されたプロパティの親までを生成
	 * @param {Object} obj
	 * @param {String} name
	 */
	function _createParents( obj, name ) {

		if ( _isEmpty( name ) ) { return obj; }

		var array = name.split( "." );
		var ret = obj;

		array.pop(); // 末端のオブジェクトは作成しない

		$.each( array, function( i, value ) {

			if ( ! ret.hasOwnProperty( value ) ) {

				ret[ value ] = {};
			}

			ret = ret[ value ];
		});

		return ret;
	}

	// -------------------------------------------------------------------------
	// セッションストレージ操作
	// -------------------------------------------------------------------------

	function _keyname( key ) { return storageNamespace + key; }

	/**
	 * セッションストレージに値を保存する
	 * （Date 型を保存した場合、取り出し時には文字列になるので注意）
	 *
	 * @param {String} key セッションストレージキー名
	 * @param {Object} val セッションストレージ格納値
	 */
	function _setSession( key, val ) {
		if ( val === undefined ) val = null;
		storage.setItem( _keyname( key ), JSON.stringify( val ) );
	}

	/**
	 * セッションストレージから値を取り出す
	 *
	 * @param {String} key セッションストレージキー名
	 * @return {Object} セッションストレージ格納値
	 */
	function _getSession( key ) {
		key = _keyname( key );
		return storage.getItem( key ) ? JSON.parse( storage.getItem( key ) ) : null;
	}

	/**
	 * セッションストレージから値を取り出して削除する
	 *
	 * @param {String} key セッションストレージキー名
	 * @return {Object} セッションストレージ格納値
	 */
	function _flashSession( key ) {
		var val = _getSession( key );
		_removeSession( key );
		return val;
	}

	/**
	 * セッションストレージ上の指定されたキーの値をクリアする
	 *
	 * @param {String} key セッションストレージキー名
	 */
	function _removeSession( key ) {
		storage.removeItem( _keyname( key ) );
	}

	/**
	 * セッションストレージを全てクリアする
	 */
	function _clearSession() {
		storage.clear();
	}

	// -------------------------------------------------------------------------
	// 画面遷移
	// -------------------------------------------------------------------------

	/**
	 * ログアウトする
	 */
	function _logout() {

		// ログアウトAPIの呼び出し
		_callLogoutApi();

		// セッションストレージの値を破棄
		_clearSession();

		// ログイン画面に遷移する
		_redirect( _getLoginUrl() );
	}

	/**
	 * 指定先へリダイレクトする
	 * @param {String} url リダイレクト先URL
	 */
	function _redirect( url ) {

		var _prop = CCS.prop;
		var _user = CCS.user;

		var absUrl = url;
		if ( ! _startsWith( absUrl, "http:" ) &&
			! _startsWith( absUrl, "https:" ) &&
			! _startsWith( absUrl, "/" ) )
		{
			// IE は location.href による遷移時に base タグの設定を無視する
			absUrl = $( "base" ).attr( "href" ) + url;
		}

		if ( _debug.dialog ) {
			if ( ! window.confirm( "--- redirect OK ? ---\nURL : " + url + "\nabsolute URL : " + absUrl ) )
			{
				return;
			}
		}

		window.location.href = absUrl;
	}

	/**
	 * エラー画面にリダイレクトする
	 *
	 * @param {String|String[]} errorName エラー名
	 * @param {String} message エラーメッセージ（使用しない）
	 */
	function _redirectError( errorName, message ) {

		var _prop = CCS.prop;
		var _user = CCS.user;

		// 遷移先 URL 算出

		var errorInfo = _prop.getRedirectMap( "error." + errorName );
		if ( _isUndefined( errorInfo ) ) errorInfo = _prop.getRedirectMap( "error.error" );

		var url = errorInfo.url;

		// メッセージ設定

		if ( _isNotEmpty( message ) ) {

			_setSession( _prop.getProperty( "common.sessionKey.errorMessage" ), message );
		}

		// リダイレクト

		if ( _debug.dialog ) {
			if ( ! window.confirm( "--- error redirect OK ? ---\nURL : " + url ) )
			{
				return;
			}
		}

		window.location.href = url;
	}

	// -------------------------------------------------------------------------
	// その他
	// -------------------------------------------------------------------------

	/**
	 * 認証トークンを返す
	 * @return {String} 認証トークン
	 */
	function _getToken() {

		var _user = CCS.user;

		return _user ? encodeURIComponent( JSON.stringify( _user.getData() ) ) : "";
	}

	/**
	 * 現在表示画面の絶対パスを返す
	 * @return {String} 現在表示画面の絶対パス
	 */
	function _getPath () {
		return window.location.href;
	}

	/**
	 * 拡張子なしのパスを返す
	 *
	 * 例）pathname が http://xxx/yyy/zzz.html の場合、http://xxx/yyy/zzz を返す
	 * ※最後が「/」で終わる場合、～/index を返す
	 *
	 * @param {String} [pathname] 省略された場合は現在の URL を使用する
	 * @return 指定されたパスから拡張子を削除した文字列
	 */
	function _getNoExtPath( pathname ) {

		if ( _isEmpty( pathname ) ) { pathname = window.location.pathname; }

		if ( pathname.length === ( pathname.lastIndexOf("/") + 1 ) ) {
			pathname += "index";
		} else {
			var extLastIndex = pathname.lastIndexOf(".");
			if (extLastIndex > -1) {
				pathname = pathname.substring(0, extLastIndex);
			}

			var lastSlashx = pathname.lastIndexOf("/");
			if (lastSlashx > -1) {
				pathname += pathname.substring( lastSlashx );
			}
		}
		return pathname;
	}

	/**
	 * 拡張子無しの現在表示画面のファイル名を返す
	 *
	 * 例）http://xxx/yyy/zzz.html の場合
	 * zzz を返す
	 * ※最後が「/」で終わる場合、「index」 を返す
	 * @return {String} 拡張子無しの現在表示画面のファイル名
	 */
	function _currentModuleName () {
		var noExtPath = _getNoExtPath();
		var lastIndex = noExtPath.lastIndexOf("/");
		if (noExtPath.length > lastIndex) {
			return noExtPath.substring(lastIndex + 1);
		//「/」で終わっていれば空を返す(this.getNoExtPathの動作仕様上ありえないが)
		} else {
			return "";
		}
	}

	/**
	 * ログアウト処理
	 * 失敗しても共通処理としてハンドリングできないため、レスポンスは評価しない。
	 * （⇒システムエラー時にもこの関数は呼ばれるため）
	 */
	function _callLogoutApi () {

		var _prop = CCS.prop;

		// ログアウト API の URL
		var url = _prop.getApiMap( "common.login.logout" );

		// 通信オプション
		var ajaxOptions = {
			async: false,
			url: url,
			type: "POST"
		};

		// ログアウト API 呼び出し
		$.ajax( ajaxOptions );

		if ( _debug.dialog ) {
			window.alert( "LOGOUT" );
		}
	}

	/**
	 * ログイン URL 取得
	 * @return {String} ログインURL
	 */
	function _getLoginUrl () {

		var _prop = CCS.prop;
		return _prop.getRedirectMap( "login" ).url;
	}

	/**
	 * 指定されたURLでimgタグを生成する
	 * @param {String} src 画像のURL
	 * @param {Number} width 表示幅
	 * @param {Number} height 表示高
	 */
	function _generateImgTag (src, width, height) {
		var style = "";
		if (this.isNumber(width)) {
			style += "max-width : " + width + "px;";
		}
		if (this.isNumber(height)) {
			style += "max-height : " + height + "px;";
		}
		var imgTag = "<img src=\"{0}\" style=\"{1}\">";
		return this.format(imgTag, [this.escapeHTML(src), style]);
	}

	// *****************************************************************************
	// 公開オブジェクト
	// *****************************************************************************

	var ret = {

		name : "CCS.util",

		// -------------------------------------------------------------------------
		// 開発用
		// -------------------------------------------------------------------------

		log : _debug.log ? _log : $.noop,

		// -------------------------------------------------------------------------
		// 各種判定関数
		// -------------------------------------------------------------------------

		isUndefined : _isUndefined,
		isNotUndefined : _isNotUndefined,
		isNumber : _isNumber,
		isPositiveInt : _isPositiveInt,
		isObject : _isObject,
		isArray : _isArray,
		isFunction : _isFunction,
		isEmpty : _isEmpty,
		isNotEmpty : _isNotEmpty,

		startsWith : _startsWith,
		endsWith : _endsWith,

		// -------------------------------------------------------------------------
		// 文字列操作
		// -------------------------------------------------------------------------

		codePointCount : _codePointCount,
		hasSurrogate : _hasSurrogate,

		format : _format,

		escapeHTML : _escapeHTML,

		camel2Snake : _camel2Snake,

		// -------------------------------------------------------------------------
		// 日付／時刻操作
		// -------------------------------------------------------------------------

		dateFormat : _dateFormat,

		startOfDay : _startOfDay,
		endOfDay : _endOfDay,

		timeFormat : _timeFormat,
		timeMiliFormat : _timeMiliFormat,

		time2Number : _time2Number,

		// -------------------------------------------------------------------------
		// オブジェクト操作処理
		// -------------------------------------------------------------------------

		resolve : _resolve,
		createParents : _createParents,

		// -------------------------------------------------------------------------
		// セッションストレージ操作
		// -------------------------------------------------------------------------

		setSession : _setSession,
		getSession : _getSession,
		flashSession : _flashSession,
		removeSession : _removeSession,
		clearSession : _clearSession,

		// -------------------------------------------------------------------------
		// 画面遷移
		// -------------------------------------------------------------------------

		logout : _logout,
		redirect : _redirect,
		redirectError : _redirectError,

		// -------------------------------------------------------------------------
		// その他
		// -------------------------------------------------------------------------

		getToken : _getToken,

		getPath : _getPath,
		getNoExtPath : _getNoExtPath,
		currentModuleName : _currentModuleName,

		callLogoutApi : _callLogoutApi,
		getLoginUrl : _getLoginUrl,

		generateImgTag : _generateImgTag
	};

	CCS.util = ret;

})( CCS );
