package com.ae.apps.common.managers;

import android.content.ContentResolver;
import android.content.res.Resources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for ContactManager
 */
public class ContactManagerTest {

    @Mock
    private Resources resources;

    @Mock
    private ContentResolver contentResolver;

    @Before
    public void setUp(){
        System.out.println("setUp is called before each test");
    }

    @Test
    public void testGetAllContacts() {
        ContactManager.Config config = new ContactManager.Config();
        config.contentResolver = contentResolver;
        config.resources = resources;
        config.useMockService = true;

        ContactManager contactManager = new ContactManager(config);
        assertEquals(AbstractContactManager.STATUS.READY, contactManager.contactManagerStatus);
        assertEquals(5, contactManager.getAllContacts().size());
    }

    @Test
    public void testGetAllContactsAsync() {
        ContactManager.Config config = new ContactManager.Config();
        config.contentResolver = contentResolver;
        config.resources = resources;
        config.readContactsAsync = true;
        config.useMockService = true;
        config.consumer = new AbstractContactManager.ContactsDataConsumer() {
            @Override
            public void onContactsRead() {
                System.out.println("onContactsRead() callback");
            }
        };

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

        assertEquals(AbstractContactManager.STATUS.READY, contactManager.contactManagerStatus);
        assertEquals(5, contactManager.getAllContacts().size());
    }

    @Test
    public void testContactManagerBuilder() {

        ContactManager contactManager = new ContactManager.Builder(contentResolver, resources)
                .addContactsWithPhoneNumbers(false)
                .readContactsAsync(false)
                /* set MockService as true only for unit testing */
                .useMockService(true)
                .build();

        assertNotNull(contactManager);
    }

}