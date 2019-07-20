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

package com.ae.apps.lib.common.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import android.widget.Toast;

import java.util.List;

import static android.net.Uri.withAppendedPath;

/**
 * Helper methods for Contact
 */
public class ContactUtils {

    private static final String WHATSAPP_ID_SUFFIX = "@s.whatsapp.net";
    private static final String CONTENT_CONTACTS_DATA = "content://com.android.contacts/data/";

    /**
     * Checks whether the numberToCheck is present in the supplied list
     *
     * @param phoneNumbers  phone numbers
     * @param numberToCheck number to check
     * @return true if phone number exists
     */
    public static boolean checkIfPhoneNumberExists(final List<String> phoneNumbers, final String numberToCheck) {
        String unformattedNumber = cleanupPhoneNumber(numberToCheck);
        return phoneNumbers.contains(unformattedNumber);
    }

    /**
     * Remove all extra symbols and spaces from a given phone number
     *
     * @param formattedPhoneNumber formatted phone number
     * @return unformatted phone number
     */
    @NonNull
    public static String cleanupPhoneNumber(String formattedPhoneNumber) {
        // Remove spaces, hyphens and + symbols from the phone number for comparison
        return formattedPhoneNumber.replaceAll("\\s+", "")
                .replaceAll("\\+", "")
                .replaceAll("-", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .trim();
    }

    /**
     * Shows this contact in the Android's Contact Manager
     *
     * @param context   the context
     * @param contactId contactId
     */
    public static void showContactInAddressBook(Context context, String contactId) {
        if (null != contactId) {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            Uri uri = withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);
            intent.setData(uri);

            context.startActivity(intent);
        }
    }

    /**
     * Tries to open a WhatsApp Contact
     *
     * @param context   the context
     * @param contactId the contact id
     */
    public static void openWhatsAppContact(final Context context, String contactId) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.contacts/data/" + contactId));
        try {
            context.startActivity(i);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method opens the conversation screen for the contact
     * The number should have the Country code prefixed when it was saved,
     * else whatsapp will not open
     *
     * @param context   context
     * @param contactNo contactNo
     */
    public static void sendWhatsAppMethod(final Context context, final String contactNo) {
        String id = cleanupPhoneNumber(contactNo) + WHATSAPP_ID_SUFFIX;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                    new String[]{ContactsContract.Contacts.Data._ID},
                    ContactsContract.Data.DATA1 + "=?",
                    new String[]{id}, null);
            if (null == cursor) {
                return;
            }
            if (!cursor.moveToFirst()) {
                Toast.makeText(context, "WhatsApp contact with this number not found. Make sure it has country code.",
                        Toast.LENGTH_LONG).show();
                return;
            }
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CONTENT_CONTACTS_DATA + cursor.getString(0)));
            PackageManager packageManager = context.getPackageManager();
            if (null == sendIntent.resolveActivity(packageManager)) {
                Toast.makeText(context, "No Activity to handle this Intent", Toast.LENGTH_SHORT).show();
            } else {
                context.startActivity(sendIntent);
            }
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
    }

}
