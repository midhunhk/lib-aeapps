package com.ae.apps.common.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.List;

import static android.net.Uri.withAppendedPath;

/**
 * Helper methods for Contact
 */
public class ContactUtils {
    /**
     * Checks whether the numberToCheck is present in the supplied list. Adds the number to the list if it doesn't exist
     * and returns false
     *
     * @param phoneNumbers  phone numbers
     * @param numberToCheck number to check
     */
    public static boolean checkIfPhoneNumberExists(final List<String> phoneNumbers, final String numberToCheck) {
        // Remove spaces, hyphens and + symbols from the phone number for comparison
        String unformattedNumber = numberToCheck.replaceAll("\\s+", "")
                .replaceAll("\\+", "")
                .replaceAll("-", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "")
                .trim();
        if (phoneNumbers.contains(unformattedNumber)) {
            return true;
        } else {
            phoneNumbers.add(unformattedNumber);
            return false;
        }
    }

    /**
     * Shows this contact in the Android's Contact Manager
     *
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
}
