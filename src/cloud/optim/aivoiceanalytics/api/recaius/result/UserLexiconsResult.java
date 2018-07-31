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

import cloud.optim.aivoiceanalytics.api.recaius.UserLexicon;
import java.util.List;

/**
 * リカイアスモデルリスト結果クラス.
 */
public class UserLexiconsResult {

  /** モデルリスト. */
  List<UserLexicon> userLexicons;

  /**
   * モデルリスト取得.
   * 
   * @return ベースモデルリスト
   */
  public List<UserLexicon> getUserLexicons() {
    return this.userLexicons;
  }

  /**
   * モデルリスト設定.
   * 
   * @param userLexicons ベースモデルリスト
   */
  public void setUserLexicons(List<UserLexicon> userLexicons) {
    this.userLexicons = userLexicons;
  }
}
