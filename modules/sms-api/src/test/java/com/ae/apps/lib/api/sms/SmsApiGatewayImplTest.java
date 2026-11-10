package com.ae.apps.lib.api.sms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.ae.apps.lib.api.sms.impl.SmsApiGatewayImpl;
import com.ae.apps.lib.api.sms.utils.SmsApiConstants;
import com.ae.apps.lib.common.models.MessageInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.List;

public class SmsApiGatewayImplTest {

    private MockedStatic<Uri> mockedUri;
    private ContentResolver contentResolver;
    private SmsApiGateway gateway;

    @Before
    public void setUp() {
        mockedUri = mockStatic(Uri.class);
        mockedUri.when(() -> Uri.parse(anyString())).thenReturn(mock(Uri.class));

        Context context = mock(Context.class);
        contentResolver = mock(ContentResolver.class);
        when(context.getContentResolver()).thenReturn(contentResolver);
        gateway = new SmsApiGatewayImpl(context);
    }

    @After
    public void tearDown() {
        mockedUri.close();
    }

    @Test
    public void getMessagesForUri_closes_cursor_when_there_are_no_messages() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToFirst()).thenReturn(false);
        when(contentResolver.query(any(), any(), any(), any(), any())).thenReturn(cursor);

        List<MessageInfo> messages = gateway.getMessagesForUri(SmsApiConstants.URI_INBOX);

        assertTrue(messages.isEmpty());
        verify(cursor).close();
    }

    @Test
    public void getMessagesForContact_closes_cursor_when_there_are_no_messages() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToFirst()).thenReturn(false);
        when(contentResolver.query(any(), any(), any(), any(), any())).thenReturn(cursor);

        List<MessageInfo> messages = gateway.getMessagesForContact(SmsApiConstants.URI_INBOX, "7");

        assertTrue(messages.isEmpty());
        verify(cursor).close();
    }

    @Test
    public void getMessagesForUri_handles_a_null_cursor() {
        when(contentResolver.query(any(), any(), any(), any(), any())).thenReturn(null);

        assertEquals(0, gateway.getMessageCountForUri(SmsApiConstants.URI_ALL));
    }

    @Test
    public void projection_requests_every_column_that_is_mapped() {
        List<String> projection = Arrays.asList(SmsApiConstants.SMS_TABLE_PROJECTION);

        assertTrue(projection.contains(SmsApiConstants.COLUMN_DATE));
        assertTrue(projection.contains(SmsApiConstants.COLUMN_TYPE));
        assertTrue(projection.contains(SmsApiConstants.COLUMN_PROTOCOL));
    }
}
