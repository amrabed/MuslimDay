package amrabed.android.release.evaluation.models

import amrabed.android.release.evaluation.data.Repository
import amrabed.android.release.evaluation.data.entities.Day
import amrabed.android.release.evaluation.data.entities.Task
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DayViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application)
    val dayList: LiveData<List<Day>>? by lazy {
        repository.loadAllDays()
    }
    val selectedDay = MutableLiveData<Day>()

    fun selectDay(day: Day?) {
        selectedDay.value = day
    }

    fun updateDay(day: Day?) {
        repository.updateDay(day)
    }

    val selectedTask = MutableLiveData<Task?>()

    fun selectTask(task: Task?) {
        selectedTask.value = task
    }

}