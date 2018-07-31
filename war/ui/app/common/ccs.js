"use strict";

/*
 * CallCenterSolution（CCS／CCS.data）
 */

// 空の CallCenterSolution コンテキスト

window.CCS = {

	name : "CCS",

	data : null,
	util : null,
	prop : null,
	auth : null,
	view : null,
	api  : null,
	user : null,

	// リリース時に、以下のフラグを全て false にすること

	debug : {
		log : false,		// true のとき、$util.log() でコンソールに出力
		dialog : false,	// true のとき、リダイレクト／ログアウト時にダイアログ表示
	}
};

/*
 * 共通データ設定（CCS.data）
 */
(function( CCS ) {

	// ----------------------------------------------------------------------------
	// ルート URL 取得
	// ----------------------------------------------------------------------------

	var PATH = "/ui/app/common/ccs.js"; // このファイルのパス（ルートは /ui の親ディレクトリ）

	var root = _rootPath();
	var apiroot = root;


	/**
	 * ルートコンテキストパスを取得（http://～/[contextName] 形式）.
	 **/
	function _rootPath() {

		var path;
		var scripts = document.getElementsByTagName( "script" );

		for ( var i = 0 ; i < scripts.length ; i++ ) {

			var src = _absolutePath( scripts[i].src );
			var sub = src.length - PATH.length;

			if ( ( sub >= 0 ) && ( src.lastIndexOf( PATH ) === sub ) ) {
				path = src.substring( 0, sub );
				break;
			}
		}

		return path;
	}

	/**
	 * URL（相対含む）から URL パス絶対表記を取得
	 *
	 * @param {String} path ファイルパス（URL）
	 * @return {String} URL パス絶対表記
	 **/
	function _absolutePath( path ) {

		var elem = document.createElement( "span" );
		elem.innerHTML = "<a href='" + path + "' />";
		return elem.firstChild.href;
	}

	// ----------------------------------------------------------------------------
	// 共通データ定義
	// ----------------------------------------------------------------------------

var ret =
{
	property : {
		common : {
			root : root,
			apiroot : apiroot,

			extHeaderName : {
				userInfo : "X-USER-INFO"	// ユーザ情報 HTTP ヘッダ名
			},

			sessionKey : { // セッションストレージキー名
				errorMessage : "errorMessage",
				callLogId    : "callLogId",	// 通話ログID（通話終了からの遷移のときに設定される）
			},

			apiResponse : {
				success  : "00_00_000",
				partial  : "00_00_002",
				overflow : "00_00_003",
				sysErrorPrefix : "90",
				detailColumnNamePrefix : "#",
				bizNoAuthError : "21_14_001",
				bizAuthError : "21_14_004",
			},

			anonymousUserInfo : {
				companyId : "（不明）",
				userId : "（不明）",
				userName : "（不明）",
				authList : [ "ROLE_ANONYMOUS" ],
				token : null
			},

			dateFormat : {
				"default" : "yyyy/mm/dd",
				date : "yyyy/mm/dd",
				time : "HH:MM",
				dateTime : "yyyy/mm/dd HH:MM:ss"
			},
		}, // end of common

		layout : {
			operation : {

				selector : "#navOperation",
			},
			admin : {

				selector : "#navAdmin",
			},
			edit : {

				selector : "#navEdit",
			},
			csvimport : {

				selector : "#navCsvimport",
			},

			speech : {

				selector : "#navSpeech",
			},
			license : {

				selector : "#navLicense",
			},
			agencyManagement : {

				selector : "#navAgencyManagement",
			},
			companyManagement : {

				selector : "#navCompanyManagement",
			},
			usetime : {

				selector : "#navUsetime",
			},
			lexicon : {

				selector : "#navLexicon",
			},
			companySetting : {

				selector : "#navCompanySetting",
			},
			filler : {

				selector : "#navFiller",
			},
			speechLogManagementByCompany : {

				selector : "#navSpeechLogManagementByCompany",
			},
			speechLogManagementByUser : {

				selector : "#navSpeechLogManagementByUser",
			},
			password : {

				selector : "#navPassword",
			},
			ccsNavbar : {

				selector : ".ccs-navbar",
			},
			userId : {

				selector : "#navUserId",
			},
			userName : {

				selector : "#navUserName",
			},
			logout : {

				selector : "#logout",	// ログアウトボタン
			},
			bar : {

				selector : "#navBar",
			},
			menu : {

				selector : "#navMenu",	// サイドメニュー
			},
			menuItem: {

				selector : ".ccs-menu", // サイドメニュー上の項目
			},
			dialog : {

				selector : ".modal[role=dialog]",
			},
			dialogTitle : {

				selector : ".modal-title", // dialog からの相対位置
			},
			dialogMessage : {

				selector : ".modal-body p", // dialog からの相対位置
			},
			dialogOk : {

				selector : ".modal-footer .ok-button", // dialog からの相対位置
			},
			dialogCancel : {

				selector : ".modal-footer .cancel-button", // dialog からの相対位置
			},
			dialogParts : {

				selector : ".ccs-modal-parts", // dialog パーツ
			},

		}, // end of layout

	}, // end of property

	// ----------------------------------------------------------------------------

	// ＜権限制御の記述方法＞
	//
	// data-ccs-rule    : 指定された権限を保持している場合にアクセス許可
	// data-ccs-notrule : 指定された権限を保持している場合はアクセス不許可
	// data-ccs-okclass : アクセス許可と判定された場合に適用されるスタイルクラス
	// data-ccs-ngclass : アクセス不許可と判定された場合に適用されるスタイルクラス
	//
	// ＜権限制御の内容＞
	//
	// 指定権限を保持していると判定された場合に okClass／ngClass が適用されます。
	// （redirectMap に限り、不許可の場合は不正遷移エラーになります。）
	//
	// rule, notrule を指定しない場合、アクセス判定は行われません（誰でもアクセス可能）。
	// okclass, ngclass を指定しない場合はデフォルトのスタイルが適用されます。
	//
	// ＜data-ns-rule／data-ns-notrule 属性の記述方法＞
	//
	// ・チェックする権限名を列挙
	// ・スペース区切り＝OR、カンマ区切り＝AND
	// ・左から順にチェックを行い、確定した時点で判定を終了する（&&, || 演算子と同じ）
	// ・判定順序を変更するためのカッコなどは使用不可
	//
	// 例）
	// data-ns-rule="ROLE_A ROLE_B" → ROLE_A と ROLE_B のどちらかを保持していれば OK
	// data-ns-rule="ROLE_A, ROLE_B" → ROLE_A と ROLE_B の両方を保持していれば OK
	// data-ns-rule="ROLE_A ROLE_B, ROLE_C" → （以下の手順で判定）
	//  1. ROLE_A を保持していれば OK として終了（ROLE＿B、ROLE_C はチェックしない）
	//  2. ROLE_B を保持していなければ NG として終了（ROLE_C はチェックしない）
	//  3. ROLE_C を保持していれば OK として終了（保持していなければ NG として終了）
	//  ※意味としては ROLE_A ( ROLE_B, ROLE_C ) のイメージ
	// data-ns-notrule の場合、data-ns-rule の例とは「OK」と「NG」の結果が全て逆になります

	// ----- リダイレクトによる遷移の一覧

	redirectMap : {

		top : { url : root + "/ui/speech.html", notrule : "ROLE_ANONYMOUS" }, // トップ画面はオペレーション画面とする
		logout : { url : root + "/ui/logout", notrule : "ROLE_ANONYMOUS" }, // ログアウト
		speech : { url : root + "/ui/speech.html", notrule : "ROLE_ANONYMOUS" },
		useTime : { url : root + "/ui/agency/useTime.html", rule : "ROLE_AGENCY" },
		license : { url : root + "/ui/sys/license.html", rule : "ROLE_SYS_ADMIN" },
		companyManagement : { url : root + "/ui/agency/companyManagement.html", rule : "ROLE_AGENCY" },
		agencyManagement : { url : root + "/ui/sys/agencyManagement.html", rule : "ROLE_SYS_ADMIN" },
		lexicon : { url : root + "/ui/admin/lexicon.html", rule : "ROLE_ADMIN" },
		companySetting : { url : root + "/ui/admin/companySetting.html", rule : "ROLE_ADMIN" },
		filler : { url : root + "/ui/sys/filler.html", rule : "ROLE_SYS_ADMIN" },
		speechLogManagementByCompany : { url : root + "/ui/admin/speechLogManagement.html", rule : "ROLE_ADMIN" },
		speechLogManagementByUser : { url : root + "/ui/speechLogManagement.html", notrule : "ROLE_ANONYMOUS" },
		password : { url : root + "/ui/admin/password.html", rule : "ROLE_ADMIN" },

		error : {
			accessDenied : { url : root + "/ui/error/access-denied.html" },
			error : { url : root + "/ui/error/error.html" },
		},

		"*" : {} // どれにも該当しない場合に適用する

	}, // end of redirectMap

	// ----- 共通の API 一覧

	apiMap : {

		common : {

		}, // end of common

		login : {

			info : apiroot + "/api/login/info",
			forceLogin : apiroot + "/api/login/forceLogin",
		},

		speech : {

			start : apiroot + "/api/speech/start",
			update : apiroot + "/api/speech/update",
			end : apiroot + "/api/speech/end",
			fileAnalyze : apiroot + "/api/speech/fileAnalyze",
			getProgressRate : apiroot + "/api/speech/getProgressRate",
			resume : apiroot + "/api/speech/resume",

		},

		speechLog : {

			searchByUser : apiroot + "/api/speechlog/searchByUser",
			searchByCompany : apiroot + "/api/speechlog/searchByCompany",
			get : apiroot + "/api/speechlog/get",
			generateVoiceByUser : apiroot + "/api/speechlog/generateVoiceByUser",
			generateVoiceByCompany : apiroot + "/api/speechlog/generateVoiceByCompany",
			downloadFileByUser : apiroot + "/api/speechlog/downloadFileByUser",
			downloadFileByCompany : apiroot + "/api/speechlog/downloadFileByCompany",
			del : apiroot + "/api/speechlog/delete",
		},

		speechlogdetail : {

			update : apiroot + "/api/speechlogdetail/update",
			voice : apiroot + "/api/speechlogdetail/voice",
		},

		digest : {

			digest : apiroot + "/api/digest/digest",
		},

		lexicon : {

			get : apiroot + "/api/lexicon/get",
			update : apiroot + "/api/lexicon/update",
			file : apiroot + "/api/lexicon/file",
		},

		filler : {

			get : apiroot + "/api/filler/get",
			update : apiroot + "/api/filler/update",
			file : apiroot + "/api/filler/file",
		},

		recaiusLicense : {

			search : apiroot + "/api/recaiuslicense/search",
			get : apiroot + "/api/recaiuslicense/get",
			put : apiroot + "/api/recaiuslicense/put",
			update : apiroot + "/api/recaiuslicense/update",
			del : apiroot + "/api/recaiuslicense/delete",

		},

		companyManagement : {

			search : apiroot + "/api/companymanagement/search",
			get : apiroot + "/api/companymanagement/get",
			put : apiroot + "/api/companymanagement/put",
			update : apiroot + "/api/companymanagement/update",
			del : apiroot + "/api/companymanagement/delete",
			issueUrl : apiroot + "/api/companymanagement/issueUrl",
			deleteUrl : apiroot + "/api/companymanagement/deleteUrl",
			getCompanySettings : apiroot + "/api/companymanagement/getCompanySettings",
			updateCompanySettings : apiroot + "/api/companymanagement/updateCompanySettings",

		},

		companySetting : {

			getCompanySettings : apiroot + "/api/companymanagement/getCompanySettings",
			updateCompanySettings : apiroot + "/api/companymanagement/updateCompanySettings",

		},

		agency : {

			search : apiroot + "/api/agency/search",
			get : apiroot + "/api/agency/get",
			put : apiroot + "/api/agency/put",
			del : apiroot + "/api/agency/delete",

		},

		useTime : {

			//searchByMonth : apiroot + "/api/usetime/searchByMonth",
			searchByCompanyId : apiroot + "/api/usetime/searchByCompanyId",
			searchByUserId : apiroot + "/api/usetime/searchByUserId",
			getUseTime : apiroot + "/api/usetime/get",
		},

		password : {

			xauth : apiroot + "/api/password/xauth",
			search : apiroot + "/api/password/search",
			update : apiroot + "/api/password/update",
		},

	}, // end of apiMap

	// ----------------------------------------------------------------------------

	label : {

		speech : {

			fileAnalyze : "音声ファイル解析中",
			endFileAnalyze : "解析終了処理中",
			saveFileAnalyze : "解析結果保存処理中",
		},

		knowledge : {

			title : "タイトル",
			content : "回答",
			url : "URL",
			manualName : "マニュアル名",
			manualPage : "マニュアルページ",
			manualUrl : "マニュアル URL",
			script : "スクリプト"
		}, // end of knowledge

		tag : {

			tagName : "タグ"
		},

		callLog : {

			callLogId : "時刻"
		},

		knowledgeStatus : "完了",

		speechLogManagement : {

			speechLogNo : "解析番号",
			fileName: "ファイル名",
		},

		recaiusLicense : {

			agencyCompanyId: "代理店企業ID",
			serviceId : "サービス利用ID",
			password : "パスワード"
		},

		companyManagement : {

			companyId: "企業ID",
			companyName: "企業名",
			energyThreshold : "音声判断レベル",
			saveVoice : "音声を保存する",
			permitIpAddress : "制限IPアドレス"
		},

		agency : {

			agencyCompanyId : "代理店企業ID"
		},

		password : {

			password: "パスワード",
			confirmPassword : "確認用パスワード"
		},

	}, // end of label

	// ----------------------------------------------------------------------------

	message : {

		common : {
			complete : {
				regist : "登録しました。",
				update : "更新しました。",
				del : "削除しました。"
			},

			confirm : {
				logout : "ログアウトします。よろしいですか？",
				regist : "登録します。よろしいですか？",
				update : "更新します。よろしいですか？",
				del : "削除します。よろしいですか？"
			},

			error : {
				regist : "登録できませんでした。",
				update : "更新できませんでした。",
				del : "削除できませんでした。"
			},

			systemerror : {
				parseError : "レスポンス情報の解析に失敗しました。",
				authError : "権限がありません。",
				httpStatusError : "HTTPステータスエラー({0})",
				unauthError : "ログインしていないか、セッションがタイムアウトしました。"
			},

			accounterror : {
				title : "現在のアカウントは以下の理由により、本システムではご利用いただけません。",
				companyId : "・企業 ID の文字数が {0} 文字を超えています。",
				userId : "・ユーザ ID の文字数が {0} 文字を超えています。",
				userName : "・ユーザ名の文字数が {0} 文字を超えています。",
			},

			reload : "画面をリロードしてください。",

		}, // end of common

		layout : {
			confirm : {
				lexicon : "ユーザ辞書管理画面に移動します。入力内容は保存されません。よろしいですか？",
				usetime : "利用時間確認画面に移動します。入力内容は保存されません。よろしいですか？",
				license : "ライセンス管理画面に移動します。入力内容は保存されません。よろしいですか？",
				companyManagement : "企業管理画面に移動します。入力内容は保存されません。よろしいですか？",
				agencyManagement : "代理店管理画面に移動します。入力内容は保存されません。よろしいですか？",
				companySetting : "企業設定画面に移動します。入力内容は保存されません。よろしいですか？",
				speech : "音声解析画面に移動します。入力内容は保存されません。よろしいですか？",
				filler : "フィラー情報管理画面に移動します。入力内容は保存されません。よろしいですか？",
				speechLogManagementByCompany : "音声解析履歴管理画面に移動します。入力内容は保存されません。よろしいですか？",
				speechLogManagementByUser : "音声解析履歴管理画面に移動します。入力内容は保存されません。よろしいですか？",
				password : "パスワード管理画面に移動します。入力内容は保存されません。よろしいですか？",
			},
		}, // end of layout


		useTime : {

			noSelect : "（左側リストから選択してください）",
			overflow : "該当件数が{0}件以上あるため、先頭の{0}件のみ表示しています。",

			error : {

				listError : "利用時間一覧を取得できませんでした。",
				getError  : "利用時間情報を取得できませんでした。",
			}
		},

		speech : {

			confirm : "解析結果を消去します。よろしいですか？",
			exportEditingConfirm : "編集中の文言があります。出力ファイルには反映されません。よろしいですか？",
			exportConfirm : "ファイルを出力します。よろしいですか？",
			pauseSpeech : "音声解析は一時停止中です。",

			error : {

				analyzeFailed : "音声解析に失敗しました。",
				analyzeBeginFailed : "音声解析を開始できません。",
				noFile : "ファイルを選択してください。",
				emptyFile : "空のファイルは指定できません。",
				speechStartError : "音声解析を開始できません。",
				endSpeechError  : "解析終了に失敗しました。",
				noMic : "マイクが接続されていないか使用できません。",
				logError  : "音声解析内容を取得できませんでした。",
				partialError : "一部更新できませんでした。",
				generateError : "音声ファイルを生成できませんでした。",
				generatePartialError : "音声ファイルを一部生成できませんでした。生成されたファイルを出力します。よろしいですか？",
				generateMergeError : "音声ファイルを生成できませんでした。残りのファイルを出力します。よろしいですか？",
				resumeError : "音声解析を再開できませんでした。",
			}
		},

		lexicon : {

			complete : "ユーザ登録辞書を更新しました（{0}件）。",
			confirm : "登録済みのユーザ辞書の内容は選択されたファイルの内容で置き換えられます。よろしいですか？",
			exportConfirm : "ファイルを出力します。よろしいですか？",
			exportComplete : "ファイルを出力しました。",

			error : {

				noFile : "ファイルを選択してください。",
				emptyFile : "空のファイルは指定できません。",
				updateError : "ユーザ登録辞書を更新できませんでした。",
				partialError : "インポートできませんでした。（エラー {0}件）",
				tooManyError : "※先頭の{0}件分のエラーのみ表示しています。",
				exportError : "ファイルの出力に失敗しました。",
				searchModelError  : "未登録のモデルIDです。"
			}
		},

		filler : {

			complete : "フィラー情報を更新しました（{0}件）。",
			confirm : "登録済みのフィラー情報の内容は選択されたファイルの内容で置き換えられます。よろしいですか？",
			exportConfirm : "ファイルを出力します。よろしいですか？",
			exportComplete : "ファイルを出力しました。",

			error : {

				noFile : "ファイルを選択してください。",
				emptyFile : "空のファイルは指定できません。",
				updateError : "フィラー情報を更新できませんでした。",
				partialError : "インポートできませんでした。（エラー {0}件）",
				tooManyError : "※先頭の{0}件分のエラーのみ表示しています。",
				exportError : "ファイルの出力に失敗しました。",
				searchModelError  : "未登録のモデルIDです。"
			}
		},

		license : {

			noSelect : "（編集はライセンスをリストから選択してください）",
			overflow : "該当件数が{0}件以上あるため、先頭の{0}件のみ表示しています。",
			passwordEdit : "（未入力の場合には登録済みのものが適用されます）",

			error : {

				listError : "ライセンス一覧を取得できませんでした。",
				getError  : "ライセンス情報を取得できませんでした。",
			}
		},

		companyManagement : {

			noSelect : "（編集は企業をリストから選択してください）",
			overflow : "該当件数が{0}件以上あるため、先頭の{0}件のみ表示しています。",
			confirm : {
				issueUrl : "URLを発行します。すでに発行済みの場合は再発行のURLで上書きしますがよろしいですか？",
				delUrl : "URLを削除します。よろしいですか？",
			},
			complete : {
				issueUrl : "URLを発行しました。",
				delUrl : "URLを削除しました。",
			},

			error : {

				listError : "企業一覧を取得できませんでした。",
				getError  : "企業情報を取得できませんでした。",
				getUrlError : "URLを取得できませんでした。",
				issueUrlError : "URLを発行できませんでした。",
				delUrlError : "URLを削除できませんでした。",
			}
		},

		companySetting : {

			error : {

				getcompanySettingError : "企業設定を取得できませんでした。",
				noInputEnergyThreshold : "音声判断レベル入力欄に半角数字を入力してください",
			}
		},

		agencyManagement : {

			noSelect : "（編集は代理店をリストから選択してください）",
			overflow : "該当件数が{0}件以上あるため、先頭の{0}件のみ表示しています。",

			error : {

				listError : "代理店一覧を取得できませんでした。",
				getError  : "代理店情報を取得できませんでした。",
			}
		},

		speechLogManagement : {

			noSelect : "（編集は音声解析履歴をリストから選択してください）",
			overflow : "該当件数が{0}件以上あるため、先頭の{0}件のみ表示しています。",

			exportEditingConfirm : "編集中の文言があります。出力ファイルには反映されません。よろしいですか？",
			exportConfirm : "ファイルを出力します。よろしいですか？",

			error : {

				listError : "音声解析一覧を取得できませんでした。",
				logError  : "音声解析内容を取得できませんでした。",
				partialError : "一部更新できませんでした。",
				generateError : "音声ファイルを生成できませんでした。",
				generatePartialError : "音声ファイルを一部生成できませんでした。生成されたファイルを出力します。よろしいですか？",
			}
		},

		password : {

			inputAuthPassword : "パスワードを入力してください。",
			noSelect : "（編集はユーザをリストから選択してください）",

			error : {

				failAuthError : "認証に失敗しました。再度パスワードを入力してください。",
				authError : "認証に失敗しました。",
				listError : "ユーザ一覧を取得できませんでした。",
			}
		},

		concurrentLogin : {

			confirm : "強制ログインを行うと現在ログインしているユーザが強制ログアウトされます。よろしいですか？",
			error : {

				forceLoginError : "強制ログインに失敗しました。",
			}
		},

	}, // end of message

	// ----------------------------------------------------------------------------

	validatorMessage : {
		common : {
			required : "必須項目です",

			fixlength : "{0} 文字で入力してください",
			rangelength : "{0} 文字以上{1}文字以下で入力してください",
			minlength : "{0} 文字以上を入力してください",
			maxlength : "{0} 文字以下で入力してください",

			range : "{0} から {1} までの値を入力してください",
			min : "{0} 以上の値を入力してください",
			max : "{0} 以下の値を入力してください",

			rangeTime : "{0} から {1} までの日付（時刻）を入力してください",
			minTime : "{0} 以降の日付（時刻）を入力してください",
			maxTime : "{0} 以前の日付（時刻）を入力してください",

			digits : "半角数字を入力してください",
			numeric : "半角数字を入力してください",
			numberInt : "半角で整数を入力してください",
			number : "半角で数値を入力してください",

			rangeInt : "-999999999～999999999までの値を入力してください",
			rangeLong : "-999999999999999～999999999999999までの値を入力してください",

			alpha : "半角英字を入力してください",
			alphaNumeric : "半角英数字を入力してください",
			hankakuAscii : "半角英数記号を入力してください",
			hankakuKana : "半角カタカナを入力してください",
			hankakuAll : "半角文字を入力してください",

			katakana : "全角カタカナを入力してください",
			hirakana : "全角ひらがなを入力してください",
			kana : "全角ひらがな･カタカナを入力してください",
			zenkakuAll : "全角文字を入力してください",
			zenkakuAllAndNewLine : "全角文字または改行を入力してください",

			regexp : "書式が正しくありません",
			ngRegexp : "不正な書式です",

			date : "{0} の形式で入力してください",
			dateTime : "{0} の形式で入力してください",
			dateTimeSec : "{0} の形式で入力してください",
			time : "HH:MM の形式で入力してください",
			timeSec : "HH:MM:SS の形式で入力してください",
			timeFormat : "HH:MM:SS の形式で入力してください",

			isPast : "{1} 以前の日付（時刻）を指定してください",
			isFuture : "{1} 以降の日付（時刻）を指定してください",

			equalTo : "同じ値を入力してください",
			notEqualTo : "異なる値を入力してください",

			url : "URL を入力してください"
		} // end of common
	} // end of validatorMessage

};  // end of ret

	CCS.data = ret;

})( CCS );
