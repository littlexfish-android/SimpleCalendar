package org.lf.calendar.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView
import org.lf.calendar.R
import org.lf.calendar.calendar.CalendarView
import org.lf.calendar.calendar.CalenderPlanList
import java.time.Instant
import java.util.*

class Calendar : Fragment() {

	lateinit var calendar: CalendarView
	lateinit var calendarPlans: CalenderPlanList

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let { }
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_calendar, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val manager = parentFragmentManager
		calendar = manager.findFragmentById(R.id.calendar_calendar) as CalendarView
		calendarPlans = manager.findFragmentById(R.id.calendar_list) as CalenderPlanList

		if(savedInstanceState == null) {




		}



	}
	
	fun setDay(year: Int, month: Int, day: Int) {
		calendar.changeDays(year, month, day)
	}
	
	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
	}

	companion object {
		@JvmStatic
		fun newInstance() =
			Calendar().apply {
				arguments = Bundle().apply { }
			}
	}
}