package org.lf.calendar.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import org.lf.calendar.MainActivity
import org.lf.calendar.R
import org.lf.calendar.io.sqlitem.list.SqlList1

/**
 * The list use to show list item
 */
class ListList : Fragment() {
	
	/**
	 * The map holds group name and list item
	 */
	private val groups = ArrayList<Pair<String, ListItem>>()
	
	/**
	 * List item list
	 */
	lateinit var list: LinearLayout
	
	/**
	 * the task list will run on view created
	 */
	private val task = ArrayList<Runnable>()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_list_list, container, false)
	}
	
	/**
	 * Initial views and refresh list
	 */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		list = view.findViewById(R.id.list_list_items)
		
		if(task.isNotEmpty()) {
			for(t in task) {
				t.run()
			}
			task.clear()
		}
		
	}
	
	/**
	 * reset the group view
	 */
	fun reset() {
		val t = {
			groups.clear()
			list.removeAllViews()
		}
		if(this::list.isInitialized) {
			list.post(t)
		}
		else {
			task.add(t)
		}
	}
	
	/**
	 * construct list item from sql data
	 */
	fun constructListItem(group: String, list: ArrayList<SqlList1>) {
		task.add {
			val item = ListItem(requireContext())
			item.groupName = group
			item.constructItems(list)
			groups.add(Pair(group, item))
			// FIXME: can not direct to item shower, it will check and uncheck the checkbox
			for(it2 in list) {
				if(it2.content.isEmpty()) {
					item.setOnLongClickListener {
						val act = activity as MainActivity
						act.setFragmentToOther(ListItemShower.newInstance(it2._id))
						true
					}
					break
				}
			}
			this.list.addView(item)
			item.color = list[0].color
			refreshList()
		}
	}
	
	/**
	 * Refresh the list
	 */
	private fun refreshList() {
		val t = {
			// remove all list children
			list.removeAllViews()

			// construct children
			for(group in groups) {
				list.addView(group.second)
			}
		}
		if(this::list.isInitialized) {
			list.post(t)
		}
		else {
			task.add(t)
		}
	}
	
	companion object {
		@JvmStatic
		fun newInstance() =
			ListList().apply {
				arguments = Bundle().apply {
				}
			}
	}
}