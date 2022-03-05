package org.lf.calendar.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.lf.calendar.R
import java.util.*

private const val WEEK_TO_SHOW = 6

private const val MilliOfSecond = 1000
private const val MilliOfMinute = MilliOfSecond * 60
private const val MilliOfHour = MilliOfMinute * 60
private const val MilliOfDay = MilliOfHour * 24

private const val PARAM_YEAR = "calendar.year"
private const val PARAM_MONTH = "calendar.month"
private const val PARAM_DAY = "calendar.day"

class CalendarView : Fragment() {

    var day = 0
    var month = 0
    var year = 0
    private val daysArray = Array<Calendar>(7 * WEEK_TO_SHOW) { Calendar.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            year = it.getInt(PARAM_YEAR)
            month = it.getInt(PARAM_MONTH)
            day = it.getInt(PARAM_DAY)
        }
        initDays()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.view_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    /**
     * Initial days array
     * @param year - the year of the days
     * @param month - the month of the days, 1 to 12
     */
    private fun initDays() {
        val date = Calendar.getInstance()
        date.set(year, month - 1, 0)
        val day = date.get(Calendar.DAY_OF_WEEK) // 1 for Sunday...

        // check the first date of the calendar needed
        val firstDay = Calendar.getInstance().also { it.time = Date(date.time.time - MilliOfDay * (7 + day - 1)) }

        var lastDay = firstDay.time.time

        for(i in 0..(7 * WEEK_TO_SHOW)) {
            daysArray[i].time = Date(lastDay)
            lastDay += MilliOfDay
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(year: Int = 2022, month: Int = 0, day: Int = 0) =
            org.lf.calendar.tabs.List().apply {
                arguments = Bundle().apply {
                    putInt(PARAM_YEAR, year)
                    putInt(PARAM_MONTH, month)
                    putInt(PARAM_DAY, day)
                }
            }
    }

}