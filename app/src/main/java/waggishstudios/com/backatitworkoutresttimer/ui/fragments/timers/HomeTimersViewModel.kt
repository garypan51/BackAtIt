package waggishstudios.com.backatitworkoutresttimer.ui.fragments.timers

import kotlinx.coroutines.*
import waggishstudios.com.backatitworkoutresttimer.core.adapters.recyclers.TimerRecyclerAdapter
import waggishstudios.com.backatitworkoutresttimer.core.entities.Timer
import waggishstudios.com.backatitworkoutresttimer.core.ServiceLocator
import waggishstudios.com.backatitworkoutresttimer.ui.base.templates.BaseViewModel
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