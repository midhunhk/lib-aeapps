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

import static com.ae.apps.lib.api.contacts.utils.ContactsApiConstants.SELECT_WITH_CONTACT_ID;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;

import com.ae.apps.lib.api.contacts.ContactsApiGateway;
import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions;
import com.ae.apps.lib.api.contacts.types.ContactsDataConsumer;
import com.ae.apps.lib.api.contacts.utils.ContactsApiUtils;
import com.ae.apps.lib.common.models.ContactInfo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An abstract implementation of ContactsApiGateway
 */
public abstract class AbstractContactsApiGateway implements ContactsApiGateway {

    protected enum STATE {
        UNINITIALIZED, INITIALIZING, READY
    }

    protected Resources resources;
    protected ContentResolver contentResolver;
    protected volatile transient STATE gatewayState = STATE.UNINITIALIZED;
    protected volatile transient List<ContactInfo> contacts = Collections.emptyList();

    @Override
    public void initialize(final ContactInfoFilterOptions options) {
        if (STATE.UNINITIALIZED == gatewayState) {
            gatewayState = STATE.INITIALIZING;

            readContacts(options);

            gatewayState = STATE.READY;
        }
    }

    protected abstract void readContacts(final ContactInfoFilterOptions options);

    @Override
    public void initializeAsync(final ContactInfoFilterOptions options,
                                final ContactsDataConsumer dataConsumer) {
        final Handler handler = new Handler();

        new Thread(() -> {
            initialize(options);

            if (null != dataConsumer) {
                // do the callback on the original thread
                handler.post(dataConsumer::onContactsRead);
            }
        }).start();
    }

    @Override
    public List<ContactInfo> getAllContacts() throws IllegalStateException {
        throwExceptionIfNotInitialized();

        return new ArrayList<>(contacts);
    }

    @Override
    public long getReadContactsCount() {
        throwExceptionIfNotInitialized();

        return contacts.size();
    }

    protected void throwExceptionIfNotInitialized() {
        if (checkIfApiNotInitialized()) {
            throw new IllegalStateException("ContactsApiGateway not initialized");
        }
    }

    /**
     * Checks if the contact has more than 1 phone numbers associated with it
     *
     * @param contactInfo a valid contactInfo object
     * @return true if the contact has multiple phone numbers, false otherwise
     */
    protected boolean hasMultiplePhoneNumbers(ContactInfo contactInfo) {
        return contactInfo.hasPhoneNumber()
                && null != contactInfo.getPhoneNumbersList()
                && contactInfo.getPhoneNumbersList().size() > 1;
    }

    protected void updateWithPhoneDetails(final ContactInfo contactInfo) {
        if (contactInfo.getPhoneNumbersList() == null && contactInfo.hasPhoneNumber()) {
            Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, SELECT_WITH_CONTACT_ID, new String[]{contactInfo.getId()},
                    null);
            contactInfo.setPhoneNumbersList(ContactsApiUtils.createPhoneNumberList(phoneCursor, resources));
        }
    }

    @Nullable
    protected Bitmap getContactPicture(final String contactId) {
        long contactIdLong = Long.parseLong(contactId);
        Uri contactPhotoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactIdLong);

        InputStream photoDataStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver,
                contactPhotoUri);

        if (null == photoDataStream) {
            return null;
        }
        return BitmapFactory.decodeStream(photoDataStream);
    }

    @Override
    public ContactInfo getContactInfo(String s) {
        return null;
    }

    protected boolean checkIfApiInitialized() {
        return gatewayState == STATE.READY;
    }

    protected boolean checkIfApiNotInitialized() {
        return !checkIfApiInitialized();
    }
}
