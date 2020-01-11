package waggishstudios.com.backatitworkoutresttimer.ui.fragments.timers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home_timers.*
import kotlinx.coroutines.*

import waggishstudios.com.backatitworkoutresttimer.R
import waggishstudios.com.backatitworkoutresttimer.core.IntentConstants
import waggishstudios.com.backatitworkoutresttimer.core.ServiceLocator
import waggishstudios.com.backatitworkoutresttimer.core.adapters.recyclers.DragManageAdapter
import waggishstudios.com.backatitworkoutresttimer.core.adapters.recyclers.TimerRecyclerAdapter
import waggishstudios.com.backatitworkoutresttimer.core.services.TimerService
import waggishstudios.com.backatitworkoutresttimer.ui.fragments.dialogs.AddTimerDialogFragment
import waggishstudios.com.backatitworkoutresttimer.ui.fragments.dialogs.EditTimerDialogFragment
import waggishstudios.com.backatitworkoutresttimer.ui.utils.getViewModel

class HomeTimersFragment : Fragment(), AddTimerDialogFragment.AddTimerDialogListener,
    EditTimerDialogFragment.EditTimerDialogListener, DragManageAdapter.OnStartDragListener {
    companion object {
        const val TITLE = "Timers"
    }

    var openedAfterNotificationRestart = false
    private val activeTimers = ServiceLocator.TimerRepo.numberOfActiveTimers
    private val totalTimers = ServiceLocator.TimerRepo.numberOfTimers
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val viewModel : HomeTimersViewModel by lazy {
        getViewModel { HomeTimersViewModel() }
    }

    private lateinit var localBroadcastManager : LocalBroadcastManager

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                IntentConstants.TimerService.ALERT_TIMER_RESTART -> {
                    Log.d("testingStatus", "NotifyDataSetChanged")
                    if(!isResumed) {
                        openedAfterNotificationRestart = true
                    }
                    timersAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private lateinit var timersAdapter : TimerRecyclerAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onFinishAddDialog(name: String, timerList : ArrayList<Int>, shouldAutoInc : Boolean, autoReset: Int) {
        // Adds Timer to DB and RecyclerView
        Log.d("testingtype", "onfinishedadddialog")
        viewModel.addTimer(name, timerList, shouldAutoInc, autoReset, timersAdapter)
    }

    override fun onFinishEditDialog(id: Int, name: String, timerList: ArrayList<Int>, shouldAutoInc: Boolean,
                                    autoReset: Int, shouldDelete: Boolean) {
        if (shouldDelete) {
            timersAdapter.removeTimerById(id)
            return
        }
        timersAdapter.editTimerById(id, name, timerList, shouldAutoInc, autoReset)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_timers, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        timersAdapter = TimerRecyclerAdapter(context!!, this)
        itemTouchHelper = ItemTouchHelper(DragManageAdapter(timersAdapter, ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),
            ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)))
        viewModel.displayUserTimers(timersAdapter)
        setUpObservers()
        setUpViews()
    }

    override fun onResume() {
        timersAdapter.notifyDataSetChanged()
        Log.d("testingLife", "HomeTimerList onResume")
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        localBroadcastManager = LocalBroadcastManager.getInstance(activity?.applicationContext!!)
        val intentFilter = IntentFilter()
        intentFilter.addAction(IntentConstants.TimerService.ALERT_TIMER_RESTART)
        activity?.applicationContext?.registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onStop() {
        localBroadcastManager.unregisterReceiver(broadcastReceiver)
        super.onStop()
    }

    override fun onDestroy() {
        Log.d("testingLife","onDestroy")
        super.onDestroy()
    }

    private fun setUpObservers(){
        activeTimers.observe(this, Observer { tvActiveTimers.text = this.getString(R.string.activeTimers, it) })
        totalTimers.observe(this, Observer { tvTotalTimers.text = this.getString(R.string.totalTimers, it) })
    }

    private fun setUpViews(){
        setUpRecyclerView()
        fabAddTimer.setOnClickListener {
            val addTimerDialogFragment = AddTimerDialogFragment()
            addTimerDialogFragment.setTargetFragment(this, 300)
            addTimerDialogFragment.show(fragmentManager!!, "addTimerDialog")
        }
    }

    private fun setUpRecyclerView(){
        rvTimers.apply {
            setHasFixedSize(true)
            adapter = timersAdapter
            layoutManager = LinearLayoutManager(activity)
            recycledViewPool.setMaxRecycledViews(0, 0)
        }
        itemTouchHelper.attachToRecyclerView(rvTimers)
    }

    override fun onStartDrag(viewHolder : RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
    }

    override fun onPause() {
        startTimerService()
        super.onPause()
    }

    private fun startTimerService(){
        val timerServiceIntent = Intent(context, TimerService::class.java)
        activity?.applicationContext?.startService(timerServiceIntent)
    }
}