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
package com.ae.apps.lib.db

import android.content.Context
import android.content.res.AssetManager
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * Boiler plate code for a Database file that is copied from the assets folder
 *
 * @author Midhun
 */
abstract class CopiedDataBaseHelper(
    context: Context,
    private val databaseName: String,
    factory: CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, databaseName, factory, version) {
    private var mDataBase: SQLiteDatabase? = null
    private val mAssetManager: AssetManager

    init {
        mAssetManager = context.assets
        databasePath = context.getDatabasePath(databaseName).toString()
    }

    /**
     * Creates the database
     *
     */
    fun createDataBase() {
        val dbExist = checkDataBase()
        if (dbExist) {
            // do nothing - database already exists
        } else {
            // By calling this method an empty database will be created into the default system path
            // of your application so we are gonna be able to overwrite that database with our database.
            this.readableDatabase
            try {
                copyDataBase()
            } catch (e: IOException) {
                throw Error("Error copying database")
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private fun checkDataBase(): Boolean {
        var checkDB: SQLiteDatabase? = null
        try {
            val myPath = databasePath
            checkDB = SQLiteDatabase.openDatabase(myPath!!, null, SQLiteDatabase.OPEN_READONLY)
        } catch (e: SQLiteException) {
            // database does't exist yet.
        }
        checkDB?.close()
        return checkDB != null
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the system folder, from
     * where it can be accessed and handled. This is done by transferring byte stream.
     */
    @Throws(IOException::class)
    protected fun copyDataBase() {
        // Open your local db as the input stream
        val myInput = mAssetManager.open(databaseName)

        // Path to the just created empty db
        val outFileName = databasePath

        // Open the empty db as the output stream
        val myOutput: OutputStream = FileOutputStream(outFileName)

        // transfer bytes from the input file to the output file
        val buffer = ByteArray(1024)
        var length: Int
        while (myInput.read(buffer).also { length = it } > 0) {
            myOutput.write(buffer, 0, length)
        }

        // Close the streams
        myOutput.flush()
        myOutput.close()
        myInput.close()
    }

    /**
     * Opens the database
     *
     * @throws SQLException while opening the database
     */
    @Throws(SQLException::class)
    fun openDataBase() {
        // Open the database
        val myPath = databasePath
        mDataBase = SQLiteDatabase.openDatabase(myPath!!, null, SQLiteDatabase.OPEN_READONLY)
    }

    /**
     * close the database
     */
    @Synchronized
    override fun close() {
        if (mDataBase != null) mDataBase!!.close()
        super.close()
    }

    /**
     * Invoked onCreate
     *
     * @param arg0 database
     */
    override fun onCreate(arg0: SQLiteDatabase) {}

    /**
     * called on database upgrade
     *
     * @param db database
     * @param oldVersion oldversion
     * @param newVersion newversion
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    /**
     * Runs a query on the real database
     *
     * @param table table name
     * @param columns columns
     * @param selection selection
     * @param selectionArgs selectionargs
     * @param groupBy groupby
     * @param having having clause
     * @param orderBy orderby
     * @return cursor
     */
    protected fun query(
        table: String?,
        columns: Array<String?>?,
        selection: String?,
        selectionArgs: Array<String?>?,
        groupBy: String?,
        having: String?,
        orderBy: String?
    ): Cursor {
        return mDataBase!!.query(table, columns, selection, selectionArgs, groupBy, having, orderBy)
    }

    companion object {
        private var databasePath: String? = null
    }
}