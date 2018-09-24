package com.ae.apps.lib.api.contacts.types;

import android.graphics.Bitmap;
import android.support.annotation.IntegerRes;

/**
 * Specifies options for getting ContactInfo
 */
public final class ContactInfoOptions {

    private boolean includePhoneDetails;

    private boolean includeContactPicture;

    private int defaultContactPicture;

    public static ContactInfoOptions of(final boolean includePhoneDetails) {
        ContactInfoOptions options = new ContactInfoOptions();
        options.includePhoneDetails = includePhoneDetails;
        return options;
    }

    public static ContactInfoOptions of(boolean includeContactPicture, int defaultContactPicture) {
        ContactInfoOptions options = new ContactInfoOptions();
        options.includeContactPicture = includeContactPicture;
        options.defaultContactPicture = defaultContactPicture;
        return options;
    }

    public static ContactInfoOptions of(final boolean includePhoneDetails,
                                        boolean includeContactPicture, int defaultContactPicture) {
        ContactInfoOptions options = new ContactInfoOptions();
        options.includePhoneDetails = includePhoneDetails;
        options.includeContactPicture = includeContactPicture;
        options.defaultContactPicture = defaultContactPicture;
        return options;
    }

    public boolean isIncludePhoneDetails() {
        return includePhoneDetails;
    }

    public boolean isIncludeContactPicture() {
        return includeContactPicture;
    }

    public int getDefaultContactPicture() {
        return defaultContactPicture;
    }

}
