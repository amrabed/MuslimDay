package amrabed.android.release.evaluation.data.entities

import amrabed.android.release.evaluation.preferences.Preferences
import android.content.Context
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.chrono.IslamicChronology
import java.util.*

@Entity(tableName = "days")
data class Day(@PrimaryKey var date: Long, var selections: HashMap<String, Byte>? = hashMapOf()) {

    @Ignore
    constructor(date: LocalDate) : this(date.toDateTimeAtStartOfDay().millis)

    fun getSelection(id: String): Byte {
        val value = selections?.get(id)
        return value ?: 0
    }

    val ratios: FloatArray
        get() {
            val ratios = floatArrayOf(0f, 0f, 0f, 0f)
            selections?.values?.forEach { selection -> ratios[selection.toInt()]++ }
            return ratios
        }

    fun setSelectionAt(id: String, selection: Byte) {
        selections?.set(id, selection)
    }

    fun isFastingDay(context: Context?): Boolean {
        val fasting = Preferences.getFastingDays(context)
        val dayAfterDay = fasting and 0x08 != 0
        if (dayAfterDay) {
            val lastDayOfFasting = Preferences.getLastDayOfFasting(context)
            val start = DateTime(lastDayOfFasting)
            val end = DateTime(date)
            val isMoreThanOne = Days.daysBetween(start, end).isGreaterThan(Days.ONE)
            if (isMoreThanOne) {
                Preferences.setLastDayOfFasting(context, date)
                return true
            }
        } else {
            Preferences.removeLastDayOfFasting(context)
        }
        val dateHijri = DateTime(date).withChronology(IslamicChronology.getInstance())
        val month = dateHijri.monthOfYear().get()
        val dayOfMonth = dateHijri.dayOfMonth().get()
        if (month == 1 && (dayOfMonth == 9 || dayOfMonth == 10)) // Aashoraa
        {
            return true
        }
        if (month == 12 && dayOfMonth == 9) // Arafaat
        {
            return true
        }
        val dayOfWeek = dateHijri.dayOfWeek().get()
        val isFastingMonday = fasting and 0x01 != 0
        val isFastingThursday = fasting and 0x02 != 0
        val isFastingWhiteDays = fasting and 0x04 != 0
        return isFastingThursday && dayOfWeek == DateTimeConstants.THURSDAY ||
                isFastingMonday && dayOfWeek == DateTimeConstants.MONDAY ||
                isFastingWhiteDays && (dayOfMonth == 13 || dayOfMonth == 14 || dayOfMonth == 15)
    }
}