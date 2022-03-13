package org.lf.calendar.calendar

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.lf.calendar.R
import java.util.*

/**
 * The class is use on {@link org.lf.calendar.calendar.CalendarView}, use to show single item of day
 */
class CalendarItemDay : ConstraintLayout {
    
    /* data */
    /**
     * The days this item hold
     */
    private var day: Calendar = Calendar.getInstance()
        set(value) {
            field = value
            num.text = value[Calendar.DAY_OF_MONTH].toString()
        }
    
    /**
     * Check is today
     */
    private var isToday = false
        set(value) {
            field = value
            background.setImageResource(if(value) R.color.default_theme_color else R.color.none)
        }
    
    /**
     * Check the item has been selected
     */
    var isSelect = false
        set(value) {
            field = value
            selectFrame.setImageResource(if(value) R.color.default_theme_color_dark else R.color.none)
        }
    
    /**
     * Check the item is the day of select month
     */
    private var isCurrentMonth = false
        set(value) {
            field = value
            num.setTextColor(resources.getColorStateList(if(value) R.color.calendar_current_month else R.color.calendar_not_current_month, null))
        }
    
    /* inner view */
    
    /**
     * The Text show on the view
     */
    private lateinit var num: TextView
    
    /**
     * The item of background
     */
    private lateinit var background: ImageView
    
    /**
     * The item of select frame
     */
    private lateinit var selectFrame: ImageView
    
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
     * Initial the value
     */
    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.CalendarItemDay, defStyle, 0)
        a.recycle()

        inflate(context, R.layout.view_calendar_item_day, this)
        num = findViewById(R.id.calendarItemNum)
        background = findViewById(R.id.calendarItemBackground)
        selectFrame = findViewById(R.id.calendarItemSelected)
        
    }
    
    /**
     * Set the day of this item
     */
    fun setDay(day: Calendar, isToday: Boolean, isCurrentMonth: Boolean) {
        this.day = day
        this.isToday = isToday
        this.isCurrentMonth = isCurrentMonth
    }

}