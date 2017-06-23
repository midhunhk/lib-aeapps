package com.ae.apps.common.services;

import java.util.List;

import android.graphics.Bitmap;

import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.common.vo.MessageVo;
import com.ae.apps.common.vo.PhoneNumberVo;

public interface AeContactService {
	
	/**
	 * Read all contacts from the phone's contact database
	 * 
	 * @param addContactsWithPhoneNumbers
	 * @return list of contacts
	 */
	List<ContactVo> getContacts(boolean addContactsWithPhoneNumbers);
	
	/**
	 * Returns the contact photo
	 * 
	 * @param contactId
	 * @return contact photo if it exists
	 */
	Bitmap getContactPhoto(final String contactId);

	/**
	 * Returns the list of phone numbers for this contact if it exists
	 * 
	 * @param contactId
	 * @return list of phone numbers for this contact
	 */
	List<PhoneNumberVo> getContactPhoneDetails(String contactId);
	
	/**
	 * Returns list of messages for this contact
	 * 
	 * @param contactId
	 * @return list of messages
	 */
	List<MessageVo> getContactMessages(String contactId);
	
	/**
	 * Returns the contactId when given the rawContactId which is used in the master table 
	 * 
	 * @param rawContactId
	 * @return contactId
	 */
	long getContactIdFromRawContactId(final String rawContactId);
}
