/*
 * Copyright (c) 2015 Midhun Harikumar
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
 *
 */

package com.ae.apps.lib.common.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    /**
     * Creates a FrameLayout that can be used as a parent container
     *
     * @param context the context
     * @return an instance of FrameLayout
     */
    public static ViewGroup createParentLayout(final Context context){
        FrameLayout layout = new FrameLayout(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT,Gravity.CENTER_HORIZONTAL| Gravity.CENTER_VERTICAL);
        layout.setLayoutParams(lp);
        return layout;
    }

    public static void launchWebPage(final Context context, final String webPageUrl){
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(webPageUrl));
        context.startActivity(intent);
    }
}
