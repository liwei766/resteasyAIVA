/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：UseTimeService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.usetime;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.aivoiceanalytics.api.entity.UseTime;
import cloud.optim.aivoiceanalytics.api.entity.dao.UseTimeDao;


/**
 * UseTimeService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class UseTimeService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());


	/**
	 * HibernateDAO
	 */
	@Resource
	private UseTimeDao useTimeDao;

	/**
	 * UseTimeDao 取得
	 * @return UseTimeDao
	 */
	public UseTimeDao getUseTimeDao() {
		return useTimeDao;
	}

	/**
	 * MyBatis Mapper
	 */
	@Resource
	private UseTimeMapper useTimeMapper;

	/**
	 * UseTimeMapper 取得
	 * @return UseTimeMapper
	 */
	public UseTimeMapper getUseTimeMapper() {
		return useTimeMapper;
	}


	/**
	 * 一件検索
	 * @param id エンティティの識別ID
	 * @return エンティティ
	 */
	public UseTime getUseTime( Serializable id ) {
		return this.useTimeDao.get(id);
	}

	/**
	 * 企業毎の利用時間検索
	 * @param searchForm 検索フォーム
	 * @return 検索結果リスト
	 * @throws Exception エラー
	 */
	public List<SearchResult> searchByCompanyId(SearchForm searchForm) throws Exception {
		List<SearchResult> list = useTimeMapper.searchByCompanyId(searchForm);
		return list;
	}

	/**
	 * ユーザ毎の利用時間検索
	 * @param searchForm 検索フォーム
	 * @return 検索結果リスト
	 * @throws Exception エラー
	 */
	public List<SearchResult> searchByUserId(SearchForm searchForm) throws Exception {
		List<SearchResult> list = useTimeMapper.searchByUserId(searchForm);
		return list;
	}

	/**
	 * ユーザ毎の利用時間検索
	 * @param searchForm 検索フォーム
	 * @return 検索結果リスト
	 * @throws Exception エラー
	 */
	public Long get(String companyId, String userId) throws Exception {
		Long result = useTimeMapper.getUsersUseTimeSummary(companyId, userId);
		return result;
	}


	/**
	 * 登録
	 * @param entity エンティティ
	 * @return 登録したエンティティ
	 */
	public UseTime save( UseTime entity ) {
		this.useTimeDao.save(entity);
		return entity;
	}


	/**
	 * 更新
	 * @param entity エンティティ
	 * @return 更新したエンティティ
	 */
	public UseTime update( UseTime entity ) {
		this.useTimeDao.update(entity);
		return entity;
	}


	/**
	 * 削除
	 * @param id エンティティの識別ID
	 */
	public void delete( Serializable id ) {
		this.useTimeDao.delete( this.useTimeDao.get(id) );
	}

}