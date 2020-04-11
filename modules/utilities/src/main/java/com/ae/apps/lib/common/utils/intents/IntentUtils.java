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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Helper functions for handling intents
 *
 * @since 4.0
 */
public class IntentUtils {

    /**
     * Returns an intent for sharing
     *
     * @param context           context
     * @param textResourceId    text resource
     * @param titleResourceId   title resource
     * @param subjectResourceId suibject resource
     * @return intent
     */
    public static Intent getShareIntent(final Context context,
                                        int textResourceId,
                                        int titleResourceId,
                                        int subjectResourceId) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(textResourceId));
        shareIntent.putExtra(Intent.EXTRA_TITLE, context.getString(titleResourceId));
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(subjectResourceId));
        return shareIntent;
    }

    /**
     * Creates an intent for URI
     *
     * @param context context
     * @param url     url
     * @return intent
     */
    public static Intent getUriIntent(final Context context, final String url) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        return intent;
    }

}
