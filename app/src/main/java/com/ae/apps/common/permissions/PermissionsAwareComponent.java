package com.ae.apps.common.permissions;

/**
 * A Component that is aware of Runtime Permissions. This would typically
 * be an Activity or a Fragment that depends on some runtime permissions to be
 * granted inorder to function
 */
public interface PermissionsAwareComponent {

    /**
     * Indicates the permissions that are required
     *
     * @return An array of permissions
     */
    String[] requiredPermissions();

    /**
     * Requesting permission to be made on the host as only an Activity or FragmentActivity
     * can do requestPermission
     */
    void requestForPermissions();

    /**
     * Callback that is invoked when permissions are granted
     */
    void onPermissionsGranted();

    /**
     * Callback that is invoked when permissions are denied
     */
    void onPermissionsDenied();
}
