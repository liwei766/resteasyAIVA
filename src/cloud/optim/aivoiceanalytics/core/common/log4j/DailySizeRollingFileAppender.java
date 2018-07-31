/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：DailySizeRollingFileAppender.java
 *
 */
package cloud.optim.aivoiceanalytics.core.common.log4j;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * <code>RollingFileAppender</code> と <code>DailyRollingFileAppender</code> の
 * 機能を組み合わせたクラス.
 *
 *
 *
 * <b>maxFileSize</b> の指定がある場合
 * 出力中のログファイルのサイズが <b>maxFileSize</b> を超えた場合は、
 * ローテーションが行われます.
 *
 * <b>datePattern</b> の指定がある場合
 * 日付パターンが切り替わる時にローテーションが行われます.
 *
 * <b>backupUnit</b> の指定がある場合
 * <b>datePattern</b> が指定されている場合
 * 現在日付から <b>datePattern</b> と <b>backupUnit</b>
 * の値に応じたバックアップ期間を算出して、
 * バックアップ期間外のログファイルを物理削除します.
 * <b>maxFileSize</b> のみが指定されている場合
 * ログファイル数が、<b>backupUnit</b> の値を超えた場合に、
 * インデックスが若い順にログファイルを削除します.
 *
 * <p>
 * 本クラスで追加されたプロパティの種類とデフォルト値は以下の通り.
 * </p>
 *
 * <table border="1">
 * <tr>
 * <th><b>プロパティ名</b></th>
 * <th><b>説明</b></th>
 * <th><b>デフォルト値</b></th>
 * </tr>
 * <tr>
 * <td nowrap="true"><b>maxFileSize</b></td>
 * <td>最大ファイルサイズ</td>
 * <td nowrap="true">デフォルト値なし</td>
 * </tr>
 * <tr>
 * <td nowrap="true"><b>datePattern</b></td>
 * <td>日付パターン</td>
 * <td nowrap="true">デフォルト値なし</td>
 * </tr>
 * <tr>
 * <td nowrap="true"><b>maxBackupUnit</b></td>
 * <td>バックアップ単位</td>
 * <td nowrap="true">デフォルト値なし</td>
 * </tr>
 * </table>
 *
 * ■日付のみでローテーションさせたい場合は、
 * <b>MaxFileSize</b> プロパティを省略して下さい.
 * ■サイズのみでローテーションする必要がある場合は、
 * <b>datePattern</b> プロパティを省略して下さい.
 * ■サイズと日付でローテーションさせたい場合は、
 * <b>maxFileSize</b> と <b>datePattern</b> の
 * 2つのプロパティを設定ファイル等で設定します.
 *
 * <h4>設定ファイル例</h4>
 *
 * <pre>
 *   &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
 *   &lt;!DOCTYPE log4j:configuration SYSTEM &quot;log4j.dtd&quot;&gt;
 *
 *   &lt;log4j:configuration xmlns:log4j=&quot;http://jakarta.apache.org/log4j/&quot; debug=&quot;false&quot;&gt;
 *       &lt;appender name=&quot;FILE&quot; class=&quot;cloud.optim.aivoiceanalytics.core.common.log4j.DailySizeRollingFileAppender&quot;&gt;
 *           &lt;param name=&quot;File&quot; value=&quot;C:\\logs\\AIVoiceanAlytics.log&quot; /&gt;
 *           &lt;param name=&quot;maxFileSize&quot; value=&quot;10MB&quot; /&gt;
 *           &lt;param name=&quot;datePattern&quot; value=&quot;'.'yyyy-MM-dd&quot; /&gt;
 *           &lt;param name=&quot;maxBackupUnit&quot; value=&quot;7&quot; /&gt;
 *           &lt;layout class=&quot;org.apache.log4j.PatternLayout&quot;&gt;
 *               &lt;param name=&quot;ConversionPattern&quot; value=&quot;%x %d %t %-5p %c{2} - %m%n&quot; /&gt;
 *           &lt;/layout&gt;
 *       &lt;/appender&gt;
 *
 *       &lt;省略&gt;
 *
 *   &lt;/log4j:configuration&gt;
 * </pre>
 *
 * @author ebiken
 */
public class DailySizeRollingFileAppender extends FileAppender {

	/** 日付パターン指定タイプ(不正) */
	private static final int TOP_OF_TROUBLE = -1;
	/** 日付パターン指定タイプ(分) */
	private static final int TOP_OF_MINUTE = 0;
	/** 日付パターン指定タイプ(時) */
	private static final int TOP_OF_HOUR = 1;
	/** 日付パターン指定タイプ(半日) */
	private static final int HALF_DAY = 2;
	/** 日付パターン指定タイプ(日) */
	private static final int TOP_OF_DAY = 3;
	/** 日付パターン指定タイプ(週) */
	private static final int TOP_OF_WEEK = 4;
	/** 日付パターン指定タイプ(月) */
	private static final int TOP_OF_MONTH = 5;

	/** timezone(GMT) */
	private static final TimeZone TIME_ZONE_GMT = TimeZone.getTimeZone("GMT");
	/** ロケール(デフォルト) */
	private static final Locale LOCALE_DEFAULT = Locale.JAPAN;

	/** サイズローテーションを実施することを示す定数 */
	private static final int BY_SIZE = 0;
	/** 日付ローテーションを実施することを示す定数 */
	private static final int BY_DATE = 1;



	/** 最大ファイルサイズ */
	protected long maxFileSize = 0L;

	/** 日付パターン */
	protected String datePattern;

	/** バックアップ単位 */
	protected int maxBackupUnit = 0;

	/** 出力予定ファイル名 */
	protected String scheduledFileName;



	/** ロケール */
	protected Locale locale = LOCALE_DEFAULT;

	/** 次回チェックミリ秒 */
	private long nextCheck = System.currentTimeMillis() - 1;

	/** システム日付 */
	private final Date now = new Date();

	/** 日付フォーマット */
	private SimpleDateFormat sdf;

	/** 日付ローテート用のカレンダー */
	private final DateRollingCalendar rc = new DateRollingCalendar();



	/**
	 * コンストラクタ
	 */
	public DailySizeRollingFileAppender() {
		super();
	}

	/**
	 * コンストラクタ
	 *
	 * @param layout 出力ログフォーマットクラスオブジェクト
	 * @param fileName ログファイル名
	 * @param append モード(false:上書き、true:追記)
	 * @throws IOException 入出力例外
	 */
	public DailySizeRollingFileAppender(
			Layout layout, String fileName, boolean append)
			throws IOException {
		super(layout, fileName, append);
	}

	/**
	 * コンストラクタ
	 *
	 * @param layout 出力ログフォーマットクラスオブジェクト
	 * @param fileName ログファイル名
	 * @throws IOException 入出力例外
	 */
	public DailySizeRollingFileAppender(
			Layout layout, String fileName)
			throws IOException {
		super(layout, fileName);
	}

	/**
	 * コンストラクタ
	 *
	 * @param layout 出力ログフォーマットクラスオブジェクト
	 * @param fileName ログファイル名
	 * @param datePattern 日付パターン
	 * @throws IOException 入出力例外
	 */
	public DailySizeRollingFileAppender(
			Layout layout, String fileName, String datePattern)
			throws IOException {
		super(layout, fileName, true);
		this.datePattern = datePattern;
		activateOptions();
	}

	/**
	 * コンストラクタ
	 *
	 * @param layout 出力ログフォーマットクラスオブジェクト
	 * @param fileName ログファイル名
	 * @param datePattern 日付パターン
	 * @param append モード(false:上書き、true:追記)
	 * @throws IOException 入出力例外
	 */
	public DailySizeRollingFileAppender(
			Layout layout, String fileName, String datePattern, boolean append)
			throws IOException {
		super(layout, fileName, append);
		this.datePattern = datePattern;
		activateOptions();
	}

	/**
	 * ログ出力を行うファイルを設定し、オープンする.
	 *
	 * @param fileName ログファイル名
	 * @param append モード(false:上書き、true:追記)
	 * @param bufferedIO バッファリングをするか否かを指定する
	 *        (デフォルトは false でバッファリングをしない)。
	 *        true にするとバッファリングのおかげで頻繁にログが取られる場合には
	 *        効率が向上することもある。
	 * @param bufferSize バッファリングをする場合のバッファサイズ
	 */
	@Override
	public synchronized void setFile(String fileName, boolean append,
			boolean bufferedIO, int bufferSize) throws IOException {
		super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
		if (append) {
			File f = new File(fileName);
			((CountingQuietWriter) this.qw).setCount(f.length());
		}
	}

	/**
	 * FILE_OPTIONの値がnullでない場合に、setFile(java.lang.String)を呼び出す。
	 *
	 */
	@Override
	public void activateOptions() {
		super.activateOptions();
		this.now.setTime(System.currentTimeMillis());
		// 日付パターン指定がある場合
		if (this.datePattern != null) {
			// 日付フォーマット設定
			this.sdf = new SimpleDateFormat(this.datePattern);
			// 日付ローテートタイプ設定
			int type = computeCheckPeriod();
			// ログ出力
			printPeriodicity(type);
			// カレンダにtypeを設定
			this.rc.setType(type);
			// ログ出力予定ファイル名設定
			File file = new File(this.fileName);
			this.scheduledFileName = this.fileName
					+ this.sdf.format(new Date(file.lastModified()));
		}
		// パラメータチェック
		this.checkParams();
	}

	/**
	 * 日付パターンタイプを出力する.
	 *
	 * @param type 日付パターンタイプ
	 */
	private void printPeriodicity(int type) {
		switch (type) {
		// 分ローテート
		case TOP_OF_MINUTE:
			LogLog.debug("Appender [" + name + "] to be rolled every minute.");
			break;
		// 時ローテート
		case TOP_OF_HOUR:
			LogLog.debug("Appender [" + name
					+ "] to be rolled on top of every hour.");
			break;
		// 半日ローテート
		case HALF_DAY:
			LogLog.debug("Appender [" + name
					+ "] to be rolled at midday and midnight.");
			break;
		// 日ローテート
		case TOP_OF_DAY:
			LogLog.debug("Appender [" + name + "] to be rolled at midnight.");
			break;
		// 週ローテート
		case TOP_OF_WEEK:
			LogLog.debug("Appender [" + name
					+ "] to be rolled at start of week.");
			break;
		// 月ローテート
		case TOP_OF_MONTH:
			LogLog.debug("Appender [" + name
					+ "] to be rolled at start of every month.");
			break;
		// 想定外のタイプ
		default:
			LogLog.warn("Unknown periodicity for appender [" + name + "].");
		}
	}

	/**
	 * 日付ローテートタイプを算出する.
	 *
	 * @return 日付ローテートタイプ
	 */
	private int computeCheckPeriod() {
		DateRollingCalendar rollingCalendar = new DateRollingCalendar(
				TIME_ZONE_GMT, this.locale);
		Date epoch = new Date(0);
		if (this.datePattern != null) {
			for (int i = TOP_OF_MINUTE; i <= TOP_OF_MONTH; i++) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						this.datePattern);
				String r0 = simpleDateFormat.format(epoch);
				rollingCalendar.setType(i);
				Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
				String r1 = simpleDateFormat.format(next);
				if (r0 != null && r1 != null && !r0.equals(r1)) {
					return i;
				}
			}
		}
		return TOP_OF_TROUBLE;
	}

	/**
	 * ローテーションを実施した後に、<br/>
	 * ログファイルへの書込み処理を行う.
	 *
	 */
	@Override
	protected void subAppend(LoggingEvent event) {

		long n = System.currentTimeMillis();
		// 日付ローテーション
		if (StringUtils.isNotEmpty(this.datePattern) && n >= this.nextCheck) {
			this.now.setTime(n);
			this.nextCheck = this.rc.getNextCheckMillis(this.now);
			try {
				// ローテーション実施
				rollOverTime();
			} catch (IOException ioe) {
				LogLog.error("rollOver() failed.", ioe);
			}

			if (this.maxBackupUnit > 0) {
				// バックアップファイルの削除
				this.deleteOutsideBackupUnitFile();
			}

		}

		// サイズローテーション
		if ((this.maxFileSize > 0L && this.fileName != null)
				&& ((CountingQuietWriter) this.qw).getCount() >= this.maxFileSize) {
			// ローテーション実施
			rollOverSize();

			if (this.maxBackupUnit > 0) {
				// バックアップファイルの削除
				this.deleteOutsideBackupUnitFile();
			}
		}

		// 書き込み実行
		super.subAppend(event);

	}

	/**
	 * サイズローテーション処理.
	 */
	private void rollOverSize() {

		if (this.qw != null) {
			LogLog.debug("rolling over count="
					+ ((CountingQuietWriter) this.qw).getCount());
		}

		// ローテーション実施
		rotateLogFilesBy(BY_SIZE);

		try {
			this.setFile(this.fileName, false, bufferedIO, bufferSize);
		} catch (IOException e) {
			LogLog.error("setFile(" + this.fileName + ", false) call failed.", e);
		}
	}

	/**
	 * 時間ローテーション処理.
	 *
	 * @throws IOException 入出力例外
	 */
	private void rollOverTime() throws IOException {

		// 出力予定のファイルと出力中のファイル名が、
		// 同じ場合はローテート不要
		String datedFileName = this.fileName + this.sdf.format(this.now);
		if (this.scheduledFileName.equals(datedFileName)) {
			return;
		}

		// ローテーション実施
		rotateLogFilesBy(BY_DATE);

		try {
			this.setFile(this.fileName, false, this.bufferedIO, this.bufferSize);
		} catch (IOException e) {
			this.errorHandler.error("setFile(" + this.fileName + ", false) call failed.");
		}

		// 出力予定ファイル名設定
		this.scheduledFileName = datedFileName;
	}

	/**
	 * maxBackupUnit の値に応じて、 バックアップファイルを削除.
	 *
	 */
	private void deleteOutsideBackupUnitFile() {
		File[] logs = getAllLogFiles();

		// 日付指定なし && (ログファイル数 > バックアップ単位)
		if (StringUtils.isEmpty(this.datePattern) && logs.length > this.maxBackupUnit) {
			// ログファイルをインデックスで並び替え
			TreeMap<Integer, File> logTreeMap = new TreeMap<Integer, File>();
			for (File file : logs) {
				// 出力ファイルは処理対象外
				if (StringUtils.INDEX_NOT_FOUND == this.fileName.indexOf(file
						.getName())) {
					logTreeMap.put(this.getIndex(file.getName()), file);
				}
			}
			// 要削除ファイル数
			int overCount = logs.length - this.maxBackupUnit;
			// 削除済みファイル数
			int deletedCount = 0;
			for (Entry<Integer, File> logMap : logTreeMap.entrySet()) {
				if(! logMap.getValue().delete() ){
					LogLog.warn("Couldn't delete file " + logMap.getValue());
				}
				deletedCount++;
				// 要削除ファイル数 == 削除済みファイル数の場合は、削除処理終了
				if (overCount == deletedCount) {
					break;
				}
			}
		}
		// 日付指定ありの場合
		else if (StringUtils.isNotEmpty(this.datePattern)) {
			// バックアップ期限日を取得
			Date savePeriodLimitDate = this.calcBackupLimitDate();
			// バックアップ期限日以前に更新されたログファイルを全て削除
			for (File file : logs) {
				if (savePeriodLimitDate.getTime() > file.lastModified()) {
					if(file.exists()){
						if(! file.delete() ){
							LogLog.warn("Couldn't delete file " + file.getAbsolutePath());
						}
					}
				}
			}
		}
	}

	/**
	 * 全てのログファイルのリストを取得.
	 *
	 * @return 全てのログファイルのリスト
	 */
	private File[] getAllLogFiles() {
		File outputPath = new File(this.fileName);
		String logFileName = outputPath.getName();
		File logDir = outputPath.getParentFile();
		if (logDir == null) {
			logDir = new File(".");
		}
		return logDir.listFiles(new LogFileNameFilter(logFileName));
	}

	/**
	 * ファイルリスト内で最大のインデックス番号を返す.
	 *
	 * @param files インデックス番号付きファイルリスト
	 * @return 最大インデックス番号
	 */
	private int getMaxIndex(List<File> files) {
		// デフォルト値
		int maxIndex = 0;
		// 出力中ファイル情報取得
		File outputPath = new File(this.fileName);
		// 出力中ファイル名取得
		String logFileName = outputPath.getName();
		// 1つ1つのファイルのインデックス番号を確認
		for (File file : files) {
			// チェック対象ファイル名
			String targetFileName = file.getName();
			// 出力中のファイルはインデックスが付与されていないため、スキップ
			if (!targetFileName.equals(logFileName)) {
				// ファイルのインデックス番号取得
				String ext = targetFileName.substring(logFileName.length() + 1);
				// 取得したインデックスが数値以外の場合はスキップ
				if (NumberUtils.isDigits(ext)) {
					// 前回のインデックスより大きいインデックスの場合は、
					// インデックスを書き換える.
					if (Integer.parseInt(ext) > maxIndex) {
						maxIndex = Integer.parseInt(ext);
					}
				}
			}
		}
		return maxIndex;
	}

	/**
	 * 日付ローテーションされていないファイルのインデックス番号を取得.
	 *
	 * @return 日付ローテーションされていないファイル数
	 */
	private int getMaxIndexOfNotDailyLogFile() {
		// 全てのログファイルを取得
		File[] allLogs = getAllLogFiles();
		// 日付ローテーションされていないファイルのリストを取得
		List<File> notDailyLogs = new ArrayList<File>();
		for (File allLog : allLogs) {
			if (!isDailyRotatedLog(allLog)) {
				notDailyLogs.add(allLog);
			}
		}
		// 日付ローテーションされていないファイルの最大インデックス番号を取得
		return getMaxIndex(notDailyLogs);
	}

	/**
	 * ローテーションを実行する.
	 *
	 * @param mode (0:サイズ,1:日付)
	 */
	private void rotateLogFilesBy(int mode) {
		// synchronization not necessary since doAppend is alreasy synched

		// 日付ローテーションされていないファイル数を取得
		int maxIndexOfNotDailyLogFile = getMaxIndexOfNotDailyLogFile();

		// サイズローテーション
		if (mode == BY_SIZE) {
			boolean renameSucceeded = true;
			File file = null;
			File target = null;
			for (int i = maxIndexOfNotDailyLogFile; i >= 1 && renameSucceeded; i--) {
				file = new File(this.fileName + "." + i);
				if (file.exists()) {
					target = new File(this.fileName + '.' + (i + 1));
					LogLog.debug("Renaming file " + file + " to " + target);
					renameSucceeded = file.renameTo(target);
					if(!renameSucceeded){
						LogLog.warn("Couldn't rename file " + file + " to " + target);
					}
				}
			}

			if(renameSucceeded) {
				// ファイル名にインデックスを付与
				target = new File(this.fileName + ".1");
				this.closeFile();
				file = new File(this.fileName);
				LogLog.debug("Renaming file " + file + " to " + target);
				if(! file.renameTo(target) ){
					LogLog.warn("Couldn't rename file " + file + " to " + target);
				}
		    }
		}
		// 日付ローテーション
		else if (mode == BY_DATE) {
			this.closeFile();
			// サイズが指定されている場合
			if (this.maxFileSize > 0) {
				int i;
				for (i = 1; i <= maxIndexOfNotDailyLogFile - 1; i++) {
					// ファイル名に日付とインデックスを付与
					String from = this.fileName + '.' + i;
					String to = this.scheduledFileName + '.' + i;
					renameFile(from, to);
				}
				// 出力中のファイル名に日付とインデックスを付与する
				renameFile(this.fileName, this.scheduledFileName + '.' + i);
			}
			// サイズが指定されていない場合
			else {
				// 出力中のファイル名に日付を付与
				renameFile(this.fileName, this.scheduledFileName);
			}
		} else {
			LogLog.warn("invalid mode:" + mode);
		}
	}

	/**
	 * 対象ファイルが日付ローテーションされたファイルか否かを判断する.
	 *
	 * @param targetFile チェック対象ファイル
	 * @return true : 日付ローテーション実施済 false : 日付ローテーション未実施
	 */
	private boolean isDailyRotatedLog(File targetFile) {

		// チェック対象ファイル名
		String targetFileName = targetFile.getName();
		// 出力中のファイル名
		File outputPath = new File(this.fileName);
		String logFileName = outputPath.getName();

		// チェック対象ファイル名と出力ファイル名が同じ ⇒ 日付ローテーション未実施
		if (targetFileName.equals(logFileName)) {
			return false;
		}

		// チェック対象ファイル名から出力ファイル以降の文字列を取得
		// 数値以外の場合 ⇒ 日付ローテーション実施済
		// 数値の場合 ⇒ 日付ローテーション未実施
		String index = targetFileName.substring(logFileName.length() + 1);
		return !NumberUtils.isDigits(index);
	}

	/**
	 * ファイルをリネームする.
	 *
	 * @param from リネーム元ファイル名
	 * @param to リネーム先ファイル名
	 */
	private void renameFile(String from, String to) {
		File toFile = new File(to);
		// リネーム先ファイル名のファイルが既に存在する場合は、削除
		if (toFile.exists()) {
			if(! toFile.delete() ){
				LogLog.warn("Couldn't delete file " + toFile.getAbsolutePath());
			}
		}

		File fromFile = new File(from);
		if(! fromFile.renameTo(toFile) ){
			LogLog.warn("Couldn't rename file " + fromFile + " to " + toFile);
		}
	}

	/**
	 * 最大ログファイルサイズを返す.
	 *
	 * @return maxFileSize 最大ログファイルサイズ
	 */
	public long getMaximumFileSize() {
		return this.maxFileSize;
	}

	/**
	 * 最大ログファイルサイズを設定する.
	 *
	 * @param maxFileSize 最大ログファイルサイズ
	 */
	public void setMaximumFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	/**
	 * 最大ログファイルサイズを設定する.
	 *
	 * @param value 最大ログファイルサイズ
	 */
	public void setMaxFileSize(String value) {
		this.maxFileSize = OptionConverter.toFileSize(
				value, this.maxFileSize + 1);
	}

	/**
	 * quietライタを設定する.
	 *
	 * @param writer ライタ
	 */
	@Override
	protected void setQWForFiles(Writer writer) {
		this.qw = new CountingQuietWriter(writer, this.errorHandler);
	}

	/**
	 * 日付パターンを設定する.
	 *
	 * @param pattern 日付パターン
	 */
	public void setDatePattern(String pattern) {
		this.datePattern = pattern;
	}

	/**
	 * 日付パターンを返す.
	 *
	 * @return datePattern 日付パターン
	 */
	public String getDatePattern() {
		return this.datePattern;
	}

	/**
	 * バックアップ単位を設定する.
	 *
	 * @param maxBackupUnit バックアップ単位
	 */
	public void setMaxBackupUnit(int maxBackupUnit) {
		this.maxBackupUnit = maxBackupUnit;
	}

	/**
	 * バックアップ単位を返す.
	 *
	 * @return maxBackupUnit バックアップ単位
	 */
	public int getMaxBackupUnit() {
		return this.maxBackupUnit;
	}

	/**
	 * ファイル名からインデックス番号を取得
	 *
	 * @param logFileNameWithIndex インデックス番号付きファイル名
	 * @return インデックス番号
	 */
	private Integer getIndex(String logFileNameWithIndex) {

		File outputPath = new File(this.fileName);
		String logFileName = outputPath.getName();
		String ext = logFileNameWithIndex.substring(logFileName.length() + 1);
		return Integer.valueOf(ext);

	}

	/**
	 * バックアップ期限日を算出する.
	 *
	 * @return バックアップ期限日
	 */
	private Date calcBackupLimitDate() {
		// 現在日付設定
		Calendar cal = new GregorianCalendar();
		cal.setTime(this.now);
		// 秒フィールドをリセット
		cal.set(Calendar.SECOND, 0);
		// ミリ秒フィールドのリセット
		cal.set(Calendar.MILLISECOND, 0);
		// 日付パターン算出
		switch (computeCheckPeriod()) {
		// 分単位
		case TOP_OF_MINUTE:
			cal.add(Calendar.MINUTE, - this.maxBackupUnit);
			break;
		// 時間単位
		case TOP_OF_HOUR:
			cal.add(Calendar.HOUR_OF_DAY, - this.maxBackupUnit);
			cal.set(Calendar.MINUTE, 0);
			break;
		// 半日単位
		case HALF_DAY:
			cal.add(Calendar.HOUR_OF_DAY, - (this.maxBackupUnit * 12));
			cal.set(Calendar.MINUTE, 0);
			break;
		// 日単位
		case TOP_OF_DAY:
			cal.add(Calendar.DAY_OF_MONTH, - this.maxBackupUnit);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			break;
		// 週単位
		case TOP_OF_WEEK:
			cal.add(Calendar.DAY_OF_MONTH, - (this.maxBackupUnit * 7));
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			break;
		// 月単位
		case TOP_OF_MONTH:
			cal.add(Calendar.MONTH, - this.maxBackupUnit);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			break;
		// その他
		default:
			throw new IllegalStateException("Unknown periodicity type.");
		}
		return cal.getTime();
	}

	/**
	 * パラメータチェック処理.<br>
	 * <br>
	 *
	 * 以下、エラーログ出力パターン.<br>
	 * ファイルサイズが負数の場合<br>
	 * バックアップ単位が負数の場合<br>
	 * ファイルサイズと日付パターンの両方が指定されていない場合<br>
	 */
	private void checkParams() {

		// ファイルサイズが負数
		if (this.maxFileSize < 0) {
			throw new IllegalArgumentException(
					"maxFileSize is invalid. [" + this.maxFileSize + "]");
		}

		// バックアップ単位が負数
		if (this.maxBackupUnit < 0) {
			throw new IllegalArgumentException(
					"backupUnit is invalid. [" + this.maxBackupUnit + "]");
		}

		// ファイルサイズと日付パターンの両方が指定されていない
		if (this.maxFileSize == 0 && StringUtils.isEmpty(this.datePattern)) {
			this.errorHandler.error(
				"either of maxFileSize or datePattern is recommended to be set.");
		}

	}



	/**
	 * org.apache.log4.RollingCalendarのコピークラス.
	 */
	private static class DateRollingCalendar extends GregorianCalendar {

		/** serialVersionUID */
		private static final long serialVersionUID = -2703999806806403682L;

		/** デフォルト日付パターン指定タイプ */
		int type = TOP_OF_TROUBLE;

		/**
		 * コンストラクタ
		 */
		public DateRollingCalendar() {
			super();
		}

		/**
		 * コンストラクタ
		 *
		 * @param tz タイムゾーン
		 * @param locale 地域
		 */
		public DateRollingCalendar(TimeZone tz, Locale locale) {
			super(tz, locale);
		}

		/* (non-Javadoc)
		 * @see java.util.GregorianCalendar#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			return obj instanceof DateRollingCalendar &&
			super.equals(obj) && this.type == ((DateRollingCalendar)obj).type;
		}

		/* (non-Javadoc)
		 * @see java.util.GregorianCalendar#hashCode()
		 */
		@Override
		public int hashCode() {
			return super.hashCode() ^ this.type;
		}

		/**
		 * 日付パターン指定タイプを設定する.
		 *
		 * @param type 日付パターン指定タイプ
		 */
		void setType(int type) {
			this.type = type;
		}

		/**
		 * 次回の日付ローテートを実施する日付をLong型で返す.
		 *
		 * @param now 基準日付
		 * @return 次回ローテート実施日(Long型)
		 */
		public long getNextCheckMillis(Date now) {
			return getNextCheckDate(now).getTime();
		}

		/**
		 * 次回の日付ローテートを実施する日付を返す.
		 *
		 * @param now 基準日付
		 * @return 次回ローテート実施日
		 */
		public Date getNextCheckDate(Date now) {
			// 現在日付設定
			this.setTime(now);
			// 日付パターン指定タイプに応じた処理
			switch (type) {
			// 分ローテート
			case TOP_OF_MINUTE:
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.MINUTE, 1);
				break;
			// 時間ローテート
			case TOP_OF_HOUR:
				this.set(Calendar.MINUTE, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.HOUR_OF_DAY, 1);
				break;
			// 半日ローテート
			case HALF_DAY:
				this.set(Calendar.MINUTE, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				int hour = get(Calendar.HOUR_OF_DAY);
				if (hour < 12) {
					this.set(Calendar.HOUR_OF_DAY, 12);
				} else {
					this.set(Calendar.HOUR_OF_DAY, 0);
					this.add(Calendar.DAY_OF_MONTH, 1);
				}
				break;
			// 日ローテート
			case TOP_OF_DAY:
				this.set(Calendar.HOUR_OF_DAY, 0);
				this.set(Calendar.MINUTE, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.DATE, 1);
				break;
			// 週ローテート
			case TOP_OF_WEEK:
				this.set(Calendar.DAY_OF_WEEK, getFirstDayOfWeek());
				this.set(Calendar.HOUR_OF_DAY, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.WEEK_OF_YEAR, 1);
				break;
			// 月ローテート
			case TOP_OF_MONTH:
				this.set(Calendar.DATE, 1);
				this.set(Calendar.HOUR_OF_DAY, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.MONTH, 1);
				break;
			// その他
			default:
				throw new IllegalStateException("Unknown periodicity type.");
			}
			return getTime();
		}
	}



	/**
	 * ログファイル名のフィルタークラス.
	 *
	 */
	private static class LogFileNameFilter implements FilenameFilter {
		/** ログファイル名 */
		private final String logFileName;

		/**
		 * コンストラクタ
		 * @param name ログファイル名
		 */
		public LogFileNameFilter(String name) {
			this.logFileName = name;
		}

		/* (non-Javadoc)
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		@Override
		public boolean accept(File dir, String name) {
			// 指定されたファイルがファイルリストに含まれるかどうかをチェックする.
			return name.startsWith(this.logFileName);
		}
	}
}
