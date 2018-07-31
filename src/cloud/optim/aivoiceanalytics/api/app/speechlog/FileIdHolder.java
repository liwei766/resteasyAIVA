/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：FileIdHolder.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.api.app.speechlog;

import java.io.Serializable ;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope ;
import org.springframework.context.annotation.ScopedProxyMode ;
import org.springframework.stereotype.Component ;

import cloud.optim.aivoiceanalytics.core.common.utility.ToStringHelper;

/**
 * 音声ファイルダウンロード用のファイルIDを保持する
 */
@Component
@Scope( proxyMode=ScopedProxyMode.TARGET_CLASS, value="session" )
public class FileIdHolder implements Serializable
{
	/** serialVersionUID */
	private static final long serialVersionUID = 1L ;

	/**
	 * ファイルIDマップ
	 */
	private Map<String, Long> fileIdMap ;

	/**
	 * 文字列表現への変換
	 *
	 * @return 文字列表現
	 */
	@Override
	public String toString() { return ToStringHelper.toString( this ) ; }

	/**
	 * ファイルIDマップにファイルIDが存在しているかチェックする.
	 * 存在している場合はファイルIDマップから削除する
	 * @param fileId ファイルID
	 * @return fileId ファイルIDが存在していた場合は音声解析ログID、存在していない場合はnull
	 */
	public Long isExistFileId( String fileId )
	{
		if(fileIdMap == null) return null;
		return fileIdMap.remove(fileId) ;
	}

	/**
	 * fileId 設定.
	 *
	 * @param fileId ファイルIDマップに設定する値.
	 */
	public void setFileId( String fileId, Long speechLogId )
	{
		if(this.fileIdMap == null) {
			this.fileIdMap = new HashMap<>();
		}
		this.fileIdMap.put(fileId, speechLogId) ;
	}
}