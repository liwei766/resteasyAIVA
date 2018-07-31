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

public class BaseModel {

	/** モデルID. */
	private Integer model_id;

    /** 最終更新日時. */
    private Date last_updated;

    /** ユーザ辞書の説明分. */
    private String description;

    /** モデルが対応する言語. */
    private String language;

    /** この辞書に登録された単語の数. */
	private Boolean support_user_lexicon;

    /** 対応するサンプリング周波数. */
    private Integer sample_rate;
}
