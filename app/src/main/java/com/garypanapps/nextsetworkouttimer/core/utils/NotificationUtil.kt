package com.garypanapps.nextsetworkouttimer.core.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.garypanapps.nextsetworkouttimer.R
import com.garypanapps.nextsetworkouttimer.core.TimeConstants
import com.garypanapps.nextsetworkouttimer.core.entities.Timer
import com.garypanapps.nextsetworkouttimer.ui.activities.MainActivity
import com.garypanapps.nextsetworkouttimer.ui.utils.TextUtil
import android.media.RingtoneManager
import com.garypanapps.nextsetworkouttimer.core.IntentConstants

class NotificationUtil {
    companion object {
        private const val BARBELL_ICON = R.drawable.ic_barbell_24dp
        fun createNotificationChannel(notificationManager: NotificationManager?, id: String, name: String, description: String,
                                      channelImportance: Int) {
            if (Build.VERSION.SDK_INT >= 26) {
                val channel = NotificationChannel(id, name, channelImportance)
                channel.description = description
                channel.enableLights(true)
                channel.lightColor = Color.BLUE
//                if(playSound) {
//                    val notificationAudioAttribute = AudioAttributes.Builder()
//                        .setFlags(AudioAttributes.USAGE_NOTIFICATION)
//                        .build()
//                    val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//                    channel.setSound(alarmSound, notificationAudioAttribute)
//                }
                notificationManager?.createNotificationChannel(channel)
            }
        }

        fun createActiveTimerNotification(timer: Timer, notificationId: String, context: Context): Notification {
            val activeTimerNotificationView = RemoteViews(context.packageName, R.layout.notification_running_timer)
            val timerCompletePercentage = getTimerCompletePercentage(timer)
            var remainingTimeColor = "green"
            if(timerCompletePercentage < 0.25) {
                remainingTimeColor = "red"
            }
            activeTimerNotificationView.setTextViewText(R.id.timeRemainingView,
                TextUtil.getColoredText(timer.getCurrentTimeForDisplay(false), remainingTimeColor))
            activeTimerNotificationView.setTextViewText(R.id.setCounterView,
                TextUtil.getColoredText(timer.setCounter.toString(), "green"))

            val openAppPendingIntent = createOpenAppPI(false, 0, context)
            val stopServicePendingIntent = createStopServicePI(context)
            val stopServiceAction = NotificationCompat.Action.Builder(R.drawable.ic_close_24dp, "Close", stopServicePendingIntent).build()
            return NotificationCompat.Builder(context, notificationId)
                .setSmallIcon(BARBELL_ICON)
                .setContentTitle("")
                .setContentText("")
                .setAutoCancel(true)
                .addAction(stopServiceAction)
                .setCustomContentView(activeTimerNotificationView)
                .setContentIntent(openAppPendingIntent)
                .setSubText("Tap To View Timers")
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .build()
        }

        fun createMultipleActiveTimerNotification(notificationId: String, context: Context): Notification {
            return NotificationCompat.Builder(context, notificationId)
                .setSmallIcon(BARBELL_ICON)
                .setContentTitle("Multiple Timers")
                .setContentText("One or More Timer is Running")
                .setAutoCancel(true)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .build()
        }

        fun createCompletedTimerNotification(timer: Timer, notificationId: String, context: Context): Notification {
            val completedTimerNotificationView = RemoteViews(context.packageName, R.layout.notification_completed_timer)
            completedTimerNotificationView.setTextViewText(R.id.timerTimeView,
                TextUtil.getColoredText(timer.initTimeString, "red"))
            completedTimerNotificationView.setTextViewText(R.id.setCounterView,
                TextUtil.getColoredText(timer.setCounter.toString(), "red"))

            val openAppPendingIntent = createOpenAppPI(false, 0, context)
            val stopServicePendingIntent = createStopServicePI(context)
            val stopServiceAction = NotificationCompat.Action.Builder(R.drawable.ic_close_24dp, "Close", stopServicePendingIntent).build()
            val startTimerPendingIntent = createRestartTimerPI(timer.id!!, context)
            completedTimerNotificationView.setOnClickPendingIntent(R.id.restartButton, startTimerPendingIntent)

            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            return NotificationCompat.Builder(context, notificationId)
                .setSmallIcon(BARBELL_ICON)
                .setContentTitle("")
                .setContentText("")
                .setAutoCancel(true)
                .setSound(alarmSound)
                .addAction(stopServiceAction)
                .setCustomContentView(completedTimerNotificationView)
                .setContentIntent(openAppPendingIntent)
                .setSubText("Tap Start Button to Restart Timer")
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .build()
        }

        private fun getTimerCompletePercentage(timer: Timer): Double {
            return timer.getCurrentTime().toDouble() / (timer.getInitTotalTime() * TimeConstants.SECOND_IN_MILLISECONDS)
        }

        private fun createOpenAppPI(shouldScroll: Boolean, id: Int, context: Context): PendingIntent {
            val openAppNotificationIntent = Intent(context, MainActivity::class.java)
            openAppNotificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            if (shouldScroll) {
                openAppNotificationIntent.putExtra("ScrollId", id)
                openAppNotificationIntent.putExtra("OpenFromApp", true)
            }

            return PendingIntent.getActivity(context, id, openAppNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        private fun createStopServicePI(context: Context): PendingIntent {
            val exitIntent = Intent(IntentConstants.TimerService.STOP_SERVICE)
            return PendingIntent.getBroadcast(context, 0, exitIntent, 0)
        }

        private fun createRestartTimerPI(timerId: Int, context: Context): PendingIntent {
            val startTimerIntent = Intent(IntentConstants.TimerService.RESTART_TIMER)
            startTimerIntent.putExtra("timerId", timerId)
            return PendingIntent.getBroadcast(context, timerId,startTimerIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}