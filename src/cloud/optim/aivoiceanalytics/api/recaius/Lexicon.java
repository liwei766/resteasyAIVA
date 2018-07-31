/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：Lexicons.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.recaius;

public class Lexicon {

	/** 表記 */
	private String surface;

	/** 読み方 */
	private String pron;

	/** 品詞ID */
	private Integer class_id;

	/**
	 * @return surface
	 */
	public String getSurface() {
		return surface.trim();
	}

	/**
	 * @param surface セットする surface
	 */
	public void setSurface(String surface) {
		this.surface = surface;
	}

	/**
	 * @return pron
	 */
	public String getPron() {
		return pron;
	}

	/**
	 * @param pron セットする pron
	 */
	public void setPron(String pron) {
		this.pron = pron.trim();
	}

	/**
	 * @return class_id
	 */
	public Integer getClass_id() {
		return class_id;
	}

	/**
	 * @param class_id セットする class_id
	 */
	public void setClass_id(Integer class_id) {
		this.class_id = class_id;
	}
}
