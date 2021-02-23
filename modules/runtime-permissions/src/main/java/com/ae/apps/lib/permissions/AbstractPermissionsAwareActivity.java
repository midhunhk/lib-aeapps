package com.ae.apps.lib.permissions;

import android.annotation.TargetApi;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * A helper implementation of a PermissionAwareComponent and PermissionRequestableComponent
 */
public abstract class AbstractPermissionsAwareActivity
        extends AppCompatActivity
        implements PermissionsAwareComponent, PermissionsRequestableComponent {

    protected static final int PERMISSION_CHECK_REQUEST_CODE = 8000;
    protected final RuntimePermissionChecker permissionChecker =
            RuntimePermissionChecker.newInstance(this);

    protected void checkPermissions(){
        permissionChecker.checkPermissions();
    }

    @Override
    public void invokeRequestPermissions() {
        requestPermissionsForAPI();
    }

    @Override
    public void requestForPermissions() {
        showPermissionsRequiredView();
    }

    @Override
    public void onPermissionsRequired() {
        showPermissionsRequiredView();
    }

    @Override
    public void onPermissionsDenied() {
        showPermissionsRequiredView();
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void requestPermissionsForAPI(){
        requestPermissions(requiredPermissions(), PERMISSION_CHECK_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_CHECK_REQUEST_CODE){
            // The permission checker will handle the result and call the appropriate function
            permissionChecker.handlePermissionsResult(permissions, grantResults);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Display the content for NoAccess or explain the reason for the permissions
     * and a method to request for permissions
     */
    abstract void showPermissionsRequiredView();
}
