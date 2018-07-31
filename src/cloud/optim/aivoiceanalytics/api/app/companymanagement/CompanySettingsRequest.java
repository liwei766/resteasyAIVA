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
package cloud.optim.aivoiceanalytics.api.app.companymanagement;

import javax.xml.bind.annotation.XmlRootElement;

import cloud.optim.aivoiceanalytics.api.entity.CompanyManagement;
import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;



/**
 * Company API リクエストクラス.<br/>
 */
@XmlRootElement(name = "restRequest")
public class CompanySettingsRequest implements java.io.Serializable {

		/** serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** 1 エンティティ情報. */
		private CompanyManagement companyManagement;

		/**
		 * 文字列表現への変換.
		 *
		 * @return 文字列表現
		 */
		@Override
		public String toString() {
			return ToStringHelper.toString(this);
		}

		/**
		 * companyManagement 取得.
		 *
		 * @return companyManagement
		 */
		public CompanyManagement getCompanyManagement() {
			return companyManagement;
		}

		/**
		 * companyManagement 設定.
		 *
		 * @param companyManagement
		 *            companyManagement に設定する値.
		 */
		public void setCompanyManagement(CompanyManagement companyManagement) {
			this.companyManagement = companyManagement;
		}

}
