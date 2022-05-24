package org.lf.calendar.calendar

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.lf.calendar.MainActivity
import org.lf.calendar.R
import org.lf.calendar.databinding.FragmentCalendarPlanShowerBinding
import org.lf.calendar.io.SqlHelper
import org.lf.calendar.io.sqlitem.calendar.SqlCalendar1
import org.lf.calendar.list.ListItemShower
import java.util.*

const val PARAM_ID = "calendar.viewer.id"

class CalendarPlanShower : Fragment() {
	
	private lateinit var binder: FragmentCalendarPlanShowerBinding
	private var planId: Int = 0
	private lateinit var sqlItem: SqlCalendar1
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			planId = it.getInt(PARAM_ID)
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		binder = FragmentCalendarPlanShowerBinding.inflate(inflater)
		return binder.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val sql = SqlHelper.getInstance(context)
		
		Log.e("TAG", planId.toString()) // FIXME: remove
		sqlItem = sql.getCalendar(sql.readableDatabase, "WHERE _id==$planId").getCalendar()[0]
		
		val cal = Calendar.getInstance().also { it.time = sqlItem.time }
		
		binder.calendarShowTime.text.append(
			resources.getString(R.string.calendarShowerTimeFormat,
				cal[Calendar.YEAR], cal[Calendar.MONTH] + 1, cal[Calendar.DAY_OF_MONTH], cal[Calendar.HOUR_OF_DAY], cal[Calendar.MINUTE]))
		
		binder.calendarShowerContent.text.append(sqlItem.content)
		binder.calendarShowerContent.setTextColor(sqlItem.color)
		sqlItem.remark?.let { binder.calendarShowerRemark.text.append(it) }
		
		binder.calendarShowerReminder.visibility = if(sqlItem.remindTime == null) View.GONE else View.VISIBLE
		if(sqlItem.remindTime != null) {
			val cal2 = Calendar.getInstance().also { it.time = sqlItem.remindTime!! }
			binder.calendarShowerReminder.text.clear()
			binder.calendarShowerReminder.text.append(
				resources.getString(R.string.calendarShowerReminderFormat,
					cal2[Calendar.YEAR], cal2[Calendar.MONTH] + 1, cal2[Calendar.DAY_OF_MONTH], cal2[Calendar.HOUR_OF_DAY], cal2[Calendar.MINUTE]))
		}
		binder.calendarShowerOpenList.visibility = if(sqlItem.listId == null) View.GONE else View.VISIBLE
		
		if(sqlItem.listId != null) {
			binder.calendarShowerOpenList.setOnClickListener {
				val act = requireActivity() as MainActivity
				act.setFragmentToOther(ListItemShower.newInstance(sqlItem.listId!!), "代辦")
			}
		}
		
		binder.calendarShowerEdit.setOnClickListener {
			// TODO: edit
		}
		binder.calendarShowerClose.setOnClickListener {
			val act = requireActivity() as MainActivity
			act.setFragmentToCalendar(reload = false)
			act.setCalendarDay(cal[Calendar.YEAR], cal[Calendar.MONTH] + 1, cal[Calendar.DAY_OF_MONTH]) // FIXME: not switch
		}
	}
	
	companion object {
		@JvmStatic
		fun newInstance(id: Int) =
			CalendarPlanShower().apply {
				arguments = Bundle().apply {
					putInt(PARAM_ID, id)
				}
			}
	}
}