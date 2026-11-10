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

    /**
     * Invoked instead of {@link #onContactsRead()} when the contacts could not be read, for
     * example because the READ_CONTACTS permission is missing. The gateway is left
     * uninitialized, so initialization can be attempted again.
     * <p>
     * The default implementation does nothing.
     *
     * @param error the reason the contacts could not be read
     * @since 5.0
     */
    default void onContactsReadFailed(Exception error) {
    }
}
