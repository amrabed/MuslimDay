package amrabed.android.release.evaluation.data.entities

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.data.converters.ActiveDaysConverter
import amrabed.android.release.evaluation.preferences.Preferences
import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.joda.time.LocalDate
import java.util.*

@Entity(tableName = "tasks")
data class Task(
        @PrimaryKey var id: String,
        @ColumnInfo(defaultValue = "-1") var defaultIndex: Int, /*   Original index of an item of the default list */
        var currentIndex: Int,
        var currentTitle: String? = null,
        @ColumnInfo(defaultValue = "0x7f") var activeDays: BooleanArray = BooleanArray(7)) {

    @Ignore
    val guideEntry: Int = if (defaultIndex == -1) 0 else DEFAULT_LIST[defaultIndex]

    /**
     * Used for creating new user-defined tasks
     * @param index position of the task in the task list
     */
    @Ignore
    constructor(index: Int) : this(-1, index)

    /**
     * Mainly used for creating default tasks
     * @param defaultIndex default/original index of the task (default -1 for user-defined tasks)
     * @param currentIndex current index of the task
     */
    @Ignore
    constructor(defaultIndex: Int, currentIndex: Int) : this(UUID.randomUUID().toString(), defaultIndex, currentIndex,
            if (defaultIndex != -1 && DEFAULT_LIST[defaultIndex] == R.raw.friday) ACTIVE_FRIDAY else ACTIVE_EVERYDAY)

    @Ignore
    constructor(id: String, defaultIndex: Int, currentIndex: Int, activeDays: Byte) : this(
            id = id,
            defaultIndex = defaultIndex,
            currentIndex = currentIndex,
            activeDays = ActiveDaysConverter().setActiveDays(activeDays)
    )

    fun getTitle(context: Context?): String {
        return (if (currentTitle != null) currentTitle else getDefaultTitle(context))!!
    }

    private fun getDefaultTitle(context: Context?): String? {
        return Preferences.getDefaultTaskTitles(context)[defaultIndex]
    }

    private fun isActiveDay(day: Int): Boolean {
        return activeDays[day - 1]
    }

    fun setCurrentIndex(currentIndex: Int): Task {
        this.currentIndex = currentIndex
        return this
    }

    fun setCurrentTitle(currentTitle: String?): Task {
        this.currentTitle = currentTitle
        return this
    }

    /**
     * Get shifted version of active days for Arabic list of days
     * (Mon, Tue, ..., Fri) -> (Sat, Sun, ..., Fri)
     *
     * @param shift number of days
     * @return shifted version of active days
     */
    fun getActiveDays(shift: Int): BooleanArray {
        if (shift == 0) return activeDays
        val shifted = BooleanArray(7)
        for (i in activeDays.indices) {
            shifted[(i + shift) % 7] = activeDays[i]
        }
        return shifted
    }

    fun setActiveDay(day: Int, isActive: Boolean) {
        activeDays[day - 1] = isActive
    }

    fun isVisible(context: Context?, day: Day?): Boolean {
        return if (guideEntry == R.raw.fasting) day!!.isFastingDay(context) else isActiveDay(LocalDate(day!!.date).dayOfWeek)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (id != other.id) return false
        if (defaultIndex != other.defaultIndex) return false
        if (currentIndex != other.currentIndex) return false
        if (guideEntry != other.guideEntry) return false
        if (currentTitle != other.currentTitle) return false
        if (!activeDays.contentEquals(other.activeDays)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + defaultIndex
        result = 31 * result + currentIndex
        result = 31 * result + guideEntry
        result = 31 * result + (currentTitle?.hashCode() ?: 0)
        result = 31 * result + activeDays.contentHashCode()
        return result
    }

    companion object {
        private const val ACTIVE_FRIDAY = 0x10.toByte()
        private const val ACTIVE_EVERYDAY = 0x7F.toByte()
        val DEFAULT_LIST = intArrayOf(R.raw.wakeup, R.raw.brush, R.raw.night, R.raw.fasting,
                R.raw.sunna, R.raw.fajr, R.raw.fajr_azkar,
                R.raw.quran, R.raw.memorize,
                R.raw.morning, R.raw.duha,
                R.raw.sports, R.raw.friday, R.raw.work,
                R.raw.cong, R.raw.prayer_azkar, R.raw.rawateb,
                R.raw.cong, R.raw.prayer_azkar, R.raw.evening,
                R.raw.cong, R.raw.fajr_azkar, R.raw.rawateb,
                R.raw.isha, R.raw.prayer_azkar, R.raw.rawateb, R.raw.wetr,
                R.raw.diet, R.raw.manners, R.raw.honesty, R.raw.backbiting, R.raw.gaze,
                R.raw.wudu, R.raw.sleep)
    }
}