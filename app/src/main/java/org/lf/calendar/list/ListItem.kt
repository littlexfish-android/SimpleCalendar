package org.lf.calendar.list

import android.content.Context
import android.util.AttributeSet
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import org.lf.calendar.R

class ListItem : LinearLayout {
	
	var groupName = ""
		set(value) {
			field = value
			refreshItems(false)
		}
	private val items = ArrayList<String>()
	private lateinit var group: TextView
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
	
	private fun init(attrs: AttributeSet?, defStyle: Int) {
		// Load attributes
		val a = context.obtainStyledAttributes(attrs, R.styleable.ListItem, defStyle, 0)
		a.recycle()
		
		inflate(context, R.layout.view_list_item, this)
		
		listGroup = findViewById(R.id.list_item_items)
		group = findViewById(R.id.list_item_group)
		
		refreshItems(true)
		
	}
	
	fun addItem(str: String, index: Int = -1) {
		if(index == -1) items.add(str) else items.add(index, str)
		addItemView(str, index)
		refreshItems(false)
	}
	
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
	
	private fun addItemView(str: String, index: Int = -1) {
		val checkbox = CheckBox(context)
		checkbox.text = str
		listGroup.addView(checkbox, index)
	}
	
}