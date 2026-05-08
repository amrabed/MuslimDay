package amrabed.android.release.evaluation.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface PrayerTimeRepository {
    fun getPrayerTime(prayerGroup: String, date: String): Flow<String?>
}

class PrayerTimeRepositoryPlaceholder : PrayerTimeRepository {
    override fun getPrayerTime(prayerGroup: String, date: String): Flow<String?> {
        // Placeholder implementation
        val time = when (prayerGroup) {
            "FAJR" -> "5:43 AM"
            "MORNING" -> "6:15 AM"
            "DHUHR_ASR" -> "12:30 PM"
            "MAGHRIB" -> "6:45 PM"
            "ISHA_NIGHT" -> "8:15 PM"
            else -> null
        }
        return flowOf(time)
    }
}
