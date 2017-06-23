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
package com.ae.apps.common.services;

import java.io.InputStream;
import java.util.List;

import android.graphics.Bitmap;

import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.common.vo.MessageVo;
import com.ae.apps.common.vo.PhoneNumberVo;

/**
 * Specification for a DAO that accesses the Android Contacts API
 */
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

    /**
     * Get contactId corresponding to an "address"
     *
     * @param address address to find the contact id for
     * @return contactid
     */
    String getContactIdFromAddress(final String address);

    /**
     * Opens a contact photo?
     *
     * See if you could use @LINK{getContactPhoto()} instead
     *
     * @param contactId contactId
     * @return contact photo
     */
    @Deprecated
    InputStream openPhoto(final long contactId);
}
