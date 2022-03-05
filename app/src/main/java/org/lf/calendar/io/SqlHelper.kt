package org.lf.calendar.io

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.Nullable
import androidx.core.database.getLongOrNull
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private const val dataBaseName = "simple_calendar"
private const val dataBaseVersion = 1
private const val databaseTableListName = "List"
private const val databaseTableCalendarName = "Calendar"

class SqlHelper(@Nullable context: Context?, @Nullable factory: SQLiteDatabase.CursorFactory?) :
	SQLiteOpenHelper(context, dataBaseName, factory, dataBaseVersion) {

	companion object {
		lateinit var sql: SqlHelper

		fun getInstance(context: Context): SqlHelper {
			if(!::sql.isInitialized) {
				sql = SqlHelper(context, null)
			}
			return sql
		}
	}

	override fun onCreate(db: SQLiteDatabase?) {
		db?.execSQL("CREATE TABLE IF NOT EXISTS $databaseTableListName (_id INTEGER PRIMARY KEY AUTOINCREMENT,category TEXT NOT NULL,content TEXT NOT NULL,isComplete INTEGER NOT NULL DEFAULT 0,createTime INTEGER NOT NULL,completeTime INTEGER)")
		db?.execSQL("CREATE TABLE IF NOT EXISTS $databaseTableCalendarName (_id INTEGER PRIMARY KEY AUTOINCREMENT,type TEXT NOT NULL,content TEXT NOT NULL,time INTEGER NOT NULL,isComplete INTEGER NOT NULL DEFAULT 0,createTime INTEGER NOT NULL,completeTime INTEGER)")
	}

	override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
		db?.execSQL("DROP TABLE $databaseTableListName")
		db?.execSQL("DROP TABLE $databaseTableCalendarName")
	}

	fun getList(db: SQLiteDatabase): ListProcessor {
		return ListProcessor(db)
	}

	fun getCalendar(db: SQLiteDatabase): CalendarProcessor {
		return CalendarProcessor(db)
	}

	fun insertList(db: SQLiteDatabase, items: ArrayList<SqlList>) {
		for(item in items) {
			db.insert(databaseTableListName, null, item.getContentValues())
		}
	}

	fun updateList(db: SQLiteDatabase, items: ArrayList<SqlList>) {
		for(item in items) {
			db.update(databaseTableListName, item.getContentValues(), "_id=${item._id}", null)
		}
	}

	fun deleteList(db: SQLiteDatabase, items: ArrayList<SqlList>) {
		for(item in items) {
			db.delete(databaseTableListName, "_id=${item._id}", null)
		}
	}

	fun insertCalendar(db: SQLiteDatabase, items: ArrayList<SqlCalendar>) {
		for(item in items) {
			db.insert(databaseTableCalendarName, null, item.getContentValues())
		}
	}

	fun updateCalendar(db: SQLiteDatabase, items: ArrayList<SqlCalendar>) {
		for(item in items) {
			db.update(databaseTableCalendarName, item.getContentValues(), "_id=${item._id}", null)
		}
	}

	fun deleteCalendar(db: SQLiteDatabase, items: ArrayList<SqlCalendar>) {
		for(item in items) {
			db.delete(databaseTableCalendarName, "_id=${item._id}", null)
		}
	}

	class ListProcessor(db: SQLiteDatabase) {

		private val list = HashMap<String, ArrayList<SqlList>>()

		init {
			val c: Cursor = db.rawQuery("SELECT * FROM $databaseTableListName", null)
			c.moveToFirst()
			for(i in 0..c.count) {
				val id = c.getInt(0)
				val category = c.getString(1)
				val content = c.getString(2)
				val isComplete = c.getInt(3) != 0
				val createTime = Date(c.getLong(4))
				val tmpTime = c.getLongOrNull(5)
				val completeTime: Date? = if(tmpTime == null) null else Date(tmpTime)
				if(!list.containsKey(category)) list[category] = ArrayList()
				list[category]!!.add(SqlList(id, category, content, isComplete, createTime, completeTime))
				c.moveToNext()
			}
			c.close()
		}

		fun getList() = list

	}

	class CalendarProcessor(db: SQLiteDatabase) {

		private val calendar = ArrayList<SqlCalendar>()

		init {
			val c: Cursor = db.rawQuery("SELECT * FROM Calendar", null)
			c.moveToFirst()
			for(i in 0..c.count) {
				val id = c.getInt(0)
				val type = c.getString(1)
				val content = c.getString(2)
				val time = Date(c.getLong(3))
				val isComplete = c.getInt(4) != 0
				val createTime = Date(c.getLong(5))
				val tmpTime = c.getLongOrNull(6)
				val completeTime: Date? = if(tmpTime == null) null else Date(tmpTime)
				calendar.add(SqlCalendar(id, type, content, time, isComplete, createTime, completeTime))
			}
			c.close()
		}

		fun getCalendar() = calendar

	}

	data class SqlList(var _id: Int, val category: String, val content: String, val isComplete: Boolean, val createTime: Date, val completeTime: Date?) {

		fun getContentValues(): ContentValues {
			val contentValue = ContentValues()
			contentValue.put("category", category)
			contentValue.put("content", content)
			contentValue.put("isComplete", if(isComplete) 1 else 0)
			contentValue.put("createTime", createTime.time)
			completeTime?.let {
				contentValue.put("completeTime", it.time)
			}
			return contentValue
		}

	}

	data class SqlCalendar(val _id: Int, val type: String, val content: String, val time: Date, val isComplete: Boolean, val createTime: Date, val completeTime: Date?) {

		fun getContentValues(): ContentValues {
			val contentValue = ContentValues()
			contentValue.put("type", type)
			contentValue.put("content", content)
			contentValue.put("time", time.time)
			contentValue.put("isComplete", if(isComplete) 1 else 0)
			contentValue.put("createTime", createTime.time)
			completeTime?.let {
				contentValue.put("completeTime", it.time)
			}
			return contentValue
		}

	}

}

