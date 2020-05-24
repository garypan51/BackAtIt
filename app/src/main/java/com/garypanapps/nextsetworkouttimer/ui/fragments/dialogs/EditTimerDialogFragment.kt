package com.garypanapps.nextsetworkouttimer.ui.fragments.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_fragment_timer.*

import com.garypanapps.nextsetworkouttimer.R
import com.garypanapps.nextsetworkouttimer.core.TimeConstants

class EditTimerDialogFragment : DialogFragment() {
    private var timerId = 0
    private var timerName = ""
    private var timerHours = 0
    private var timerMinutes = 0
    private var timerSeconds = 0
    private var timerAutoInc = true
    private var timerAutoReset = 0
    private var shouldDelete = false

    companion object {
        fun newInstance(id: Int, name: String, hr: Int, min: Int, sec: Int, autoInc: Boolean, autoReset: Int):
                EditTimerDialogFragment {
            val fragment = EditTimerDialogFragment()
            val args = Bundle()
            args.putInt("id", id)
            args.putString("name", name)
            args.putInt("hr", hr)
            args.putInt("min", min)
            args.putInt("sec", sec)
            args.putBoolean("autoInc", autoInc)
            args.putInt("autoReset", autoReset)
            fragment.arguments = args
            return fragment
        }
    }

    interface EditTimerDialogListener{
        fun onFinishEditDialog(id: Int, name: String, timerList : ArrayList<Int>, shouldAutoInc : Boolean, autoReset: Int, shouldDelete: Boolean)
    }

    private fun sendBackDialogData(){
        val listener = targetFragment as EditTimerDialogListener
        if(shouldDelete) {
            listener.onFinishEditDialog(timerId, timerName, ArrayList(), timerAutoInc, timerAutoReset, true)
            dismiss()
        }
        val hours = npHours.value
        val minutes = npMinutes.value
        val seconds = npSeconds.value * 5
        val totalTimeInSeconds = (hours * TimeConstants.HOUR_IN_SECONDS) + (minutes * TimeConstants.MINUTE_IN_SECONDS) + seconds
        val shouldAutoInc = switchAutoInc.isChecked
        val autoResetCount = etAutoResetSets.text.toString().toIntOrNull() ?: 0

        if(hours == 0 && minutes == 0 && seconds == 0){
            dismiss()
            return
        }

        val name = tiTimerName.editText?.text.toString()
        val timerList = ArrayList<Int>()
        timerList.add(totalTimeInSeconds)

        listener.onFinishEditDialog(timerId, name, timerList, shouldAutoInc, autoResetCount, false)
        dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.let {
            timerId = it.getInt("id")
            timerName = it.getString("name") ?: ""
            timerHours = it.getInt("hr")
            timerMinutes = it.getInt("min")
            timerSeconds = it.getInt("sec") / 5
            timerAutoInc = it.getBoolean("autoInc")
            timerAutoReset = it.getInt("autoReset")
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.dialog_fragment_timer, container, false)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpViews()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    private fun setUpViews(){
//        throw RuntimeException("Test")
        tvDialogTitle.text = getString(R.string.editTimerTitle)
        /*
            Make seconds only display intervals of 5 seconds.
         */
        val possibleSecs = arrayOf("0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55")
        npSeconds.displayedValues = null
        npSeconds.minValue = 0
        npSeconds.maxValue = 11
        npSeconds.displayedValues = possibleSecs

        /*
            Set Hours, Minutes and Seconds to match the current timer
         */
        npHours.value = timerHours
        npMinutes.value = timerMinutes
        npSeconds .value = timerSeconds


        /*
            Set Auto Increment toggle and Auto Reset
         */
        if(!timerAutoInc) {
            switchAutoInc.isChecked = false
        }

        if(timerAutoReset != 0){
            etAutoResetSets.setText(timerAutoReset.toString())
        }

        /*
            Set Name EditText Field and Buttons
         */
        tiTimerName.editText?.setText(timerName)
        btnDelete.visibility = View.VISIBLE
        btnDelete.setOnClickListener {
            shouldDelete = true
            sendBackDialogData()
        }

        btnOk.setOnClickListener { sendBackDialogData() }
        btnCancel.setOnClickListener { dismiss() }
    }
}