/*
 * Copyright (c) 2018 Midhun Harikumar
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

package com.ae.apps.lib.api.sms;

import com.ae.apps.lib.common.models.MessageInfo;

import java.util.List;

/**
 * A gateway which provides an abstraction over Android's SMS API
 * Requires the permission <b>android.permission.READ_SMS</b> in the manifest
 *
 * Replacement for the SMSManager implementation from libAeApps up to version 3
 *
 * @since 4.0
 */
public interface SmsApiGateway {

    /**
     * Returns a list of SMS Messages based on the URI requested
     *
     * @param uri The URI
     * @return list of messages
     */
    List<MessageInfo> getMessagesForUri(final String uri);

    /**
     * Returns the number of messages based on the URI requested
     *
     * @param uri the URI
     * @return number of messages
     */
    long getMessageCountForUri(final String uri);

    /**
     * Returns the list of messages for the specified contact and URI
     *
     * @param uri the uri
     * @param contactId the contact id
     * @return list of messages
     */
    List<MessageInfo> getMessagesForContact(final String uri, final String contactId);

    /**
     * Returns the number of messages for the specified contact and URI
     *
     * @param uri the uri
     * @param contactId the contact id
     * @return number of messages
     */
    long getMessageCountForContact(final String uri, final String contactId);
}
