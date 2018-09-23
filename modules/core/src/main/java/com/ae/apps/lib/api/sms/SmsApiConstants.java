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

import java.util.Arrays;
import java.util.List;

public final class SmsApiConstants {

    private SmsApiConstants(){

    }

    /* public constants */
    public static final String URI_ALL = "content://sms/";
    public static final String URI_SENT = "content://sms/sent";
    public static final String URI_DRAFTS = "content://sms/draft";
    public static final String URI_INBOX = "content://sms/inbox";

    /* internal constants */
    static final List<String> ALL_URIS = Arrays.asList(URI_ALL, URI_DRAFTS, URI_INBOX, URI_SENT);

    static final String COLUMN_ID = "_id";
    static final String COLUMN_THREAD_ID = "thread_id";
    static final String COLUMN_ADDRESS = "address";
    static final String COLUMN_PERSON = "person";
    static final String COLUMN_BODY = "body";
    static final String COLUMN_DATE = "date";
    static final String COLUMN_TYPE = "type";
    static final String COLUMN_PROTOCOL = "protocol";

    static final String[] SMS_TABLE_PROJECTION = {
            COLUMN_ID, COLUMN_THREAD_ID, COLUMN_BODY,
            COLUMN_ADDRESS, COLUMN_PERSON, COLUMN_PROTOCOL};

    static final String SELECT_BY_ADDRESS = " address = ? ";
}
