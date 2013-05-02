package com.ae.apps.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {

	private static int		MINIMUM_STRING_LENGTH	= 26;

	/**
	 * Formats a timestamp as required
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String formatTimeStamp(String timestamp, String pattern) {
		String lastContactedTimeString = "Never";
		if (timestamp != null && timestamp.trim().length() > 0) {
			long tempLastContacted = Long.parseLong(timestamp);
			if (tempLastContacted > 0) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
				Date date = new Date(Long.parseLong(timestamp));
				lastContactedTimeString = dateFormat.format(date);
			}
		}

		return lastContactedTimeString;
	}

	public static String truncateString(String sourceString) {
		if (sourceString == null)
			return "";
		if (sourceString.length() > MINIMUM_STRING_LENGTH) {
			String truncated = sourceString.substring(0, MINIMUM_STRING_LENGTH) + "...";
			return truncated;
		}
		return sourceString;
	}
}
