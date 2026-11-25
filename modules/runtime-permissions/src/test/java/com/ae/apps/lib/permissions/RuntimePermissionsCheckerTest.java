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


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mockito.Mock;
import org.mockito.Mockito;

public class RuntimePermissionsCheckerTest {

    @Mock
    private PermissionsAwareComponent component;

    private final String[] singlePermission = {"PERMISSION ONE"};
    private final String[] multiplePermissions = {"PERMISSION ONE", "PERMISSION TWO", "PERMISSION THREE"};

    @BeforeEach
    public void setup() {
        component = Mockito.mock(MockPermissionsAwareActivity.class);
    }

    @Test
    public void shouldCreateObject_withSinglePermission(){
        Mockito.when(component.requiredPermissions()).thenReturn(singlePermission);
        RuntimePermissionChecker permissionChecker = RuntimePermissionChecker.newInstance(component);
        Assertions.assertNotNull(permissionChecker);
    }

    @Test
    public void shouldCreateObject_withMultiplePermission(){
        Mockito.when(component.requiredPermissions()).thenReturn(multiplePermissions);
        RuntimePermissionChecker permissionChecker = RuntimePermissionChecker.newInstance(component);
        Assertions.assertNotNull(permissionChecker);
    }

    @Test
    public void shouldThrowIllegalArgumentException_forNullParameter() {
        assertThrows(IllegalArgumentException.class, () -> RuntimePermissionChecker.newInstance(null));
    }

    @Test
    public void shouldThrowIllegalStateException_forNullPermissions() {
        Mockito.when(component.requiredPermissions()).thenReturn(null);
        assertThrows(IllegalStateException.class, () -> RuntimePermissionChecker.newInstance(component));
    }

    @Test
    public void shouldThrowIllegalStateException_forEmptyPermissions() {
        String[] empty = {};
        Mockito.when(component.requiredPermissions()).thenReturn(empty);
        assertThrows(IllegalStateException.class, () -> RuntimePermissionChecker.newInstance(component));
    }


}
