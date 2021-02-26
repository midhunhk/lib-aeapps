package com.ae.apps.lib.review;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.ae.apps.lib.review.AppReviewImpl.PREF_KEY_TIMES_PROMPTED;
import static com.ae.apps.lib.review.AppReviewImpl.PREF_KEY_USER_INTERACTION_COUNT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AppReviewTest {

    AppReview appReview;
    Context context;
    SharedPreferences sharedPrefs;

    @Before
    public void setUp(){
        appReview = AppReviewImpl.getInstance();
        context = ApplicationProvider.getApplicationContext();
        sharedPrefs = context.getSharedPreferences("test_shared", Context.MODE_PRIVATE);
    }

    @After
    public void tearDown(){
        sharedPrefs.edit()
                .putInt(PREF_KEY_USER_INTERACTION_COUNT, 0)
                .putInt(PREF_KEY_TIMES_PROMPTED, 0)
                .apply();
    }

    @Test
    public void test_thatAppReviewObjectBeCreated() {
        assertNotNull(appReview);
    }

    @Test
    public void test_thatDefaultReviewThreshold(){
        AppReviewImpl impl = (AppReviewImpl) appReview;
        assertEquals(AppReviewImpl.DEFAULT_REVIEW_THRESHOLD, impl.getReviewRequestThreshold());
    }

    @Test
    public void test_thatConditionsMetSatisfiesForDefaultImplementation(){
        Context context = ApplicationProvider.getApplicationContext();
        AppReviewImpl impl = (AppReviewImpl) appReview;
        impl.preferences = sharedPrefs;

        for(int i = 0; i < impl.getReviewRequestThreshold(); i++){
            impl.userInteractionEvent(context);
        }
        boolean result = impl.conditionsMetForShowingReview(context);

        // This should return true at least once during  getReviewRequestThreshold number of times
        // in the default implementation
        assertTrue(result);
    }
}
