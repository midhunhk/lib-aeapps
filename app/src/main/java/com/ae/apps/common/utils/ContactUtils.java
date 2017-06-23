package com.ae.apps.common.utils;

import java.util.List;

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
}
