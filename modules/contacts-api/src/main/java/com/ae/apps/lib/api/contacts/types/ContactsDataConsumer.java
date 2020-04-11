package com.ae.apps.lib.api.contacts.types;

/**
 * Consumers that need to invoke the async version of fetch contacts must implement this to receive a callback
 */
public interface ContactsDataConsumer {

    /**
     * Represents a consumer that needs contacts data and supplies a callback when the
     * contacts data are read
     */
    void onContactsRead();
}
