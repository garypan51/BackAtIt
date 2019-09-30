package waggishstudios.com.backatitworkoutresttimer.ui.fragments.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_fragment_timer.*

import waggishstudios.com.backatitworkoutresttimer.R
import waggishstudios.com.backatitworkoutresttimer.core.TimeConstants

class AddTimerDialogFragment : DialogFragment() {
    interface AddTimerDialogListener{
        fun onFinishAddDialog(name: String, timerList : ArrayList<Int>, shouldAutoInc : Boolean, autoReset: Int)
    }

    private fun sendBackDialogData(){
        val listener = targetFragment as AddTimerDialogListener
        val hours = npHours.value
        val minutes = npMinutes.value
        val seconds = npSeconds.value * 5
        val totalTimeInSeconds = (hours * TimeConstants.HOUR_IN_SECONDS) + (minutes * TimeConstants.MINUTE_IN_SECONDS) + seconds
        val shouldAutoInc = switchAutoInc.isChecked
        val autoResetValue = etAutoResetSets.text.toString().toIntOrNull()
        val autoResetCount = if(autoResetValue != null && autoResetValue != 0) autoResetValue else 0

        if(hours == 0 && minutes == 0 && seconds == 0){
            dismiss()
            return
        }

        val name = tiTimerName.editText?.text.toString()
        val timerList = ArrayList<Int>()
        timerList.add(totalTimeInSeconds)

        listener.onFinishAddDialog(name, timerList, shouldAutoInc, autoResetCount)
        dismiss()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_fragment_timer, container, false)
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
        /*
            Make seconds only display intervals of 5 seconds.
         */
        val possibleSecs = arrayOf("0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55")
        npSeconds.displayedValues = null
        npSeconds.minValue = 0
        npSeconds.maxValue = 11
        npSeconds.displayedValues = possibleSecs

        btnOk.setOnClickListener { sendBackDialogData() }

        btnCancel.setOnClickListener { dismiss() }
    }
}