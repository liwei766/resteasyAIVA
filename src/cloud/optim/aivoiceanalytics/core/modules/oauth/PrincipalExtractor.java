package cloud.optim.aivoiceanalytics.core.modules.oauth;

import java.util.Map;

/**
 * ユーザ ID 抽出クラス
 *
 * @author itsukaha
 */
public class PrincipalExtractor {

	/**
	 * 認証情報からユーザ ID を抽出して返却する.
	 *
	 * @param authInfo 認証情報
	 * @return ユーザ ID
	 */
	public Object extractPrincipal( Map<String, Object> authInfo )
	{
		Object userId = AuthMapUtil.userId( authInfo );

		return userId;
	}
}