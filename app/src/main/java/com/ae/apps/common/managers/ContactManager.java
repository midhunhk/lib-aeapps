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
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ae.apps.common.managers.contact.AbstractContactManager;
import com.ae.apps.common.services.ContactService;
import com.ae.apps.common.vo.ContactVo;

import java.io.InputStream;

/**
 * The ContactManager abstracts access to the Android's Contacts API and provide public methods to retrieve data.
 * Users of ContactManager do not need to know the internals of Android's ContactsAPI or database calls.
 * <p>
 * <p>Needs the following permissions added to the manifest</p>
 * <pre>
 * 	android.permission.READ_CONTACTS
 * 	android.permission.CALL_PHONE
 * 	android.permission.READ_SMS
 * </pre>
 */
public class ContactManager extends AbstractContactManager {

    private static final String TAG = "ContactManager";

    /**
     * Construct an instance with configuration
     *
     * @param builder for initializing a ContactManager object
     */
    protected ContactManager(@NonNull Builder builder) {
        this.contentResolver = builder.contentResolver;
        this.resources = builder.resources;
        this.addContactsWithPhoneNumbers = builder.addContactsWithPhoneNumbers;

        // Create the service class instance to serve the contacts
        this.mContactService = new ContactService(contentResolver, resources);
    }

    /**
     * Returns a Bitmap object taking into consideration whether the supplied contact is mock
     *
     * @param contactVo    contact vo
     * @param defaultImage default image
     * @param resource     resources
     * @return contact image
     */
    public Bitmap getContactPhotoWithMock(@NonNull final ContactVo contactVo, final Bitmap defaultImage, final Resources resource) {
        if (contactVo.isMockUser()) {
            try {
                // try to decode the image resource if this is a mock user
                Bitmap bitmap = BitmapFactory.decodeResource(resource, contactVo.getMockProfileImageResource());
                if (null != bitmap) {
                    return bitmap;
                }
            } catch (Exception exception) {
                Log.e(TAG, "getContactPhotoWithMock() " + exception.getMessage());
            }
        }
        return getContactPhoto(contactVo.getId(), defaultImage);
    }

    /**
     * Opens the contact's photo
     *
     * @param contactId contact id
     * @return contact image
     */
    @Nullable
    public InputStream openPhoto(final long contactId) {
        return mContactService.openPhoto(contactId);
    }

    /**
     * Shows this contact in the Android's Contact Manager
     *
     * @param contactVo contact vo
     */
    public void showContactInAddressBook(@NonNull Context context, @Nullable ContactVo contactVo) {
        if (null != contactVo && null != contactVo.getId()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactVo.getId());
            intent.setData(uri);

            context.startActivity(intent);
        }
    }

    /**
     * Builds an instance of ContactManager
     */
    public static class Builder {
        private ContentResolver contentResolver;
        private Resources resources;
        private boolean addContactsWithPhoneNumbers;

        public Builder(ContentResolver contentResolver, Resources resources) {
            this.contentResolver = contentResolver;
            this.resources = resources;
        }

        @NonNull
        public Builder addContactsWithPhoneNumbers(boolean addContactsWithPhoneNumbers) {
            this.addContactsWithPhoneNumbers = addContactsWithPhoneNumbers;
            return this;
        }

        @NonNull
        public ContactManager build() {
            return new ContactManager(this);
        }

    }
}
