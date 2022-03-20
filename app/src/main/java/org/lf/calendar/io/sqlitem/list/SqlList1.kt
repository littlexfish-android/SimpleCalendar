package org.lf.calendar.io.sqlitem.list

import android.content.ContentValues
import android.database.Cursor
import androidx.core.database.getLongOrNull
import org.lf.calendar.io.sqlitem.Sqlable
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
 */
class SqlList1 : SqlListBase {
	
	var _id: Int = -1
	var groupName: String = ""
	var content: String = ""
	var isComplete: Boolean = false
	var createTime: Date = Date()
	var completeTime: Date? = null
	
	constructor() : super()
	
	constructor(groupName: String, content: String, createTime: Date) : super() {
		this.groupName = groupName
		this.content = content
		this.createTime = createTime
	}
	
	override fun initFromDatabase(cursor: Cursor) {
		_id = cursor.getInt(0)
		groupName = cursor.getString(1)
		content = cursor.getString(2)
		isComplete = cursor.getInt(3) != 0
		createTime = Date(cursor.getLong(4))
		val tmpTime = cursor.getLongOrNull(5)
		completeTime = if(tmpTime == null) null else Date(tmpTime)
	}
	
	override fun getContentValues(): ContentValues {
		val contentValue = ContentValues()
		contentValue.put("category", groupName)
		contentValue.put("content", content)
		contentValue.put("isComplete", if(isComplete) 1 else 0)
		contentValue.put("createTime", createTime.time)
		completeTime?.let {
			contentValue.put("completeTime", it.time)
		}
		return contentValue
	}
	
	override fun upgrade(version: Int): SqlListBase {
		return this
	}
	
	override fun equals(other: Any?): Boolean {
		return other == this || (other is SqlList1 && (other.groupName == groupName && other.content == content))
	}
	
	override fun hashCode(): Int {
		var result = _id
		result = 31 * result + groupName.hashCode()
		result = 31 * result + content.hashCode()
		result = 31 * result + isComplete.hashCode()
		result = 31 * result + createTime.hashCode()
		result = 31 * result + (completeTime?.hashCode() ?: 0)
		return result
	}
	
}
