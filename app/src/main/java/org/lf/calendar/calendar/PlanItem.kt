package org.lf.calendar.calendar

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.lf.calendar.R
import java.text.SimpleDateFormat
import java.util.*

const val DATE_NORMAL24 = "HH:mm:ss"
const val DATE_NORMAL12 = "a hh:mm:ss"

class PlanItem : ConstraintLayout {

	val time = Calendar.getInstance().also { it.time = Date() }
	var content = ""
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

	private fun init(attrs: AttributeSet?, defStyle: Int) {
		// Load attributes
		val a = context.obtainStyledAttributes(attrs, R.styleable.PlanItem, defStyle, 0)
		a.recycle()

		inflate(context, R.layout.view_plan_item, this)

		updateContent()

	}

	fun setContent(time: Long, content: String) {
		this.time.clear()
		this.time.time = Date(time)
		this.content = content
		updateContent()
	}

	private fun updateContent() {
		findViewById<TextView>(R.id.plan_content).text = content
		findViewById<TextView>(R.id.plan_time).text = SimpleDateFormat.getTimeInstance().format(time.time)
	}

}