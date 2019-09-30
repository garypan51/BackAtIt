package waggishstudios.com.backatitworkoutresttimer

import android.content.Context
import androidx.multidex.MultiDexApplication
import waggishstudios.com.backatitworkoutresttimer.core.ServiceLocator

class App : MultiDexApplication() {
    var appContext : Context? = null
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
        appContext = applicationContext
    }
}