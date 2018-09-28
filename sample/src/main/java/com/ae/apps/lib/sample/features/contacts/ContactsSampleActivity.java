package com.ae.apps.lib.sample.features.contacts;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ae.apps.lib.api.contacts.ContactsApiGateway;
import com.ae.apps.lib.api.contacts.impl.ContactsApiGatewayImpl;
import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions;
import com.ae.apps.lib.api.contacts.types.ContactInfoOptions;
import com.ae.apps.lib.api.contacts.types.ContactsDataConsumer;
import com.ae.apps.lib.common.models.ContactInfo;
import com.ae.apps.lib.permissions.PermissionsAwareComponent;
import com.ae.apps.lib.permissions.RuntimePermissionChecker;
import com.ae.apps.lib.sample.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ContactsSampleActivity extends AppCompatActivity
        implements PermissionsAwareComponent, ContactsDataConsumer {

    private RuntimePermissionChecker mPermissionChecker;
    private static final int PERMISSION_CODE = 2000;

    private View mRequestLayout;
    private View mContactsLayout;

    private ContactsApiGateway mContactsApiGateway;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_sample);

        mRequestLayout = findViewById(R.id.layout_need_permissions);
        mContactsLayout = findViewById(R.id.text_permissions_granted);

        View requestPermissionBtn = findViewById(R.id.btn_request_permissions);
        requestPermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestForPermissions();
            }
        });

        mPermissionChecker = new RuntimePermissionChecker(this);
        mPermissionChecker.checkPermissions();
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
            mPermissionChecker.handlePermissionsResult(permissions, grantResults);
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
        mContactsLayout.setVisibility(View.GONE);
        mRequestLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPermissionsGranted() {
        // show permission granted view
        mContactsLayout.setVisibility(View.VISIBLE);
        mRequestLayout.setVisibility(View.GONE);

        loadContacts();
    }

    private void loadContacts() {
        mContactsApiGateway = new ContactsApiGatewayImpl.Builder(this)
                .build();
        startTime = System.currentTimeMillis();
        mContactsApiGateway.initializeAsync(ContactInfoFilterOptions.of(false), this);
    }

    @Override
    public void onContactsRead() {
        long loadTime = System.currentTimeMillis() - startTime;
        Toast.makeText(this, "Contacts loaded in " + loadTime + " ms", Toast.LENGTH_SHORT).show();
        displayContactInfo();
    }

    private void displayContactInfo() {
        // Read a Random contact with picture and phone number details
        ContactInfo contactInfo = mContactsApiGateway.getContactInfo(
                mContactsApiGateway.getRandomContact().getId(),
                ContactInfoOptions.of(true, true,
                        com.ae.apps.lib.R.drawable.profile_icon_3));

        TextView contactName = findViewById(R.id.text_contact_name);
        contactName.setText(contactInfo.getName());

        TextView totalContacts = findViewById(R.id.text_total_contacts);
        totalContacts.setText(mContactsApiGateway.getAllContacts().size() + " contacts found");

        ImageView profile = findViewById(R.id.img_contact_profile);
        profile.setImageBitmap(contactInfo.getPicture());

        TextView phoneNum1 = findViewById(R.id.text_phone_num1);
        phoneNum1.setText(contactInfo.getPhoneNumbersList().get(0).getPhoneNumber());

        TextView timesContacted = findViewById(R.id.text_times_contacted);
        timesContacted.setText("times contacted: " + contactInfo.getTimesContacted());
    }

    @Override
    public void onPermissionsDenied() {
        // Show permissions required view with button
        showPermissionsRequiredView();
    }
}
