package com.ae.apps.lib.sample.features.permissions;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;

import com.ae.apps.lib.permissions.PermissionsAwareComponent;
import com.ae.apps.lib.permissions.RuntimePermissionChecker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.ae.apps.lib.sample.R;

public class PermissionsSampleActivity extends AppCompatActivity
        implements PermissionsAwareComponent {

    private RuntimePermissionChecker mPermissionChecker;
    private static final int PERMISSION_CODE = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions_sample);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    }

    @Override
    public void onPermissionsGranted() {
        // show permission granted view
    }

    @Override
    public void onPermissionsDenied() {
        // Show permissions required view with button
    }
}
