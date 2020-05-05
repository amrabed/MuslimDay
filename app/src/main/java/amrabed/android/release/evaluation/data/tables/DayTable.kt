package amrabed.android.release.evaluation.data.tables

import amrabed.android.release.evaluation.data.entities.Day
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DayTable {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg days: Day?)

    @Update
    fun updateDay(day: Day?)

    @Query("SELECT * FROM days WHERE date = :date")
    operator fun get(date: Long): LiveData<Day?>

    @get:Query("SELECT * FROM days ORDER BY date ASC")
    val all: LiveData<List<Day>>
}