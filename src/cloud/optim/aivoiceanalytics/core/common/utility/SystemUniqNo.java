/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * ソースファイル名：SystemUniqNo.java
 */
package cloud.optim.aivoiceanalytics.core.common.utility;

import java.util.Date ;
import java.util.concurrent.atomic.AtomicInteger ;

/**
 * システム内（VM 内）でユニークな文字列を生成するためのクラス
 *
 * @author itsukaha
 */
public class SystemUniqNo
{
	/** ユニーク ID 生成用の一連番号 */
	private static AtomicInteger uniqNo = new AtomicInteger( 1 ) ;

	/**
	 * システムユニーク ID 生成（サーバ内でユニーク）
	 *
	 * @return 生成したシステムユニーク ID
	 */
	public static String getNextUniqId()
	{
		// yyyymmdd_hhmmss_SSS_xxx

		String next = String.format(
			"%1$tY%1$tm%1$td_%1$tH%1$tM%1$tS_%1$tL_%2$03d",
			new Date(), getNextSeqNo() ) ;

		return next ;
	}

	/**
	 * 一連番号取得
	 *
	 * @return 取得した一連番号
	 */
	protected static short getNextSeqNo()
	{
		uniqNo.compareAndSet( 1000, 1 ) ;

		int ret = uniqNo.getAndIncrement() ;

		return (short)ret ;
	}
}
