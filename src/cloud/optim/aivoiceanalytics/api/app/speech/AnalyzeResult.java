package cloud.optim.aivoiceanalytics.api.app.speech;

public class AnalyzeResult {

	/** 解析結果種別 */
	private String type;

	/** 解析結果文字列 */
	private String str;

	/** 音声開始秒数 */
	private Integer time;

	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type セットする type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return str
	 */
	public String getStr() {
		return str;
	}

	/**
	 * @param str セットする str
	 */
	public void setStr(String str) {
		this.str = str;
	}

	/**
	 * @return time
	 */
	public Integer getTime() {
		return time;
	}

	/**
	 * @param time セットする time
	 */
	public void setTime(Integer time) {
		this.time = time;
	}
}
