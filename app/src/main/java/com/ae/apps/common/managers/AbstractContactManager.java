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
import android.content.ContentUris;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

import com.ae.apps.common.utils.CommonUtils;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.common.vo.MessageVo;
import com.ae.apps.common.vo.PhoneNumberVo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An abstract implementation of common methods for AeContactManager
 */
abstract class AbstractContactManager implements AeContactManager {

    protected enum STATUS {
        UNINITIALIZED, INITIALIZING, READY
    }

    private static final String SMS_PERSON = "person";
    private static final String SMS_URI_INBOX = "content://sms/inbox";
    private static final String DATE_FORMAT = "MMM dd, yyyy hh:mm a";
    private static final String SMS_BODY = "body";
    private static final String SMS_ADDRESS = "address";
    private static final String ID = "_id";

    protected Resources resources;
    protected ContentResolver contentResolver;
    protected ArrayList<ContactVo> contactsList;

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
            ArrayList<PhoneNumberVo> phoneNumbersList = new ArrayList<>();

            // Query to fetch the Phone Entries with this Contact's Id
            Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactVo.getId()},
                    null);

            int phoneType;
            String phoneLabel = null;
            String phoneNumber;
            String customLabel;

            // Prefetch some column indexes from the cursor
            if (null != phoneCursor) {
                int phoneNumberIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int phoneTypeIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                int phoneLabelIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL);

                List<String> phoneNumbers = new ArrayList<>();
                while (phoneCursor.moveToNext()) {
                    // Retrieve values from the cursor
                    phoneType = phoneCursor.getInt(phoneTypeIndex);
                    phoneNumber = phoneCursor.getString(phoneNumberIndex);

                    // Check for duplicate numbers before adding to the phone numbers
                    if (!checkIfPhoneNumberExists(phoneNumbers, phoneNumber)) {
                        // Get the label for this number
                        if (null != resources) {
                            customLabel = phoneCursor.getString(phoneLabelIndex);
                            phoneLabel = (String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(resources, phoneType,
                                    customLabel);
                        }

                        // Save this phone number as a VO
                        PhoneNumberVo phoneNumberVo = new PhoneNumberVo();
                        phoneNumberVo.setPhoneNumber(phoneNumber);
                        phoneNumberVo.setPhoneType(phoneLabel);

                        // Add this Phone Number to the list of phone numbers
                        phoneNumbersList.add(phoneNumberVo);
                    }
                }
                phoneCursor.close();
            }
            contactVo.setPhoneNumbersList(phoneNumbersList);
        }
        return contactVo;
    }

    @Override
    public List<MessageVo> getContactMessages(final String contactId) {
        MessageVo messageVo;
        List<MessageVo> messagesList = new ArrayList<>();
        String[] projection = new String[]{ID, SMS_ADDRESS, SMS_PERSON, SMS_BODY};
        Cursor inboxCursor = contentResolver.query(Uri.parse(SMS_URI_INBOX), projection, SMS_PERSON + " = ?",
                new String[]{contactId}, null);

        if (null != inboxCursor && inboxCursor.moveToLast()) {
            int bodyIndex = inboxCursor.getColumnIndex(SMS_BODY);
            do {
                String messageBody = inboxCursor.getString(bodyIndex);
                messageVo = new MessageVo();
                messageVo.setBody(messageBody);
                messagesList.add(messageVo);
            } while (inboxCursor.moveToNext());
            inboxCursor.close();
        }
        return messagesList;
    }

    @Override
    public long getContactIdFromRawContactId(final String rawContactId) {
        long contactId = 0;
        String[] projection = new String[]{ContactsContract.RawContacts._ID, ContactsContract.RawContacts.CONTACT_ID};
        Cursor cursor = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, projection, ContactsContract.RawContacts._ID + " = ?",
                new String[]{rawContactId}, null);

        if (null != cursor && cursor.moveToFirst()) {
            int contactIdIndex = cursor.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID);
            contactId = cursor.getLong(contactIdIndex);
            cursor.close();
        }

        return contactId;
    }

    @Override
    public String getContactIdFromAddress(final String address) {
        String contactId = null;
        Cursor cursor = null;
        try {
            if (address != null) {
                String[] projection = new String[]{ID};
                Uri personUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, address);
                if (null != personUri) {
                    cursor = contentResolver.query(personUri, projection, null, null, null);
                    if (null != cursor && cursor.moveToFirst()) {
                        contactId = cursor.getString(cursor.getColumnIndex(ID));
                    }
                }
            }
        } catch (IllegalArgumentException i) {
            // Maybe a device specific implementation difference
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return contactId;
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

    /**
     * Returns a Bitmap object for the contact's photo
     *
     * @param contactId contact id
     * @return contact photo
     */
    private Bitmap getContactPhoto(final String contactId) {
        long contactIdLong = Long.parseLong(contactId);
        Uri contactPhotoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactIdLong);

        InputStream photoDataStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver,
                contactPhotoUri);

        return BitmapFactory.decodeStream(photoDataStream);
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
    protected ArrayList<ContactVo> fetchAllContacts() {
        ArrayList<ContactVo> contactsList = new ArrayList<>();

        contactManagerStatus = STATUS.INITIALIZING;

        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0) {
            String id;
            String name;
            String timesContacted;
            String hasPhoneNumberText;
            String lastContactedTime;
            String lastContactedTimeString;
            boolean hasPhoneNumber;

            while (cursor.moveToNext()) {
                // Read data from the Contacts data table
                id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                timesContacted = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.TIMES_CONTACTED));
                hasPhoneNumberText = cursor
                        .getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                lastContactedTime = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED));

                // Support v1.5 and below API to exclude contacts without phone numbers
                hasPhoneNumber = Integer.parseInt(hasPhoneNumberText) > 0;
                if (!addContactsWithPhoneNumbers || addContactsWithPhoneNumbers && hasPhoneNumber) {
                    lastContactedTimeString = CommonUtils.formatTimeStamp(lastContactedTime, DATE_FORMAT);

                    // Save that data to a VO
                    ContactVo contactVo = new ContactVo();
                    contactVo.setId(id);
                    contactVo.setName(name);

                    contactVo.setHasPhoneNumber(hasPhoneNumber);
                    contactVo.setTimesContacted(timesContacted);
                    contactVo.setLastContactedTime(lastContactedTimeString);

                    // Add the contact to a list
                    contactsList.add(contactVo);
                }
            }
        }
        cursor.close();

        contactManagerStatus = STATUS.READY;

        return contactsList;
    }

    /**
     * Checks whether the numberToCheck is present in the supplied list. Adds the number to the list if it doesn't exist
     * and returns false
     *
     * @param phoneNumbers  phone numbers
     * @param numberToCheck number to check
     */
    private boolean checkIfPhoneNumberExists(final List<String> phoneNumbers, final String numberToCheck) {
        // Remove spaces, hyphens and + symbols from the phone number for comparison
        String unformattedNumber = numberToCheck.replaceAll("\\s+", "")
                .replaceAll("\\+", "")
                .replaceAll("-", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .trim();
        if (phoneNumbers.contains(unformattedNumber)) {
            return true;
        } else {
            phoneNumbers.add(unformattedNumber);
            return false;
        }
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
