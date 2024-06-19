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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import com.ae.apps.lib.common.utils.CommonUtils;


/**
 * An abstract Fragment that can check for Runtime Permissions if your app target is above Lollipop
 * Use {@link RuntimePermissionChecker} if you need to extend any other component
 * The onCreateView method is used to check for the permission and returns an empty
 * FrameLayout to the Activity that hosts this fragment.
 * Based on the permission check result, the below methods would be invoked and can return
 * the appropriate View for that scenario
 *  {@link PermissionCheckingFragment#setupViewWithPermission} If permission is granted
 *  {@link PermissionCheckingFragment#setupViewWithoutPermission} If permission is not granted
 */
public abstract class PermissionCheckingFragment extends Fragment {

    protected LayoutInflater inflater;
    protected ViewGroup container;
    protected Context context;
    private ViewGroup mainContainer;
    private String[] permissionNames;
    private static final int PERMISSION_CHECK_REQUEST_CODE = 8000;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        mainContainer = CommonUtils.createParentLayout(context);

        permissionNames = getRequiredPermissions();

        checkPermissions(savedInstanceState);

        return mainContainer;
    }

    /**
     * Permissions that are required
     *
     * @return an array of required permissions
     */
    protected abstract String[] getRequiredPermissions();

    /**
     * Create the view to be shown when the permissions are not granted
     *
     * @return a view that will be added to the view group
     */
    protected abstract View setupViewWithoutPermission();

    /**
     * Create the view when permissions are granted
     *
     * @param savedInstanceState bundle
     * @return a view that will be added to the view group
     */
    protected abstract View setupViewWithPermission(Bundle savedInstanceState);

    /**
     * Invoked when the requested permissions are not granted
     *
     * @param requestCode requestCode
     * @param permissions list of permissions
     * @param grantResults grant results
     */
    protected abstract void onPermissionNotGranted(int requestCode, String[] permissions, int[] grantResults);

    private void onPermissionGranted(Bundle savedInstanceState){
        View randomContactView = setupViewWithPermission(savedInstanceState);
        mainContainer.removeAllViews();
        mainContainer.addView(randomContactView);
    }

    private void checkPermissions(Bundle savedInstanceState) {
        if(checkAllPermissions()){
            onPermissionGranted(savedInstanceState);
        } else {
            // else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), READ_CONTACTS))
            // show a no access view as read contacts permission is required
            View noAccessView = setupViewWithoutPermission();
            if(null != noAccessView) {
                mainContainer.addView(noAccessView);
            }
            requestForPermissions();
        }
    }

    private boolean checkAllPermissions(){
        for(String permissionName : permissionNames){
            if(PermissionChecker.PERMISSION_GRANTED != PermissionChecker.checkSelfPermission(context, permissionName)){
                return false;
            }
        }
        return true;
    }

    // TODO: https://developer.android.com/training/permissions/requesting#java
    protected void requestForPermissions() {
        requestPermissions(permissionNames, PERMISSION_CHECK_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CHECK_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionGranted(null);
                } else {
                    onPermissionNotGranted(requestCode, permissions, grantResults);
                }
                break;
            }
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
