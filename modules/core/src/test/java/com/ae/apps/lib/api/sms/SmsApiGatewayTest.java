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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class SmsApiGatewayTest {

    @Mock
    private ContentResolver contentResolver;

    private SmsApiGateway apiGateway;

    @Before
    public void setUp() {
        MockSmsApiGateway mockSmsApiGateway = new MockSmsApiGateway();
        mockSmsApiGateway.mContentResolver = contentResolver;
        apiGateway = mockSmsApiGateway;
    }

    @Test
    public void shouldReturnMessageCountForUri() {
        long messageCount = apiGateway.getMessageCountForUri(SmsApiConstants.URI_ALL);
        Assert.assertEquals(MockSmsApiGateway.MESSAGE_INFO_COUNT, messageCount);
    }

    @Test
    public void shouldReturnMessageCountForContactId() {
        long messageCount = apiGateway.getMessageCountForContact(SmsApiConstants.URI_ALL, "contact_id");
        Assert.assertEquals(MockSmsApiGateway.MESSAGE_INFO_COUNT, messageCount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForInvalidUri_messageCountForUri() {
        apiGateway.getMessageCountForUri("INVALID_URI");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForInvalidUri_messageCountForContact() {
        apiGateway.getMessageCountForContact("INVALID_URI", "contact_id");
    }
}
