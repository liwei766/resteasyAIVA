/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechNBestResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.recaius.result;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 音声認識nbestモード時の結果クラス
 * resultCount1にのみ対応
 */
public class SpeechNBestResult {

	/** 解析結果種別  */
	private String type;

	/** ステータス */
	private String status;

	/** 解析結果 解析結果の種別により型が変わるのでObject型で定義する */
	private Object result;

	/** 結果詳細 resultの内容をセットする */
	private SpeechNBestResultDetail resultDetail;

	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type セットする type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status セットする status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return result
	 */
	public Object getResult() {
		return this.result;
	}

	/**
	 * @param result セットする result
	 */
	public void setResult(Object result) {
		this.result = result;
		this.resultDetail = new SpeechNBestResultDetail();
		if (result instanceof String) {
			this.resultDetail.setResult((String) result);
		} else {
			// リカイアスにnbestモードでresultCount1なので必ず1件の結果になる
			@SuppressWarnings("unchecked")
			LinkedHashMap<String, Object> details = ((List<LinkedHashMap<String, Object>>) result).get(0);
			this.resultDetail.setResult((String) details.get("str"));

			// 開始、終了時間の取得
			@SuppressWarnings("unchecked")
			List<LinkedHashMap<String, Object>> words = (List<LinkedHashMap<String, Object>>) details.get("words");
			this.resultDetail.setBegin((Integer) words.get(0).get("begin"));
			this.resultDetail.setEnd((Integer) words.get(words.size() - 1).get("end"));
		}
	}

	/**
	 * @return resultDetail
	 */
	public SpeechNBestResultDetail getResultDetail() {
		return resultDetail;
	}

	/**
	 * @param resultDetail セットする resultDetail
	 */
	public void setResultDetail(SpeechNBestResultDetail resultDetail) {
		this.resultDetail = resultDetail;
	}
}
