package org.lf.calendar.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ArrayRes
import org.lf.calendar.R
import org.lf.calendar.io.SqlHelper

class ColorSpinnerAdapter : ArrayAdapter<ColorSpinnerAdapter.ColorSpinnerItem> {
	
	private val list = ArrayList<ColorSpinnerItem>()
	
	constructor(context: Context) : super(context, 0)
	
	constructor(context: Context, @ArrayRes color: Int, @ArrayRes name: Int) : this(context) {
		context.let {
			val colors = it.resources.getStringArray(color)
			val names = it.resources.getStringArray(name)
			
			for(i in colors.indices) {
				val c = Color.parseColor(colors[i])
				registerColor(c, if(i < names.size) names[i] else "")
			}
			
			val sql = SqlHelper.getInstance(it)
			for(i in sql.getColor(sql.readableDatabase).getColor().values) {
				registerColor(i.color, i.name ?: "")
			}
		}
	}
	
	private fun registerColor(color: Int, name: String) {
		val item = ColorSpinnerItem(color, name)
		val index = list.indexOf(item)
		if(index != -1) {
			list[index] = item
			remove(item)
			insert(item, index)
		}
		else {
			list.add(item)
			add(item)
		}
	}
	
	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		return initView(position, parent)
	}
	
	override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
		return initView(position, parent)
	}
	
	/**
	 * get view show on spinner
	 */
	fun initView(position: Int, parent: ViewGroup?): View {
		val view = LayoutInflater.from(context).inflate(R.layout.color_sinner_item, parent, false)
		val item = getItem(position) as ColorSpinnerItem
		view.findViewById<ImageView>(R.id.spinnerColorImage).setBackgroundColor(item.color)
		view.findViewById<TextView>(R.id.spinnerColorText).text = item.name
		return view
	}
	
	/**
	 * data in spinner to show color
	 */
	data class ColorSpinnerItem(val color: Int, val name: String) {
		
		override fun toString(): String {
			return name
		}
		
		override fun equals(other: Any?): Boolean {
			return other is ColorSpinnerItem && other.color == color
		}
		
		override fun hashCode(): Int {
			var result = color
			result = 31 * result + name.hashCode()
			return result
		}
	}
	
}