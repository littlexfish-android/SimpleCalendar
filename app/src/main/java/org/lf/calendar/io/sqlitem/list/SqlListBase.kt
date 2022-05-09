package org.lf.calendar.io.sqlitem.list

import org.lf.calendar.io.sqlitem.Sqlable

abstract class SqlListBase : Sqlable {
	
	/**
	 * @return SqlList been upgraded
	 */
	abstract fun upgrade(sql: SqlListBase, oldVersion: Int, newVersion: Int): SqlListBase
	
	/**
	 * @return SqlList been downgraded or throw error when it not support downgrade
	 */
	fun downgrade(sql: SqlListBase, oldVersion: Int, newVersion: Int): SqlListBase {
		throw IllegalStateException("sql downgrade")
	}
	
}