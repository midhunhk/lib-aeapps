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


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class RuntimePermissionsCheckerTest {

    @Mock
    private PermissionsAwareComponent component;

    private final String[] singlePermission = {"PERMISSION ONE"};
    private final String[] multiplePermissions = {"PERMISSION ONE", "PERMISSION TWO", "PERMISSION THREE"};

    @Before
    public void setup() {
        component = Mockito.mock(MockPermissionsAwareActivity.class);
    }

    @Test
    public void shouldCreateObject_withSinglePermission(){
        Mockito.when(component.requiredPermissions()).thenReturn(singlePermission);
        RuntimePermissionChecker permissionChecker = RuntimePermissionChecker.newInstance(component);
        Assert.assertNotNull(permissionChecker);
    }

    @Test
    public void shouldCreateObject_withMultiplePermission(){
        Mockito.when(component.requiredPermissions()).thenReturn(multiplePermissions);
        RuntimePermissionChecker permissionChecker = RuntimePermissionChecker.newInstance(component);
        Assert.assertNotNull(permissionChecker);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentException_forNullParameter() {
        RuntimePermissionChecker.newInstance(null);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIllegalStateException_forNullPermissions() {
        Mockito.when(component.requiredPermissions()).thenReturn(null);
        RuntimePermissionChecker.newInstance(component);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIllegalStateException_forEmptyPermissions() {
        String[] empty = {};
        Mockito.when(component.requiredPermissions()).thenReturn(empty);
        RuntimePermissionChecker.newInstance(component);
    }


}
