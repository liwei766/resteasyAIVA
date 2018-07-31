/*****
* CONFIGURATION
*/

/*
	dev: main.jsから共通パーツ用(主にナビ関連)を抜き出したものはmain_parts.jsへ移動
*/


/*
	dev: main_parts.jsへ
    //Main navigation
    $.navigation = $('nav > ul.nav');
*/

  $.panelIconOpened = 'icon-arrow-up';
  $.panelIconClosed = 'icon-arrow-down';

'use strict';

/****
* MAIN NAVIGATION
*/

$(document).ready(function($){

/*
	dev: 元の main.js の内容を取り込み　↓
*/
	var fauxTable1 = document.getElementById("t1");
	var fauxTable2 = document.getElementById("t3");
	var mainTable = document.getElementById("main-table");
	if ( mainTable ) {
		var clonedElement1 = mainTable.cloneNode(true);
		var clonedElement2 = mainTable.cloneNode(true);
		clonedElement1.id = "";
		if ( fauxTable1 ) fauxTable1.appendChild(clonedElement1);
		clonedElement2.id = "";
		if ( fauxTable2 ) fauxTable2.appendChild(clonedElement2);
	}
/*
	dev: 元の main.js の内容を取り込み　↑
*/


  /* ---------- Disable moving to top ---------- */
  $('a[href="#"][data-top!=true]').click(function(e){
    e.preventDefault();
  });

});

/****
* CARDS ACTIONS
*/

$(document).on('click', '.card-actions a', function(e){
  e.preventDefault();

  if ($(this).hasClass('btn-close')) {
    $(this).parent().parent().parent().fadeOut();
  } else if ($(this).hasClass('btn-minimize')) {
    var $target = $(this).parent().parent().next('.card-block');
    if (!$(this).hasClass('collapsed')) {
      $('i',$(this)).removeClass($.panelIconOpened).addClass($.panelIconClosed);
    } else {
      $('i',$(this)).removeClass($.panelIconClosed).addClass($.panelIconOpened);
    }

  } else if ($(this).hasClass('btn-setting')) {
    $('#myModal').modal('show');
  }

});

function capitalizeFirstLetter(string) {
  return string.charAt(0).toUpperCase() + string.slice(1);
}

function init(url) {

  /* ---------- Tooltip ---------- */
  $('[rel="tooltip"],[data-rel="tooltip"]').tooltip({"placement":"bottom",delay: { show: 400, hide: 200 }});

  /* ---------- Popover ---------- */
  $('[rel="popover"],[data-rel="popover"],[data-toggle="popover"]').popover();

}
