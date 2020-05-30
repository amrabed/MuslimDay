package amrabed.android.release.evaluation.data

import amrabed.android.release.evaluation.core.Record
import amrabed.android.release.evaluation.core.Task
import amrabed.android.release.evaluation.utilities.time.DateRange
import android.content.Context

class Repository(context: Context) {
    private val db: AppDatabase? = AppDatabase[context]

    fun loadDayTasks(date: Long) = db?.dayTable()?.searchByDate(date)
    fun loadTaskHistoryByDateRange(taskId: String, range: DateRange) = db?.dayTable()?.taskHistoryByDateRange(taskId, range.start.millis, range.end.millis)
    fun updateDay(record: Record?) = AppDatabase.writeExecutor.execute { db!!.dayTable().insert(record) }
    fun loadCurrentTaskList() = db?.taskTable()?.loadCurrentTasks()
    fun updateTask(task: Task?) = AppDatabase.writeExecutor.execute { db!!.taskTable().updateTask(task) }
    fun deleteTask(task: Task?) = AppDatabase.writeExecutor.execute { db!!.taskTable().deleteTask(task) }
    fun addTask(task: Task?) = AppDatabase.writeExecutor.execute { db!!.taskTable().insertTasks(task) }
    fun getDayRange(start: Long, end: Long) = db?.dayTable()?.searchByRange(start, end)

    fun getDayCount() = db?.dayTable()?.countDays()
}