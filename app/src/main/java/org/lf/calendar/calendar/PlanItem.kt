package org.lf.calendar.calendar

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.lf.calendar.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * The format of 24 hour
 */
const val DATE_NORMAL24 = "HH:mm:ss"

/**
 * The format of 12 hour
 */
const val DATE_NORMAL12 = "a hh:mm:ss"

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
	 * The time format use to show
	 */
	var timeFormat = DATE_NORMAL24

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
	fun setContent(time: Long, content: String) {
		this.time.clear()
		this.time.time = Date(time)
		this.content = content
		updateContent()
	}
	
	/**
	 * Update the plan
	 */
	private fun updateContent() {
		findViewById<TextView>(R.id.plan_content).text = content
		findViewById<TextView>(R.id.plan_time).text = SimpleDateFormat.getTimeInstance().format(time.time)
	}

}