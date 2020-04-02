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

package com.ae.apps.lib.multicontact;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ae.apps.lib.common.models.ContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Base Activity that supports picking multiple contacts from the address book.
 * This activity is based on AppCompatActivity and leaves customizations to classes
 * that extend this activity.
 * <p>
 * <p>
 * The multi contact picker can be invoked with the below sample code where
 * <pre>MULTI_CONTACT_PICKER_RESULT</pre> is an int value defined in the calling code
 * <p>
 * <pre>
 *     Intent multiContactPickerIntent = new Intent(getActivity(), MultiContactPickerActivity.class);
 *     startActivityForResult(multiContactPickerIntent, MULTI_CONTACT_PICKER_RESULT);
 * </pre>
 * <p>
 *
 *  Retrieve the selected contactIds separated by @{MultiContactPickerConstants.CONTACT_ID_SEPARATOR}
 *  from the resulting intent
 *  <pre>
 *    if(requestCode == MULTI_CONTACT_PICKER_RESULT){
 *      data.getStringExtra(MultiContactPickerConstants.RESULT_CONTACT_IDS);
 *    }
 *  </pre>
 *
 *  The implementing class layout should include the below layout and return the layout resource id
 *  in the method @{getLayoutResourceId()}
 *  <pre>
 *    <include layout="@layout/layout_multi_contact_picker"/>
 *  </pre>
 */
public abstract class MultiContactBaseActivity extends AppCompatActivity
        implements MultiContactInteractionListener {

    protected List<String> selectedContactIds;
    protected View cancelButton;
    protected View continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedContactIds = new ArrayList<>();

        setContentView( getLayoutResourceId() );

        initViews();

        setUpRecyclerView();
    }

    /**
     * View used to `setContentView` before the views are initialized
     *
     * @return
     */
    public abstract @LayoutRes int getLayoutResourceId();

    private void initViews() {
        continueButton = findViewById(R.id.btnContinueWithSelectedContacts);
        cancelButton = findViewById(R.id.btnCancelMultiContactSelection);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedContactIds.size() > 0) {
                    onActivityComplete();
                } else {
                    Toast.makeText(MultiContactBaseActivity.this, R.string.str_multi_contact_validation, Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActivityCancelled();
            }
        });
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.list);

        if (null != recyclerView) {
            List<ContactInfo> contactsList = contactsList();
            MultiContactRecyclerViewAdapter mViewAdapter = new MultiContactRecyclerViewAdapter(contactsList, this);
            recyclerView.setAdapter(mViewAdapter);
        }
    }

    private void onActivityComplete() {
        Intent resultIntent = new Intent();
        String result = convertSelectedContactValues();
        resultIntent.putExtra(MultiContactPickerConstants.RESULT_CONTACT_IDS, result);

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @NonNull
    private String convertSelectedContactValues() {
        StringBuilder builder = new StringBuilder();
        for (String contactId : selectedContactIds) {
            builder.append(contactId)
                    .append(MultiContactPickerConstants.CONTACT_ID_SEPARATOR);
        }
        builder.deleteCharAt(builder.lastIndexOf(MultiContactPickerConstants.CONTACT_ID_SEPARATOR));
        return builder.toString();
    }

    private void onActivityCancelled() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
    }

    @Override
    public void onContactSelected(String contactId) {
        selectedContactIds.add(contactId);
    }

    @Override
    public void onContactUnselected(String contactId) {
        selectedContactIds.remove(contactId);
    }

}
