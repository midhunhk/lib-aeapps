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

import android.database.Cursor;

import com.ae.apps.lib.common.models.MessageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods used by SmsApiGateway
 */
public class SmsApiUtils {

    /**
     * Create a list of MessageInfo from the cursor
     *
     * @param cursor cursor
     * @return list of messages
     */
    public static List<MessageInfo> createMessageInfoList(final Cursor cursor) {
        List<MessageInfo> messages = new ArrayList<>();
        MessageInfo messageInfo;
        do {
            messageInfo = createMessageInfo(cursor);
            messages.add(messageInfo);
        } while (cursor.moveToNext());
        return messages;
    }

    /**
     * Create a message info object from the cursor
     *
     * @param cursor the cursor
     * @return a MessageInfo
     */
    public static MessageInfo createMessageInfo(final Cursor cursor) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setId(cursor.getString(cursor.getColumnIndex(SmsApiConstants.COLUMN_ID)));
        messageInfo.setThreadId(cursor.getString(cursor.getColumnIndex(SmsApiConstants.COLUMN_THREAD_ID)));
        messageInfo.setAddress(cursor.getString(cursor.getColumnIndex(SmsApiConstants.COLUMN_ADDRESS)));
        messageInfo.setBody(cursor.getString(cursor.getColumnIndex(SmsApiConstants.COLUMN_BODY)));
        messageInfo.setPerson(cursor.getString(cursor.getColumnIndex(SmsApiConstants.COLUMN_PERSON)));
        //messageInfo.setDate(Long.parseLong(cursor.getString(cursor.getColumnIndex(SmsApiConstants.COLUMN_DATE))));
        return messageInfo;
    }
}
