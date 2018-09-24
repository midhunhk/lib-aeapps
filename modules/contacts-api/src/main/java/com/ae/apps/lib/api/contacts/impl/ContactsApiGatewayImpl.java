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

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;

import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions;
import com.ae.apps.lib.api.contacts.types.ContactInfoOptions;
import com.ae.apps.lib.api.contacts.utils.ContactsApiUtils;
import com.ae.apps.lib.common.models.ContactInfo;

/**
 * An implementation of ContactsApiGateway
 */
public class ContactsApiGatewayImpl extends AbstractContactsApiGateway {

    @Override
    public void initialize(ContactInfoFilterOptions options) {
        if (STATE.UNINITIALIZED == gatewayState) {
            gatewayState = STATE.INITIALIZING;
            Cursor cursor = mContentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            mContacts = ContactsApiUtils.createContactsList(cursor, options.isIncludeContactsWithPhoneNumbers());

            gatewayState = STATE.READY;
        }
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
}
