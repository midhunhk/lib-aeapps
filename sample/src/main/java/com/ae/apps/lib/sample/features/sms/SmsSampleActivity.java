/*
 * Copyright 2018 Midhun Harikumar
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
 */

package com.ae.apps.lib.sample.features.sms;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ae.apps.lib.api.sms.SmsApiConstants;
import com.ae.apps.lib.api.sms.SmsApiGateway;
import com.ae.apps.lib.api.sms.SmsApiGatewayImpl;
import com.ae.apps.lib.permissions.PermissionCheckingActivity;
import com.ae.apps.lib.permissions.RuntimePermissionChecker;
import com.ae.apps.lib.sample.R;

/**
 * Sample for Sms Api
 */
public class SmsSampleActivity extends PermissionCheckingActivity {

    private SmsApiGateway mSmsApiGateway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_sample);

        Button requestBtn = findViewById(R.id.btn_request_permissions);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestForPermissions();
            }
        });

        mPermissionChecker.checkPermissions();
    }

    @Override
    public String[] requiredPermissions() {
        return new String[]{
                Manifest.permission.READ_SMS
        };
    }

    @Override
    public void onPermissionsGranted() {
        super.onPermissionsGranted();

        loadSmsData();
    }

    private void loadSmsData() {
        mSmsApiGateway = new SmsApiGatewayImpl(this);
        long allMessages = mSmsApiGateway.getMessageCountForUri(SmsApiConstants.URI_ALL);
        long draftMessages = mSmsApiGateway.getMessageCountForUri(SmsApiConstants.URI_DRAFTS);
        long inboxMessages = mSmsApiGateway.getMessageCountForUri(SmsApiConstants.URI_INBOX);
        long sentMessages = mSmsApiGateway.getMessageCountForUri(SmsApiConstants.URI_SENT);

        TextView textAllMessages = findViewById(R.id.textSmsAllCount);
        textAllMessages.setText(getResources().getString(R.string.str_sms_count_all, String.valueOf(allMessages)));
        TextView textDraftsMessages = findViewById(R.id.textSmsDraftsCount);
        textDraftsMessages.setText(getResources().getString(R.string.str_sms_count_all, String.valueOf(draftMessages)));
        TextView textSentMessages = findViewById(R.id.textSmsSentCount);
        textSentMessages.setText(getResources().getString(R.string.str_sms_count_all, String.valueOf(sentMessages)));
        TextView textRecvMessages = findViewById(R.id.textSmsReceivedCount);
        textRecvMessages.setText(getResources().getString(R.string.str_sms_count_all, String.valueOf(inboxMessages)));
    }

    @Override
    protected View getPermissionDeniedLayout() {
        return findViewById(R.id.smsNoAccessLayout);
    }

    @Override
    protected View getPermissionGrantedLayout() {
        return findViewById(R.id.smsSampleLayout);
    }
}
