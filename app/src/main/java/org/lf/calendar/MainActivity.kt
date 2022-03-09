package org.lf.calendar

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.lf.calendar.databinding.ActivityMainBinding
import org.lf.calendar.io.SqlHelper
import org.lf.calendar.tabs.Calendar
import org.lf.calendar.tabs.List
import org.lf.calendar.tabs.Profile
import java.util.*

class MainActivity : AppCompatActivity() {
	
	private var isOptionOpen = false
	
	private lateinit var binding: ActivityMainBinding
	private lateinit var list: SqlHelper.ListProcessor
	private lateinit var calendar: SqlHelper.CalendarProcessor
	private lateinit var lastDataBase: SQLiteDatabase
	
	private lateinit var fragmentList: List
	private lateinit var fragmentCalendar: Calendar
	private lateinit var fragmentProfile: Profile
	private var nowFrag: Fragment = fragmentList
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityMainBinding.inflate(layoutInflater)

		setContentView(binding.root)
		
		fragmentList = List.newInstance()
		fragmentCalendar = Calendar.newInstance()
		fragmentProfile = Profile.newInstance()
		
		nowFrag = fragmentList
		
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
					setFragmentToCalendar()
				}
				if(event == "addCalendarPlan") { // add calendar plan
				
				}
				if(event == "addListItem") {
				
				}
			}
		}
		
		moveOptionsMenu(false)
		
	}
	
	private fun setFragmentToList() {
		val t = supportFragmentManager.beginTransaction()
		t.replace(R.id.main_tab_container, fragmentList)
		t.setCustomAnimations(R.anim.mv_left_frag_cutin, R.anim.mv_left_frag_cutout)
		t.commit()
		nowFrag = fragmentList
	}
	private fun setFragmentToCalendar(fromLeft: Boolean = true) {
		val t = supportFragmentManager.beginTransaction()
		t.replace(R.id.main_tab_container, fragmentCalendar)
		if(fromLeft) {
			t.setCustomAnimations(R.anim.mv_right_frag_cutin, R.anim.mv_right_frag_cutout)
		}
		else {
			t.setCustomAnimations(R.anim.mv_left_frag_cutin, R.anim.mv_left_frag_cutout)
		}
		t.commit()
		nowFrag = fragmentCalendar
	}
	private fun setFragmentToProfile() {
		val t = supportFragmentManager.beginTransaction()
		t.replace(R.id.main_tab_container, fragmentProfile)
		t.setCustomAnimations(R.anim.mv_right_frag_cutin, R.anim.mv_right_frag_cutout)
		t.commit()
		nowFrag = fragmentProfile
	}
	
	fun onListButtonPressed(v: View?) {
		if(nowFrag == fragmentList) return
		setFragmentToList()
	}
	fun onCalendarButtonPressed(v: View?) {
		if(nowFrag == fragmentCalendar) return
		setFragmentToCalendar(nowFrag == fragmentList)
	}
	fun onProfileButtonPressed(v: View?) {
		if(nowFrag == fragmentProfile) return
		setFragmentToProfile()
	}
	
	fun moveOptionsMenu(open: Boolean) {
		if(open && !isOptionOpen) {
			binding.mainOptionsMenu.animate().x(0f).setDuration(500).start()
			binding.mainOptionsOverlay.visibility = View.VISIBLE
			binding.mainOptionsOverlay.animate().alpha(1f).setDuration(500).start()
		}
		else {
			binding.mainOptionsMenu.animate().x(-resources.displayMetrics.widthPixels.toFloat()).setDuration(500).start()
			binding.mainOptionsOverlay.animate().alpha(0f).withEndAction { binding.mainOptionsOverlay.visibility = View.INVISIBLE }.setDuration(500).start()
		}
	}
	fun onOptionsMenuOpen(v: View) {
		moveOptionsMenu(true)
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