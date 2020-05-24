package com.garypanapps.nextsetworkouttimer

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.garypanapps.nextsetworkouttimer.core.ServiceLocator

class App : MultiDexApplication() {
    var appContext : Context? = null
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
        appContext = applicationContext
    }
}