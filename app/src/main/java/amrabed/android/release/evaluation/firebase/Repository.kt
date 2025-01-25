package amrabed.android.release.evaluation.firebase

import amrabed.android.release.evaluation.core.Record
import amrabed.android.release.evaluation.core.Task
import amrabed.android.release.evaluation.data.AppDatabase
import amrabed.android.release.evaluation.utilities.auth.Authenticator
import android.app.Application
import androidx.preference.PreferenceManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

const val TASKS = "tasks"
const val USERS = "users"

object Repository {
    private val db = Firebase.firestore
    private val taskCollection = getCollection(TASKS)
    private val history = getCollection("history")

    fun loadTaskList() = FirestoreLiveData(taskCollection)

    fun saveTask(task: Task) = taskCollection?.document(task.id)?.set(task)

    fun deleteTask(task: Task) = taskCollection?.document(task.id)?.delete()

    fun loadDayTasks(date: Long) = history?.whereEqualTo("date", date)

    //    fun loadTaskHistoryByDateRange(taskId: String, range: DateRange) = history.taskHistoryByDateRange(taskId, range.start.millis, range.end.millis)
    fun updateRecord(record: Record) = AppDatabase.writeExecutor.execute { history?.document(record.id)?.set(record) }

    ////    fun loadCurrentTaskList() = db?.taskTable()?.loadCurrentTasks()
////    fun updateTask(task: Task?) = AppDatabase.writeExecutor.execute { db!!.taskTable().updateTask(task) }
////    fun deleteTask(task: Task?) = AppDatabase.writeExecutor.execute { db!!.taskTable().deleteTask(task) }
////    fun addTask(task: Task?) = AppDatabase.writeExecutor.execute { db!!.taskTable().insertTasks(task) }
    fun getDayRange(start: Long, end: Long) =
        history?.whereGreaterThan("date", start)?.whereLessThanOrEqualTo("date", end)
//    fun getDayCount() = db.history().countDays()
//    fun getTaskReminders() = taskCollection["reminder"].

    private fun getCollection(name: String): CollectionReference? {
        val user = Authenticator.user
        return if (user != null) db.collection(USERS).document(user.uid).collection(name) else null
    }

    private fun saveTasks(tasks: MutableList<Task>?) = db.runBatch { batch ->
        if (taskCollection != null) {
            tasks?.forEach { task ->
                batch.set(taskCollection.document(task.id), task)
            }
        }
    }

    private fun saveHistory(records: List<Record>?) = db.runBatch { batch ->
        if (history != null) {
            records?.forEach { record ->
                batch.set(history.document(record.id), record)
            }
        }
    }


    fun migrate(application: Application) {
        val isMigrated = PreferenceManager.getDefaultSharedPreferences(application).getBoolean("migrated", false)
        if (!isMigrated) {
            val db = AppDatabase[application]
            val tasks = db?.taskTable()?.loadCurrentTasks()
            tasks?.observeForever { taskList ->
                saveTasks(taskList).addOnCompleteListener {
                    PreferenceManager.getDefaultSharedPreferences(application).edit().putBoolean("migrated", true)
                        .apply()
                }

//                taskList.forEach { task ->
//                    val history = db.history().searchByTask(task.id)
//                    history.observeForever {
//                        taskCollection?.document(task.id)?.update("history", it)
//                    }
//                }
            }

            db?.history()?.all()?.observeForever { saveHistory(it) }
        }
    }
}