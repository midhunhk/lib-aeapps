package com.ae.apps.common.permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.PermissionChecker;

/**
 * A helper class to check and handle RuntimePermissions.
 * Use this in an Activity or Fragment that implements {@link PermissionsAwareComponent}
 */
public class RuntimePermissionChecker {

    private PermissionsAwareComponent mComponent;
    private Context mContext;

    public RuntimePermissionChecker(PermissionsAwareComponent component) {
        if (null == component) {
            throw new IllegalArgumentException("component must not be null");
        }
        if (!(component instanceof Context)) {
            throw new IllegalArgumentException("component must extend Context");
        }
        if (component.requiredPermissions() == null || component.requiredPermissions().length == 0) {
            throw new IllegalStateException("At least one Permission should be returned by requiredPermissions()");
        }
        mContext = (Context) component;
        mComponent = component;
    }

    /**
     * Checks if the required permissions are already granted or asks the component
     * to make the request for permissions
     */
    public void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkAllPermissions(mComponent.requiredPermissions())) {
                mComponent.onPermissionsGranted();
            } else {
                mComponent.requestForPermissions();
            }
        } else {
            mComponent.onPermissionsGranted();
        }
    }

    /**
     * Since the callback for requestPermissions comes to the component,
     * this method needs to be explicitly invoked to check the result
     *
     * @param permissions  permissions that were requested
     * @param grantResults grant result of permissions
     */
    public void handlePermissionsResult(String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mComponent.onPermissionsGranted();
        } else {
            mComponent.onPermissionsDenied();
        }
    }

    private boolean checkAllPermissions(String[] permissions) {
        for (String permissionName : permissions) {
            if (PackageManager.PERMISSION_GRANTED !=
                    PermissionChecker.checkSelfPermission(mContext, permissionName)) {
                return false;
            }
        }
        return true;
    }

}
