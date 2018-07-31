/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：LexiconGetResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.recaius.result;

import java.util.List;

import cloud.optim.aivoiceanalytics.api.recaius.Lexicon;

/**
 * リカイアスユーザ辞書取得結果クラス
 */
public class LexiconGetResult {


	/** 登録単語リスト */
	List<Lexicon> ulex;

	/**
	 * @return ulex
	 */
	public List<Lexicon> getUlex() {
		return ulex;
	}

	/**
	 * @param ulex セットする ulex
	 */
	public void setUlex(List<Lexicon> ulex) {
		this.ulex = ulex;
	}
}
