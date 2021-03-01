package com.ae.apps.lib.api.contacts.types;

import com.ae.apps.lib.api.contacts.ContactsApiGateway;

/**
 * A Factory interface that can be used to create an instance of ContactsApiGateway.
 * Would most likely be used in unit testing scenarios
 *
 * @since 4.1
 */
public interface ContactsApiGatewayFactory {

    /**
     *
     * @return an instance of ContactsApiGateway Implementation
     */
    ContactsApiGateway getContactsApiGateway();
}
