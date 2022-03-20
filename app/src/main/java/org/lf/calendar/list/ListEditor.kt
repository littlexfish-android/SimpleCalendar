package org.lf.calendar.list

import android.os.Bundle
import android.util.ArrayMap
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import org.lf.calendar.R


class ListEditor : Fragment() {
	
	/* data */
	
	
	
	/* view */
	
	private lateinit var groupView: EditText
	
	private lateinit var items: LinearLayout
	
	private val itemViews = ArrayMap<String, CheckBox>()
	
	//TODO
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_list_editor, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		
	}
	
	companion object {
		@JvmStatic
		fun newInstance() =
			ListEditor().apply {
				arguments = Bundle().apply {
				}
			}
	}
}