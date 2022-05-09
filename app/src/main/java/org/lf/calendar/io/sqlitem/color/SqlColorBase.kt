package org.lf.calendar.io.sqlitem.color

import org.lf.calendar.io.sqlitem.Sqlable

abstract class SqlColorBase : Sqlable {
	
	/**
	 * @return SqlCalendar been upgraded
	 */
	abstract fun upgrade(sql: SqlColorBase, oldVersion: Int, newVersion: Int): SqlColorBase
	
	/**
	 * @return SqlCalendar been downgraded or throw error when it not support downgrade
	 */
	fun downgrade(sql: SqlColorBase, oldVersion: Int, newVersion: Int): SqlColorBase {
		throw IllegalStateException("sql downgrade")
	}
	
}