package org.lf.calendar.calendar

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import org.lf.calendar.R

class CalendarItemDay : ConstraintLayout {

    var month = 0
    var day = 0
    var isToday = false

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
        val a = context.obtainStyledAttributes(attrs, R.styleable.CalendarItemDay, defStyle, 0)

        isToday = a.getBoolean(R.styleable.CalendarItemDay_is_today, false)
        month = a.getInt(R.styleable.CalendarItemDay_month, 0)
        day = a.getInt(R.styleable.CalendarItemDay_day, 0)

        a.recycle()

        inflate(context, R.layout.view_calendar_item_day, this)

    }

    fun setDay(month: Int, day: Int, isToday: Boolean) {
        this.month = month
        this.day = day
        this.isToday = isToday
    }

}