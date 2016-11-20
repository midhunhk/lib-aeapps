package com.ae.apps.common.db;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Boiler plate code for a Database file that is copied from the assets folder
 * 
 * @author Midhun
 * 
 */
public abstract class CopiedDataBaseHelper extends SQLiteOpenHelper {
	private SQLiteDatabase	mDataBase;
	private AssetManager	mAssetManager;
	private String			databaseName;
	private static String	databasePath	= null;

	/**
	 * Required constructor
	 * 
	 * @param context
	 * @param databaseName
	 * @param factory
	 * @param version
	 */
	public CopiedDataBaseHelper(Context context, String databaseName, CursorFactory factory, int version) {
		super(context, databaseName, factory, version);
		mAssetManager = context.getAssets();
		this.databaseName = databaseName;
		databasePath = context.getDatabasePath(databaseName).toString();
	}

	/**
	 * Creates the database
	 * 
	 * @throws IOException
	 */
	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		if (dbExist) {
			// do nothing - database already exists
		} else {
			// By calling this method an empty database will be created into the default system path
			// of your application so we are gonna be able to overwrite that database with our database.
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	protected boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = databasePath;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// database does't exist yet.
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created empty database in the system folder, from
	 * where it can be accessed and handled. This is done by transfering bytestream.
	 * */
	protected void copyDataBase() throws IOException {
		// Open your local db as the input stream
		InputStream myInput = mAssetManager.open(databaseName);

		// Path to the just created empty db
		String outFileName = databasePath;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void openDataBase() throws SQLException {
		// Open the database
		String myPath = databasePath;
		mDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	}

	@Override
	public synchronized void close() {
		if (mDataBase != null)
			mDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	/**
	 * Runs a query on the real database
	 * 
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	protected Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		return mDataBase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	}

}
