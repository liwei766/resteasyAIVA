/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：CompanyManagementService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.companymanagement;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.aivoiceanalytics.api.entity.CompanyManagement;
import cloud.optim.aivoiceanalytics.api.entity.dao.CompanyManagementDao;

/**
 * CompanyManagementService実装.<br/>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class,
    isolation = Isolation.READ_COMMITTED)
public class CompanyManagementService {

  /// ** Commons Logging instance. */
  // @SuppressWarnings("unused")
  // private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

  /**
   * HibernateDAO.
   */
  @Resource
  private CompanyManagementDao companyManagementDao;

  /**
   * companyManagement Setting.
   */
  private CompanyManagement compMgmtSetting;

  /**
   * CompanyManagementDao 取得.
   *
   * @return CompanyManagementDao
   */
  public CompanyManagementDao getCompanyManagementDao() {
    return companyManagementDao;
  }

  /**
   * MyBatis Mapper.
   */
  @Resource
  private CompanyManagementMapper companyManagementMapper;

  /**
   * CompanyManagementMapper 取得.
   *
   * @return CompanyManagementMapper
   */
  public CompanyManagementMapper getCompanyManagementMapper() {
    return companyManagementMapper;
  }

  /**
   * 一件検索.
   *
   * @param id エンティティの識別ID
   * @return エンティティ
   */
  public CompanyManagement getCompanyManagement(Serializable id) {
    return this.companyManagementDao.get(id);
  }

  /**
   * 複数検索.
   *
   * @param searchForm 検索フォーム
   * @return 検索結果リスト
   * @throws Exception エラー
   */
  public List<SearchResult> search(SearchForm searchForm) throws Exception {
    List<SearchResult> list = companyManagementMapper.search(searchForm);
    return list;
  }

  /**
   * 登録.
   *
   * @param entity エンティティ
   * @return 登録したエンティティ
   */
  public CompanyManagement save(CompanyManagement entity) {
    this.companyManagementDao.save(entity);
    return entity;
  }

  /**
   * 更新.
   *
   * @param entity エンティティ
   * @return 更新したエンティティ
   */
  public CompanyManagement update(CompanyManagement entity) {
    this.companyManagementDao.update(entity);
    return entity;
  }

  /**
   * 削除.
   *
   * @param id エンティティの識別ID
   */
  public void delete(Serializable id) {
    this.companyManagementDao.delete(this.companyManagementDao.get(id));
  }

  /**
   * 存在チェック.
   * @param id エンティティの識別ID
   */
  public boolean contains(Serializable id) {
    CompanyManagement companyManagement = this.companyManagementDao.get(id);
    if (companyManagement == null) {
      return false;
    }
    return true;
  }

  /**
   * 企業ID重複チェック.
   * <p>同じ企業IDは登録できない.</p>
   * @param companyId 企業ID
   * @return 重複していたら ture
   */
  public boolean isDuplicateCompanyId(final String companyId) {
    CompanyManagement condition = new CompanyManagement();
    condition.setCompanyId(companyId);
    List<CompanyManagement> companyManagementList = companyManagementDao.findByExample(condition);
    if (companyManagementList != null && !companyManagementList.isEmpty()) {
      return true;
    }
    return false;
  }

  /**
   * リカイアスライセンス利用企業有無チェック.
   * @param recaiusLicenseid リカイアスライセンスID
   * @return 利用中の企業がある場合は ture
   */
  public boolean isInUseRecaiusLicense(final Long recaiusLicenseid) {
    CompanyManagement condition = new CompanyManagement();
    condition.setRecaiusLicenseId(recaiusLicenseid);
    List<CompanyManagement> companyManagementList = companyManagementDao.findByExample(condition);
    if (companyManagementList != null && !companyManagementList.isEmpty()) {
      return true;
    }
    return false;
  }



  /**
   * 企業IDから企業管理を取得する.
   * @param companyId 企業ID
   * @return 企業管理インスタンス（存在しない場合はnull）
   */
  public CompanyManagement getCompanyManagementByCompanyId(final String companyId) {
    CompanyManagement condition = new CompanyManagement();
    condition.setCompanyId(companyId);
    List<CompanyManagement> companyManagementList = companyManagementDao.findByExample(condition);
    if (companyManagementList != null && !companyManagementList.isEmpty()) {
      return companyManagementList.get(0);
    }
    return null;
  }

	/**
	 * 企業IDから企業設定を取得する.
	 *
	 * @param companyId
	 *            企業ID
	 * @return 企業管理インスタンス（存在しない場合はnull）
	 */
	public CompanyManagement getCompanySettingsByCompanyId(final String companyId) {
		CompanyManagement entity = new CompanyManagement();
		entity.setCompanyId(companyId);
		List<CompanyManagement> companyManagementList = companyManagementDao.findByExample(entity);
		if (companyManagementList != null && !companyManagementList.isEmpty()) {
			compMgmtSetting = companyManagementList.get(0) ;
			entity.setCompanyId(null);
			entity.setUpdateDate(compMgmtSetting.getUpdateDate());
			entity.setEnergyThreshold(compMgmtSetting.getEnergyThreshold());
			return entity;
		}
		return null;
	}

	/**
	 * 企業設定更新.
	 *
	 * @param entity
	 *            エンティティ
	 * @return 更新したエンティティ
	 */
	public CompanyManagement updateCompanySettings(CompanyManagement entity) {

		CompanyManagement compMgmtDTO = new CompanyManagement();

		mergeObject(compMgmtSetting, compMgmtDTO );
		mergeObject(entity, compMgmtDTO);
		companyManagementDao.update(compMgmtDTO);

		mergeObject(compMgmtDTO,compMgmtSetting );

		entity.setUpdateDate(compMgmtDTO.getUpdateDate());
		entity.setEnergyThreshold(compMgmtDTO.getEnergyThreshold());
		entity.setCompanyId(null);
		entity.setUpdateUserId(null);
		entity.setUpdateUserName(null);

		return entity;
	}

	/**
	 * origin属性をdestinationへマージする
	 */
	private <T> void mergeObject(T origin, T destination) {
		if (origin == null || destination == null)
			return;
		if (!origin.getClass().equals(destination.getClass()))
			return;

		Field[] fields = origin.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				fields[i].setAccessible(true);
				Object value = fields[i].get(origin);
				if (null != value) {
					fields[i].set(destination, value);
				}
				fields[i].setAccessible(false);
			} catch (Exception e) {
			}
		}
	}

}
