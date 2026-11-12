package com.ae.apps.lib.api.contacts.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;

import com.ae.apps.lib.api.contacts.ContactsApiGateway;
import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions;
import com.ae.apps.lib.api.contacts.types.ContactInfoOptions;

import org.junit.Before;
import org.junit.Test;

public class ContactsApiGatewayImplTest {

    private ContentResolver contentResolver;
    private ContactsApiGateway gateway;

    @Before
    public void setUp() {
        Context context = mock(Context.class);
        contentResolver = mock(ContentResolver.class);
        when(context.getContentResolver()).thenReturn(contentResolver);
        when(context.getResources()).thenReturn(mock(Resources.class));
        gateway = new ContactsApiGatewayImpl.Builder(context).build();
    }

    private Cursor stubQueryWithEmptyCursor() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getCount()).thenReturn(0);
        when(contentResolver.query(any(), any(), any(), any(), any())).thenReturn(cursor);
        return cursor;
    }

    @Test
    public void initialize_closes_the_contacts_cursor() {
        Cursor cursor = stubQueryWithEmptyCursor();

        gateway.initialize(ContactInfoFilterOptions.of(false));

        verify(cursor).close();
        assertEquals(0, gateway.getReadContactsCount());
    }

    @Test
    public void initialize_handles_a_null_cursor() {
        when(contentResolver.query(any(), any(), any(), any(), any())).thenReturn(null);

        gateway.initialize(ContactInfoFilterOptions.of(false));

        assertEquals(0, gateway.getReadContactsCount());
    }

    @Test
    public void getContactInfo_with_options_returns_null_for_unknown_contact() {
        stubQueryWithEmptyCursor();
        gateway.initialize(ContactInfoFilterOptions.of(false));
        ContactInfoOptions options = new ContactInfoOptions.Builder()
                .includePhoneDetails(true)
                .includeContactPicture(true)
                .build();

        assertNull(gateway.getContactInfo("unknown-id", options));
    }

    @Test
    public void getContactIdFromRawContact_closes_cursor_when_no_row_matches() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.moveToFirst()).thenReturn(false);
        when(contentResolver.query(any(), any(), any(), any(), any())).thenReturn(cursor);

        assertEquals("0", gateway.getContactIdFromRawContact("12"));
        verify(cursor).close();
    }

    @Test
    public void getAllContacts_before_initialize_throws() {
        assertThrows(IllegalStateException.class, () -> gateway.getAllContacts());
    }
}
