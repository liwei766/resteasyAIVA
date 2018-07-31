/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechLogService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.speechlog;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation ;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.aivoiceanalytics.api.entity.SpeechLog;
import cloud.optim.aivoiceanalytics.api.entity.dao.SpeechLogDao;


/**
 * SpeechLogService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class SpeechLogService {

	///** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());


	/**
	 * HibernateDAO
	 */
	@Resource
	private SpeechLogDao speechLogDao;

	/**
	 * SpeechLogDao 取得
	 * @return SpeechLogDao
	 */
	public SpeechLogDao getSpeechLogDao() {
		return speechLogDao;
	}

	/**
	 * MyBatis Mapper
	 */
	@Resource
	private SpeechLogMapper speechLogMapper;

	/**
	 * SpeechLogMapper 取得
	 * @return SpeechLogMapper
	 */
	public SpeechLogMapper getSpeechLogMapper() {
		return speechLogMapper;
	}


	/**
	 * 一件検索
	 * @param id エンティティの識別ID
	 * @param companyId 企業ID
	 * @param userId ユーザID
	 * @return エンティティ
	 */
	public SpeechLog getSpeechLog( Serializable id, String companyId, String userId ) {
//		SpeechLog result = this.speechLogDao.get(id);
//		if (result == null) return null;
//		if (!companyId.equals(result.getCompanyId())) return null;
//		if (!userId.equals(result.getUserId())) return null;

		SpeechLog result = speechLogMapper.get(id, companyId, userId);
		if (result == null) return null;
		return result;
	}


	/**
	 * 一件検索(企業ID配下の全ユーザ)
	 * @param id エンティティの識別ID
	 * @param companyId 企業ID
	 * @return エンティティ
	 */
	public SpeechLog getSpeechLogCompanyAllUser( Serializable id, String companyId ) {
		SpeechLog result = speechLogMapper.getSpeechLogCompanyAllUser(id, companyId);

		if (result == null) return null;
		return result;
	}


	/**
	 * 一件検索
	 * @param id エンティティの識別ID
	 * @return エンティティ
	 */
	public SpeechLog get( Serializable id ) {
		return this.speechLogDao.get(id);
	}

	/**
	 * 音声解析ログ番号の最大値+1を取得
	 * @param companyId 企業ID
	 * @return 音声解析ログ番号最大値+1
	 */
	public Long getMaxSpeechLogNo( String companyId ) {
		Long result = speechLogMapper.getMaxSpeechLogNo(companyId);
		return result == null ? 1L : result + 1L;
	}

	/**
	 * 複数検索(ユーザ自身のみ)
	 * @param searchForm 検索フォーム
	 * @return 検索結果リスト
	 * @throws Exception エラー
	 */
	public List<SearchResult> searchByUser(SearchForm searchForm) throws Exception {
		List<SearchResult> list = speechLogMapper.searchByUser(searchForm);
		return list;
	}

	/**
	 * 複数検索(企業ID配下の全ユーザ)
	 * @param searchForm 検索フォーム
	 * @return 検索結果リスト
	 * @throws Exception エラー
	 */
	public List<SearchResult> searchByCompany(SearchForm searchForm) throws Exception {
		List<SearchResult> list = speechLogMapper.searchByCompany(searchForm);
		return list;
	}

	/**
	 * 登録
	 * @param entity エンティティ
	 * @return 登録したエンティティ
	 */
	public SpeechLog save( SpeechLog entity ) {
		this.speechLogDao.save(entity);
		return entity;
	}

	/**
	 * 更新
	 * @param entity エンティティ
	 * @return 更新したエンティティ
	 */
	public SpeechLog update( SpeechLog entity ) {
		this.speechLogDao.update(entity);
		return entity;
	}

	/**
	 * 削除対象の音声解析ログを検索する
	 * @param callLogId デフォルト保存期間
	 * @return 音声ファイル削除対象の音声解析ログIDのリスト
	 * @throws Exception エラー
	 */
	public List<SpeechLog> searchForLogDelete(Long defaultLogKeepDays) throws Exception {
		return speechLogMapper.searchForLogDelete(defaultLogKeepDays);
	}

	/**
	 * 音声ファイル削除対象の音声解析ログを検索する
	 * @param speechLogId デフォルト保存期間
	 * @return 音声ファイル削除対象の音声解析ログIDのリスト
	 * @throws Exception エラー
	 */
	public List<SpeechLog> searchForVoiceDelete(Long defaultKeepDays) throws Exception {
		return speechLogMapper.searchForVoiceDelete(defaultKeepDays);
	}

	/**
	 * 削除
	 * @param id エンティティの識別ID
	 */
	public void delete( Serializable id ) {
		this.speechLogDao.delete( this.speechLogDao.get(id) );
	}

}