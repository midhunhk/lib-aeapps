/*
 * Copyright (c) 2015 Midhun Harikumar
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

/**
 * A Component that is aware of Runtime Permissions. This would typically
 * be an Activity or a Fragment that depends on some runtime permissions to be
 * granted inorder to function
 *
 * @since 3.0
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
