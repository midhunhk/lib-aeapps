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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.ae.apps.lib.common.models.MessageInfo;

import java.util.ArrayList;
import java.util.List;

import static com.ae.apps.lib.api.sms.SmsApiConstants.SELECT_BY_PERSON;
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
        checkInputParams(uri);

        Cursor cursor = mContentResolver.query(Uri.parse(uri),
                SMS_TABLE_PROJECTION,
                null, null, null);
        List<MessageInfo> messages = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            SmsApiUtils.createMessageInfoList(cursor);
            cursor.close();
        }
        return messages;
    }

    @Override
    public List<MessageInfo> getMessagesForContact(String uri, String contactId) {
        checkInputParams(uri);

        Cursor cursor = mContentResolver.query(Uri.parse(uri),
                SMS_TABLE_PROJECTION,
                SELECT_BY_PERSON, new String[]{contactId}, null);
        List<MessageInfo> messages = new ArrayList<>();
        if (null != cursor && cursor.moveToFirst()) {
            messages = SmsApiUtils.createMessageInfoList(cursor);
        }
        return messages;
    }

}
