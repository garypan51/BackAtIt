package com.garypanapps.nextsetworkouttimer.core.entities

import android.os.SystemClock
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.garypanapps.nextsetworkouttimer.core.TimeConstants
import com.garypanapps.nextsetworkouttimer.core.TimerStatus

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
            val timeInSeconds = totalTimeInMilli / TimeConstants.SECOND_IN_MILLISECONDS
            val initTimes = getHoursMinsSecsFromTotalSeconds(timeInSeconds)
            val hours = initTimes[0]
            val minutes = initTimes[1]
            val seconds = initTimes[2]
            var milliSeconds = if(showMilli) Math.round((totalTimeInMilli % TimeConstants.SECOND_IN_MILLISECONDS) / 10.0).toInt() else 0

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
        initTimeString = getStringInitialTime()
    }
    fun changeTimerTime(position : Int, time : Int){
        if(position <= timerList.size){
            timerList[position] = time
        }
    }

    fun getCurrentTime(): Long {
        if(isInactive() || isCompleted()){
            return (timerList[0] * TimeConstants.SECOND_IN_MILLISECONDS).toLong()
        }

        if (startTime == 0L){
            startTime = SystemClock.elapsedRealtime()
        }

        var currentTime = 0L
        if(pausedTime == 0L){
            currentTime = startTime + (timerList[0] * TimeConstants.SECOND_IN_MILLISECONDS)
        }
        else {
            currentTime = startTime + (pausedTime)
        }
        return currentTime - SystemClock.elapsedRealtime()
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

