package org.lf.calendar.io

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.Nullable
import org.intellij.lang.annotations.Language
import org.lf.calendar.io.sqlitem.calendar.SqlCalendar1
import org.lf.calendar.io.sqlitem.list.SqlList1

/**
 * The database name
 */
private const val dataBaseName = "org.lf.simple_calendar"

/**
 * The database version
 */
private const val dataBaseVersion = 1
private val defaultList = SqlList1()
private val defaultCalendar = SqlCalendar1()

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
		fun getInstance(context: Context?): SqlHelper {
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
		db?.execSQL(defaultList.getOnCreateCommand(databaseTableListName))
		db?.execSQL(defaultCalendar.getOnCreateCommand(databaseTableCalendarName))
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
	fun getList(db: SQLiteDatabase, limit: Int? = null, orderBy: String? = null, increase: Boolean = true): ListProcessor {
		return ListProcessor(db, limit, orderBy, increase)
	}
	
	/**
	 * Get calendar from database
	 */
	fun getCalendar(db: SQLiteDatabase, limit: Int? = null, orderBy: String? = null, increase: Boolean = true, timeMin: Long = -1, timeMax: Long = -1): CalendarProcessor {
		return CalendarProcessor(db, limit, orderBy, increase, timeMin, timeMax)
	}
	
	/**
	 * The class contains SqlList item
	 */
	class ListProcessor {
		
		/**
		 * The list from database
		 */
		private val list = HashMap<String, ArrayList<SqlList1>>()
		
		/**
		 * The list item need to insert into database
		 */
		private val appendList = ArrayList<SqlList1>()
		
		/**
		 * The list item need to delete from database
		 */
		private val deleteList = ArrayList<SqlList1>()
		
		@Volatile
		var hasChange = false
		
		/**
		 * Construct by database
		 */
		constructor(db: SQLiteDatabase, limit: Int? = null, orderBy: String? = null, increase: Boolean = true) {
			@Language("SQL")
			var select = "SELECT * FROM $databaseTableListName"
			if(orderBy != null) select += " order by $orderBy ${if(increase) "ASC" else "DESC"}"
			if(limit != null) select += " limit $limit"
			val c: Cursor = db.rawQuery(select, null)
			c.moveToFirst()
			for(i in 0 until c.count) {
				val l = SqlList1()
				l.initFromDatabase(c)
				if(!list.containsKey(l.groupName)) list[l.groupName] = ArrayList()
				list[l.groupName]!!.add(l)
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
		fun addListItem(data: SqlList1) {
			if(list.containsKey(data.groupName) && list[data.groupName]!!.contains(data)) {
				list[data.groupName]!!.remove(data)
				list[data.groupName]!!.add(data)
			}
			else {
				appendList.add(data)
			}
			hasChange = true
		}
		
		/**
		 * Delete list item from database
		 */
		fun deleteListItem(data: SqlList1) {
			val r = list[data.groupName]?.remove(data)
			if(r != null && r) {
				deleteList.add(data)
			}
			hasChange = true
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
				data._id = db.insert(databaseTableListName, null, data.getContentValues()).toInt()
			}
			
			// add append list into map
			for(data in appendList) {
				val arr = list[data.groupName] ?: ArrayList<SqlList1>().also { list[data.groupName] = it }
				
				arr.add(data)
			}
			appendList.clear()
			hasChange = false
			
		}
		
	}
	
	/**
	 * The class contains calendar item
	 */
	class CalendarProcessor {
		
		/**
		 * The item from database
		 */
		private val calendar = ArrayList<SqlCalendar1>()
		
		/**
		 * The item need insert into database
		 */
		private val appendCalendar = ArrayList<SqlCalendar1>()
		
		/**
		 * The item need delete from database
		 */
		private val deleteCalendar = ArrayList<SqlCalendar1>()
		
		@Volatile
		var hasChange = false
		
		/**
		 * Construct by database
		 */
		constructor(db: SQLiteDatabase, limit: Int? = null, orderBy: String? = null, increase: Boolean = true, timeMin: Long = -1, timeMax: Long = -1) {
			@Language("SQL")
			var select = "SELECT * FROM $databaseTableCalendarName"
			if(orderBy != null) select += " order by $orderBy ${if(increase) "ASC" else "DESC"}"
			if(limit != null) select += " limit $limit"
			if(timeMin >= 0 && timeMax >= 0) select += " WHERE time BETWEEN $timeMin AND $timeMax";
			else if(timeMin >= 0 && timeMax < 0) select += " WHERE time >= $timeMin"
			else if(timeMin < 0 && timeMax >= 0) select += "WHERE time < $timeMax"
			val c: Cursor = db.rawQuery(select, null)
			c.moveToFirst()
			for(i in 0 until c.count) {
				val ca = SqlCalendar1()
				ca.initFromDatabase(c)
				calendar.add(ca)
				c.moveToNext()
			}
			c.close()
		}
		
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
		fun addCalendarPlan(data: SqlCalendar1) {
			if(calendar.contains(data)) {
				calendar.remove(data)
				calendar.add(data)
			}
			else {
				appendCalendar.add(data)
			}
			hasChange = true
		}
		
		/**
		 * Delete calendar plan from database
		 */
		fun deleteCalendarPlan(data: SqlCalendar1) {
			if(calendar.remove(data)) {
				deleteCalendar.add(data)
			}
			hasChange = true
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
			hasChange = false
			
		}
		
	}

}

