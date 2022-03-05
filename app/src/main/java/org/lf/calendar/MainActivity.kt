package org.lf.calendar

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.lf.calendar.databinding.ActivityMainBinding
import org.lf.calendar.io.SqlHelper

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var list: SqlHelper.ListProcessor
    lateinit var calendar: SqlHelper.CalendarProcessor
    lateinit var lastDataBase: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val sql = SqlHelper.getInstance(applicationContext)

        lastDataBase = sql.writableDatabase
        list = sql.getList(lastDataBase)
        calendar = sql.getCalendar(lastDataBase)

    }

    override fun onDestroy() {
        super.onDestroy()
        if(::lastDataBase.isInitialized) {
            lastDataBase.close()
        }
    }

}