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
 * Common Utilities
 */
public class CommonUtils {

    public static final int MINIMUM_STRING_LENGTH = 26;

    /**
     * Formats a timestamp as required
     *
     * @param timestamp timestamp
     * @param pattern   pattern
     * @return formatted time stamp
     */
    public static String formatTimeStamp(String timestamp, String pattern) {
        String timeString = null; //"Never";
        if (timestamp != null && timestamp.trim().length() > 0) {
            long tempLastContacted = Long.parseLong(timestamp);
            if (tempLastContacted > 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
                Date date = new Date(Long.parseLong(timestamp));
                timeString = dateFormat.format(date);
            }
        }
        return timeString;
    }

    /**
     * Well, truncates the given string if length greater than MINIMUM_STRING_LENGTH with an ellipsis
     *
     * @param sourceString source
     * @return truncated string
     */
    public static String truncateString(String sourceString) {
        // Don't want to return a null here
        if (sourceString == null)
            return "";
        // Do the truncation
        if (sourceString.length() > MINIMUM_STRING_LENGTH) {
            return sourceString.substring(0, MINIMUM_STRING_LENGTH) + "...";
        }
        return sourceString;
    }

    /**
     * Create an animation object from the values passed in
     *
     * @param context the context
     * @param animRes animation resource
     * @param startOffset animation start offset
     * @return Animation object
     */
    public static Animation createAnimation(Context context, int animRes, int startOffset) {
        Animation animation = AnimationUtils.loadAnimation(context, animRes);
        animation.setStartOffset(startOffset);
        return animation;
    }

    /**
     * Checks if this is a first install of an application
     *
     * @param context the context
     * @return true if first install
     */
    public static boolean isFirstInstall(final Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.firstInstallTime == packageInfo.lastUpdateTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if a package is installed
     *
     * @param packageName package name to check
     * @param context the context
     * @return true if package is installed, false otherwise
     */
    public static boolean isPackageInstalled(String packageName, final Context context) {
        try {
            context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
