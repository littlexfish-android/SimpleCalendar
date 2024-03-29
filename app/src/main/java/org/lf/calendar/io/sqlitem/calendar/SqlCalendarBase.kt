package org.lf.calendar.io.sqlitem.calendar

import org.lf.calendar.io.sqlitem.Sqlable

abstract class SqlCalendarBase : Sqlable {
	
	/**
	 * @return SqlCalendar been upgraded
	 */
	abstract fun upgrade(sql: SqlCalendarBase, oldVersion: Int, newVersion: Int): SqlCalendarBase
	
	/**
	 * @return SqlCalendar been downgraded or throw error when it not support downgrade
	 */
	fun downgrade(sql: SqlCalendarBase, oldVersion: Int, newVersion: Int): SqlCalendarBase {
		throw IllegalStateException("sql downgrade")
	}
	
}