package com.ae.apps.lib.api.contacts.types;

/**
 * Filter Options when initializing an {@link com.ae.apps.lib.api.contacts.ContactsApiGateway} instance
 */
public final class ContactInfoFilterOptions {

    private boolean includeContactsWithPhoneNumbers;

    public static ContactInfoFilterOptions of(boolean includeContactsWithPhoneNumbers) {
        ContactInfoFilterOptions filterOptions = new ContactInfoFilterOptions();
        filterOptions.includeContactsWithPhoneNumbers = includeContactsWithPhoneNumbers;
        return filterOptions;
    }

    public boolean isIncludeContactsWithPhoneNumbers() {
        return includeContactsWithPhoneNumbers;
    }

}
