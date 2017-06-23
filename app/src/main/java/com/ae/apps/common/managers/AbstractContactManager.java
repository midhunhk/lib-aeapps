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

package com.ae.apps.common.managers;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.TimingLogger;

import com.ae.apps.common.services.AeContactService;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.common.vo.MessageVo;
import com.ae.apps.common.vo.PhoneNumberVo;

import java.util.List;
import java.util.Random;

/**
 * An abstract implementation of common methods for AeContactManager
 */
abstract class AbstractContactManager implements AeContactManager {

    protected enum STATUS {
        UNINITIALIZED, INITIALIZING, READY
    }

    protected AeContactService contactService;
    protected Resources resources;
    protected ContentResolver contentResolver;
    protected List<ContactVo> contactsList;

    protected STATUS contactManagerStatus = STATUS.UNINITIALIZED;

    // Config Item
    protected boolean addContactsWithPhoneNumbers;
    protected ContactsDataConsumer consumer;

    /**
     * Fetches all the Phone numbers for this contacts.
     *
     * @param contactVo contactVo
     * @return contact vo
     */
    private ContactVo getContactPhoneDetails(final ContactVo contactVo) {
        // Make sure we update the contact only if we didn't populate the
        // phone numbers list already
        // and the contact has phone numbers
        if (contactVo.getPhoneNumbersList() == null && contactVo.getHasPhoneNumber()) {
            List<PhoneNumberVo> phoneNumbersList = contactService.getContactPhoneDetails(contactVo.getId());

            contactVo.setPhoneNumbersList(phoneNumbersList);
        }
        return contactVo;
    }

    @Override
    public List<MessageVo> getContactMessages(final String contactId) {
        return contactService.getContactMessages(contactId);
    }

    @Override
    public long getContactIdFromRawContactId(final String rawContactId) {
        return contactService.getContactIdFromRawContactId(rawContactId);
    }

    @Override
    public String getContactIdFromAddress(final String address) {
        return contactService.getContactIdFromAddress(address);
    }

    @Override
    public ContactVo getContactInfo(final String contactId) {
        ContactVo contactVo = null;
        String tmp;
        for (ContactVo vo : contactsList) {
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
        return contactService.getContactPhoto(contactId);
    }

    @Override
    public ContactVo getRandomContact() {
        ContactVo contactVo = null;
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
    public ContactVo getContactWithPhoneDetails(final String contactId) {
        ContactVo contactVo = null;

        if (null != contactId && getTotalContactCount() > 0) {
            contactVo = getContactPhoneDetails(getContactInfo(contactId));
        }

        return contactVo;
    }

    /**
     * Fetches all contacts as async ona new thread
     */
    protected void fetchContactsAsync() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                contactsList = fetchAllContacts();

                // Inform the consumer that the data is ready
                if (null != consumer) {
                    consumer.onContactsRead();
                }
            }
        }).start();
    }

    /**
     * Fetches all the contacts from the contacts data table
     *
     * @return list of contacts
     */
    protected List<ContactVo> fetchAllContacts() {
        contactManagerStatus = STATUS.INITIALIZING;

        TimingLogger timingLogger = new TimingLogger("ContactManager", "Read Contacts");
        timingLogger.addSplit("start to read contacts");

        List<ContactVo> contactsList = contactService.getContacts(addContactsWithPhoneNumbers);

        timingLogger.addSplit("end reading contacts");
        timingLogger.dumpToLog();

        contactManagerStatus = STATUS.READY;

        return contactsList;
    }

    /**
     * Returns a MessageVo for the contactId
     *
     * @param contactId contact id
     * @return
     */
    public MessageVo getLatestMessage(final String contactId) {
        MessageVo messageVo = null;
        // TODO : Complete this method
        /*
         * Cursor inboxCursor = contentResolver.query(Uri.parse(SMS_INBOX_URI), null, SMS_PERSON + " = ?", new String[]
		 * { contactId }, null);
		 *
		 * if (inboxCursor != null) { if (inboxCursor.moveToNext()) { int bodyColumn =
		 * inboxCursor.getColumnIndex(COLUMN_BODY); if (bodyColumn > 0) { String messageBody =
		 * inboxCursor.getString(bodyColumn); messageVo = new MessageVo(); messageVo.setBody(messageBody); } } }
		 */
        List<MessageVo> listOfMessages = getContactMessages(contactId);
        if (listOfMessages != null && listOfMessages.size() > 0) {
            messageVo = listOfMessages.get(0);
        }
        return messageVo;
    }

    /**
     * Represents a consumer that needs contacts data and supplies a callback when the
     * contacts data are read
     */
    public interface ContactsDataConsumer {
        void onContactsRead();
    }

}
