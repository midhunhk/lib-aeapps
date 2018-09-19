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

package com.ae.apps.lib.contacts;

import android.graphics.Bitmap;

import com.ae.apps.lib.common.models.ContactInfo;
import com.ae.apps.lib.common.models.MessageInfo;

import java.util.List;

/**
 * A specification for a ContactManager used in AE App lab projects
 *
 * <pre>
 *     Call @link{fetchAllContacts} or @link{fetchAllContactsAsync} to initialize the contacts
 * </pre>
 */
public interface AeContactManager {

    /**
     * Invoke to fetch all contacts and use them right away
     */
    void fetchAllContacts();

    /**
     * Invoke to fetch contacts in a separate thread. Use if lazily creating an instance
     * of @link{{@link AeContactManager}}
     *
     * @param consumer consumer to be notified when the contacts have been read
     */
    void fetchAllContactsAsync(final ContactDataConsumer consumer);

    /**
     * Invoke to fetch all contacts once read from are read. Returns a copy of the contacts list.
     * Note that this is an expensive operation as a new list is created with original
     * values and returned to the caller.
     *
     * @return contacts list
     * @throws UnsupportedOperationException {@inheritDoc} unless implemented
     */
    List<ContactInfo> getAllContacts() throws UnsupportedOperationException;

    /**
     * Gets the total contacts read
     *
     * @return number of contacts
     */
    int getTotalContactCount();

    /**
     * Returns a random contactVo from the list if the list is non empty, null otherwise
     *
     * @return a ContactVo object
     */
    ContactInfo getRandomContact();

    /**
     * Returns a contact with phone details
     *
     * @param contactId contact id
     * @return contact vo
     */
    ContactInfo getContactWithPhoneDetails(final String contactId);

    /**
     * Returns a Bitmap object for the contact's photo, returning a default image
     *
     * @param contactId    contact id
     * @param defaultImage default image
     * @return contact photo
     */
    Bitmap getContactPhoto(final String contactId, final Bitmap defaultImage);

    /**
     * Returns the contact info
     *
     * @param contactId contact id
     * @return contact vo
     */
    ContactInfo getContactInfo(final String contactId);

    /**
     * Returns the list of messages for this contact with id
     *
     * @param contactId contact id
     * @return list of message vo s
     */
    List<MessageInfo> getContactMessages(final String contactId);

    /**
     * Returns the contactId based on the rawContactId
     *
     * @param rawContactId raw contact id
     * @return contact id
     */
    long getContactIdFromRawContactId(final String rawContactId);

    /**
     * Does a lookup on the contacts by phone number lookup
     *
     * @param address address
     * @return contact id
     */
    String getContactIdFromAddress(final String address);

    /**
     * Returns a Bitmap object for the contact's photo
     *
     * @param contactId contact id
     * @return contact photo
     */
    Bitmap getContactPhoto(final String contactId);

}
