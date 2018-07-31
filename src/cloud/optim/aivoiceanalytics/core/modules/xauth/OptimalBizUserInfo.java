package cloud.optim.aivoiceanalytics.core.modules.xauth;

public class OptimalBizUserInfo implements java.io.Serializable {

	/** OptimalBizユーザーGUID */
	private String userGuid;

	/** OptimalBizユーザー名称 */
	private String userName;

	/**
	 * @return userGuid
	 */
	public String getUserGuid() {
		return userGuid;
	}
	/**
	 * @param userGuid セットする userGuid
	 */
	public void setUserGuid(String userGuid) {
		this.userGuid = userGuid;
	}
	/**
	 * @return userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName セットする userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

}
