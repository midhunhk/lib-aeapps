package com.ae.apps.lib.mock

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MockSmsApiUtilsTest {

    @Test
    @DisplayName("getMessageInfoList returns correct number of messages")
    fun getMessageInfoList_returnsCorrectCount() {
        val count = 5
        val messages = MockSmsApiUtils.getMessageInfoList(count)
        assertEquals(count, messages.size, "Should return $count messages")
    }

    @Test
    @DisplayName("getMessageInfoList returns empty list for zero count")
    fun getMessageInfoList_returnsEmptyForZeroCount() {
        val messages = MockSmsApiUtils.getMessageInfoList(0)
        assertTrue(messages.isEmpty(), "Should return an empty list for count 0")
    }

    @Test
    @DisplayName("getMessageInfoList returns empty list for negative count")
    fun getMessageInfoList_returnsEmptyForNegativeCount() {
        val messages = MockSmsApiUtils.getMessageInfoList(-5)
        assertTrue(messages.isEmpty(), "Should return an empty list for negative count")
    }

    @Test
    @DisplayName("getMessageInfoList populates messages with expected data and unique IDs")
    fun getMessageInfoList_populatesMessagesWithExpectedData() {
        val count = 3
        val messages = MockSmsApiUtils.getMessageInfoList(count)
        assertFalse(messages.isEmpty(), "Messages list should not be empty")

        messages.forEachIndexed { index, message ->
            // Check ID - this is the main thing that changes per iteration
            assertEquals(index.toString(), message.id, "Message ID should be the loop index $index")

            // Check other properties - they should be from the base `messageInfo` object
            assertEquals("22", message.person, "Message person should be '22' at index $index")
            assertEquals(100L, message.date, "Message date should be 100 at index $index")
            assertNull(message.protocol, "Message protocol should be null at index $index")
            assertEquals("sent", message.type, "Message type should be 'sent' at index $index")
        }
    }
}
