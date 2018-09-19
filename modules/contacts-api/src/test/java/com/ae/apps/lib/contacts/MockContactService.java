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

package com.ae.apps.lib.contacts;

import android.graphics.Bitmap;

import com.ae.apps.lib.common.models.ContactInfo;
import com.ae.apps.lib.common.models.MessageInfo;
import com.ae.apps.lib.common.models.PhoneNumberInfo;
import com.ae.apps.lib.contacts.service.AeContactService;
import com.ae.apps.lib.mock.MockContactDataUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock ContactService
 */
public class MockContactService implements AeContactService {

    /**
     * Number of contacts served by this implementation
     */
    private final static int MOCK_CONTACTS_SIZE = 5;

    private List<ContactInfo> list;

    public MockContactService() {
        list = new ArrayList<>();
        for (int i = 0; i < MOCK_CONTACTS_SIZE; i++) {
            list.add(MockContactDataUtils.getMockContact());
        }
    }

    @Override
    public List<ContactInfo> getContacts(boolean addContactsWithPhoneNumbers) {
        return list;
    }

    @Override
    public Bitmap getContactPhoto(String contactId) {
        return null;
    }

    @Override
    public List<PhoneNumberInfo> getContactPhoneDetails(String contactId) {
        return list.get(0).getPhoneNumbersList();
    }

    @Override
    public List<MessageInfo> getContactMessages(String contactId) {
        return null;
    }

    @Override
    public long getContactIdFromRawContactId(String rawContactId) {
        return 0;
    }

    @Override
    public String getContactIdFromAddress(String address) {
        return null;
    }

}
