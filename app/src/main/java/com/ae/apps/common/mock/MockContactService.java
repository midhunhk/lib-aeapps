package com.ae.apps.common.mock;

import android.graphics.Bitmap;

import com.ae.apps.common.services.AeContactService;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.common.vo.MessageVo;
import com.ae.apps.common.vo.PhoneNumberVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock ContactService
 */
public class MockContactService implements AeContactService {

    private List<ContactVo> list;

    public MockContactService() {
        list = new ArrayList<>();
        list.add(MockContactDataUtils.getMockContact());
        list.add(MockContactDataUtils.getMockContact());
        list.add(MockContactDataUtils.getMockContact());
        list.add(MockContactDataUtils.getMockContact());
        list.add(MockContactDataUtils.getMockContact());
    }

    @Override
    public List<ContactVo> getContacts(boolean addContactsWithPhoneNumbers) {
        return list;
    }

    @Override
    public Bitmap getContactPhoto(String contactId) {
        return null;
    }

    @Override
    public List<PhoneNumberVo> getContactPhoneDetails(String contactId) {
        return list.get(0).getPhoneNumbersList();
    }

    @Override
    public List<MessageVo> getContactMessages(String contactId) {
        return null;
    }

    @Override
    public long getContactIdFromRawContactId(String rawContactId) {
        return 0;
    }

    @Override
    public String getContactIdFromAddress(String address) {
        return null;
    }
}
