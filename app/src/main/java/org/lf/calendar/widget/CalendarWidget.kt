package org.lf.calendar.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import org.lf.calendar.MainActivity
import org.lf.calendar.R
import java.util.*
import kotlin.collections.HashMap

/**
 * How amount of weeks show on this calendar
 */
private const val WEEK_TO_SHOW = 6

/**
 * The milli-seconds of a second
 */
private const val MilliOfSecond = 1000
/**
 * The milli-second of a minute
 */
private const val MilliOfMinute = MilliOfSecond * 60
/**
 * The milli-second of an hour
 */
private const val MilliOfHour = MilliOfMinute * 60
/**
 * The milli-second of a day
 */
private const val MilliOfDay = MilliOfHour * 24

/**
 * The calendar use to show on the device home screen
 */
class CalendarWidget : AppWidgetProvider() {
	
	/**
	 * On widget update view
	 */
	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
		// There may be multiple widgets active, so update all of them
		for (appWidgetId in appWidgetIds) {
			CalendarWidgetInternal.updateAppWidget(context, appWidgetManager, appWidgetId)
		}
	}
	
	/**
	 * On delete any widget
	 */
	override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
		super.onDeleted(context, appWidgetIds)
		if(appWidgetIds != null) {
			for(appWidgetId in appWidgetIds) {
				CalendarWidgetInternal.onWidgetDelete(appWidgetId)
			}
		}
	}
	
	/**
	 * On widget id changed
	 */
	override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
		super.onRestored(context, oldWidgetIds, newWidgetIds)
		CalendarWidgetInternal.onIdChange(oldWidgetIds, newWidgetIds)
	}
	
	/**
	 * On first widget insert into home screen
	 */
	override fun onEnabled(context: Context) {
		// Enter relevant functionality for when the first widget is created
	}
	
	/**
	 * On last widget remove from home screen
	 */
	override fun onDisabled(context: Context) {
		// Enter relevant functionality for when the last widget is disabled
		CalendarWidgetInternal.onDeleteAll()
	}
}

/**
 * The main function of the calendar widget
 */
private object CalendarWidgetInternal {
	
	/**
	 * The day of today
	 */
	var today: Calendar = Calendar.getInstance().also { it.time = Date() }
	
	/**
	 * The days of each widget
	 */
	val calendarItemIdsForWidget = HashMap<Int, IntArray>()
	
	/**
	 * The year of each widget
	 */
	val yearForWidget = HashMap<Int, Int>()
	
	/**
	 * The month of each widget
	 */
	val monthForWidget = HashMap<Int, Int>()
	
	/**
	 * The calendars of each widget
	 */
	val daysArrayForWidget = HashMap<Int, Array<Calendar>>()
	
	/**
	 * On each widget update
	 */
	fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
		// Construct the RemoteViews object
		val views = RemoteViews(context.packageName, R.layout.calendar_widget)
		
		// init days array
		initDays(appWidgetId)
		
		val daysArray = daysArrayForWidget[appWidgetId]!!
		
		val list = calendarItemIdsForWidget[appWidgetId] ?: IntArray(7 * WEEK_TO_SHOW).also { calendarItemIdsForWidget[appWidgetId] = it }
		
		// get the views
		list[0] = R.id.calendar_widget_0_0
		list[1] = R.id.calendar_widget_0_1
		list[2] = R.id.calendar_widget_0_2
		list[3] = R.id.calendar_widget_0_3
		list[4] = R.id.calendar_widget_0_4
		list[5] = R.id.calendar_widget_0_5
		list[6] = R.id.calendar_widget_0_6
		list[7] = R.id.calendar_widget_1_0
		list[8] = R.id.calendar_widget_1_1
		list[9] = R.id.calendar_widget_1_2
		list[10] = R.id.calendar_widget_1_3
		list[11] = R.id.calendar_widget_1_4
		list[12] = R.id.calendar_widget_1_5
		list[13] = R.id.calendar_widget_1_6
		list[14] = R.id.calendar_widget_2_0
		list[15] = R.id.calendar_widget_2_1
		list[16] = R.id.calendar_widget_2_2
		list[17] = R.id.calendar_widget_2_3
		list[18] = R.id.calendar_widget_2_4
		list[19] = R.id.calendar_widget_2_5
		list[20] = R.id.calendar_widget_2_6
		list[21] = R.id.calendar_widget_3_0
		list[22] = R.id.calendar_widget_3_1
		list[23] = R.id.calendar_widget_3_2
		list[24] = R.id.calendar_widget_3_3
		list[25] = R.id.calendar_widget_3_4
		list[26] = R.id.calendar_widget_3_5
		list[27] = R.id.calendar_widget_3_6
		list[28] = R.id.calendar_widget_4_0
		list[29] = R.id.calendar_widget_4_1
		list[30] = R.id.calendar_widget_4_2
		list[31] = R.id.calendar_widget_4_3
		list[32] = R.id.calendar_widget_4_4
		list[33] = R.id.calendar_widget_4_5
		list[34] = R.id.calendar_widget_4_6
		list[35] = R.id.calendar_widget_5_0
		list[36] = R.id.calendar_widget_5_1
		list[37] = R.id.calendar_widget_5_2
		list[38] = R.id.calendar_widget_5_3
		list[39] = R.id.calendar_widget_5_4
		list[40] = R.id.calendar_widget_5_5
		list[41] = R.id.calendar_widget_5_6
		
		
		// set button to open the activity
		for(i in 0 until (7 * WEEK_TO_SHOW)) {
			views.setTextViewText(list[i], "${daysArray[i][Calendar.DAY_OF_MONTH]}")
			if(!isCurrentMonth(daysArray[i])) {
				views.setTextColor(list[i], context.resources.getColor(R.color.calendar_not_current_month, null))
			}
			else if(isToday(daysArray[i])) {
				views.setTextColor(list[i], context.resources.getColor(R.color.default_theme_color, null))
			}
			
			val intentToStart = Intent(context, MainActivity::class.java)
			intentToStart.putExtra("event", "selectCalendar")
			intentToStart.putExtra("time", daysArray[i].time.time)
			intentToStart.flags = Intent.FLAG_ACTIVITY_NEW_TASK
			val pi = PendingIntent.getActivity(context, 0, intentToStart, PendingIntent.FLAG_IMMUTABLE)
			views.setOnClickPendingIntent(list[i], pi)
		}
		
		// Instruct the widget manager to update the widget
		appWidgetManager.updateAppWidget(appWidgetId, views)
	}
	
	/**
	 * Check is today
	 */
	fun isToday(theDay: Calendar) = isCurrentMonth(theDay) &&
			theDay.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
	
	/**
	 * Check is current month
	 */
	fun isCurrentMonth(theDay: Calendar) = theDay.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
			theDay.get(Calendar.MONTH) == today.get(Calendar.MONTH)
	
	/**
	 * On each widget been remove
	 */
	fun onWidgetDelete(appWidgetId: Int) {
		daysArrayForWidget.remove(appWidgetId)
	}
	
	/**
	 * Remove all widget and its data
	 */
	fun onDeleteAll() {
		yearForWidget.clear()
		monthForWidget.clear()
		daysArrayForWidget.clear()
	}
	
	/**
	 * On id been changed, remap to new id
	 */
	fun onIdChange(oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
		if(oldWidgetIds != null && newWidgetIds != null) {
			val yearClone = HashMap<Int, Int>(yearForWidget)
			val monthClone = HashMap<Int, Int>(monthForWidget)
			val daysClone = HashMap<Int, Array<Calendar>>(daysArrayForWidget)
			
			onDeleteAll()
			
			for(i in oldWidgetIds.indices) {
				val oldId = oldWidgetIds[i]
				val newId = newWidgetIds[i]
				
				yearClone[oldId]?.let { yearForWidget[newId] = it }
				monthClone[oldId]?.let { monthForWidget[newId] = it }
				daysClone[oldId]?.let { daysArrayForWidget[newId] = it }
			}
			
		}
		
	}
	
	/**
	 * Change the day
	 * @param year - the year of the days
	 * @param month - the month of the days, 1 to 12
	 */
	fun changeDays(appWidgetId: Int, year: Int, month: Int) {
		yearForWidget[appWidgetId] = year
		monthForWidget[appWidgetId] = month
		initDays(appWidgetId)
	}
	
	/**
	 * Initial days array
	 */
	private fun initDays(appWidgetId: Int) {
		val daysArray = daysArrayForWidget[appWidgetId] ?: Array<Calendar>(7 * WEEK_TO_SHOW) { Calendar.getInstance() }.also { daysArrayForWidget[appWidgetId] = it }
		val date = Calendar.getInstance()
		date.set(yearForWidget[appWidgetId] ?: today[Calendar.YEAR].also { yearForWidget[appWidgetId] = it },
			(monthForWidget[appWidgetId] ?: today[Calendar.MONTH].also { monthForWidget[appWidgetId] = it }), 0)
		val day = date.get(Calendar.DAY_OF_WEEK) // 1 for Sunday...
		
		// check the first date of the calendar needed
		val firstDay = Calendar.getInstance().also { it.time = Date(date.time.time - MilliOfDay * (day - 1)) }
		
		var lastDay = firstDay.time.time
		
		for(i in 0 until (7 * WEEK_TO_SHOW)) {
			daysArray[i].clear()
			daysArray[i].time = Date(lastDay)
			lastDay += MilliOfDay
		}
		
	}
	
}
