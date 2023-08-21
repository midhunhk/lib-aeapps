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
package com.ae.apps.lib.multicontact;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import androidx.fragment.app.Fragment;

/**
 * A @link{android.support.v4.app.Fragment} that provides support for selecting
 * from contacts list
 */
public abstract class ContactPickerFragment extends Fragment {

    private static final int CONTACT_PICKER_RESULT = 2020;

    /**
     * Start the contact picking process
     */
    public void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, CONTACT_PICKER_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == CONTACT_PICKER_RESULT) {
            onContactPicked(data.getData());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Implement this method to get callback for pickContact()
     *
     * @param data data from contact selection
     */
    public abstract void onContactPicked(Uri data);

}
