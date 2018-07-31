/**
 * Copyright (C) 2017 OPTiM Corp. All Rights Reserved
 * システム名：
 * ソースファイル名：VoiceEncodeJob.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.job.voiceencode;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;

import cloud.optim.aivoiceanalytics.api.entity.VoiceEncodeQueue;

/**
 * 音声ファイル圧縮処理ジョブ<br/>
 */
public class VoiceEncodeJob {

	/** Commons Logging instance.  */
	private Log log = LogFactory.getFactory().getInstance(this.getClass().getName());

	/** VoiceEncodeQueueService. */
	@Resource
	private VoiceEncodeQueueService voiceEncodeQueueService;

	// -------------------------------------------------------------------------

	/** 処理件数 */
	@Value( "${encode.max.result}" )
	private int maxResult;

	// -------------------------------------------------------------------------

	/**
	 * 音声ファイル圧縮処理
	 */
	public void encode() throws Exception {

		log.info("### START");

		try {
			int dataCount = 0;	// 処理件数
			int errorCount = 0;	// エラー件数

			while (true) {

				// キューからデータを取得する
				List<VoiceEncodeQueue> list =  voiceEncodeQueueService.search(maxResult, errorCount);

				// なければ終了
				if (list == null || list.isEmpty()) break;
				dataCount += list.size();

				// 圧縮
				for (VoiceEncodeQueue each : list) {
					try {
						voiceEncodeQueueService.encode(each);
					} catch (Exception e) {
						log.error(String.format("speechLogId : %d, speechLogDetailId : %d\n", each.getSpeechLogId(), each.getSpeechLogDetailId()), e);
						errorCount++;
					}
				}
			}

			log.info(String.format("### END 処理件数：%d件、エラー件数：%d件", dataCount, errorCount));

		} catch (Exception e) {
			log.error("### ABORT", e);
		}
    }
}
