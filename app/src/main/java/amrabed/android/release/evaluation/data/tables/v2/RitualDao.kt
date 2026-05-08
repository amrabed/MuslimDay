package amrabed.android.release.evaluation.data.tables.v2

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RitualDao {
    @Query("SELECT * FROM rituals WHERE isHidden = 0 ORDER BY sortOrder ASC")
    fun getAllVisibleRituals(): Flow<List<RitualEntity>>

    @Query("SELECT * FROM rituals ORDER BY prayerGroup, sortOrder ASC")
    fun getAllRituals(): Flow<List<RitualEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRitual(ritual: RitualEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRituals(rituals: List<RitualEntity>)

    @Update
    suspend fun updateRitual(ritual: RitualEntity)

    @Delete
    suspend fun deleteRitual(ritual: RitualEntity)

    @Query("DELETE FROM rituals WHERE defaultIndex < 0")
    suspend fun deleteAllCustomRituals()

    @Query("SELECT COUNT(*) FROM rituals")
    suspend fun getRitualCount(): Int
}
