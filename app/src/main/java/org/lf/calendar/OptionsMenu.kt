package org.lf.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * The class is fragment of options menu
 */
class OptionsMenu : Fragment() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_options_menu, container, false)
	}
	
	/**
	 * Call on out of menu space been touch
	 */
	fun onCloseOptionMenu(v: View?) {
		if(activity != null) { // activity is null, means this not attach on any view
			if(activity is MainActivity) { // activity not main, activity must main activity
				(activity as MainActivity).moveOptionsMenu(false)
			}
		}
	}
	
	companion object {
		@JvmStatic
		fun newInstance() =
			OptionsMenu().apply {
				arguments = Bundle().apply {
				}
			}
	}
}