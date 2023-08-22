package com.ae.apps.lib.sample.features.contacts;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ae.apps.lib.api.contacts.ContactsApiGateway;
import com.ae.apps.lib.api.contacts.impl.ContactsApiGatewayImpl;
import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions;
import com.ae.apps.lib.api.contacts.types.ContactInfoOptions;
import com.ae.apps.lib.api.contacts.types.ContactsDataConsumer;
import com.ae.apps.lib.common.models.ContactInfo;
import com.ae.apps.lib.permissions.PermissionsAwareComponent;
import com.ae.apps.lib.permissions.RuntimePermissionChecker;
import com.ae.apps.lib.sample.R;

public class ContactsSampleActivity extends AppCompatActivity
        implements PermissionsAwareComponent, ContactsDataConsumer {

    private RuntimePermissionChecker permissionChecker;
    private static final int PERMISSION_CODE = 2000;

    private View requestLayout;
    private View contactsLayout;

    private ContactsApiGateway contactsApiGateway;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_sample);

        requestLayout = findViewById(R.id.layout_need_permissions);
        contactsLayout = findViewById(R.id.text_permissions_granted);

        View requestPermissionBtn = findViewById(R.id.btn_request_permissions);
        requestPermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestForPermissions();
            }
        });

        View refreshContactsBtn = findViewById(R.id.btn_refresh);
        refreshContactsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayContactInfo();
            }
        });

        permissionChecker = RuntimePermissionChecker.newInstance(this);
        permissionChecker.checkPermissions();
    }

    @Override
    public String[] requiredPermissions() {
        return new String[]{
                Manifest.permission.READ_CONTACTS
        };
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void requestForPermissions() {
        requestPermissions(requiredPermissions(), PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            permissionChecker.handlePermissionsResult(permissions, grantResults);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onPermissionsRequired() {
        // Show permissions required view with button
        showPermissionsRequiredView();
    }

    protected void showPermissionsRequiredView() {
        contactsLayout.setVisibility(View.GONE);
        requestLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPermissionsGranted() {
        // show permission granted view
        contactsLayout.setVisibility(View.VISIBLE);
        requestLayout.setVisibility(View.GONE);

        loadContacts();
    }

    private void loadContacts() {
        contactsApiGateway = new ContactsApiGatewayImpl.Builder(this)
                .build();
        startTime = System.currentTimeMillis();
        contactsApiGateway.initializeAsync(ContactInfoFilterOptions.of(false), this);
    }

    @Override
    public void onContactsRead() {
        long loadTime = System.currentTimeMillis() - startTime;
        Toast.makeText(this, "Contacts loaded in " + loadTime + " ms", Toast.LENGTH_SHORT).show();
        displayContactInfo();
    }

    private void displayContactInfo() {
        // Read a Random contact with picture and phone number details
        final ContactInfo randomContact = contactsApiGateway.getRandomContact();
        // Handle scenario when there are no contacts on the device
        if(null != randomContact) {
            ContactInfoOptions options = new ContactInfoOptions.Builder()
                    .includePhoneDetails(true)
                    .includeContactPicture(true)
                    .defaultContactPicture(com.ae.apps.lib.core.R.drawable.profile_icon_3)
                    .filterDuplicatePhoneNumbers(true)
                    .build();
            ContactInfo contactInfo = contactsApiGateway.getContactInfo(
                    randomContact.getId(),options);

            TextView contactName = findViewById(R.id.text_contact_name);
            contactName.setText(contactInfo.getName());

            ImageView profile = findViewById(R.id.img_contact_profile);
            profile.setImageBitmap(contactInfo.getPicture());

            if (null != contactInfo.getPhoneNumbersList() && contactInfo.getPhoneNumbersList().size() > 0) {
                TextView phoneNum1 = findViewById(R.id.text_phone_num1);
                phoneNum1.setText(contactInfo.getPhoneNumbersList().get(0).getPhoneNumber());
            }

        }
        TextView totalContacts = findViewById(R.id.text_total_contacts);
        String contactsRead = String.valueOf(contactsApiGateway.getReadContactsCount());
        totalContacts.setText(contactsRead + " contacts found");
    }

    @Override
    public void onPermissionsDenied() {
        // Show permissions required view with button
        showPermissionsRequiredView();
    }
}
