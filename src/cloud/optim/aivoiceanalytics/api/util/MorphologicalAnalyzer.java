/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：MorphologicalAnalyzer.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.util;

import java.util.List;

public interface MorphologicalAnalyzer {

	public List<String> extractNouns(String text, boolean reverse);

	public String removeFiller(String text);

	public void reloadUserDictionary() throws Exception;
}
