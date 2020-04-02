package com.ae.apps.lib.sample.features.contacts;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ae.apps.lib.api.contacts.types.ContactsDataConsumer;
import com.ae.apps.lib.multicontact.MultiContactPickerConstants;
import com.ae.apps.lib.permissions.PermissionsAwareComponent;
import com.ae.apps.lib.permissions.RuntimePermissionChecker;
import com.ae.apps.lib.sample.R;

public class MultiContactSampleActivity extends AppCompatActivity
        implements PermissionsAwareComponent, ContactsDataConsumer {

    private RuntimePermissionChecker permissionChecker;
    private static final int PERMISSION_CODE = 2000;
    private static final int MULTI_CONTACT_PICKER_RESULT = 2001;
    private View requestLayout;
    private View contactsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_contact_sample);

        requestLayout = findViewById(R.id.layout_need_permissions);
        contactsLayout = findViewById(R.id.text_permissions_granted);

        View requestPermissionBtn = findViewById(R.id.btn_request_permissions);
        requestPermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestForPermissions();
            }
        });

        View multiContactBtn = findViewById(R.id.btn_multi_contact);
        multiContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent multiContactPickerIntent = new Intent(getBaseContext(), MultiContactPicker.class);
                startActivityForResult(multiContactPickerIntent, MULTI_CONTACT_PICKER_RESULT);
            }
        });

        permissionChecker = new RuntimePermissionChecker(this);
        permissionChecker.checkPermissions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MULTI_CONTACT_PICKER_RESULT) {
                try {
                    String result = data.getStringExtra(MultiContactPickerConstants.RESULT_CONTACT_IDS);
                    TextView txtContactIds = findViewById(R.id.txt_contact_ids);
                    txtContactIds.setText(result);
                } catch (NullPointerException e) {
                    Toast.makeText(this, "NPE " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
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
    public void onPermissionsRequired() {
        showPermissionsRequiredView();
    }

    protected void showPermissionsRequiredView() {
        contactsLayout.setVisibility(View.GONE);
        requestLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPermissionsGranted() {
        contactsLayout.setVisibility(View.VISIBLE);
        requestLayout.setVisibility(View.GONE);
    }

    @Override
    public void onPermissionsDenied() {
        showPermissionsRequiredView();
    }

    @Override
    public void onContactsRead() {
        Toast.makeText(this, "onContactsRead", Toast.LENGTH_SHORT).show();
    }
}
