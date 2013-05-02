package com.ae.apps.common.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class MobileNetworkUtils {

	/**
	 * Call a contact
	 * 
	 * @param contactNo
	 */
	public static void callContact(Context context, String contactNo) {
		try {
			String uri = "tel:" + contactNo;
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse(uri));
			context.startActivity(intent);
		} catch (Exception e) {

		}
	}

	/**
	 * Text a contact
	 * 
	 * @param contactNo
	 */
	public static void textContact(Context context,String contactNo) {
		try {
			String uri = "smsto:" + contactNo;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			// intent.putExtra(Intent.EXTRA_PHONE_NUMBER, Uri.parse(uri));
			intent.putExtra("address", contactNo);
			intent.setData(Uri.parse(uri));
			// intent.setType("vnd.android-dir/mms-sms");
			context.startActivity(intent);
		} catch (Exception e) {

		}
	}
}
