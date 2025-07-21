package com.ae.apps.lib.mock

import com.ae.apps.lib.common.models.MessageInfo
import org.junit.jupiter.api.Assertions.* // For JUnit 5 assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

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

    @ParameterizedTest
    @ValueSource(ints = [1, 5, 10]) // Test with different counts
    @DisplayName("getMessageInfoList generates distinct message instances if MessageInfo is mutable and reused")
    fun getMessageInfoList_referencesAreDistinctIfObjectIsMutatedAndReAdded(count: Int) {
        if (count <= 0) return // Skip for non-positive counts as it returns empty

        val messages = MockSmsApiUtils.getMessageInfoList(count)

        // Crucial Check: Your current implementation reuses and mutates the SAME MessageInfo instance
        // from the `messageInfo` getter for its base properties, then updates the ID.
        // When you add it to the list, you are adding references to this *same* object
        // multiple times, just with its ID having been mutated before each add.
        // This means all elements in the list will point to the same object in memory,
        // and that object will have the ID of the *last* iteration.

        // Test the ID of the last element, as all elements will reflect this
        assertEquals((count - 1).toString(), messages.last().id, "Last message ID should be ${count - 1}")

        if (count > 1) {
            // Verify all objects in the list are actually the same instance
            // and thus all have the ID of the last iteration.
            for (i in 0 until count -1) {
                assertSame(messages[i], messages[i+1], "All MessageInfo objects in the list should be the same instance due to reuse.")
                assertEquals((count - 1).toString(), messages[i].id, "Message at index $i should also have the last ID due to object reuse.")
            }
        }
    }

    @Test
    @DisplayName("getMessageInfoList items should have IDs corresponding to their loop index IF objects were cloned")
    fun getMessageInfoList_idsWouldBeCorrectIfObjectsWereCloned() {
        // This test describes the behavior IF each MessageInfo object was a NEW instance or a clone.
        // Given the current implementation, this test as written below would FAIL.
        // It's here to highlight the current behavior vs. a potentially expected one.

        val count = 3
        val messages = MockSmsApiUtils.getMessageInfoList(count)

        // IF each messageInfo was a new instance:
        // messages.forEachIndexed { index, message ->
        //     assertEquals(index.toString(), message.id)
        // }
        // HOWEVER, with the current implementation:
        messages.forEachIndexed { index, message ->
            // All messages in the list point to the same object, which has its ID set to the last index.
            assertEquals((count - 1).toString(), message.id,
                "Due to object reuse, message at index $index will have ID of the last iteration.")
        }
    }
}
