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

package com.ae.apps.lib.common.utils.intents;

import android.content.Intent;
import android.net.Uri;

/**
 * Helps to build an intent for sending an email
 */
public class EmailIntentBuilder {
    private String mTo;
    private String mSubject;
    private String mBody;

    /**
     * Create a new instance of the builder
     */
    public EmailIntentBuilder() {
    }

    /**
     * set the to address
     *
     * @param to to address
     * @return this instance
     */
    public EmailIntentBuilder to(String to) {
        mTo = to;
        return this;
    }

    /**
     * set the subject
     *
     * @param subject the email subject
     * @return this instance
     */
    public EmailIntentBuilder subject(String subject) {
        mSubject = subject;
        return this;
    }

    /**
     * set the body
     *
     * @param body the email body
     * @return this instance
     */
    public EmailIntentBuilder body(String body) {
        mBody = body;
        return this;
    }

    /**
     * get the intent for an email
     *
     * @return the intent
     */
    public Intent get() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/html");
        if (null != mTo) {
            intent.setData(Uri.parse("mailto:" + mTo));
            intent.putExtra(Intent.EXTRA_EMAIL, mTo);
        }
        if (mSubject != null) intent.putExtra(Intent.EXTRA_SUBJECT, mSubject);
        if (null != mBody) intent.putExtra(Intent.EXTRA_TEXT, mBody);

        return intent;
    }
}
