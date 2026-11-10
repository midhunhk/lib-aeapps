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

package com.ae.apps.lib.common.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by user on 6/24/2017.
 */

public class CommonUtilsTest {

    @Test
    public void testTruncateString() {
        assertEquals("", CommonUtils.truncateString(null));
        assertNotNull(CommonUtils.truncateString(null));

        assertEquals("Hello", CommonUtils.truncateString("Hello"));
        assertEquals("abcdefghijklmnopqrstuvwxyz...",
                CommonUtils.truncateString("abcdefghijklmnopqrstuvwxyz1234"));
    }

    @Test
    public void formatTimeStamp_validTimeStamp() {
        String timestamp = "1623349200000"; // June 10, 2021, 12:00:00 AM
        String pattern = "dd/MM/yyyy";
        String expected = "10/06/2021";
        String actual = CommonUtils.formatTimeStamp(timestamp, pattern);
        assertEquals(expected, actual);
    }
    @Test
    public void testFormatTimeStamp_NegativeTimestamp() {
        String timestamp = "-1623349200000"; // June 10, 1917, 12:00:00 AM
        String pattern = "dd/MM/yyyy";
        String expected = null;
        String actual = CommonUtils.formatTimeStamp(timestamp, pattern);
        assertEquals(expected, actual);
    }

    @Test
    public void truncateString() {
        String result = CommonUtils.truncateString(null);
        Assert.assertEquals("", result);

        result = CommonUtils.truncateString("ABCD");
        Assert.assertEquals("ABCD", result);

        result = CommonUtils.truncateString("The quick brown fox jumped over the lazy dog!");
        Assert.assertEquals("The quick brown fox jumped...", result);
    }

    @Test
    public void isPackageInstalled_checks_the_requested_package() throws Exception {
        Context context = mock(Context.class);
        PackageManager packageManager = mock(PackageManager.class);
        when(context.getPackageManager()).thenReturn(packageManager);
        when(context.getPackageName()).thenReturn("com.example.self");
        when(packageManager.getPackageInfo("com.example.installed", 0)).thenReturn(mock(PackageInfo.class));
        when(packageManager.getPackageInfo("com.example.missing", 0))
                .thenThrow(new PackageManager.NameNotFoundException());

        assertTrue(CommonUtils.isPackageInstalled("com.example.installed", context));
        assertFalse(CommonUtils.isPackageInstalled("com.example.missing", context));
    }
}
