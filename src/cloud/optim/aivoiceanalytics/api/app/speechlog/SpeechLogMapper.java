/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：SpeechLogMapper.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.speechlog;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.api.entity.SpeechLog;

/**
 * MyBatis SpeechLogMapper I/F.<br/>
 */
@Component
public interface SpeechLogMapper {

	/**
	 * 検索
	 *
	 * @param form 検索条件
	 * @return 検索結果
	 */
	List<SearchResult> searchByUser( SearchForm form );


	/**
	 * 検索
	 *
	 * @param form 検索条件
	 * @return 検索結果
	 */
	List<SearchResult> searchByCompany( SearchForm form );


	/**
	 * 音声解析詳細取得
	 *
	 * @param speechLogId 音声解析ログID
	 * @param companyId 検索条件
	 * @param userId ユーザID
	 * @return 検索結果
	 */
	SpeechLog get(@Param("speechLogId") Serializable speechLogId,@Param("companyId") String companyId, @Param("userId") String userId);

	/**
	 * 音声解析詳細取得(企業ID配下の全ユーザ)
	 *
	 * @param speechLogId 音声解析ログID
	 * @param companyId 検索条件
	 * @return 検索結果
	 */
	SpeechLog getSpeechLogCompanyAllUser(@Param("speechLogId") Serializable speechLogId,@Param("companyId") String companyId);

	/**
	 * 企業内の音声解析ログ番号の最大値を取得する
	 * @param companyId 企業ID
	 * @return 音声解析ログ番号の最大値
	 */
	Long getMaxSpeechLogNo(@Param("companyId")String companyId);

	/**
	 * 削除対象の音声解析ログを検索する
	 * @param defaultLogKeepDays デフォルト保存期間
	 * @return 削除対象の音声解析ログIDのリスト
	 */
	List<SpeechLog> searchForLogDelete(@Param("defaultLogKeepDays")Long defaultLogKeepDays);

	/**
	 * 音声ファイル削除対象の音声解析ログを検索する
	 * @param defaultKeppDays デフォルト保存期間
	 * @return 音声ファイル削除対象の音声解析ログIDのリスト
	 */
	List<SpeechLog> searchForVoiceDelete(@Param("defaultKeppDays")Long defaultKeppDays);
}
