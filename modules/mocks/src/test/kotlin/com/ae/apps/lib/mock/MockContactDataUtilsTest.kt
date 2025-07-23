package com.ae.apps.lib.mock

import android.content.Context
import android.content.res.Resources
import com.ae.apps.lib.common.models.ContactInfo
import com.ae.apps.lib.mocks.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`

class MockContactDataUtilsTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockResources: Resources

    private val sampleMockNames = arrayOf("Alice Wonderland", "Bob The Builder", "Charlie Brown")

    @BeforeEach
    fun setUp() {
        // Mock the context to return our mockResources
        `when`(mockContext.resources).thenReturn(mockResources)

        // Mock the resources to return our sample names when getStringArray is called
        // with the specific ID for mock_names.
        // Ensure R.array.mock_names resolves correctly in your test environment.
        // If R.array.mock_names is 0 or not found in pure JVM tests, you might need
        // to use a placeholder integer or use Robolectric.
        `when`(mockResources.getStringArray(R.array.mock_names)).thenReturn(sampleMockNames)
    }

    @Test
    @DisplayName("getMockContact should return a valid ContactInfo object")
    fun getMockContact_returnsValidContact() {
        // Act: Call the method under test
        val contactInfo: ContactInfo? = MockContactDataUtils.getMockContact(mockContext)

        // Assert: Basic checks for a valid contact
        assertNotNull(contactInfo, "ContactInfo object should not be null.")

        contactInfo?.let { // Use safe call for further assertions
            assertNotNull(it.name, "Contact name should not be null.")
            assertTrue(it.name.isNotBlank(), "Contact name should not be blank.")
            // Check if the name is one of the names we mocked
            assertTrue(sampleMockNames.contains(it.name), "Contact name should be one of the mock names.")

            assertNotNull(it.id, "Contact ID should not be null.")
            assertTrue(it.id.isNotBlank(), "Contact ID should not be blank.")

            assertTrue(it.hasPhoneNumber(), "Contact should have a phone number.")

            assertNotNull(it.phoneNumbersList, "Phone numbers list should not be null.")
            assertFalse(it.phoneNumbersList.isEmpty(), "Phone numbers list should not be empty.")

            // Verify the known phone numbers from your MockContactDataUtils implementation
            assertEquals(2, it.phoneNumbersList.size, "Should have 2 phone numbers as per implementation.")

            val firstPhoneNumber = it.phoneNumbersList[0]
            assertEquals("87 7781 6267", firstPhoneNumber.phoneNumber)
            assertEquals("8777816267", firstPhoneNumber.unformattedPhoneNumber)

            val secondPhoneNumber = it.phoneNumbersList[1]
            assertEquals("86 2343 6789", secondPhoneNumber.phoneNumber)
            assertEquals("8623436789", secondPhoneNumber.unformattedPhoneNumber)
        }
    }
}

