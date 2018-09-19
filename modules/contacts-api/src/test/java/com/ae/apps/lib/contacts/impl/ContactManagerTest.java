/*
 * Copyright (c) 2015 Midhun Harikumar
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

package com.ae.apps.lib.contacts.impl;

import android.content.ContentResolver;
import android.content.res.Resources;

import com.ae.apps.lib.contacts.ContactDataConsumer;
import com.ae.apps.lib.contacts.MockContactService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for ContactManager
 */
public class ContactManagerTest {

    @Mock
    private Resources resources;

    @Mock
    private ContentResolver contentResolver;

    private ContactManager contactManager;

    @Before
    public void setUp() {
        System.out.println("setUp is called before each test");
        contactManager = new ContactManager.Builder(contentResolver, resources)
                .addContactsWithPhoneNumbers(true)
                .build();
        contactManager.mContactService = new MockContactService();
    }

    @Test
    public void testGetAllContacts() {
        contactManager.fetchAllContacts();
        int size = null != contactManager.getAllContacts() ? contactManager.getAllContacts().size() : 0;
        assertEquals(MockContactService.MOCK_CONTACTS_SIZE, size);
    }

    @Test
    public void testGetAllContactsAsync() {

        contactManager.fetchAllContactsAsync(new ContactDataConsumer() {
            @Override
            public void onContactsRead() {
                System.out.println("onContactsRead() callback");
                assertEquals(AbstractContactManager.STATUS.READY, contactManager.contactManagerStatus);
                assertEquals(MockContactService.MOCK_CONTACTS_SIZE, contactManager.getAllContacts().size());
            }
        });

    }

    @Test
    public void testGetContactPhoneDetails() {
        contactManager.fetchAllContacts();

        String contactId = null != contactManager.getAllContacts() ? contactManager.getAllContacts().get(0).getId() : "";
        assertNotNull(contactManager.getContactWithPhoneDetails(contactId).getPhoneNumbersList());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupportedOperationException() {
        if (null != contactManager.getAllContacts()) {
            contactManager.getAllContacts().get(0).getId();
        }
    }

}
