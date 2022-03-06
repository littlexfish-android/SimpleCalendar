package org.lf.calendar

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.lf.calendar.databinding.ActivityMainBinding
import org.lf.calendar.io.SqlHelper
import org.lf.calendar.tabs.Calendar
import org.lf.calendar.tabs.List
import org.lf.calendar.tabs.Profile
import java.util.*

class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding
	private lateinit var list: SqlHelper.ListProcessor
	private lateinit var calendar: SqlHelper.CalendarProcessor
	private lateinit var lastDataBase: SQLiteDatabase
	
	private lateinit var fragmentList: List
	private lateinit var fragmentCalendar: Calendar
	private lateinit var fragmentProfile: Profile
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityMainBinding.inflate(layoutInflater)

		setContentView(binding.root)
		
		fragmentList = List.newInstance()
		fragmentCalendar = Calendar.newInstance()
		fragmentProfile = Profile.newInstance()
		
		val sql = SqlHelper.getInstance(applicationContext)

		lastDataBase = sql.writableDatabase
		list = sql.getList(lastDataBase)
		calendar = sql.getCalendar(lastDataBase)
		
		val extra = intent.extras
		if(extra != null) { // call from other activity or my widget
			val event = extra.getString("event")
			if(event != null) {
				if(event == "selectCalendar") { // select the date
					val date = java.util.Calendar.getInstance().also { it.time = Date(extra.getLong("time")) }
					fragmentCalendar.setDay(date[java.util.Calendar.YEAR], date[java.util.Calendar.MONTH] + 1, date[java.util.Calendar.DAY_OF_MONTH])
				}
				if(event == "addCalendarPlan") { // add calendar plan
				
				}
				if(event == "addListItem") {
				
				}
			}
		}
		
	}
	
	private fun setFragmentToList() {
	
	}
	private fun setFragmentToCalendar() {
	
	}
	private fun setFragmentToProfile() {
	
	}
	
	fun getCalendar() = SqlHelper.CalendarProcessor(calendar)
	fun getList() = SqlHelper.ListProcessor(list)

	override fun onDestroy() {
		super.onDestroy()
		if(::lastDataBase.isInitialized) {
			lastDataBase.close()
		}
	}

}