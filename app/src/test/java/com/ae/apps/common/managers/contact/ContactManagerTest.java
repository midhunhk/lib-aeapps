package com.ae.apps.common.managers.contact;

import android.content.ContentResolver;
import android.content.res.Resources;

import com.ae.apps.common.managers.ContactManager;
import com.ae.apps.common.mock.MockContactService;

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

    private ContactManager contactManager;

    @Before
    public void setUp() {
        System.out.println("setUp is called before each test");
        contactManager = new ContactManager.Builder(contentResolver, resources)
                .addContactsWithPhoneNumbers(true)
                .build();
        contactManager.mContactService = new MockContactService();
    }

    @Test
    public void testGetAllContacts() {
        contactManager.fetchAllContacts();

        assertEquals(MockContactService.MOCK_CONTACTS_SIZE, contactManager.getAllContacts().size());
    }

    @Test
    public void testGetAllContactsAsync() {

        contactManager.fetchAllContactsAsync(new ContactDataConsumer() {
            @Override
            public void onContactsRead() {
                System.out.println("onContactsRead() callback");
                assertEquals(AbstractContactManager.STATUS.READY, contactManager.contactManagerStatus);
                assertEquals(MockContactService.MOCK_CONTACTS_SIZE, contactManager.getAllContacts().size());
            }
        });

    }

    @Test
    public void testGetContactPhoneDetails() {
        contactManager.fetchAllContacts();

        String contactId = contactManager.getAllContacts().get(0).getId();
        assertNotNull(contactManager.getContactWithPhoneDetails(contactId).getPhoneNumbersList());
    }

}