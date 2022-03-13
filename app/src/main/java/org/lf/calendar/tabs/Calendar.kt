package org.lf.calendar.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.lf.calendar.R
import org.lf.calendar.calendar.CalendarView
import org.lf.calendar.calendar.CalenderPlanList

/**
 * The class is a tab of main activity, use to show calendar
 */
class Calendar : Fragment() {
	
	/**
	 * The calendar
	 */
	lateinit var calendar: CalendarView
	
	/**
	 * The calendar plans
	 */
	lateinit var calendarPlans: CalenderPlanList
	
	/**
	 * The year month of calendar
	 */
	lateinit var yearMonth: TextView

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let { }
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_calendar, container, false)
	}
	
	/**
	 * Initial views
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		val manager = parentFragmentManager
		calendar = manager.findFragmentById(R.id.calendarCalendar) as CalendarView
		calendarPlans = manager.findFragmentById(R.id.calendarList) as CalenderPlanList
		
		yearMonth = view.findViewById<TextView>(R.id.calendarYearMonth).also {
			it.text = resources.getString(R.string.calendarYearMonth, calendar.year, calendar.month)
		}
		
		if(savedInstanceState == null) {




		}



	}
	
	/**
	 * Set the day of calendar
	 */
	fun setDay(year: Int, month: Int, day: Int) {
		calendar.changeDays(year, month, day)
		yearMonth.text = resources.getString(R.string.calendarYearMonth, calendar.year, calendar.month)
		// TODO: change calendar plans
	}
	
	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
	}
	
	/**
	 * Call when button has been click
	 */
	fun onArrowButtonClick(v: View) {
		var year = calendar.year
		var month = calendar.month
		when(v.id) {
			R.id.calendarPreMonth -> {
				if(month <= 1) {
					month = 12
					year--
				}
				else month--
			}
			R.id.calendarPostMonth -> {
				if(month >= 12) {
					month = 1
					year++
				}
				else month++
			}
			R.id.calendarPreYear -> {
				year--
			}
			R.id.calendarPostYear -> {
				year++
			}
		}
		calendar.changeDays(year, month)
	}
	
	companion object {
		@JvmStatic
		fun newInstance() =
			Calendar().apply {
				arguments = Bundle().apply { }
			}
	}
}