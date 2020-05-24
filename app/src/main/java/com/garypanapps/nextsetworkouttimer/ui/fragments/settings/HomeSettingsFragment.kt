package com.garypanapps.nextsetworkouttimer.ui.fragments.settings


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.garypanapps.nextsetworkouttimer.R
import com.garypanapps.nextsetworkouttimer.ui.fragments.dialogs.BaseFragment

class HomeSettingsFragment : BaseFragment() {
    companion object {
        const val TITLE = "Settings"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
}
