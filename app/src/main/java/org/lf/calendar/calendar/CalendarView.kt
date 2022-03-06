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

private const val WEEK_TO_SHOW = 6

private const val MilliOfSecond = 1000
private const val MilliOfMinute = MilliOfSecond * 60
private const val MilliOfHour = MilliOfMinute * 60
private const val MilliOfDay = MilliOfHour * 24

private const val PARAM_YEAR = "calendar.year"
private const val PARAM_MONTH = "calendar.month"
private const val PARAM_DAY = "calendar.day"

class CalendarView : Fragment() {
	
	var today = Calendar.getInstance().also { it.time = Date() }
	var selectDate = Calendar.getInstance().also { it.time = Date() }
	var month = 0
	var year = 0
	private val daysArray = Array<Calendar>(7 * WEEK_TO_SHOW) { Calendar.getInstance() }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		if(savedInstanceState == null) {
			today = Calendar.getInstance().also { it.time = Date() }

			val year = today.get(Calendar.YEAR)
			val month = today.get(Calendar.MONTH)

			this.month = month
			this.year = year
		}
		else {
			year = savedInstanceState.getInt("year")
			month = savedInstanceState.getInt("month")
		}

		arguments?.let {
			year = it.getInt(PARAM_YEAR)
			month = it.getInt(PARAM_MONTH)
		}

		initDays(year, month)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.view_calendar, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		val table = view.findViewById<TableLayout>(R.id.calender_table)
		
		var count = 0
		var nowRow = TableRow(requireContext())
		
		for(day in daysArray) {
			if(count == 0) {
				nowRow = TableRow(requireContext())
				table.addView(nowRow)
			}
			
			val calendarItem = CalendarItemDay(requireContext())
			calendarItem.setDay(day, isToday(day), isCurrentMonth(day))
			if(dateEquals(day, selectDate)) {
				calendarItem.isSelect = true
			}
			nowRow.addView(calendarItem)
			
			count++
			if(count == 7) {
				count = 0
			}
		}

	}
	
	private fun isToday(theDay: Calendar) = dateEquals(theDay, today)
	
	private fun isCurrentMonth(theDay: Calendar) = monthEqual(theDay, today)
	
	private fun dateEquals(day1: Calendar, day2: Calendar) = monthEqual(day1, day2) &&
			day1.get(Calendar.MONTH) == day2.get(Calendar.MONTH)
	
	private fun monthEqual(day1: Calendar, day2: Calendar) = day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR) &&
			day1.get(Calendar.MONTH) == day2.get(Calendar.MONTH)
	
	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putInt("year", year)
		outState.putInt("month", month)
	}
	/**
	 * @param year - the year of the days
	 * @param month - the month of the days, 1 to 12
	 */
	fun changeDays(year: Int, month: Int, day: Int) {
		selectDate.clear()
		selectDate.set(year, month - 1, day)
		initDays(year, month)
	}

	/**
	 * Initial days array
	 */
	private fun initDays(year: Int, month: Int) {
		val date = Calendar.getInstance().also { it.set(year, month - 1, 0) }
		val dayWeek = date.get(Calendar.DAY_OF_WEEK) // 1 for Sunday...

		// check the first date of the calendar needed
		val firstDay = Calendar.getInstance().also { it.time = Date(date.time.time - MilliOfDay * (dayWeek - 1)) }

		var lastDay = firstDay.time.time

		for(i in 0..(7 * WEEK_TO_SHOW)) {
			daysArray[i].time = Date(lastDay)
			lastDay += MilliOfDay
		}

	}

	companion object {
		@JvmStatic
		fun newInstance(year: Int = 2022, month: Int = 0) =
			org.lf.calendar.tabs.List().apply {
				arguments = Bundle().apply {
					putInt(PARAM_YEAR, year)
					putInt(PARAM_MONTH, month)
				}
			}
	}

}