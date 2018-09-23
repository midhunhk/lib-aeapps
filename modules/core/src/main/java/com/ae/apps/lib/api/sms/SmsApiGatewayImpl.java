/*
 * Copyright (c) 2015 Midhun Harikumar
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
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.ae.apps.lib.common.models.MessageInfo;

import java.util.ArrayList;
import java.util.List;

import static com.ae.apps.lib.api.sms.SmsApiConstants.SELECT_BY_ADDRESS;
import static com.ae.apps.lib.api.sms.SmsApiConstants.SMS_TABLE_PROJECTION;

/**
 * An implementation for SmsApiGateway
 *
 * @since 4.0
 */
public class SmsApiGatewayImpl extends AbstractSmsApiGateway {

    public SmsApiGatewayImpl(final Context context) {
        this.mContentResolver = context.getContentResolver();
    }

    @Override
    public List<MessageInfo> getMessagesForUri(String uri) {
        if (!checkIfValidUri(uri)) {
            throw new IllegalArgumentException(uri + " is not a valid URI");
        }
        Cursor cursor = mContentResolver.query(Uri.parse(uri),
                SMS_TABLE_PROJECTION,
                null, null, null);
        List<MessageInfo> messages = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            createMessageInfoList(cursor);
            cursor.close();
        }
        return messages;
    }

    @Override
    public List<MessageInfo> getMessagesForContact(String uri, String contactId) {
        if (!checkIfValidUri(uri)) {
            throw new IllegalArgumentException(uri + " is not a valid URI");
        }
        // TODO Revisit if lookup by address is correct
        // https://stackoverflow.com/a/27907211
        // convert contactId into address?
        Cursor cursor = mContentResolver.query(Uri.parse(uri),
                SMS_TABLE_PROJECTION,
                SELECT_BY_ADDRESS, new String[]{contactId}, null);
        List<MessageInfo> messages = new ArrayList<>();
        if (null != cursor && cursor.moveToFirst()) {
            messages = createMessageInfoList(cursor);
        }
        return messages;
    }

    private List<MessageInfo> createMessageInfoList(Cursor cursor) {
        List<MessageInfo> messages = new ArrayList<>();
        MessageInfo messageInfo;
        do {
            messageInfo = createMessageInfo(cursor);
            messages.add(messageInfo);
        } while (cursor.moveToNext());
        return messages;
    }

    private MessageInfo createMessageInfo(final Cursor cursor) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setId(cursor.getString(cursor.getColumnIndex(SmsApiConstants.COLUMN_ID)));
        messageInfo.setThreadId(cursor.getString(cursor.getColumnIndex(SmsApiConstants.COLUMN_THREAD_ID)));
        messageInfo.setAddress(cursor.getString(cursor.getColumnIndex(SmsApiConstants.COLUMN_ADDRESS)));
        messageInfo.setBody(cursor.getString(cursor.getColumnIndex(SmsApiConstants.COLUMN_BODY)));
        messageInfo.setPerson(cursor.getString(cursor.getColumnIndex(SmsApiConstants.COLUMN_PERSON)));
        messageInfo.setDate(Long.parseLong(cursor.getString(cursor.getColumnIndex(SmsApiConstants.COLUMN_DATE))));
        return messageInfo;
    }
}
