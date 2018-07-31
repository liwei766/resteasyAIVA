package cloud.optim.aivoiceanalytics.core.modules.rest.app.login;

import java.util.ArrayList ;
import java.util.List ;

import javax.xml.bind.annotation.XmlElement ;
import javax.xml.bind.annotation.XmlElementWrapper ;
import javax.xml.bind.annotation.XmlRootElement ;

import com.fasterxml.jackson.annotation.JsonProperty;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;
import cloud.optim.aivoiceanalytics.core.modules.loginutil.CustomUser;
import cloud.optim.aivoiceanalytics.core.modules.rest.RestResult;

/**
 * Login サービスのレスポンス.<br />
 * result : 処理結果（常に設定）
 *
 * @author itsukaha
 */
@XmlRootElement( name="restResponse" )
public class LoginResponse
{
	/** 処理結果 */
	private List<RestResult> resultList = new ArrayList<RestResult>() ;

	/** 認証情報 */
	private CustomUserCopy loginInfo ;

	// -------------------------------------------------------------------------

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString()
	{
		return ToStringHelper.toString( this ) ;
	}

	// -------------------------------------------------------------------------
	// 処理結果を扱う処理
	// -------------------------------------------------------------------------

	/**
	 * 処理結果を登録する.
	 *
	 * @param result 登録する処理結果
	 */
	public void addResult( RestResult result )
	{
		if ( resultList == null )
		{
			resultList = new ArrayList<RestResult>() ;
		}

		resultList.add( result ) ;
	}

	// -------------------------------------------------------------------------

	/**
	 * ログインユーザ情報のコピーを格納するためのクラス.
	 *
	 * <i>
	 * XML プロバイダはデフォルトコンストラクタが定義されているクラスのみ
	 * 扱うことが可能<br/>
	 * CustomUser クラスはデフォルトコンストラクタが存在しないので
	 * LoginResponse ではコピーを扱うようにする.
	 * </i>
	 *
	 * @author itsukaha
	 */
	public static class CustomUserCopy
	{
		/** 企業ID */
		private String companyId ;

		/** ユーザID */
		private String userId ;

		/** ユーザ名 */
		private String userName ;

		/** 音声保存設定 */
		private boolean saveVoice;

		/** 権限 ID リスト */
		private List<String> authList ;

		/**
		 * 文字列表現への変換
		 *
		 * @return 文字列表現
		 */
		@Override
		public String toString()
		{
			return ToStringHelper.toString( this ) ;
		}

		/**
		 * companyId 取得.
		 *
		 * @return companyId
		 */
		public String getCompanyId()
		{
			return companyId;
		}

		/**
		 * companyId 設定.
		 *
		 * @param companyId companyId への設定値.
		 */
		public void setCompanyId( String companyId )
		{
			this.companyId = companyId;
		}

		/**
		 * userId 取得.
		 *
		 * @return userId
		 */
		public String getUserId()
		{
			return userId ;
		}

		/**
		 * userId 設定.
		 *
		 * @param userId userId への設定値.
		 */
		public void setUserId( String userId )
		{
			this.userId = userId ;
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
		 * userName 設定.
		 *
		 * @param userName userName への設定値.
		 */
		public void setUserName( String userName )
		{
			this.userName = userName;
		}


		/**
		 * @return saveVoice
		 */
		public boolean isSaveVoice() {
			return saveVoice;
		}

		/**
		 * @param saveVoice セットする saveVoice
		 */
		public void setSaveVoice(boolean saveVoice) {
			this.saveVoice = saveVoice;
		}

		/**
		 * authList 取得.
		 *
		 * @return authList
		 */
		@XmlElementWrapper( name="authIdList" )
		@XmlElement( name="authId" )
		@JsonProperty( "authIdList" )
		public List<String> getAuthIdList()
		{
			return authList ;
		}

		/**
		 * authList 設定.
		 *
		 * @param authList authList への設定値.
		 */
		public void setAuthIdList( List<String> authList )
		{
			this.authList = authList ;
		}
	}

	// -------------------------------------------------------------------------
	// アクセサメソッド
	// -------------------------------------------------------------------------

	/**
	 * resultList 取得.
	 *
	 * @return resultList
	 */
	@XmlElementWrapper( name="resultList" )
	@XmlElement( name="result" )
	@JsonProperty( "resultList" )
	public List<RestResult> getResultList()
	{
		return resultList ;
	}

	/**
	 * resultList 設定.
	 *
	 * @param resultList resultList に設定する値.
	 */
	public void setResultList( List<RestResult> resultList )
	{
		this.resultList = resultList ;
	}

	/**
	 * loginInfo 取得.
	 *
	 * @return loginInfo
	 */
	public CustomUserCopy getLoginInfo()
	{
		return loginInfo ;
	}

	/**
	 * loginInfo 設定.
	 *
	 * @param loginInfo loginInfo に設定する値.
	 */
	public void setLoginInfo( CustomUserCopy loginInfo )
	{
		this.loginInfo = loginInfo ;
	}

	/**
	 * loginInfo 設定.
	 *
	 * @param loginInfo loginInfo に設定する値.
	 */
	public void setCustomUser( CustomUser loginInfo )
	{
		CustomUserCopy copy = new CustomUserCopy() ;

		copy.setCompanyId( loginInfo.getCompanyId());
		copy.setUserId( loginInfo.getUserId() ) ;
		copy.setUserName( loginInfo.getUserName() );
		copy.setSaveVoice( loginInfo.isSaveVoice() );
		copy.setAuthIdList( loginInfo.getAuthList() ) ;

		this.loginInfo = copy ;
	}
}
