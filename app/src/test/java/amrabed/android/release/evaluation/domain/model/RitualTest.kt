package amrabed.android.release.evaluation.domain.model

import org.junit.Test
import org.junit.Assert.*

class RitualTest {

    @Test
    fun testIsActiveOn() {
        val ritual = Ritual(
            id = "1",
            title = "Test",
            prayerGroup = PrayerGroup.MORNING,
            sortOrder = 0,
            activeDays = 9, // 1 (Mon) | 8 (Thu)
            isHidden = false,
            createdAt = 0,
            updatedAt = 0
        )

        assertTrue(ritual.isActiveOn(1)) // Monday
        assertFalse(ritual.isActiveOn(2)) // Tuesday
        assertFalse(ritual.isActiveOn(3)) // Wednesday
        assertTrue(ritual.isActiveOn(4)) // Thursday
        assertFalse(ritual.isActiveOn(5)) // Friday
        assertFalse(ritual.isActiveOn(6)) // Saturday
        assertFalse(ritual.isActiveOn(7)) // Sunday
    }

    @Test
    fun testIsActiveOnAllDays() {
        val ritual = Ritual(
            id = "1",
            title = "Test",
            prayerGroup = PrayerGroup.MORNING,
            sortOrder = 0,
            activeDays = 127,
            isHidden = false,
            createdAt = 0,
            updatedAt = 0
        )

        for (i in 1..7) {
            assertTrue(ritual.isActiveOn(i))
        }
    }
}
