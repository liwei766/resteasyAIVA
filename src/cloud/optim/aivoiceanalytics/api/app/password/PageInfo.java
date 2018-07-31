package cloud.optim.aivoiceanalytics.api.app.password;

public class PageInfo {

	/**  ページNo */
	private Long pageNo;
	/**  ページ表示件数 */
	private Long pageSize;
	/**  総件数 */
	private Long totalNumber;
	/**  総ページ数 */
	private Long totalPage;
	/**  取得開始件数 */
	private Long offset;

	/**
	 * @return pageNo
	 */
	public Long getPageNo() {
		return pageNo;
	}
	/**
	 * @param pageNo セットする pageNo
	 */
	public void setPageNo(Long pageNo) {
		this.pageNo = pageNo;
	}
	/**
	 * @return pageSize
	 */
	public Long getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize セットする pageSize
	 */
	public void setPageSize(Long pageSize) {
		this.pageSize = pageSize;
	}
	/**
	 * @return totalNumber
	 */
	public Long getTotalNumber() {
		return totalNumber;
	}
	/**
	 * @param totalNumber セットする totalNumber
	 */
	public void setTotalNumber(Long totalNumber) {
		this.totalNumber = totalNumber;
	}
	/**
	 * @return totalPage
	 */
	public Long getTotalPage() {
		return totalPage;
	}
	/**
	 * @param totalPage セットする totalPage
	 */
	public void setTotalPage(Long totalPage) {
		this.totalPage = totalPage;
	}
	/**
	 * @return offset
	 */
	public Long getOffset() {
		return offset;
	}
	/**
	 * @param offset セットする offset
	 */
	public void setOffset(Long offset) {
		this.offset = offset;
	}

}
