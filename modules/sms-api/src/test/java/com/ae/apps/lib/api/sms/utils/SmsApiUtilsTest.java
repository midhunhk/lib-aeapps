package com.ae.apps.lib.api.sms.utils;

import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.database.Cursor;

import com.ae.apps.lib.common.models.MessageInfo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class SmsApiUtilsTest {

    @Mock
    Cursor mockCursor;

    // To mock the static method SmsApiUtils.createMessageInfo
    private MockedStatic<SmsApiUtils> mockedSmsApiUtils;

    @Before
    public void setUp() {
        // Initialize static mock for SmsApiUtils. The method createMessageInfo is in this class.
        // This must be done for each test method where the static mock is needed.
        // It's often cleaner to manage this per test method if not all tests need it.
        // For this example, we'll open it in setUp and close in tearDown.
        mockedSmsApiUtils = mockStatic(SmsApiUtils.class, CALLS_REAL_METHODS);

        // We want createMessageInfoList to call the real implementation,
        // but the createMessageInfo inside it to be mocked.
        // The CALLS_REAL_METHODS allows other static methods in SmsApiUtils
        // (like createMessageInfoList itself if it were called statically from elsewhere)
        // to call their real implementations by default.
        // We will then specifically mock createMessageInfo.
    }

    @After
    public void tearDown() {
        // It's crucial to close the static mock to avoid interference between tests.
        if (mockedSmsApiUtils != null) {
            mockedSmsApiUtils.close();
        }
    }

    @Test
    public void createMessageInfoList_withMultipleRows_returnsListOfMessages() {
        // Arrange
        MessageInfo mockMessage1 = new MessageInfo();
        mockMessage1.setId("1");
        MessageInfo mockMessage2 = new MessageInfo();
        mockMessage2.setId("2");

        // Simulate cursor having multiple rows
        // The do-while loop in createMessageInfoList processes the current row *before* moveToNext()
        // So, the first call to createMessageInfo happens with the cursor at its initial position.
        // If the cursor is non-empty, it's assumed to be positioned at the first valid row initially.
        when(mockCursor.moveToNext())
                .thenReturn(true) // For the second item
                .thenReturn(false); // To terminate the loop

        // Mock the static SmsApiUtils.createMessageInfo(cursor) call
        // We need to use thenAnswer or thenReturn with multiple values if createMessageInfo
        // is expected to return different objects based on cursor state (not shown here for simplicity)
        mockedSmsApiUtils.when(() -> SmsApiUtils.createMessageInfo(mockCursor))
                .thenReturn(mockMessage1) // First call
                .thenReturn(mockMessage2); // Second call

        // Act
        List<MessageInfo> result = SmsApiUtils.createMessageInfoList(mockCursor);

        // Assert
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertSame("First message should be the first mocked object", mockMessage1, result.get(0));
        Assert.assertSame("Second message should be the second mocked object", mockMessage2, result.get(1));

        // Verify createMessageInfo was called twice with the mockCursor
        mockedSmsApiUtils.verify(() -> SmsApiUtils.createMessageInfo(mockCursor), times(2));
        // Verify moveToNext was called twice
        verify(mockCursor, times(2)).moveToNext();
    }

    @Test
    public void createMessageInfoList_withSingleRow_returnsListWithOneMessage() {
        Assert.assertNotNull("mockCursor should be initialized", mockCursor); // Defensive check
        MessageInfo mockMessage = new MessageInfo();
        mockMessage.setId("single");

        when(mockCursor.moveToNext()).thenReturn(false);

        mockedSmsApiUtils.when(() -> SmsApiUtils.createMessageInfo(mockCursor))
                .thenReturn(mockMessage);

        List<MessageInfo> result = SmsApiUtils.createMessageInfoList(mockCursor);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertSame(mockMessage, result.get(0));

        mockedSmsApiUtils.verify(() -> SmsApiUtils.createMessageInfo(mockCursor), times(1));
        verify(mockCursor, times(1)).moveToNext();
    }

    @Test
    public void createMessageInfoList_withEmptyCursor_handlesDoWhileNature() {
        Assert.assertNotNull("mockCursor should be initialized", mockCursor); // Defensive check
        MessageInfo mockMessageForPotentiallyInvalidPosition = new MessageInfo();
        mockMessageForPotentiallyInvalidPosition.setId("from_invalid_pos");

        when(mockCursor.moveToNext()).thenReturn(false);
        mockedSmsApiUtils.when(() -> SmsApiUtils.createMessageInfo(mockCursor))
                .thenReturn(mockMessageForPotentiallyInvalidPosition);

        List<MessageInfo> result = SmsApiUtils.createMessageInfoList(mockCursor);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size()); // do-while executes createMessageInfo once
        Assert.assertSame(mockMessageForPotentiallyInvalidPosition, result.get(0));

        mockedSmsApiUtils.verify(() -> SmsApiUtils.createMessageInfo(mockCursor), times(1));
        verify(mockCursor, times(1)).moveToNext();
    }


    @Test
    public void createMessageInfoList_withNullCursor_throwsNullPointerException() {
        // This test does not use mockCursor, so its initialization state is irrelevant here.
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            SmsApiUtils.createMessageInfoList(null);
        });
        Assert.assertNotNull(exception);
    }

    // A helper for JUnit 4 to assert throws, similar to JUnit 5's assertThrows
    public static <T extends Throwable> T assertThrows(Class<T> expectedType, Runnable executable) {
        try {
            executable.run();
        } catch (Throwable actualException) {
            if (expectedType.isInstance(actualException)) {
                return (T) actualException;
            } else {
                String message = String.format("Expected %s to be thrown, but %s was thrown",
                        expectedType.getSimpleName(), actualException.getClass().getSimpleName());
                throw new AssertionError(message, actualException);
            }
        }
        String message = String.format("Expected %s to be thrown, but nothing was thrown.",
                expectedType.getSimpleName());
        throw new AssertionError(message);
    }


    @Test
    public void createMessageInfoList_whenCreateMessageInfoReturnsNull_addsNullToList() {
        Assert.assertNotNull("mockCursor should be initialized", mockCursor); // Defensive check
        when(mockCursor.moveToNext()).thenReturn(false);
        mockedSmsApiUtils.when(() -> SmsApiUtils.createMessageInfo(mockCursor)).thenReturn(null);

        List<MessageInfo> result = SmsApiUtils.createMessageInfoList(mockCursor);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertNull("The list should contain the null returned by createMessageInfo", result.get(0));

        mockedSmsApiUtils.verify(() -> SmsApiUtils.createMessageInfo(mockCursor), times(1));
    }

    @Test
    public void createMessageInfo_whenAllColumnsExist_populatesMessageInfoCorrectly() {
        // Arrange
        Assert.assertNotNull("mockCursor should be initialized", mockCursor);

        // Define expected values
        String expectedId = "sms_123";
        String expectedThreadId = "thread_45";
        String expectedAddress = "+1234567890";
        String expectedBody = "Hello there!";
        long expectedDate = System.currentTimeMillis();
        String expectedPerson = "ContactName";

        // Mock getColumnIndex for all columns to return valid indices
        when(mockCursor.getColumnIndex(SmsApiConstants.COLUMN_ID)).thenReturn(0);
        when(mockCursor.getColumnIndex(SmsApiConstants.COLUMN_THREAD_ID)).thenReturn(1);
        when(mockCursor.getColumnIndex(SmsApiConstants.COLUMN_ADDRESS)).thenReturn(2);
        when(mockCursor.getColumnIndex(SmsApiConstants.COLUMN_BODY)).thenReturn(3);
        when(mockCursor.getColumnIndex(SmsApiConstants.COLUMN_DATE)).thenReturn(4);
        when(mockCursor.getColumnIndex(SmsApiConstants.COLUMN_PERSON)).thenReturn(5);

        // Mock getString and getLong for those indices
        when(mockCursor.getString(0)).thenReturn(expectedId);
        when(mockCursor.getString(1)).thenReturn(expectedThreadId);
        when(mockCursor.getString(2)).thenReturn(expectedAddress);
        when(mockCursor.getString(3)).thenReturn(expectedBody);
        when(mockCursor.getLong(4)).thenReturn(expectedDate);
        when(mockCursor.getString(5)).thenReturn(expectedPerson);

        // Act
        MessageInfo result = SmsApiUtils.createMessageInfo(mockCursor);

        // Assert
        Assert.assertNotNull(result);
        Assert.assertEquals(expectedId, result.getId());
        Assert.assertEquals(expectedThreadId, result.getThreadId());
        Assert.assertEquals(expectedAddress, result.getAddress());
        Assert.assertEquals(expectedBody, result.getBody());
        Assert.assertEquals(expectedDate, result.getDate());
        Assert.assertEquals(expectedPerson, result.getPerson());

        // Verify interactions (optional, but good for completeness)
        verify(mockCursor).getColumnIndex(SmsApiConstants.COLUMN_ID);
        verify(mockCursor).getString(0); // Verifying with the actual index

        verify(mockCursor).getColumnIndex(SmsApiConstants.COLUMN_THREAD_ID);
        verify(mockCursor).getString(1);

        verify(mockCursor).getColumnIndex(SmsApiConstants.COLUMN_ADDRESS);
        verify(mockCursor).getString(2);

        verify(mockCursor).getColumnIndex(SmsApiConstants.COLUMN_BODY);
        verify(mockCursor).getString(3);

        verify(mockCursor).getColumnIndex(SmsApiConstants.COLUMN_DATE);
        verify(mockCursor).getLong(4);

        verify(mockCursor).getColumnIndex(SmsApiConstants.COLUMN_PERSON);
        verify(mockCursor).getString(5);
    }

    @Test
    public void createMessageInfo_whenSomeColumnsAreMissing_populatesAvailableFields() {
        // Arrange
        Assert.assertNotNull("mockCursor should be initialized", mockCursor);

        String expectedId = "sms_only_id";
        String expectedBody = "Body only";

        // Simulate only ID and BODY columns exist
        when(mockCursor.getColumnIndex(SmsApiConstants.COLUMN_ID)).thenReturn(0);
        when(mockCursor.getColumnIndex(SmsApiConstants.COLUMN_THREAD_ID)).thenReturn(-1); // Thread ID missing
        when(mockCursor.getColumnIndex(SmsApiConstants.COLUMN_ADDRESS)).thenReturn(-1);    // Address missing
        when(mockCursor.getColumnIndex(SmsApiConstants.COLUMN_BODY)).thenReturn(1);     // Body exists at index 1
        when(mockCursor.getColumnIndex(SmsApiConstants.COLUMN_DATE)).thenReturn(-1);        // Date missing
        when(mockCursor.getColumnIndex(SmsApiConstants.COLUMN_PERSON)).thenReturn(-1);     // Person missing

        // Mock getString for existing columns
        when(mockCursor.getString(0)).thenReturn(expectedId);
        when(mockCursor.getString(1)).thenReturn(expectedBody);

        // Act
        MessageInfo result = SmsApiUtils.createMessageInfo(mockCursor);

        // Assert
        Assert.assertNotNull(result);
        Assert.assertEquals(expectedId, result.getId());
        Assert.assertNull("ThreadId should be null as column was missing", result.getThreadId());
        Assert.assertNull("Address should be null as column was missing", result.getAddress());
        Assert.assertEquals(expectedBody, result.getBody());
        Assert.assertEquals(0, result.getDate()); // Default long value if not set
        Assert.assertNull("Person should be null as column was missing", result.getPerson());

        // Verify interactions: getString/getLong only called for existing columns
        verify(mockCursor).getString(0); // For ID
        verify(mockCursor).getString(1); // For Body

        // Verify getString/getLong NOT called for other indices (e.g., those that would be > 1 or negative)
        // More specifically, verify they weren't called with specific indices that weren't returned by getColumnIndex
        verify(mockCursor, never()).getString(2); // Example index for a missing column
        verify(mockCursor, never()).getLong(anyInt()); // Since Date was missing, getLong shouldn't be called if its index was -1
        // Note: If COLUMN_DATE returned say, index 2, but it was a string
        // then getLong(2) might be called and throw. Test that separately if needed.
    }

    @Test
    public void createMessageInfo_whenAllColumnsAreMissing_returnsMessageInfoWithNullOrDefaults() {
        // Arrange
        Assert.assertNotNull("mockCursor should be initialized", mockCursor);

        // Simulate all columns are missing
        when(mockCursor.getColumnIndex(anyString())).thenReturn(-1); // anyString() matches any column name

        // Act
        MessageInfo result = SmsApiUtils.createMessageInfo(mockCursor);

        // Assert
        Assert.assertNotNull(result);
        Assert.assertNull("Id should be null", result.getId());
        Assert.assertNull("ThreadId should be null", result.getThreadId());
        Assert.assertNull("Address should be null", result.getAddress());
        Assert.assertNull("Body should be null", result.getBody());
        Assert.assertEquals("Date should be default long", 0, result.getDate());
        Assert.assertNull("Person should be null", result.getPerson());

        // Verify no getString or getLong calls were made on the cursor
        verify(mockCursor, never()).getString(anyInt());
        verify(mockCursor, never()).getLong(anyInt());
    }

    @Test
    public void createMessageInfo_withNullCursor_throwsNullPointerException() {
        // Test current behavior. If you want to handle null cursor gracefully in createMessageInfo,
        // you'd add a null check and then change this test.
        NullPointerException exception = null;
        try {
            SmsApiUtils.createMessageInfo(null);
        } catch (NullPointerException e) {
            exception = e;
        }
        Assert.assertNotNull("Should throw NullPointerException for null cursor", exception);
    }
}
