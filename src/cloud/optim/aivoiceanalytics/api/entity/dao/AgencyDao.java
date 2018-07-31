/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：AgencyDao.java
 */
package cloud.optim.aivoiceanalytics.api.entity.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.CacheMode ;
import org.hibernate.Criteria ;
import org.hibernate.Filter ;
import org.hibernate.FlushMode ;
import org.hibernate.LockOptions ;
import org.hibernate.Session ;
import org.hibernate.SessionFactory ;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example ;
import org.hibernate.query.NativeQuery ;
import org.hibernate.query.Query ;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.aivoiceanalytics.api.entity.Agency;
import cloud.optim.aivoiceanalytics.core.common.DaoException ;

/**
 * AgencyDao クラス
 *
 * @see cloud.optim.aivoiceanalytics.api.entity.Agency
 * @author Hibernate Tools
 */
@Repository
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class AgencyDao {

	///** log  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());





	/** sessionFactory  */
	@Resource private SessionFactory sessionFactory;

	/**
	 * sessionFactory 設定
	 * @param sessionFactory セッションファクトリ
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * sessionFactory 取得
	 * @return sessionFactory
	 */
	public SessionFactory getSessionFactory(){
		return sessionFactory;
	}





	/**
	 * current session 取得
	 * @return sessionFactory
	 */
	public Session getSession(){
		return getSessionFactory().getCurrentSession();
	}





	//-------------------------------------------------------------------------
	// DAO 共通処理
	//-------------------------------------------------------------------------

	/**
	 * カレントセッションの FlushMode 設定
	 * @param flushMode flushMode への設定内容
	 */
	public void setFlushMode(FlushMode flushMode) {
		getSession().setHibernateFlushMode(flushMode);
	}

	/**
	 * カレントセッションの FlushMode 取得
	 * @return flushMode
	 */
	public FlushMode getFlushMode() {
		return getSession().getHibernateFlushMode();
	}

	//-------------------------------------------------------------------------

	/** 生成するクエリの cacheable 値 */
	private Boolean queryCacheable;

	/**
	 * queryCacheable 設定
	 * @param queryCacheable queryCacheable への設定内容
	 */
	public void setQueryCacheable(boolean queryCacheable) {
		this.queryCacheable = queryCacheable;
	}

	/**
	 * queryCacheable 取得
	 * @return cacheQueries
	 */
	public boolean isQueryCacheable() {
		return queryCacheable;
	}

	//-------------------------------------------------------------------------

	/** 生成するクエリの CacheMode */
	private CacheMode queryCacheMode;

	/**
	 * CacheMode 設定
	 * @param queryCacheMode queryCacheMode への設定内容
	 */
	public void setQueryCacheMode(CacheMode queryCacheMode) {
		this.queryCacheMode = queryCacheMode;
	}

	/**
	 * CacheMode 取得
	 * @return cacheMode
	 */
	public CacheMode getQueryCacheMode() {
		return queryCacheMode;
	}

	//-------------------------------------------------------------------------

	/** 生成するクエリの cacheRegion */
	private String queryCacheRegion;

	/**
	 * queryCacheRegion 設定
	 * @param queryCacheRegion への設定内容
	 */
	public void setQueryCacheRegion(String queryCacheRegion) {
		this.queryCacheRegion = queryCacheRegion;
	}

	/**
	 * queryCacheRegion 取得
	 * @return queryCacheRegion
	 */
	public String getQueryCacheRegion() {
		return queryCacheRegion;
	}

	//-------------------------------------------------------------------------

	/** 生成するクエリの fetchSize */
	private Integer fetchSize;

	/**
	 * fetchSize 設定
	 * @param fetchSize への設定内容
	 */
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	/**
	 * fetchSize 取得
	 * @return fetchSize
	 */
	public int getFetchSize() {
		return fetchSize;
	}

	//-------------------------------------------------------------------------

	/** 生成するクエリの maxResults */
	private Integer maxResults;

	/**
	 * maxResults 設定
	 * @param maxResults MAX結果数
	 */
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	/**
	 * maxResults 取得
	 * @return maxResults
	 */
	public int getMaxResults() {
		return maxResults;
	}





	//-------------------------------------------------------------------------
	// Convenience methods for loading individual objects
	//-------------------------------------------------------------------------

	/**
	 * @param id エンティティの識別ID
	 * @return Agency
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public Agency get(Serializable id) throws DataAccessException {
		try {
			return (Agency)getSession().get(Agency.class, id);
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex) ;
		}
	}

	/**
	 * @param id エンティティの識別ID
	 * @param lockOptions ロックオプション
	 * @return Agency
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public Agency get(Serializable id, LockOptions lockOptions) throws DataAccessException {
		try {
			return (Agency)getSession().get(Agency.class, id, lockOptions);
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param id エンティティの識別ID
	 * @return Agency
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public Agency load(Serializable id) throws DataAccessException {
		try {
			return (Agency)getSession().load(Agency.class, id);
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param id エンティティの識別ID
	 * @param lockOptions ロックオプション
	 * @return Agency
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public Agency load(Serializable id, LockOptions lockOptions) throws DataAccessException {
		try {
			return (Agency)getSession().load(Agency.class, id, lockOptions);
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entity エンティティ
	 * @param id エンティティの識別ID
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void load(Agency entity, Serializable id) throws DataAccessException {
		try {
			getSession().load(entity, id);
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entity エンティティ
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void refresh(Agency entity) throws DataAccessException {
		try {
			getSession().refresh(entity);
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entity エンティティ
	 * @param lockOptions ロックオプション
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void refresh(Agency entity, LockOptions lockOptions) throws DataAccessException {
		try {
			getSession().refresh(entity, lockOptions);
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entity エンティティ
	 * @return true/false
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public boolean contains(Agency entity) throws DataAccessException {
		try {
			return getSession().contains(entity);
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entity エンティティ
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void evict(Agency entity) throws DataAccessException {
		try {
			getSession().evict(entity);
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param filterName フィルタ名
	 * @return Hibernate フィルタ
	 * @throws IllegalStateException Hibernate の不正状態Exception
	 */
	public Filter enableFilter(String filterName) throws IllegalStateException {
		try {
			return getSession().enableFilter(filterName);
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}





	//-------------------------------------------------------------------------
	// Convenience methods for storing individual objects
	//-------------------------------------------------------------------------

	/**
	 * @param entity エンティティ
	 * @param lockOptions ロックオプション
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void lock(Agency entity, LockOptions lockOptions) throws DataAccessException {
		try {
			getSession().buildLockRequest(lockOptions).lock(entity);
			flush();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entity エンティティ
	 * @return エンティティの識別ID
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public Serializable save(Agency entity) throws DataAccessException {
		try {
			Serializable returnObj = getSession().save(Agency.class.getName(), entity);
			flush();
			return returnObj;
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entity エンティティ
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void update(Agency entity) throws DataAccessException {
		try {
			getSession().update(Agency.class.getName(), entity);
			flush();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entity エンティティ
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void saveOrUpdate(Agency entity) throws DataAccessException {
		try {
			getSession().saveOrUpdate(Agency.class.getName(), entity);
			flush();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entities エンティティ コレクション
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void saveOrUpdate(Collection<Agency> entities) throws DataAccessException {
		try {
			for(Agency entity : entities) {
				getSession().saveOrUpdate(Agency.class.getName(), entity);
			}
			flush();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entity エンティティ
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void persist(Agency entity) throws DataAccessException {
		try {
			getSession().persist(entity);
			flush();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entity エンティティ
	 * @return Agency
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public Agency merge(Agency entity) throws DataAccessException {
		try {
			Agency returnObj = (Agency)getSession().merge(entity);
			flush();
			return returnObj;
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param id エンティティの識別ID
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void delete(Serializable id) throws DataAccessException {
		try {
			getSession().delete(Agency.class.getName(), get(id));
			flush();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entity エンティティ
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void delete(Agency entity) throws DataAccessException {
		try {
			getSession().delete(Agency.class.getName(), entity);
			flush();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entities エンティティ コレクション
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void deleteAll(Collection<Agency> entities) throws DataAccessException {
		try {
			for(Agency entity : entities) {
				getSession().delete(Agency.class.getName(), entity);
			}
			flush();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void flush() throws DataAccessException {
		try {
			getSession().flush();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void clear() throws DataAccessException{
		try {
			getSession().clear();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}





	//-------------------------------------------------------------------------
	// Convenience methods for storing individual objects ( Do not flush )
	//-------------------------------------------------------------------------

	/**
	 * @param entity エンティティ
	 * @return エンティティの識別ID
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public Serializable saveNoFlush(Agency entity) throws DataAccessException {
		try {
			Serializable returnObj = getSession().save(Agency.class.getName(), entity);
			return returnObj;
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entity エンティティ
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void updateNoFlush(Agency entity) throws DataAccessException {
		try {
			getSession().update(Agency.class.getName(), entity);
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entity エンティティ
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void saveOrUpdateNoFlush(Agency entity) throws DataAccessException {
		try {
			getSession().saveOrUpdate(Agency.class.getName(), entity);
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entities エンティティ コレクション
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void saveOrUpdateNoFlush(Collection<Agency> entities) throws DataAccessException {
		try {
			for(Agency entity : entities) {
				getSession().saveOrUpdate(Agency.class.getName(), entity);
			}
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param id エンティティの識別ID
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void deleteNoFlush(Serializable id) throws DataAccessException {
		try {
			getSession().delete(Agency.class.getName(), get(id));
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entity エンティティ
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void deleteNoFlush(Agency entity) throws DataAccessException {
		try {
			getSession().delete(Agency.class.getName(), entity);
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param entities エンティティ コレクション
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public void deleteAllNoFlush(Collection<Agency> entities) throws DataAccessException {
		try {
			for(Agency entity : entities) {
				getSession().delete(Agency.class.getName(), entity);
			}
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}





	//-------------------------------------------------------------------------
	// Convenience finder methods for HQL strings
	//-------------------------------------------------------------------------

	/**
	 * @param queryString クエリ文字列
	 * @param paramMap パラメータマップ
	 * @return Agency リスト
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public List<Agency> findByMap(String queryString, Map<String,Object> paramMap) throws DataAccessException {
		try {
			return applyNamedParameter(createQuery(queryString, Agency.class), paramMap).getResultList();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}





	//-------------------------------------------------------------------------
	// Convenience finder methods for named queries
	//-------------------------------------------------------------------------

	/**
	 * @param queryName クエリ名
	 * @param paramMap パラメータマップ
	 * @return Agency リスト
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public List<Agency> findByNamedQueryAndMap(String queryName, Map<String,Object> paramMap)
			throws DataAccessException {
		try {
			return applyNamedParameter(createNamedQuery(queryName, Agency.class), paramMap).getResultList();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}





	//-------------------------------------------------------------------------
	// Convenience finder methods for detached criteria
	//-------------------------------------------------------------------------

	/**
	 * @param exampleEntity エンティティ
	 * @return Agency リスト
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	@SuppressWarnings( "unchecked" )
	public List<Agency> findByExample(Object exampleEntity) throws DataAccessException {
		try {
			return DetachedCriteria.forClass(Agency.class).getExecutableCriteria(getSession())
				.add(Example.create(exampleEntity))
				.list();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * @param exampleEntity エンティティ
	 * @param firstResult 検索結果の最初のインデックス番号
	 * @param maxResults 検索結果の最大数
	 * @return Agency リスト
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	@SuppressWarnings( "unchecked" )
	public List<Agency> findByExample(Object exampleEntity, int firstResult, int maxResults) throws DataAccessException {
		try {
			return DetachedCriteria.forClass(Agency.class).getExecutableCriteria(getSession())
				.add(Example.create(exampleEntity))
				.setFirstResult(firstResult)
				.setMaxResults(maxResults)
				.list();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}





	//-------------------------------------------------------------------------
	// Convenience query methods for bulk updates/deletes
	//-------------------------------------------------------------------------

	/**
	 * 更新系のクエリ実行
	 *
	 * @param queryString クエリ文字列
	 * @return 更新/削除されたレコード数
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public int executeUpdate(String queryString) throws DataAccessException {
		try {
			return createQuery(queryString, int.class).executeUpdate();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * 更新系のクエリ実行
	 *
	 * @param queryString クエリ文字列
	 * @param value パラメータ値
	 * @return 更新/削除されたレコード数
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public int executeUpdate(String queryString, Object value) throws DataAccessException {
		try {
			return createQuery(queryString, int.class).setParameter(0, value).executeUpdate();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * 更新系のクエリ実行
	 *
	 * @param queryString クエリ文字列
	 * @param values パラメータ値配列
	 * @return 更新/削除されたレコード数
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public int executeUpdate(String queryString, Object[] values) throws DataAccessException {
		try {
			return applyParameter(createQuery(queryString, int.class), values).executeUpdate();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * 更新系のクエリ実行
	 *
	 * @param queryString クエリ文字列
	 * @param name パラメータ名
	 * @param value パラメータ値
	 * @return 更新/削除されたレコード数
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public int executeUpdate(String queryString, String name, Object value) throws DataAccessException {
		try {
			return createQuery(queryString, int.class).setParameter(name, value).executeUpdate();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * 更新系のクエリ実行
	 *
	 * @param queryString クエリ文字列
	 * @param names パラメータ名配列
	 * @param values パラメータ値配列
	 * @return 更新/削除されたレコード数
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public int executeUpdate(String queryString, String[] names, Object[] values) throws DataAccessException {
		try {
			return applyNamedParameter(createQuery(queryString, int.class), names, values).executeUpdate();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * 更新系のクエリ実行
	 *
	 * @param queryString クエリ文字列
	 * @param properties パラメータ名／パラメータ値のマップ
	 * @return 更新/削除されたレコード数
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public int executeUpdate(String queryString, Map<String, Object> properties) throws DataAccessException {
		try {
			return applyNamedParameter(createQuery(queryString, int.class), properties).executeUpdate();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}





	/**
	 * 更新系の SQL クエリ実行
	 *
	 * @param queryString SQL クエリ文字列
	 * @return 更新/削除されたレコード数
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public int executeSqlUpdate(String queryString) throws DataAccessException {
		try {
			return createNativeQuery(queryString, int.class).executeUpdate();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * 更新系の SQL クエリ実行
	 *
	 * @param queryString SQL クエリ文字列
	 * @param value パラメータ値
	 * @return 更新/削除されたレコード数
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public int executeSqlUpdate(String queryString, Object value) throws DataAccessException {
		try {
			return createNativeQuery(queryString, int.class).setParameter(0, value).executeUpdate();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * 更新系の SQL クエリ実行
	 *
	 * @param queryString SQL クエリ文字列
	 * @param values パラメータ値配列
	 * @return 更新/削除されたレコード数
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public int executeSqlUpdate(String queryString, Object[] values) throws DataAccessException {
		try {
			return applyParameter(createNativeQuery(queryString, int.class), values).executeUpdate();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * 更新系の SQL クエリ実行
	 *
	 * @param queryString SQL クエリ文字列
	 * @param name パラメータ名
	 * @param value パラメータ値
	 * @return 更新/削除されたレコード数
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public int executeSqlUpdate(String queryString, String name, Object value) throws DataAccessException {
		try {
			return createNativeQuery(queryString, int.class).setParameter(name, value).executeUpdate();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * 更新系の SQL クエリ実行
	 *
	 * @param queryString SQL クエリ文字列
	 * @param names パラメータ名配列
	 * @param values パラメータ値配列
	 * @return 更新/削除されたレコード数
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public int executeSqlUpdate(String queryString, String[] names, Object[] values) throws DataAccessException {
		try {
			return applyNamedParameter(createNativeQuery(queryString, int.class), names, values).executeUpdate();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}

	/**
	 * 更新系の SQL クエリ実行
	 *
	 * @param queryString SQL クエリ文字列
	 * @param properties パラメータ名／パラメータ値のマップ
	 * @return 更新/削除されたレコード数
	 * @throws DataAccessException Hibernate のデータアクセスException
	 */
	public int executeSqlUpdate(String queryString, Map<String, Object> properties) throws DataAccessException {
		try {
			return applyNamedParameter(createNativeQuery(queryString, int.class), properties).executeUpdate();
		} catch (Exception ex) {
			throw DaoException.handleDBException(ex);
		}
	}





	//-------------------------------------------------------------------------
	// クエリに関する共通処理
	//-------------------------------------------------------------------------

	/**
	 * デフォルト設定を適用した Criteria を作成
	 *
	 * @return 作成した Criteria
	 */
	protected Criteria createCriteria() {
		Criteria criteria = DetachedCriteria.forClass(Agency.class).getExecutableCriteria(getSession());
		return applyConfig(criteria);
	}

	/**
	 * Criteria にデフォルトの設定を適用する
	 *
	 * @param criteria 適用対象 Criteria
	 * @return 設定を適用した Criteria
	 */
	protected Criteria applyConfig(Criteria criteria) {
		if(queryCacheable != null) criteria.setCacheable(queryCacheable);
		if(queryCacheMode != null) criteria.setCacheMode(queryCacheMode);
		if(queryCacheRegion != null) criteria.setCacheRegion(queryCacheRegion);
		if(fetchSize != null) criteria.setFetchSize(fetchSize);
		if(maxResults != null) criteria.setMaxResults(maxResults);
		return criteria;
	}

	//-------------------------------------------------------------------------

	/**
	 * デフォルト設定を適用したクエリを作成
	 *
	 * @param queryString クエリ文字列
	 * @param cls クエリのターゲットクラス
	 * @return 作成したクエリ
	 */
	protected <T> Query<T> createQuery(String queryString, Class<T> cls) {
		Query<T> query = getSession().createQuery(queryString, cls);
		return applyConfig(query);
	}

	/**
	 * デフォルト設定を適用したクエリを作成
	 *
	 * @param queryName 名前付きクエリ名
	 * @param cls クエリのターゲットクラス
	 * @return 作成したクエリ
	 */
	protected <T> Query<T> createNamedQuery(String queryName, Class<T> cls) {
		@SuppressWarnings( "unchecked" )
		Query<T> query = getSession().getNamedQuery(queryName);
		return applyConfig(query);
	}

	/**
	 * クエリにデフォルトの設定を適用する
	 *
	 * @param query 適用対象クエリ
	 * @return 設定を適用したクエリ
	 */
	protected <T> Query<T> applyConfig(Query<T> query) {
		if(queryCacheable != null) query.setCacheable(queryCacheable);
		if(queryCacheMode != null) query.setCacheMode(queryCacheMode);
		if(queryCacheRegion != null) query.setCacheRegion(queryCacheRegion);
		if(fetchSize != null) query.setFetchSize(fetchSize);
		if(maxResults != null) query.setMaxResults(maxResults);
		return query;
	}

	//-------------------------------------------------------------------------

	/**
	 * デフォルト設定を適用した SQL クエリを作成
	 *
	 * @param queryName 名前付きクエリ名
	 * @param cls クエリのターゲットクラス
	 * @return 作成した SQL クエリ
	 */
	protected <T> NativeQuery<T> createNativeQuery(String queryName, Class<T> cls) {
		NativeQuery<T> query = getSession().createNativeQuery(queryName, cls);
		return applyConfigToNativeQuery(query);
	}

	/**
	 * SQL クエリにデフォルトの設定を適用する
	 *
	 * @param <T>
	 * @param query 適用対象 SQL クエリ
	 * @return 設定を適用した SQL クエリ
	 */
	protected <T> NativeQuery<T> applyConfigToNativeQuery( NativeQuery<T> query ) {
		applyConfig(query);
		return query;
	}

	//-------------------------------------------------------------------------

	/**
	 * クエリパラメータを適用する
	 *
	 * @param query クエリ
	 * @param value パラメータ
	 * @return パラメータ適用済みクエリ
	 */
	protected <T> Query<T> applyParameter(Query<T> query, Object value) {
		return applyParameter(query, new Object[] { value });
	}

	/**
	 * クエリパラメータを適用する
	 *
	 * @param query クエリ
	 * @param values パラメータの配列
	 * @return パラメータ適用済みクエリ
	 */
	protected <T> Query<T> applyParameter(Query<T> query, Object[] values) {
		for (int i = 0 ; i < values.length ; i++) {
			query.setParameter(i, values[i]);
		}
		return query;
	}

	/**
	 * クエリに名前付きパラメータを適用する
	 *
	 * @param query クエリ
	 * @param name パラメータ名
	 * @param value パラメータ
	 * @return パラメータ適用済みクエリ
	 */
	protected <T> Query<T> applyNamedParameter(Query<T> query, String name, Object value) {
		return applyNamedParameter(query, new String[] { name }, new Object[] { value });
	}

	/**
	 * クエリに名前付きパラメータを適用する
	 *
	 * @param query クエリ
	 * @param names パラメータ名の配列
	 * @param values パラメータの配列
	 * @return パラメータ適用済みクエリ
	 */
	protected <T> Query<T> applyNamedParameter(Query<T> query, String[] names, Object[] values) {
		for (int i = 0 ; i < names.length ; i++) {
			Object value = (i < values.length) ? values[i] : null;
			query.setParameter(names[i], value);
		}
		return query;
	}

	/**
	 * クエリに名前付きパラメータを適用する
	 *
	 * @param query クエリ
	 * @param properties パラメータ名／パラメータ値のマップ
	 * @return パラメータ適用済みクエリ
	 */
	protected <T> Query<T> applyNamedParameter(Query<T> query, Map<String, Object> properties) {
		for (String name : properties.keySet()) {
			query.setParameter(name, properties.get(name));
		}
		return query;
	}
}
