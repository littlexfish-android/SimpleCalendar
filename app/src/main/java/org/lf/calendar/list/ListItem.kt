package org.lf.calendar.list

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import androidx.core.view.children
import org.lf.calendar.MainActivity
import org.lf.calendar.R
import org.lf.calendar.io.SqlHelper
import org.lf.calendar.io.sqlitem.list.SqlList1
import java.util.*
import kotlin.collections.ArrayList

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
			refreshItems()
		}
	
	/* view */
	
	/**
	 * The view of group
	 */
	private lateinit var group: CheckBox
	
	/**
	 * The list of group view
	 */
	private lateinit var listGroup: LinearLayout
	
	private lateinit var sqlData: ArrayList<SqlList1>
	
	var color = 0
		set(value) {
			field = value
			if(::group.isInitialized) {
				group.setTextColor(value)
			}
			else {
				refreshItems()
			}
		}
	
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
	}
	
	fun constructItems(sql: ArrayList<SqlList1>) {
		sqlData = sql
		refreshItems()
	}
	
	/**
	 * Refresh the group
	 */
	private fun refreshItems() {
		if(::sqlData.isInitialized) {
			// clear children view
			listGroup.removeAllViews()

			// construct children
			for(it in sqlData) {
				addItemView(it.content, it.isComplete, sql = it)
			}
		}

		// refresh group name
		group.text = groupName
		group.setTextColor(color)
	}
	
	/**
	 * Add item view into list
	 */
	private fun addItemView(str: String, isChecked: Boolean = false, index: Int = -1, sql: SqlList1? = null) {
		val checkbox = if(str == "") {
			group
		}
		else {
			val c = CheckBox(context)
			c.text = str
			c.textSize = resources.getDimension(R.dimen.listItemItemSize)
			listGroup.addView(c, index)
			c
		}
		
		checkbox.isChecked = isChecked
		checkbox.paintFlags = if(isChecked) {
			checkbox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
		}
		else {
			checkbox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
		}
		checkbox.setOnCheckedChangeListener(OnChecked(this, sql))
	}
	
	fun disableAll() {
		group.isEnabled = false
		for(it in listGroup.children) {
			it.isEnabled = false
		}
	}
	
	class OnChecked(val parent: ListItem, var sql: SqlList1?) : CompoundButton.OnCheckedChangeListener {
		
		override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
			if(buttonView != null) {
				val act = buttonView.context.getActivity()
				if(act is MainActivity) {
					sql?.let {
						it.isComplete = isChecked
						it.completeTime = Date()
					}
					act.getList().saveSql(SqlHelper.getInstance(act).writableDatabase)
					parent.refreshItems()
				}
			}
		}
		
		private tailrec fun Context.getActivity(): Activity? = this as? Activity
			?: (this as? ContextWrapper)?.baseContext?.getActivity()
		
	}
	
	
	
}