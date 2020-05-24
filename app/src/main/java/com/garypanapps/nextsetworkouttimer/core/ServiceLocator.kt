package com.garypanapps.nextsetworkouttimer.core

import android.content.Context
import com.garypanapps.nextsetworkouttimer.core.repositories.TimerRepository

object ServiceLocator {
    private lateinit var appContext : Context

    lateinit var TimerRepo : TimerRepository

    fun init(context : Context){
        appContext = context.applicationContext
        TimerRepo = TimerRepository(appContext)
    }
}