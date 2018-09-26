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
import android.graphics.Bitmap;
import androidx.annotation.Nullable;

import com.ae.apps.lib.common.models.ContactInfo;
import com.ae.apps.lib.common.models.MessageInfo;
import com.ae.apps.lib.common.models.PhoneNumberInfo;
import com.ae.apps.lib.contacts.AeContactManager;
import com.ae.apps.lib.contacts.ContactDataConsumer;
import com.ae.apps.lib.contacts.service.AeContactService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * An abstract implementation of common methods for AeContactManager
 */
public abstract class AbstractContactManager implements AeContactManager {

    protected enum STATUS {
        UNINITIALIZED, INITIALIZING, READY
    }

    protected STATUS contactManagerStatus = STATUS.UNINITIALIZED;

    protected AeContactService mContactService;
    protected Resources resources;
    protected ContentResolver contentResolver;

    protected List<ContactInfo> contactsList;

    // Config Item
    protected boolean addContactsWithPhoneNumbers;

    @Override
    public void fetchAllContacts() {
        // The getContacts method is not called once the data is READY
        if (STATUS.UNINITIALIZED == contactManagerStatus) {
            contactManagerStatus = STATUS.INITIALIZING;

            // Read the contacts data from the device
            contactsList = mContactService.getContacts(addContactsWithPhoneNumbers);

            contactManagerStatus = STATUS.READY;
        }
    }

    @Override
    public void fetchAllContactsAsync(final ContactDataConsumer consumer) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                fetchAllContacts();

                // Inform the consumer that the data is ready
                if (null != consumer) {
                    consumer.onContactsRead();
                }
            }
        }).start();
    }

    @Nullable
    @Override
    public List<ContactInfo> getAllContacts() throws UnsupportedOperationException {
        if (STATUS.READY == contactManagerStatus) {
            if (null != contactsList) {
                return new ArrayList<>(contactsList);
            } else {
                return Collections.emptyList();
            }
        }
        throw new UnsupportedOperationException("Call fetchAllContacts() or fetchAllContactsAsync() before invoking this");
    }

    /**
     * Fetches all the Phone numbers for this contacts.
     *
     * @param contactInfo contactVo
     * @return contact vo
     */
    private ContactInfo getContactPhoneDetails(final ContactInfo contactInfo) {
        // Make sure we update the contact only if we didn't populate the
        // phone numbers list already
        // and the contact has phone numbers
        if (contactInfo.getPhoneNumbersList() == null && contactInfo.hasPhoneNumber()) {
            List<PhoneNumberInfo> phoneNumbersList = mContactService.getContactPhoneDetails(contactInfo.getId());

            contactInfo.setPhoneNumbersList(phoneNumbersList);
        }
        return contactInfo;
    }

    @Override
    public List<MessageInfo> getContactMessages(final String contactId) {
        return mContactService.getContactMessages(contactId);
    }

    @Override
    public long getContactIdFromRawContactId(final String rawContactId) {
        return mContactService.getContactIdFromRawContactId(rawContactId);
    }

    @Override
    public String getContactIdFromAddress(final String address) {
        return mContactService.getContactIdFromAddress(address);
    }

    @Override
    public ContactInfo getContactInfo(final String contactId) {
        ContactInfo contactVo = null;
        String tmp;
        for (ContactInfo vo : contactsList) {
            tmp = vo.getId();
            if (tmp.equals(contactId)) {
                contactVo = vo;
                break;
            }
        }
        return contactVo;
    }

    @Override
    public Bitmap getContactPhoto(final String contactId, final Bitmap defaultImage) {
        Bitmap contactPhoto = getContactPhoto(contactId);
        if (null == contactPhoto) {
            return defaultImage;
        }
        return contactPhoto;
    }

    /**
     * Returns the total number of contacts fetched from the phone's database
     *
     * @return total contacts read
     */
    @Override
    public int getTotalContactCount() {
        if (STATUS.READY == contactManagerStatus && null != contactsList) {
            return contactsList.size();
        }
        return 0;
    }

    @Override
    public Bitmap getContactPhoto(final String contactId) {
        return mContactService.getContactPhoto(contactId);
    }

    @Override
    public ContactInfo getRandomContact() {
        ContactInfo contactVo = null;
        // generate a random number less than contactsList.size();
        int numContacts = getTotalContactCount();

        if (numContacts > 0) {
            Random random = new Random();
            int contactIndex = random.nextInt(numContacts);
            contactVo = getContactPhoneDetails(contactsList.get(contactIndex));

            // Update the contacts list with the updated contact item
            contactsList.set(contactIndex, contactVo);
        }

        return contactVo;
    }

    @Override
    public ContactInfo getContactWithPhoneDetails(final String contactId) {
        ContactInfo contactVo = null;

        if (null != contactId && getTotalContactCount() > 0) {
            contactVo = getContactPhoneDetails(getContactInfo(contactId));
        }

        return contactVo;
    }


}
