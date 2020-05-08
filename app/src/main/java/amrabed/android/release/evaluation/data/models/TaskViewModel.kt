package amrabed.android.release.evaluation.data.models

import amrabed.android.release.evaluation.data.Repository
import amrabed.android.release.evaluation.data.entities.Task
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository = Repository(application)
    val taskList: LiveData<MutableList<Task?>?>? by lazy {
        repository.loadCurrentTaskList()
    }

    val selected = MutableLiveData<Int>()

    fun updateTask(task: Task?) {
        repository.updateTask(task)
    }

    fun deleteTask(task: Task?) {
        repository.deleteTask(task)
    }

    fun addTask(task: Task?) {
        repository.addTask(task)
    }

    fun addTasks(tasks: List<Task?>) {
        repository.addTasks(tasks)
    }
}