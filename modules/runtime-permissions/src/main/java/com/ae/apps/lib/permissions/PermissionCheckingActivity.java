/*
 * Copyright 2018 Midhun Harikumar
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
 */

package com.ae.apps.lib.permissions;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class PermissionCheckingActivity extends AppCompatActivity
        implements PermissionsAwareComponent {

    private static final int PERMISSION_CODE = 2004;

    protected RuntimePermissionChecker mPermissionChecker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPermissionChecker = new RuntimePermissionChecker(this);
    }

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

    /**
     * Get the layout that represents Permission Denied Layout
     *
     * @return View
     */
    protected abstract View getPermissionDeniedLayout();

    /**
     * Get the view that represents Permission Granted Layout
     *
     * @return View
     */
    protected abstract View getPermissionGrantedLayout();

    @Override
    public void onPermissionsRequired() {
        if (null != getPermissionGrantedLayout()) {
            getPermissionGrantedLayout().setVisibility(View.GONE);
        }
        if (null != getPermissionDeniedLayout()) {
            getPermissionDeniedLayout().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPermissionsGranted() {
        if (null != getPermissionGrantedLayout()) {
            getPermissionGrantedLayout().setVisibility(View.VISIBLE);
        }
        if (null != getPermissionDeniedLayout()) {
            getPermissionDeniedLayout().setVisibility(View.GONE);
        }
    }

    @Override
    public void onPermissionsDenied() {
        onPermissionsRequired();
    }
}
