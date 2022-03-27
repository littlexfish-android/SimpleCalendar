package org.lf.calendar.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.lf.calendar.MainActivity
import org.lf.calendar.R
import org.lf.calendar.calendar.CalendarEditor
import org.lf.calendar.calendar.CalendarView
import org.lf.calendar.calendar.CalenderPlanList
import org.lf.calendar.io.SqlHelper

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
	
	private val task = ArrayList<Runnable>()
	
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
		
		val manager = childFragmentManager
		calendar = manager.findFragmentById(R.id.calendarCalendar) as CalendarView
		calendarPlans = manager.findFragmentById(R.id.calendarList) as CalenderPlanList
		yearMonth = view.findViewById<TextView>(R.id.calendarYearMonth).also {
			it.text = resources.getString(R.string.calendarYearMonth, calendar.year, calendar.month + 1)
		}
		
		view.findViewById<ImageView>(R.id.calendarPreMonth).setOnClickListener { onArrowButtonClick(it) }
		view.findViewById<ImageView>(R.id.calendarPreYear).setOnClickListener { onArrowButtonClick(it) }
		view.findViewById<ImageView>(R.id.calendarPostMonth).setOnClickListener { onArrowButtonClick(it) }
		view.findViewById<ImageView>(R.id.calendarPostYear).setOnClickListener { onArrowButtonClick(it) }
		view.findViewById<TextView>(R.id.calendarYearMonth).setOnLongClickListener {
			calendar.changeToToday()
			yearMonth.text = resources.getString(R.string.calendarYearMonth, calendar.year, calendar.month + 1)
			true
		}
		view.findViewById<FloatingActionButton>(R.id.calendarAddPlan).setOnClickListener { onAddPlan() }
		
		if(savedInstanceState == null) {




		}


		if(task.isNotEmpty()) {
			for(t in task) {
				t.run()
			}
			task.clear()
		}
		
	}
	
	/**
	 * Set the day of calendar
	 */
	fun setDay(year: Int, month: Int, day: Int) {
		val t = {
			calendar.changeDays(year, month, day)
			yearMonth.text = resources.getString(R.string.calendarYearMonth, calendar.year, calendar.month + 1)
			reloadFromSql(false)
		}
		if(this::calendar.isInitialized) {
			t()
		}
		else {
			task.add(t)
		}
	}
	
	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
	}
	
	/**
	 * Call when button has been click
	 */
	private fun onArrowButtonClick(v: View) {
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
		yearMonth.text = resources.getString(R.string.calendarYearMonth, calendar.year, calendar.month + 1)
	}
	
	private fun onAddPlan() {
		if(activity is MainActivity) {
			(activity as MainActivity).setFragmentToOther(CalendarEditor.newInstance())
		}
	}
	
	/**
	 * Reload from sqlite using SqlHelper
	 */
	fun reloadFromSql(force: Boolean = false) {
		if(force || !dateEquals(calendar.selectDate, calendarPlans.day)) {
			val day = calendar.selectDate
			val end = day.timeInMillis + (24*60*60*1000)
			val sqlHelper = SqlHelper.getInstance(context)
			val sqlCalendar = sqlHelper.getCalendar(sqlHelper.writableDatabase, orderBy = "time", timeMin = day.timeInMillis, timeMax = end).getCalendar()
			calendarPlans.removeAllPlan()
			for(sql in sqlCalendar) {
				calendarPlans.addPlan(sql.time.time, sql.content, sqlItem = sql)
			}
			calendarPlans.refreshList()
			calendarPlans.day.time = calendar.selectDate.time
		}
	}
	
	private fun dateEquals(day1: java.util.Calendar, day2: java.util.Calendar) =
		day1.get(java.util.Calendar.YEAR) == day2.get(java.util.Calendar.YEAR) &&
				day1.get(java.util.Calendar.MONTH) == day2.get(java.util.Calendar.MONTH)&&
				day1.get(java.util.Calendar.DAY_OF_MONTH) == day2.get(java.util.Calendar.DAY_OF_MONTH)
	
	companion object {
		@JvmStatic
		fun newInstance() =
			Calendar().apply {
				arguments = Bundle().apply { }
			}
	}
}