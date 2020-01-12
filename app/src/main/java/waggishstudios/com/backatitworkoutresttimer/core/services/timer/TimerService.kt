package waggishstudios.com.backatitworkoutresttimer.core.services.timer

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import waggishstudios.com.backatitworkoutresttimer.core.ServiceLocator
import waggishstudios.com.backatitworkoutresttimer.core.entities.Timer
import waggishstudios.com.backatitworkoutresttimer.core.utils.NotificationUtil
import android.content.BroadcastReceiver
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.IntentFilter
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.RetryStrategy
import com.firebase.jobdispatcher.Trigger
import waggishstudios.com.backatitworkoutresttimer.core.IntentConstants
import waggishstudios.com.backatitworkoutresttimer.core.TimeConstants
import waggishstudios.com.backatitworkoutresttimer.core.TimerStatus

class TimerService : LifecycleService() {
    companion object {
        const val FOREGROUND_ID = 2140000000
        const val PERSISTENT_ACTIVE_CHANNEL_ID = "PERSISTENT_ACTIVE"
        const val PERSISTENT_COMPLETED_CHANNEL_ID = "PERSISTENT_COMPLETED"
    }

    private val parentJob = Job()
    private val scope = CoroutineScope(Dispatchers.Main + parentJob)

    var activeTimers = 0
    private var notificationManager: NotificationManager? = null
    val notificationHandler = Handler()
    private lateinit var localBroadcastManager : LocalBroadcastManager
    private val dispatcher by lazy { FirebaseJobDispatcher(GooglePlayDriver(applicationContext)) }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                IntentConstants.TimerService.STOP_SERVICE -> {
                    notificationHandler.removeMessages(0)
                    stopForeground(true)
                }

                IntentConstants.TimerService.RESTART_TIMER -> {
                    val timerId = intent.extras?.getInt("timerId") ?: 0
                    startTimer(timerId)
                }

                IntentConstants.TimerService.START_SERVICE_TIMEOUT -> {
                    val stopTimerServiceJob = dispatcher.newJobBuilder()
                            .setService(StopTimerJobService::class.java)
                            .setTag("killService")
                            .setTrigger(Trigger.executionWindow(20 * TimeConstants.MINUTE_IN_SECONDS, (20 + 1) * TimeConstants.MINUTE_IN_SECONDS))
                            .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                            .build()
                    dispatcher.mustSchedule(stopTimerServiceJob)
                }

                IntentConstants.TimerService.CANCEL_SERVICE_TIMEOUT -> {
                    dispatcher.cancelAll()
                }

            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(IntentConstants.TimerService.STOP_SERVICE)
        intentFilter.addAction(IntentConstants.TimerService.RESTART_TIMER)
        registerReceiver(broadcastReceiver, intentFilter)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        NotificationUtil.createNotificationChannel(notificationManager,
            PERSISTENT_ACTIVE_CHANNEL_ID, "Running Timer",
            "Persistent Notification", 2)
        NotificationUtil.createNotificationChannel(notificationManager,
            PERSISTENT_COMPLETED_CHANNEL_ID, "Completed Timer",
            "Persistent Notification", 4)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleForeground()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        localBroadcastManager.unregisterReceiver(broadcastReceiver)
        notificationManager?.cancelAll()
        super.onDestroy()
    }

    private fun handleForeground() = scope.launch {
        val numberOfActiveTimers = getNumberOfActiveTimers()
        when (numberOfActiveTimers) {
            0 -> {
                cancelForeground()
            }
            1 -> {
                val activeTimer = getActiveTimer()
                val notificationCurrentTime = MutableLiveData<Handler>()
                activeTimer?.let { createForegroundForSingleActiveTimer(activeTimer, notificationCurrentTime) }
            }
            else -> {
                notificationHandler.removeMessages(0)
                val persistentNotification = NotificationUtil.createMultipleActiveTimerNotification(
                    PERSISTENT_ACTIVE_CHANNEL_ID, applicationContext)
                startForeground(FOREGROUND_ID, persistentNotification)
            }
        }
    }

    private fun cancelForeground() {
        notificationHandler.removeMessages(0)
        stopForeground(true)
    }

    private fun createForegroundForSingleActiveTimer(timer: Timer, notificationCurrentTime: MutableLiveData<Handler>) {
        notificationCurrentTime.observe(this, Observer {
            timer.getCurrentTime().let {currentTime ->
                scope.launch {
                    val persistentNotification : Notification
                    if(currentTime > 0L && getNumberOfActiveTimers()!! > 0) {
                        persistentNotification = NotificationUtil.createActiveTimerNotification(timer,
                            PERSISTENT_ACTIVE_CHANNEL_ID, applicationContext)
                        startForeground(FOREGROUND_ID, persistentNotification)
                        notificationHandler.postDelayed ({notificationCurrentTime.value = it}, 1000L)
                    } else {
                        notificationHandler.removeMessages(0)
                        CoroutineScope(Dispatchers.Default).launch { ServiceLocator.TimerRepo.updateTimerStatus(timer.id, TimerStatus.COMPLETED) }
                        persistentNotification = NotificationUtil.createCompletedTimerNotification(timer,
                            PERSISTENT_COMPLETED_CHANNEL_ID, applicationContext)
                        notificationManager?.notify(FOREGROUND_ID, persistentNotification)
                    }
                }
            }
        })
        notificationHandler.post{ notificationCurrentTime.value = notificationHandler }
    }

    private suspend fun getNumberOfActiveTimers(): Int? {
        return withContext(Dispatchers.IO) { ServiceLocator.TimerRepo.getNumberOfActiveTimersInDB() }
    }

    private fun getActiveTimer(): Timer? {
        var activeTimer : Timer? = null
        val timersInDB = runBlocking(Dispatchers.Default) { ServiceLocator.TimerRepo.getListOfSortedTimers() }
        timersInDB?.let {
            for (timer in it){
                if (timer.isRunning() || timer.isCompleted()) {
                    activeTimer = timer
                    break
                }
            }
        }
        return activeTimer
    }

    fun startTimer(id: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            ServiceLocator.TimerRepo.startTimerInDB(id)
            withContext(Dispatchers.Main) {
                handleForeground()
                val intent = Intent(IntentConstants.TimerService.ALERT_TIMER_RESTART)
                applicationContext.sendBroadcast(intent)
            }
        }
    }
}