package com.ae.apps.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * 
 */
public class CommonUtils {

    public static final int MINIMUM_STRING_LENGTH = 26;

    /**
     * Formats a timestamp as required
     *
     * @param timestamp
     * @return
     */
    public static String formatTimeStamp(String timestamp, String pattern) {
        String lastContactedTimeString = null; //"Never";
        if (timestamp != null && timestamp.trim().length() > 0) {
            long tempLastContacted = Long.parseLong(timestamp);
            if (tempLastContacted > 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
                Date date = new Date(Long.parseLong(timestamp));
                lastContactedTimeString = dateFormat.format(date);
            }
        }
        return lastContactedTimeString;
    }

    /**
     * Well, truncates the given string if length greater than MINIMUM_STRING_LENGTH with an ellipsis
     *
     * @param sourceString
     * @return
     */
    public static String truncateString(String sourceString) {
        // Don't want to return a null here
        if (sourceString == null)
            return "";
        // Do the truncation
        if (sourceString.length() > MINIMUM_STRING_LENGTH) {
            String truncated = sourceString.substring(0, MINIMUM_STRING_LENGTH) + "...";
            return truncated;
        }
        return sourceString;
    }

    /**
     * Create an animation object from the values passed in
     *
     * @param context
     * @param animRes
     * @param startOffset
     * @return
     */
    public static Animation createAnimation(Context context, int animRes, int startOffset) {
        Animation animation = AnimationUtils.loadAnimation(context, animRes);
        animation.setStartOffset(startOffset);
        return animation;
    }

    /**
     * Checks if this is a first install of an application
     *
     * @param context
     * @return
     */
    public static boolean isFirstInstall(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.firstInstallTime == packageInfo.lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
