package org.lf.calendar.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import org.lf.calendar.R

class ColorComponent : FrameLayout {
	
	private lateinit var colorView: ImageView
	private lateinit var nameView: TextView
	var color: Int = 0
		set(value) {
			field = value
			colorView.setBackgroundColor(value)
		}
	var name: String = ""
		set(value) {
			field = value
			nameView.text = value
		}
	
	
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
		val a = context.obtainStyledAttributes(attrs, R.styleable.ColorComponent, defStyle, 0)
		a.recycle()
		
		inflate(context, R.layout.view_color_component, this)
		
		colorView = findViewById(R.id.colorComponentColor)
		nameView = findViewById(R.id.colorComponentName)
	}
	
}