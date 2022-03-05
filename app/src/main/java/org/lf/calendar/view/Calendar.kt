package org.lf.calendar.view

import android.content.Context
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import org.lf.calendar.R

class Calendar : ConstraintLayout {

    var day = 0
    var month = 0
    var year = 2022

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
        val a = context.obtainStyledAttributes(attrs, R.styleable.Calendar, defStyle, 0)

        year = a.getInt(R.styleable.Calendar_year, 0)
        month = a.getInt(R.styleable.Calendar_month, 0)
        day = a.getInt(R.styleable.Calendar_day, 0)

        a.recycle()

        inflate(context, R.layout.view_calendar, this)


    }

}