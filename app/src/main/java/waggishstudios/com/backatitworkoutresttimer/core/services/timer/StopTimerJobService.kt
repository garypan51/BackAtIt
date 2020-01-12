package waggishstudios.com.backatitworkoutresttimer.core.services.timer

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import android.content.Intent

class StopTimerJobService : JobService() {
    override fun onStartJob(params: JobParameters): Boolean {
        applicationContext.stopService(Intent(applicationContext, TimerService::class.java))
        return false
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return false
    }
}