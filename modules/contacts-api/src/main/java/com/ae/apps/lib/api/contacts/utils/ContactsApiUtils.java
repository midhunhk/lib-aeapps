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

package com.ae.apps.lib.api.contacts.utils;

import android.content.res.Resources;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;

import com.ae.apps.lib.common.models.ContactInfo;
import com.ae.apps.lib.common.models.PhoneNumberInfo;
import com.ae.apps.lib.common.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ae.apps.lib.api.contacts.utils.ContactsApiConstants.DATE_FORMAT;

/**
 * Internal utility methods used by ContactsApiGateway
 */
public class ContactsApiUtils {

    public static final String NON_DIGITS_REGEX = "[^\\d.]";

    public static ContactInfo createContactInfo(final Cursor cursor) {
        ContactInfo contactInfo = new ContactInfo();
        String id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
        String hasPhoneNumberText;
        String lastContactedTimeString;
        boolean hasPhoneNumber;

        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        String timesContacted = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.TIMES_CONTACTED));
        hasPhoneNumberText = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
        String lastContactedTime = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED));
        lastContactedTimeString = CommonUtils.formatTimeStamp(lastContactedTime, DATE_FORMAT);
        hasPhoneNumber = Integer.parseInt(hasPhoneNumberText) > 0;

        contactInfo.setId(id);
        contactInfo.setName(name);

        contactInfo.setHasPhoneNumber(hasPhoneNumber);
        contactInfo.setTimesContacted(timesContacted);
        contactInfo.setLastContactedTime(lastContactedTimeString);
        return contactInfo;
    }

    public static List<ContactInfo> createContactsList(Cursor cursor,
                                                       boolean includeContactsWithPhoneNumbers) {
        List<ContactInfo> contactsList = new ArrayList<>();
        if (null != cursor && cursor.getCount() > 0) {
            ContactInfo contactInfo;
            while (cursor.moveToNext()) {
                contactInfo = createContactInfo(cursor);
                boolean addContact = !includeContactsWithPhoneNumbers || contactInfo.hasPhoneNumber();
                if (addContact) {
                    contactsList.add(contactInfo);
                }
            }
        }
        return contactsList;
    }

    public static List<PhoneNumberInfo> createPhoneNumberList(final Cursor phoneCursor, final Resources resources) {
        List<PhoneNumberInfo> phoneNumbers = new ArrayList<>();

        // Prefetch some column indexes from the cursor
        if (null != phoneCursor) {
            PhoneNumberInfo phoneNumberInfo;
            while (phoneCursor.moveToNext()) {
                phoneNumberInfo = createPhoneNumber(phoneCursor, resources);
                phoneNumbers.add(phoneNumberInfo);
            }
            phoneCursor.close();
        }
        return phoneNumbers;
    }

    private static PhoneNumberInfo createPhoneNumber(Cursor phoneCursor, Resources resources) {
        String phoneLabel = null;
        String customLabel;
        int phoneNumberIndex = phoneCursor.getColumnIndex(CommonDataKinds.Phone.NUMBER);
        int phoneTypeIndex = phoneCursor.getColumnIndex(CommonDataKinds.Phone.TYPE);
        int phoneLabelIndex = phoneCursor.getColumnIndex(CommonDataKinds.Phone.LABEL);
        String phoneNumber = phoneCursor.getString(phoneNumberIndex);
        int phoneType = phoneCursor.getInt(phoneTypeIndex);

        // Getting the label for this number
        if (null != resources) {
            customLabel = phoneCursor.getString(phoneLabelIndex);
            phoneLabel = (String) CommonDataKinds.Phone.getTypeLabel(resources, phoneType, customLabel);
        }

        PhoneNumberInfo phoneNumberInfo = new PhoneNumberInfo();
        phoneNumberInfo.setPhoneNumber(phoneNumber);
        phoneNumberInfo.setPhoneType(phoneLabel);
        if(null != phoneNumber){
            // Populate the unformatted phone number field by removing all non numeric characters
            phoneNumberInfo.setUnformattedPhoneNumber(
                    phoneNumber.replaceAll(NON_DIGITS_REGEX, "") );
        }

        return phoneNumberInfo;
    }
}
