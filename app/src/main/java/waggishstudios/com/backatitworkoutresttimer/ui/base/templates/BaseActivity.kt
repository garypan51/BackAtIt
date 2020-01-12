package waggishstudios.com.backatitworkoutresttimer.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import waggishstudios.com.backatitworkoutresttimer.core.ServiceLocator

abstract class BaseActivity : AppCompatActivity() {
    private val TIME_INTERVAL = 2000
    private var mBackPressed: Long = 0
    private val parentJob = Job()
    private val scope = CoroutineScope(Dispatchers.Main + parentJob)

    override fun onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            this.finish()
            scope.launch(Dispatchers.IO) {
                ServiceLocator.TimerRepo.resetAllTimers()
            }
            return
        }
        else {
            Toast.makeText(baseContext, "Tap BACK Button Again To Shutdown", Toast.LENGTH_SHORT).show()
        }

        mBackPressed = System.currentTimeMillis()
    }
}
