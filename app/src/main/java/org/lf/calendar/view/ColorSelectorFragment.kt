package org.lf.calendar.view

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import org.lf.calendar.databinding.FragmentColorSelectorBinding

private const val PARAM_COLOR_MODE = "color_selector.mode"
private const val PARAM_INIT_COLOR = "color_selector.color"

class ColorSelectorFragment : Fragment() {
	
	private var mode = COLOR_HSV
	private var colorCode: Int = Color.BLACK
		set(value) {
			field = value
			updateColor()
		}
	private lateinit var binder: FragmentColorSelectorBinding
	private var callback: ((it: Int) -> Unit)? = null
	private var closeCallback: (() -> Unit)? = null
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			mode = it.getInt(PARAM_COLOR_MODE)
			colorCode = it.getInt(PARAM_INIT_COLOR)
		}
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		// Inflate the layout for this fragment
		binder = FragmentColorSelectorBinding.inflate(inflater)
		return binder.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		binder.colorBar1.setOnSeekBarChangeListener(OnBarChange())
		binder.colorBar2.setOnSeekBarChangeListener(OnBarChange())
		binder.colorBar3.setOnSeekBarChangeListener(OnBarChange())
		
		binder.colorSelectorClose.setOnClickListener { closeCallback?.let { it() } }
		
		buildBarPosition()
	}
	
	fun setOnClose(func: (() -> Unit)?) {
		if(::binder.isInitialized) {
			closeCallback = func
			binder.colorSelectorClose.setOnClickListener { closeCallback?.let { it() } }
		}
		else closeCallback = func
	}
	
	fun setCallback(func: ((it: Int) -> Unit)?) {
		callback = func
	}
	
	private fun buildBarBackground() {
		when(mode) {
			COLOR_HSV -> {
				val bitmap1 = Bitmap.createBitmap(361, 6, Bitmap.Config.ARGB_8888)
				val bitmap2 = Bitmap.createBitmap(101, 6, Bitmap.Config.ARGB_8888)
				val bitmap3 = Bitmap.createBitmap(101, 6, Bitmap.Config.ARGB_8888)
				var canvas = Canvas(bitmap1)
				val paint = Paint()
				paint.style = Paint.Style.FILL
				for(i in 0..360) {
					paint.color = Color.HSVToColor(floatArrayOf(i.toFloat(), 1f, 1f))
					canvas.drawRect(Rect(i, 2, i + 1, 4), paint)
				}
				canvas.save()
				canvas = Canvas(bitmap2)
				for(i in 0..100) {
					paint.color = Color.HSVToColor(floatArrayOf(binder.colorBar1.progress.toFloat(), i / 100f, 1f))
					canvas.drawRect(Rect(i, 2, i + 1, 4), paint)
				}
				canvas.save()
				canvas = Canvas(bitmap3)
				for(i in 0..100) {
					paint.color = Color.HSVToColor(floatArrayOf(binder.colorBar1.progress.toFloat(), 1f, i / 100f))
					canvas.drawRect(Rect(i, 2, i + 1, 4), paint)
				}
				canvas.save()
				binder.colorBar1.progressDrawable = BitmapDrawable(resources, bitmap1)
				binder.colorBar2.progressDrawable = BitmapDrawable(resources, bitmap2)
				binder.colorBar3.progressDrawable = BitmapDrawable(resources, bitmap3)
				
				colorCode = Color.HSVToColor(floatArrayOf(binder.colorBar1.progress.toFloat(), binder.colorBar2.progress / 100f, binder.colorBar3.progress / 100f))
			}
			COLOR_RGB -> {
				val bitmap1 = Bitmap.createBitmap(256, 6, Bitmap.Config.ARGB_8888)
				val bitmap2 = Bitmap.createBitmap(256, 6, Bitmap.Config.ARGB_8888)
				val bitmap3 = Bitmap.createBitmap(256, 6, Bitmap.Config.ARGB_8888)
				var canvas = Canvas(bitmap1)
				val paint = Paint()
				paint.style = Paint.Style.FILL
				for(i in 0..255) {
//					paint.color = Color.rgb(i, binder.colorBar2.progress, binder.colorBar3.progress)
					paint.color = Color.rgb(i, 0, 0)
					canvas.drawRect(Rect(i, 2, i + 1, 4), paint)
				}
				canvas.save()
				canvas = Canvas(bitmap2)
				for(i in 0..255) {
//					paint.color = Color.rgb(binder.colorBar1.progress, i, binder.colorBar3.progress)
					paint.color = Color.rgb(0, i, 0)
					canvas.drawRect(Rect(i, 2, i + 1, 4), paint)
				}
				canvas.save()
				canvas = Canvas(bitmap3)
				for(i in 0..255) {
//					paint.color = Color.rgb(binder.colorBar1.progress, binder.colorBar2.progress, i)
					paint.color = Color.rgb(0, 0, i)
					canvas.drawRect(Rect(i, 2, i + 1, 4), paint)
				}
				canvas.save()
				binder.colorBar1.progressDrawable = BitmapDrawable(resources, bitmap1)
				binder.colorBar2.progressDrawable = BitmapDrawable(resources, bitmap2)
				binder.colorBar3.progressDrawable = BitmapDrawable(resources, bitmap3)
				
				colorCode = Color.rgb(binder.colorBar1.progress, binder.colorBar2.progress, binder.colorBar3.progress)
			}
		}
	}
	
	private fun updateColor() {
//		when(mode) {
//			COLOR_HSV -> colorCode = Color.HSVToColor(floatArrayOf(binder.colorBar1.progress.toFloat(), binder.colorBar2.progress / 100f, binder.colorBar3.progress / 100f))
//			COLOR_RGB -> colorCode = Color.rgb(binder.colorBar1.progress, binder.colorBar2.progress, binder.colorBar3.progress)
//		}
		binder.colorSelectorColor.setBackgroundColor(colorCode)
//		binder.textView12.setBackgroundColor(colorCode)
	}
	
	private fun buildBarPosition() {
		when(mode) {
			COLOR_HSV -> {
				val hsv = FloatArray(3)
				Color.colorToHSV(colorCode, hsv)
				
				binder.colorBar1.max = 360
				binder.colorBar2.max = 100
				binder.colorBar3.max = 100
				
				binder.colorBar1.progress = hsv[0].toInt()
				binder.colorBar2.progress = (hsv[1] * 100).toInt()
				binder.colorBar3.progress = (hsv[2] * 100).toInt()
			}
			COLOR_RGB -> {
				val rgb = intArrayOf(Color.red(colorCode), Color.green(colorCode), Color.blue(colorCode))
				
				binder.colorBar1.max = 255
				binder.colorBar2.max = 255
				binder.colorBar3.max = 255
				
				binder.colorBar1.progress = rgb[0]
				binder.colorBar2.progress = rgb[1]
				binder.colorBar3.progress = rgb[2]
			}
		}
		buildBarBackground()
	}
	
	private inner class OnBarChange : SeekBar.OnSeekBarChangeListener {
		override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
			buildBarBackground()
			callback?.let { it(colorCode) }
		}
		override fun onStartTrackingTouch(seekBar: SeekBar?) { /*  do nothing  */ }
		override fun onStopTrackingTouch(seekBar: SeekBar?) { /*  do nothing  */ }
	}
	
	companion object {
		const val COLOR_HSV = 0
		const val COLOR_RGB = 1
		@JvmStatic
		fun newInstance(colorMode: Int, initColor: Int = Color.BLACK) =
			ColorSelectorFragment().apply {
				arguments = Bundle().apply {
					putInt(PARAM_COLOR_MODE, colorMode)
					putInt(PARAM_INIT_COLOR, initColor)
				}
			}
	}
}