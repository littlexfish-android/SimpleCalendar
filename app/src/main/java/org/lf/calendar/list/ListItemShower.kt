package org.lf.calendar.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.lf.calendar.MainActivity
import org.lf.calendar.calendar.CalendarPlanShower
import org.lf.calendar.databinding.FragmentListItemShowerBinding
import org.lf.calendar.io.SqlHelper
import org.lf.calendar.io.sqlitem.list.SqlList1

const val PARAM_ID = "list.viewer.id"

class ListItemShower : Fragment() {
	
	private var itemId: Int = 0
	private lateinit var binder: FragmentListItemShowerBinding
	private lateinit var sqlItems: ArrayList<SqlList1>
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			itemId = it.getInt(PARAM_ID)
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		binder = FragmentListItemShowerBinding.inflate(inflater)
		return binder.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		val sql = SqlHelper.getInstance(context)
		
		sqlItems = sql.getList(sql.readableDatabase, "WHERE _id==$itemId").getList()[0].second
		
		binder.listShowerItems.constructItems(sqlItems)
		binder.listShowerItems.disableAll()
		
		if(sqlItems[0].attachCalendarId != null) {
			binder.listShowerAttach.visibility = View.VISIBLE
			binder.listShowerAttach.setOnClickListener {
				val act = activity as MainActivity
				act.setFragmentToOther(CalendarPlanShower.newInstance(sqlItems[0].attachCalendarId!!), "日曆")
			}
		}
		else {
			binder.listShowerAttach.visibility = View.GONE
		}
		
		binder.listShowerClose.setOnClickListener {
			val act = requireActivity() as MainActivity
			act.setFragmentToList()
		}
		binder.listShowerEdit.setOnClickListener {
			// TODO: edit
		}
		
	}
	
	companion object {
		@JvmStatic
		fun newInstance(id: Int) =
			ListItemShower().apply {
				arguments = Bundle().apply {
					putInt(PARAM_ID, id)
				}
			}
	}
}