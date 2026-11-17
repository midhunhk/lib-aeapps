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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

/**
 * A @link{android.support.v4.app.Fragment} that provides support for selecting
 * from contacts list
 */
public abstract class ContactPickerFragment extends Fragment {

    private final ActivityResultLauncher<Intent> contactPickerResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    final Intent data = result.getData();
                    if(data != null) {
                        onContactPicked(data.getData());
                    }
                }
            }
    );

    /**
     * Start the contact picking process
     * @noinspection unused
     */
    public void pickContact() {
        final Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        contactPickerResultLauncher.launch(intent);
    }

    /**
     * Implement this method to get callback for pickContact()
     *
     * @param data data from contact selection
     */
    public abstract void onContactPicked(Uri data);

}
