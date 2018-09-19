package com.ae.apps.common.managers.contact;

/**
 * Consumers that need to invoke the async version of fetch contacts must implement this to receive a callback
 */
public interface ContactDataConsumer {

    /**
     * Represents a consumer that needs contacts data and supplies a callback when the
     * contacts data are read
     */
    void onContactsRead();
}
