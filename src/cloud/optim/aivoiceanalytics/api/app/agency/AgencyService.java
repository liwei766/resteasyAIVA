/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：AgencyService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.agency;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.aivoiceanalytics.api.entity.Agency;
import cloud.optim.aivoiceanalytics.api.entity.dao.AgencyDao;

/**
 * AgencyService実装.<br/>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class,
    isolation = Isolation.READ_COMMITTED)
public class AgencyService {

  /// ** Commons Logging instance. */
  // @SuppressWarnings("unused")
  // private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());


  /**
   * HibernateDAO.
   */
  @Resource
  private AgencyDao agencyDao;

  /**
   * AgencyDao 取得.
   *
   * @return AgencyDao
   */
  public AgencyDao getAgencyDao() {
    return agencyDao;
  }

  /**
   * MyBatis Mapper.
   */
  @Resource
  private AgencyMapper agencyMapper;

  /**
   * AgencyMapper 取得.
   *
   * @return AgencyMapper
   */
  public AgencyMapper getAgencyMapper() {
    return agencyMapper;
  }


  /**
   * 一件検索.
   *
   * @param id エンティティの識別ID
   * @return エンティティ
   */
  public Agency getAgency(Serializable id) {
    return this.agencyDao.get(id);
  }


  /**
   * 一件検索.（検索結果形式）
   *
   * @param id エンティティの識別ID
   * @return 検索結果
   * @throws Exception エラー
   */
  /*
  public SearchResult getSearchResult(Long id) throws Exception {

    SearchForm searchForm = new SearchForm();
    AgencySearchForm entityForm = new AgencySearchForm();
    entityForm.setAgencyId(String.valueOf(id));
    searchForm.setAgency(entityForm);

    SearchResult ret = null;

    List<SearchResult> list = agencyMapper.search(searchForm);

    if (list.size() > 1) {
      throw new DaoException("More than one records are found. : " + list.size());
    }
    if (list.size() == 1) {
      ret = list.get(0);
    }

    return ret;
  }
  */

  /**
   * 複数検索.
   *
   * @param searchForm 検索フォーム
   * @return 検索結果リスト
   * @throws Exception エラー
   */
  public List<SearchResult> search(SearchForm searchForm) throws Exception {
    List<SearchResult> list = agencyMapper.search(searchForm);
    return list;
  }


  /**
   * 登録.
   *
   * @param entity エンティティ
   * @return 登録したエンティティ
   */
  public Agency save(Agency entity) {
    this.agencyDao.save(entity);
    return entity;
  }


  /**
   * 更新.
   *
   * @param entity エンティティ
   * @return 更新したエンティティ
   */
  public Agency update(Agency entity) {
    this.agencyDao.update(entity);
    return entity;
  }


  /**
   * 削除.
   *
   * @param id エンティティの識別ID
   */
  public void delete(Serializable id) {
    this.agencyDao.delete(this.agencyDao.get(id));
  }

  /**
   * 代理店企業IDで検索.
   *
   * @param agencyCompanyId 代理店企業ID
   * @return Agencyリスト
   */
  public List<Agency> selectByAgencyCompanyId(final String agencyCompanyId) {
    Agency condition = new Agency();
    condition.setAgencyCompanyId(agencyCompanyId);
    return agencyDao.findByExample(condition);
  }

}
