package org.lf.calendar.view

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.lf.calendar.MainActivity
import org.lf.calendar.R
import org.lf.calendar.databinding.FragmentCustomColorBinding
import org.lf.calendar.io.SqlHelper
import org.lf.calendar.io.sqlitem.color.SqlColor1

class CustomColorFragment : Fragment() {
	
	private lateinit var binder: FragmentCustomColorBinding
	
	private var colors = ArrayList<SqlColor1>()
	private val task = ArrayList<Runnable>()
	private val loadSql = {
		val sql = SqlHelper.getInstance(context)
		val sqlColor = sql.getColor(sql.readableDatabase)
		colors.addAll(sqlColor.getColor().values)
	}
	private val buildView = {
		binder.customColorList.removeAllViews()
		for(it in colors) {
			val v = ColorComponent(requireContext())
			binder.customColorList.addView(v)
			v.color = it.color
			v.name = it.name ?: ""
			v.setOnLongClickListener { _ ->
				AlertDialog.Builder(requireContext())
					.setPositiveButton(R.string.Confirm) { _, _ ->
						binder.customColorList.removeView(v)
						deleteData(it)
					}
					.setCancelable(true)
					.setTitle(R.string.Delete)
					.setMessage(R.string.DeleteConfirm)
					.create().show()
				true
			}
			v.setOnClickListener {
				if(activity != null) {
					(activity as MainActivity).setFragmentToOther(ColorEditor.newInstance(v.color, v.name), "編輯顏色")
				}
			}
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		// Inflate the layout for this fragment
		binder = FragmentCustomColorBinding.inflate(inflater)
		return binder.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		binder.customColorAdd.setOnClickListener {
			if(activity != null) {
				(activity as MainActivity).setFragmentToOther(ColorEditor.newInstance(), "新增顏色")
			}
		}
		
		if(task.isNotEmpty()) for(it in task) it.run()
		if(colors.isNotEmpty()) {
			buildView()
		}
		else {
			task.add { buildView() }
		}
		
	}
	
	override fun onAttach(context: Context) {
		super.onAttach(context)
		loadSql()
		if(::binder.isInitialized) {
			if(task.isNotEmpty()) {
				for(it in task) it.run()
			}
			else buildView()
		}
	}
	
	private fun reloadFromSql() {
//		if(!::binder.isInitialized || ctx == null) return
		
		binder.customColorList.removeAllViews()
		val sql = SqlHelper.getInstance(context)
		val sqlColor = sql.getColor(sql.readableDatabase)
		val colors = sqlColor.getColor()
		
		Log.e("colors", colors.keys.joinToString(","))
		
		for(it in colors.values) {
			val v = ColorComponent(requireContext())
			binder.customColorList.addView(v)
			v.color = it.color
			v.name = it.name ?: ""
			v.setOnLongClickListener { _ ->
				AlertDialog.Builder(requireContext())
					.setPositiveButton(R.string.Confirm) { _, _ ->
						binder.customColorList.removeView(v)
						sqlColor.deleteColor(it)
						sqlColor.saveSql(sql.writableDatabase)
					}
					.setCancelable(true)
					.setTitle(R.string.Delete)
					.setMessage(R.string.DeleteConfirm)
					.create().show()
				true
			}
			v.setOnClickListener {
				if(activity != null) {
					(activity as MainActivity).setFragmentToOther(ColorEditor.newInstance(v.color, v.name), "編輯顏色")
				}
			}
		}
	}
	
	private fun deleteData(color: SqlColor1) {
		val sql = SqlHelper.getInstance(context)
		val colors = sql.getColor(sql.writableDatabase)
		colors.deleteColor(color)
	}
	
	companion object {
		@JvmStatic
		fun newInstance() =
			CustomColorFragment().apply {
				arguments = Bundle().apply {
				}
			}
	}
}