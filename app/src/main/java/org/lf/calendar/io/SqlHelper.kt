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

/**
 * The database name
 */
private const val dataBaseName = "org.lf.simple_calendar"

/**
 * The database version
 */
private const val dataBaseVersion = 1

/**
 * The database table name of list
 */
private const val databaseTableListName = "List"

/**
 * The database table name of calendar
 */
private const val databaseTableCalendarName = "Calendar"

/**
 * The class use to contact to sqlite
 */
class SqlHelper(@Nullable context: Context?, @Nullable factory: SQLiteDatabase.CursorFactory?) :
	SQLiteOpenHelper(context, dataBaseName, factory, dataBaseVersion) {

	companion object {
		private lateinit var sql: SqlHelper
		
		/**
		 * Only can create a sqlite, prevent too many helper
		 */
		fun getInstance(context: Context): SqlHelper {
			if(!::sql.isInitialized) {
				sql = SqlHelper(context, null)
			}
			return sql
		}
	}
	
	/**
	 * On sqlite create, use to create table
	 */
	override fun onCreate(db: SQLiteDatabase?) {
		db?.execSQL("CREATE TABLE IF NOT EXISTS $databaseTableListName (_id INTEGER PRIMARY KEY AUTOINCREMENT,category TEXT NOT NULL,content TEXT NOT NULL,isComplete INTEGER NOT NULL DEFAULT 0,createTime INTEGER NOT NULL,completeTime INTEGER)")
		db?.execSQL("CREATE TABLE IF NOT EXISTS $databaseTableCalendarName (_id INTEGER PRIMARY KEY AUTOINCREMENT,type TEXT NOT NULL,content TEXT NOT NULL,time INTEGER NOT NULL,isComplete INTEGER NOT NULL DEFAULT 0,createTime INTEGER NOT NULL,completeTime INTEGER)")
	}
	
	/**
	 * On database upgrade
	 */
	override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
		//do nothing because version only 1
		//use "drop" to delete old table when it has new version
	}
	
	/**
	 * Get list from database
	 */
	fun getList(db: SQLiteDatabase, limit: Int? = null, increase: Boolean = true, orderBy: String = "_id"): ListProcessor {
		return ListProcessor(db, limit, increase, orderBy)
	}
	
	/**
	 * Get calendar from database
	 */
	fun getCalendar(db: SQLiteDatabase, limit: Int? = null, increase: Boolean = true, orderBy: String = "_id"): CalendarProcessor {
		return CalendarProcessor(db, limit, increase, orderBy)
	}
	
	/**
	 * The class contains SqlList item
	 */
	class ListProcessor {
		
		/**
		 * The list from database
		 */
		private val list = HashMap<String, ArrayList<SqlList>>()
		
		/**
		 * The list item need to insert into database
		 */
		private val appendList = ArrayList<SqlList>()
		
		/**
		 * The list item need to delete from database
		 */
		private val deleteList = ArrayList<SqlList>()
		
		/**
		 * Construct by database
		 */
		constructor(db: SQLiteDatabase, limit: Int? = null, increase: Boolean = true, orderBy: String = "_id") {
			val c: Cursor = if(limit != null) db.rawQuery("SELECT * FROM $databaseTableListName order by $orderBy ${if(increase) "ASC" else "DESC"} limit $limit", null) else db.rawQuery("SELECT * FROM $databaseTableListName order by $orderBy ${if(increase) "ASC" else "DESC"}", null)
			c.moveToFirst()
			for(i in 0 until c.count) {
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
		
		// TODO: add constructor that can construct this by database use time range
		
		/**
		 * Construct by other same class
		 */
		constructor(processor: ListProcessor) {
			for(item in processor.list) {
				list[item.key] = ArrayList(item.value)
			}
			for(item in processor.appendList) {
				appendList.add(item)
			}
			for(item in deleteList) {
				deleteList.add(item)
			}
		}
		
		/**
		 * Get the map of list
		 */
		fun getList() = list
		
		/**
		 * Add list item into database
		 */
		fun addListItem(data: SqlList) {
			if(list.containsKey(data.category) && list[data.category]!!.contains(data)) {
				list[data.category]!!.remove(data)
				list[data.category]!!.add(data)
			}
			else {
				appendList.add(data)
			}
		}
		
		/**
		 * Delete list item from database
		 */
		fun deleteListItem(data: SqlList) {
			val r = list[data.category]?.remove(data)
			if(r != null && r) {
				deleteList.add(data)
			}
		}
		
		/**
		 * Save list item to database
		 */
		fun saveSql(db: SQLiteDatabase) {
			//remove
			for(data in deleteList) {
				db.delete(databaseTableListName, "_id=${data._id}", null)
			}
			
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
	
	/**
	 * The class contains calendar item
	 */
	class CalendarProcessor {
		
		/**
		 * The item from database
		 */
		private val calendar = ArrayList<SqlCalendar>()
		
		/**
		 * The item need insert into database
		 */
		private val appendCalendar = ArrayList<SqlCalendar>()
		
		/**
		 * The item need delete from database
		 */
		private val deleteCalendar = ArrayList<SqlCalendar>()
		
		/**
		 * Construct by database
		 */
		constructor(db: SQLiteDatabase, limit: Int? = null, increase: Boolean = true, orderBy: String = "_id") {
			val c: Cursor = if(limit != null) db.rawQuery("SELECT * FROM $databaseTableCalendarName order by $orderBy ${if(increase) "ASC" else "DESC"} limit $limit", null) else db.rawQuery("SELECT * FROM $databaseTableCalendarName order by $orderBy ${if(increase) "ASC" else "DESC"}", null)
			c.moveToFirst()
			for(i in 0 until c.count) {
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
		
		// TODO: add constructor that can construct this by database use time range
		
		/**
		 * Construct by other same class
		 */
		constructor(processor: CalendarProcessor) {
			for(item in processor.calendar) {
				calendar.add(item)
			}
			for(item in processor.appendCalendar) {
				appendCalendar.add(item)
			}
			for(item in deleteCalendar) {
				deleteCalendar.add(item)
			}
		}
		
		/**
		 * Get list of calendar items
		 */
		fun getCalendar() = calendar
		
		/**
		 * Add calendar plan into database
		 */
		fun addCalendarPlan(data: SqlCalendar) {
			if(calendar.contains(data)) {
				calendar.remove(data)
				calendar.add(data)
			}
			else {
				appendCalendar.add(data)
			}
		}
		
		/**
		 * Delete calendar plan from database
		 */
		fun deleteCalendarPlan(data: SqlCalendar) {
			if(calendar.remove(data)) {
				deleteCalendar.add(data)
			}
		}
		
		/**
		 * Save calendar plans to database
		 */
		fun saveSql(db: SQLiteDatabase) {
			//remove
			for(data in deleteCalendar) {
				db.delete(databaseTableCalendarName, "_id=${data._id}", null)
			}
			
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
	
	/**
	 * List item from sqlite
	 */
	data class SqlList(var _id: Int, val category: String, val content: String, val isComplete: Boolean, val createTime: Date, val completeTime: Date?) {
		
		/**
		 * To content values
		 */
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
	
	/**
	 * Calendar plan from sqlite
	 */
	data class SqlCalendar(val _id: Int, val type: String, val content: String, val time: Date, val isComplete: Boolean, val createTime: Date, val completeTime: Date?) {
		
		/**
		 * To content value
		 */
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

