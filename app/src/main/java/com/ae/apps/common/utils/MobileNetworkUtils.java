package com.ae.apps.common.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

public class MobileNetworkUtils {

	/**
	 * Call a contact
	 * 
	 * @param contactNo the contact no
	 */
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
	 * This method checks whether Internet connectivity is available on the device
	 * 
	 * @param context the context
	 * @return true if internet connection available
	 */
	public static boolean isInternetAvailable(Context context) {
		// Request the Connectivity service to the OS
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		// Check the current state of the Network Information
		if (networkInfo == null)
			return false;
		if (!networkInfo.isConnected())
			return false;
		if (!networkInfo.isAvailable())
			return false;
		return true;
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