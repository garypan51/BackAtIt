package waggishstudios.com.backatitworkoutresttimer.core.services.timerService

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
import waggishstudios.com.backatitworkoutresttimer.R
import waggishstudios.com.backatitworkoutresttimer.core.entities.Timer
import waggishstudios.com.backatitworkoutresttimer.core.ServiceLocator

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val ACTION_FOO = "waggishstudios.com.backatitworkoutresttimer.services.timerService.action.FOO"
private const val ACTION_BAZ = "waggishstudios.com.backatitworkoutresttimer.services.timerService.action.BAZ"

// TODO: Rename parameters
private const val EXTRA_PARAM1 = "waggishstudios.com.backatitworkoutresttimer.services.timerService.extra.PARAM1"
private const val EXTRA_PARAM2 = "waggishstudios.com.backatitworkoutresttimer.services.timerService.extra.PARAM2"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
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
//            notificationDisplayHandler.post{
//                notificationManager.notify(1, createRunningTimerNotification(122, Timer.getStringTime(runningTimer?.getCurrentTime()?.toInt()!!, false)))
//                notificationDisplayHandler.post()
//            }


        }
//        when (intent?.action) {
//            ACTION_FOO -> {
//                val param1 = intent.getStringExtra(EXTRA_PARAM1)
//                val param2 = intent.getStringExtra(EXTRA_PARAM2)
//                handleActionFoo(param1, param2)
//            }
//            ACTION_BAZ -> {
//                val param1 = intent.getStringExtra(EXTRA_PARAM1)
//                val param2 = intent.getStringExtra(EXTRA_PARAM2)
//                handleActionBaz(param1, param2)
//            }
//        }
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


    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionFoo(context: Context, param1: String, param2: String) {
            val intent = Intent(context, TimerIntentService::class.java).apply {
                action =
                        ACTION_FOO
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionBaz(context: Context, param1: String, param2: String) {
            val intent = Intent(context, TimerIntentService::class.java).apply {
                action =
                        ACTION_BAZ
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }
    }

    inner class mHandler(val notificationManager: NotificationManager, val runningTimer: Timer) : Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            notificationManager.notify(1, createRunningTimerNotification(122, Timer.getStringTime(runningTimer.getCurrentTime().toInt()!!, false)))
            sendEmptyMessageDelayed(0, 0)
        }
    }
}
