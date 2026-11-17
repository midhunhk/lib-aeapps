package com.ae.apps.lib.review;

import android.app.Activity;
import android.content.Context;

/**
 *
 *
 * See <a href="https://developer.android.com/guide/playcore/in-app-review">...</a>
 * See <a href="https://developer.android.com/guide/playcore/in-app-review/kotlin-java">...</a>
 * @since 4.1
 */
public interface AppReview {

    /**
     * User has interacted with the app in such a way as to update the conditions
     * to show the app rating window.
     *
     * Examples include "launching the app", "using certain feature"
     * Or anything that shows the user is interacting with the app
     *
     * @param context the context, use applicationContext
     */
    void userInteractionEvent(Context context);

    /**
     * Request to launch the review flow, once the user has performed a reasonable action.
     *
     * @param activity the activity instance
     */
    void launchAppReviewFlow(Activity activity);
}
