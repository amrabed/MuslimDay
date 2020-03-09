package amrabed.android.release.evaluation.data.repositories

import amrabed.android.release.evaluation.data.AppDatabase
import amrabed.android.release.evaluation.data.entities.Task
import android.content.Context
import androidx.lifecycle.LiveData

class TaskRepository(context: Context) {
    private val db: AppDatabase? = AppDatabase[context]
    fun loadCurrentTaskList(): LiveData<MutableList<Task?>?>? {
        return db?.taskTable()?.loadCurrentTasks()
    }

    fun addTasks(tasks: List<Task?>) {
        AppDatabase.writeExecutor.execute { db!!.taskTable().insertTasks(*tasks.toTypedArray()) }
    }

    fun updateTask(task: Task?) {
        AppDatabase.writeExecutor.execute { db!!.taskTable().updateTask(task) }
    }

    fun deleteTask(task: Task?) {
        AppDatabase.writeExecutor.execute { db!!.taskTable().deleteTask(task) }
    }

    fun addTask(task: Task?) {
        AppDatabase.writeExecutor.execute { db!!.taskTable().insertTasks(task) }
    }

}