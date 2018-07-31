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

import java.util.Date;

public class UserLexicon {

	/** モデルID. */
	private Integer model_id;

    /** ベースモデルID. */
    private Integer base_model_id;

	/** この辞書に登録された単語の数. */
	private Integer entry_num;

    /** リビジョン. */
    private Integer revision;

    /** 最終更新日時. */
    private Date last_updated;

    /** ユーザ辞書の説明分. */
    private String description;

    /** モデルが対応する言語. */
    private String language;

    /** 対応するサンプリング周波数. */
    private Integer sample_rate;


    /** モデルID取得. */
    public int getModelId() {
      return this.model_id;
    }

    /** モデルID設定. */
    public void setModelId(final int modelId) {
      this.model_id = modelId;
    }

    public Integer getModel_id() {
      return model_id;
    }

    public void setModel_id(Integer model_id) {
      this.model_id = model_id;
    }

    public Integer getBase_model_id() {
      return base_model_id;
    }

    public void setBase_model_id(Integer base_model_id) {
      this.base_model_id = base_model_id;
    }

    public Integer getEntry_num() {
      return entry_num;
    }

    public void setEntry_num(Integer entry_num) {
      this.entry_num = entry_num;
    }

    public Integer getRevision() {
      return revision;
    }

    public void setRevision(Integer revision) {
      this.revision = revision;
    }

    public Date getLast_updated() {
      return last_updated;
    }

    public void setLast_updated(Date last_updated) {
      this.last_updated = last_updated;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getLanguage() {
      return language;
    }

    public void setLanguage(String language) {
      this.language = language;
    }

    public Integer getSample_rate() {
      return sample_rate;
    }

    public void setSample_rate(Integer sample_rate) {
      this.sample_rate = sample_rate;
    }
}
