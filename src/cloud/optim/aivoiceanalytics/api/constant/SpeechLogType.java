/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechLogType.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.constant;

/**
 * 音声解析ログTYPEの列挙型
 */
public enum SpeechLogType {

	MIC("0"),
	FILE("1");

	private String value;

	SpeechLogType (String value) {
		this.value = value;
	}

	public String getValue(){
		return this.value;
	}

	public boolean matches(String value) {
		return this.value.equals(value);
	}
}

