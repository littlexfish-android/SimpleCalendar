package org.lf.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class TestActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_test)
		
		if(intent.extras != null) {
			findViewById<TextView>(R.id.testText).text = intent.extras!!.getString("test")
		}
		
	}
}