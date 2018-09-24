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
import android.content.ContentUris;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

import com.ae.apps.lib.api.contacts.ContactsApiGateway;
import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions;
import com.ae.apps.lib.api.contacts.types.ContactInfoOptions;
import com.ae.apps.lib.api.contacts.types.ContactsDataConsumer;
import com.ae.apps.lib.api.contacts.utils.ContactsApiConstants;
import com.ae.apps.lib.api.contacts.utils.ContactsApiUtils;
import com.ae.apps.lib.common.models.ContactInfo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.ae.apps.lib.api.contacts.utils.ContactsApiConstants.PROJECTION_ID_RAW_CONTACT_ID;
import static com.ae.apps.lib.api.contacts.utils.ContactsApiConstants.SELECT_WITH_CONTACT_ID;
import static com.ae.apps.lib.api.contacts.utils.ContactsApiConstants.SELECT_WITH_RAW_CONTACT_ID;

/**
 * An abstract implementation of ContactsApiGateway
 */
public abstract class AbstractContactsApiGateway implements ContactsApiGateway {

    protected Resources mResources;

    protected ContentResolver mContentResolver;

    protected enum STATE {
        UNINITIALIZED, INITIALIZING, READY
    }

    protected transient STATE gatewayState = STATE.UNINITIALIZED;

    protected transient List<ContactInfo> mContacts = Collections.emptyList();

    @Override
    public void initializeAsync(final ContactInfoFilterOptions options,
                                final ContactsDataConsumer dataConsumer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initialize(options);

                if (null != dataConsumer) {
                    dataConsumer.onContactsRead();
                }

            }
        }).start();
    }

    @Override
    public List<ContactInfo> getAllContacts() throws IllegalStateException {
        if (checkIfApiNotInitialized()) {
            throw new IllegalStateException("ContactsApiGateway not initialized");
        }
        return new ArrayList<>(mContacts);
    }

    @Override
    public ContactInfo getRandomContact() {
        ContactInfo contactInfo = null;
        if (getReadContactsCount() > 0) {
            Random random = new Random();
            int contactIndex = random.nextInt(Integer.valueOf(getReadContactsCount() + ""));
            contactInfo = getContactInfo(mContacts.get(contactIndex).getId(),
                    ContactInfoOptions.of(true, com.ae.apps.lib.R.drawable.profile_icon_1));

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

    @Override
    public long getReadContactsCount() {
        return mContacts.size();
    }

    protected Bitmap getContactPicture(final String contactId) {
        long contactIdLong = Long.parseLong(contactId);
        Uri contactPhotoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactIdLong);

        InputStream photoDataStream = ContactsContract.Contacts.openContactPhotoInputStream(mContentResolver,
                contactPhotoUri);

        return BitmapFactory.decodeStream(photoDataStream);
    }

    protected void updateWithPhoneDetails(final ContactInfo contactInfo) {
        if (contactInfo.getPhoneNumbersList() == null && contactInfo.hasPhoneNumber()) {
            Cursor phoneCursor = mContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, SELECT_WITH_CONTACT_ID, new String[]{contactInfo.getId()},
                    null);
            contactInfo.setPhoneNumbersList(ContactsApiUtils.createPhoneNumberList(phoneCursor, mResources));
        }
    }

    protected boolean checkIfApiInitialized() {
        return gatewayState == STATE.READY;
    }

    protected boolean checkIfApiNotInitialized() {
        return !checkIfApiInitialized();
    }
}
