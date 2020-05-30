package amrabed.android.release.evaluation.data.tables

import amrabed.android.release.evaluation.core.Record
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

data class SelectionCount(var selection: Byte, val count: Int)

@Dao
interface History {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg records: Record?)

    @Query("SELECT julianDay('now', 'localtime') - julianDay(min(Date(date/1000, 'unixepoch', 'localtime'))) FROM $NAME")
    fun countDays(): LiveData<Int?>

    @Query("SELECT * FROM $NAME WHERE DATE(date/1000, 'unixepoch', 'localtime') = DATE(:date/1000, 'unixepoch', 'localtime')")
    fun searchByDate(date: Long): LiveData<List<Record>>

    @Query("SELECT * FROM $NAME WHERE task = :task ORDER BY date ASC")
    fun searchByTask(task: String): LiveData<List<Record>>

    @Query("SELECT * FROM $NAME WHERE DATE(date/1000, 'unixepoch', 'localtime') BETWEEN DATE(:start/1000, 'unixepoch', 'localtime') AND DATE(:end/1000, 'unixepoch', 'localtime') ORDER BY date, task ASC ")
    fun searchByRange(start: Long, end: Long): LiveData<List<Record>>

    @Query("SELECT selection, Count(selection) as count FROM $NAME WHERE task = :task AND DATE(date/1000, 'unixepoch', 'localtime') BETWEEN DATE(:start/1000, 'unixepoch', 'localtime') AND DATE(:end/1000, 'unixepoch', 'localtime') GROUP BY selection ORDER BY selection ASC ")
    fun taskHistoryByDateRange(task: String, start: Long, end: Long): LiveData<List<SelectionCount>>

    @get:Query("SELECT * FROM $NAME ORDER BY date DESC, task ASC")
    val all: LiveData<List<Record>>
}

const val NAME = "history"