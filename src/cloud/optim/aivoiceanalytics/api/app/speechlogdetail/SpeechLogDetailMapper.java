/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechLogDetailMapper.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.speechlogdetail;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.api.entity.SpeechLogDetail;

/**
 * MyBatis SpeechLogDetailMapper I/F.<br/>
 */
@Component
public interface SpeechLogDetailMapper {


	/**
	 * 音声解析ログIDによる検索
	 * @param companyId 企業ID
	 * @param speechLogId 音声解析ログID
	 * @return 音声解析ログ詳細情報のリスト
	 */
	List<SpeechLogDetail> getDetails(@Param("companyId") String companyId, @Param("speechLogId") Long speechLogId);

	/**
	 * 音声解析ログIDによる検索
	 * @param companyId 企業ID
	 * @param userId ユーザID
	 * @param speechLogId 音声解析ログID
	 * @return 音声解析ログ詳細情報のリスト
	 */
	List<SpeechLogDetail> getDetailsByUser(@Param("companyId") String companyId, @Param("userId") String userId, @Param("speechLogId") Long speechLogId);

	/**
	 * 音声無し音声解析ログ詳細情報検索
	 * @param companyId 企業ID
	 * @param speechLogId 音声解析ログID
	 * @return 音声無し音声解析ログ詳細情報のリスト
	 */
	List<SpeechLogDetail> searchNoVoice(@Param("companyId") String companyId, @Param("speechLogId") Long speechLogId);

	/**
	 * 音声有り音声解析ログ詳細情報検索
	 * @param companyId 企業ID
	 * @param userId ユーザID
	 * @param speechLogId 音声解析ログID
	 * @return 音声有り音声解析ログ詳細情報のリスト
	 */
	List<SpeechLogDetail> searchExistVoiceByUser(@Param("companyId") String companyId, @Param("userId") String userId, @Param("speechLogId") Long speechLogId);

	/**
	 * 音声有り音声解析ログ詳細情報検索
	 * @param companyId 企業ID
	 * @param speechLogId 音声解析ログID
	 * @return 音声有り音声解析ログ詳細情報のリスト
	 */
	List<SpeechLogDetail> searchExistVoiceByCompany(@Param("companyId") String companyId, @Param("speechLogId") Long speechLogId);

	/**
	 * 音声有り音声解析ログ詳細情報検索(論理削除も含む)
	 * @param companyId 企業ID
	 * @param speechLogId 音声解析ログID
	 * @return 音声有り音声解析ログ詳細情報のリスト
	 */
	List<SpeechLogDetail> searchAllExistVoiceByCompany(@Param("companyId") String companyId, @Param("speechLogId") Long speechLogId);


	/**
	 * 音声有無フラグ無しに更新する
	 * @param speechLogDetailId 音声解析ログ詳細ID
	 */
	void updateVoiceExistence(@Param("speechLogDetailId") Long speechLogDetailId);

	/**
	 * 音声解析ログIDに紐づく詳細ログを一括論理削除
	 * @param speechLogId 音声解析ログID
	 * @param companyId 企業ID
	 * @param updateUserId 更新ユーザID
	 * @param updateUserName 更新ユーザ名
	 * @param deleteDate 削除日時
	 */
	void logicalDeleteAllSpeechLogDetailsByCompany( @Param("speechLogId") Long speechLogId, @Param("companyId") String companyId,
		@Param("updateUserId") String updateUserId, @Param("updateUserName") String updateUserName,
		@Param("deleteDate") Date deleteDate);

	/**
	 * 音声解析ログIDに紐づく詳細ログを一括削除
	 * @param speechLogId 音声解析ログID
	 */
	void deleteAllSpeechLogDetails( @Param("speechLogId") Long speechLogId);
}
