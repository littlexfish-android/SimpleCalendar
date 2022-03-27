package org.lf.calendar.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import org.lf.calendar.R

/**
 * The list use to show list item
 */
class ListList : Fragment() {
	
	/**
	 * The map holds group name and list item
	 */
	private val groups = HashMap<String, ListItem>()
	
	/**
	 * The list holds group order
	 */
	private val groupOrder = ArrayList<String>()
	
	/**
	 * List item list
	 */
	lateinit var list: LinearLayout
	
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
		
		refreshList(true)
		
	}
	
	/**
	 * Add group into list
	 */
	fun addListItem(groupName: String, index: Int = -1) {
		val t = {
			if(!groupOrder.contains(groupName)) {
				val item = ListItem(requireContext())
				item.groupName = groupName
				if(index == -1) groupOrder.add(groupName) else groupOrder.add(index, groupName)
				groups[groupName] = item
				list.addView(item, index)
				refreshList(false)
			}
		}
		if(this::list.isInitialized) {
			t()
		}
		else {
			task.add(t)
		}
	}
	
	fun clearItems() {
		val t = {
			groups.clear()
			groupOrder.clear()
			list.removeAllViews()
		}
		if(this::list.isInitialized) {
			t()
		}
		else {
			task.add(t)
		}
	}
	
	/**
	 * Add item into group
	 */
	fun addItem(group: String, item: String, isComplete: Boolean = false, index: Int = -1) {
		val t = {
			var listItem = groups[group]
			if(listItem == null) {
				addListItem(group)
				listItem = groups[group]
			}
			listItem!!.addItem(item, isComplete, index)
			refreshList(false)
		}
		if(this::list.isInitialized) {
			t()
		}
		else {
			task.add(t)
		}
	}
	
	/**
	 * Remove item from group
	 */
	fun removeItem(group: String, item: String) {
		//TODO
	}
	
	/**
	 * Remove group from list
	 */
	fun removeGroup(group: String) {
		//TODO
	}
	
	/**
	 * Refresh the list
	 * @param force - {@code true} to force refresh, or {@code false} will refresh when it has been changed
	 */
	fun refreshList(force: Boolean) {
		val t = {
			if(force || (groupOrder.size != groups.size || groups.size != list.childCount)) {
				// remove all list children
				list.removeAllViews()
				
				// construct children
				for(groupName in groupOrder) {
					list.addView(groups[groupName])
				}
			}
		}
		if(this::list.isInitialized) {
			t()
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