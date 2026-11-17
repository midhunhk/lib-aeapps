package com.ae.apps.lib.api.contacts.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions;
import com.ae.apps.lib.api.contacts.types.ContactInfoOptions;
import com.ae.apps.lib.api.contacts.types.ContactsDataConsumer;
import com.ae.apps.lib.common.models.ContactInfo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Covers the initialization state machine, in particular the case where a second caller
 * arrives while the contacts are still being read.
 */
public class AbstractContactsApiGatewayTest {

    private static final ContactInfoFilterOptions OPTIONS = ContactInfoFilterOptions.of(false);
    private static final long TIMEOUT_SECONDS = 5;

    /**
     * A gateway whose read can be held open and made to fail, with callbacks delivered inline.
     */
    private static class TestGateway extends AbstractContactsApiGateway {
        final AtomicInteger readCount = new AtomicInteger();
        final CountDownLatch readStarted = new CountDownLatch(1);
        final CountDownLatch allowReadToFinish = new CountDownLatch(1);
        volatile RuntimeException failure;

        TestGateway(boolean blockRead) {
            if (!blockRead) {
                allowReadToFinish.countDown();
            }
        }

        @Override
        protected void readContacts(ContactInfoFilterOptions options) {
            readCount.incrementAndGet();
            readStarted.countDown();
            try {
                if (!allowReadToFinish.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Test timed out waiting to finish the read");
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            if (null != failure) {
                throw failure;
            }
            List<ContactInfo> read = new ArrayList<>();
            read.add(new ContactInfo());
            read.add(new ContactInfo());
            contacts = read;
        }

        @Override
        protected Executor createCallbackExecutor() {
            // No Looper in unit tests: run callbacks on the thread that finished the read
            return Runnable::run;
        }

        @Override
        public ContactInfo getRandomContact() {
            return null;
        }

        @Override
        public ContactInfo getContactInfo(String contactId, ContactInfoOptions options) {
            return null;
        }

        @Override
        public String getContactIdFromRawContact(String rawContactId) {
            return null;
        }

        @Override
        public String getContactIdFromAddress(String address) {
            return null;
        }
    }

    /**
     * Records what the gateway looked like at the moment the callback arrived.
     */
    private static class RecordingConsumer implements ContactsDataConsumer {
        final TestGateway gateway;
        final CountDownLatch done = new CountDownLatch(1);
        final AtomicReference<Long> countSeenInCallback = new AtomicReference<>();
        final AtomicReference<Throwable> errorInCallback = new AtomicReference<>();
        final AtomicReference<Exception> failure = new AtomicReference<>();

        RecordingConsumer(TestGateway gateway) {
            this.gateway = gateway;
        }

        @Override
        public void onContactsRead() {
            try {
                // This is what a real consumer does, and what used to throw IllegalStateException
                countSeenInCallback.set(gateway.getReadContactsCount());
            } catch (Throwable t) {
                errorInCallback.set(t);
            } finally {
                done.countDown();
            }
        }

        @Override
        public void onContactsReadFailed(Exception error) {
            failure.set(error);
            done.countDown();
        }

        void awaitCallback() throws InterruptedException {
            assertTrue("Callback was not delivered", done.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }
    }

    @Test
    public void initialize_reads_contacts_once() {
        TestGateway gateway = new TestGateway(false);

        gateway.initialize(OPTIONS);
        gateway.initialize(OPTIONS);

        assertEquals(1, gateway.readCount.get());
        assertEquals(2, gateway.getReadContactsCount());
    }

    @Test
    public void getters_throw_before_initialize() {
        TestGateway gateway = new TestGateway(false);

        assertThrows(IllegalStateException.class, gateway::getReadContactsCount);
        assertThrows(IllegalStateException.class, gateway::getAllContacts);
    }

    @Test
    public void second_initialize_waits_for_the_read_in_progress() throws Exception {
        final TestGateway gateway = new TestGateway(true);
        final CountDownLatch secondReturned = new CountDownLatch(1);
        final AtomicReference<Throwable> secondError = new AtomicReference<>();

        Thread first = new Thread(() -> gateway.initialize(OPTIONS));
        first.start();
        assertTrue(gateway.readStarted.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));

        Thread second = new Thread(() -> {
            try {
                gateway.initialize(OPTIONS);
                // Must be usable as soon as initialize returns
                gateway.getReadContactsCount();
            } catch (Throwable t) {
                secondError.set(t);
            } finally {
                secondReturned.countDown();
            }
        });
        second.start();

        // While the first read is still running the second caller must not have returned
        assertFalse(secondReturned.await(200, TimeUnit.MILLISECONDS));

        gateway.allowReadToFinish.countDown();
        assertTrue(secondReturned.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));
        first.join(TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS));

        assertNull(secondError.get());
        assertEquals(1, gateway.readCount.get());
    }

    @Test
    public void concurrent_initializeAsync_only_calls_back_when_ready() throws Exception {
        final TestGateway gateway = new TestGateway(true);
        RecordingConsumer firstConsumer = new RecordingConsumer(gateway);
        RecordingConsumer secondConsumer = new RecordingConsumer(gateway);

        gateway.initializeAsync(OPTIONS, firstConsumer);
        assertTrue(gateway.readStarted.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));
        // The second request arrives while the first is still reading
        gateway.initializeAsync(OPTIONS, secondConsumer);

        assertFalse("Callback delivered before the contacts were read",
                secondConsumer.done.await(200, TimeUnit.MILLISECONDS));

        gateway.allowReadToFinish.countDown();
        firstConsumer.awaitCallback();
        secondConsumer.awaitCallback();

        assertNull(firstConsumer.errorInCallback.get());
        assertNull(secondConsumer.errorInCallback.get());
        assertEquals(Long.valueOf(2), firstConsumer.countSeenInCallback.get());
        assertEquals(Long.valueOf(2), secondConsumer.countSeenInCallback.get());
        assertEquals(1, gateway.readCount.get());
    }

    @Test
    public void initializeAsync_after_ready_calls_back_without_reading_again() throws Exception {
        TestGateway gateway = new TestGateway(false);
        gateway.initialize(OPTIONS);
        RecordingConsumer consumer = new RecordingConsumer(gateway);

        gateway.initializeAsync(OPTIONS, consumer);
        consumer.awaitCallback();

        assertEquals(Long.valueOf(2), consumer.countSeenInCallback.get());
        assertEquals(1, gateway.readCount.get());
    }

    @Test
    public void failed_initialize_can_be_retried() {
        TestGateway gateway = new TestGateway(false);
        gateway.failure = new SecurityException("READ_CONTACTS denied");

        assertThrows(SecurityException.class, () -> gateway.initialize(OPTIONS));
        assertThrows(IllegalStateException.class, gateway::getReadContactsCount);

        gateway.failure = null;
        gateway.initialize(OPTIONS);

        assertEquals(2, gateway.getReadContactsCount());
        assertEquals(2, gateway.readCount.get());
    }

    @Test
    public void failed_initializeAsync_reports_the_failure() throws Exception {
        TestGateway gateway = new TestGateway(false);
        SecurityException denied = new SecurityException("READ_CONTACTS denied");
        gateway.failure = denied;
        RecordingConsumer consumer = new RecordingConsumer(gateway);

        gateway.initializeAsync(OPTIONS, consumer);
        consumer.awaitCallback();

        assertSame(denied, consumer.failure.get());
        assertNull("onContactsRead must not be called after a failure",
                consumer.countSeenInCallback.get());
        assertNull(consumer.errorInCallback.get());
    }

    @Test
    public void initializeAsync_accepts_a_null_consumer() throws Exception {
        TestGateway gateway = new TestGateway(false);

        gateway.initializeAsync(OPTIONS, null);

        // Wait for the background read by taking the same lock through a second call
        gateway.initialize(OPTIONS);
        assertEquals(2, gateway.getReadContactsCount());
    }
}
