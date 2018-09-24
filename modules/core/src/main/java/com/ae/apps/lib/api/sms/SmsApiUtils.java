package com.ae.apps.lib.api.sms;

import android.database.Cursor;

import com.ae.apps.lib.common.models.MessageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods used by SmsApiGateway
 */
class SmsApiUtils {

    /**
     * Create a list of MessageInfo from the cursor
     *
     * @param cursor cursor
     * @return list of messages
     */
    static List<MessageInfo> createMessageInfoList(final Cursor cursor) {
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
