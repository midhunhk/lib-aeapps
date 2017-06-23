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

import com.ae.apps.common.mock.MockContactService;
import com.ae.apps.common.services.ContactService;
import com.ae.apps.common.vo.ContactVo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The ContactManager abstracts access to the Android's Contacts API and provide public methods to retrieve data.
 * Users of ContactManager do not need to know the internals of Android's ContactsAPI or database calls.
 * <p>
 * <p>Need the following permissions in manifest</p>
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
     * @param config configuration for initializing a ContactManager object
     */
    public ContactManager(@NonNull Config config) {
        this.contentResolver = config.contentResolver;
        this.resources = config.resources;
        this.addContactsWithPhoneNumbers = config.addContactsWithPhoneNumbers;

        // Decide whether to create a Mock implementation or accessing the phone's
        // contacts database. Set to true for unit tests
        if (config.useMockService) {
            this.contactService = new MockContactService();
        } else {
            this.contactService = new ContactService(contentResolver, resources);
        }

        if (config.readContactsAsync && null != config.consumer) {
            // Lazy load contacts if contacts data are not required when creating
            // an instance of this object and not block the calling method while
            // we are reading the contacts
            this.consumer = config.consumer;
            fetchContactsAsync();
        } else {
            // Read all the contacts from the contacts database
            contactsList = fetchAllContacts();
        }
    }

    /**
     * Returns a copy of the contacts list
     * <p>
     * Note that this is an expensive operation as a new list is created with original
     * values and returned to the caller.
     *
     * @return all contacts
     */
    @Nullable
    public List<ContactVo> getAllContacts() {
        if (STATUS.READY == contactManagerStatus && null != contactsList) {
            return new ArrayList<>(contactsList);
        }
        return null;
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
     * @param contactId contact id
     * @return contact image
     */
    @Nullable
    public InputStream openPhoto(final long contactId) {
        return contactService.openPhoto(contactId);
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
     * Configuration data for initializing a ContactManager instance
     * This is a temporary workaround
     */
    public static class Config {
        public ContentResolver contentResolver;
        public Resources resources;
        public boolean addContactsWithPhoneNumbers;
        public boolean readContactsAsync;
        public boolean useMockService;
        public ContactsDataConsumer consumer;
    }

    /**
     * Builds an instance of ContactManager
     */
    public static class Builder {
        private Config config;

        public Builder(ContentResolver contentResolver, Resources resources) {
            this.config = new Config();
            this.config.contentResolver = contentResolver;
            this.config.resources = resources;
        }

        @NonNull
        public Builder addContactsWithPhoneNumbers(boolean addContactsWithPhoneNumbers) {
            this.config.addContactsWithPhoneNumbers = addContactsWithPhoneNumbers;
            return this;
        }

        @NonNull
        public Builder readContactsAsync(boolean readContactsAsync) {
            this.config.readContactsAsync = readContactsAsync;
            return this;
        }

        @NonNull
        public Builder consumer(ContactsDataConsumer consumer) {
            this.config.consumer = consumer;
            return this;
        }

        @NonNull
        public Builder useMockService(boolean useMockService) {
            this.config.useMockService = useMockService;
            return this;
        }

        @NonNull
        public ContactManager build() {
            return new ContactManager(this.config);
        }

    }
}
