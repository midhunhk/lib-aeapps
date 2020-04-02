package com.ae.apps.lib.sample.features.contacts;

import android.os.Build;
import android.os.Bundle;

import com.ae.apps.lib.api.contacts.ContactsApiGateway;
import com.ae.apps.lib.api.contacts.impl.ContactsApiGatewayImpl;
import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions;
import com.ae.apps.lib.common.models.ContactInfo;
import com.ae.apps.lib.multicontact.MultiContactBaseActivity;
import com.ae.apps.lib.sample.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MultiContactPicker extends MultiContactBaseActivity {

    private ContactsApiGateway contactsApiGateway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        customize();
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.content_multi_contact;
    }

    @Override
    public List<ContactInfo> contactsList() {
        // Assuming that the permissions are taken care by the invoking Activity/App
        contactsApiGateway = new ContactsApiGatewayImpl.Builder(this)
                .build();
        contactsApiGateway.initialize(ContactInfoFilterOptions.of(false));

        // Sort the contacts based on name
        List<ContactInfo> list = contactsApiGateway.getAllContacts();

        Collections.sort(list, new Comparator<ContactInfo>() {
            @Override
            public int compare(ContactInfo o1, ContactInfo o2) {

                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        return list;
    }

    private void customize(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cancelButton.setBackgroundTintList(getResources().getColorStateList(R.color.multi_contact_cancel_button_tint));
            continueButton.setBackgroundTintList(getResources().getColorStateList(R.color.multi_contact_continue_button_tint));
        } else {
            cancelButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            continueButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

}
