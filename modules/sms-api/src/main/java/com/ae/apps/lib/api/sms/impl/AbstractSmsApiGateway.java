/*
 * Copyright 2019 Midhun Harikumar
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
 */

package com.ae.apps.lib.api.sms.impl;

import android.content.ContentResolver;

import com.ae.apps.lib.api.sms.SmsApiGateway;
import com.ae.apps.lib.api.sms.utils.SmsApiConstants;
import com.ae.apps.lib.common.models.MessageInfo;

import java.util.List;

/**
 * An abstract implementation of the SmsApiGateway interface
 */
public abstract class AbstractSmsApiGateway implements SmsApiGateway {

    protected ContentResolver contentResolver;

    @Override
    public long getMessageCountForContact(String uri, String contactId) {
        validateInputParams(uri);

        List<MessageInfo> messages = getMessagesForContact(uri, contactId);
        if (null != messages && !messages.isEmpty()) {
            return messages.size();
        }
        return 0;
    }

    @Override
    public long getMessageCountForUri(String uri) {
        validateInputParams(uri);

        List<MessageInfo> messages = getMessagesForUri(uri);
        if (null != messages && !messages.isEmpty()) {
            return messages.size();
        }
        return 0;
    }

    protected boolean isValidUri(final String uri) {
        return null != uri && SmsApiConstants.ALL_URIS.contains(uri);
    }

    protected void validateInputParams(String uri) {
        if (!isValidUri(uri)) {
            throw new IllegalArgumentException(uri + " is not a valid URI");
        }
    }
}
