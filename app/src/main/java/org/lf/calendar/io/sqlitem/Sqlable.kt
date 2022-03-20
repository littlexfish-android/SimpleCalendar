package org.lf.calendar.io.sqlitem

import android.content.ContentValues
import android.database.Cursor

interface Sqlable {
	
	/**
	 * Initial data from database cursor
	 */
	fun initFromDatabase(cursor: Cursor)
	
	/**
	 * Get {@link android.content.ContentValues} use to SQL
	 */
	fun getContentValues(): ContentValues
	
}