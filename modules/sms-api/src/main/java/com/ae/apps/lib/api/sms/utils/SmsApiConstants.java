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

package com.ae.apps.lib.api.sms.utils;

import java.util.Arrays;
import java.util.List;

public interface SmsApiConstants {

    /* public constants */
    String URI_ALL = "content://sms/";
    String URI_SENT = "content://sms/sent";
    String URI_DRAFTS = "content://sms/draft";
    String URI_INBOX = "content://sms/inbox";

    /* internal constants */
    List<String> ALL_URIS = Arrays.asList(URI_ALL, URI_DRAFTS, URI_INBOX, URI_SENT);

    String COLUMN_ID = "_id";
    String COLUMN_THREAD_ID = "thread_id";
    String COLUMN_ADDRESS = "address";
    String COLUMN_PERSON = "person";
    String COLUMN_BODY = "body";
    String COLUMN_DATE = "date";
    String COLUMN_TYPE = "type";
    String COLUMN_PROTOCOL = "protocol";

    String[] SMS_TABLE_PROJECTION = {
            COLUMN_ID, COLUMN_THREAD_ID, COLUMN_BODY,
            COLUMN_ADDRESS, COLUMN_PERSON, COLUMN_PROTOCOL};

    String SELECT_BY_PERSON = " person = ? ";
}
