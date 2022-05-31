package org.lf.calendar.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import org.lf.calendar.MainActivity
import org.lf.calendar.R
import org.lf.calendar.TestActivity
import org.lf.calendar.io.SqlHelper
import org.lf.calendar.io.sqlitem.calendar.SqlCalendar1
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
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

private val WeekOfNames = arrayOf("S", "M", "T", "W", "T", "F", "S")

private const val CalendarRequestCodeOffset = 200
private const val ToolBarRequestCodeOffset = 0

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
		CalendarWidgetInternal.onEnable(context)
	}
	
	/**
	 * On last widget remove from home screen
	 */
	override fun onDisabled(context: Context) {
		// Enter relevant functionality for when the last widget is disabled
		CalendarWidgetInternal.onDeleteAll()
	}
	
	override fun onReceive(context: Context?, intent: Intent?) {
		super.onReceive(context, intent)
		if(intent != null && intent.extras != null) {
			val extra = intent.extras!!
//			Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
//			Toast.makeText(context, extra.getString("event", "null"), Toast.LENGTH_SHORT).show()
			when(extra.getString("event")) {
				"selectCalendar" -> {
					val id = extra.getInt("widgetId")
					val i = Intent(context, MainActivity::class.java)
					i.putExtra("event", "selectCalendar")
					i.putExtra("time", extra.getLong("time"))
					i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
					context?.let { CalendarWidgetInternal.updateAppWidget(context, AppWidgetManager.getInstance(context), id) }
					context?.startActivity(i)
				}
				"preMonth" -> {
					val id = extra.getInt("widgetId")
					var year = extra.getInt("year")
					var month = extra.getInt("month")
					month--
					if(month < 0) {
						year--
						month = 11
					}
					CalendarWidgetInternal.changeDays(id, year, month)
					context?.let { CalendarWidgetInternal.updateAppWidget(context, AppWidgetManager.getInstance(context), id) }
				}
				"postMonth" -> {
					val id = extra.getInt("widgetId")
					var year = extra.getInt("year")
					var month = extra.getInt("month")
					month++
					if(month > 11) {
						year++
						month = 0
					}
					CalendarWidgetInternal.changeDays(id, year, month)
					context?.let { CalendarWidgetInternal.updateAppWidget(context, AppWidgetManager.getInstance(context), id) }
				}
			}
		}
	}
	
	fun openTest(context: Context?, str: String) {
		val i = Intent(context, TestActivity::class.java)
		i.putExtra("test", str)
		i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
		context?.startActivity(i)
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
	val calendarItemsForWidget = HashMap<Int, Array<RemoteViews>>()
	
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
	@SuppressLint("SimpleDateFormat")
	fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
		// Construct the RemoteViews object
		val views = RemoteViews(context.packageName, R.layout.calendar_widget)
		views.removeAllViews(R.id.calendarWidgetWeek)
		views.removeAllViews(R.id.calendarWidgetCalendar)
		views.removeAllViews(R.id.calendarWidgetPlans)
		
		// init week names
		for(i in WeekOfNames) {
			val v = RemoteViews(context.packageName, R.layout.widget_calendar_week_item)
			v.setTextViewText(R.id.widgetCalendarWeekItemWeek, i)
			views.addView(R.id.calendarWidgetWeek, v)
		}
		
		// init days array
		val y = yearForWidget[appWidgetId] ?: today[Calendar.YEAR].also { yearForWidget[appWidgetId] = it }
		val m = monthForWidget[appWidgetId] ?: today[Calendar.MONTH].also { monthForWidget[appWidgetId] = it }
		changeDays(appWidgetId, y, m)
//		initDays(appWidgetId)

		val daysArray = daysArrayForWidget[appWidgetId]!!

		val list = calendarItemsForWidget[appWidgetId] ?: Array(7 * WEEK_TO_SHOW) { RemoteViews(context.packageName, R.layout.widget_calendar_view_item) }.also { calendarItemsForWidget[appWidgetId] = it }
		val theDay = Calendar.getInstance().also { it.set(yearForWidget[appWidgetId]!!, monthForWidget[appWidgetId]!!, 1) }
		views.setTextViewText(R.id.calendarWidgetYearMonth, context.resources.getString(R.string.calendarYearMonth, yearForWidget[appWidgetId]!!, monthForWidget[appWidgetId]!! + 1))
		
		for((i, it) in list.withIndex()) {
			it.setTextViewText(R.id.widgetCalendarViewItemDay, "${daysArray[i][Calendar.DAY_OF_MONTH]}")
			when {
				isToday(daysArray[i]) -> {
					it.setTextColor(R.id.widgetCalendarViewItemDay, context.resources.getColor(R.color.default_theme_color, null))
				}
				monthEqual(daysArray[i], theDay) -> {
					it.setTextColor(R.id.widgetCalendarViewItemDay, context.resources.getColor(R.color.calendar_current_month_white, null))
				}
				else -> {
					it.setTextColor(R.id.widgetCalendarViewItemDay, context.resources.getColor(R.color.calendar_not_current_month, null))
				}
			}
			
			views.addView(R.id.calendarWidgetCalendar, it)
			
			val intentToStart = Intent(context, CalendarWidget::class.java)
			intentToStart.putExtra("event", "selectCalendar")
			intentToStart.putExtra("time", daysArray[i].time.time)
			intentToStart.putExtra("widgetId", appWidgetId)
//			intentToStart.flags = Intent.FLAG_ACTIVITY_NEW_TASK
			it.setOnClickPendingIntent(R.id.widgetCalendarViewItemDay, PendingIntent.getBroadcast(context, CalendarRequestCodeOffset + i, intentToStart, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))
		}
		
		val td = Calendar.getInstance()
		td.set(today[Calendar.YEAR], today[Calendar.MONTH], today[Calendar.DAY_OF_MONTH], 0, 0, 0)
		val last = td.time.time + (24 * 60 * 60 * 1000)
		val sql = SqlHelper.getInstance(context)
		val plan = sql.getCalendar(sql.readableDatabase, "WHERE time > ${td.time.time} AND time < $last")
		val plans = plan.getCalendar()
		
		for(it in plans) {
			val view = RemoteViews(context.packageName, R.layout.view_plan_item)
			val time = SimpleDateFormat("HH:mm").format(it.time)
			view.setTextViewText(R.id.plan_content, it.content)
			view.setTextViewText(R.id.plan_time, time)
			views.addView(R.id.calendarWidgetPlans, view)
		}
		
		// register arrow button
		val preArrowIntent = Intent(context, CalendarWidget::class.java)
		preArrowIntent.putExtra("event", "preMonth")
		preArrowIntent.putExtra("widgetId", appWidgetId)
		preArrowIntent.putExtra("year", yearForWidget[appWidgetId])
		preArrowIntent.putExtra("month", monthForWidget[appWidgetId])
		views.setOnClickPendingIntent(R.id.calendarWidgetPreMonth, PendingIntent.getBroadcast(context, ToolBarRequestCodeOffset + 1, preArrowIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))
		
		val postArrowIntent = Intent(context, CalendarWidget::class.java)
		postArrowIntent.putExtra("event", "postMonth")
		postArrowIntent.putExtra("widgetId", appWidgetId)
		postArrowIntent.putExtra("year", yearForWidget[appWidgetId])
		postArrowIntent.putExtra("month", monthForWidget[appWidgetId])
		views.setOnClickPendingIntent(R.id.calendarWidgetPostMonth, PendingIntent.getBroadcast(context, ToolBarRequestCodeOffset + 2, postArrowIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))
		
		// Instruct the widget manager to update the widget
		appWidgetManager.updateAppWidget(appWidgetId, views)
	}
	
	fun onEnable(context: Context) {
	
	}
	
	/**
	 * Check is today
	 */
	private fun isToday(theDay: Calendar) = monthEqual(theDay, today) &&
			theDay.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
	
	/**
	 * Check is current month
	 */
	private fun monthEqual(day1: Calendar, day2: Calendar) = day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR) &&
			day1.get(Calendar.MONTH) == day2.get(Calendar.MONTH)
	
	/**
	 * On each widget been remove
	 */
	fun onWidgetDelete(appWidgetId: Int) {
		daysArrayForWidget.remove(appWidgetId)
		monthForWidget.remove(appWidgetId)
		yearForWidget.remove(appWidgetId)
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
		
		val date = Calendar.getInstance().also { oit -> oit.set(yearForWidget[appWidgetId] ?: today[Calendar.YEAR].also { yearForWidget[appWidgetId] = it },
			(monthForWidget[appWidgetId] ?: today[Calendar.MONTH].also { monthForWidget[appWidgetId] = it }), 0) }
		val dayWeek = date.get(Calendar.DAY_OF_WEEK) // 1 for Sunday...
		
		// check the first date of the calendar needed
		val firstDay = Calendar.getInstance().also { it.time = Date(date.time.time - MilliOfDay * (dayWeek - 1)) }
		
		var lastDay = firstDay.time.time
		
		for(i in 0 until (7 * WEEK_TO_SHOW)) {
			daysArray[i].time = Date(lastDay)
			lastDay += MilliOfDay
		}
		
	}
	
}
