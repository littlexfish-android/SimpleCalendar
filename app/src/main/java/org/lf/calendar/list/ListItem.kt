package org.lf.calendar.list

import android.content.Context
import android.util.AttributeSet
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import org.lf.calendar.R

/**
 * The class use to show on {@link org.lf.calendar.list.ListList}
 */
class ListItem : LinearLayout {
	
	/**
	 * The name of group
	 */
	var groupName = ""
		set(value) {
			field = value
			refreshItems(false)
		}
	
	/**
	 * The items of group
	 */
	private val items = ArrayList<String>()
	
	/* view */
	
	/**
	 * The view of group
	 */
	private lateinit var group: TextView
	
	/**
	 * The list of group view
	 */
	private lateinit var listGroup: LinearLayout
	
	constructor(context: Context) : super(context) {
		init(null, 0)
	}
	
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		init(attrs, 0)
	}
	
	constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
		init(attrs, defStyle)
	}
	
	/**
	 * Initial the view and refresh them
	 */
	private fun init(attrs: AttributeSet?, defStyle: Int) {
		// Load attributes
		val a = context.obtainStyledAttributes(attrs, R.styleable.ListItem, defStyle, 0)
		a.recycle()
		
		inflate(context, R.layout.view_list_item, this)
		
		listGroup = findViewById(R.id.list_item_items)
		group = findViewById(R.id.list_item_group)
		
		refreshItems(true)
		
	}
	
	/**
	 * Add the list item into this group
	 */
	fun addItem(str: String, index: Int = -1) {
		if(index == -1) items.add(str) else items.add(index, str)
		addItemView(str, index)
		refreshItems(false)
	}
	
	/**
	 * Refresh the group
	 * @param force - {@code true} to force it refresh, or {@code false} will refresh when it has been change
	 */
	private fun refreshItems(force: Boolean) {
		if(force || listGroup.childCount != items.size || group.text != groupName) {
			// clear children view
			listGroup.removeAllViews()
			
			// construct children
			for(str in items) {
				addItemView(str)
			}
			
			// refresh group name
			group.text = groupName
		}
		
	}
	
	/**
	 * Add item view into list
	 */
	private fun addItemView(str: String, index: Int = -1) {
		val checkbox = CheckBox(context)
		checkbox.text = str
		listGroup.addView(checkbox, index)
	}
	
}