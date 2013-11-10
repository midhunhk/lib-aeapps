package com.ae.apps.common.vo;

public class PhoneNumberVo {
	private String phoneType;
	private String phoneNumber;
	private String lastTimeContacted;

	/**
	 * @param phoneNumber
	 *            the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param lastTimeContacted
	 *            the lastTimeContacted to set
	 */
	public void setLastTimeContacted(String lastTimeContacted) {
		this.lastTimeContacted = lastTimeContacted;
	}

	/**
	 * @return the lastTimeContacted
	 */
	public String getLastTimeContacted() {
		return lastTimeContacted;
	}

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}
}
