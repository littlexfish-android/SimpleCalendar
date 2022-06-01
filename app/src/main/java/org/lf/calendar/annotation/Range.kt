package org.lf.calendar.annotation

interface Range {
	
	@Target(AnnotationTarget.FIELD, AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.PROPERTY)
	@Retention(AnnotationRetention.SOURCE)
	annotation class IntRange(val from: Int = Int.MIN_VALUE, val to: Int = Int.MAX_VALUE, val nullable: Boolean = false)
	
}