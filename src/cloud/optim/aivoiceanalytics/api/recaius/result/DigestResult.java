/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：DigestResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.recaius.result;

import java.util.List;

/**
 * リカイアス要約結果クラス
 */
public class DigestResult {

	/** 解析結果 */
	private List<Sentences> sentences;

	/**
	 * @return sentences
	 */
	public List<Sentences> getSentences() {
		return sentences;
	}

	/**
	 * @param sentences セットする sentences
	 */
	public void setSentences(List<Sentences> sentences) {
		this.sentences = sentences;
	}


	// -------------------------------------------------------------------------
	// 内部クラス
	// -------------------------------------------------------------------------

	/** 一括処理結果 */
	public static final class Sentences {

		/** 要約テキスト */
		private String text;

		/** ランク */
		private int rank;

		/**
		 * @return text
		 */
		public String getText() {
			return text;
		}

		/**
		 * @param text セットする text
		 */
		public void setText(String text) {
			this.text = text;
		}

		/**
		 * @return rank
		 */
		public int getRank() {
			return rank;
		}

		/**
		 * @param rank セットする rank
		 */
		public void setRank(int rank) {
			this.rank = rank;
		}
	}
}
