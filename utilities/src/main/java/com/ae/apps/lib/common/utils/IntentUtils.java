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


}
