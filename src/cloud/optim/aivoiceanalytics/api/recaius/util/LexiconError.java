/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：LexiconError.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.recaius.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import cloud.optim.aivoiceanalytics.core.modules.rest.ResponseCode;

/**
 * ユーザ辞書登録エラーメッセージの列挙型
 */
public enum LexiconError {
	INVALID_SURFACE("invalid surface", ResponseCode.LEXICON_INVALID_SURFACE),
	INVALID_PRON("invalid pron", ResponseCode.LEXICON_INVALID_PRON),
	INVALID_CLASS_ID("invalid class_id", ResponseCode.LEXICON_INVALID_CLASS_ID),
	SURFACE_TOO_LONG("surface is too long", ResponseCode.LEXICON_SURFACE_TOO_LONG),
	PRON_TOO_LONG("pron is too long", ResponseCode.LEXICON_PRON_TOO_LONG),
	EMPTY_PRON("empty pron", ResponseCode.LEXICON_EMPTY_PRON);

	/** リカイアスが返却するエラーメッセージ */
	private String errorMessage;

	/** エラーメッセージに対応するレスポンスコード */
	private ResponseCode responseCode;

	/**
	 * コンストラクタ
	 * @param errorMessage エラーメッセージ
	 * @param responseCode レスポンスコード
	 */
	LexiconError (String errorMessage, ResponseCode responseCode) {
		this.errorMessage = errorMessage;
		this.responseCode = responseCode;
	}

	//マッピング用のマップ生成
	private static final Map<String, LexiconError> mappings = new HashMap<>(6);
	static {
		for(LexiconError each : values()) {
			mappings.put(each.errorMessage, each);
		}
	}

	/**
	 * エラーメッセージからレスポンスコードを取得する。マッチするメッセージが無い場合は入力エラーのコードを返す。
	 * @param errorMessage エラーメッセージ
	 * @return レスポンスコード
	 */
	public static ResponseCode getResponseCode (String errorMessage) {
		if(StringUtils.isEmpty(errorMessage)) return ResponseCode.LEXICON_INPUT_ERROR;
		LexiconError error = mappings.get(errorMessage);
		return error == null ? ResponseCode.LEXICON_INPUT_ERROR : error.responseCode;
	}
}
