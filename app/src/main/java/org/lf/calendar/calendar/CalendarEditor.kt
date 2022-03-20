package org.lf.calendar.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import org.lf.calendar.R
import java.util.*

private const val BEGIN_YEAR = 2000
private const val END_YEAR = 2500

private const val PARAM_TIME = "calendar.editor.time"

/**
 * The calendar plan editor
 */
class CalendarEditor : Fragment() {
	
	/* data */
	
	private val day = Calendar.getInstance()
	
	/* view */
	
	private lateinit var spinnerYear: Spinner
	private lateinit var spinnerMonth: Spinner
	private lateinit var spinnerDay: Spinner
	private lateinit var spinnerHour: Spinner
	private lateinit var spinnerMinute: Spinner
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			day.time = Date(it.getLong(PARAM_TIME))
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_calendar_editor, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		spinnerYear = view.findViewById(R.id.calendarEditorYear)
		spinnerMonth = view.findViewById(R.id.calendarEditorMonth)
		spinnerDay = view.findViewById(R.id.calendarEditorDay)
		spinnerHour = view.findViewById(R.id.calendarEditorHour)
		spinnerMinute = view.findViewById(R.id.calendarEditorMinute)
		
		spinnerMonth.onItemSelectedListener = OnMonthSelect(spinnerYear, spinnerDay)
		
		initSpinner()
		
	}
	
	
	/**
	 * Initial the time spinners' content list
	 */
	private fun initSpinner() {
		val yearSelect = day[Calendar.YEAR] - BEGIN_YEAR
		val monthSelect = day[Calendar.MONTH]
		val daySelect = day[Calendar.DAY_OF_MONTH]
		val hourSelect = day[Calendar.HOUR_OF_DAY]
		val minuteSelect = day[Calendar.MINUTE]
		val yearArr = (BEGIN_YEAR..END_YEAR).toList().map { it.toString() }
		val monthArr = (1..12).toList().map { it.toString() }
		val dayArr = (1..(if(monthSelect < 7) {
			if(monthSelect == 2) {
				val isBig = (yearSelect + BEGIN_YEAR) % 4 == 0
				if(isBig) 29
				else 28
			}
			else {
				if(monthSelect % 2 == 0) 31
				else 30
			}
		}
		else {
			if(monthSelect % 2 == 0) 30
			else 31
		})).toList().map { it.toString() }
		val hourArr = (0..24).toList().map { it.toString() }
		val minuteArr = (0..59).toList().map { it.toString() }
		
		spinnerYear.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.emptyArray, android.R.layout.simple_spinner_item).also {
			it.addAll(yearArr)
		}
		spinnerYear.setSelection(yearSelect)
		spinnerMonth.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.emptyArray, android.R.layout.simple_spinner_item).also {
			it.addAll(monthArr)
		}
		spinnerMonth.setSelection(monthSelect)
		spinnerDay.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.emptyArray, android.R.layout.simple_spinner_item).also {
			it.addAll(dayArr)
		}
		spinnerDay.setSelection(daySelect)
		spinnerHour.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.emptyArray, android.R.layout.simple_spinner_item).also {
			it.addAll(hourArr)
		}
		spinnerHour.setSelection(hourSelect)
		spinnerMinute.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.emptyArray, android.R.layout.simple_spinner_item).also {
			it.addAll(minuteArr)
		}
		spinnerMinute.setSelection(minuteSelect)
		
	}
	
	companion object {
		@JvmStatic
		fun newInstance(time: Long = System.currentTimeMillis()) =
			CalendarEditor().apply {
				arguments = Bundle().apply {
					this.putLong(PARAM_TIME, time)
				}
			}
		@JvmStatic
		fun newInstance(time: Calendar = Calendar.getInstance().also { it.time = Date() }) =
			CalendarEditor().apply {
				arguments = Bundle().apply {
					this.putLong(PARAM_TIME, time.time.time)
				}
			}
	}
	
	class OnMonthSelect(private val yearSpinner: Spinner, private val daySpinner: Spinner) : AdapterView.OnItemSelectedListener {
		
		override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
			val arr = (1..(if(position < 7) {
				if(position == 2) {
					val isBig = Integer.parseInt(yearSpinner.selectedItem.toString()) % 4 == 0
					if(isBig) 29
					else 28
				}
				else {
					if(position % 2 == 0) 31
					else 30
				}
			}
			else {
				if(position % 2 == 0) 30
				else 31
			})).toList().map { it.toString() }
			val adapter = ArrayAdapter.createFromResource(daySpinner.context, R.array.emptyArray, android.R.layout.simple_spinner_item)
			adapter.addAll(arr)
			daySpinner.adapter = adapter
		}
		
		override fun onNothingSelected(parent: AdapterView<*>?) {
			// do nothing
		}
		
	}
	
}