package com.ae.apps.lib.api.sms;

import android.database.Cursor;

import com.ae.apps.lib.common.models.MessageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods used by SmsApiGateway
 */
class SmsApiUtils {

    static List<MessageInfo> createMessageInfoList(Cursor cursor) {
        List<MessageInfo> messages = new ArrayList<>();
        MessageInfo messageInfo;
        do {
            messageInfo = createMessageInfo(cursor);
            messages.add(messageInfo);
        } while (cursor.moveToNext());
        return messages;
    }

    static MessageInfo createMessageInfo(final Cursor cursor) {
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
