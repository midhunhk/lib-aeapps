/*
 * Copyright (c) 2018 Midhun Harikumar
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
 *
 */

package com.ae.apps.lib.api.contacts;

import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions;
import com.ae.apps.lib.api.contacts.types.ContactInfoOptions;
import com.ae.apps.lib.api.contacts.types.ContactsDataConsumer;
import com.ae.apps.lib.common.models.ContactInfo;

import java.util.List;

/**
 * An Abstraction over the Android Contacts API
 * <p>
 * Requires the below permission in the manifest
 * <p>android.permission.READ_CONTACTS</p>
 * <p>
 * Usage: First call one of the initialize methods and after the contacts are read,
 * use any other method to perform an operation on the
 *
 * @since 4.0
 */
public interface ContactsApiGateway {

    /**
     * Initialize the ContactsApi to read all contacts based on the options provided
     *
     * @param options filter options
     */
    void initialize(ContactInfoFilterOptions options);

    /**
     * Initialize the ContactApi to read all contacts based on the options provided
     * in a separate thread. The data consumer would  be notified when the
     * operation is complete
     *
     * @param options      filter options
     * @param dataConsumer data consumer
     */
    void initializeAsync(ContactInfoFilterOptions options, ContactsDataConsumer dataConsumer);

    /**
     * Return the list of contacts that were read by the API based on the Options used with the initialize method.
     * <p>
     * Make sure one of the below methods before invoking this method
     * com.ae.apps.lib.api.contacts.ContactsApiGateway#initialize(com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions)
     * com.ae.apps.lib.api.contacts.ContactsApiGateway#initializeAsync(com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions, com.ae.apps.lib.api.contacts.types.ContactsDataConsumer)
     *
     * @return list of read contacts
     * @throws IllegalStateException if the API is not yet initialized
     */
    List<ContactInfo> getAllContacts() throws IllegalStateException;

    /**
     * Get the count of read contacts
     *
     * @return the number of contacts that were read
     */
    long getReadContactsCount();

    /**
     * Return a Random contact from the contacts that have been read by the API
     *
     * @return a random contact from the contacts that are read by the API, null if there are no contacts
     * @throws IllegalStateException if the API is not yet initialized
     */
    ContactInfo getRandomContact() throws IllegalStateException;

    /**
     * Returns a contact based on the contactId
     *
     * @param contactId the contactId
     * @return a Contact
     */
    ContactInfo getContactInfo(final String contactId);

    /**
     * Returns a contact based on the contactId and options
     *
     * @param contactId the contactId
     * @param options   the options
     * @return a Contact
     */
    ContactInfo getContactInfo(final String contactId, final ContactInfoOptions options);

    /**
     * Convert a rawContactId into the corresponding contactId
     *
     * @param rawContactId raw contact id
     * @return contact id
     */
    String getContactIdFromRawContact(final String rawContactId);

    /**
     * Convert an address into corresponding contactId
     *
     * @param address address
     * @return contact id
     */
    String getContactIdFromAddress(final String address);

}
