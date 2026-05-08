package amrabed.android.release.evaluation.domain.model

enum class PrayerGroup {
    PRE_FAJR, FAJR, MORNING, DHUHR_ASR, MAGHRIB, ISHA_NIGHT
}

data class Ritual(
    val id: String,
    val title: String?,          // null = use localized default title
    val defaultIndex: Int = -1,  // -1 = custom
    val prayerGroup: PrayerGroup,
    val sortOrder: Int,
    val activeDays: Int,         // bitmask
    val isHidden: Boolean,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun isDefaultRitual() = defaultIndex >= 0
    fun isActiveOn(dayOfWeek: Int): Boolean {
        // bit 0=Mon ... bit 6=Sun
        // dayOfWeek: 1=Mon ... 7=Sun (Joda-Time convention)
        return (activeDays shr (dayOfWeek - 1)) and 1 == 1
    }
}
