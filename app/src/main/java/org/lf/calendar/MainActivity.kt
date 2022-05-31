package org.lf.calendar

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import org.lf.calendar.databinding.ActivityMainBinding
import org.lf.calendar.io.SqlHelper
import org.lf.calendar.service.NoticeService
import org.lf.calendar.tabs.Calendar
import org.lf.calendar.tabs.List
import org.lf.calendar.tabs.Profile
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

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
	
	private val lastBack = AtomicBoolean(false)
	private val exitTimer = ExitTimer(this)
	
	/* view */
	
	/**
	 * The binding of the activity
	 */
	private lateinit var binding: ActivityMainBinding
	
	/**
	 * The fragment of list
	 */
	lateinit var fragmentList: List
	
	/**
	 * The fragment of calendar
	 */
	lateinit var fragmentCalendar: Calendar
	
	/**
	 * The fragment of profile
	 */
	lateinit var fragmentProfile: Profile
	
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
		
		setFragmentToList()
		// ensure options menu is close
		binding.mainOptionsMenu.x = -resources.displayMetrics.widthPixels.toFloat()
		
		// process when extra not null
		val extra = intent.extras
		if(extra != null) { // call from other activity or my widget
			val event = extra.getString("event")
			if(event != null) {
				if(event == "selectCalendar") { // select the date
					val date = java.util.Calendar.getInstance().also { it.time = Date(extra.getLong("time")) }
					setFragmentToCalendar(day = date)
				}
				if(event == "showPlan") { // show the calendar plan
				
				}
				if(event == "addCalendarPlan") { // add calendar plan
				
				}
				if(event == "addListItem") { // add list item
				
				}
			}
		}
		
		val service = Intent(this, NoticeService::class.java)
		startService(service) // ensure service is on
	}
	
	/**
	 * Switch fragment to fragment of list
	 */
	fun setFragmentToList(reload: Boolean = false) {
		val t = supportFragmentManager.beginTransaction()
		t.replace(R.id.main_tab_container, fragmentList)
		t.setCustomAnimations(R.anim.mv_left_frag_cutin, R.anim.mv_left_frag_cutout)
		t.commit()
		binding.mainTitle.text = "代辦"
		nowFrag = fragmentList
		if(reload) {
			fragmentList.reloadFromSql()
		}
	}
	
	/**
	 * Switch fragment to fragment of calendar
	 */
	fun setFragmentToCalendar(fromLeft: Boolean = true, reload: Boolean = false, day: java.util.Calendar? = null) {
		val t = supportFragmentManager.beginTransaction()
		t.replace(R.id.main_tab_container, fragmentCalendar)
		if(fromLeft) {
			t.setCustomAnimations(R.anim.mv_right_frag_cutin, R.anim.mv_right_frag_cutout)
		} else {
			t.setCustomAnimations(R.anim.mv_left_frag_cutin, R.anim.mv_left_frag_cutout)
		}
		t.commit()
		binding.mainTitle.text = "月曆"
		nowFrag = fragmentCalendar
		if(day != null) {
			fragmentCalendar.setDay(day[java.util.Calendar.YEAR], day[java.util.Calendar.MONTH], day[java.util.Calendar.DAY_OF_MONTH])
		}
	}
	
	fun setCalendarDay(year: Int, month: Int, day: Int) {
		if(nowFrag == fragmentCalendar) {
			fragmentCalendar.setDay(year, month, day)
		}
	}
	
	/**
	 * Switch fragment to fragment of profile
	 */
	private fun setFragmentToProfile() {
		val t = supportFragmentManager.beginTransaction()
		t.replace(R.id.main_tab_container, fragmentProfile)
		t.setCustomAnimations(R.anim.mv_right_frag_cutin, R.anim.mv_right_frag_cutout)
		t.commit()
		binding.mainTitle.text = "資料"
		nowFrag = fragmentProfile
	}
	
	fun setFragmentToOther(frag: Fragment, title: String = "") {
		findViewById<FragmentContainerView>(R.id.main_tab_container).post {
			val t = supportFragmentManager.beginTransaction()
			t.replace(R.id.main_tab_container, frag)
			t.setCustomAnimations(R.anim.frag_fade_in, R.anim.frag_fade_out)
			t.commit()
			binding.mainTitle.text = title
			nowFrag = frag
		}
	}
	
	/**
	 * Call when list button been click
	 */
	fun onListButtonPressed(v: View?) {
		if(nowFrag == fragmentList) return
		setFragmentToList(true)
	}
	
	/**
	 * Call when calendar button been click
	 */
	fun onCalendarButtonPressed(v: View?) {
		if(nowFrag == fragmentCalendar) return
		setFragmentToCalendar(nowFrag == fragmentList, true)
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
	fun getCalendar() = calendar
	
	/**
	 * Get list data
	 */
	fun getList() = list
	
	override fun onBackPressed() {
		val m = nowFrag?.let { it::class.java.methods.find { it2 -> it2.name == "onBackPressed" && it2.returnType == Boolean::class.java } }
		if(m != null && (m.invoke(nowFrag) as? Boolean == true)) return
		if(lastBack.get()) {
			finish()
		}
		else {
			lastBack.set(true)
			exitTimer.start()
			Toast.makeText(this, "在按一次退出", Toast.LENGTH_SHORT).show()
		}
	}
	
	fun resetLastBack() {
		lastBack.set(false)
	}
	
	/**
	 * On activity been destroy, need close database
	 */
	override fun onDestroy() {
		super.onDestroy()
		if(::lastDataBase.isInitialized) {
			calendar.saveSql(lastDataBase)
			list.saveSql(lastDataBase)
			SqlHelper.getInstance(applicationContext).getColor(lastDataBase).saveSql(lastDataBase)
		}
	}
	
	class ExitTimer(private val activity: MainActivity) : CountDownTimer(3 * 1000L, 3 * 1000L) {
		override fun onTick(millisUntilFinished: Long) {
			// do nothing
		}
		
		override fun onFinish() {
			activity.resetLastBack()
		}
	}
	
}