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

package com.ae.apps.lib.api.contacts.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ae.apps.lib.api.contacts.ContactsApiGateway;
import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions;
import com.ae.apps.lib.api.contacts.types.ContactInfoOptions;
import com.ae.apps.lib.api.contacts.utils.ContactsApiConstants;
import com.ae.apps.lib.api.contacts.utils.ContactsApiUtils;
import com.ae.apps.lib.common.models.ContactInfo;

import java.util.Random;

import static com.ae.apps.lib.api.contacts.utils.ContactsApiConstants.PROJECTION_ID_RAW_CONTACT_ID;
import static com.ae.apps.lib.api.contacts.utils.ContactsApiConstants.SELECT_WITH_CONTACT_ID;
import static com.ae.apps.lib.api.contacts.utils.ContactsApiConstants.SELECT_WITH_RAW_CONTACT_ID;

/**
 * An implementation of ContactsApiGateway
 */
public class ContactsApiGatewayImpl extends AbstractContactsApiGateway {

    private ContactsApiGatewayImpl(Builder builder) {
        this.mResources = builder.resources;
        this.mContentResolver = builder.contentResolver;
    }

    @Override
    protected void readContacts(ContactInfoFilterOptions options) {
        Cursor cursor = mContentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        mContacts = ContactsApiUtils.createContactsList(cursor, options.isIncludeContactsWithPhoneNumbers());
    }

    @Override
    public ContactInfo getContactInfo(String contactId) {
        if (null == contactId) {
            throw new IllegalArgumentException("contactId cannot be null");
        }
        for (ContactInfo contactInfo : mContacts) {
            if (contactId.equals(contactInfo.getId())) {
                return contactInfo;
            }
        }
        return null;
    }

    @Override
    public ContactInfo getRandomContact() {
        throwExceptionIfNotInitialized();

        ContactInfo contactInfo = null;
        if (getReadContactsCount() > 0) {
            Random random = new Random();
            int contactIndex = random.nextInt(Integer.valueOf(getReadContactsCount() + ""));
            contactInfo = getContactInfo(mContacts.get(contactIndex).getId(),
                    ContactInfoOptions.of(true,
                            true, com.ae.apps.lib.R.drawable.profile_icon_1));

            // Update the contacts list with the updated contact item
            mContacts.set(contactIndex, contactInfo);
        }
        return contactInfo;
    }

    @Override
    public String getContactIdFromRawContact(String rawContactId) {
        String contactId = String.valueOf(0);
        Cursor cursor = mContentResolver.query(ContactsContract.RawContacts.CONTENT_URI,
                PROJECTION_ID_RAW_CONTACT_ID,
                SELECT_WITH_RAW_CONTACT_ID,
                new String[]{rawContactId}, null);

        if (null != cursor && cursor.moveToFirst()) {
            int contactIdIndex = cursor.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID);
            contactId = cursor.getString(contactIdIndex);
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
                String[] projection = new String[]{ContactsApiConstants.COLUMN_ID};
                Uri personUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, address);
                if (null != personUri) {
                    cursor = mContentResolver.query(personUri, projection, null, null, null);
                    if (null != cursor && cursor.moveToFirst()) {
                        contactId = cursor.getString(cursor.getColumnIndex(ContactsApiConstants.COLUMN_ID));
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

    private void updateWithPhoneDetails(final ContactInfo contactInfo) {
        if (contactInfo.getPhoneNumbersList() == null && contactInfo.hasPhoneNumber()) {
            Cursor phoneCursor = mContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, SELECT_WITH_CONTACT_ID, new String[]{contactInfo.getId()},
                    null);
            contactInfo.setPhoneNumbersList(ContactsApiUtils.createPhoneNumberList(phoneCursor, mResources));
        }
    }

    @Override
    public ContactInfo getContactInfo(String contactId, ContactInfoOptions options) {
        ContactInfo contactInfo = getContactInfo(contactId);
        if (options.isIncludePhoneDetails()) {
            updateWithPhoneDetails(contactInfo);
        }
        if (options.isIncludeContactPicture()) {
            Bitmap picture = getContactPicture(contactId);
            if (null == picture) {
                if (null != mResources && options.getDefaultContactPicture() > 0) {
                    contactInfo.setPicture(
                            BitmapFactory.decodeResource(mResources, options.getDefaultContactPicture()));
                }
            } else {
                contactInfo.setPicture(picture);
            }
        }
        return contactInfo;
    }

    /**
     * Builds an instance of {@link ContactsApiGateway}
     */
    public static class Builder {
        private ContentResolver contentResolver;
        private Resources resources;

        public Builder(Context context) {
            this.contentResolver = context.getContentResolver();
            this.resources = context.getResources();
        }

        @NonNull
        public ContactsApiGateway build() {
            return new ContactsApiGatewayImpl(this);
        }
    }
}
