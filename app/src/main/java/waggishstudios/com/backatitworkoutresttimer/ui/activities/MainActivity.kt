package waggishstudios.com.backatitworkoutresttimer.ui.activities

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.ads.AdRequest

import waggishstudios.com.backatitworkoutresttimer.R
import waggishstudios.com.backatitworkoutresttimer.core.adapters.pagers.FragmentTabsAdapter

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragmentPager = FragmentTabsAdapter(supportFragmentManager)
        viewPager.adapter = homeFragmentPager
        viewPager.currentItem = 1

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
}
