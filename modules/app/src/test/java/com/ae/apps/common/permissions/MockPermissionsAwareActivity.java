package com.ae.apps.common.permissions;

import android.app.Activity;

public class MockPermissionsAwareActivity extends Activity implements PermissionsAwareComponent {
    @Override
    public String[] requiredPermissions() {
        return new String[0];
    }

    @Override
    public void requestForPermissions() {

    }

    @Override
    public void onPermissionsGranted() {

    }

    @Override
    public void onPermissionsDenied() {

    }
}
