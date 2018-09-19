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
package com.ae.apps.lib.contacts.services;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ae.apps.common.utils.CommonUtils;
import com.ae.apps.common.utils.ContactUtils;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.common.vo.MessageVo;
import com.ae.apps.common.vo.PhoneNumberVo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * An Implementation for Contact API access
 */
public class ContactService implements AeContactService {

    private static final String ID = "_id";
    private static final String SMS_PERSON = "person";
    private static final String SMS_URI_INBOX = "content://sms/inbox";
    private static final String SMS_BODY = "body";
    private static final String SMS_ADDRESS = "address";
    private static final String DATE_FORMAT = "MMM dd, yyyy hh:mm a";

    private Resources resources;
    private ContentResolver contentResolver;

    /**
     * Creates an instance of the ContactService
     *
     * @param contentResolver contentResolver
     * @param resources       resources
     */
    public ContactService(ContentResolver contentResolver, Resources resources) {
        this.contentResolver = contentResolver;
        this.resources = resources;
    }

    @NonNull
    @Override
    public List<ContactVo> getContacts(boolean addContactsWithPhoneNumbers) {
        List<ContactVo> contactsList = new ArrayList<>();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (null != cursor && cursor.getCount() > 0) {
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

                hasPhoneNumber = Integer.parseInt(hasPhoneNumberText) > 0;

                // Simplification of below expression
                // !addContactsWithPhoneNumbers || addContactsWithPhoneNumbers && hasPhoneNumber
                boolean addContact = !addContactsWithPhoneNumbers || hasPhoneNumber;

                if (addContact) {
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
        if (null != cursor) {
            cursor.close();
        }

        return contactsList;
    }

    @Override
    public Bitmap getContactPhoto(String contactId) {
        long contactIdLong = Long.parseLong(contactId);
        Uri contactPhotoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactIdLong);

        InputStream photoDataStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver,
                contactPhotoUri);

        return BitmapFactory.decodeStream(photoDataStream);
    }

    @NonNull
    @Override
    public List<PhoneNumberVo> getContactPhoneDetails(String contactId) {
        List<PhoneNumberVo> phoneNumbersList = new ArrayList<>();

        // Query to fetch the Phone Entries with this Contact's Id
        Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId},
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
                if (!ContactUtils.checkIfPhoneNumberExists(phoneNumbers, phoneNumber)) {
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
        return phoneNumbersList;
    }

    @NonNull
    @Override
    public List<MessageVo> getContactMessages(String contactId) {
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
    public long getContactIdFromRawContactId(String rawContactId) {
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

    @Nullable
    @Override
    public String getContactIdFromAddress(@Nullable String address) {
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

    @Nullable
    @Override
    public InputStream openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = contentResolver.query(photoUri, new String[]{ContactsContract.Contacts.Photo.DATA15}, null, null, null);

        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }
}
