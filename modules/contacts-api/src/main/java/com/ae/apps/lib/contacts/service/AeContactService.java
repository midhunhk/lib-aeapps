/*
 * Copyright 2017 Midhun Harikumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ae.apps.lib.contacts.service;

import android.graphics.Bitmap;

import com.ae.apps.lib.common.models.ContactInfo;
import com.ae.apps.lib.common.models.MessageInfo;
import com.ae.apps.lib.common.models.PhoneNumberInfo;

import java.util.List;

/**
 * Specification for a DAO that accesses the Android Contacts API
 */
public interface AeContactService {
	
	/**
	 * Read all contacts from the phone's contact database
	 * 
	 * @param addContactsWithPhoneNumbers flag
	 * @return list of contacts
	 */
	List<ContactInfo> getContacts(boolean addContactsWithPhoneNumbers);
	
	/**
	 * Returns the contact photo
	 * 
	 * @param contactId contactId
	 * @return contact photo if it exists
	 */
	Bitmap getContactPhoto(final String contactId);

	/**
	 * Returns the list of phone numbers for this contact if it exists
	 * 
	 * @param contactId contactId
	 * @return list of phone numbers for this contact
	 */
	List<PhoneNumberInfo> getContactPhoneDetails(String contactId);
	
	/**
	 * Returns list of messages for this contact
	 * 
	 * @param contactId contactId
	 * @return list of messages
	 */
	List<MessageInfo> getContactMessages(String contactId);
	
	/**
	 * Returns the contactId when given the rawContactId which is used in the master table 
	 * 
	 * @param rawContactId contactId
	 * @return contactId
	 */
	long getContactIdFromRawContactId(final String rawContactId);

    /**
     * Get contactId corresponding to an "address"
     *
     * @param address address to find the contact id for
     * @return contactId
     */
    String getContactIdFromAddress(final String address);

}
