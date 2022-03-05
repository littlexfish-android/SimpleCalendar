package org.lf.calendar.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.lf.calendar.R

class CalenderPlanList : Fragment() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_calender_plan_list, container, false)
	}

	fun addPlan() {
		// TODO: init plan item and add to linear layout
	}

	companion object {
		@JvmStatic
		fun newInstance() =
			CalenderPlanList().apply {
				arguments = Bundle().apply {
				}
			}
	}
}