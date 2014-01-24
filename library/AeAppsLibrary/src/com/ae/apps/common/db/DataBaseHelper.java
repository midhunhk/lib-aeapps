package com.ae.apps.common.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Boilerplate code and helper for a simple database
 * 
 * @author Midhun
 * 
 */
public class DataBaseHelper extends SQLiteOpenHelper {

	private SQLiteDatabase	mDataBase;
	private String[]		mCreateTables;

	public DataBaseHelper(Context context, String name, CursorFactory factory, int version, String[] createTables) {
		super(context, name, factory, version);
		mCreateTables = createTables;
		mDataBase = this.getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create the requested tables
		for (String query : mCreateTables) {
			db.execSQL(query);
		}
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

	protected Cursor query(String table, String[] columns, String selection, String[] selectionArgs) {
		return mDataBase.query(table, columns, selection, selectionArgs, null, null, null);
	}

	protected Cursor rawQuery(String sql, String[] selectionArgs) {
		return mDataBase.rawQuery(sql, selectionArgs);
	}

	/**
	 * Inserts a row
	 * 
	 * @param table
	 * @param values
	 * @return
	 */
	protected long insert(String table, ContentValues values) {
		return mDataBase.insert(table, null, values);
	}

	/**
	 * Updates an existing row
	 * 
	 * @param table
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	protected long update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		return mDataBase.update(table, values, whereClause, whereArgs);
	}

}
