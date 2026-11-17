package com.ae.apps.lib.permissions;

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
    private final RuntimePermissionChecker permissionChecker = RuntimePermissionChecker.newInstance(this);

    /** @noinspection unused*/
    protected void checkPermissions(){
        permissionChecker.checkPermissions();
    }

    @Override
    public void invokeRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissionsForAPI();
        } else {
            // For versions before Android Build.VERSION_CODES_FULL.M, permissions are granted at install time.
            // If the permission is in the manifest, the app has it.
            // You can directly call the method that uses the permission.
            // No specific action needed here to *request* them.
            // The onPermissionsGranted() or equivalent method can be called directly.
            onPermissionsGranted();
        }
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

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.M)
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
    public abstract void showPermissionsRequiredView();
}
