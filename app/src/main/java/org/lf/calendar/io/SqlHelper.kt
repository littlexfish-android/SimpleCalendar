package org.lf.calendar.io

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.Nullable
import org.intellij.lang.annotations.Language
import org.lf.calendar.io.sqlitem.calendar.SqlCalendar1
import org.lf.calendar.io.sqlitem.color.SqlColor1
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
private val defaultColor = SqlColor1()

/**
 * The class use to contact to sqlite
 */
class SqlHelper(@Nullable context: Context?, @Nullable factory: SQLiteDatabase.CursorFactory?) :
	SQLiteOpenHelper(context, dataBaseName, factory, dataBaseVersion) {

	companion object {
		/**
		 * The database table name of list
		 */
		const val databaseTableListName = "List"
		
		/**
		 * The database table name of calendar
		 */
		const val databaseTableCalendarName = "Calendar"
		
		/**
		 * The database table name of color
		 */
		const val databaseTableColorName = "Color"
		
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
		db?.execSQL(defaultColor.getOnCreateCommand(databaseTableColorName))
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
	 * Get list use sql command
	 */
	fun getList(db: SQLiteDatabase, whereCommand: String): ListProcessor {
		return ListProcessor(db, "SELECT * FROM $databaseTableListName $whereCommand")
	}
	
	/**
	 * Get calendar from database
	 */
	fun getCalendar(db: SQLiteDatabase, limit: Int? = null, orderBy: String? = null, increase: Boolean = true, timeMin: Long = -1, timeMax: Long = -1): CalendarProcessor {
		return CalendarProcessor(db, limit, orderBy, increase, timeMin, timeMax)
	}
	
	/**
	 * Get calendar use sql command
	 */
	fun getCalendar(db: SQLiteDatabase, whereCommand: String): CalendarProcessor {
		return CalendarProcessor(db, "SELECT * FROM $databaseTableCalendarName $whereCommand")
	}
	
	fun getColor(db: SQLiteDatabase): ColorProcessor {
		return ColorProcessor(db)
	}
	
	/**
	 * The class contains SqlList item
	 */
	class ListProcessor {
		
		/**
		 * The list from database
		 */
		private val list = ArrayList<Pair<String, ArrayList<SqlList1>>>()
		
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
			constructFromCursor(c)
			c.close()
		}
		
		constructor(db: SQLiteDatabase, command: String) {
			val c: Cursor = db.rawQuery(command, null)
			constructFromCursor(c)
			c.close()
		}
		
		private fun constructFromCursor(c: Cursor) {
			c.moveToFirst()
			for(i in 0 until c.count) {
				val l = SqlList1()
				l.initFromDatabase(c)
				val index = indexAt(l.groupName)
				if(index < 0) list.add(Pair(l.groupName, ArrayList()))
				list[indexAt(l.groupName)].second.add(l)
				c.moveToNext()
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
			val index = indexAt(data.groupName)
			if(index >= 0 && list[index].second.contains(data)) {
				list[index].second.remove(data)
				list[index].second.add(data)
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
			if(list[indexAt(data.groupName)].second.remove(data)) {
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
			deleteList.clear()
			
			// update
			for(sqlListPair in list) {
				for(data in sqlListPair.second) {
					db.update(databaseTableListName, data.getContentValues(), "_id=${data._id}", null)
				}
			}
			
			// add
			for(data in appendList) {
				data._id = db.insert(databaseTableListName, null, data.getContentValues()).toInt()
			}
			
			// add append list into map
			for(data in appendList) {
				val index = indexAt(data.groupName)
				if(index < 0) {
					list.add(Pair(data.groupName, ArrayList()))
				}
				list[indexAt(data.groupName)].second.add(data)
			}
			appendList.clear()
			hasChange = false
			
		}
		
		private fun indexAt(key: String): Int {
			for((i, it) in list.withIndex()) {
				if(it.first == key) {
					return i
				}
			}
			return -1
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
			if(timeMin >= 0 && timeMax >= 0) select += " WHERE time BETWEEN $timeMin AND $timeMax"
			else if(timeMin >= 0 && timeMax < 0) select += " WHERE time >= $timeMin"
			else if(timeMin < 0 && timeMax >= 0) select += "WHERE time < $timeMax"
			if(orderBy != null) select += " order by $orderBy ${if(increase) "ASC" else "DESC"}"
			if(limit != null) select += " limit $limit"
			val c: Cursor = db.rawQuery(select, null)
			constructFromCursor(c)
			c.close()
		}
		
		constructor(db: SQLiteDatabase, command: String) {
			val c: Cursor = db.rawQuery(command, null)
			constructFromCursor(c)
			c.close()
		}
		
		private fun constructFromCursor(c: Cursor) {
			c.moveToFirst()
			for(i in 0 until c.count) {
				val ca = SqlCalendar1()
				ca.initFromDatabase(c)
				calendar.add(ca)
				c.moveToNext()
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
			deleteCalendar.clear()
			
			// update
			for(data in calendar) {
				db.update(databaseTableCalendarName, data.getContentValues(), "_id=${data._id}", null)
			}
			
			// add
			for(data in appendCalendar) {
				data._id = db.insert(databaseTableCalendarName, null, data.getContentValues()).toInt()
			}
			
			// add append list into list
			calendar.addAll(appendCalendar)
			appendCalendar.clear()
			hasChange = false
			
		}
		
	}
	
	class ColorProcessor(db: SQLiteDatabase) {
		
		private val colors = HashMap<Int, SqlColor1>()
		
		private val appendColor = ArrayList<SqlColor1>()
		
		private val deleteColor = ArrayList<SqlColor1>()
		
		@Volatile
		var hasChange = false
		
		init {
			@Language("SQL")
			var select = "SELECT * FROM $databaseTableColorName"
			val c: Cursor = db.rawQuery(select, null)
			c.moveToFirst()
			for(i in 0 until c.count) {
				val l = SqlList1()
				l.initFromDatabase(c)
				c.moveToNext()
			}
			c.close()
		}
		
		fun getColor() = colors
		
		fun addColor(sql: SqlColor1) {
			if(colors.containsKey(sql.color)) {
				colors[sql.color] = sql
			}
			else {
				appendColor.add(sql)
			}
			hasChange = true
		}
		
		fun deleteColor(sql: SqlColor1) {
			if(colors.remove(sql.color) != null) {
				deleteColor.add(sql)
			}
			hasChange = true
		}
		
		fun saveSql(db: SQLiteDatabase) {
			for(it in deleteColor) {
				db.delete(databaseTableColorName, "color=${it.color}", null)
			}
			deleteColor.clear()
			
			for(it in colors.values) {
				db.update(databaseTableColorName, it.getContentValues(), "color=${it.color}", null)
			}
			
			for(it in appendColor) {
				db.insert(databaseTableColorName, null, it.getContentValues())
				colors[it.color] = it
			}
			appendColor.clear()
			
			hasChange = false
		}
	}
	
}

