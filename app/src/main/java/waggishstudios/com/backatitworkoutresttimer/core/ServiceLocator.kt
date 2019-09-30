package waggishstudios.com.backatitworkoutresttimer.core

import android.content.Context
import waggishstudios.com.backatitworkoutresttimer.core.repositories.TimerRepository

object ServiceLocator {
    private lateinit var appContext : Context

    lateinit var TimerRepo : TimerRepository

    fun init(context : Context){
        appContext = context.applicationContext
        TimerRepo = TimerRepository(appContext)
    }
}