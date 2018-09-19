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

package com.ae.apps.lib.mock;

import android.graphics.Bitmap;

import com.ae.apps.lib.contacts.service.AeContactService;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.common.vo.MessageVo;
import com.ae.apps.common.vo.PhoneNumberVo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock ContactService
 */
public class MockContactService implements AeContactService {

    /**
     * Number of contacts served by this implementation
     */
    public final static int MOCK_CONTACTS_SIZE = 5;

    private List<ContactVo> list;

    public MockContactService() {
        list = new ArrayList<>();
        for (int i = 0; i < MOCK_CONTACTS_SIZE; i++) {
            list.add(MockContactDataUtils.getMockContact());
        }
    }

    @Override
    public List<ContactVo> getContacts(boolean addContactsWithPhoneNumbers) {
        return list;
    }

    @Override
    public Bitmap getContactPhoto(String contactId) {
        return null;
    }

    @Override
    public List<PhoneNumberVo> getContactPhoneDetails(String contactId) {
        return list.get(0).getPhoneNumbersList();
    }

    @Override
    public List<MessageVo> getContactMessages(String contactId) {
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

    @Override
    public InputStream openPhoto(long contactId) {
        return null;
    }
}
