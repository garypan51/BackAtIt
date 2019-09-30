package waggishstudios.com.backatitworkoutresttimer.core.repositories

import android.content.Context
import android.os.SystemClock
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import waggishstudios.com.backatitworkoutresttimer.core.TimerStatus
import waggishstudios.com.backatitworkoutresttimer.core.database.AppDatabase
import waggishstudios.com.backatitworkoutresttimer.core.entities.Timer

class TimerRepository(context: Context) {
    private var db = AppDatabase.getDatabase(context)
    private val timerDao = db.timerDao()
    val numberOfTimers: LiveData<Int> = timerDao.getLiveDataNumberOfTimers()
    val numberOfActiveTimers: LiveData<Int> = timerDao.getLiveDataNumberOfActiveTimers()

    @WorkerThread
    fun addTimerToDB(timer : Timer){
        timerDao.insertTimer(timer)
    }

    @WorkerThread
    fun getTimerById(timerId: Int): Timer? {
        return timerDao.getTimer(timerId)
    }

    @WorkerThread
    fun getNewestTimerInDB(): Timer? {
        return timerDao.getLastTimer()
    }

    @WorkerThread
    fun getNewestTimerIdInDB(): Int? {
        return timerDao.getLatestTimerId()
    }

    @WorkerThread
    fun getListOfSortedTimers(): List<Timer>? {
        return timerDao.getTimersSortedByPosition()
    }

    @WorkerThread
    fun getAllSortedTimerIds(): List<Int>?{
        return timerDao.getTimerIdsSortedByPosition()
    }

    @WorkerThread
    fun getAllTimers(): List<Timer>?{
        return timerDao.getTimers()
    }

    @WorkerThread
    fun updateTimerInDB(timer: Timer) {
        timerDao.updateTimer(timer)
    }

    @WorkerThread
    fun updateTimerPositionInDB(id: Int, newPosition: Int){
        timerDao.updateTimerPosition(id, newPosition)
    }

    @WorkerThread
    fun updateTimerStartTime(timerId: Int, startTime: Long) {
        timerDao.updateTimerStartTime(timerId, startTime)
    }

    @WorkerThread
    fun updateTimerPausedTime(timerId: Int, pausedTime: Long) {
        timerDao.updateTimerPausedTime(timerId, pausedTime)
    }

    @WorkerThread
    fun updateTimerStatus(timerId: Int, newStatus : TimerStatus) {
        timerDao.updateTimerStatus(timerId, newStatus)
    }

    @WorkerThread
    fun updateTimerSetCounter(timerId: Int, setCounter: Int) {
        timerDao.updateTimerSetCounter(timerId, setCounter)
    }

    @WorkerThread
    fun updateAnimationProgress(timerId: Int, progress: Long) {
        timerDao.updateAnimationProgress(timerId, progress)
    }

    @WorkerThread
    fun updateTimerShowNotifications(timerId: Int, shouldShow: Boolean) {
        timerDao.updateTimerShowNotification(timerId, shouldShow)
    }

    @WorkerThread
    fun deleteTimerById(timerId: Int) {
        timerDao.deleteTimerById(timerId)
    }

    @WorkerThread
    fun resetAllTimers() {
        timerDao.resetAllTimerStatuses()
        timerDao.resetAllTimerPausedTimes()
        timerDao.resetAllTimerSetCounters()
    }

    @WorkerThread
    fun startTimerInDB(timerId: Int) {
        val timer = getTimerById(timerId)
        timer?.let {
            if((it.isInactive() || it.isCompleted()) && it.shouldAutoInc){
                if(it.autoReset != 0 && it.setCounter + 1 > it.autoReset) {
                    updateTimerSetCounter(timerId, 1)
                } else {
                    updateTimerSetCounter(timerId, it.setCounter + 1)
                }
            }
        }
        updateTimerStatus(timerId, TimerStatus.RUNNING)
        updateTimerStartTime(timerId, SystemClock.elapsedRealtime())
    }

    @WorkerThread
    fun getNumberOfActiveTimersInDB(): Int {
        return timerDao.getNumberOfActiveTimers()
    }
}