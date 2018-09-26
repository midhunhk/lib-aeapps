/*
 * Copyright (c) 2015 Midhun Harikumar
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

package com.ae.apps.lib.contacts.impl;

import android.content.ContentResolver;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ae.apps.lib.contacts.service.ContactService;

/**
 * The ContactManager abstracts access to the Android's Contacts API and provide public methods to retrieve data.
 * Users of ContactManager do not need to know the internals of Android's ContactsAPI or database calls.
 *
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
