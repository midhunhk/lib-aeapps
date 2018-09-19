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

import android.graphics.Bitmap;

import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.common.vo.MessageVo;
import com.ae.apps.lib.contacts.AeContactManager;
import com.ae.apps.lib.contacts.ContactDataConsumer;

import java.util.List;

/**
 * An implementation of ContactManager that proxies all calls to an underling implementation
 * of ContactManager.
 *
 * Note: mContactManager must be initialized before using any of its methods
 */
public class ContactManagerProxy implements AeContactManager {

    protected AeContactManager mContactManager;

    @Override
    public void fetchAllContacts() {
        mContactManager.fetchAllContacts();
    }

    @Override
    public void fetchAllContactsAsync(ContactDataConsumer consumer) {
        mContactManager.fetchAllContactsAsync(consumer);
    }

    @Override
    public List<ContactVo> getAllContacts() throws UnsupportedOperationException {
        return mContactManager.getAllContacts();
    }

    @Override
    public int getTotalContactCount() {
        return mContactManager.getTotalContactCount();
    }

    @Override
    public ContactVo getRandomContact() {
        return mContactManager.getRandomContact();
    }

    @Override
    public ContactVo getContactWithPhoneDetails(String contactId) {
        return mContactManager.getContactWithPhoneDetails(contactId);
    }

    @Override
    public Bitmap getContactPhoto(String contactId, Bitmap defaultImage) {
        return mContactManager.getContactPhoto(contactId, defaultImage);
    }

    @Override
    public ContactVo getContactInfo(String contactId) {
        return mContactManager.getContactInfo(contactId);
    }

    @Override
    public List<MessageVo> getContactMessages(String contactId) {
        return mContactManager.getContactMessages(contactId);
    }

    @Override
    public long getContactIdFromRawContactId(String rawContactId) {
        return mContactManager.getContactIdFromRawContactId(rawContactId);
    }

    @Override
    public String getContactIdFromAddress(String address) {
        return mContactManager.getContactIdFromAddress(address);
    }

    @Override
    public Bitmap getContactPhoto(String contactId) {
        return mContactManager.getContactPhoto(contactId);
    }
}
