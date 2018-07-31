package cloud.optim.aivoiceanalytics.core.modules.oauth;

import java.util.Map;

/**
 * 認証サーバから取得したユーザ情報を扱うためのクラス
 *
 * @author itsukaha
 */
public class AuthMapUtil
{
	// ---------- ユーザ情報

	/** 項目名：ログインユーザ情報 */
	private static final String USER = "current_user";

	/** 項目名：ログインユーザ情報．ユーザ ID */
	private static final String USER_ID = "user_id";

	/** 項目名：ログインユーザ情報．ユーザ名 */
	private static final String USER_NAME = "name";

	/** 項目名：ログインユーザ情報．権限名 */
	private static final String ROLE = "role";

	// ---------- 企業情報情報

	/** 項目名：ログインユーザの所属情報 */
	private static final String USER_GROUP = "current_user_group";

	/** 項目名：ログインユーザの所属情報．企業 ID */
	private static final String COMPANY_ID = "company_code";

	/**
	 * ユーザ情報の項目を取得する
	 *
	 * @param authMap 認証サーバから取得したユーザ情報
	 * @param name 取得する項目名
	 *
	 * @return 指定した項目名（存在しないとき null）
	 */
	@SuppressWarnings( "unchecked" )
	public static String getInfo( Map<String, Object> authMap, String name1, String name2 )
	{
		try
		{
			return ((Map<String, Object>)authMap.get( name1 )).get( name2 ).toString();
		}
		catch ( Exception ex )
		{
			return null; // 項目が存在しない または 認証情報の構成が想定と異なる場合
		}
	}

	// ---------- 個別情報抽出処理

	/**
	 * ユーザ ID 取得
	 *
	 * @param authMap 認証サーバから取得したユーザ情報
	 * @return ユーザ ID
	 */
	public static String userId( Map<String, Object> authMap )
	{
		return getInfo( authMap, USER, USER_ID );
	}

	/**
	 * ユーザ名取得
	 *
	 * @param authMap 認証サーバから取得したユーザ情報
	 * @return ユーザ名
	 */
	public static String userName( Map<String, Object> authMap )
	{
		return getInfo( authMap, USER, USER_NAME );
	}

	/**
	 * 権限名取得
	 *
	 * @param authMap 認証サーバから取得したユーザ情報
	 * @return 権限名
	 */
	public static String role( Map<String, Object> authMap )
	{
		return getInfo( authMap, USER, ROLE );
	}

	/**
	 * 企業 ID 取得
	 *
	 * @param authMap 認証サーバから取得したユーザ情報
	 * @return ユーザ名
	 */
	public static String companyId( Map<String, Object> authMap )
	{
		return getInfo( authMap, USER_GROUP, COMPANY_ID );
	}
}
