package com.garypanapps.nextsetworkouttimer.core.adapters.recyclers

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.robinhood.ticker.TickerUtils
import kotlinx.android.synthetic.main.cardview_timer.view.*
import kotlinx.coroutines.*
import com.garypanapps.nextsetworkouttimer.R
import com.garypanapps.nextsetworkouttimer.core.TimerStatus
import com.garypanapps.nextsetworkouttimer.core.entities.Timer
import com.garypanapps.nextsetworkouttimer.core.ServiceLocator
import com.garypanapps.nextsetworkouttimer.core.TimeConstants
import com.garypanapps.nextsetworkouttimer.core.services.timer.TimerService
import com.garypanapps.nextsetworkouttimer.ui.fragments.dialogs.EditTimerDialogFragment
import com.garypanapps.nextsetworkouttimer.ui.fragments.timers.HomeTimersFragment
import java.util.*

class TimerRecyclerAdapter(private val context : Context, private val lifeCycle : LifecycleOwner) : RecyclerView.Adapter<TimerRecyclerAdapter.TimerViewHolder>() {
    inner class TimerViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val timerCV = view.cardView
        val timerNameTV = view.tvTimerName
        val timerTVHours = view.tvTimerHours
        val timerTVMinutes = view.tvTimerMinutes
        val timerTVSeconds = view.tvTimerSeconds
        val timerTVHoursTitle = view.tvTimerHoursTitle
        val timerTVMinutesTitle = view.tvTimerMinutesTitle
        val timerTVHoursSepartor = view.tvTimerHoursSepartor
        val timerTVMinutesTitleSepartor = view.tvTimerMinutesSepartor
        val setCounterTV = view.tvSetCounter
        val startButton = view.btnStart
        val pauseButton = view.btnPause
        val resetButton = view.btnReset
        val incSetButton = view.btnIncSet
        val decSetButton = view.btnDecSet
        val dragButton = view.btnDrag
        val timerMenu = view.btnTimerMenu
    }

    private val parentJob = Job()
    private val scope = CoroutineScope(Dispatchers.Main + parentJob)

    private val timersList = mutableListOf<Timer>()
    private lateinit var blinkingAnimation: Animation

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        blinkingAnimation = AlphaAnimation(0.0f, 1.0f)
        blinkingAnimation.duration = 50
        blinkingAnimation.startOffset = 450
        blinkingAnimation.repeatMode = Animation.REVERSE
        blinkingAnimation.repeatCount = Animation.INFINITE
        return TimerViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview_timer, parent, false))
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        val timerStatus = MutableLiveData<TimerStatus>()
        val timerCurrentTime = MutableLiveData<Handler>()
        val timerCurrentSet = MutableLiveData<Int>()
        val timeDisplayHandler = Handler()

        holder.timerTVHours.setCharacterLists(TickerUtils.provideNumberList())
        holder.timerTVMinutes.setCharacterLists(TickerUtils.provideNumberList())
        holder.timerTVSeconds.setCharacterLists(TickerUtils.provideNumberList())

        scope.launch(Dispatchers.IO) {
            val timer = if (!(lifeCycle as HomeTimersFragment).openedAfterNotificationRestart) {
                            ServiceLocator.TimerRepo.getTimerById(timersList[position].id) }
                        else { timersList[position] } ?: timersList[position]

            withContext(Dispatchers.Main) {
                setUpInitialTimerText(timer, holder)
                setUpObservers(timer, holder, timeDisplayHandler, timerCurrentTime, timerStatus, timerCurrentSet)
                setUpViews(timer, holder, timeDisplayHandler, timerCurrentTime, timerStatus, timerCurrentSet)
                timerStatus.value = timer.status
            }
        }
    }

    private fun checkAndHideHoursAndMinutes(currentHours: String, currentMinutes: String, holder: TimerViewHolder) {
        if(currentHours == "0") {
            holder.timerTVHours.visibility = View.GONE
            holder.timerTVHoursTitle.visibility = View.GONE
            holder.timerTVHoursSepartor.visibility = View.GONE
        }
    }

    private fun showHoursAndMinutes(holder: TimerViewHolder) {
        holder.timerTVHours.visibility = View.VISIBLE
        holder.timerTVHoursTitle.visibility = View.VISIBLE
        holder.timerTVHoursSepartor.visibility = View.VISIBLE
        holder.timerTVMinutes.visibility = View.VISIBLE
        holder.timerTVMinutesTitle.visibility = View.VISIBLE
        holder.timerTVMinutesTitleSepartor.visibility = View.VISIBLE
    }

    private fun setUpInitialTimerText(timer: Timer, holder: TimerViewHolder){
        holder.timerNameTV.text = timer.name
        holder.timerTVHours.text = timer.initHours.toString()
        holder.timerTVMinutes.text = timer.initMinutes.toString()
        holder.timerTVSeconds.text = String.format("%02d", timer.initSeconds)
        checkAndHideHoursAndMinutes(holder.timerTVHours.text, holder.timerTVMinutes.text, holder)
        holder.setCounterTV.text = context.getString(R.string.setCount, timer.setCounter)
    }


    private fun setUpObservers(timer: Timer, holder : TimerViewHolder, timeHandler: Handler, currentTime : MutableLiveData<Handler>,
                               status : MutableLiveData<TimerStatus>, currentSet: MutableLiveData<Int>){

        status.observe(lifeCycle, Observer { it ->
            when(it){
                TimerStatus.INACTIVE -> {
                    holder.startButton.visibility = View.VISIBLE
                    holder.pauseButton.visibility = View.GONE
                    holder.resetButton.visibility = View.GONE
                    holder.timerTVHours.clearAnimation()
                    holder.timerTVMinutes.clearAnimation()
                    holder.timerTVSeconds.clearAnimation()
                    holder.timerCV.setCardBackgroundColor(ContextCompat.getColor(context, R.color.spaceGray))
                }

                TimerStatus.RUNNING -> {
                    holder.startButton.visibility = View.INVISIBLE
                    holder.pauseButton.visibility = View.VISIBLE
                    holder.resetButton.visibility = View.VISIBLE
                    holder.timerTVHours.clearAnimation()
                    holder.timerTVMinutes.clearAnimation()
                    holder.timerTVSeconds.clearAnimation()
                    holder.timerCV.setCardBackgroundColor(ContextCompat.getColor(context, R.color.greenTimerCard))
                    timeHandler.post{ currentTime.value = timeHandler }
                }

                TimerStatus.PAUSED -> {
                    holder.startButton.visibility = View.VISIBLE
                    holder.pauseButton.visibility = View.INVISIBLE
                    holder.resetButton.visibility = View.VISIBLE
                    holder.timerCV.setCardBackgroundColor(ContextCompat.getColor(context, R.color.yellowTimerCard))
                    holder.timerTVHours.text = Timer.getStringTime(timer.pausedTime.toInt(), true)
                }

                TimerStatus.COMPLETED -> {
                    holder.startButton.visibility = View.VISIBLE
                    holder.pauseButton.visibility = View.INVISIBLE
                    holder.resetButton.visibility = View.VISIBLE
                    if(timer.initHours != 0) {
                        holder.timerTVHours.startAnimation(blinkingAnimation)
                        showHoursAndMinutes(holder)
                    }
                    if(timer.initMinutes != 0) {
                        holder.timerTVMinutes.startAnimation(blinkingAnimation)
                        showHoursAndMinutes(holder)
                    }
                    holder.timerTVSeconds.startAnimation(blinkingAnimation)
                    checkAndHideHoursAndMinutes(timer.initHours.toString(), timer.initMinutes.toString(), holder)
                    holder.timerCV.setCardBackgroundColor(ContextCompat.getColor(context, R.color.redTimerCard))
                    holder.timerTVHours.text = timer.initHours.toString()
                    holder.timerTVMinutes.text = timer.initMinutes.toString()
                    holder.timerTVSeconds.text = timer.initSeconds.toString()
                }

                else -> {}
            }
        })

        currentTime.observe(lifeCycle, Observer {
            if (timer.getCurrentTime() > 0L) {
                val currentTimeInSeconds = (timer.getCurrentTime() / TimeConstants.SECOND_IN_MILLISECONDS).toInt()
                val currentHourMinutesSeconds = Timer.getHoursMinsSecsFromTotalSeconds(currentTimeInSeconds)
                val currentHours = currentHourMinutesSeconds[0].toString()
                val currentMinutes = currentHourMinutesSeconds[1].toString()
                val currentSeconds = currentHourMinutesSeconds[2].toString()

                holder.timerTVHours.text = currentHours
                holder.timerTVMinutes.text = currentMinutes
                holder.timerTVSeconds.text = currentSeconds

                checkAndHideHoursAndMinutes(currentHours, currentMinutes, holder)

                timeHandler.post{ currentTime.value = it }
            } else {
                timeHandler.removeMessages(0)
                val homeTimersFragment = lifeCycle as HomeTimersFragment
                if(!((homeTimersFragment).openedAfterNotificationRestart)) {
                    updateTimerStatus(timer, TimerStatus.COMPLETED, status)
                } else {
                    (homeTimersFragment).openedAfterNotificationRestart = false
                }
            }
        })

        currentSet.observe(lifeCycle, Observer {
            holder.setCounterTV.text = context.getString(R.string.setCount, it)
            if(it > 1){
                holder.resetButton.visibility = View.VISIBLE
            }
            else if (it == 1 && status.value == TimerStatus.INACTIVE){
                holder.resetButton.visibility = View.GONE
            }
        })
    }

    private fun setUpViews(timer: Timer, holder : TimerViewHolder, handler: Handler, currentTime: MutableLiveData<Handler>,
                           status: MutableLiveData<TimerStatus>, currentSet: MutableLiveData<Int>){
        // Start button on click
        holder.startButton.setOnClickListener {
            startTimer(timer, holder, handler, currentTime, status, currentSet)
        }

        // Pause button on click
        holder.pauseButton.setOnClickListener {
            pauseTimer(timer, handler, status)
        }

        // Reset button on click
        holder.resetButton.setOnClickListener {
            resetTimer(timer, holder, handler, status, currentSet)
        }

        // Increment Set Counter onClick
        holder.incSetButton.setOnClickListener {
            incrementSetCounter(timer, currentSet)
        }

        // Decrement Set Counter onClick
        holder.decSetButton.setOnClickListener {
            decrementSetCounter(timer, currentSet)

        }

        // Timer Menu
        holder.timerMenu.setOnClickListener { view -> showPopup(view, timer) }

        holder.dragButton.setOnTouchListener { _, _ ->
            (lifeCycle as HomeTimersFragment).onStartDrag(holder)
            true
        }
    }

    private fun showPopup(viewToAnchorTo: View, timer: Timer) {
        val popup = PopupMenu(context, viewToAnchorTo)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.timer_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.edit -> {
                    showEditTimerDialog(timer)
                    true
                }
                R.id.delete -> {
                    removeTimerById(timer.id ?: 0)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun startTimer(timer: Timer, holder: TimerViewHolder, handler: Handler, currentTime: MutableLiveData<Handler>,
                           status: MutableLiveData<TimerStatus>, currentSet: MutableLiveData<Int>){
        startTimerService()
        if((timer.isInactive() || timer.isCompleted()) && timer.shouldAutoInc){
            timer.setCounter += 1
            updateTimerSetCounter(timer, currentSet)
        }
        updateTimerStatus(timer, TimerStatus.RUNNING, status)
        updateTimerStartTime(timer, false)
        currentTime.value = handler
    }

    private fun pauseTimer(timer: Timer, handler: Handler, status: MutableLiveData<TimerStatus>){
        handler.removeMessages(0)
        updateTimerPausedTime(timer, false)
        updateTimerStatus(timer, TimerStatus.PAUSED, status)
    }

    private fun resetTimer(timer: Timer, holder: TimerViewHolder, handler: Handler, status: MutableLiveData<TimerStatus>,
                           currentSet: MutableLiveData<Int>){
        handler.removeMessages(0)
        updateTimerStartTime(timer, true)
        updateTimerPausedTime(timer, true)
        updateTimerStatus(timer, TimerStatus.INACTIVE, status)
        timer.setCounter = 1
        updateTimerSetCounter(timer, currentSet)
        setUpInitialTimerText(timer, holder)
        startTimerService()
    }

    private fun incrementSetCounter(timer: Timer, currentSet: MutableLiveData<Int>) {
        timer.setCounter += 1
        updateTimerSetCounter(timer, currentSet)
    }

    private fun decrementSetCounter(timer: Timer, currentSet: MutableLiveData<Int>) {
        if(timer.setCounter == 1){
            return
        }
        timer.setCounter -= 1
        updateTimerSetCounter(timer, currentSet)
    }

    private fun updateTimerStartTime(timer: Timer, shouldSetZero: Boolean = false){
        val sTime = if(shouldSetZero) 0L else SystemClock.elapsedRealtime()
        timer.startTime = sTime
        val timerID = timer.id ?: 0
        scope.launch(Dispatchers.IO) { ServiceLocator.TimerRepo.updateTimerStartTime(timerID, timer.startTime) }
    }

    private fun updateTimerPausedTime(timer: Timer, shouldSetZero: Boolean = false){
        val pausedTime = if(shouldSetZero) 0L else timer.getCurrentTime()
        timer.pausedTime = pausedTime
        val timerID = timer.id ?: 0
        scope.launch(Dispatchers.IO) { ServiceLocator.TimerRepo.updateTimerPausedTime(timerID, pausedTime) }
    }

    private fun updateTimerStatus(timer: Timer, newStatus: TimerStatus, status: MutableLiveData<TimerStatus>){
        status.value = newStatus
        timer.status = newStatus
        scope.launch(Dispatchers.IO) { ServiceLocator.TimerRepo.updateTimerStatus(timer.id, newStatus) }
    }

    private fun updateTimerSetCounter(timer: Timer, currentSet: MutableLiveData<Int>){
        if(timer.autoReset != 0 && timer.setCounter > timer.autoReset) {
            timer.setCounter = 1
        }
        currentSet.value = timer.setCounter
        scope.launch(Dispatchers.IO) { ServiceLocator.TimerRepo.updateTimerSetCounter(timer.id, timer.setCounter) }
    }

    private fun updateAnimationProgress(timer: Timer, newProgress: Long){
        timer.animationProgress = newProgress
    }

    private fun showEditTimerDialog(timer: Timer) {
        val fragment = EditTimerDialogFragment.newInstance(timer.id!!, timer.name, timer.initHours, timer.initMinutes,
            timer.initSeconds, timer.shouldAutoInc, timer.autoReset)
        fragment.setTargetFragment(lifeCycle as Fragment, 500)
        fragment.show(lifeCycle.fragmentManager!!, "editTimerDialog")
    }

    override fun getItemCount(): Int {
        return timersList.size
    }

    fun addTimer(timer: Timer){
        timersList.add(timer)
    }

    fun addAllTimers(timersToAdd: List<Timer>){
        timersList.addAll(timersToAdd)
    }

    fun removeTimerById(id: Int){
        CoroutineScope(Dispatchers.IO).launch { ServiceLocator.TimerRepo.deleteTimerById(id) }
        val index = findTimerIdIndex(id)
        index?.let { timersList.removeAt(index) }
        notifyDataSetChanged()
    }

    fun editTimerById(id: Int, newName: String, newTimerList: ArrayList<Int>, newShouldAutoInc: Boolean, newAutoReset: Int){
        findTimerIdIndex(id)?.let {
            val timerToEdit = timersList[it]
            timerToEdit.name = newName
            timerToEdit.timerList = newTimerList
            timerToEdit.shouldAutoInc = newShouldAutoInc
            timerToEdit.autoReset = newAutoReset
            timerToEdit.status = TimerStatus.INACTIVE
            timerToEdit.setUpInitialTime()
            notifyDataSetChanged()
            scope.launch(Dispatchers.IO) { ServiceLocator.TimerRepo.updateTimerInDB(timerToEdit) }
        }
    }

    private fun findTimerIdIndex(id: Int): Int? {
        var index: Int? = null
        for (i in 0 until timersList.size){
            if(timersList[i].id == id){
                index = i
                break
            }
        }
        return index
    }

    fun swapTimers(fromPosition: Int, toPosition: Int) {
        Collections.swap(timersList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)

        scope.launch(Dispatchers.IO) {
            for(i in 0 until timersList.size){
                timersList[i].position = i
                ServiceLocator.TimerRepo.updateTimerPositionInDB(timersList[i].id, timersList[i].position)
            }
        }
    }

    private fun startTimerService(){
        val timerServiceIntent = Intent(context, TimerService::class.java)
        context.applicationContext?.startService(timerServiceIntent)
    }
}