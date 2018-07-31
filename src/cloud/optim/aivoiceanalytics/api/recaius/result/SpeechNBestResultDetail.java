/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechNBestResultDetail.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.recaius.result;

/**
 * 音声認識nbestモード時の結果詳細クラス
 * resultCount1にのみ対応
 */
public class SpeechNBestResultDetail {
	/** 解析結果 */
	private String result;

	/** 開始秒数 */
	private Integer begin;

	/** 終了秒数 */
	private Integer end;
	/**
	 * @return result
	 */
	public String getResult() {
		return result;
	}
	/**
	 * @param result セットする result
	 */
	public void setResult(String result) {
		this.result = result;
	}
	/**
	 * @return begin
	 */
	public Integer getBegin() {
		return begin;
	}
	/**
	 * @param begin セットする begin
	 */
	public void setBegin(Integer begin) {
		this.begin = begin;
	}
	/**
	 * @return end
	 */
	public Integer getEnd() {
		return end;
	}
	/**
	 * @param end セットする end
	 */
	public void setEnd(Integer end) {
		this.end = end;
	}
}
