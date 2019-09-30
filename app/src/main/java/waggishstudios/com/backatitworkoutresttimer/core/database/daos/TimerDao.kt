package waggishstudios.com.backatitworkoutresttimer.core.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import waggishstudios.com.backatitworkoutresttimer.core.TimerStatus
import waggishstudios.com.backatitworkoutresttimer.core.entities.Timer

@Dao
interface TimerDao {
    /*************************************************************
                            INSERT CALLS
     *************************************************************/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTimer(timer: Timer)

    /*************************************************************
                            QUERY GET CALLS
     *************************************************************/
    @Query("SELECT * FROM Timer WHERE id == :id")
    fun getTimer(id: Int): Timer

    @Query("SELECT * FROM Timer WHERE name == :name")
    fun getTimerByName(name: String): List<Timer>

    @Query("SELECT * FROM Timer")
    fun getTimers(): List<Timer>

    @Query("SELECT * FROM Timer ORDER BY position")
    fun getLiveDataTimersSortedByPosition(): LiveData<List<Timer>>

    @Query("SELECT * FROM Timer ORDER BY position")
    fun getTimersSortedByPosition(): List<Timer>

    @Query("SELECT id FROM Timer ORDER BY position")
    fun getTimerIdsSortedByPosition(): List<Int>

    @Query("SELECT * FROM Timer ORDER BY id DESC LIMIT 1")
    fun getLastTimer(): Timer

    @Query("SELECT id FROM Timer ORDER BY id DESC LIMIT 1")
    fun getLatestTimerId(): Int

    @Query("SELECT COUNT(*) FROM Timer")
    fun getLiveDataNumberOfTimers(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM Timer WHERE status IN (1, 2, 3)")
    fun getLiveDataNumberOfActiveTimers(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM Timer WHERE status IN (1, 2, 3)")
    fun getNumberOfActiveTimers(): Int

    /*************************************************************
                            UPDATE CALLS
     *************************************************************/
    @Update
    fun updateTimer(timer: Timer)

    @Query("UPDATE Timer SET startTime = :time WHERE id = :id")
    fun updateTimerStartTime(id: Int, time: Long)

    @Query("UPDATE Timer SET pausedTime = :time WHERE id = :id")
    fun updateTimerPausedTime(id: Int, time: Long)

    @Query("UPDATE Timer SET status = :newStatus WHERE id = :id")
    fun updateTimerStatus(id: Int, newStatus: TimerStatus)

    @Query("UPDATE Timer SET setCounter = :newSet WHERE id = :id")
    fun updateTimerSetCounter(id: Int, newSet: Int)

    @Query("UPDATE Timer SET position = :newPosition WHERE id = :id")
    fun updateTimerPosition(id: Int, newPosition: Int)

    @Query("UPDATE Timer SET animationProgress = :progress WHERE id = :id")
    fun updateAnimationProgress(id: Int, progress: Long)

    @Query("UPDATE Timer SET showNotifications = :shouldShow WHERE id = :id")
    fun updateTimerShowNotification(id: Int, shouldShow: Boolean)

    @Query("UPDATE Timer SET status = 0")
    fun resetAllTimerStatuses()

    @Query("UPDATE Timer SET pausedTime = 0")
    fun resetAllTimerPausedTimes()

    @Query("UPDATE Timer SET setCounter = 1")
    fun resetAllTimerSetCounters()

    /*************************************************************
                            DELETE CALLS
     *************************************************************/
    @Delete
    fun deleteTimer(timer: Timer)

    @Query("DELETE FROM Timer WHERE id == :id")
    fun deleteTimerById(id: Int)
}