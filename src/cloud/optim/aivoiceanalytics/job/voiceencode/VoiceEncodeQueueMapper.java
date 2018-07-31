/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：VoiceEncodeQueueMapper.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.job.voiceencode;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import cloud.optim.aivoiceanalytics.api.entity.VoiceEncodeQueue;

/**
 * MyBatis VoiceEncodeQueueMapper I/F.<br/>
 */
@Component
public interface VoiceEncodeQueueMapper {

	/**
	 * 圧縮対象データ検索
	 *
	 * @param maxResult 検索件数上限
	 * @param offset オフセット
	 * @return 検索結果
	 */
	List<VoiceEncodeQueue> search( @Param("maxResult") Integer maxResult,  @Param("offset") Integer offset );
}
