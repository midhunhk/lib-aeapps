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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class RuntimePermissionsCheckerTest {

    @Mock
    private PermissionsAwareComponent component;

    private String[] singlePermission = {"PERMISSION ONE"};
    private String[] multiplePermissions = {"PERMISSION ONE", "PERMISSION TWO", "PERMISSION THREE"};

    @Before
    public void setup() {
        component = Mockito.mock(MockPermissionsAwareActivity.class);
    }

    @Test
    public void shouldCreateObject_withSinglePermission(){
        Mockito.when(component.requiredPermissions()).thenReturn(singlePermission);
        RuntimePermissionChecker permissionChecker = new RuntimePermissionChecker(component);
        Assert.assertNotNull(permissionChecker);
    }

    @Test
    public void shouldCreateObject_withMultiplePermission(){
        Mockito.when(component.requiredPermissions()).thenReturn(multiplePermissions);
        RuntimePermissionChecker permissionChecker = new RuntimePermissionChecker(component);
        Assert.assertNotNull(permissionChecker);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentException_forNullParameter() {
        new RuntimePermissionChecker(null);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIllegalStateException_forNullPermissions() {
        Mockito.when(component.requiredPermissions()).thenReturn(null);
        new RuntimePermissionChecker(component);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIllegalStateException_forEmptyPermissions() {
        String[] empty = {};
        Mockito.when(component.requiredPermissions()).thenReturn(empty);
        new RuntimePermissionChecker(component);
    }


}
