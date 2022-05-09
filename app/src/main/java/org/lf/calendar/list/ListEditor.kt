package org.lf.calendar.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.lf.calendar.MainActivity
import org.lf.calendar.R
import org.lf.calendar.io.SqlHelper
import org.lf.calendar.io.sqlitem.list.SqlList1
import org.lf.calendar.view.ColorSpinnerAdapter
import org.lf.calendar.view.Reminder
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
	
	private lateinit var spinnerColor: Spinner
	
	private val itemViews = ArrayList<EditText>()
	
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
		spinnerColor = view.findViewById(R.id.listEditorColorSpinner)
		
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
				if(tmpPos > 0 && tmpPos < tmpList!!.size) {
					itemViews[tmpPos].requestFocus()
				}
			}
		}
		
		
		view.findViewById<FloatingActionButton>(R.id.listEditorAddItem).setOnClickListener {
			// add item
			addItem()
		}
		view.findViewById<Button>(R.id.listEditorConfirm).setOnClickListener {
			if(activity != null) {
				if(activity is MainActivity) {
					// add list item
					val act = activity as MainActivity
					val groupName = groupView.text.toString()
					val sqlList = act.getList()
					
					val item = SqlList1(groupName, "", (spinnerColor.selectedItem as ColorSpinnerAdapter.ColorSpinnerItem).color)
					sqlList.addListItem(item)
					for(v in itemViews) {
						val str = v.text.toString()
						if(str.isBlank()) continue
						val sqlItem = SqlList1(groupName, str, (spinnerColor.selectedItem as ColorSpinnerAdapter.ColorSpinnerItem).color)
						sqlList.addListItem(sqlItem)
					}
					
					// TODO: add attach calendar
					
					sqlList.saveSql(SqlHelper.getInstance(context).writableDatabase)
					
					// close editor
					act.setFragmentToList(reload = true)
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
		
		spinnerColor.adapter = ColorSpinnerAdapter(requireContext(), R.array.defaultColorStringArray, R.array.defaultColorNameArray)
		
	}
	
	private fun addItem(initString: String = "") {
		val text = EditText(context)
		itemViews.add(text)
		items.addView(text)
		text.text.append(initString)
		// long click to delete
		text.setOnLongClickListener {
			// find the item and remove it
			val pos = itemViews.indexOf(it)
			items.removeViewAt(pos)
			itemViews.removeAt(pos)
			true
		}
		text.requestFocus()
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