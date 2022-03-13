package org.lf.calendar.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import org.lf.calendar.R

/**
 * The class contains all of plans in the day
 */
class CalenderPlanList : Fragment() {
	
	/**
	 * List view of the class
	 */
	lateinit var list: LinearLayout
	
	/**
	 * Plan item of the day
	 */
	var planArray = ArrayList<PlanItem>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_calender_plan_list, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		list = view.findViewById(R.id.plan_list)

	}
	
	/**
	 * Add plan item into list
	 */
	fun addPlan(time: Long, content: String) {
		if(context == null) return
		val plan = PlanItem(requireContext())
		plan.setContent(time, content)

		sortArray()
		refreshList()
	}
	
	/**
	 * Sort the array by time
	 */
	private fun sortArray() {
		planArray.sortWith { o1, o2 ->
			when {
				o1.time > o2.time -> 1
				o1.time == o2.time -> 0
				else -> -1
			}
		}
	}
	
	/**
	 * Refresh the list, detach the views from list and re-add into the list order by the array
	 */
	private fun refreshList() {
		list.removeAllViews()
		for(item in planArray) {
			list.addView(item)
		}
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