package amrabed.android.release.evaluation.data.tables

import amrabed.android.release.evaluation.core.Task
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskTable {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTasks(vararg tasks: Task?)

    @Update
    fun updateTask(task: Task?)

    @Delete
    fun deleteTask(task: Task?)

    @Query("SELECT * FROM tasks ORDER BY currentIndex ASC")
    fun loadCurrentTasks(): LiveData<MutableList<Task>>?

    @Query("SELECT * FROM tasks WHERE reminder IS NOT NULL ORDER BY currentIndex ASC")
    fun loadReminders(): LiveData<MutableList<Task>>?
}