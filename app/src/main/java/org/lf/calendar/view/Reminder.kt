package org.lf.calendar.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import org.lf.calendar.R
import org.lf.calendar.databinding.FragmentReminderBinding
import java.util.*

class Reminder : Fragment() {
	
	private lateinit var binder: FragmentReminderBinding
	private lateinit var day: Calendar
	private var reminderDay: Calendar? = null
	
	private var onConfirm: (() -> Unit)? = null
	private var onCancel: (() -> Unit)? = null
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_reminder, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binder = FragmentReminderBinding.bind(view)
		
//		binder.root.visibility = View.GONE
	}
	
	private fun initSpinner() {
		val year = day[Calendar.YEAR]
		val yRange = ((year - 5)..(year)).toList()
		val mRange = (1..12).toList()
		val dRange = (1..31).toList()
		val hRange = (0..23).toList()
		val miRange = (0..59).toList()
		val mSel = day[Calendar.MONTH]
		val dSel = day[Calendar.DAY_OF_MONTH] - 1
		val hSel = day[Calendar.HOUR_OF_DAY]
		val miSel = day[Calendar.MINUTE]
		
		binder.reminderYear.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, yRange)
		binder.reminderYear.setSelection(yRange.lastIndex)
		binder.reminderMonth.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mRange)
		binder.reminderMonth.setSelection(mSel)
		binder.reminderDay.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dRange)
		binder.reminderDay.setSelection(dSel)
		binder.reminderHour.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, hRange)
		binder.reminderHour.setSelection(hSel)
		binder.reminderMinute.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, miRange)
		binder.reminderMinute.setSelection(miSel)
		
		binder.reminderMonth.onItemSelectedListener = OnMonthSelect(binder.reminderYear, binder.reminderDay)
		
	}
	
	private fun setSelect() {
		reminderDay = when {
			binder.reminder5m.isChecked -> (day.clone() as Calendar).also { it.set(Calendar.MINUTE, it[Calendar.MINUTE] - 5) }
			binder.reminder10m.isChecked -> (day.clone() as Calendar).also { it.set(Calendar.MINUTE, it[Calendar.MINUTE] - 10) }
			binder.reminder30m.isChecked -> (day.clone() as Calendar).also { it.set(Calendar.MINUTE, it[Calendar.MINUTE] - 30) }
			binder.reminder1h.isChecked -> (day.clone() as Calendar).also { it.set(Calendar.HOUR, it[Calendar.HOUR] - 1) }
			binder.reminder2h.isChecked -> (day.clone() as Calendar).also { it.set(Calendar.HOUR, it[Calendar.HOUR] - 2) }
			binder.reminder12h.isChecked -> (day.clone() as Calendar).also { it.set(Calendar.HOUR, it[Calendar.HOUR] - 12) }
			binder.reminder1d.isChecked -> (day.clone() as Calendar).also { it.set(Calendar.DAY_OF_MONTH, it[Calendar.DAY_OF_MONTH] - 1) }
			binder.reminder2d.isChecked -> (day.clone() as Calendar).also { it.set(Calendar.DAY_OF_MONTH, it[Calendar.DAY_OF_MONTH] - 2) }
			binder.reminder1w.isChecked -> (day.clone() as Calendar).also { it.set(Calendar.DAY_OF_MONTH, it[Calendar.DAY_OF_MONTH] - 7) }
			else -> { // binder.reminderCustom.isChecked
				val year = binder.reminderYear.selectedItem.toString().toInt()
				val month = binder.reminderMonth.selectedItem.toString().toInt() - 1
				val day = binder.reminderDay.selectedItem.toString().toInt()
				val hour = binder.reminderHour.selectedItem.toString().toInt()
				val minute = binder.reminderMinute.selectedItem.toString().toInt()
				
				Calendar.getInstance().also { it.set(year, month, day, hour, minute) }
			}
		}
		reminderDay?.set(Calendar.SECOND, 0)
	}
	
	private fun initView() {
		binder.reminderCustom.setOnCheckedChangeListener { _, isChecked ->
			binder.reminderCustomGroup.visibility = if(isChecked) View.VISIBLE else View.INVISIBLE
		}
		
		initSpinner()
		
		binder.reminderConfirm.setOnClickListener {
			setSelect()
			onConfirm?.let { it1 -> it1() }
		}
		binder.reminderCancel.setOnClickListener {
			onCancel?.let { it1 -> it1() }
		}
		
	}
	
	fun openReminder(day: Calendar, confirmCallback: (() -> Unit)?, cancelCallback: (() -> Unit)?) {
		this.day = day
		onConfirm = confirmCallback
		onCancel = cancelCallback
		initView()
		binder.root.visibility = View.VISIBLE
	}
	
	fun getReminderDay() = reminderDay
	
	companion object {
		@JvmStatic
		fun newInstance() =
			Reminder().apply {
				arguments = Bundle().apply {
				}
			}
	}
	
	class OnMonthSelect(private val yearSpinner: Spinner, private val daySpinner: Spinner) : AdapterView.OnItemSelectedListener {
		
		/**
		 * Check the max day of month
		 */
		override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
			val dayMax = Calendar.getInstance().also { it.set(yearSpinner.selectedItem.toString().toInt(), position, 1) }.getActualMaximum(
				Calendar.DAY_OF_MONTH)
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