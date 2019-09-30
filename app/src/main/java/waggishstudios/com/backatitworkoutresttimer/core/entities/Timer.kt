package waggishstudios.com.backatitworkoutresttimer.core.entities

import android.os.SystemClock
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import waggishstudios.com.backatitworkoutresttimer.core.TimeConstants
import waggishstudios.com.backatitworkoutresttimer.core.TimerStatus

@Entity
data class Timer(
    var name: String,
    var timerList : ArrayList<Int>,
    var shouldAutoInc : Boolean,
    var autoReset : Int,
    var position: Int) {

    companion object {
        fun timeHasHours(timeInSeconds: Int): Boolean{
            return getHoursMinsSecsFromTotalSeconds(timeInSeconds)[0] != 0
        }

        fun timeHasMinutes(timeInSeconds: Int): Boolean{
            return getHoursMinsSecsFromTotalSeconds(timeInSeconds)[1] != 0
        }

        fun getStringTime(totalTimeInMilli: Int, showMilli: Boolean): String {
//            Log.i("testingStringTime", "calling getStringTime: " + totalSecs)
            val timeInSeconds = totalTimeInMilli / TimeConstants.SECOND_IN_MILLISECONDS
            val initTimes = getHoursMinsSecsFromTotalSeconds(timeInSeconds)
            val hours = initTimes[0]
            val minutes = initTimes[1]
            val seconds = initTimes[2]
            var milliSeconds = if(showMilli) Math.round((totalTimeInMilli % TimeConstants.SECOND_IN_MILLISECONDS) / 10.0).toInt() else 0
            // Removes the jank.
            if(milliSeconds > 99) {
                milliSeconds = 99
            }

            if(timeHasHours(timeInSeconds)){
                if(showMilli){
                    return ("%2dh : %2dm\n%02ds : %02dms".format(hours, minutes, seconds, milliSeconds))
                }
                return ("%2dh : %2dm\n%02ds".format(hours, minutes, seconds))
            }

            else if(timeHasMinutes(timeInSeconds)){
                if(showMilli){
                    return ("%2dm : %02ds\n%02dms".format(minutes, seconds, milliSeconds))
                }
                return ("%2dm : %02ds".format(minutes, seconds))
            }

            if(showMilli){
                return ("%2ds\n%02dms".format(seconds, milliSeconds))
            }
            return ("%2ds".format(seconds))
        }

        fun getHoursMinsSecsFromTotalSeconds(totalSeconds: Int): ArrayList<Int> {
            val listOfTime = ArrayList<Int>()
            val hours = totalSeconds / TimeConstants.HOUR_IN_SECONDS
            listOfTime.add(hours)

            val leftOver = totalSeconds % TimeConstants.HOUR_IN_SECONDS
            val minutes = leftOver / TimeConstants.MINUTE_IN_SECONDS
            listOfTime.add(minutes)

            val seconds = totalSeconds % TimeConstants.MINUTE_IN_SECONDS
            listOfTime.add(seconds)
            return listOfTime
        }
    }

    @PrimaryKey(autoGenerate = true)
    var id = 0

    var status = TimerStatus.INACTIVE
    var setCounter = 1
    var startTime = 0L
    var pausedTime = 0L
    var animationProgress = 0L
    var showNotifications = true

    var initHours : Int = 0
    var initMinutes : Int = 0
    var initSeconds : Int = 0
    var initTimeString : String = ""

    init{
        setUpInitialTime()
    }

    private fun getStringInitialTime() : String {
        return getStringTime(timerList[0] * TimeConstants.SECOND_IN_MILLISECONDS, false)
    }

    fun setUpInitialTime(){
        var totaltime = 0
        if(timerList.size != 0){
            totaltime = timerList[0]
        }
        val initTimes = getHoursMinsSecsFromTotalSeconds(totaltime)
        initHours = initTimes[0]
        initMinutes = initTimes[1]
        initSeconds = initTimes[2]

        Log.i("testingStringTime", "initHours: " + initHours.toString())
        Log.i("testingStringTime", "initMins: " + initMinutes.toString())
        Log.i("testingStringTime", "initSeconds: " + initSeconds.toString())

        initTimeString = getStringInitialTime()
    }
    fun changeTimerTime(position : Int, time : Int){
        if(position <= timerList.size){
            timerList[position] = time
        }
    }

    fun getCurrentTime(): Long {
        if(isInactive() || isCompleted()){
//            Log.d("testingStatus", "inactive or complete in getCurrentTime")
            return (timerList[0] * TimeConstants.SECOND_IN_MILLISECONDS).toLong()
        }

        if (startTime == 0L){
            Log.d("testingTime", "setting start")
            startTime = SystemClock.elapsedRealtime()
//            pausedTime = 0L
        }

        var currentTime = 0L
        if(pausedTime == 0L){
            currentTime = startTime + (timerList[0] * TimeConstants.SECOND_IN_MILLISECONDS)
//            Log.d("testingTime", "not paused currentTime: " + currentTime)
        }
        else {
            currentTime = startTime + (pausedTime)
//            Log.d("testingTime", "was paused currentTime: " + currentTime)
        }
//        Log.d("testingStatus", "currentTime: " + (currentTime - SystemClock.elapsedRealtime()))
        return currentTime - SystemClock.elapsedRealtime()
//        return SystemClock.elapsedRealtime() + (timerList[0] * TimeConstants.SECOND_IN_MILLISECONDS)
//        return if(pausedTime == 0L) SystemClock.elapsedRealtime() + (timerList[0] * TimeConstants.SECOND_IN_MILLISECONDS)
//        else SystemClock.elapsedRealtime() + (pausedTime * TimeConstants.SECOND_IN_MILLISECONDS)
    }

    fun getCurrentTimeForDisplay(showMilli: Boolean): String {
        return getStringTime(getCurrentTime().toInt(), showMilli)
    }

    fun getPausedTimeForDisplay(showMilli: Boolean): String {
        return getStringTime(pausedTime.toInt(), showMilli)
    }

    fun getInitTotalTime(): Int {
        return timerList[0]
    }

    fun isRunning(): Boolean {
        return status == TimerStatus.RUNNING
    }

    fun isInactive(): Boolean {
        return status == TimerStatus.INACTIVE
    }

    fun isCompleted(): Boolean {
        return status == TimerStatus.COMPLETED
    }
}

