package org.lf.calendar.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.lf.calendar.MainActivity
import org.lf.calendar.R
import org.lf.calendar.io.SqlHelper
import org.lf.calendar.io.sqlitem.calendar.SqlCalendar1
import java.util.*

const val wait = 30 * 1000L
const val channelId = "simpleCalendar.notice"

class NoticeService : Service() {
	
	class NoticeThread(private val context: Context) : Thread() {
		
		// TODO: test background
		override fun run() {
			//init
			createChannel()
			val sql = SqlHelper.getInstance(context)
			var calendar = sql.getCalendar(sql.readableDatabase, "WHERE notice < 3")
			
			// notice
			while(true) {
				val now = System.currentTimeMillis()
				val list = calendar.getCalendar()
				
				var change = false
				
				Log.e("${Date()} TAG", list.joinToString(", ")) // FIXME: remove
				
				for(item in list) {
					// check reminder
					if(item.remindTime != null && !item.isReminderNotice) {
						if(item.remindTime!!.time < now) {
							notice(item, true)
							item.isReminderNotice = true
							change = true
						}
					}
					else if(item.remindTime == null) {
						item.isReminderNotice = true
						change = true
					}
					
					// check notice
					if(!item.isNotice) {
						if(item.time.time < now) {
							notice(item)
							item.isNotice = true
							change = true
						}
					}
				}
				
				if(change) {
					calendar.saveSql(sql.writableDatabase)
				}
				
				//reload
				calendar = sql.getCalendar(sql.readableDatabase, "WHERE notice < 3")
				sleep(wait)
			}
		}
		
		private fun notice(sql: SqlCalendar1, remind: Boolean = false) {
			val intent = Intent(context, MainActivity::class.java)
			intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
			
			val pendingIntent = PendingIntent.getActivity(context, sql._id, intent, PendingIntent.FLAG_IMMUTABLE)
			
			val r = sql.remark
			val remark = if(r != null && r.length > 10) r.substring(0..10) else r
			
			val builder = NotificationCompat.Builder(context, channelId)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle("${if(remind) "提醒" else "通知"}: ${sql.content}")
				.setColor(sql.color)
				.setContentText(remark ?: "")
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setContentIntent(pendingIntent)
				.setAutoCancel(true)
			
			NotificationManagerCompat.from(context).notify(sql._id, builder.build())
			
		}
		
		private fun createChannel() {
			if(Build.VERSION.SDK_INT >= 26) {
				val name = context.resources.getString(R.string.noticeChannelName)
				val import = NotificationManager.IMPORTANCE_DEFAULT
				val channel = NotificationChannel(channelId, name, import)
				(context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
			}
		}
		
	}
	
	private lateinit var thread: NoticeThread
	
	override fun onCreate() {
		super.onCreate()
		thread = NoticeThread(applicationContext)
		thread.start()
	}
	
	override fun onBind(intent: Intent): IBinder? {
		return null
	}
	
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		return START_STICKY
	}
	
}