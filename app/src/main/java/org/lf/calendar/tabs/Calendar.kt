package org.lf.calendar.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentContainerView
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
		//TODO: add long click on text view to switch to today
		
		
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
			// TODO: change calendar plans
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
	
	companion object {
		@JvmStatic
		fun newInstance() =
			Calendar().apply {
				arguments = Bundle().apply { }
			}
	}
}