package com.garypanapps.nextsetworkouttimer.ui.fragments.timer

import kotlinx.coroutines.*
import com.garypanapps.nextsetworkouttimer.core.adapters.recyclers.TimerRecyclerAdapter
import com.garypanapps.nextsetworkouttimer.core.entities.Timer
import com.garypanapps.nextsetworkouttimer.core.ServiceLocator
import com.garypanapps.nextsetworkouttimer.ui.base.templates.BaseViewModel
import kotlin.collections.ArrayList

class HomeTimersViewModel : BaseViewModel() {
    private val parentJob = Job()
    private val defaultScope = CoroutineScope(Dispatchers.Main + parentJob)

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    fun displayUserTimers(adapter: TimerRecyclerAdapter) = defaultScope.launch(Dispatchers.IO){
        val listOfSortedTimers = ServiceLocator.TimerRepo.getListOfSortedTimers()
        listOfSortedTimers?.let {
            adapter.addAllTimers(listOfSortedTimers)
            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
            }
        }
    }

    fun addTimer(name: String, timerList : ArrayList<Int>, shouldAutoInc : Boolean, autoReset: Int,
                 adapter : TimerRecyclerAdapter) = defaultScope.launch(Dispatchers.IO) {
        val timerToAdd = createTimer(name, timerList, shouldAutoInc, autoReset, adapter.itemCount)
        ServiceLocator.TimerRepo.addTimerToDB(timerToAdd)
        val newestTimerInDB = ServiceLocator.TimerRepo.getNewestTimerInDB()
        withContext(Dispatchers.Main) {
            newestTimerInDB?.let {
                updateTimerListRV(newestTimerInDB, adapter)
            }
        }
    }

    private fun createTimer(name: String, timerList : ArrayList<Int>, shouldAutoInc : Boolean, autoReset: Int,
                            position: Int): Timer{
        return Timer(name, timerList, shouldAutoInc, autoReset, position)
    }

    private fun updateTimerListRV(timerToAdd: Timer, adapter : TimerRecyclerAdapter){
        adapter.addTimer(timerToAdd)
        adapter.notifyDataSetChanged()
    }
}