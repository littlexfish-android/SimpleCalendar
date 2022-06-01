package org.lf.calendar.calendar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.lf.calendar.R
import org.lf.calendar.io.sqlitem.calendar.SqlCalendar1
import java.text.SimpleDateFormat
import java.util.*

/**
 * The format of 24 hour
 */
const val DATE_NORMAL24 = "HH:mm"


/**
 * The format of 12 hour
 */
const val DATE_NORMAL12 = "a hh:mm"

/**
 * The class is contains the plan and its time
 */
class PlanItem : ConstraintLayout {
	
	/**
	 * The time of the plan
	 */
	val time: Calendar = Calendar.getInstance().also { it.time = Date() }
	
	/**
	 * The content of the plan
	 */
	private var content = ""
	
	/**
	 * the color of plan
	 */
	private var color = 0
	
	/**
	 * The time format use to show
	 */
	var timeFormat = DATE_NORMAL24
	
	/**
	 * the sql data
	 */
	lateinit var sqlItem: SqlCalendar1

	constructor(context: Context) : super(context) {
		init(null, 0)
	}

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		init(attrs, 0)
	}

	constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
		init(attrs, defStyle)
	}
	
	/**
	 * Initial the content and update it
	 */
	private fun init(attrs: AttributeSet?, defStyle: Int) {
		// Load attributes
		val a = context.obtainStyledAttributes(attrs, R.styleable.PlanItem, defStyle, 0)
		a.recycle()

		inflate(context, R.layout.view_plan_item, this)

		updateContent()

	}
	
	/**
	 * Set content and time to this plan
	 */
	fun setContent(time: Long, content: String, color: Int = Color.BLACK) {
		this.time.clear()
		this.time.time = Date(time)
		this.content = content
		this.color = color
		updateContent()
	}
	
	/**
	 * set sql data
	 */
	fun attachSqlItem(sql: SqlCalendar1) {
		sqlItem = sql
		setContent(sql.time.time, sql.content, sql.color)
	}
	
	/**
	 * Update the plan
	 */
	private fun updateContent() {
		if(this::sqlItem.isInitialized) {
			findViewById<TextView>(R.id.plan_content).text = sqlItem.content
			findViewById<TextView>(R.id.plan_time).text = SimpleDateFormat(timeFormat, Locale.CHINESE).format(sqlItem.time)
			findViewById<TextView>(R.id.plan_content).setTextColor(sqlItem.color)
		}
		else {
			findViewById<TextView>(R.id.plan_content).text = content
			findViewById<TextView>(R.id.plan_time).text = SimpleDateFormat(timeFormat, Locale.CHINESE).format(time.time)
			findViewById<TextView>(R.id.plan_content).setTextColor(color)
		}
		
	}

}