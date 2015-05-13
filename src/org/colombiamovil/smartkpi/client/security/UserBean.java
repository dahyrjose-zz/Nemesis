package org.colombiamovil.smartkpi.client.security;

import java.io.Serializable;

public class UserBean implements Serializable {

	private static final long serialVersionUID = -3444887125217275090L;
	private String sessionId, userUid, userLogin, userMsisdn, userIdNumber, curSessionStartTime;

	public UserBean() {
	}

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public void setUserMsisdn(String userMsisdn) {
		this.userMsisdn = userMsisdn;
	}

	public String getUserMsisdn() {
		return userMsisdn;
	}

	public String getUserIdNumber() {
		return userIdNumber;
	}

	public void setUserIdNumber(String userIdNumber) {
		this.userIdNumber = userIdNumber;
	}

	public String getCurSessionStartTime() {
		return curSessionStartTime;
	}

	public void setCurSessionStartTime(String curSessionStartTime) {
		this.curSessionStartTime = curSessionStartTime;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	@Override
	public String toString() {
		return "uid:"+userUid+"!login:"+userLogin+"!msisdn:"+userMsisdn+"!id:"+userIdNumber+"!sessStart:"+curSessionStartTime;
	}

	/**
	 * Method to build the user bean from the session id string.
	 * 
	 * @param sessionData The session id in the format: "uid:userUid;login:userLogin;id:userIdNumber;sessStart:curSessionStartTime"
	 * */
	public void parseUserBean(String sessionData) {
		String[] sess = sessionData.split("!");
		setUserUid(sess[0].substring(4));
		setUserLogin(sess[1].substring(6));
		setUserMsisdn(sess[2].substring(7));
		setUserIdNumber(sess[3].substring(3));
		setCurSessionStartTime(sess[4].substring(10));
	}
}
