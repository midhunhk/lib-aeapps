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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 *  Some utility methods that work on mobile networks
 */
public class MobileNetworkUtils {

	/**
	 * Call a contact
	 * Declare the permission "android.permission.CALL_PHONE" in the Manifest to use this method
	 *
	 * @param contactNo the contact no
	 */
	@SuppressLint("MissingPermission")
	public static void callContact(Context context, String contactNo) {
		try {
			String uri = "tel:" + contactNo;
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse(uri));
			context.startActivity(intent);
		} catch (Exception e) {
			// Report no exception
		}
	}

	/**
	 * Send to the dialer activity
	 *
	 * @param context
	 * @param contactNo
	 */
	public static void dialContact(Context context, String contactNo) {
		try {
			String uri = "tel:" + contactNo;
			Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse(uri));
			context.startActivity(intent);
		} catch (Exception e) {
			// Report no exception
		}
	}

	/**
	 * Text a contact
	 * 
	 * @param contactNo contact number
	 */
	public static void textContact(Context context, String contactNo) {
		try {
			String uri = "smsto:" + contactNo;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.putExtra("address", contactNo);
			intent.setData(Uri.parse(uri));
			context.startActivity(intent);
		} catch (Exception e) {
			// Report no exception
		}
	}

	/**
	 * Launches an Intent to open a web page
	 * 
	 * @param context the context
	 * @param url url
	 */
	public static void launchWebPage(Context context, String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		context.startActivity(i);
	}

}