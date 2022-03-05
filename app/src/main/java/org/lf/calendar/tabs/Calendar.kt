package org.lf.calendar.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.lf.calendar.R
import java.time.Instant
import java.util.*

private const val PARAM_YEAR = "calendar.year"
private const val PARAM_MONTH = "calendar.month"
private const val PARAM_DAY = "calendar.day"

class Calendar : Fragment() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let { }
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_calendar, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)



	}

	companion object {
		@JvmStatic
		fun newInstance(year: Int, month: Int, day: Int) =
			Calendar().apply {
				arguments = Bundle().apply { }
			}
	}
}