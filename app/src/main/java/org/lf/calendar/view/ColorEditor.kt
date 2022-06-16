package org.lf.calendar.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.lf.calendar.MainActivity
import org.lf.calendar.R
import org.lf.calendar.databinding.FragmentColorEditorBinding
import org.lf.calendar.io.SqlHelper
import org.lf.calendar.io.sqlitem.color.SqlColor1

private const val PARAM_OLD = "colorEditor.old"
private const val PARAM_COLOR = "colorEditor.color"
private const val PARAM_NAME = "colorEditor.name"

class ColorEditor : Fragment() {
	
	private var isOld = false
	private var colorCode = Color.argb(255, 0, 0, 0)
		set(value) {
			field = value
			if(::binder.isInitialized) binder.colorEditorColor.setBackgroundColor(value)
		}
	private var name = ""
	private lateinit var binder: FragmentColorEditorBinding
	private lateinit var selector: ColorSelectorFragment
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			isOld = it.getBoolean(PARAM_OLD)
			colorCode = it.getInt(PARAM_COLOR)
			name = it.getString(PARAM_NAME, "")
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		// Inflate the layout for this fragment
		binder = FragmentColorEditorBinding.inflate(inflater)
		return binder.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		selector = childFragmentManager.findFragmentById(binder.colorEditorSelector.id) as ColorSelectorFragment
		binder.colorEditorName.text.append(name)
		
		binder.colorEditorColor.setOnClickListener {
			if(binder.colorEditorCard.visibility == View.GONE) {
				binder.colorEditorCard.visibility = View.VISIBLE
			}
		}
		
		binder.colorEditorCard.visibility = View.GONE
		
		selector.setCallback {
			colorCode = it
		}
		selector.setOnClose {
			binder.colorEditorCard.visibility = View.GONE
		}
		binder.colorEditorConfirm.setOnClickListener { onButtonClick(it) }
		binder.colorEditorCancel.setOnClickListener { onButtonClick(it) }
		
		binder.colorEditorColor.setBackgroundColor(colorCode)
	}
	
	private fun onButtonClick(v: View) {
		when(v.id) {
			R.id.colorEditorConfirm -> {
				val name = if(binder.colorEditorName.text.isNotBlank()) binder.colorEditorName.text.toString() else null
				
				val sql = SqlHelper.getInstance(requireContext())
				val sqlColor = sql.getColor(sql.readableDatabase)
				val colors = sqlColor.getColor()
				if(colors.containsKey(colorCode)) {
					colors[colorCode]!!.name = name
				}
				else {
					sqlColor.addColor(SqlColor1(colorCode, name))
				}
				sqlColor.saveSql(sql.writableDatabase)
				
				val act = requireActivity() as MainActivity
				act.setFragmentToOther(CustomColorFragment.newInstance(), "自訂顏色")
			}
			R.id.colorEditorCancel -> {
				val act = requireActivity() as MainActivity
				act.setFragmentToOther(CustomColorFragment.newInstance(), "自訂顏色")
			}
		}
	}
	
	companion object {
		@JvmStatic
		fun newInstance(initColor: Int? = null, name: String? = null) =
			ColorEditor().apply {
				arguments = Bundle().apply {
					putBoolean(PARAM_OLD, initColor != null)
					putInt(PARAM_COLOR, initColor ?: Color.BLACK)
					putString(PARAM_NAME, name)
				}
			}
	}
}