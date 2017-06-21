package com.ae.apps.common.managers;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.ae.apps.common.vo.ContactVo;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ContactManager
 */
public class ContactManagerTest {

    private Cursor cursor = Mockito.mock(Cursor.class);
    private Resources resources = Mockito.mock(Resources.class);
    private ContentResolver contentResolver = Mockito.mock(ContentResolver.class);

    @Test
    public void testGetAllContacts() {
        ContactManager.Config config = new ContactManager.Config();
        config.contentResolver = contentResolver;
        config.resources = resources;

        mockMethods();

        ContactManager contactManager = new ContactManager(config);
        assertEquals(contactManager.contactManagerStatus, AbstractContactManager.STATUS.READY);
        assertEquals(contactManager.getAllContacts(), new ArrayList<ContactVo>());
    }

    @Test
    public void testGetAllContactsAsync() {
        ContactManager.Config config = new ContactManager.Config();
        config.contentResolver = contentResolver;
        config.resources = resources;
        config.readContactsAsync = true;
        config.consumer = new AbstractContactManager.ContactsDataConsumer() {
            @Override
            public void onContactsRead() {
                System.out.println("onContactsRead() callback");
            }
        };

        mockMethods();

        System.out.println("Creating ContactManager instance");
        ContactManager contactManager = new ContactManager(config);
        assertEquals(contactManager.contactManagerStatus, AbstractContactManager.STATUS.UNINITIALIZED);
        try {
            System.out.println("Force wait start for async method to complete");
            Thread.sleep(300);
            System.out.println("Force wait end for async method to complete");
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
        assertEquals(contactManager.contactManagerStatus, AbstractContactManager.STATUS.READY);
        assertEquals(contactManager.getAllContacts(), new ArrayList<ContactVo>());
    }

    @Test
    public void testContactManagerBuilder() {
        mockMethods();

        ContactManager contactManager = new ContactManager.Builder(contentResolver, resources)
                .addContactsWithPhoneNumbers(false)
                .readContactsAsync(false)
                .build();

        assertNotNull(contactManager);
    }

    private void mockMethods() {
        when(contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null))
                .thenReturn(cursor);
        when(cursor.getCount())
                .thenReturn(0);
    }
}
