package com.ae.apps.common.vo;

import java.util.ArrayList;

/**
 * Represents a contact entity
 * 
 * @author Midhun
 * 
 */
public class ContactVo implements Comparable<ContactVo> {

	private String						id;
	private String						name;
	private String						timesContacted;
	private String						lastContactedTime;
	private boolean						hasPhoneNumber;
	private ArrayList<PhoneNumberVo>	phoneNumbersList;
	
	private boolean mockUser;
	private int mockProfileImageResource;

	/**
	 * @param id
	 *            the id to set
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
	 * @param name
	 *            the name to set
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
	 * @param timesContacted
	 *            the timesContacted to set
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
	 * @param hasPhoneNumber
	 *            the hasPhoneNumber to set
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
	 * @param lastContactedTime
	 *            the lastContactedTime to set
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
	 * @param phoneNumbersList
	 *            the phoneNumbersList to set
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

	@Override
	public int compareTo(ContactVo another) {
		if (this.id != null || another == null || another.id == null) {
			return 0;
		}
		return this.id.compareTo(another.id);
	}

	@Override
	public boolean equals(Object o) {
		ContactVo other = (ContactVo) o;
		return this.getId().equals(other.getId());
	}

	public boolean isMockUser() {
		return mockUser;
	}

	public void setMockUser(boolean mockUser) {
		this.mockUser = mockUser;
	}

	public int getMockProfileImageResource() {
		return mockProfileImageResource;
	}

	public void setMockProfileImageResource(int mockProfileImageResource) {
		this.mockProfileImageResource = mockProfileImageResource;
	}
}
