/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：ErrorResult.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.recaius.result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;

/**
 * リカイアスエラー結果クラス
 */
public class ErrorResult {
	/** エラーコード */
	private int code;

	/** エラー文字列 */
	private String message;

	/** RFC3339形式で以下のように、UTC時刻を記述する。 ”yyyy-MM-ddTHH:mm:ssZ”*/
	private String timestamp;

	/** 追加情報（必要に応じて出力されます） */
	private String more_info;

	/** 追加情報（必要に応じて出力されます）JSON */
	private Object more_info_obj;

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString()
	{
		return ToStringHelper.toString( this, "more_info_obj" );
	}

	/**
	 * @return code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code セットする code
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message セットする message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp セットする timestamp
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return more_info
	 */
	public String getMore_info() {
		return more_info;
	}

	/**
	 * @param more_info セットする more_info
	 */
	public void setMore_info(String more_info) {
		this.more_info = more_info;
	}

	/**
	 * @return more_info_obj
	 */
	public Object getMore_info_obj() {
		return more_info_obj;
	}

	/**
	 * more_info_objを指定した型のリストとして取得する
	 * @return more_info_obj
	 * @throws Exception
	 * @throws
	 */
	public <T> List<T>  toListMoreInfoObj (Class<T> type) throws Exception {
		if (more_info_obj == null) return new ArrayList<>();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) more_info_obj;

		List<T> result = new ArrayList<>();
		for (Map<String, Object> each : list) {
			T object = type.newInstance();
			BeanUtils.populate(object, each);
			result.add(object);
		}
		return result;
	}

	/**
	 * @param more_info_obj セットする more_info_obj
	 */
	public void setMore_info_obj(Object more_info_obj) {
		this.more_info_obj = more_info_obj;
	}


}
