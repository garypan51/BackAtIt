package waggishstudios.com.backatitworkoutresttimer.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import waggishstudios.com.backatitworkoutresttimer.R
import waggishstudios.com.backatitworkoutresttimer.core.adapters.pagers.FragmentTabsAdapter
import com.google.android.gms.ads.AdRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import waggishstudios.com.backatitworkoutresttimer.core.ServiceLocator


class MainActivity : AppCompatActivity() {
    private val TIME_INTERVAL = 2000
//    private val INTERSTITIAL_FREQ = 30
    private var mBackPressed: Long = 0
    private val parentJob = Job()
    private val scope = CoroutineScope(Dispatchers.Main + parentJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragmentPager = FragmentTabsAdapter(supportFragmentManager)
        viewPager.adapter = homeFragmentPager
        viewPager.currentItem = 1

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

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

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // todo check if any running timers, if so then start job
        // intent with sendstartmessage
        // applicationContext.sendBroadcast(intent)
    }
}
