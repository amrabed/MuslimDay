package amrabed.android.release.evaluation.models

import amrabed.android.release.evaluation.core.Record
import amrabed.android.release.evaluation.core.Task
import amrabed.android.release.evaluation.data.Repository
import amrabed.android.release.evaluation.utilities.time.DateRange
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class DayViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application)
    val selectedTask = MutableLiveData<Task>()

    fun getDayCount() = repository.getDayCount()

    fun getDayList(date: Long) = repository.loadDayTasks(date)

    fun taskHistoryByDateRange(taskId: String, range: DateRange) = repository.loadTaskHistoryByDateRange(taskId, range)

    fun getRange(start: Long, end: Long) = repository.getDayRange(start, end)

    fun updateDay(record: Record?) {
        if (record != null) {
            repository.updateDay(record)
        }
    }

    fun selectTask(task: Task) {
        selectedTask.value = task
    }
}