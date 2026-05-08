package amrabed.android.release.evaluation.domain.repository

import amrabed.android.release.evaluation.domain.model.DailyChecklist
import amrabed.android.release.evaluation.domain.model.PrayerGroup
import amrabed.android.release.evaluation.domain.model.Ritual
import kotlinx.coroutines.flow.Flow

interface RitualRepository {
    fun observeDailyChecklist(date: String): Flow<DailyChecklist>
    suspend fun toggleRitual(ritualId: String, date: String, isComplete: Boolean)
    suspend fun addRitual(ritual: Ritual)
    suspend fun updateRitual(ritual: Ritual)
    suspend fun hideRitual(ritualId: String)
    suspend fun deleteRitual(ritualId: String)
    suspend fun reorderRituals(ritualIds: List<String>, prayerGroup: PrayerGroup)
}
