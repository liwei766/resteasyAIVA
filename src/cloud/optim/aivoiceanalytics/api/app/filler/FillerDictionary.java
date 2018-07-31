package cloud.optim.aivoiceanalytics.api.app.filler;

public class FillerDictionary {
	private static final String FILLER = "フィラー";

	/** 表記 */
	private String surface;

	/** セグメント値 */
	private String segmentationValue;

	/** 読み */
	private String readingsValue;

	/** 品詞 */
	private String partOfSpeech;

	/**
	 * コンストラクタ
	 * @param surface 表記
	 */
	public FillerDictionary(String surface) {
		this.surface = surface;
		this.segmentationValue = surface;
		this.readingsValue = null;
		this.partOfSpeech = FILLER;
	}

	public FillerDictionary() {
	}

	public String getSegmentationValue() {
		return segmentationValue;
	}
	/**
	 * @return surface
	 */
	public String getSurface() {
		return surface;
	}
	/**
	 * @param surface セットする surface
	 */
	public void setSurface(String surface) {
		this.surface = surface;
	}
	/**
	 * @return partOfSpeech
	 */
	public String getPartOfSpeech() {
		return partOfSpeech;
	}
	/**
	 * @param partOfSpeech セットする partOfSpeech
	 */
	public void setPartOfSpeech(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}

	public void setSegmentationValue(String segmentationValue) {
		this.segmentationValue = segmentationValue;
	}

	public String getReadingsValue() {
		return readingsValue;
	}

	public void setReadingsValue(String readingsValue) {
		this.readingsValue = readingsValue;
	}
}
