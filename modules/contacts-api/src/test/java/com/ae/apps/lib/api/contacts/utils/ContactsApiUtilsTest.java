package com.ae.apps.lib.api.contacts.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;

import com.ae.apps.lib.common.models.ContactInfo;
import com.ae.apps.lib.common.models.PhoneNumberInfo;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ContactsApiUtilsTest {

    private static final String[] CONTACT_COLUMNS = {
            BaseColumns._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
    };

    private static final String[] PHONE_COLUMNS = {
            CommonDataKinds.Phone.NUMBER,
            CommonDataKinds.Phone.TYPE,
            CommonDataKinds.Phone.LABEL
    };

    /**
     * Creates a mocked Cursor backed by the rows supplied, positioned before the first row.
     * Only the Cursor methods used by ContactsApiUtils are stubbed.
     */
    private static Cursor cursorOf(final String[] columns, final String[]... rows) {
        final List<String> columnNames = Arrays.asList(columns);
        final int[] position = {-1};
        Cursor cursor = mock(Cursor.class);

        when(cursor.getCount()).thenReturn(rows.length);
        when(cursor.moveToNext()).thenAnswer(invocation -> ++position[0] < rows.length);
        when(cursor.moveToFirst()).thenAnswer(invocation -> {
            position[0] = 0;
            return rows.length > 0;
        });
        when(cursor.getColumnIndex(anyString()))
                .thenAnswer(invocation -> columnNames.indexOf(invocation.<String>getArgument(0)));
        when(cursor.getString(anyInt()))
                .thenAnswer(invocation -> rows[position[0]][invocation.<Integer>getArgument(0)]);
        when(cursor.getInt(anyInt())).thenAnswer(invocation -> {
            String value = rows[position[0]][invocation.<Integer>getArgument(0)];
            return null == value ? 0 : Integer.parseInt(value);
        });
        return cursor;
    }

    private static Cursor contactCursorAtFirstRow(String id, String name, String hasPhoneNumber) {
        Cursor cursor = cursorOf(CONTACT_COLUMNS, new String[]{id, name, hasPhoneNumber});
        cursor.moveToFirst();
        return cursor;
    }

    @Test
    public void createContactInfo_with_valid_cursor() {
        ContactInfo contactInfo = ContactsApiUtils.createContactInfo(
                contactCursorAtFirstRow("42", "Alice", "1"));

        assertEquals("42", contactInfo.getId());
        assertEquals("Alice", contactInfo.getName());
        assertTrue(contactInfo.hasPhoneNumber());
        assertNull(contactInfo.getPhoneNumbersList());
    }

    @Test
    public void createContactInfo_with_null_id_and_name() {
        ContactInfo contactInfo = ContactsApiUtils.createContactInfo(
                contactCursorAtFirstRow(null, null, "1"));

        assertNull(contactInfo.getId());
        assertNull(contactInfo.getName());
    }

    @Test
    public void createContactInfo_with_empty_id_and_name() {
        ContactInfo contactInfo = ContactsApiUtils.createContactInfo(
                contactCursorAtFirstRow("", "", "1"));

        assertEquals("", contactInfo.getId());
        assertEquals("", contactInfo.getName());
    }

    @Test
    public void createContactInfo_hasPhoneNumber_is_zero() {
        ContactInfo contactInfo = ContactsApiUtils.createContactInfo(
                contactCursorAtFirstRow("1", "Bob", "0"));

        assertFalse(contactInfo.hasPhoneNumber());
    }

    @Test
    public void createContactInfo_hasPhoneNumber_is_greater_than_one() {
        ContactInfo contactInfo = ContactsApiUtils.createContactInfo(
                contactCursorAtFirstRow("1", "Bob", "2"));

        assertTrue(contactInfo.hasPhoneNumber());
    }

    @Test
    public void createContactInfo_hasPhoneNumber_is_non_numeric() {
        Cursor cursor = contactCursorAtFirstRow("1", "Bob", "yes");

        assertThrows(NumberFormatException.class, () -> ContactsApiUtils.createContactInfo(cursor));
    }

    @Test
    public void createContactInfo_hasPhoneNumber_is_null() {
        Cursor cursor = contactCursorAtFirstRow("1", "Bob", null);

        assertThrows(NumberFormatException.class, () -> ContactsApiUtils.createContactInfo(cursor));
    }

    @Test
    public void createContactsList_with_null_cursor() {
        assertTrue(ContactsApiUtils.createContactsList(null, false).isEmpty());
    }

    @Test
    public void createContactsList_with_empty_cursor() {
        Cursor cursor = cursorOf(CONTACT_COLUMNS);

        assertTrue(ContactsApiUtils.createContactsList(cursor, false).isEmpty());
    }

    @Test
    public void createContactsList_includes_all_contacts_when_not_filtering() {
        Cursor cursor = cursorOf(CONTACT_COLUMNS,
                new String[]{"1", "Alice", "1"},
                new String[]{"2", "Bob", "0"},
                new String[]{"3", "Charlie", "1"});

        List<ContactInfo> contacts = ContactsApiUtils.createContactsList(cursor, false);

        assertEquals(3, contacts.size());
        assertEquals("1", contacts.get(0).getId());
        assertEquals("Bob", contacts.get(1).getName());
        assertEquals("3", contacts.get(2).getId());
    }

    @Test
    public void createContactsList_skips_contacts_without_phone_numbers_when_filtering() {
        Cursor cursor = cursorOf(CONTACT_COLUMNS,
                new String[]{"1", "Alice", "1"},
                new String[]{"2", "Bob", "0"},
                new String[]{"3", "Charlie", "1"});

        List<ContactInfo> contacts = ContactsApiUtils.createContactsList(cursor, true);

        assertEquals(2, contacts.size());
        assertEquals("Alice", contacts.get(0).getName());
        assertEquals("Charlie", contacts.get(1).getName());
    }

    @Test
    public void createPhoneNumberList_with_null_cursor() {
        assertTrue(ContactsApiUtils.createPhoneNumberList(null, null).isEmpty());
    }

    @Test
    public void createPhoneNumberList_reads_all_rows_and_closes_cursor() {
        Cursor cursor = cursorOf(PHONE_COLUMNS,
                new String[]{"+1 (555) 123-4567", "2", null},
                new String[]{"98112 37896", "1", null});

        List<PhoneNumberInfo> phoneNumbers = ContactsApiUtils.createPhoneNumberList(cursor, null);

        assertEquals(2, phoneNumbers.size());
        assertEquals("+1 (555) 123-4567", phoneNumbers.get(0).getPhoneNumber());
        assertEquals("15551234567", phoneNumbers.get(0).getUnformattedPhoneNumber());
        assertEquals("9811237896", phoneNumbers.get(1).getUnformattedPhoneNumber());
        verify(cursor).close();
    }

    @Test
    public void createPhoneNumberList_without_resources_leaves_phone_type_unset() {
        Cursor cursor = cursorOf(PHONE_COLUMNS, new String[]{"12345", "2", "Custom"});

        List<PhoneNumberInfo> phoneNumbers = ContactsApiUtils.createPhoneNumberList(cursor, null);

        assertNull(phoneNumbers.get(0).getPhoneType());
        // The label column is only read when resources are available to resolve it
        verify(cursor, never()).getString(2);
    }

    @Test
    public void createPhoneNumberList_with_null_phone_number() {
        Cursor cursor = cursorOf(PHONE_COLUMNS, new String[]{null, "2", null});

        List<PhoneNumberInfo> phoneNumbers = ContactsApiUtils.createPhoneNumberList(cursor, null);

        assertEquals(1, phoneNumbers.size());
        assertNull(phoneNumbers.get(0).getPhoneNumber());
        assertNull(phoneNumbers.get(0).getUnformattedPhoneNumber());
    }
}
