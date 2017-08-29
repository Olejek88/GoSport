package ru.shtrm.gosport;

public class AuthorizedUser {
	
	private String mUuid;
	private String mToken;
	private static AuthorizedUser mInstance;
	
	public static synchronized AuthorizedUser getInstance() {
		if (mInstance == null) {
			mInstance = new AuthorizedUser();
		}
		return mInstance;
	}

	/**
	 * @return the mUuid
	 */
	public String getUuid() {
		return mUuid;
	}

	/**
	 * @param Uuid the mUuid to set
	 */
	public void setUuid(String Uuid) {
		this.mUuid = Uuid;
	}

	/**
	 * @return the mToken
	 */
	public String getToken() {
		return mToken;
	}

	/**
	 * @param Token the mToken to set
	 */
	public void setToken(String Token) {
		this.mToken = Token;
	}

	/**
	 * @return The bearer
	 */
	public String getBearer() {
		return "bearer " + mToken;
	}
}
