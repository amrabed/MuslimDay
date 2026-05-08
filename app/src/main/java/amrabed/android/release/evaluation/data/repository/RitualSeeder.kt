package amrabed.android.release.evaluation.data.repository

import amrabed.android.release.evaluation.data.tables.v2.RitualDao
import amrabed.android.release.evaluation.data.tables.v2.RitualEntity
import amrabed.android.release.evaluation.domain.model.PrayerGroup
import java.util.*

object RitualSeeder {
    suspend fun seedIfEmpty(ritualDao: RitualDao) {
        if (ritualDao.getRitualCount() == 0) {
            val now = System.currentTimeMillis()
            val rituals = mutableListOf<RitualEntity>()

            // Pre-Fajr
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 0, PrayerGroup.PRE_FAJR.name, 0, 127, false, now, now)) // Wake up
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 1, PrayerGroup.PRE_FAJR.name, 1, 127, false, now, now)) // Siwak
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 32, PrayerGroup.PRE_FAJR.name, 2, 127, false, now, now)) // Wudu

            // Fajr
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 5, PrayerGroup.FAJR.name, 3, 127, false, now, now)) // Fajr prayer
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 4, PrayerGroup.FAJR.name, 4, 127, false, now, now)) // Fajr sunnah
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 6, PrayerGroup.FAJR.name, 5, 127, false, now, now)) // Fajr azkar

            // Morning
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 9, PrayerGroup.MORNING.name, 6, 127, false, now, now)) // Morning azkar
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 7, PrayerGroup.MORNING.name, 7, 127, false, now, now)) // Quran recitation
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 8, PrayerGroup.MORNING.name, 8, 127, false, now, now)) // Quran memorization
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 10, PrayerGroup.MORNING.name, 9, 127, false, now, now)) // Duha prayer
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 11, PrayerGroup.MORNING.name, 10, 127, false, now, now)) // Exercise
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 3, PrayerGroup.MORNING.name, 11, 9, false, now, now)) // Fasting (Mon=1, Thu=8 -> 1|8 = 9)
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 12, PrayerGroup.MORNING.name, 12, 16, false, now, now)) // Jumu'ah (Fri=16)

            // Dhuhr/Asr
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 14, PrayerGroup.DHUHR_ASR.name, 13, 127, false, now, now)) // Congregational (Dhuhr)
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 15, PrayerGroup.DHUHR_ASR.name, 14, 127, false, now, now)) // Prayer azkar
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 16, PrayerGroup.DHUHR_ASR.name, 15, 127, false, now, now)) // Rawatib sunnah
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 13, PrayerGroup.DHUHR_ASR.name, 16, 127, false, now, now)) // Work / Study
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 17, PrayerGroup.DHUHR_ASR.name, 17, 127, false, now, now)) // Congregational Asr
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 19, PrayerGroup.DHUHR_ASR.name, 18, 127, false, now, now)) // Evening azkar

            // Maghrib
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 20, PrayerGroup.MAGHRIB.name, 19, 127, false, now, now)) // Congregational Maghrib
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 21, PrayerGroup.MAGHRIB.name, 20, 127, false, now, now)) // Maghrib azkar
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 22, PrayerGroup.MAGHRIB.name, 21, 127, false, now, now)) // Rawatib sunnah

            // Isha/Night
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 23, PrayerGroup.ISHA_NIGHT.name, 22, 127, false, now, now)) // Isha prayer
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 24, PrayerGroup.ISHA_NIGHT.name, 23, 127, false, now, now)) // Prayer azkar
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 25, PrayerGroup.ISHA_NIGHT.name, 24, 127, false, now, now)) // Rawatib sunnah
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 26, PrayerGroup.ISHA_NIGHT.name, 25, 127, false, now, now)) // Witr prayer
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 27, PrayerGroup.ISHA_NIGHT.name, 26, 127, false, now, now)) // Diet
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 28, PrayerGroup.ISHA_NIGHT.name, 27, 127, false, now, now)) // Good manners
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 29, PrayerGroup.ISHA_NIGHT.name, 28, 127, false, now, now)) // Honesty
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 30, PrayerGroup.ISHA_NIGHT.name, 29, 127, false, now, now)) // Avoiding backbiting
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 31, PrayerGroup.ISHA_NIGHT.name, 30, 127, false, now, now)) // Lowering the gaze
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 33, PrayerGroup.ISHA_NIGHT.name, 31, 127, false, now, now)) // Night azkar
            rituals.add(RitualEntity(UUID.randomUUID().toString(), null, 32, PrayerGroup.ISHA_NIGHT.name, 32, 127, false, now, now)) // Sleep with wudu

            ritualDao.insertRituals(rituals)
        }
    }
}
