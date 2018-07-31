"use strict";


/*
 * 利用時間管理画面
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

			search_by_month : {				// ----- 検索ボタン

				selector : "#search_by_month",
				handler : [
					[ "click", searchByMonth ]
				]
			},

			useTimeParent : {		// ----- 一覧

				selector : "#main-table tbody",	// <tbody>
			},

			listCompanyId : {

				selector : ".ccs-useTime-companyId",		// 企業IDを選択
			},

			listCompanyDtl : {

				selector : ".ccs-useTime-companyDtl",
			},

			listUserId : {

				selector : ".ccs-useTime-userId",		// 企業IDを選択
			}

		}, // end of component

		startYear : 2017,
		maxResult : 300,	// 一覧の最大表示件数

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

	// ----- 企業IDを click

	data.component.useTimeParent.$element.on( "click", data.component.listCompanyId.selector, companyTitleClick );

	data.component.useTimeParent.$element.on( "click", data.component.listCompanyDtl.selector, dtlClick );

	data.component.useTimeParent.$element.on( "click", data.component.listUserId.selector, userTitleClick );


	// -------------------------------------------------------------------------

	/**
	 * 一覧の選択状態解除.
	 */
	function _clearSelect() {

		data.component.useTimeParent.$element.find( ".bg-primary" ).removeClass( "bg-primary" );
	}

	/**
	 * 一覧の指定行を選択状態にする.
	 *
	 * @param {Element|jQuery} target 選択状態にする
	 */
	function _applySelect( target ) {

		$( target ).addClass( "bg-primary" );
	}

	// -------------------------------------------------------------------------
	// 検索用の年月の値を用意。ループ処理（スタート数字、終了数字、表示id名、デフォルト数字）
	// -------------------------------------------------------------------------
	function optionLoop(start, end, id, this_day) {
		let opt = null;
		for (let i = start; i <= end ; i++) {
			if (i === this_day) {
				opt += `<option value='${i}' selected>${i}</option>`;
			} else {
				opt += `<option value='${i}'>${i}</option>`;
			}
		}
		document.getElementById(id).innerHTML = opt;
	};

	let today = new Date();
	let thisYear = today.getFullYear();
	let thisMonth = today.getMonth() + 1;
	/*
	 *関数設定（スタート数字[必須]、終了数字[必須]、表示id名[省略可能]、デフォルト数字[省略可能]）
	 */
	optionLoop(data.startYear, thisYear, 'id_year', thisYear);
	optionLoop(1, 12, 'id_month', thisMonth);




	function searchByMonth( event ) {

		var form = { searchForm : {
			useTime : {

				year : $("#id_year").val(),
				month : $("#id_month").val()

			},
			sortForm : { sortElement : [

				{ name : "useTime", asc : true }
			]}
		}};

		var url = _prop.getApiMap( "useTime.searchByCompanyId" );
		var json = JSON.stringify( form );

		var option = {

			handleError : _searchError,
			handleSuccess : _searchSuccess
		};

		_api.postJSON( url, json, option );
	}

	function _searchError( xhr, status, errorThrown, option ) {

		var msgList = [ _prop.getMessage( "useTime.error.listError" ) ];

		var msg = option.result.msgList[0].message;

		if ( msg ) {

			msgList.push( `( ${msg} )` );
		}

		_dispError( msgList );

		_clearSelect();

	}

	function _searchSuccess( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_searchError( xhr, status, null, option );
			return;
		}

		// ----- 正常終了


		// 一覧表示
		var table = $("#main-table");
		table.find("tr").each(function(i){
			if(i !== 0){
				this.remove();
			}
		});

		for (var i = 0; i < response.searchResultList.length; i++) {
			var tr = $("<tr>", {
				//align: "left",
				height: "36"
			});

			var usetime = response.searchResultList[i].useTime;

			$( "<td></td>" ).addClass( "ccs-useTime-companyId" ).attr( "data-ccs-id", usetime.companyId )
				.text( usetime.companyId ).appendTo( tr );

			$( '<td style="text-align: right;"></td>' ).css('align','left').text( millisecondToDate(usetime.useTime) ).appendTo( tr );

			$( '<td></td>' ).addClass( "ccs-useTime-companyDtl").attr( "data-ccs-id", usetime.companyId ).text( "▼" ).appendTo( tr );



			tr.appendTo(table);
		}


		//右側クリア
		var table = $("#main-table2");
		table.find("tr").each(function(i){
			if(i !== 0){
				this.remove();
			}
		});

		$("#dispMsg").html( _prop.getMessage( "useTime.noSelect" ) );

		$("#companyOrUser").html("");

		$("#companyOrUserId").text("");

	}


	function companyTitleClick( event ) {

		let id = $( event.target ).attr( "data-ccs-id" );

		if ( _util.isEmpty( id ) ) return false;

//		$("#companyOrUser").html("企業ID:&nbsp;&nbsp;");
//
//		$("#companyOrUserId").text(id);

		_clearSelect();

		_applySelect( $( event.target ).closest( "tr" ) );

		searchByCompanyID(event);

	}


	function dtlClick( event ) {

		let id = $( event.target ).attr( "data-ccs-id" );

		if ( _util.isEmpty( id ) ) return false;

		let companyUserList = $( `tr[name='${id}_user']`);


		$(event.target).text()==="▼"?$(event.target).text("▲"):$(event.target).text("▼");

		if(companyUserList.length > 0 ){

			companyUserList.is(':hidden') ? companyUserList.show(): companyUserList.hide();

			return;
		}


		// ----- API 呼び出し
		var form = { searchForm : {
			useTime : {

				year : $("#id_year").val(),
				month : $("#id_month").val(),

				companyId : id

			},
			sortForm : { sortElement : [

				{ name : "use_time", asc : false }
			]}
		}};

		var url = _prop.getApiMap( "useTime.searchByUserId" );
		var json = JSON.stringify( form );

		let  $tag_td = $(this)[0];
		let  $tag_tr = $(this).parent()[0];

		var option = {
			line:$tag_tr.rowIndex,

			handleError : _searchError,
			handleSuccess : _searchSuccess_userList
		};


		_api.postJSON( url, json, option );

	}


	function _searchSuccess_userList( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_searchError( xhr, status, null, option );
			return;
		}

		// ----- 正常終了


		// 表示
		var usetimeList = response.searchResultList;

		let userIdx= 1;
		for (var i = 0; i < response.searchResultList.length; i++) {
			let usetime = usetimeList[i].useTime;
			let row=document.getElementById("main-table").insertRow(option.line + userIdx);
			row.innerHTML = `<td class="ccs-useTime-userId">&nbsp;&nbsp;&nbsp;&nbsp;${usetime.userId}</td><td style="text-align: right;">${millisecondToDate(usetime.useTime)}</td><td></td>`;
			row.id= `${usetime.companyId}_user_${userIdx}`;

			$('#'+row.id).addClass( "ccs-useTime-user" ).attr('name',`${usetime.companyId}_user`).attr( "userId", usetime.userId ).attr( "companyId", usetime.companyId );

	 		userIdx++;

		}


	}


	function searchByCompanyID( event ) {

		var form = { searchForm : {
			useTime : {

				companyId : $(event.target).attr('data-ccs-id')

			},
			sortForm : { sortElement : [

				{ name : "year_months", asc : false }
			]}
		}};

		var url = _prop.getApiMap( "useTime.searchByCompanyId" );

		var json = JSON.stringify( form );

		var option = {

			handleError : _searchError,
			handleSuccess : _searchSuccess_companyID
		};

		_api.postJSON( url, json, option );
	}



	function _searchSuccess_companyID( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_searchError( xhr, status, null, option );
			return;
		}

		// ----- 正常終了


		$("#dispMsg").html("　");


		$("#companyOrUser").html("企業ID:&nbsp;&nbsp;");

		$("#companyOrUserId").text(response.searchResultList[0].useTime.companyId);


		// 一覧表示
		var table = $("#main-table2");
		table.find("tr").each(function(i){
			if(i !== 0){
				this.remove();
			}
		});

		for (var i = 0; i < response.searchResultList.length; i++) {
			var tr = $("<tr>", {
				//align: "left",
				height: "36"
			});

			var usetime = response.searchResultList[i].useTime;

			$( "<td></td>" ).text( usetime.yearMonths ).appendTo( tr );

			//var usetimeTrim = Math.floor( usetime.useTime/1000/60 * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
			$( '<td style="text-align: right;"></td>' ).text( millisecondToDate(usetime.useTime) ).appendTo( tr );

			$( "<td></td>" ).text( usetime.memo ).appendTo( tr );

			tr.appendTo(table);
		}

	}


	function userTitleClick( event ) {

		let id = event.target.innerText.trim();

		if ( _util.isEmpty( id ) ) return false;

//		$("#companyOrUser").html("ユーザID:&nbsp;&nbsp;");
//
//		$("#companyOrUserId").text(id);

		_clearSelect();

		_applySelect( $( event.target ).closest( "tr" ) );

		searchByUserID(event);

	}

	function searchByUserID( event ) {

		var form = { searchForm : {
			useTime : {

				companyId : $(event.target.parentElement).attr('companyId'),

				userId : event.target.innerText.trim()

			},
			sortForm : { sortElement : [

				{ name : "year_months", asc : false }
			]}
		}};

		var url = _prop.getApiMap( "useTime.searchByUserId" );
		var json = JSON.stringify( form );

		var option = {

			handleError : _searchError,
			handleSuccess : _searchSuccess_userID
		};

		_api.postJSON( url, json, option );
	}



	function _searchSuccess_userID( response, status, xhr, option ) {

		if ( ! option.result.ok ) {

			_searchError( xhr, status, null, option );
			return;
		}

		// ----- 正常終了

		$("#dispMsg").html("　");

		$("#companyOrUser").html("ユーザID:&nbsp;&nbsp;");

		$("#companyOrUserId").text(response.searchResultList[0].useTime.userId);


		// 一覧表示
		var table = $("#main-table2");
		table.find("tr").each(function(i){
			if(i !== 0){
				this.remove();
			}
		});

		for (var i = 0; i < response.searchResultList.length; i++) {
			var tr = $("<tr>", {
				//align: "left",
				height: "36"
			});

			var usetime = response.searchResultList[i].useTime;

			$( "<td></td>" ).text( usetime.yearMonths ).appendTo( tr );

			//var usetimeTrim = Math.floor( usetime.useTime/1000/60 * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
			$( '<td style="text-align: right;"></td>' ).text( millisecondToDate(usetime.useTime) ).appendTo( tr );

			$( "<td></td>" ).text( usetime.memo ).appendTo( tr );

			tr.appendTo(table);
		}

	}


	function millisecondToDate(msd) {

		let oneDay = 3600*24*1000;
		let days = Math.floor(msd/oneDay);

		if( days<1 ){

			return new Date(msd).toISOString().slice(11, -5);

		}else{

			let time = new Date(msd % oneDay).toISOString().slice(11, -5);

			return parseInt(time.substr(0,2)) + 24*days + time.substr(2);
		}

	}

	//備考
	function millisecondToDate1(msd) {
		let time = parseFloat(msd) / 1000;
		if (null != time && "" != time) {
			if (time > 60 && time < 60 * 60) {
				time = parseInt(time / 60.0) + "分" + parseInt((parseFloat(time / 60.0) -
					parseInt(time / 60.0)) * 60) + "秒";
			}
			else if (time >= 60 * 60 && time < 60 * 60 * 24) {
				time = parseInt(time / 3600.0) + "時間" + parseInt((parseFloat(time / 3600.0) -
					parseInt(time / 3600.0)) * 60) + "分" +
					parseInt((parseFloat((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60) -
					parseInt((parseFloat(time / 3600.0) - parseInt(time / 3600.0)) * 60)) * 60) + "秒";
			}
			else {
				time = parseInt(time) + "秒";
			}
		} else {
			time = "0秒";
		}
		return time;
	}


	// -------------------------------------------------------------------------
	// 共通処理
	// -------------------------------------------------------------------------

	/**
	 * エラーダイアログ表示.
	 *
	 * @param {String|String[]} message エラーメッセージ.
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

