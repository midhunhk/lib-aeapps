package com.ae.apps.common.permissions;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.junit.Assert.*;

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
