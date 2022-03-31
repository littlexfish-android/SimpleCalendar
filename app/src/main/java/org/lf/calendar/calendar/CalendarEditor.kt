package org.lf.calendar.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import org.lf.calendar.MainActivity
import org.lf.calendar.R
import org.lf.calendar.io.SqlHelper
import org.lf.calendar.io.sqlitem.calendar.SqlCalendar1
import java.util.*

private const val BEGIN_YEAR = 2000
private const val END_YEAR = 2200

private const val PARAM_TIME = "calendar.editor.time"
private const val PARAM_CONTENT = "calendar.editor.content"
private const val PARAM_REMARK = "calendar.editor.remark"

/**
 * The calendar plan editor
 */
class CalendarEditor : Fragment() {
	
	/* data */
	
	private val day = Calendar.getInstance()
	private var tmpContent = ""
	private var tmpRemark = ""
	
	/* view */
	
	private lateinit var content: EditText
	private lateinit var spinnerYear: Spinner
	private lateinit var spinnerMonth: Spinner
	private lateinit var spinnerDay: Spinner
	private lateinit var spinnerHour: Spinner
	private lateinit var spinnerMinute: Spinner
	private lateinit var remark: EditText
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			day.time = Date(it.getLong(PARAM_TIME))
			if(it.containsKey(PARAM_CONTENT)) tmpContent = it.getString(PARAM_CONTENT)!!
			if(it.containsKey(PARAM_REMARK)) tmpRemark = it.getString(PARAM_REMARK)!!
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_calendar_editor, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		content = view.findViewById(R.id.calendarEditorContent)
		spinnerYear = view.findViewById(R.id.calendarEditorYear)
		spinnerMonth = view.findViewById(R.id.calendarEditorMonth)
		spinnerDay = view.findViewById(R.id.calendarEditorDay)
		spinnerHour = view.findViewById(R.id.calendarEditorHour)
		spinnerMinute = view.findViewById(R.id.calendarEditorMinute)
		remark = view.findViewById(R.id.calendarEditorRemark)
		
		view.findViewById<Button>(R.id.calendarEditorConfirm).setOnClickListener { onButtonClick(it) }
		view.findViewById<Button>(R.id.calendarEditorCancel).setOnClickListener { onButtonClick(it) }
		
		spinnerMonth.onItemSelectedListener = OnMonthSelect(spinnerYear, spinnerDay)
		
		content.text.append(tmpContent)
		remark.text.append(tmpRemark)
		
		initSpinner()
		
	}
	
	/**
	 * Call on click confirm or cancel button
	 */
	private fun onButtonClick(v: View) {
		when(v.id) {
			R.id.calendarEditorConfirm -> {
				// close editor
				if(activity != null) {
					if(activity is MainActivity) {
						val act = activity as MainActivity
						val contentString = content.text.toString()
						val remarkString = remark.text.toString().let { if(it.isEmpty()) null else it }
						val year = spinnerYear.selectedItem.toString().toInt()
						val month = spinnerMonth.selectedItem.toString().toInt()
						val day = spinnerDay.selectedItem.toString().toInt()
						val hour = spinnerHour.selectedItem.toString().toInt()
						val minute = spinnerMinute.selectedItem.toString().toInt()
						val sqlCalendar = act.getCalendar()
						
						val time = Calendar.getInstance().also {
							it.set(year, month - 1, day, hour, minute)
						}
						
						val item = SqlCalendar1(contentString, remarkString, time.time)
						
						sqlCalendar.addCalendarPlan(item)
						
						sqlCalendar.saveSql(SqlHelper.getInstance(context).writableDatabase)
						
						act.setFragmentToCalendar()
						act.fragmentCalendar.reloadFromSql(true)
						
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
		val yearArr = (BEGIN_YEAR..END_YEAR).toList().toTypedArray()//.map { it.toString() }
		val monthArr = (1..12).toList().map { it.toString() }
		val dayArr = (1..day.getActualMaximum(Calendar.DAY_OF_MONTH)).toList().map { it.toString() }
		val hourArr = (0..24).toList().map { it.toString() }
		val minuteArr = (0..59).toList().map { it.toString() }
		spinnerYear.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, yearArr)
		spinnerYear.setSelection(yearSelect)
		spinnerMonth.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, monthArr)
		spinnerMonth.setSelection(monthSelect)
		spinnerDay.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dayArr)
		spinnerDay.setSelection(daySelect)
		spinnerHour.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, hourArr)
		spinnerHour.setSelection(hourSelect)
		spinnerMinute.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, minuteArr)
		spinnerMinute.setSelection(minuteSelect)
		
	}
	
	companion object {
		@JvmStatic
		fun newInstance(time: Long = Date().time, initContent: String = "") =
			CalendarEditor().apply {
				arguments = Bundle().apply {
					this.putLong(PARAM_TIME, time)
					this.putString(PARAM_CONTENT, initContent)
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
			daySpinner.adapter = ArrayAdapter(daySpinner.context, android.R.layout.simple_spinner_item, arr)
		}
		
		override fun onNothingSelected(parent: AdapterView<*>?) {
			// do nothing
		}
		
	}
	
}