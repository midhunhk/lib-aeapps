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

import android.content.ContentResolver;

import com.ae.apps.lib.api.sms.impl.AbstractSmsApiGateway;
import com.ae.apps.lib.common.models.MessageInfo;
import com.ae.apps.lib.mock.MockSmsApiUtils;

import java.util.List;

public class MockSmsApiGateway extends AbstractSmsApiGateway {

    public static final int MESSAGE_INFO_COUNT = 5;

    public void setContentResoler(final ContentResolver contentResolver){
        this.contentResolver = contentResolver;
    }

    @Override
    public List<MessageInfo> getMessagesForUri(String uri) {
        return MockSmsApiUtils.getMessageInfoList(MESSAGE_INFO_COUNT);
    }

    @Override
    public List<MessageInfo> getMessagesForContact(String uri, String contactId) {
        return MockSmsApiUtils.getMessageInfoList(MESSAGE_INFO_COUNT);
    }

}
