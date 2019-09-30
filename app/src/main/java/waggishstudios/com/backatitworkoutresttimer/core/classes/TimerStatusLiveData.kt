package waggishstudios.com.backatitworkoutresttimer.core.classes

import androidx.lifecycle.LiveData
import waggishstudios.com.backatitworkoutresttimer.core.TimerStatus
import waggishstudios.com.backatitworkoutresttimer.core.ServiceLocator

class TimerStatusLiveData(id: Int) :  LiveData<TimerStatus>(){
    private val TimerRepo = ServiceLocator.TimerRepo

    override fun onActive() {
        // add listener to the repo, whenever status is updated
    }

    override fun onInactive() {
        // remove listener
    }
}