package cloud.optim.aivoiceanalytics.core.modules.rest.app.login;

import javax.xml.bind.annotation.XmlElement ;
import javax.xml.bind.annotation.XmlRootElement ;

import com.fasterxml.jackson.annotation.JsonProperty ;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;

/**
 * Login API リクエストクラス（現在使用していない）.
 *
 * @author itsukaha
 */
@XmlRootElement( name="restRequest" )
public class LoginRequest
{
	/** ユーザ ID */
	private String userId = "" ;

	/** パスワード */
	private String password = "" ;

	// -------------------------------------------------------------------------

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString()
	{
		return ToStringHelper.toString( this, "password" ) ;
	}

	// -------------------------------------------------------------------------
	// アクセサメソッド
	// -------------------------------------------------------------------------

	/**
	 * userId 取得.
	 *
	 * @return userId
	 */
	@JsonProperty( "j_username" )
	@XmlElement( name="j_username" )
	public String getUserId()
	{
		return userId ;
	}

	/**
	 * userId 設定.
	 *
	 * @param userId userId に設定する値.
	 */
	@JsonProperty( "j_username" )
	@XmlElement( name="j_username" )
	public void setUserId( String userId )
	{
		this.userId = userId ;
	}

	/**
	 * password 取得.
	 *
	 * @return password
	 */
	@JsonProperty( "j_password" )
	@XmlElement( name="j_password" )
	public String getPassword()
	{
		return password ;
	}

	/**
	 * password 設定.
	 *
	 * @param password password に設定する値.
	 */
	@JsonProperty( "j_password" )
	@XmlElement( name="j_password" )
	public void setPassword( String password )
	{
		this.password = password ;
	}
}
