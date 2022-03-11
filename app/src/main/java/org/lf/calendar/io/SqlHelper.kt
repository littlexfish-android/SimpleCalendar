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
		private lateinit var sql: SqlHelper

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

	fun getList(db: SQLiteDatabase, limit: Int? = null, increase: Boolean = true, orderBy: String = "_id"): ListProcessor {
		return ListProcessor(db, limit, increase, orderBy)
	}

	fun getCalendar(db: SQLiteDatabase, limit: Int? = null, increase: Boolean = true, orderBy: String = "_id"): CalendarProcessor {
		return CalendarProcessor(db, limit, increase, orderBy)
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

	class ListProcessor {

		private val list = HashMap<String, ArrayList<SqlList>>()
		private val appendList = ArrayList<SqlList>()
		
		constructor(db: SQLiteDatabase, limit: Int? = null, increase: Boolean = true, orderBy: String = "_id") {
			val c: Cursor = if(limit != null) db.rawQuery("SELECT * FROM $databaseTableListName order by $orderBy ${if(increase) "ASC" else "DESC"} limit $limit", null) else db.rawQuery("SELECT * FROM $databaseTableListName order by $orderBy ${if(increase) "ASC" else "DESC"}", null)
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
		
		constructor(processor: ListProcessor) {
			for(item in processor.list) {
				list[item.key] = ArrayList(item.value)
			}
		}
		
		fun getList() = list
		
		fun addListItem(data: SqlList) {
			if(list.containsKey(data.category) && list[data.category]!!.contains(data)) {
				list[data.category]!!.remove(data)
				list[data.category]!!.add(data)
			}
			else {
				appendList.add(data)
			}
		}
		
		fun saveSql(db: SQLiteDatabase) {
			// update
			for(sqlListPair in list) {
				for(data in sqlListPair.value) {
					db.update(databaseTableListName, data.getContentValues(), "_id=${data._id}", null)
				}
			}
			
			// add
			for(data in appendList) {
				db.insert(databaseTableListName, null, data.getContentValues())
			}
			
			// add append list into map
			for(data in appendList) {
				val arr = list[data.category] ?: ArrayList<SqlList>().also { list[data.category] = it }
				
				arr.add(data)
			}
			appendList.clear()
			
		}
		
	}

	class CalendarProcessor {

		private val calendar = ArrayList<SqlCalendar>()
		private val appendCalendar = ArrayList<SqlCalendar>()
	
		constructor(db: SQLiteDatabase, limit: Int? = null, increase: Boolean = true, orderBy: String = "_id") {
			val c: Cursor = if(limit != null) db.rawQuery("SELECT * FROM $databaseTableCalendarName order by $orderBy ${if(increase) "ASC" else "DESC"} limit $limit", null) else db.rawQuery("SELECT * FROM $databaseTableCalendarName order by $orderBy ${if(increase) "ASC" else "DESC"}", null)
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

		constructor(processor: CalendarProcessor) {
			for(item in processor.calendar) {
				calendar.add(item)
			}
		}

		fun getCalendar() = calendar
		
		fun addCalendarPlan(data: SqlCalendar) {
			if(calendar.contains(data)) {
				calendar.remove(data)
				calendar.add(data)
			}
			else {
				appendCalendar.add(data)
			}
		}
		
		fun saveSql(db: SQLiteDatabase) {
			// update
			for(data in calendar) {
				db.update(databaseTableCalendarName, data.getContentValues(), "_id=${data._id}", null)
			}
			
			// add
			for(data in appendCalendar) {
				db.insert(databaseTableCalendarName, null, data.getContentValues())
			}
			
			// add append list into list
			calendar.addAll(appendCalendar)
			appendCalendar.clear()
			
		}
		
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
		
		override fun equals(other: Any?): Boolean {
			return other == this || (other is SqlList && (other.category == category && other.content == content))
		}
		
		override fun hashCode(): Int {
			var result = _id
			result = 31 * result + category.hashCode()
			result = 31 * result + content.hashCode()
			result = 31 * result + isComplete.hashCode()
			result = 31 * result + createTime.hashCode()
			result = 31 * result + (completeTime?.hashCode() ?: 0)
			return result
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
		
		override fun equals(other: Any?): Boolean {
			return other == this || (other is SqlCalendar && (other.type == type && other.content == content))
		}
		
		override fun hashCode(): Int {
			var result = _id
			result = 31 * result + type.hashCode()
			result = 31 * result + content.hashCode()
			result = 31 * result + time.hashCode()
			result = 31 * result + isComplete.hashCode()
			result = 31 * result + createTime.hashCode()
			result = 31 * result + (completeTime?.hashCode() ?: 0)
			return result
		}
		
	}

}

