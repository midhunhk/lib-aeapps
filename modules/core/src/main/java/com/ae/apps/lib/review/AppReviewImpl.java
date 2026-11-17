package com.ae.apps.lib.review;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

/**
 * An implementation for managing In-App Reviews
 * Usage:
 * Invoke this method whenever there is a user event considered as a positive interaction
 * Typical example would be each time the user launches the app
 * <pre>
 *      AppReviewImpl.getInstance().userInteractionEvent(applicationContext)
 * </pre>
 *
 * Invoke this method to kick of the In-App Review flow, which computes whether to show or not
 * based on the user interaction events
 * <pre>
 *     AppReviewImpl.getInstance().launchAppReviewFlow(activity)
 * </pre>
 */
public class AppReviewImpl implements AppReview {

    protected static final String PREF_KEY_USER_INTERACTION_COUNT = "key_review_user_interaction_count";
    protected static final String PREF_KEY_TIMES_PROMPTED = "key_review_times_prompted";
    static final int DEFAULT_REVIEW_THRESHOLD = 5;

    protected SharedPreferences preferences;

    private AppReviewImpl() {

    }

    private static final class InstanceHolder {
        static final AppReview instance = new AppReviewImpl();
    }

    public static AppReview getInstance() {
        return InstanceHolder.instance;
    }

    @Override
    public void userInteractionEvent(Context context) {
        ensurePreferences(context);

        int currentUserEventCount = preferences.getInt(PREF_KEY_USER_INTERACTION_COUNT, 0);

        preferences.edit()
                .putInt(PREF_KEY_USER_INTERACTION_COUNT, ++currentUserEventCount)
                .apply();

    }

    private void ensurePreferences(Context context) {
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    /**
     * A minimum value for the review threshold
     *
     * @return a value used as threshold
     */
    protected int getReviewRequestThreshold() {
        return DEFAULT_REVIEW_THRESHOLD;
    }

    /**
     * Performs the calculation to decide whether to show the in-app review flow
     *
     * @return true if conditions are met, false otherwise
     */
    protected boolean conditionsMetForShowingReview(Context context) {
        ensurePreferences(context);
        int reviewThreshold = getReviewRequestThreshold();
        if (reviewThreshold <= 0) {
            throw new IllegalStateException("Review threshold must be greater than 0");
        }

        int currentUserEventCount = preferences.getInt(PREF_KEY_USER_INTERACTION_COUNT, 0);
        int timesPrompted = preferences.getInt(PREF_KEY_TIMES_PROMPTED, 0);
        if (currentUserEventCount % reviewThreshold == 0) {
            return timesPrompted < reviewThreshold || timesPrompted % reviewThreshold == 0;
        }
        return false;
    }

    @Override
    public void launchAppReviewFlow(final Activity activity) {
        boolean showReview = conditionsMetForShowingReview(activity);
        if (showReview) {
            final ReviewManager reviewManager = ReviewManagerFactory.create(activity);
            final Task<ReviewInfo> request = reviewManager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ReviewInfo reviewInfo = task.getResult();

                    launchReviewFlow(reviewInfo, reviewManager, activity);
                }
            });
        }
    }

    private void launchReviewFlow(ReviewInfo reviewInfo, ReviewManager reviewManager, Activity activity) {
        int timesPrompted = preferences.getInt(PREF_KEY_TIMES_PROMPTED, 0);
        preferences.edit()
                .putInt(PREF_KEY_TIMES_PROMPTED, ++timesPrompted)
                .apply();

        Task<Void> flow = reviewManager.launchReviewFlow(activity, reviewInfo);
        flow.addOnCompleteListener(task -> {
            // Flow has finished, but we do not get an indication from the API
        });
    }
}
