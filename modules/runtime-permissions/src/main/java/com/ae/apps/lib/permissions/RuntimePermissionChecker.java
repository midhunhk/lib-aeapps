/*
 * Copyright (c) 2018 Midhun Harikumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ae.apps.lib.permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.content.PermissionChecker;

/**
 * A helper class to check and handle RuntimePermissions.
 * Use this in an Activity or Fragment that implements {@link PermissionsAwareComponent}
 *
 * @since 3.0
 */
public class RuntimePermissionChecker {

    private PermissionsAwareComponent component;
    private Context context;

    /**
     * Initialize an instance of RuntimePermissionChecker
     *
     * @param component a component that is aware of Runtime Permissions
     */
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
        context = (Context) component;
        this.component = component;
    }

    /**
     * Checks if the required permissions are already granted or informs the component
     * that permissions are required
     */
    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkAllPermissions(component.requiredPermissions())) {
                component.onPermissionsGranted();
            } else {
                component.onPermissionsRequired();
            }
        } else {
            component.onPermissionsGranted();
        }
    }

    /**
     * Does the validation whether all permissions were granted
     *
     * Since the callback for requestPermissions comes to the component,
     * this method needs to be explicitly invoked to check the result
     *
     * @param permissions  permissions that were requested
     * @param grantResults grant result of permissions
     */
    public void handlePermissionsResult(String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && checkPermissionGrantResults(grantResults)) {
            component.onPermissionsGranted();
        } else {
            component.onPermissionsDenied();
        }
    }

    private boolean checkPermissionGrantResults(int[] grantResults) {
        for (int result : grantResults) {
            if (PackageManager.PERMISSION_GRANTED != result) {
                return false;
            }
        }
        return true;
    }

    private boolean checkAllPermissions(String[] permissions) {
        for (String permissionName : permissions) {
            if (PermissionChecker.PERMISSION_GRANTED !=
                    PermissionChecker.checkSelfPermission(context, permissionName)) {
                return false;
            }
        }
        return true;
    }

}
