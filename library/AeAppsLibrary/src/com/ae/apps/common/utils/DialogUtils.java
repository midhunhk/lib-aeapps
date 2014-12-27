package com.ae.apps.common.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Utility class to show dialogs
 * 
 * @author Midhun
 * 
 */
public class DialogUtils {

	/**
	 * Displays a basic dialog with a button
	 * 
	 * @param context
	 * @param titleResourceId
	 * @param messageResourceId
	 */
	public static void showWithMessageAndOkButton(final Context context, int titleResourceId, int messageResourceId,
			int buttonResourceId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context)
			.setCancelable(true)
			.setTitle(titleResourceId)
			.setMessage(messageResourceId)
			.setPositiveButton(buttonResourceId, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// We shall dismiss the dialog when the ok is clicked
					dialog.dismiss();
				}
			});
		builder.show();
	}
	
	/**
	 * To display a Material Dialog box, with title, content and a button
	 * 
	 * @param context
	 * @param titleResourceId
	 * @param messageResourceId
	 * @param positiveButtonResourceId
	 */
	public static void showMaterialDialog(final Context context, int titleResourceId, int messageResourceId, 
			int positiveButtonResourceId){
		
	}
}
