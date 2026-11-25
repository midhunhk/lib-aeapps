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

import com.ae.apps.lib.api.sms.utils.SmsApiConstants;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mockito.Mock;

public class SmsApiGatewayTest {

    @Mock
    private ContentResolver contentResolver;

    private SmsApiGateway apiGateway;

    @BeforeEach
    public void setUp() {
        MockSmsApiGateway mockSmsApiGateway = new MockSmsApiGateway();
        mockSmsApiGateway.setContentResolver(contentResolver);
        apiGateway = mockSmsApiGateway;
    }

    @Test
    public void shouldReturnMessageCountForUri() {
        long messageCount = apiGateway.getMessageCountForUri(SmsApiConstants.URI_ALL);
        Assertions.assertEquals(MockSmsApiGateway.MESSAGE_INFO_COUNT, messageCount);
    }

    @Test
    public void shouldReturnMessageCountForContactId() {
        long messageCount = apiGateway.getMessageCountForContact(SmsApiConstants.URI_ALL, "contact_id");
        Assertions.assertEquals(MockSmsApiGateway.MESSAGE_INFO_COUNT, messageCount);
    }

    @Test
    public void shouldThrowExceptionForInvalidUri_messageCountForUri() {
        assertThrows(IllegalArgumentException.class, () -> apiGateway.getMessageCountForUri("INVALID_URI"));
    }

    @Test
    public void shouldThrowExceptionForInvalidUri_messageCountForContact() {
        assertThrows(IllegalArgumentException.class, () -> apiGateway.getMessageCountForContact("INVALID_URI", "contact_id"));
    }
}
