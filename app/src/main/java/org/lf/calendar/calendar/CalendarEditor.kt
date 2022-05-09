package org.lf.calendar.calendar

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import org.intellij.lang.annotations.Language
import org.lf.calendar.MainActivity
import org.lf.calendar.R
import org.lf.calendar.databinding.FragmentCalendarEditorBinding
import org.lf.calendar.io.SqlHelper
import org.lf.calendar.io.sqlitem.calendar.SqlCalendar1
import org.lf.calendar.view.ColorSpinnerAdapter
import org.lf.calendar.view.Reminder
import java.util.*

private const val BEGIN_YEAR = 2000
private const val END_YEAR = 2200

private const val PARAM_OLD = "calendar.editor.old"
private const val PARAM_TIME = "calendar.editor.time"
private const val PARAM_CONTENT = "calendar.editor.content"
private const val PARAM_REMARK = "calendar.editor.remark"
private const val PARAM_LIST_ID = "calendar.editor.listId"

/**
 * The calendar plan editor
 */
class CalendarEditor : Fragment() {
	
	/* data */
	
	private var oldData = false
	
	private val day = Calendar.getInstance()
	private var tmpContent = ""
	private var tmpRemark = ""
	private var tmpListId: Int? = null
	
	private var reminderDay: Calendar? = null
	
	/* view */
	
	private lateinit var binder: FragmentCalendarEditorBinding
	private lateinit var reminder: Reminder
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			val date = Date(it.getLong(PARAM_TIME))
			day.clear()
			day.time = date
			oldData = it.getBoolean(PARAM_OLD)
			if(it.containsKey(PARAM_CONTENT)) tmpContent = it.getString(PARAM_CONTENT)!!
			if(it.containsKey(PARAM_REMARK)) tmpRemark = it.getString(PARAM_REMARK)!!
			if(it.containsKey(PARAM_LIST_ID)) tmpListId = it.getInt(PARAM_LIST_ID)
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_calendar_editor, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binder = FragmentCalendarEditorBinding.bind(view)
		
		reminder = childFragmentManager.findFragmentById(binder.calendarEditorReminder.id) as Reminder
		
		binder.calendarEditorConfirm.setOnClickListener { onButtonClick(it) }
		binder.calendarEditorCancel.setOnClickListener { onButtonClick(it) }
		
		binder.calendarEditorContent.text.append(tmpContent)
		binder.calendarEditorRemark.text.append(tmpRemark)
		
		initSpinner()
		
		binder.calendarEditorLinkList.visibility = View.GONE
		
		if(tmpListId != null) {
			binder.calendarEditorLinkList.text.clear()
			binder.calendarEditorLinkList.text.append(resources.getString(R.string.calendarEditorLinkListItem, Integer.toHexString(tmpListId!!)))
			binder.calendarEditorLinkList.visibility = View.VISIBLE
		}
		
		binder.calendarEditorReminder.visibility = View.GONE
		
		// on click add reminder
		binder.calendarEditorAddReminder.setOnClickListener {
			binder.calendarEditorConfirm.visibility = View.INVISIBLE
			binder.calendarEditorCancel.visibility = View.INVISIBLE
			binder.calendarEditorContent.isFocusable = false
			binder.calendarEditorRemark.isFocusable = false
			reminder.openReminder(getTimeOnSelect(), {
				reminderDay = reminder.getReminderDay()
				binder.calendarEditorAddReminder.text.clear()
				if(reminderDay != null) {
					reminderDay?.let { it2 ->
						binder.calendarEditorAddReminder.text.append(
							resources.getString(R.string.calendarEditorReminderFormat, it2[Calendar.YEAR], it2[Calendar.MONTH] + 1,
								it2[Calendar.DAY_OF_MONTH], it2[Calendar.HOUR_OF_DAY], it2[Calendar.MINUTE]))
					}
				}
				else {
					binder.calendarEditorAddReminder.text.append(resources.getString(R.string.calendarEditorAddReminder))
				}
				binder.calendarEditorReminder.visibility = View.GONE
				binder.calendarEditorConfirm.visibility = View.VISIBLE
				binder.calendarEditorCancel.visibility = View.VISIBLE
			}) {
				reminderDay = null
				binder.calendarEditorAddReminder.text.clear()
				binder.calendarEditorAddReminder.text.append(resources.getString(R.string.calendarEditorAddReminder))
				binder.calendarEditorReminder.visibility = View.GONE
				binder.calendarEditorConfirm.visibility = View.VISIBLE
				binder.calendarEditorCancel.visibility = View.VISIBLE
			}
			binder.calendarEditorReminder.visibility = View.VISIBLE
		}
		
		// on open remark
		binder.calendarEditorRemark.setOnFocusChangeListener { _, hasFocus ->
			val flag = if(hasFocus) View.GONE else View.VISIBLE
			binder.calendarEditorAddReminder.visibility = flag
			binder.textView5.visibility = flag
			binder.calendarEditorColor.visibility = flag
			if(tmpListId != null) binder.calendarEditorLinkList.visibility = flag
		}
		
	}
	
	private fun getTimeOnSelect(): Calendar {
		val year = binder.calendarEditorYear.selectedItem.toString().toInt()
		val month = binder.calendarEditorMonth.selectedItem.toString().toInt()
		val day = binder.calendarEditorDay.selectedItem.toString().toInt()
		val hour = binder.calendarEditorHour.selectedItem.toString().toInt()
		val minute = binder.calendarEditorMinute.selectedItem.toString().toInt()
		val c = Calendar.getInstance()
		c.set(year, month - 1, day, hour, minute)
		return c
	}
	
	fun onBackPressed() {
		if(binder.calendarEditorAddReminder.visibility == View.GONE && binder.calendarEditorRemark.isFocusable) {
			binder.calendarEditorAddReminder.visibility = View.VISIBLE
			binder.textView5.visibility = View.VISIBLE
			binder.calendarEditorColor.visibility = View.VISIBLE
			if(tmpListId != null) binder.calendarEditorLinkList.visibility = View.VISIBLE
		}
	}
	
	/**
	 * Call on click confirm or cancel button
	 */
	private fun onButtonClick(v: View) {
		when(v.id) {
			R.id.calendarEditorConfirm -> {
				if(activity != null) {
					if(activity is MainActivity) {
						if(binder.calendarEditorContent.text.isBlank()) {
							Toast.makeText(requireContext(), "內容不可為空", Toast.LENGTH_SHORT).show()
							return
						}
						val act = activity as MainActivity
						val contentString = binder.calendarEditorContent.text.toString()
						val remarkString = binder.calendarEditorRemark.text.toString().let { if(it.isEmpty()) null else it }
						val sqlCalendar = act.getCalendar()
						
						val time = getTimeOnSelect()
						
						val item = SqlCalendar1(contentString, remarkString, time.time,
							(binder.calendarEditorColor.selectedItem as ColorSpinnerAdapter.ColorSpinnerItem).color,
							reminderDay?.time, tmpListId)
						
						sqlCalendar.addCalendarPlan(item)
						
						sqlCalendar.saveSql(SqlHelper.getInstance(context).writableDatabase)
						
						if(tmpListId != null) {
							val sql = SqlHelper.getInstance(requireContext())
							
							val l1 = sql.getList(sql.writableDatabase,
								"WHERE _id == $tmpListId").getList()
							
							val name = l1[0].first
							
							val l2 = sql.getList(sql.writableDatabase,
								"WHERE groupName == $name")
							val l2l = l2.getList()
							for(it2 in l2l[0].second) {
								it2.attachCalendarId = item._id
								l2.addListItem(it2)
							}
							
						}
						
						// close editor
						act.setFragmentToCalendar(reload = true)
						act.setCalendarDay(binder.calendarEditorYear.selectedItem.toString().toInt(),
							binder.calendarEditorMonth.selectedItem.toString().toInt(),
							binder.calendarEditorDay.selectedItem.toString().toInt())
						
					}
				}
			}
			R.id.calendarEditorCancel -> {
				// close editor
				if(activity != null) {
					if(activity is MainActivity) {
						(activity as MainActivity).setFragmentToCalendar()
					}
				}
			}
		}
	}
	
	
	/**
	 * Initial the time spinners' content list
	 */
	private fun initSpinner() {
		val yearSelect = day[Calendar.YEAR] - BEGIN_YEAR
		val monthSelect = day[Calendar.MONTH]
		val daySelect = day[Calendar.DAY_OF_MONTH] - 1
		val hourSelect = day[Calendar.HOUR_OF_DAY]
		val minuteSelect = day[Calendar.MINUTE]
		val yearArr = (BEGIN_YEAR..END_YEAR).toList().toTypedArray()
		val monthArr = (1..12).toList().map { it.toString() }
		val dayArr = (1..day.getActualMaximum(Calendar.DAY_OF_MONTH)).toList().map { it.toString() }
		val hourArr = (0..23).toList().map { it.toString() }
		val minuteArr = (0..59).toList().map { it.toString() }
		binder.calendarEditorYear.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, yearArr)
		binder.calendarEditorYear.setSelection(yearSelect)
		binder.calendarEditorMonth.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, monthArr)
		binder.calendarEditorMonth.setSelection(monthSelect)
		binder.calendarEditorDay.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dayArr)
		binder.calendarEditorDay.setSelection(daySelect)
		binder.calendarEditorHour.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, hourArr)
		binder.calendarEditorHour.setSelection(hourSelect)
		binder.calendarEditorMinute.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, minuteArr)
		binder.calendarEditorMinute.setSelection(minuteSelect)
		
		binder.calendarEditorColor.adapter = ColorSpinnerAdapter(requireContext(), R.array.defaultColorStringArray, R.array.defaultColorNameArray)
		
		binder.calendarEditorMonth.onItemSelectedListener = OnMonthSelect(binder.calendarEditorYear, binder.calendarEditorDay)
		
	}
	
	companion object {
		@JvmStatic
		fun newInstance(time: Long = Date().time, initContent: String = "", linkListId: Int? = null, oldData: Boolean = false) =
			CalendarEditor().apply {
				arguments = Bundle().apply {
					this.putLong(PARAM_TIME, time)
					this.putString(PARAM_CONTENT, initContent)
					if(linkListId != null) this.putInt(PARAM_LIST_ID, linkListId)
					this.putBoolean(PARAM_OLD, oldData)
				}
			}
	}
	
	class OnMonthSelect(private val yearSpinner: Spinner, private val daySpinner: Spinner) : AdapterView.OnItemSelectedListener {
		
		/**
		 * Check the max day of month
		 */
		override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
			val dayMax = Calendar.getInstance().also { it.set(yearSpinner.selectedItem.toString().toInt(), position, 1) }.getActualMaximum(Calendar.DAY_OF_MONTH)
			val arr = (1..dayMax).toList().toTypedArray()
			val oriSelect = daySpinner.selectedItemPosition
			daySpinner.adapter = ArrayAdapter(daySpinner.context, android.R.layout.simple_spinner_item, arr)
			
			if(oriSelect < arr.size) {
				daySpinner.setSelection(oriSelect)
			}
			
		}
		
		override fun onNothingSelected(parent: AdapterView<*>?) {
			// do nothing
		}
		
	}
	
}