package amrabed.android.release.evaluation.data.tables.v2

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RitualLogDao {
    @Query("SELECT * FROM ritual_logs WHERE date = :date")
    fun getLogsForDate(date: String): Flow<List<RitualLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: RitualLogEntity)

    @Query("DELETE FROM ritual_logs WHERE ritualId = :ritualId AND date = :date")
    suspend fun deleteLog(ritualId: String, date: String)

    @Query("SELECT * FROM ritual_logs WHERE ritualId = :ritualId AND date = :date LIMIT 1")
    suspend fun getLog(ritualId: String, date: String): RitualLogEntity?
}
