package amrabed.android.release.evaluation.domain.model

import org.junit.Test
import org.junit.Assert.*

class DailyChecklistTest {

    @Test
    fun testCompletionRate() {
        val ritual1 = Ritual("1", "R1", -1, PrayerGroup.FAJR, 0, 127, false, 0, 0)
        val ritual2 = Ritual("2", "R2", -1, PrayerGroup.FAJR, 1, 127, false, 0, 0)

        val log1 = RitualLog("l1", "1", "2023-10-27", 0, true)

        val group = ChecklistGroup(
            PrayerGroup.FAJR,
            "5:00 AM",
            listOf(
                RitualWithLog(ritual1, log1),
                RitualWithLog(ritual2, null)
            )
        )

        val checklist = DailyChecklist("2023-10-27", listOf(group))

        assertEquals(2, checklist.totalCount)
        assertEquals(1, checklist.completedCount)
        assertEquals(0.5f, checklist.completionRate, 0.001f)
    }

    @Test
    fun testCompletionRateEmpty() {
        val checklist = DailyChecklist("2023-10-27", emptyList())
        assertEquals(0, checklist.totalCount)
        assertEquals(0, checklist.completedCount)
        assertEquals(0f, checklist.completionRate, 0.001f)
    }
}
