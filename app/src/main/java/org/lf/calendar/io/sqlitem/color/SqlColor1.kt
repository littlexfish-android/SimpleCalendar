package org.lf.calendar.io.sqlitem.color

import android.content.ContentValues
import android.database.Cursor
import androidx.core.database.getStringOrNull
import org.intellij.lang.annotations.Language
import java.util.Objects

class SqlColor1 : SqlColorBase() {
	
	var color: Int = -1
	var name: String? = null
	
	override fun upgrade(sql: SqlColorBase, oldVersion: Int, newVersion: Int): SqlColorBase = this
	
	override fun initFromDatabase(cursor: Cursor) {
		color = cursor.getInt(0)
		name = cursor.getStringOrNull(1)
	}
	
	override fun getContentValues(): ContentValues {
		val contentValue = ContentValues()
		contentValue.put("color", color)
		contentValue.put("name", name)
		return contentValue
	}
	
	@Language("SQL")
	override fun getOnCreateCommand(tableName: String): String =
		"CREATE TABLE IF NOT EXISTS $tableName (" +
				"color INTEGER PRIMARY KEY NOT NULL," +
				"name TEXT)"
	
	override fun equals(other: Any?): Boolean {
		return other is SqlColor1 && other.color == color
	}
	
	override fun hashCode(): Int {
		return Objects.hash(color, name)
	}
	
	override fun toString(): String {
		return "SqlColor{color=$color,name=$name}"
	}
	
}