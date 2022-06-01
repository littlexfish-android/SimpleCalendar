package org.lf.calendar.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import org.lf.calendar.MainActivity
import org.lf.calendar.R
import org.lf.calendar.io.sqlitem.calendar.SqlCalendar1
import java.util.*
import kotlin.collections.ArrayList

/**
 * The class contains all of plans in the day
 */
class CalenderPlanList : Fragment() {
	
	/**
	 * Day use to check
	 */
	val day: Calendar = Calendar.getInstance().also { it.time = Date() }
	
	/**
	 * List view of the class
	 */
	lateinit var list: LinearLayout
	
	/**
	 * Plan item of the day
	 */
	private var planArray = ArrayList<PlanItem>()
	
	/**
	 * the task will run on view created
	 */
	private val task = ArrayList<Runnable>()

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
		list = view.findViewById(R.id.planList)
		
		if(task.isNotEmpty()) {
			for(t in task) {
				t.run()
			}
		}
		
	}
	
	/**
	 * Add plan item into list
	 */
	fun addPlan(time: Long, content: String, refresh: Boolean = false, sqlItem: SqlCalendar1? = null) {
		addPlanItem(time, content, sqlItem)
		
		if(refresh) {
			refreshList()
		}
	}
	
	/**
	 * add plan item view
	 */
	private fun addPlanItem(time: Long, content: String, sqlItem: SqlCalendar1? = null) {
		if(context == null) return
		val plan = PlanItem(requireContext())
		if(sqlItem != null) plan.attachSqlItem(sqlItem)
		else plan.setContent(time, content)
		if(sqlItem != null) {
			plan.setOnClickListener {
				val act = requireActivity() as MainActivity
				act.setFragmentToOther(CalendarPlanShower.newInstance(sqlItem._id), sqlItem.content)
			}
		}
		list.addView(plan)
		planArray.add(plan)
	}
	
	/**
	 * Sort the array by time
	 */
	private fun sortArray() {
		planArray.sortWith { o1, o2 ->
			when {
				o1.time.timeInMillis > o2.time.timeInMillis -> 1
				o1.time.timeInMillis == o2.time.timeInMillis -> 0
				else -> -1
			}
		}
	}
	
	/**
	 * remove all plan view and data
	 */
	fun removeAllPlan() {
		if(::list.isInitialized) {
			list.removeAllViews()
			planArray.clear()
		}
	}
	
	/**
	 * Refresh the list, detach the views from list and re-add into the list order by the array
	 */
	fun refreshList() {
		val t = {
			for(item in planArray) {
				list.addView(item)
			}
		}
		sortArray()
		if(::list.isInitialized) {
			list.removeAllViews()
			t()
		}
		else {
			task.add(t)
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