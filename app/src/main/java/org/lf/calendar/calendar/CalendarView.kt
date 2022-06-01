package org.lf.calendar.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import androidx.fragment.app.Fragment
import org.lf.calendar.R
import java.util.*
import kotlin.collections.ArrayList

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
 * Parameter of date
 */
private const val PARAM_SELECTED_DATE = "calendar.date"

/**
 * The class is use to construct a calendar view use fragment
 */
class CalendarView : Fragment() {
	
	/* calendar */
	
	/**
	 * The day of today
	 */
	private var today: Calendar = Calendar.getInstance().also { it.time = Date() }
	
	/**
	 * The day of user selected
	 */
	var selectDate: Calendar = Calendar.getInstance().also { it.time = Date() }
	
	val year get() = selectDate[Calendar.YEAR]
	val month get() = selectDate[Calendar.MONTH]
	
	private val task = ArrayList<Runnable>()
	
	/* day data */
	/**
	 * A array contains the calendar's day on show
	 */
	private val daysArray = Array<Calendar>(7 * WEEK_TO_SHOW) { Calendar.getInstance() }
	
	/**
	 * A array contains the {@link org.lf.calendar.calendar.CalendarItemDay} on show
	 */
	private val dayViews = Array<CalendarItemDay?>(7 * WEEK_TO_SHOW) { null }
	
	/* view */
	
	/**
	 * The layout use to line up the item
	 */
	private lateinit var table: TableLayout
	
	/**
	 * Call when view on create, initial the year and month been selected and init days after initial
	 */
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		if(savedInstanceState != null) {
			selectDate.time.time = savedInstanceState.getLong(PARAM_SELECTED_DATE)
		}
		else {
			arguments?.let {
			}
		}


		initDays(selectDate[Calendar.YEAR], selectDate[Calendar.MONTH])
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.view_calendar, container, false)
	}
	
	/**
	 * Call on view been created, create the day item and show them
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		table = view.findViewById(R.id.calenderTable)
		
		createCalendarItem(false)
		
		table.post {
			if(task.isNotEmpty()) {
				for(t in task) {
					t.run()
				}
				task.clear()
			}
		}
		
	}
	
	/**
	 * Check us today
	 */
	private fun isToday(theDay: Calendar) = dateEquals(theDay, today)
	
	/**
	 * Check the day is equals
	 */
	private fun dateEquals(day1: Calendar, day2: Calendar) = monthEqual(day1, day2) &&
			day1.get(Calendar.DAY_OF_MONTH) == day2.get(Calendar.DAY_OF_MONTH)
	
	/**
	 * Check the month is equals
	 */
	private fun monthEqual(day1: Calendar, day2: Calendar) = day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR) &&
			day1.get(Calendar.MONTH) == day2.get(Calendar.MONTH)
	
	/**
	 * Save the data
	 */
	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putLong(PARAM_SELECTED_DATE, selectDate.time.time)
	}
	
	/**
	 * Change the day
	 * @param year - the year of the days
	 * @param month - the month of the days, 0 to 11
	 */
	fun changeDays(year: Int, month: Int, day: Int = selectDate[Calendar.DAY_OF_MONTH], func: () -> Unit = {}) {
		val t = {
			if(selectDate[Calendar.YEAR] != year || selectDate[Calendar.MONTH] != month) {
				selectDate.clear()
				selectDate.set(year, month, day)
				initDays(year, month)
				createCalendarItem(false)
			}
			else {
				selectDate.clear()
				selectDate.set(year, month, day)
				createCalendarItem(true)
			}
			func()
		}
		if(this::table.isInitialized) {
			table.post(t)
		}
		else {
			task.add(t)
		}
	}
	
	/**
	 * make this fragment can use post
	 */
	fun post(r: Runnable) {
		if(::table.isInitialized) {
			table.post(r)
		}
		else {
			task.add(r)
		}
	}
	
	/**
	 * make select day change to today
	 */
	fun changeToToday() {
		changeDays(today[Calendar.YEAR], today[Calendar.MONTH], today[Calendar.DAY_OF_MONTH])
	}
	
	/**
	 * Create item view or change selected day
	 */
	private fun createCalendarItem(justChangeSelect: Boolean) {
		if(!justChangeSelect) {
			val d = Calendar.getInstance().also { it.set(selectDate[Calendar.YEAR], selectDate[Calendar.MONTH], 1) }
			val c = OnItemSelected(this)
			
			table.removeAllViews()
			
			var nowRow = TableRow(context)
			
			for((viewIndex, day) in daysArray.withIndex()) {
				if(viewIndex % 7 == 0) {
					nowRow = TableRow(context)
					table.addView(nowRow)
				}
				
				val calendarItem = CalendarItemDay(requireContext())
				calendarItem.setDay(day, isToday(day), monthEqual(day, d))
				if(dateEquals(day, selectDate)) {
					calendarItem.isSelect = true
				}
				calendarItem.setOnClickListener(c)
				nowRow.addView(calendarItem)
				dayViews[viewIndex] = calendarItem
				
			}
		}
		for(dayIndex in daysArray.indices) {
			val day = daysArray[dayIndex]
			val view = dayViews[dayIndex]!! // must not null
			
			view.isSelect = dateEquals(day, selectDate)
		}
	}

	/**
	 * Initial days array
	 */
	private fun initDays(year: Int, month: Int) {
		val date = Calendar.getInstance().also { it.set(year, month, 0) }
		val dayWeek = date.get(Calendar.DAY_OF_WEEK) // 1 for Sunday...

		// check the first date of the calendar needed
		val firstDay = Calendar.getInstance().also { it.time = Date(date.time.time - MilliOfDay * (dayWeek - 1)) }

		var lastDay = firstDay.time.time

		for(i in 0 until (7 * WEEK_TO_SHOW)) {
			daysArray[i].time = Date(lastDay)
			lastDay += MilliOfDay
		}

	}

	companion object {
		@JvmStatic
		fun newInstance(time: Long) =
			org.lf.calendar.tabs.List().apply {
				arguments = Bundle().apply {
					this.putLong(PARAM_SELECTED_DATE, time)
				}
			}
	}
	
	/**
	 * on day be click
	 */
	class OnItemSelected(private val c: CalendarView) : View.OnClickListener {
		
		override fun onClick(v: View?) { // v always calendar item day
			v?.let {
				val cal = (it as CalendarItemDay).day
				c.parentFragment?.let { calit ->
					(calit as org.lf.calendar.tabs.Calendar)
						.setDay(cal[Calendar.YEAR],
							cal[Calendar.MONTH],
							cal[Calendar.DAY_OF_MONTH])
				}
			}
		}
		
	}
	
}