package org.lf.calendar.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.lf.calendar.R
import org.w3c.dom.Text
import java.util.*

class CalendarItemDay : ConstraintLayout {

    var day = Calendar.getInstance()
    var isToday = false
    var isSelect = false
    var isCurrentMonth = false
    
    lateinit var num: TextView
    lateinit var background: ImageView

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
        a.recycle()

        inflate(context, R.layout.view_calendar_item_day, this)
        num = findViewById(R.id.calendar_item_num)
        
    }

    fun setDay(day: Calendar, isToday: Boolean, isCurrentMonth: Boolean) {
        this.day = day
        this.isToday = isToday
        this.isCurrentMonth = isCurrentMonth
        num.text = day.toString()
        background.setImageResource(if(isToday) R.color.default_theme_color else R.color.none)
        num.setTextColor(resources.getColorStateList(if(isCurrentMonth) R.color.calendar_current_month else R.color.calendar_not_current_month, null))
    }

}