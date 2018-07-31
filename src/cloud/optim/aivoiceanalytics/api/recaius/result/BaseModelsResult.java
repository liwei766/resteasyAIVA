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

import cloud.optim.aivoiceanalytics.api.recaius.BaseModel;
import java.util.List;

/**
 * リカイアスモデルリスト結果クラス.
 */
public class BaseModelsResult {


  /** モデルリスト. */
  List<BaseModel> models;

  /**
   * ベースモデルリスト取得.
   * 
   * @return ベースモデルリスト
   */
  public List<BaseModel> getModels() {
    return this.models;
  }

  /**
   * ベースモデルリスト設定.
   * 
   * @param models ベースモデルリスト
   */
  public void setModels(List<BaseModel> models) {
    this.models = models;
  }
}
