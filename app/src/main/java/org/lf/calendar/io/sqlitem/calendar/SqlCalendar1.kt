package org.lf.calendar.io.sqlitem.calendar

import android.content.ContentValues
import android.database.Cursor
import android.graphics.Color
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import org.intellij.lang.annotations.Language
import java.util.*

/**
 * SQL version 1
 *
 * _id - unique id
 * content - the content
 * remark - the remark of plan
 * time - the time this plan append on
 * isComplete - is this item complete
 * createTime - this item create time
 * completeTime - this item completeTime, if incomplete will got null
 *
 * NOTE: complete relative is keep if future need use
 */
class SqlCalendar1 : SqlCalendarBase {
	
	var _id: Int = -1
	var content: String = ""
	var remark: String? = null
	var time: Date = Date()
	var color: Int = Color.BLACK
	var isComplete: Boolean = false
	var createTime: Date = Date()
	var completeTime: Date? = null
	
	constructor() : super()
	
	constructor(content: String, remark: String?, time: Date, color: Int = Color.BLACK) : super() {
		this.content = content
		this.remark = remark
		this.time = time
		this.color = color
	}
	
	override fun initFromDatabase(cursor: Cursor) {
		_id = cursor.getInt(0)
		content = cursor.getString(1)
		remark = cursor.getStringOrNull(2)
		time = Date(cursor.getLong(3))
		color = cursor.getInt(4)
		isComplete = cursor.getInt(5) != 0
		createTime = Date(cursor.getLong(6))
		val tmpTime = cursor.getLongOrNull(7)
		completeTime = if(tmpTime == null) null else Date(tmpTime)
	}
	
	override fun getContentValues(): ContentValues {
		val contentValue = ContentValues()
		contentValue.put("content", content)
		contentValue.put("remark", remark)
		contentValue.put("time", time.time)
		contentValue.put("color", color)
		contentValue.put("isComplete", if(isComplete) 1 else 0)
		contentValue.put("createTime", createTime.time)
		completeTime?.let {
			contentValue.put("completeTime", it.time)
		}
		return contentValue
	}
	
	override fun upgrade(sql: SqlCalendarBase, oldVersion: Int, newVersion: Int): SqlCalendarBase {
		return this
	}
	
	@Language("SQL")
	override fun getOnCreateCommand(tableName: String): String =
		"CREATE TABLE IF NOT EXISTS $tableName (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"content TEXT NOT NULL, " +
				"remark TEXT," +
				"time INTEGER NOT NULL," +
				"color INTEGER NOT NULL DEFAULT " + Color.BLACK + "," +
				"isComplete INTEGER NOT NULL DEFAULT 0," +
				"createTime INTEGER NOT NULL," +
				"completeTime INTEGER)"
	
	
	override fun equals(other: Any?): Boolean {
		return other is SqlCalendar1 && other._id == _id
	}
	
	override fun hashCode(): Int {
		var result = _id
		result = 31 * result + content.hashCode()
		result = 31 * result + remark.hashCode()
		result = 31 * result + time.hashCode()
		result = 31 * result + isComplete.hashCode()
		result = 31 * result + createTime.hashCode()
		result = 31 * result + (completeTime?.hashCode() ?: 0)
		return result
	}
	
	override fun toString(): String {
		return "SqlCalendar{_id=$_id,content=$content,remark=$remark,time=$time,color=$color,isComplete=$isComplete,createTime=$createTime,completeTime=$completeTime}"
	}
}
