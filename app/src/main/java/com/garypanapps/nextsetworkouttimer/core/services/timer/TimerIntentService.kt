package com.garypanapps.nextsetworkouttimer.core.services.timer

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Message
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import com.garypanapps.nextsetworkouttimer.R
import com.garypanapps.nextsetworkouttimer.core.entities.Timer
import com.garypanapps.nextsetworkouttimer.core.ServiceLocator

class TimerIntentService : IntentService("TimerIntentService") {
    val timerCurrentTime = MutableLiveData<Handler>()
    private val PERSISTENT_NOTIF_ID = "Persistent"
    private val COMPLETED_NOTIF_ID = "Completed"
    val defaultScope = CoroutineScope(Dispatchers.Default)

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            createNotificationChannel(notificationManager,
                PERSISTENT_NOTIF_ID,
                "Foreground",
                "Persistent Notification", 2, false
            )
            val timerId = intent.getIntExtra("timerId", 0)
            val runningTimer = ServiceLocator.TimerRepo.getTimerById(timerId)
            val notificationDisplayHandler = mHandler(notificationManager, runningTimer!!)

            notificationDisplayHandler.sendEmptyMessage(0)
        }
    }

    private fun createNotificationChannel(notificationManager: NotificationManager, id: String, name: String,
        description: String, channelImportance: Int, playSound: Boolean) {

        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(id, name, channelImportance)

            channel.description = description
            channel.enableLights(true)
            channel.lightColor = Color.BLUE

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createRunningTimerNotification(id: Int, time: String): Notification {
        val channelID = PERSISTENT_NOTIF_ID
        return NotificationCompat.Builder(this, channelID)
            .setContentTitle(time)
            .setContentText("")
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.navigation_empty_icon)
            .setChannelId(channelID)
            .setSubText("test")
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()
    }

    inner class mHandler(val notificationManager: NotificationManager, val runningTimer: Timer) : Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            notificationManager.notify(1, createRunningTimerNotification(122, Timer.getStringTime(runningTimer.getCurrentTime().toInt()!!, false)))
            sendEmptyMessageDelayed(0, 0)
        }
    }
}
