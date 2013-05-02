package com.ae.apps.common.vo;

import java.util.ArrayList;

public class ContactVo {
	private String id;
	private String name;
	private String timesContacted;
	private boolean hasPhoneNumber;
	private String lastContactedTime;
	
	private ArrayList<PhoneNumberVo> phoneNumbersList;
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param timesContacted the timesContacted to set
	 */
	public void setTimesContacted(String timesContacted) {
		this.timesContacted = timesContacted;
	}
	/**
	 * @return the timesContacted
	 */
	public String getTimesContacted() {
		return timesContacted;
	}
	/**
	 * @param hasPhoneNumber the hasPhoneNumber to set
	 */
	public void setHasPhoneNumber(boolean hasPhoneNumber) {
		this.hasPhoneNumber = hasPhoneNumber;
	}
	/**
	 * @return the hasPhoneNumber
	 */
	public boolean getHasPhoneNumber() {
		return hasPhoneNumber;
	}
	/**
	 * @param lastContactedTime the lastContactedTime to set
	 */
	public void setLastContactedTime(String lastContactedTime) {
		this.lastContactedTime = lastContactedTime;
	}
	/**
	 * @return the lastContactedTime
	 */
	public String getLastContactedTime() {
		return lastContactedTime;
	}
	/**
	 * @param phoneNumbersList the phoneNumbersList to set
	 */
	public void setPhoneNumbersList(ArrayList<PhoneNumberVo> phoneNumbersList) {
		this.phoneNumbersList = phoneNumbersList;
	}
	/**
	 * @return the phoneNumbersList
	 */
	public ArrayList<PhoneNumberVo> getPhoneNumbersList() {
		return phoneNumbersList;
	}
}
