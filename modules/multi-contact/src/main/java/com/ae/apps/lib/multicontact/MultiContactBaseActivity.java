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
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.ae.apps.R;
import com.ae.apps.lib.common.models.ContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Base Activity that supports picking multiple contacts from the address book.
 * This activity is based on ToolBarBaseActivity and leaves customizations to classes
 * that extend this activity.
 * <p>
 * <p>
 * Activities that extend this should implement the abstract methods and can be invoked with the below sample code
 * <p>
 * <pre>
 *     Intent multiContactPickerIntent = new Intent(getActivity(), MultiContactPickerActivity.class);
 *     startActivityForResult(multiContactPickerIntent, MULTI_CONTACT_PICKER_RESULT);
 * </pre>
 * <p>
 * Use a layout that contains the below layout in 'getLayoutResourceId()'
 * <pre>
 *  <include layout="@layout/layout_multi_contact_picker"/>
 * </pre>
 */
public abstract class MultiContactBaseActivity extends AppCompatActivity
        implements MultiContactInteractionListener {

    protected List<String> mSelectedContactIds;
    protected View mCancelButton;
    protected View mContinueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelectedContactIds = new ArrayList<>();

        initViews();

        setUpRecyclerView();
    }

    private void initViews() {
        mContinueButton = findViewById(R.id.btnContinueWithSelectedContacts);
        mCancelButton = findViewById(R.id.btnCancelMultiContactSelection);

        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedContactIds.size() > 0) {
                    onActivityComplete();
                } else {
                    Toast.makeText(MultiContactBaseActivity.this, R.string.str_multi_contact_validation, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
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
        for (String contactId : mSelectedContactIds) {
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
        mSelectedContactIds.add(contactId);
    }

    @Override
    public void onContactUnselected(String contactId) {
        mSelectedContactIds.remove(contactId);
    }

}
