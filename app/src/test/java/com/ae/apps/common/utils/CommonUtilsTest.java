package com.ae.apps.common.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

}
