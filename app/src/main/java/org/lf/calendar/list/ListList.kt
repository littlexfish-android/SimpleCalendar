package org.lf.calendar.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import org.lf.calendar.R

class ListList : Fragment() {
	
	lateinit var list: LinearLayout
	private val groups = HashMap<String, ListItem>()
	private val groupOrder = ArrayList<String>()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_list_list, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		list = view.findViewById(R.id.list_list_items)
		
		refreshList(true)
	}
	
	fun addListItem(groupName: String, index: Int = -1) {
		if(groupOrder.contains(groupName)) return
		val item = ListItem(requireContext())
		item.groupName = groupName
		if(index == -1) groupOrder.add(groupName) else groupOrder.add(index, groupName)
		groups[groupName] = item
		list.addView(item, index)
		refreshList(false)
	}
	
	fun addItem(group: String, item: String, index: Int = -1) {
		var listItem = groups[group]
		if(listItem == null) {
			addListItem(group)
			listItem = groups[group]
		}
		listItem!!.addItem(item, index)
		refreshList(false)
	}
	
	fun removeItem(group: String, item: String) {
		//TODO
	}
	
	fun removeItem(group: String, index: Int) {
		//TODO
	}
	
	private fun refreshList(force: Boolean) {
		if(force || (groupOrder.size != groups.size || groups.size != list.childCount)) {
			
			// remove all list children
			list.removeAllViews()
			
			// construct children
			for(groupName in groupOrder) {
				list.addView(groups[groupName])
			}
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