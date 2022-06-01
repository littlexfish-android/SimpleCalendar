package org.lf.calendar.io.sqlitem.list

import android.content.ContentValues
import android.database.Cursor
import android.graphics.Color
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import org.intellij.lang.annotations.Language
import org.lf.calendar.annotation.Range
import java.util.*

/**
 * SQL version 1
 *
 * _id - unique id
 * groupName - group
 * content - the content
 * isComplete - is this item complete
 * createTime - this item create time
 * completeTime - this item completeTime, if incomplete will got null
 * attachCalendarId - the id of calendar which attach
 */
class SqlList1 : SqlListBase {
	
	@Range.IntRange(from = 1)
	var _id: Int = -1
	var groupName: String = ""
	var content: String = ""
	var color: Int = Color.BLACK
	var isComplete: Boolean = false
	var createTime: Date = Date()
	var completeTime: Date? = null
	@Range.IntRange(from = 1, nullable = true)
	var attachCalendarId: Int? = null
	
	constructor() : super()
	
	constructor(groupName: String, content: String, color: Int = Color.BLACK) : super() {
		this.groupName = groupName
		this.content = content
		this.color = color
	}
	
	override fun initFromDatabase(cursor: Cursor) {
		_id = cursor.getInt(0)
		groupName = cursor.getString(1)
		content = cursor.getString(2)
		color = cursor.getInt(3)
		isComplete = cursor.getInt(4) != 0
		createTime = Date(cursor.getLong(5))
		val tmpTime = cursor.getLongOrNull(6)
		completeTime = if(tmpTime == null) null else Date(tmpTime)
		attachCalendarId = cursor.getIntOrNull(7)
	}
	
	override fun getContentValues(): ContentValues {
		val contentValue = ContentValues()
		contentValue.put("groupName", groupName)
		contentValue.put("content", content)
		contentValue.put("color", color)
		contentValue.put("isComplete", if(isComplete) 1 else 0)
		contentValue.put("createTime", createTime.time)
		completeTime?.let {
			contentValue.put("completeTime", it.time)
		}
		attachCalendarId?.let { contentValue.put("attachCalendarId", it) }
		return contentValue
	}
	
	override fun upgrade(sql: SqlListBase, oldVersion: Int, newVersion: Int): SqlListBase {
		return this
	}
	
	@Language("SQL")
	override fun getOnCreateCommand(tableName: String): String =
		"CREATE TABLE IF NOT EXISTS $tableName (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"groupName TEXT NOT NULL," +
				"content TEXT NOT NULL," +
				"color INTEGER NOT NULL DEFAULT " + Color.BLACK + "," +
				"isComplete INTEGER NOT NULL DEFAULT 0," +
				"createTime INTEGER NOT NULL," +
				"completeTime INTEGER," +
				"attachCalendarId INTEGER," +
				"CHECK ( isComplete == 0 OR isComplete == 1 ))"
	
	override fun equals(other: Any?): Boolean {
		return other is SqlList1 && other._id == _id
	}
	
	override fun hashCode(): Int {
		var result = _id
		result = 31 * result + groupName.hashCode()
		result = 31 * result + content.hashCode()
		result = 31 * result + color.hashCode()
		result = 31 * result + isComplete.hashCode()
		result = 31 * result + createTime.hashCode()
		result = 31 * result + (completeTime?.hashCode() ?: 0)
		result = 31 * result + (attachCalendarId?.hashCode() ?: 0)
		return result
	}
	
	override fun toString(): String {
		return "SqlList{_id=$_id,groupName=$groupName,content=$content,color=$color,isComplete=$isComplete," +
				"createTime=$createTime,completeTime=$completeTime,attachCalendarId=$attachCalendarId}"
	}
	
}
