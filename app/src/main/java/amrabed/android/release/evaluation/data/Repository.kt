package amrabed.android.release.evaluation.data

import amrabed.android.release.evaluation.data.entities.Day
import amrabed.android.release.evaluation.data.entities.Task
import android.content.Context
import androidx.lifecycle.LiveData

class Repository(context: Context) {
    private val db: AppDatabase? = AppDatabase[context]

    fun loadAllDays(): LiveData<List<Day>>? {
        return db?.dayTable()?.all
    }

    fun updateDay(day: Day?) {
        AppDatabase.writeExecutor.execute{ db!!.dayTable().updateDay(day) }
    }

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