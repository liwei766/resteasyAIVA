"use strict";

/**
 * 権限処理クラス（CCS.auth）
 */

( function( CCS ) {

	var _util = CCS.util;

	/** 現在のセッションの保持権限 */
	var _authList = [];

	/** 権限のルールを記述する属性名 */
	var _ruleAttrStr = "data-ccs-rule";

	/** 逆権限のルールを記述する属性名 */
	var _notRuleAttrStr = "data-ccs-notrule";

	/** 権限 OK 時に適用するクラス名を記述する属性名 */
	var _okclassAttrStr = "data-ccs-okclass";

	/** 権限 NG 時に適用するクラス名を記述する属性名 */
	var _ngclassAttrStr = "data-ccs-ngclass";

	/** 権限 OK 時に適用するクラス名のデフォルト値 */
	var _defaultOkCss = "";

	/** 権限 NG 時に適用するクラス名のデフォルト値 */
	var _defaultNgCss = "ccs-hidden";

	// -------------------------------------------------------------------------
	// 内部処理
	// -------------------------------------------------------------------------

	/**
	 * 保持権限の取得／設定.
	 * <ol>
	 * <li>パラメータが指定されていれば、保持権限として登録する</li>
	 * <li>現在の保持権限を返却する</li>
	 * </ol>
	 *
	 * @param {String[]|String} 現在のセッションの保持権限として登録する権限名（の配列）
	 */
	function _authListAccessor( value ) {

		if ( arguments.length > 0 ) {

			if ( _util.isArray( value ) ) {
				_authList = value;
			}
			else {
				_authList = [ value ];
			}
		}

		return _authList;
	}

	/**
	 * 権限を画面に反映する.<br/>
	 * 要素に指定された権限条件と保持している権限情報にもとづいて
	 * 要素のスタイルを変更します.
	 *
	 * 権限条件の記述例） <div data-ns-rule="ROLE_AAA,ROLE_BBB ROLE_CCC"></div>
	 *
	 * @param {JQuery} selector セレクタ
	 * @param {String[]} [authList=this.authList] 権限配列(設定されていなければthis.authListを使用)
	 */
	function _authApply( selector, authList ) {

		if ( _util.isUndefined( authList ) ) {
			authList = _authList;
		}

		// rule 属性／notrule 属性が付与されている全ての要素にルールを適用する

		$( selector ).find( "[" + _ruleAttrStr + "],[" + _notRuleAttrStr + "]" ).each( function () {

			var targetElement = $( this );
			var rule = $.trim( targetElement.attr( _ruleAttrStr ) );
			var notRule = $.trim( targetElement.attr( _notRuleAttrStr ) );
			var effect;

			var okClass = targetElement.attr( _okclassAttrStr );
			var ngClass = targetElement.attr( _ngclassAttrStr );

			if ( _util.isUndefined( okClass ) ) {
				okClass = _defaultOkCss;
			}
			if ( _util.isUndefined( ngClass ) ) {
				ngClass = _defaultNgCss;
			}

			// 現在当てられているスタイルクラスを除去

			targetElement.removeClass( okClass );
			targetElement.removeClass( ngClass );

			// 許可権限をチェック
			var result = _authCheck( rule, authList );

			// 禁止権限をチェック
			result &= _authCheckReverse( notRule, authList );

			// スタイルクラスを適用
			effect = result ? okClass : ngClass;

			if ( _util.isNotEmpty( effect ) ) {
				targetElement.addClass( effect );
			}
		});
	}

	/**
	 * 逆権限チェック（指定された権限を保持していないかチェック）
	 */
	function _authCheckReverse( notRule, authList ) {

		if ( _util.isEmpty( notRule ) ) { return true; }

		return _authCheck( notRule, authList, true );
	}

	/**
	 * 権限チェック（指定された権限を保持しているかチェック）
	 *
	 * @param {String} rule 権限条件（権限名をスペースまたはカンマで区切ったもの）
	 *        半角スペース区切りで OR、カンマ区切りで AND 判定する
	 *        混在している場合は、左から判定して確定した箇所で終了する（JavaScript の論理演算と多分同じ）
	 * @param {String[]} [authList=this.authList] 権限配列(設定されていなければthis.authListを使用)
	 * @param {boolean} reverse 逆チェックフラグ true:逆チェックする　false:逆チェックしない
	 *
	 * @return {boolean} true：OK（権限あり） false:NG（権限なし）
	 */
	function _authCheck( rule, authList, reverse ) {

		var ok = false;

		// ----- 条件指定がない場合は常に「OK」

		if ( _util.isEmpty( rule ) ) { return true; }

		rule.replace( / {2,}/, " " ); // 重複する半角スペースを削除
		rule.replace( ", ", "," ); // 半角カンマに続く半角スペースを削除

		// ----- その他の引数処理

		if ( _util.isUndefined( authList ) ) {
			authList = _authList;
		}

		if ( reverse === undefined ) {
			reverse = false;
		}

		// ----- 判定（OR 条件で split してループする）

		$.each( rule.split( " " ), function ( i, orVal ) {

			// 空白は無視

			if ( _util.isEmpty( orVal ) ) { return; } // continue

			orVal = $.trim( orVal );

			// OR 条件のみ（指定権限が authList に含まれていれば OK）
			if ( orVal.indexOf( "," ) < 0 ) {

				// 該当あり＝OK
				if ( $.inArray( orVal, authList ) >= 0 ) {
					ok = true;
					return false; // break
				}
			}
			// AND 条件あり（全てが含まれていれば OK）
			else  {
				ok = true;
				$.each( orVal.split( "," ), function ( i, andVal ) {

					andVal = $.trim( andVal );

					// 該当なし＝NG
					if ( $.inArray( andVal, authList ) < 0 ) {
						ok = false;
						return false; // break;
					}
				});

				// 全て含まれていた＝OK
				if ( ok ) { return false; } // break;
			}
			if ( ok ) { return false; } // break;
		});

		// 逆チェックの場合は判定結果を反転する
		return reverse ? ! ok : ok;
	}

	// -------------------------------------------------------------------------
	// 公開オブジェクト
	// -------------------------------------------------------------------------

	var ret = {

		name : "CCS.auth",

		ruleAttrStr : function() { return _ruleAttrStr; },
		notRuleAttrStr : function() { return  _notRuleAttrStr; },
		okclassAttrStr : function() { return  _okclassAttrStr; },
		ngclassAttrStr : function() { return  _ngclassAttrStr; },

		/**
		 * 権限リスト取得
		 */
		authList : _authListAccessor,

		/**
		 * 権限を画面に反映する.<br/>
		 */
		authApply : _authApply,

		/**
		 * 権限チェック
		 */
		authCheck : _authCheck,

		/**
		 * 逆権限チェック
		 */
		authCheckReverse : _authCheckReverse

	}; // end of ret

	CCS.auth = ret;

})( CCS );
