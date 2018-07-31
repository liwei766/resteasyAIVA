/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：KnowledgeRequest.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.lexicon;

import javax.xml.bind.annotation.XmlRootElement;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;

/**
 * Knowledge API リクエストクラス.<br/>
 */
@XmlRootElement( name="restRequest" )
public class LexiconRequest implements java.io.Serializable {

	/** serialVersionUID  */
	private static final long serialVersionUID = 1L;

	private Integer modelId;

	// -------------------------------------------------------------------------

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString()
	{
		return ToStringHelper.toString( this );
	}

	/**
	 * @return modelId
	 */
	public Integer getModelId() {
		return modelId;
	}

	/**
	 * @param modelId セットする modelId
	 */
	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}

}
