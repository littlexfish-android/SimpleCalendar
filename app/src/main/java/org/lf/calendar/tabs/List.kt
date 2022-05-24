package org.lf.calendar.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.lf.calendar.MainActivity
import org.lf.calendar.R
import org.lf.calendar.io.SqlHelper
import org.lf.calendar.list.ListEditor
import org.lf.calendar.list.ListList

/**
 * The class is tab of main activity
 */
class List : Fragment() {
	
	private lateinit var list: ListList
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let { }
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_list, container, false)
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		list = childFragmentManager.findFragmentById(R.id.listListFragment) as ListList
		
		view.findViewById<FloatingActionButton>(R.id.listAddGroup).setOnClickListener {
			if(activity != null && activity is MainActivity) {
				(activity as MainActivity).setFragmentToOther(ListEditor.newInstance(), "新增")
			}
		}
		
		reloadFromSql()
		
	}
	
	fun reloadFromSql() {
		val sqlHelper = SqlHelper.getInstance(context)
		val sqlList = sqlHelper.getList(sqlHelper.writableDatabase, 50, "createTime").getList()
		list.reset()
		for(sqlPair in sqlList) {
			list.constructListItem(sqlPair.first, sqlPair.second)
		}
	}
	
	companion object {
		@JvmStatic
		fun newInstance() =
			List().apply {
				arguments = Bundle().apply { }
			}
	}
}