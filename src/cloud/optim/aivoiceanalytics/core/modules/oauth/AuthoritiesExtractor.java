package cloud.optim.aivoiceanalytics.core.modules.oauth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.StringUtils;

import cloud.optim.aivoiceanalytics.api.entity.Agency;
import cloud.optim.aivoiceanalytics.api.entity.dao.AgencyDao;
import cloud.optim.aivoiceanalytics.core.Role;

/**
 * 権限リスト抽出クラス
 *
 * @author itsukaha
 */
public class AuthoritiesExtractor
{


	/** 管理者権限を付与する権限名（どれかに該当すれば Role.ADMIN を付与） */
	private List<String> adminRoleList = new ArrayList<>();

	/** AgencyDao */
	@Resource private AgencyDao agencyDao;


	/**
	 * 管理者権限を付与する権限名を設定.
	 *
	 * @param adminRoleListStr 設定値（カンマ区切りで複数指定可能）
	 */
	public void setAdminRoleList( String adminRoleListStr )
	{
		String[] adminRoleArray = null;

		if ( adminRoleListStr != null )
		{
			adminRoleArray = adminRoleListStr.split( "," );
		}

		if ( adminRoleArray == null ) return;

		for ( String role : adminRoleArray )
		{
			role = role.trim();
			if ( role.equals( "" ) ) continue;

			adminRoleList.add( role );
		}
	}

	/** システム管理者権限を付与する企業 ID（いずれかの企業に所属する Role.ADMIN ユーザに Role.SYS_ADMIN も付与） */
	private List<String> sysRoleList = new ArrayList<>();

	/**
	 * システム管理者権限を付与する企業 ID を設定.
	 *
	 * @param sysRoleListStr 設定値（カンマ区切りで複数指定可能）
	 */
	public void setSysRoleList( String sysRoleListStr )
	{
		String[] sysRoleArray = null;

		if ( sysRoleListStr != null )
		{
			sysRoleArray = sysRoleListStr.split( "," );
		}

		if ( sysRoleArray == null ) return;

		for ( String role : sysRoleArray )
		{
			role = role.trim();
			if ( role.equals( "" ) ) continue;

			sysRoleList.add( role );
		}
	}

	/**
	 * 認証情報から権限リストを作成して返却する.
	 * （この処理が呼ばれるのは認証成功した場合だけなので、最低でも Role.USER 権限を付与する）
	 *
	 * @param authInfo 認証情報
	 * @param clientId
	 * @return 権限リスト
	 */
	public List<GrantedAuthority> extractAuthorities( Map<String, Object> authInfo )
	{
		Set<Object> authorities = new HashSet<Object>();

		String authName = AuthMapUtil.role( authInfo );

		if ( adminRoleList.contains( authName ) ) authName = Role.ADMIN.getRole();
		else authName = Role.USER.getRole();

		authorities.add( authName );

		if ( authName == Role.ADMIN.getRole() ) // 特定企業の管理者にはシステム管理者権限も付与
		{
			String companyId = AuthMapUtil.companyId( authInfo ) ;
			if ( sysRoleList.contains( companyId ) )
			{
				// システム管理者企業の場合はシステム管理者権限と代理店権限を付与する
				authorities.add( Role.SYS_ADMIN.getRole() );
				authorities.add( Role.AGENCY.getRole() );
			}
			// 代理店テーブルに企業IDが設定されている場合は代理店ロールを追加する
			else if (isAgency( companyId ))
			{
				authorities.add( Role.AGENCY.getRole() );
			}
		}

		if ( authorities.isEmpty() ) authorities.add( Role.USER.getRole() );

		// 作成した権限セットをカンマ区切りの文字列に変換し、その文字列から権限リストを作成する

		return AuthorityUtils.commaSeparatedStringToAuthorityList(
				StringUtils.collectionToCommaDelimitedString( authorities ) );
	}

	/**
	 * ログインユーザの企業IDが代理店企業であるかチェックする
	 * @param companyId
	 * @return
	 */
	private boolean isAgency(String companyId) {
		Agency example = new Agency();
		example.setAgencyCompanyId(companyId);
		List<Agency> list = agencyDao.findByExample(example);
		return list != null && !list.isEmpty();
	}
}