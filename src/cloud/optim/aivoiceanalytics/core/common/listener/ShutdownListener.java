/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：ShutdownListener.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.aivoiceanalytics.core.common.listener;

import java.lang.ref.Reference ;
import java.lang.reflect.Field ;
import java.lang.reflect.InvocationTargetException ;
import java.lang.reflect.Method ;
import java.sql.Driver ;
import java.sql.DriverManager ;
import java.sql.SQLException ;
import java.util.ArrayList ;
import java.util.Enumeration ;
import java.util.List ;
import java.util.Timer ;

import javax.servlet.ServletContextEvent ;
import javax.servlet.ServletContextListener ;

import org.apache.commons.logging.Log ;
import org.apache.commons.logging.LogFactory ;

/**
 * Tomcat 6.0.24 以降でシャットダウン時にログ出力されるリソースリークメッセージの対処.
 * （対処は行わなくても問題ないが、エラーメッセージが大量に出力されるのでこれを抑制する）
 *
 * @author itsukaha
 */
public class ShutdownListener implements ServletContextListener
{
	/** Logger */
	private Log log = LogFactory.getLog( this.getClass() ) ;

	/**
	 * 初期処理（何もしない）
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized( ServletContextEvent ev ) { /* NOOP */ }

	/**
	 * シャットダウン処理（各種リリース処理）
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed( ServletContextEvent ev )
	{
		// ----- clearReferencesJdbc 対策

		clearReferencesJdbc() ;

		// ----- clearReferencesThreads 対策

		clearReferencesThreads() ;

		// ----- checkThreadLocalMapForLeaks

		checkThreadLocalMapForLeaks() ;
    }

	// -------------------------------------------------------------------------

	/**
	 * clearReferencesJdbc 対策（JDBC ドライバを deregister する）.<br />
	 *
	 * @see 【参考】http://d.hatena.ne.jp/muimy/20100918/1284812424
	 */
	protected void clearReferencesJdbc()
	{
		Enumeration<Driver> drivers = DriverManager.getDrivers() ;

		while ( drivers.hasMoreElements() )
		{
			Driver driver = drivers.nextElement() ;

			try
			{
				DriverManager.deregisterDriver( driver ) ;
				log.info( "DeregisterDriver. : " + driver.getClass().getName() ) ;
			}
			catch ( SQLException ex )
			{
				log.warn( "DeregisterDriver fail. : " + ex ) ;
			}
		}
	}

	// -------------------------------------------------------------------------

	/**
	 * clearReferencesThreads 対策（JDBC ドライバのタイマ（？）を解除する）.<br />
	 *
	 * タイマ解除の方法は DBMS ごとに異なるので、
	 * 未対応の DB については個別に作成する必要があります.
	 */
	protected void clearReferencesThreads()
	{
		// 最近の MySQL コネクタでは不要（少なくとも 5.1.18 以降では不要）
		// MySQL の Timer スレッドに関する clearReferencesThreads エラーが
		// 発生する場合にはここ↓をアンコメントしてください

		// clearReferencesThreads_MySQL() ;

		clearReferencesThreads_HSQLDB() ;

		clearReferencesThreads_postgreSQL() ;
	}

	/**
	 * clearReferencesThreads 対策（MySQL）.<br />
	 */
	protected void clearReferencesThreads_MySQL()
	{
		String dbName = "MySQL" ;
		String holderClassName = "com.mysql.jdbc.ConnectionImpl" ;
		String timerName = "cancelTimer" ;

		try
		{
			// Timer 取得

			Class<?> holderClass = Class.forName( holderClassName ) ;

			Field timerField = holderClass.getDeclaredField( timerName ) ;
			timerField.setAccessible( true ) ;

			Timer timer = (Timer)timerField.get( null ) ;

			// キャンセル呼び出し

			timer.cancel() ;

			log.info( "Cancel timer completed. : " + holderClassName ) ;
		}
		catch ( ClassNotFoundException ex ) { /* 無視 */ log.debug( "Not " + dbName ) ; }
		catch ( Exception ex )
		{
			log.warn(
				"Cancel timer fail. : " + holderClassName + ", " + ex, ex ) ;
		}
	}

	/**
	 * clearReferencesThreads 対策（HSQLDB）.<br />
	 */
	protected void clearReferencesThreads_HSQLDB()
	{
		String dbName = "HSQLDB" ;
		String holderClassName = "org.hsqldb.DatabaseManager" ;
		String timerName = "getTimer" ;

		try
		{
			// Timer 取得

			Class<?> holderClass = Class.forName( holderClassName ) ;

			Method timerGetter = holderClass.getDeclaredMethod( timerName ) ;
			timerGetter.setAccessible( true ) ;

			Object/* HsqlTimer */ timer = timerGetter.invoke( null ) ;

			// キャンセル呼び出し

			Class<?> timerClass = Class.forName( "org.hsqldb.lib.HsqlTimer" ) ;

			Method cancelMethod = timerClass.getDeclaredMethod( "shutdown" ) ;
			cancelMethod.setAccessible( true ) ;

			cancelMethod.invoke( timer ) ;

			log.info( "Cancel timer completed. : " + holderClassName ) ;
		}
		catch ( ClassNotFoundException ex ) { /* 無視 */ log.debug( "Not " + dbName ) ; }
		catch ( Exception ex )
		{
			log.warn(
				"Cancel timer fail. : " + holderClassName + ", " + ex, ex ) ;
		}
	}

	/**
	 * clearReferencesThreads 対策（postgreSQL）.<br />
	 * ※対処方法不明
	 */
	protected void clearReferencesThreads_postgreSQL()
	{
		// NOOP
	}

	// -------------------------------------------------------------------------

	/**
	 * checkThreadLocalMapForLeaks 対策（スレッド停止とスレッドローカルの削除）.<br />
	 *
	 * @see 【参考】http://d.hatena.ne.jp/shinsuke_sugaya/20100211/1265857652
	 */
	protected void checkThreadLocalMapForLeaks()
	{
		Thread[] threads = getThreads() ;

		stopThread( threads ) ;

		clearThreadLocal( threads ) ;
	}

	/**
	 * スレッド一覧を取得.
	 * Get the set of current threads as an array.
	 *
	 * @return スレッド一覧（配列）
	 */
	private Thread[] getThreads()
	{
		// Get the current thread group
		ThreadGroup tg = Thread.currentThread().getThreadGroup() ;

		// Find the root thread group
		while ( tg.getParent() != null )
		{
			tg = tg.getParent() ;
		}

		int threadCountGuess = tg.activeCount() + 50 ;
		Thread[] threads = new Thread[ threadCountGuess ] ;

		int threadCountActual = tg.enumerate( threads ) ;

		// Make sure we don't miss any threads
		while ( threadCountActual == threadCountGuess )
		{
			threadCountGuess *= 2 ;
			threads = new Thread[ threadCountGuess ] ;

			// Note tg.enumerate(Thread[]) silently ignores any threads that
			// can't fit into the array
			threadCountActual = tg.enumerate( threads ) ;
		}

		return threads ;
	}

	/**
	 * 指定された全てのスレッドを停止する.
	 *
	 * @param threads 停止対象スレッド
	 */
	@SuppressWarnings( "deprecation" )
	private void stopThread( Thread[] threads )
	{
		ClassLoader cl = this.getClass().getClassLoader() ;

		List<String> jvmThreadGroupList = new ArrayList<String>() ;
		jvmThreadGroupList.add( "system" ) ;
		jvmThreadGroupList.add( "RMI Runtime" ) ;

		// Iterate over the set of threads
		for ( Thread thread : threads )
		{
			if ( thread == null ) continue ;

			ClassLoader ccl = thread.getContextClassLoader() ;

			if ( ( ccl == null ) || ( ccl != cl ) ) continue ;

			// Don't warn about this thread
			if ( thread == Thread.currentThread() ) continue ;

			// Don't warn about JVM controlled threads
			ThreadGroup tg = thread.getThreadGroup() ;

			if ( ( tg != null ) && jvmThreadGroupList.contains( tg.getName() ) )
			{
				continue ;
			}

			waitThread( thread ) ;

			// Skip threads that have already died
			if ( ! thread.isAlive() ) continue ;

			log.info( "Interrupting a thread [" + thread.getName() + "]..." ) ;

			thread.interrupt() ;

			waitThread( thread ) ;

			// Skip threads that have already died
			if ( ! thread.isAlive() ) continue ;

			log.info( "Stopping a thread [" + thread.getName() + "]..." ) ;

			thread.stop() ; // サーバ終了時なのでおそらく問題ない
		}
	}

	/**
	 * スレッドの終了を待つ.
	 *
	 * @param thread 対象スレッド
	 */
	private void waitThread( Thread thread )
	{
		int count = 0 ;
		while ( thread.isAlive() && count < 5 )
		{
			try
			{
				Thread.sleep( 100 ) ;
			}
			catch ( InterruptedException e ) {}
			count++ ;
		}
	}

	/**
	 * 指定されたスレッドのスレッドローカルを削除
	 *
	 * @param threads スレッドローカル削除対象スレッド
	 */
	private void clearThreadLocal( Thread[] threads )
	{
		ClassLoader cl = this.getClass().getClassLoader() ;

		Field threadLocalsField = null ;
		Field inheritableThreadLocalsField = null ;
		Field tableField = null ;

		try
		{
			threadLocalsField = Thread.class.getDeclaredField( "threadLocals" ) ;
			threadLocalsField.setAccessible( true ) ;

			inheritableThreadLocalsField = Thread.class.getDeclaredField( "inheritableThreadLocals" ) ;
			inheritableThreadLocalsField.setAccessible( true ) ;

			// Make the underlying array of ThreadLoad.ThreadLocalMap.Entry
			// objects
			// accessible
			Class<?> tlmClass = Class.forName( "java.lang.ThreadLocal$ThreadLocalMap" ) ;
			tableField = tlmClass.getDeclaredField( "table" ) ;
			tableField.setAccessible( true ) ;
		}
		catch ( Exception e ) { /* ignore */ }

		for ( Thread thread : threads )
		{
			if ( thread == null ) continue ;

			Object threadLocalMap = null ;

			try
			{
				// Clear the first map
				threadLocalMap = threadLocalsField.get( thread ) ;
				clearThreadLocalMap( cl, threadLocalMap, tableField ) ;
			}
			catch ( Exception e ) { /* ignore */ }

			try
			{
				// Clear the second map
				threadLocalMap = inheritableThreadLocalsField.get( thread ) ;
				clearThreadLocalMap( cl, threadLocalMap, tableField ) ;
			}
			catch ( Exception e ) { /* ignore */ }
		}
	}

	/**
	 * スレッドローカルを削除.
	 *
	 * @param cl クラスローダ
	 * @param map ThreadLocal 一覧
	 * @param internalTableField ThreadLocal 一覧の「table」メンバ
	 *
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws InvocationTargetException
	 */
	private void clearThreadLocalMap(
		ClassLoader cl, Object map, Field internalTableField )
		throws NoSuchMethodException, IllegalAccessException,
			NoSuchFieldException, InvocationTargetException
	{
		if ( map == null ) return ;

		Method mapRemove = map.getClass().getDeclaredMethod( "remove", ThreadLocal.class ) ;
		mapRemove.setAccessible( true ) ;

		Object[] table = (Object[])internalTableField.get( map ) ;

		if ( table == null ) return ;

		for ( int j = 0 ; j < table.length ; j++ )
		{
			if ( table[j] == null ) continue ;

			boolean remove = false ;

			// Check the key
			Field keyField = Reference.class.getDeclaredField( "referent" ) ;
			keyField.setAccessible( true ) ;

			Object key = keyField.get( table[j] ) ;
			if ( cl.equals( key ) ||
				 ( key != null && cl == key.getClass().getClassLoader() ) )
			{
				remove = true ;
			}

			// Check the value
			Field valueField = table[j].getClass().getDeclaredField( "value" ) ;
			valueField.setAccessible( true ) ;

			Object value = valueField.get( table[j] ) ;
			if ( cl.equals( value ) ||
				 ( value != null && cl == value.getClass().getClassLoader() ) )
			{
				remove = true ;
			}

			if ( remove )
			{
				Object entry = ( (Reference<?>)table[j] ).get() ;

				log.info( "Removing " + key.toString() + " from a thread local..." ) ;

				mapRemove.invoke( map, entry ) ;
			}
		}
	}
}
