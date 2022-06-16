package org.lf.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import org.lf.calendar.databinding.FragmentOptionsMenuBinding
import org.lf.calendar.view.CustomColorFragment

/**
 * The class is fragment of options menu
 */
class OptionsMenu : Fragment() {
	
	private lateinit var binder: FragmentOptionsMenuBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		binder = FragmentOptionsMenuBinding.inflate(inflater)
		return binder.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binder.optionsOverlay.setOnClickListener {
			if(activity != null) { // activity is null, means this not attach on any view
				if(activity is MainActivity) { // activity not main, activity must main activity
					(activity as MainActivity).moveOptionsMenu(false)
				}
			}
		}
		binder.optionsClose.setOnClickListener {
			if(activity != null) { // activity is null, means this not attach on any view
				if(activity is MainActivity) { // activity not main, activity must main activity
					(activity as MainActivity).moveOptionsMenu(false)
				}
			}
		}
		binder.menuColor.setOnClickListener { openFragment(it) }
	}
	
	private fun openFragment(v: View) {
		if(activity != null) {
			val act = activity as MainActivity
			when(v.id) {
				R.id.menuColor -> {
					act.setFragmentToOther(CustomColorFragment.newInstance(), "自訂顏色")
				}
			}
			act.moveOptionsMenu(false)
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