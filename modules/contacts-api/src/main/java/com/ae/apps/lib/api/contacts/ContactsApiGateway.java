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
 *
 * Requires the below permission in the manifest
 * <p>android.permission.READ_CONTACTS</p>
 *
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
     * @param options filter options
     * @param dataConsumer data consumer
     */
    void initializeAsync(ContactInfoFilterOptions options, ContactsDataConsumer dataConsumer);

    /**
     * Return a list of
     *
     * @return list of read contacts
     * @throws IllegalStateException if the API is not yet initialized
     */
    List<ContactInfo> getAllContacts() throws IllegalStateException;

    /**
     *
     * @return
     */
    long getReadContactsCount();

    ContactInfo getRandomContact();

    ContactInfo getContactInfo(final String contactId);

    ContactInfo getContactInfo(final String contactId, final ContactInfoOptions options);

    String getContactIdFromRawContact(final String rawContactId);

    String getContactIdFromAddress(final String address);

}
