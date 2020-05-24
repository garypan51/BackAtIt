package com.garypanapps.nextsetworkouttimer.core.adapters.pagers

import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.garypanapps.nextsetworkouttimer.ui.fragments.settings.HomeSettingsFragment
import com.garypanapps.nextsetworkouttimer.ui.fragments.stopwatches.HomeStopwatchesFragment
import com.garypanapps.nextsetworkouttimer.ui.fragments.timers.HomeTimersFragment

class FragmentTabsAdapter(fragManager : FragmentManager) : FragmentPagerAdapter(fragManager) {
    companion object {
        const val NUMBER_OF_HOME_FRAGMENTS = 3
    }
    private var homeFragments = SparseArray<Fragment>()

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return HomeStopwatchesFragment()
            1 -> return HomeTimersFragment()
            2 -> return HomeSettingsFragment()
        }
        return HomeTimersFragment()
    }

    override fun getCount(): Int {
        return NUMBER_OF_HOME_FRAGMENTS
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position)
        homeFragments.put(position, fragment as Fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        homeFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0 -> return HomeStopwatchesFragment.TITLE
            1 -> return HomeTimersFragment.TITLE
            2 -> return HomeSettingsFragment.TITLE
        }
        return HomeTimersFragment.TITLE
    }

    fun getHomeFragment(position: Int): Fragment {
        return homeFragments.get(position)
    }
}