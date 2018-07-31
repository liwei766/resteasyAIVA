/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechLogDetailService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */

package cloud.optim.aivoiceanalytics.api.app.speechlogdetail;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cloud.optim.aivoiceanalytics.api.entity.SpeechLogDetail;
import cloud.optim.aivoiceanalytics.api.entity.VoiceEncodeQueue;
import cloud.optim.aivoiceanalytics.api.entity.dao.SpeechLogDetailDao;
import cloud.optim.aivoiceanalytics.api.entity.dao.VoiceEncodeQueueDao;
import cloud.optim.aivoiceanalytics.core.common.utility.Cryptor;

/**
 * SpeechLogDetailService実装.<br/>
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Throwable.class, isolation=Isolation.READ_COMMITTED)
public class SpeechLogDetailService {

	//** Commons Logging instance.  */
	//@SuppressWarnings("unused")
	//private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

	/** 暗号ユーティリティの共通鍵. */
	@Value("${cryptor.key}")
	private String cryptorKey;

	/**
	 * HibernateDAO
	 */
	@Resource
	private SpeechLogDetailDao speechLogDetailDao;

	@Resource
	private VoiceEncodeQueueDao voiceEncodeQueueDao;

	/**
	 * SpeechLogDetailDao 取得
	 * @return SpeechLogDetailDao
	 */
	public SpeechLogDetailDao getSpeechLogDetailDao() {
		return speechLogDetailDao;
	}

	/**
	 * MyBatis Mapper
	 */
	@Resource
	private SpeechLogDetailMapper speechLogDetailMapper;

	/**
	 * SpeechLogDetailMapper 取得
	 * @return SpeechLogDetailMapper
	 */
	public SpeechLogDetailMapper getSpeechLogDetailMapper() {
		return speechLogDetailMapper;
	}


	/**
	 * 一件検索.
	 * @param id エンティティの識別ID
	 * @return エンティティ
	 */
	public SpeechLogDetail getSpeechLogDetail(Serializable id) {
		SpeechLogDetail entity = this.speechLogDetailDao.get(id);
		entity = this.decrypt(entity);
		return entity;
	}

	/**
	 * 登録.
	 * @param entity エンティティ
	 * @return 登録したエンティティ
	 */
	public SpeechLogDetail save(SpeechLogDetail entity) {
		entity = this.encrypt(entity);
		this.speechLogDetailDao.save(entity);
		return entity;
	}


	/**
	 * 更新.
	 * @param entity エンティティ
	 * @return 更新したエンティティ(更新後の音声解析内容は復号化する)
	 */
	public SpeechLogDetail update(SpeechLogDetail entity) {
		entity = this.encrypt(entity);
		this.speechLogDetailDao.update(entity);
		this.speechLogDetailDao.evict(entity);
		return this.decrypt(entity);
	}

	/**
	 * 論理削除
	 * @param entity エンティティ
	 */
	public void delete( SpeechLogDetail entity ) {
		entity.setDeleteDate(new Date());
		this.speechLogDetailDao.update( entity );
	}

	/**
	 *  音声解析ログIDに紐づく詳細ログを一括論理削除
	 * @param speechLogId 音声解析ログID
	 * @param companyId 企業ID
	 * @param updateUserId 更新ユーザ ID
	 * @param updateUserName 更新ユーザ名
	 * @param deleteDate 削除日時
	 */
	public void logicalDeleteAllSpeechLogDetailsByCompany( Long speechLogId, String companyId, String updateUserId, String updateUserName, Date deleteDate) {
		speechLogDetailMapper.logicalDeleteAllSpeechLogDetailsByCompany(speechLogId, companyId, updateUserId, updateUserName, deleteDate);
	}

	/**
	 * 削除
	 * @param id エンティティの識別ID
	 */
	public void delete( Serializable id ) {
		this.speechLogDetailDao.delete( this.speechLogDetailDao.get(id) );
	}

	/**
	 * 音声解析ログIDによる検索.
	 * @param companyId 企業ID
	 * @param speechLogId 音声解析ログID
	 * @return 音声解析ログ詳細情報のリスト
	 */
	public List<SpeechLogDetail> getDetails(String companyId, Long speechLogId) {
		return this.decrypt(this.speechLogDetailMapper.getDetails(companyId, speechLogId));
	}

	/**
	 * 音声解析ログIDによる検索.
	 * @param companyId 企業ID
	 * @param userId ユーザID
	 * @param speechLogId 音声解析ログID
	 * @return 音声解析ログ詳細情報のリスト
	 */
	public List<SpeechLogDetail> getDetailsByUser(String companyId, String userId, Long speechLogId) {
		return this.decrypt(this.speechLogDetailMapper.getDetailsByUser(companyId, userId, speechLogId));
	}

	/**
	 * 音声が作成されていない音声解析ログ詳細を検索する.
	 * @param companyId 企業ID
	 * @param speechLogId 音声解析ログID
	 * @return 音声無し音声解析ログ詳細情報のリスト
	 */
	public List<SpeechLogDetail> searchNoVoice(String companyId, Long speechLogId) {
		return this.decrypt(this.speechLogDetailMapper.searchNoVoice(companyId, speechLogId));
	}

	/**
	 * 音声のある音声解析ログ詳細を検索する.
	 * @param companyId 企業ID
	 * @param userId ユーザID
	 * @param speechLogId 音声解析ログID
	 * @return 音声有り音声解析ログ詳細情報のリスト 画面表示で使用しないので内容は復号化しない
	 */
	public List<SpeechLogDetail> searchExistVoiceByUser(String companyId,String userId, Long speechLogId) {
		return this.speechLogDetailMapper.searchExistVoiceByUser(companyId, userId, speechLogId);
	}


	/**
	 * 音声のある音声解析ログ詳細を検索する.
	 * @param companyId 企業ID
	 * @param speechLogId 音声解析ログID
	 * @return 音声有り音声解析ログ詳細情報のリスト 画面表示で使用しないので内容は復号化しない
	 */
	public List<SpeechLogDetail> searchExistVoiceByCompany(String companyId, Long speechLogId) {
		return this.speechLogDetailMapper.searchExistVoiceByCompany(companyId, speechLogId);
	}

	/**
	 * 音声のある音声解析ログ詳細を論理削除も含めて検索する.
	 * @param companyId 企業ID
	 * @param speechLogId 音声解析ログID
	 * @return 音声有り音声解析ログ詳細情報のリスト 画面表示で使用しないので内容は復号化しない
	 */
	public List<SpeechLogDetail> searchAllExistVoiceByCompany(String companyId, Long speechLogId) {
		return this.speechLogDetailMapper.searchAllExistVoiceByCompany(companyId, speechLogId);
	}

	/**
	 * 更新して圧縮キューに登録する
	 * @param entity エンティティ
	 * @return 更新したエンティティ(更新後の音声解析内容は復号化する)
	 */
	public SpeechLogDetail updateAndRegistQueue(SpeechLogDetail entity) {
		entity = this.update(entity);
		Date now = new Date();
		VoiceEncodeQueue queue = new VoiceEncodeQueue();
		queue.setCompanyId(entity.getCompanyId());
		queue.setSpeechLogId(entity.getSpeechLogId());
		queue.setSpeechLogDetailId(entity.getSpeechLogDetailId());
		queue.setCreateDate(now);
		queue.setCreateUserId(entity.getUpdateUserId());
		queue.setCreateUserName(entity.getUpdateUserName());
		queue.setUpdateDate(now);
		voiceEncodeQueueDao.save(queue);
		return entity;
	}

	private SpeechLogDetail encrypt(SpeechLogDetail entity) {
		entity.setLog(Cryptor.encrypt(this.cryptorKey, entity.getLog()));
		return entity;
	}

	private SpeechLogDetail decrypt(SpeechLogDetail entity) {
		entity.setLog(Cryptor.decrypt(this.cryptorKey, entity.getLog()));
		return entity;
	}

	private List<SpeechLogDetail> decrypt(List<SpeechLogDetail> speechLogDetailList) {
		for (SpeechLogDetail entity : speechLogDetailList) {
			String log = entity.getLog();
			if (StringUtils.isNotEmpty(log)) {
				entity = this.decrypt(entity);
			}
		}
		return speechLogDetailList;
	}

}
