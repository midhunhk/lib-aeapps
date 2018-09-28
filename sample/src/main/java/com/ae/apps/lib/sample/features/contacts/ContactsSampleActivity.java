package com.ae.apps.lib.sample.features.contacts;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.ae.apps.lib.permissions.PermissionsAwareComponent;
import com.ae.apps.lib.permissions.RuntimePermissionChecker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ae.apps.lib.sample.R;

public class ContactsSampleActivity extends AppCompatActivity
        implements PermissionsAwareComponent {

    private RuntimePermissionChecker mPermissionChecker;
    private static final int PERMISSION_CODE = 2000;

    private View mRequestLayout;
    private View mContactsLayout;

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
    }

    @Override
    public void onPermissionsDenied() {
        // Show permissions required view with button
        showPermissionsRequiredView();
    }
}
