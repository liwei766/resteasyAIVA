/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.constant;

/**
 * 通話ログステータスの列挙型
 */
public enum UseTimeType {
	SPEECH("1"),
	DIGEST("2");

	private String value;

	UseTimeType (String value) {
		this.value = value;
	}

	public String getValue(){
		return this.value;
	}
}

