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

	/*
	 * private ContactItem getContactByAddr(Context context, final SMSItem sms) { Uri personUri =
	 * Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, sms.mAddress); Cursor cur =
	 * context.getContentResolver().query(personUri, new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null); if
	 * (cur.moveToFirst()) { int nameIdx = cur.getColumnIndex(PhoneLookup.DISPLAY_NAME); ContactItem item = new
	 * ContactItem(); item.mName = cur.getString(nameIdx); cur.close(); return item; } return null; }
	 */

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
		/*
		 * Uri parsedUri = Uri.parse(SMS_URI_INBOX); String[] projection = new String[] { "_id", "address", "person" };
		 * Cursor cursor = contentResolver.query(parsedUri, null, null, null, null); String s[] =
		 * cursor.getColumnNames(); int ss = cursor.getColumnCount();
		 * 
		 * if (cursor.getCount() > 0) { String person; int personIndex = cursor.getColumnIndex("person"); if
		 * (cursor.moveToFirst()) do { // The person will be the foreign key from the Contacts table person =
		 * cursor.getString(personIndex);
		 * 
		 * if (person != null) { if (sendersMap.containsKey(person)) { sendersMap.put(person, sendersMap.get(person) +
		 * 1); } else { sendersMap.put(person, 1); } } } while (cursor.moveToNext()); }
		 */
	}
}
