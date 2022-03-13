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

/**
 * The main activity of this app
 */
class MainActivity : AppCompatActivity() {
	
	/* data */
	
	/**
	 * Check options menu is open
	 */
	private var isOptionOpen = false
	
	/**
	 * The data of list from sqlite
	 */
	private lateinit var list: SqlHelper.ListProcessor
	
	/**
	 * The data of calendar from sqlite
	 */
	private lateinit var calendar: SqlHelper.CalendarProcessor
	
	/**
	 * The last database
	 */
	private lateinit var lastDataBase: SQLiteDatabase
	
	/* view */
	
	/**
	 * The binding of the activity
	 */
	private lateinit var binding: ActivityMainBinding
	
	/**
	 * The fragment of list
	 */
	private lateinit var fragmentList: List
	
	/**
	 * The fragment of calendar
	 */
	private lateinit var fragmentCalendar: Calendar
	
	/**
	 * The fragment of profile
	 */
	private lateinit var fragmentProfile: Profile
	
	/**
	 * The fragment of now shown
	 */
	private var nowFrag: Fragment? = null
	
	/**
	 * On view create, get data from sqlite and auto process something
	 * if extra not null(means some app call this open)
	 */
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityMainBinding.inflate(layoutInflater)

		setContentView(binding.root)
		
		// init fragment
		fragmentList = List.newInstance()
		fragmentCalendar = Calendar.newInstance()
		fragmentProfile = Profile.newInstance()
		
		nowFrag = fragmentList
		
		// read data from sqlite
		val sql = SqlHelper.getInstance(applicationContext)

		lastDataBase = sql.writableDatabase
		list = sql.getList(lastDataBase)
		calendar = sql.getCalendar(lastDataBase)
		
		// process when extra not null
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
				if(event == "addListItem") { // add list item
				
				}
			}
		}
		
		// ensure options menu is close
		moveOptionsMenu(false)
		
	}
	
	/**
	 * Switch fragment to fragment of list
	 */
	private fun setFragmentToList() {
		val t = supportFragmentManager.beginTransaction()
		t.replace(R.id.main_tab_container, fragmentList)
		t.setCustomAnimations(R.anim.mv_left_frag_cutin, R.anim.mv_left_frag_cutout)
		t.commit()
		nowFrag = fragmentList
	}
	
	/**
	 * Switch fragment to fragment of calendar
	 */
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
	
	/**
	 * Switch fragment to fragment of profile
	 */
	private fun setFragmentToProfile() {
		val t = supportFragmentManager.beginTransaction()
		t.replace(R.id.main_tab_container, fragmentProfile)
		t.setCustomAnimations(R.anim.mv_right_frag_cutin, R.anim.mv_right_frag_cutout)
		t.commit()
		nowFrag = fragmentProfile
	}
	
	/**
	 * Call when list button been click
	 */
	fun onListButtonPressed(v: View?) {
		if(nowFrag == fragmentList) return
		setFragmentToList()
	}
	
	/**
	 * Call when calendar button been click
	 */
	fun onCalendarButtonPressed(v: View?) {
		if(nowFrag == fragmentCalendar) return
		setFragmentToCalendar(nowFrag == fragmentList)
	}
	
	/**
	 * Call when profile button been click
	 */
	fun onProfileButtonPressed(v: View?) {
		if(nowFrag == fragmentProfile) return
		setFragmentToProfile()
	}
	
	/**
	 * The switch of options menu
	 * @param open - {@code true} means make options menu open, vise versa
	 */
	fun moveOptionsMenu(open: Boolean) {
		if(open && !isOptionOpen) {
			binding.mainOptionsMenu.animate().x(0f).setDuration(500).start() // move menu
			binding.mainOptionsOverlay.visibility = View.VISIBLE // dark background visible
			binding.mainOptionsOverlay.animate().alpha(1f).setDuration(500).start() // color alpha animate
		}
		else {
			binding.mainOptionsMenu.animate().x(-resources.displayMetrics.widthPixels.toFloat()).setDuration(500).start() // move menu
			// color alpha animate and dark background invisible on animation end
			binding.mainOptionsOverlay.animate().alpha(0f).withEndAction { binding.mainOptionsOverlay.visibility = View.INVISIBLE }.setDuration(500).start()
		}
	}
	
	/**
	 * Call when options menu button been click
	 */
	fun onOptionsMenuOpen(v: View) {
		moveOptionsMenu(true)
	}
	
	/**
	 * Get calendar data
	 */
	fun getCalendar() = SqlHelper.CalendarProcessor(calendar)
	
	/**
	 * Get list data
	 */
	fun getList() = SqlHelper.ListProcessor(list)
	
	/**
	 * On activity been destroy, need close database
	 */
	override fun onDestroy() {
		super.onDestroy()
		if(::lastDataBase.isInitialized) {
			lastDataBase.close()
		}
	}

}