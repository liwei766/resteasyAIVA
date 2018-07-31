/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：RecaiusLicenseService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.recaiuslicense;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.aivoiceanalytics.api.entity.RecaiusLicense;
import cloud.optim.aivoiceanalytics.api.entity.dao.RecaiusLicenseDao;
import cloud.optim.aivoiceanalytics.core.common.utility.Cryptor;

/**
 * RecaiusLicenseService実装.<br/>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class,
    isolation = Isolation.READ_COMMITTED)
public class RecaiusLicenseService {

  //** Commons Logging instance. */
  //@SuppressWarnings("unused")
  //private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

  /** 暗号ユーティリティの共通鍵. */
  @Value("${cryptor.key}")
  private String cryptorKey;

  /**
   * HibernateDAO.
   */
  @Resource
  private RecaiusLicenseDao recaiusLicenseDao;

  /**
   * RecaiusLicenseDao 取得.
   *
   * @return RecaiusLicenseDao
   */
  public RecaiusLicenseDao getRecaiusLicenseDao() {
    return recaiusLicenseDao;
  }

  /**
   * MyBatis Mapper.
   */
  @Resource
  private RecaiusLicenseMapper recaiusLicenseMapper;

  /**
   * RecaiusLicenseMapper 取得.
   *
   * @return RecaiusLicenseMapper
   */
  public RecaiusLicenseMapper getRecaiusLicenseMapper() {
    return recaiusLicenseMapper;
  }

  /**
   * 一件検索.
   *
   * @param id エンティティの識別ID
   * @return エンティティ
   */
  public RecaiusLicense getRecaiusLicense(Serializable id) {
    RecaiusLicense result = this.recaiusLicenseDao.get(id);
    result = this.decode(result); // UT 時は無効でも OK
    return result;
  }

  /**
   * 複数検索.
   * <p>一覧表示向けなので、パスワードは復号化しない。</p>
   *
   * @param searchForm 検索フォーム
   * @return 検索結果リスト
   * @throws Exception エラー
   */
  public List<SearchResult> search(SearchForm searchForm) throws Exception {
    List<SearchResult> result = recaiusLicenseMapper.search(searchForm);

    // サービスIDのみ復号化する
    if(result == null || result.isEmpty()) return result;

    for (SearchResult each : result) {
      each.getRecaiusLicense().setServiceId(
        Cryptor.decrypt(this.cryptorKey, each.getRecaiusLicense().getServiceId()));
    }

    return result;
  }

  /**
   * 登録.
   *
   * @param entity エンティティ
   * @return 登録したエンティティ
   */
  public RecaiusLicense save(RecaiusLicense entity) {
    entity = this.encode(entity); // UT 時は無効でも OK
    this.recaiusLicenseDao.save(entity);
    return entity;
  }

  /**
   * 更新.
   *
   * @param entity エンティティ
   * @return 更新したエンティティ
   */
  public RecaiusLicense update(RecaiusLicense entity) {
    entity = this.encode(entity); // UT 時は無効でも OK
    this.recaiusLicenseDao.update(entity);
    return entity;
  }

  /**
   * 削除.
   *
   * @param id エンティティの識別ID
   */
  public void delete(Serializable id) {
    this.recaiusLicenseDao.delete(this.recaiusLicenseDao.get(id));
  }

  /**
   * 存在チェック.
   * @param id エンティティの識別ID
   * @return 存在するなら true
   */
  public boolean contains(Serializable id) {
    RecaiusLicense recaiusLicense = this.recaiusLicenseDao.get(id);
    if (recaiusLicense == null) {
      return false;
    }
    return true;
  }

  /**
   * 重複チェック.
   * <p>同じサービス利用IDは登録できない.</p>
   * @param serviceId サービス利用ID
   * @return 重複していたら true
   */
  public boolean isDuplicateServiceId(final String serviceId) {
    // サービスIDも復号化するようになったので全件取得して復号化されたサービスIDと比較するように修正
    // ※encryptの度に内容が変わるのでSQLの条件にサービスIDを設定できない
    List<RecaiusLicense> recaiusLicenseList = recaiusLicenseDao.findByExample(new RecaiusLicense());
    if (recaiusLicenseList == null || recaiusLicenseList.isEmpty()) return false;
    recaiusLicenseList = decodePassword(recaiusLicenseList);
    for(RecaiusLicense each : recaiusLicenseList) {
      if(each.getServiceId().equals(serviceId)) return true;
    }
    return false;
  }

  /**
   * 代理店企業IDで検索.
   * @param agencyCompanyId 代理店企業ID
   * @return RecaiusLicenseインスタンス
   */
  public List<RecaiusLicense> selectByAgencyCompanyId(final String agencyCompanyId) {
    RecaiusLicense condition = new RecaiusLicense();
    condition.setAgencyCompanyId(agencyCompanyId);
    List<RecaiusLicense> result = recaiusLicenseDao.findByExample(condition);
    result = this.decodePassword(result); // UT 時は無効でも OK
    return result;
  }

  /**
   * 「サービス利用ID」および「パスワード」フィールドを暗号化する.
   * @param recaiusLicense 対象インスタンス
   * @return 処理されたインスタンス
   */
  private RecaiusLicense encode(final RecaiusLicense recaiusLicense) {
    RecaiusLicense result = null;
    try {
      result = (RecaiusLicense)BeanUtils.cloneBean(recaiusLicense);
    } catch (IllegalAccessException | InstantiationException | InvocationTargetException
        | NoSuchMethodException e) {
      // NOP
    }
    String plainServiceId = result.getServiceId();
    String encServiceId = Cryptor.encrypt(this.cryptorKey, plainServiceId);
    result.setServiceId(encServiceId);
    String plainPassword = result.getPassword();
    String encPassword = Cryptor.encrypt(this.cryptorKey, plainPassword);
    result.setPassword(encPassword);
    return result;
  }

  /**
   * 「サービス利用ID」および「パスワード」フィールドを復号化する.
   * @param recaiusLicense 対象インスタンス
   * @return 処理されたインスタンス
   */
  private RecaiusLicense decode(final RecaiusLicense recaiusLicense) {
    RecaiusLicense result = null;
    try {
      result = (RecaiusLicense)BeanUtils.cloneBean(recaiusLicense);
    } catch (IllegalAccessException | InstantiationException | InvocationTargetException
        | NoSuchMethodException e) {
      // NOP
    }
    String encServiceId = result.getServiceId();
    String plainServiceId = Cryptor.decrypt(this.cryptorKey, encServiceId);
    result.setServiceId(plainServiceId);
    String encPassword = result.getPassword();
    String plainPassword = Cryptor.decrypt(this.cryptorKey, encPassword);
    result.setPassword(plainPassword);
    return result;
  }

  /**
   * 「サービス利用ID」および「パスワード」フィールドを復号化する.
   * @param recaiusLicenseList 対象インスタンスが格納されたコレクション
   * @return 処理されたコレクション
   */
  private List<RecaiusLicense> decodePassword(final List<RecaiusLicense> recaiusLicenseList) {
    ArrayList<RecaiusLicense> result = new ArrayList<RecaiusLicense>(recaiusLicenseList.size());
    for (RecaiusLicense orig : recaiusLicenseList) {
      RecaiusLicense dest = new RecaiusLicense();
      try {
        BeanUtils.copyProperties(dest, orig);
      } catch (IllegalAccessException | InvocationTargetException e) {
        // NOP
      }
      dest = decode(dest);
      result.add(dest);
    }
    return result;
  }
}
