package com.ae.apps.lib.api.sms;

/**
 * A factory interface that can create an instance of SmsApiGateway.
 * Would most likely be used in unit testing scenarios
 *
 * @since 4.1
 */
public interface SmsApiGatewayFactory {

    /**
     * Creates an instance of SmsApiGateway
     *
     * @return an instance of SmsApiGateway
     */
    SmsApiGateway getSmsApiGateway();
}
