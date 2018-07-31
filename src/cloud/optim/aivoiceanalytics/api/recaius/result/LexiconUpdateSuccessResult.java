/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：AuthResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.recaius.result;

/**
 * リカイアス認証処理結果クラス
 */
public class LexiconUpdateSuccessResult {

	/** ベースモデルID */
	private String base_model_id;

	/** 説明 */
	private String description;

	/** 登録件数 */
	private int entry_num;

	/** 言語 */
	private String language;

	/** 最終更新日時 */
	private String last_updated;

	/** モデルID */
	private int model_id;

	/** リビジョン */
	private int revision;

	/** サンプリングレート */
	private int sample_rate;

	/**
	 * @return base_model_id
	 */
	public String getBase_model_id() {
		return base_model_id;
	}

	/**
	 * @param base_model_id セットする base_model_id
	 */
	public void setBase_model_id(String base_model_id) {
		this.base_model_id = base_model_id;
	}

	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description セットする description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return entry_num
	 */
	public int getEntry_num() {
		return entry_num;
	}

	/**
	 * @param entry_num セットする entry_num
	 */
	public void setEntry_num(int entry_num) {
		this.entry_num = entry_num;
	}

	/**
	 * @return language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language セットする language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return last_updated
	 */
	public String getLast_updated() {
		return last_updated;
	}

	/**
	 * @param last_updated セットする last_updated
	 */
	public void setLast_updated(String last_updated) {
		this.last_updated = last_updated;
	}

	/**
	 * @return model_id
	 */
	public int getModel_id() {
		return model_id;
	}

	/**
	 * @param model_id セットする model_id
	 */
	public void setModel_id(int model_id) {
		this.model_id = model_id;
	}

	/**
	 * @return revision
	 */
	public int getRevision() {
		return revision;
	}

	/**
	 * @param revision セットする revision
	 */
	public void setRevision(int revision) {
		this.revision = revision;
	}

	/**
	 * @return sample_rate
	 */
	public int getSample_rate() {
		return sample_rate;
	}

	/**
	 * @param sample_rate セットする sample_rate
	 */
	public void setSample_rate(int sample_rate) {
		this.sample_rate = sample_rate;
	}

}
