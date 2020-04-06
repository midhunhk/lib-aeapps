package com.ae.apps.lib.sample.features.contacts;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

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
        if (null == contactsApiGateway) {
            long startTime = System.currentTimeMillis();
            contactsApiGateway = new ContactsApiGatewayImpl.Builder(this)
                    .build();
            contactsApiGateway.initialize(ContactInfoFilterOptions.of(true));
            long loadTime = System.currentTimeMillis() - startTime;
            Toast.makeText(this, "Contacts loaded in " + loadTime + " ms", Toast.LENGTH_SHORT).show();
        }

        List<ContactInfo> list = contactsApiGateway.getAllContacts();
        // Sort the contacts based on name
        Collections.sort(list, new Comparator<ContactInfo>() {
            @Override
            public int compare(ContactInfo o1, ContactInfo o2) {
                if (null == o1.getName() && null == o2.getName()) return 0;
                if (null == o1.getName()) return -1;
                if (null == o2.getName()) return 1;

                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        return list;
    }

    private void customize() {
        searchView.setBackgroundColor(getResources().getColor(R.color.color_slate_blue_dark));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            continueButton.setBackgroundTintList(
                    getResources().getColorStateList(R.color.multi_contact_continue_button_tint));
        } else {
            continueButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

}
