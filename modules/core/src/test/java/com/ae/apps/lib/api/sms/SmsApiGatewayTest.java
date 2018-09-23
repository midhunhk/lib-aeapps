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

import org.junit.Before;
import org.mockito.Mock;

public class SmsApiGatewayTest {

    @Mock
    private ContentResolver contentResolver;

    private SmsApiGateway apiGateway;

    @Before
    public void setUp() {
        System.out.println("setUp is called before each test");
        MockSmsApiGateway mockSmsApiGateway = new MockSmsApiGateway();
        mockSmsApiGateway.mContentResolver = contentResolver;
        apiGateway = mockSmsApiGateway;
    }
}
