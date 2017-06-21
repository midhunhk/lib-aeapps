package com.ae.apps.common.managers;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.ae.apps.common.vo.ContactVo;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

/**
 * Created by midhunhk on 6/22/2017.
 */

public class ContactManagerTest {

    @Test
    public void testGetAllContacts(){
        ContactManager.Config config = new ContactManager.Config();
        config.contentResolver = Mockito.mock(ContentResolver.class);
        config.resources = Mockito.mock(Resources.class);

        Cursor cursor = Mockito.mock(Cursor.class);
        when(config.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null))
                .thenReturn(cursor);
        when(cursor.getCount())
                .thenReturn(0);

        ContactManager contactManager = new ContactManager(config);
        assertEquals(contactManager.contactManagerStatus, AbstractContactManager.STATUS.READY);
        assertEquals(contactManager.getAllContacts(), new ArrayList<ContactVo>());
    }

    @Test
    public void testGetAllContactsAsync(){
        ContactManager.Config config = new ContactManager.Config();
        config.contentResolver = Mockito.mock(ContentResolver.class);
        config.resources = Mockito.mock(Resources.class);
        config.readContactsAsync = true;
        config.consumer = new AbstractContactManager.ContactsDataConsumer() {
            @Override
            public void onContactsRead() {
                System.out.println("onContactsRead() callback");
            }
        };

        Cursor cursor = Mockito.mock(Cursor.class);
        when(config.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null))
                .thenReturn(cursor);
        when(cursor.getCount())
                .thenReturn(0);

        System.out.println("Creating ContactManager instance");
        ContactManager contactManager = new ContactManager(config);
        assertEquals(contactManager.contactManagerStatus, AbstractContactManager.STATUS.UNINITIALIZED);
        try {
            System.out.println("Force wait start for async method to complete");
            Thread.sleep(100);
            System.out.println("Force wait end for async method to complete");
        } catch (InterruptedException ex){

        }
        assertEquals(contactManager.contactManagerStatus, AbstractContactManager.STATUS.READY);
        assertEquals(contactManager.getAllContacts(), new ArrayList<ContactVo>());
    }
}
