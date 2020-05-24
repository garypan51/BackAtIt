package com.garypanapps.nextsetworkouttimer.core

class TimeConstants {
    companion object {
        const val HOUR_IN_SECONDS = 3600
        const val MINUTE_IN_SECONDS = 60
        const val SECOND_IN_MILLISECONDS = 1000
    }
}

class IntentConstants {
    class TimerService {
        companion object {
            const val STOP_SERVICE = "StopTimerService"
            const val RESTART_TIMER = "RestartTimer"
            const val ALERT_TIMER_RESTART = "AlertActivityTimerRestarted"
            const val START_SERVICE_TIMEOUT = "StartServiceTimeout"
            const val CANCEL_SERVICE_TIMEOUT = "CancelServiceTimeout"
        }
    }
}