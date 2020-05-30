package amrabed.android.release.evaluation.models

import amrabed.android.release.evaluation.core.Task
import amrabed.android.release.evaluation.data.Repository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository by lazy { Repository(application) }
    private val modifications by lazy { mutableListOf<Modification>() }

    val taskList by lazy { repository.loadCurrentTaskList() }
    val selected = MutableLiveData<Task>()

    fun select(task: Task) {
        selected.value = task
    }

    fun add(task: Task) {
        repository.addTask(task)
    }

    fun update(task: Task) {
        repository.updateTask(task)
    }

    fun delete(task: Task) {
        modifications.add(Modification(task, Modification.DELETE))
    }

    fun move(task: Task) {
        modifications.add(Modification(task, Modification.UPDATE))
    }

    fun undo() {
        modifications.removeAt(modifications.lastIndex)
    }

    fun commit() {
        modifications.forEach { modification ->
            when (modification.operation) {
                Modification.ADD -> repository.addTask(modification.task)
                Modification.DELETE -> repository.deleteTask(modification.task)
                Modification.UPDATE -> repository.updateTask(modification.task)
                else -> {
                }
            }
        }
    }

    fun discard() {
        modifications.clear()
    }

    fun isChanged(): Boolean {
        return modifications.isNotEmpty()
    }

    class Modification(val task: Task?, val operation: Int) {
        companion object {
            const val ADD = 0
            const val UPDATE = 1
            const val DELETE = 2
        }
    }
}