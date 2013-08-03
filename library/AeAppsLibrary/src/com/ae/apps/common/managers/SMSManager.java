package com.ae.apps.common.managers;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * A Manager class for acting on the sms inbox. Not to be confused with the 
 * class with the same name in the android SDK.
 * 
 * @author Midhun
 * 
 */
public class SMSManager {

	public static String	SMS_URI_ALL		= "content://sms/";
	public static String	SMS_URI_SENT	= "content://sms/sent";
	public static String	SMS_URI_INBOX	= "content://sms/inbox";

	private static String	TAG				= "SMSManager";

	private ContentResolver	contentResolver;

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public SMSManager(Context context) {
		this.contentResolver = context.getContentResolver();
	}

	/**
	 * Returns the number of messages in the inbox uri
	 * 
	 * @param uri
	 * @return
	 */
	public int getMessagesCount(String uri) {
		int count = 0;
		Uri parsedUri = Uri.parse(uri);
		String[] projection = new String[] { "_id", "thread_id", "address" };
		Cursor cursor = contentResolver.query(parsedUri, projection, null, null, null);
		count = cursor.getCount();
		cursor.close();

		Log.d(TAG, "Exiting getMessagesCount with count as " + count);
		return count;
	}

	/**
	 * Returns a Set of unique sender contact ids
	 * 
	 * @return
	 */
	public Map<String, Integer> getUniqueSenders() {
		Map<String, Integer> sendersMap = new HashMap<String, Integer>();

		Uri parsedUri = Uri.parse(SMS_URI_INBOX);
		String[] projection = new String[] { "_id", "address", "person" };
		Cursor cursor = contentResolver.query(parsedUri, projection, null, null, null);

		if (cursor.getCount() > 0) {
			String address;
			int addressIndex = cursor.getColumnIndex("address");
			if (cursor.moveToFirst())
				do {
					// The person will be the foreign key from the Contacts table
					address = cursor.getString(addressIndex);

					if (address != null) {
						if (sendersMap.containsKey(address)) {
							sendersMap.put(address, sendersMap.get(address) + 1);
						} else {
							sendersMap.put(address, 1);
						}
					}
				} while (cursor.moveToNext());
		}
		cursor.close();
		Log.d(TAG, "Unique Senders Count " + sendersMap.size());

		return sendersMap;
	}
}
