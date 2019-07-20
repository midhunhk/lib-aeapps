/*
 * Copyright 2019 Midhun Harikumar
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

package com.ae.apps.lib.common.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ContactUtilsTest {

    private List<String> phoneNumbers;

    @Before
    public void initPhoneNumbers() {
        phoneNumbers = new ArrayList<>();
        phoneNumbers.add("9811237896");
        phoneNumbers.add("9811237897");
        phoneNumbers.add("19811231234");
        phoneNumbers.add("9811234567");
    }

    @Test
    public void testCleanupPhoneNumberWithSpaces() {
        final String inputPhoneNumber = "981 123 7896";
        final String expectedPhoneNumber = "9811237896";
        assertEquals(expectedPhoneNumber, ContactUtils.cleanupPhoneNumber(inputPhoneNumber));
    }

    @Test
    public void testCleanupPhoneNumberWithSpecialCharacter() {
        final String inputPhoneNumber = "+1 981 123 7896";
        final String expectedPhoneNumber = "19811237896";
        assertEquals(expectedPhoneNumber, ContactUtils.cleanupPhoneNumber(inputPhoneNumber));
    }

    @Test
    public void testCleanupPhoneNumberWithHyphens() {
        final String inputPhoneNumber = "981-123-7896";
        final String expectedPhoneNumber = "9811237896";
        assertEquals(expectedPhoneNumber, ContactUtils.cleanupPhoneNumber(inputPhoneNumber));
    }

    @Test
    public void testCheckIfPhoneNumberExists() {
        assertTrue(ContactUtils.checkIfPhoneNumberExists(phoneNumbers, "981-123-7896"));
        assertTrue(ContactUtils.checkIfPhoneNumberExists(phoneNumbers, "+1 981 123 1234"));
        assertTrue(ContactUtils.checkIfPhoneNumberExists(phoneNumbers, "981 1234 567"));

        assertFalse(ContactUtils.checkIfPhoneNumberExists(phoneNumbers, "+1 211 237 896"));
    }
}
