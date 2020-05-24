package com.garypanapps.nextsetworkouttimer.ui.fragments.stopwatches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.garypanapps.nextsetworkouttimer.R
import com.garypanapps.nextsetworkouttimer.ui.fragments.dialogs.BaseFragment

class HomeStopwatchesFragment : BaseFragment() {
    companion object {
        const val TITLE = "Stopwatch"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_stopwatches, container, false)
    }


}
