package com.ae.apps.common.managers.contact;

import android.graphics.Bitmap;

import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.common.vo.MessageVo;

import java.util.List;

/**
 * An implementation of ContactManager that proxies all calls to an underling implementation
 * of ContactManager.
 *
 * Note: mContactManager must be initialized before using any of its methods
 */
public class ContactManagerProxy implements AeContactManager {
    protected AeContactManager mContactManager;

    @Override
    public void fetchAllContacts() {
        mContactManager.fetchAllContacts();
    }

    @Override
    public void fetchAllContactsAsync(ContactDataConsumer consumer) {
        mContactManager.fetchAllContactsAsync(consumer);
    }

    @Override
    public List<ContactVo> getAllContacts() throws UnsupportedOperationException {
        return mContactManager.getAllContacts();
    }

    @Override
    public int getTotalContactCount() {
        return mContactManager.getTotalContactCount();
    }

    @Override
    public ContactVo getRandomContact() {
        return mContactManager.getRandomContact();
    }

    @Override
    public ContactVo getContactWithPhoneDetails(String contactId) {
        return mContactManager.getContactWithPhoneDetails(contactId);
    }

    @Override
    public Bitmap getContactPhoto(String contactId, Bitmap defaultImage) {
        return mContactManager.getContactPhoto(contactId, defaultImage);
    }

    @Override
    public ContactVo getContactInfo(String contactId) {
        return mContactManager.getContactInfo(contactId);
    }

    @Override
    public List<MessageVo> getContactMessages(String contactId) {
        return mContactManager.getContactMessages(contactId);
    }

    @Override
    public long getContactIdFromRawContactId(String rawContactId) {
        return mContactManager.getContactIdFromRawContactId(rawContactId);
    }

    @Override
    public String getContactIdFromAddress(String address) {
        return mContactManager.getContactIdFromAddress(address);
    }

    @Override
    public Bitmap getContactPhoto(String contactId) {
        return mContactManager.getContactPhoto(contactId);
    }
}
