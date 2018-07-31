/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：CustomUser.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.modules.loginutil;

import java.io.Serializable ;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List ;

import cloud.optim.aivoiceanalytics.api.entity.CompanyManagement;
import cloud.optim.aivoiceanalytics.api.entity.RecaiusLicense;
import cloud.optim.aivoiceanalytics.core.common.utility.Cryptor;
import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper ;

/**
 * ログインユーザ拡張情報クラス.
 * ログイン成功時に追加で取得し、セッション上に保持する情報
 *
 * @author itsukaha
 */
public class CustomUser implements Serializable
{
	/** serialVersionUID */
	private static final long serialVersionUID = 1L ;

	/** 企業ID */
	private String companyId ;

	/** ユーザID */
	private String userId ;

	/** ユーザ名 */
	private String userName ;

	/** 権限 ID リスト */
	private List<String> authList ;

	/** 企業管理情報 */
	private CompanyManagement companyInfo;

	/** リカイアスライセンス情報 */
	private RecaiusLicense recaiusLicense;

	/** 復号化キー */
	private String decryptKey;

	/** OptimalBiz企業GUID */
	private String companyGuid ;

	/** OptimalBiz 認証トークン */
	private String optimalBizToken ;

	/** OptimalBiz 認証トークンシークレット */
	private String optimalBizTokenSecret ;

	// -------------------------------------------------------------------------
	// コンストラクタ
	// -------------------------------------------------------------------------
	public CustomUser() {}
	/**
	 * コンストラクタ.
	 *
	 * @param companyId 企業コード
	 * @param userId ログインユーザ ID
	 * @param userName ログインユーザ名
	 * @param authIdList 権限リスト
	 * @param companyInfo 企業管理情報
	 * @param recaiusLicense リカイアスライセンス情報
	 */
	public CustomUser( String companyId, String userId, String userName, List<String> authList, CompanyManagement companyInfo, RecaiusLicense recaiusLicense ) {
		this.companyId = companyId;
		this.userId = userId;
		this.userName = userName;
		this.authList = authList;
		this.companyInfo = companyInfo;
		this.recaiusLicense = recaiusLicense;
	}

	/**
	 * コンストラクタ.
	 *
	 * @param companyId 企業コード
	 * @param userId ログインユーザ ID
	 * @param userName ログインユーザ名
	 * @param authIdList 権限リスト
	 * @param customUser ログインユーザ拡張情報
	 * @param companyInfo 企業管理情報
	 * @param recaiusLicense リカイアスライセンス情報
	 */
	public CustomUser( String companyId, String userId, String userName, Collection<String> authList, CustomUser customUser, CompanyManagement companyInfo, RecaiusLicense recaiusLicense  )
	{
		if ( customUser != null )
		{
			if ( companyId == null ) companyId = customUser.getCompanyId() ;
			if ( userId == null ) userId = customUser.getUserId() ;
			if ( userName == null ) userName = customUser.getUserName() ;
			if ( authList == null ) authList = customUser.getAuthList() ;
		}

		this.companyId = companyId ;
		this.userId = userId ;
		this.userName = userName ;
		this.authList = Collections.unmodifiableList( new ArrayList<String>( authList ) ) ;
		this.companyInfo = companyInfo;
		this.recaiusLicense = recaiusLicense;
	}

	// -------------------------------------------------------------------------
	// その他の処理
	// -------------------------------------------------------------------------

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() { return ToStringHelper.toString( this ) ; }

	// -------------------------------------------------------------------------
	// アクセサメソッド
	// -------------------------------------------------------------------------
	/**
	 * companyId 取得.
	 *
	 * @return companyId
	 */
	public String getCompanyId() {
		return companyId;
	}

	/**
	 * userId 取得.
	 *
	 * @return userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * userName 取得.
	 *
	 * @return userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * authList 取得.
	 *
	 * @return authList
	 */
	public List<String> getAuthList() {
		return authList;
	}

	/**
	 * 企業管理ID取得.
	 *
	 * @return 企業管理ID
	 */
	public Long getCompanyManagementId () {
		if (companyInfo == null) return null;
		return companyInfo.getCompanyManagementId();
	}

	/**
	 * 企業名 取得.
	 *
	 * @return 企業名
	 */
	public String getCompanyName() {
		if (companyInfo == null) return null;
		return companyInfo.getCompanyName();
	}

	/**
	 * リカイアスサービス利用ID 取得.
	 *
	 * @return リカイアスサービス利用ID
	 */
	public String getRecaiusServiceId() {
		if (recaiusLicense == null) return null;
		return Cryptor.decrypt(decryptKey, recaiusLicense.getServiceId());
	}

	/**
	 * リカイアスパスワード 取得.
	 *
	 * @return リカイアスパスワード
	 */
	public String getRecaiusPassword() {
		if (recaiusLicense == null) return null;
		return Cryptor.decrypt(decryptKey, recaiusLicense.getPassword());
	}

	/**
	 * リカイアスモデルID 取得.
	 *
	 * @return リカイアスモデルID
	 */
	public Integer getRecaiusModelId() {
		if (companyInfo == null) return null;
		return companyInfo.getRecaiusModelId();
	}

	/**
	 * リカイアス音声判断レベル閾値取得.
	 *
	 * @return リカイアス音声判断レベル閾値
	 */
	public Integer getRecaiusEnergyThreshold() {
		if (companyInfo == null) return null;
		return companyInfo.getEnergyThreshold();
	}
	/**
	 * @param decryptKey セットする decryptKey
	 */
	public void setDecryptKey(String decryptKey) {
		this.decryptKey = decryptKey;
	}

	/**
	 * 音声保存設定を取得する
	 * @return 音声保存設定
	 */
	public boolean isSaveVoice() {
		if(this.companyInfo == null) return false;
		return this.companyInfo.getSaveVoice() == null ? false : this.companyInfo.getSaveVoice();
	}
	/**
	 * @return companyGuid
	 */
	public String getCompanyGuid() {
		return companyGuid;
	}
	/**
	 * @param companyGuid セットする companyGuid
	 */
	public void setCompanyGuid(String companyGuid) {
		this.companyGuid = companyGuid;
	}
	/**
	 * @return optimalBizToken
	 */
	public String getOptimalBizToken() {
		return optimalBizToken;
	}
	/**
	 * @param optimalBizToken セットする optimalBizToken
	 */
	public void setOptimalBizToken(String optimalBizToken) {
		this.optimalBizToken = optimalBizToken;
	}
	/**
	 * @return optimalBizTokenSecret
	 */
	public String getOptimalBizTokenSecret() {
		return optimalBizTokenSecret;
	}
	/**
	 * @param optimalBizTokenSecret セットする optimalBizTokenSecret
	 */
	public void setOptimalBizTokenSecret(String optimalBizTokenSecret) {
		this.optimalBizTokenSecret = optimalBizTokenSecret;
	}


}