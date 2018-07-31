/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：ResponseCode.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.rest;

import java.util.HashMap ;
import java.util.Map ;

/**
 * REST API 処理結果コード
 *
 * @author itsukaha
 */
public enum ResponseCode
{
	/** 正常終了 */
	OK( "00_00_000" ),

	/** 通知あり */
	NOTICE( "00_00_001" ),

	/** 一部エラー */
	PARTIAL( "00_00_002" ),

	/** 検索結果過多 */
	TOO_MANY_SEARCH_RESULT( "00_00_003" ),

	// -------------------------------------------------------------------------
	// システムエラー
	// -------------------------------------------------------------------------

	/** システムエラー */
	SYS_ERROR( "90_00_001" ),

	/** DB エラー */
	DB_ERROR( "90_00_002" ),

	/** DB データエラー（不整合） */
	DB_DATA_ERROR( "90_00_003" ),

	/** 認証情報不正 */
	AUTH_ERROR( "90_00_004" ),

	// -------------------------------------------------------------------------
	// 入力エラー
	// 処理結果詳細[0] : エラーと判定した項目名（#{entity}.{fieldName} 形式）
	// -------------------------------------------------------------------------

	/** 入力エラー：必須チェック */
	INPUT_ERROR_REQUIRED( "01_00_001" ),

	/** 入力エラー：入力禁止エラー（必須エラーの逆） */
	INPUT_ERROR_PROHIBIT( "01_00_002" ),

	/** 入力エラー：文字数チェック */
	INPUT_ERROR_LENGTH( "01_00_003" ),

	/** 入力エラー：数値範囲チェック */
	INPUT_ERROR_RANGE( "01_00_004" ),

	/** 入力エラー：範囲（列挙）チェック */
	INPUT_ERROR_ENUM( "01_00_005" ),

	/** 入力エラー：文字種チェック */
	INPUT_ERROR_LETTER_TYPE( "01_00_006" ),

	/** 入力エラー：正規表現チェック */
	INPUT_ERROR_REGEXP( "01_00_007" ),

	/** 入力エラー：要素数不正（リスト／配列等） */
	INPUT_ERROR_LIST_SIZE( "01_00_008" ),

	/** 入力エラー：不正範囲（開始＞終了 等） */
	INPUT_ERROR_CONFLICT_RANGE( "01_00_009" ),

	/** 入力エラー：書式エラー */
	INPUT_ERROR_FORMAT( "01_00_010" ),

	// -------------------------------------------------------------------------
	// 共通エラー
	// -------------------------------------------------------------------------

	/** ログイン失敗 */
	LOGIN_FAIL( "11_00_000" ),

	/** 存在しない */
	NOT_FOUND( "11_00_001" ),

	/** 重複 */
	DUPLICATE( "11_00_002" ),

	/** 楽観ロックエラー */
	OPTIMISTIC_LOCK( "11_00_003" ),

	/** 権限不足 */
	AUTH_INSUFFICIENT( "11_00_004" ),

	// -------------------------------------------------------------------------

	/** ファイルアップロードエラー */
	UPLOAD_ERROR( "21_00_200" ),

	/** ファイルアップロードエラー：不正要求 ID
	 *
	 * detail[0] : 要求 ID
	 */
	UPLOAD_ERROR_INVALID_ID( "21_00_201" ),

	/**
	 * ファイルアップロードエラー：要求 ID 重複
	 *
	 * detail[0] : 要求 ID
	 */
	UPLOAD_ERROR_DUPLICATE_ID( "21_00_202" ),

	/**
	 * ファイルアップロードエラー：ファイル内容なし
	 *
	 * detail[0] : 要求 ID
	 */
	UPLOAD_ERROR_NO_CONTENT( "21_00_203" ),

	/**
	 * ファイルアップロードエラー：ファイル名なし
	 *
	 * detail[0] : 要求 ID
	 */
	UPLOAD_ERROR_NO_FILENAME( "21_00_204" ),

	/**
	 * ファイルアップロードエラー：ファイルサイズオーバー
	 *
	 * detail[0] : 要求 ID
	 */
	UPLOAD_ERROR_SIZE_EXCEED( "21_00_205" ),

	/**
	 * ファイルアップロードエラー：ファイル格納エラー
	 *
	 * detail[0] : 要求 ID
	 */
	UPLOAD_ERROR_STORE_FILE( "21_00_206" ),

	/**
	 * ファイルアップロードエラー：ファイル格納エラー
	 *
	 * detail[0] : 要求 ID
	 */
	UPLOAD_ERROR_GET_STREAM( "21_00_207" ),

	/**
	 * ファイルアップロードエラー：ファイルアップロードエラー
	 *
	 * detail[0] : 要求 ID
	 */
	UPLOAD_ERROR_PARSE_REQUEST( "21_00_208" ),

	/**
	 * ファイルアップロードエラー：ファイル内容不正
	 *
	 * detail[0] : 要求 ID
	 */
	UPLOAD_ERROR_INVALID_FORMAT( "21_00_209" ),

	/**
	 * ファイルアップロードエラー：リサイズ不可（CMYK モード）
	 *
	 * detail[0] : 要求 ID
	 */
	UPLOAD_ERROR_RESIZE_CMYK( "21_00_210" ),

	/**
	 * ファイルアップロードエラー：リサイズ不可（ICC プロファイルつき）
	 *
	 * detail[0] : 要求 ID
	 */
	UPLOAD_ERROR_RESIZE_ICC( "21_00_211" ),

	// -------------------------------------------------------------------------

	/** アップロードファイル登録エラー：アップロードファイルなし */
	FILE_REGIST_ERROR_NOT_FOUND( "21_00_301" ),

	/** アップロードファイル登録エラー：アップロードファイルアクセスエラー */
	FILE_REGIST_ERROR_ACCESS_ERROR( "21_00_302" ),

	/** アップロードファイル登録エラー：ファイル移動エラー */
	FILE_REGIST_ERROR_MOVE_ERROR( "21_00_303" ),

	// -------------------------------------------------------------------------

	/** ファイルアクセスエラー */
	FILE_ERROR( "21_00_500" ),

	/** ファイルアクセスエラー：ファイルが存在しない */
	FILE_ERROR_NOT_FOUND( "21_00_501" ),

//	/** ファイルアクセスエラー：既に存在する */
//	FILE_ERROR_DUPLICATE( "21_00_502" ),
//
//	/** ファイルアクセスエラー：ディレクトリでない */
//	FILE_ERROR_NOT_DIRECTORY( "21_00_503" ),

	/** ファイルアクセスエラー：ファイルでない */
	FILE_ERROR_NOT_FILE( "21_00_504" ),

	/** ファイルアクセスエラー：作成失敗 */
	FILE_ERROR_CREATE( "21_00_505" ),

//	/** ファイルアクセスエラー：削除失敗 */
//	FILE_ERROR_DELETE( "21_00_506" ),
//
//	/** ファイルアクセスエラー：コピー失敗 */
//	FILE_ERROR_COPY( "21_00_507" ),
//
//	/** ファイルアクセスエラー：移動失敗 */
//	FILE_ERROR_MOVE( "21_00_508" ),

	// -------------------------------------------------------------------------
	// 個別エラー
	// -------------------------------------------------------------------------

	// ---------- 音声認識 ----------

	/** サポートされないファイル形式 */
	SPEECH_AUDIO_UNSUPPORTED_FILE ( "21_01_001" ),

	/** オーディオ形式不正 */
	SPEECH_AUDIO_INVALID_FORMAT ( "21_01_002" ),

	/** エンディアン不正 */
	SPEECH_AUDIO_INVALID_ENDIAN ( "21_01_003" ),

	/** 量子化ビット数不正 */
	SPEECH_AUDIO_INVALID_SAMPLE_SIZE_BIT ( "21_01_004" ),

	/** チャンネル数不正 */
	SPEECH_AUDIO_INVALID_CHANNELST ( "21_01_005" ),

	/** サンプリング周波数不正 */
	SPEECH_AUDIO_INVALID_SAMPLE_RATE ( "21_01_006" ),


	// ---------- リカイアス(認証) ----------

	/** リカイアス認証エラー */
	RECAIUS_AUTH_ERROR ( "21_02_001" ),

	/** リカイアストークン延長エラー */
	RECAIUS_AUTH_EXTENTION_TOKEN_ERROR ( "21_02_002" ),

	/** リカイアストークン情報取得エラー */
	RECAIUS_AUTH_GET_TOKEN_ERROR ( "21_02_003" ),

	/** リカイアストークン削除エラー */
	RECAIUS_AUTH_DELETE_TOKEN_ERROR ( "21_02_004" ),

	/** リカイアス認証異常終了 */
	RECAIUS_AUTH_ABEND_ERROR ( "21_02_005" ),


	// ---------- リカイアス(音声認識) ----------

	/** リカイアス音声認識開始エラー */
	RECAIUS_SPEECH_START_ERROR ( "21_03_001" ),

	/** リカイアス音声認識終了エラ */
	RECAIUS_SPEECH_END_ERROR ( "21_03_002" ),

	/** リカイアス音声認識データ送信エラー */
	RECAIUS_SPEECH_SEND_DATA_ERROR ( "21_03_003" ),

	/** リカイアス音声認識データ送信終了エラー */
	RECAIUS_SPEECH_FLUSH_ERROR ( "21_03_004" ),

	/** リカイアス音声認識異常終了 */
	RECAIUS_SPEECH_ABEND_ERROR ( "21_03_005" ),

	/** リカイアス音声認識ユーザ登録辞書取得エラー */
	RECAIUS_SPEECH_LEXICON_GET_ERROR ( "21_03_006" ),

	/** リカイアス音声認識ユーザ登録辞書更新エラー */
	RECAIUS_SPEECH_LEXICON_UPDATE_ERROR ( "21_03_007" ),

	/** リカイアス音声認識モデルID生成エラー */
	RECAIUS_SPEECH_CREATE_MODEL_ID_ERROR("21_03_008"),

	/** リカイアス音声認識モデルID破棄エラー */
	RECAIUS_SPEECH_DESTROY_MODEL_ID_ERROR("21_03_009"),

	/** リカイアス音声認識ベースモデルID一覧取得エラー */
	RECAIUS_SPEECH_GET_BASE_MODEL_ID_LIST_ERROR("21_03_010"),

	/** リカイアス音声認識モデルID超過エラー */
	RECAIUS_SPEECH_OVER_FLOW_MODEL_ID_ERROR("21_03_011"),


	// ---------- リカイアス(要約) ----------
	/** リカイアス要約エラー */
	RECAIUS_DIGEST_ERROR ( "21_04_001" ),

	/** リカイアス要約異常終了 */
	RECAIUS_DIGEST_ABEND_ERROR ( "21_04_002" ),


	// ---------- 通話 ----------
	/** 通話中エラー */
	CALL_CALLING_ERROR ( "21_05_001" ),

	/** 通話再開エラー */
	CALL_RESUME_ERROR ( "21_05_002" ),

	/** リカイアス利用者情報が取得できません */
	CALL_RECAIUS_ERROR ( "21_05_003" ),

	// ---------- ユーザ辞書更新 ----------

	/** CSVフォーマットエラー */
	LEXICON_CSV_FORMAT_ERROR ( "21_06_001" ),

	/** ユーザ辞書更新単語数過多 */
	LEXICON_TOO_MANY_WORDS ( "21_06_002" ),

	/** ユーザ辞書更新表記不正 */
	LEXICON_INVALID_SURFACE ( "21_06_003" ),

	/** ユーザ辞書更新読み不正 */
	LEXICON_INVALID_PRON ( "21_06_004" ),

	/** ユーザ辞書更新品詞ID不正 */
	LEXICON_INVALID_CLASS_ID ( "21_06_005" ),

	/** ユーザ辞書更新表記文字数超過 */
	LEXICON_SURFACE_TOO_LONG ( "21_06_006" ),

	/** ユーザ辞書更新読み文字数超過 */
	LEXICON_PRON_TOO_LONG ( "21_06_007" ),

	/** ユーザ辞書更新読みが空 */
	LEXICON_EMPTY_PRON ( "21_06_008" ),

	/** その他の入力エラー */
	LEXICON_INPUT_ERROR ( "21_06_009" ),

	/** 形態素解析辞書更新エラー */
	LEXICON_MONOPHOLOGICAL_ANALYZE_DICTIONARY_UPDATE_ERROR ( "21_06_010" ),

	// ---------- ユーザ辞書出力 ----------

	/** ユーザ辞書CSV出力失敗 */
	LEXICON_CSV_DOWNLOAD_ERROR ( "21_07_001" ),

    // ---------- リカイアスライセンス ----------

    /** サービス利用ID重複エラー */
    RECAIUS_LICENSE_DUPLICATE_SERVICE_ID("21_08_001"),

    /** リカイアスライセンス利用中エラー */
    RECAIUS_LICENSE_IN_USE_ERROR("21_08_002"),


    // ---------- 企業管理情報 ----------

    /** 企業ID重複エラー */
    COMPANY_MANAGEMENT_DUPLICATE_COMPANY_ID("21_09_001"),

    /** IP制限アドレス最大登録件数エラー */
    COMPANY_MANAGEMENT_PERMIT_IP_ADDRESS_MAX_COUNT_ERROR("21_09_002"),

    // ---------- 代理店管理情報 ----------

    /** 代理店企業ID重複エラー */
    AGENCY_DUPLICATE_COMPANY_ID("21_10_001"),


	// ---------- 音声解析ログ ----------

	/** 音声解析ログダウンロード音声なし */
	SPEECH_LOG_DWONLOAD_NO_VOICE("21_12_001"),

	/** 音声ファイルマージ失敗 */
	SPEECH_LOG_DWONLOAD_FAIL_MERGE("21_12_002"),

	/** 音声解析ログダウンロードファイルID不正 */
	SPEECH_LOG_DWONLOAD_INVALID_FILE_ID("21_12_003"),

	/** 音声解析ログダウンロード内容なし */
	SPEECH_LOG_DWONLOAD_NO_DATA("21_12_004"),



	// ---------- フィラー情報出力 ----------

	/** フィラー情報CSVフォーマット不正 */
	FILLER_CSV_FORMAT_ERROR( "21_13_001" ),

	/** フィラー情報CSV出力失敗 */
	FILLER_CSV_DOWNLOAD_ERROR ( "21_13_002" ),

	/** フィラー情報更新失敗 */
	FILLER_UPDATE_ERROR( "21_13_003" ),


	// ---------- パスワード管理 ----------

	/** 未認証エラー */
	PASSWORD_BIZ_NO_AUTH_ERROR ( "21_14_001" ),

	/** 認証用(bizAPI通信前)入力値エラー */
	PASSWORD_BIZ_AUTH_INPUT_ERROR ( "21_14_002" ),

	/** 認証用(bizAPI通信前)情報取得エラー （企業ID取得できないなど） */
	PASSWORD_BIZ_AUTH_PARAM_NOT_FOUND_ERROR ( "21_14_003" ),

	/** biz認証エラー */
	PASSWORD_BIZ_AUTH_ERROR ( "21_14_004" ),

	/** bizAPIエラー */
	PASSWORD_BIZ_API_ERROR ( "21_14_005" ),

	/** 確認用パスワード不一致エラー */
	PASSWORD_INPUT_CONFIRM_PASSWORD_DIFFERENT_ERROR ( "21_14_006" ),

	/** bizAPI入力値エラー (bizAPI側のエラー値返却時)*/
	PASSWORD_BIZ_API_VALIDATE_ERROR ( "21_14_007" ),

	// ---------- ログインユーザ管理 ----------

	/** パスワード変更時の旧パスワード不正 */
	LOGINUSER_OLDPASSWORD_INVALUD( "31_81_001" ),

	/** ログイン中ユーザの削除は不可 */
	LOGINUSER_SELF_DELETE( "31_81_002" ),

	;

	// -------------------------------------------------------------------------

	/** ログ出力種別（サーバ側のログ出力制御に使用） */
	public static enum Level
	{
		/** 正常終了 */ OK,
		/** 通知     */ NOTICE,
		/** 無視     */ IGNORE,
		/** 警告     */ WARN,
		/** エラー   */ ERROR
	} ;

	// -------------------------------------------------------------------------

	/** 結果コード */
	private final String code ;

	/** カテゴリ（ログレベル） */
	private Level level ;

	/** 処理結果をキーとしたマップ */
	private static Map<String, ResponseCode> resultMap ;

	static
	{
		RuntimeException error = null ;

		if ( resultMap == null )
		{
			resultMap = new HashMap<String, ResponseCode>() ;

			for ( ResponseCode res : values() )
			{
				ResponseCode old = resultMap.put( res.getCode(), res ) ;

				if ( old != null )
				{
					if ( error == null )
					{
						error = new RuntimeException( "Duplicate code." ) ;
					}

					error.addSuppressed( new RuntimeException(
						"'" + old.name() + "(" + old.getCode() + ")' is overwritten by" +
						"'" + res.name() + "(" + res.getCode() + ")' )" ) ) ;
				}
			}
		}

		if ( error != null ) throw error ;
	}

	/**
	 * コンストラクタ.
	 *
	 * @param code 結果コード
	 */
	private ResponseCode( String code )
	{
		this.code = code ;

		String catString = code.substring(0, 2); // 最初の 2 桁で判定

		if ( "00_00_000".equals( code ) ) level = Level.OK ;
		else if ( "00".equals( catString ) ) level = Level.NOTICE ;
		else if ( "01".equals( catString ) ) level = Level.IGNORE ;
		else if ( "11".equals( catString ) ) level = Level.WARN ;
		//21_番台でエラーログを出力しない場合は個別にコード追加する
		else if ( "31".equals( catString ) ) level = Level.IGNORE ;
		else level = Level.ERROR ;
	}

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() { return code ; }

	/**
	 * 処理結果取得
	 *
	 * @return 処理結果
	 */
	public String getCode() { return code ; }

	/**
	 * ログレベル取得
	 *
	 * @return ログレベル
	 */
	public Level getLevel() { return level ; }

	/**
	 * 結果コードから ResponseCode インスタンスを取得
	 *
	 * @param result 処理結果
	 * @return ApiResultCode インスタンス
	 */
	public static ResponseCode valueOfResult( String result )
	{
		return resultMap.get( result ) ;
	}
}
