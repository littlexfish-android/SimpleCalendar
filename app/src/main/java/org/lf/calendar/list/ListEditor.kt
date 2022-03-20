package org.lf.calendar.list

import android.os.Bundle
import android.util.ArrayMap
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import org.lf.calendar.MainActivity
import org.lf.calendar.R
import org.lf.calendar.io.SqlHelper
import org.lf.calendar.io.sqlitem.list.SqlList1
import java.util.*
import kotlin.collections.ArrayList

private const val PARAM_TYPE = "list.editor.type"
private const val PARAM_GROUP = "list.editor.group"
private const val PARAM_LIST = "list.editor.list"
private const val PARAM_POSITION = "list.editor.pos"

/**
 * The list item editor
 */
class ListEditor : Fragment() {
	
	/* data */
	
	private var tmpType: String = ""
	private var tmpGroup: String? = null
	private var tmpList: Array<String>? = null
	private var tmpPos: Int = -1
	
	/* view */
	
	private lateinit var groupView: EditText
	
	private lateinit var items: LinearLayout
	
	private val itemViews = ArrayList<EditText>()
	
	private var selectedItem: EditText? = null
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			tmpType = it.getString(PARAM_TYPE, "")
			tmpGroup = it.getString(PARAM_GROUP)
			if(it.containsKey(PARAM_LIST)) tmpList = it.getStringArray(PARAM_LIST)
			if(it.containsKey(PARAM_POSITION)) tmpPos = it.getInt(PARAM_POSITION)
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_list_editor, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		groupView = view.findViewById(R.id.listEditorGroup)
		items = view.findViewById(R.id.listEditorItems)
		
		// init group name if input
		if(tmpType == "new") {
			if(tmpGroup != null) {
				groupView.text.append(tmpGroup)
			}
		}
		else if(tmpType == "edit") {
			groupView.text.append(tmpGroup)
			if(tmpList != null) {
				for(str in tmpList!!) {
					addItem(str)
				}
				if(tmpPos >= 0 && tmpPos < tmpList!!.size) {
					focusItem(tmpPos, true)
				}
			}
		}
		
		
		view.findViewById<Button>(R.id.listEditorAddItem).setOnClickListener {
			// add item
			addItem()
		}
		view.findViewById<Button>(R.id.listEditorRemoveItem).setOnClickListener {
			if(selectedItem != null) {
				// find the item and remove it
				val pos = itemViews.indexOf(selectedItem)
				items.removeViewAt(pos)
				itemViews.removeAt(pos)
				selectedItem = null
			}
		}
		view.findViewById<Button>(R.id.listEditorConfirm).setOnClickListener {
			if(activity != null) {
				if(activity is MainActivity) {
					// add list item
					val act = activity as MainActivity
					val groupName = groupView.text.toString()
					val sqlList = act.getList()
					
					for(v in itemViews) {
						val str = v.text.toString()
						val item = SqlList1(groupName, str, Date())
						sqlList.addListItem(item)
					}
					
					// close editor
					act.setFragmentToList(true)
				}
			}
		}
		view.findViewById<Button>(R.id.listEditorCancel).setOnClickListener {
			// close editor
			if(activity != null) {
				if(activity is MainActivity) {
					(activity as MainActivity).setFragmentToList(false)
				}
			}
		}
	}
	
	private fun addItem(initString: String = "") {
		val text = EditText(context)
		itemViews.add(text)
		items.addView(text)
		text.text.append(initString)
		text.setOnClickListener {
			selectedItem = it as EditText
			for(et in itemViews) {
				et.setBackgroundColor(resources.getColor(R.color.none, null))
			}
			focusItem(-1, false)
		}
		text.requestFocus()
	}
	
	private fun focusItem(index: Int, changeBG: Boolean = true) {
		val i = if(index == -1) itemViews.size - 1 else index
		selectedItem?.setBackgroundColor(resources.getColor(R.color.none, null))
		selectedItem = itemViews[i]
		if(changeBG) {
			selectedItem?.setBackgroundColor(resources.getColor(R.color.list_editor_selected, null))
		}
		selectedItem?.requestFocus()
	}
	
	companion object {
		@JvmStatic
		fun newInstance(initGroup: String? = null) =
			ListEditor().apply {
				arguments = Bundle().apply {
					this.putString(PARAM_TYPE, "new")
					this.putString(PARAM_GROUP, initGroup)
				}
			}
		@JvmStatic
		fun newInstance(group: String, list: Array<String>, index: Int) =
			ListEditor().apply {
				arguments = Bundle().apply {
					this.putString(PARAM_TYPE, "edit")
					this.putString(PARAM_GROUP, group)
					this.putStringArray(PARAM_LIST, list)
					this.putInt(PARAM_POSITION, index)
				}
			}
	}
}