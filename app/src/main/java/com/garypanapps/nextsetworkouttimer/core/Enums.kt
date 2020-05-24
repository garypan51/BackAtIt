package com.garypanapps.nextsetworkouttimer.core

enum class TimerStatus(val status : Int) {
    INACTIVE(0),
    RUNNING(1),
    PAUSED(2),
    COMPLETED(3);

    companion object {
        fun getStatusByInt(integer: Int): TimerStatus{
            return when(integer){
                1 -> RUNNING
                2 -> PAUSED
                3 -> COMPLETED
                else -> INACTIVE
            }
        }
    }
}