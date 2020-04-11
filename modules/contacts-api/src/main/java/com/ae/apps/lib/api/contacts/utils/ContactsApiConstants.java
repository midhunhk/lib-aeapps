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

import android.provider.ContactsContract;

/**
 * Constants for Contacts Api
 */
public interface ContactsApiConstants {

    String COLUMN_ID = "_id";
    String DATE_FORMAT = "MMM dd, yyyy hh:mm a";

    String[] PROJECTION_ID_RAW_CONTACT_ID = new String[]{
            ContactsContract.RawContacts._ID, ContactsContract.RawContacts.CONTACT_ID
    };

    String SELECT_WITH_RAW_CONTACT_ID = ContactsContract.RawContacts._ID + " = ?";

    String SELECT_WITH_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
}
