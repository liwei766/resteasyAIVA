/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：ExtractUtil.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.util;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

/**
 * キーワード抽出Util
 * @author raifuyor
 *
 */
@Component
public class ExtractUtil {

	/** 形態素解析エンジン */
	@Resource private MorphologicalAnalyzer morphologicalAnalyzer;

	/**
	 * テキスト先頭から解析して名詞を抽出する
	 * @param text 解析テキスト
	 * @return 抽出した名詞
	 */
	public List<String> extractNouns(String text) {
		return morphologicalAnalyzer.extractNouns(text, false);
	}

	/**
	 * テキスト末尾から解析して名詞を抽出する
	 * @param text 解析テキスト
	 * @return 抽出した名詞
	 */
	public List<String> extractNounsReverse(String text) {
		return morphologicalAnalyzer.extractNouns(text, true);
	}

	/**
	 * フィラー品詞を除去する
	 * @param text 解析テキスト
	 * @return 除去した解析テキスト
	 */
	public String removeFiller(String text) {
		return morphologicalAnalyzer.removeFiller(text);
	}

	/**
	 * ユーザ辞書を更新する
	 * @throws Exception
	 */
	public void reloadUserDictionary() throws Exception {
		morphologicalAnalyzer.reloadUserDictionary();
	}
}
