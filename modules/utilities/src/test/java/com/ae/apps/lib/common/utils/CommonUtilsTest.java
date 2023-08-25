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
import static org.junit.Assert.assertNotNull;

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
    public void formatTimeStamp() {
        String result = CommonUtils.formatTimeStamp("", "");
        Assert.assertNotNull(result);
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
}
