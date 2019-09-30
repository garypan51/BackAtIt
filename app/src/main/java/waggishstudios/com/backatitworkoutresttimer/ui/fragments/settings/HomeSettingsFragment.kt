package waggishstudios.com.backatitworkoutresttimer.ui.fragments.settings


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import waggishstudios.com.backatitworkoutresttimer.R
class HomeSettingsFragment : Fragment() {
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
