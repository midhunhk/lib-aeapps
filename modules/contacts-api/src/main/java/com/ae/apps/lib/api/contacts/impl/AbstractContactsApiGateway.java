/*
 * Copyright (c) 2018 Midhun Harikumar
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

package com.ae.apps.lib.api.contacts.impl;

import static com.ae.apps.lib.api.contacts.utils.ContactsApiConstants.SELECT_WITH_CONTACT_ID;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ae.apps.lib.api.contacts.ContactsApiGateway;
import com.ae.apps.lib.api.contacts.types.ContactInfoFilterOptions;
import com.ae.apps.lib.api.contacts.types.ContactsDataConsumer;
import com.ae.apps.lib.api.contacts.utils.ContactsApiUtils;
import com.ae.apps.lib.common.models.ContactInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * An abstract implementation of ContactsApiGateway
 */
public abstract class AbstractContactsApiGateway implements ContactsApiGateway {

    private static final String TAG = "ContactsApiGateway";

    protected enum STATE {
        UNINITIALIZED, INITIALIZING, READY
    }

    protected Resources resources;
    protected ContentResolver contentResolver;
    protected volatile transient STATE gatewayState = STATE.UNINITIALIZED;
    protected volatile transient List<ContactInfo> contacts = Collections.emptyList();

    /**
     * Guards the state transitions so that only one thread reads contacts at a time and
     * every other caller waits for that read to finish.
     */
    private final Object initializationLock = new Object();

    /**
     * Reads the contacts unless they have already been read.
     * <p>
     * Safe to call from several threads: when a read is already in progress the caller blocks
     * until it finishes, so the gateway is always ready when this method returns normally.
     * If reading fails the exception is propagated and the gateway goes back to being
     * uninitialized, so that a later call can try again.
     */
    @Override
    public void initialize(final ContactInfoFilterOptions options) {
        synchronized (initializationLock) {
            if (STATE.READY == gatewayState) {
                return;
            }
            gatewayState = STATE.INITIALIZING;
            try {
                readContacts(options);
                gatewayState = STATE.READY;
            } finally {
                if (STATE.READY != gatewayState) {
                    // The read failed; allow a retry rather than staying stuck in INITIALIZING
                    gatewayState = STATE.UNINITIALIZED;
                }
            }
        }
    }

    protected abstract void readContacts(final ContactInfoFilterOptions options);

    /**
     * Reads the contacts on a background thread and then notifies the consumer.
     * <p>
     * {@link ContactsDataConsumer#onContactsRead()} is only invoked once the gateway is ready,
     * including when another call was already reading the contacts. If reading fails,
     * {@link ContactsDataConsumer#onContactsReadFailed(Exception)} is invoked instead.
     */
    @Override
    public void initializeAsync(final ContactInfoFilterOptions options,
                                final ContactsDataConsumer dataConsumer) {
        final Executor callbackExecutor = createCallbackExecutor();
        new Thread(() -> {
            try {
                initialize(options);
            } catch (final RuntimeException e) {
                Log.e(TAG, "Unable to read contacts", e);
                if (null != dataConsumer) {
                    callbackExecutor.execute(() -> dataConsumer.onContactsReadFailed(e));
                }
                return;
            }

            if (null != dataConsumer) {
                callbackExecutor.execute(dataConsumer::onContactsRead);
            }
        }, "ContactsApiGateway-init").start();
    }

    /**
     * Creates the executor used to deliver {@link ContactsDataConsumer} callbacks. Invoked on the
     * thread that calls {@link #initializeAsync}; callbacks are delivered on that thread if it
     * has a Looper, otherwise on the main thread.
     */
    protected Executor createCallbackExecutor() {
        final Looper callerLooper = Looper.myLooper();
        final Handler handler = new Handler(null != callerLooper ? callerLooper : Looper.getMainLooper());
        return handler::post;
    }

    @Override
    public List<ContactInfo> getAllContacts() throws IllegalStateException {
        throwExceptionIfNotInitialized();

        return new ArrayList<>(contacts);
    }

    @Override
    public long getReadContactsCount() {
        throwExceptionIfNotInitialized();

        return contacts.size();
    }

    protected void throwExceptionIfNotInitialized() {
        if (checkIfApiNotInitialized()) {
            throw new IllegalStateException("ContactsApiGateway not initialized");
        }
    }

    /**
     * Checks if the contact has more than 1 phone numbers associated with it
     *
     * @param contactInfo a valid contactInfo object
     * @return true if the contact has multiple phone numbers, false otherwise
     */
    protected boolean hasMultiplePhoneNumbers(ContactInfo contactInfo) {
        return contactInfo.hasPhoneNumber()
                && null != contactInfo.getPhoneNumbersList()
                && contactInfo.getPhoneNumbersList().size() > 1;
    }

    protected void updateWithPhoneDetails(final ContactInfo contactInfo) {
        if (contactInfo.getPhoneNumbersList() == null && contactInfo.hasPhoneNumber()) {
            Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, SELECT_WITH_CONTACT_ID, new String[]{contactInfo.getId()},
                    null);
            contactInfo.setPhoneNumbersList(ContactsApiUtils.createPhoneNumberList(phoneCursor, resources));
        }
    }

    @Nullable
    protected Bitmap getContactPicture(final String contactId) {
        long contactIdLong = Long.parseLong(contactId);
        Uri contactPhotoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactIdLong);

        InputStream photoDataStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver,
                contactPhotoUri);

        if (null == photoDataStream) {
            return null;
        }
        try {
            return BitmapFactory.decodeStream(photoDataStream);
        } finally {
            try {
                photoDataStream.close();
            } catch (IOException e) {
                // Nothing useful to do if the stream fails to close
            }
        }
    }

    @Override
    public ContactInfo getContactInfo(String s) {
        return null;
    }

    protected boolean checkIfApiInitialized() {
        return gatewayState == STATE.READY;
    }

    protected boolean checkIfApiNotInitialized() {
        return !checkIfApiInitialized();
    }
}
