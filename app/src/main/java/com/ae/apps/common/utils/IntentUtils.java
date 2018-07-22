package com.ae.apps.common.utils;

import android.content.Intent;
import android.net.Uri;

public class IntentUtils {

    /**
     * Helps to build an intent for sending an email
     */
    public static class EmailIntentBuilder {

        private String mTo;
        private String mSubject;
        private String mBody;

        public EmailIntentBuilder() {
        }

        public EmailIntentBuilder to(String to) {
            mTo = to;
            return this;
        }

        public EmailIntentBuilder subject(String subject) {
            mSubject = subject;
            return this;
        }

        public EmailIntentBuilder body(String body) {
            mBody = body;
            return this;
        }

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


}
