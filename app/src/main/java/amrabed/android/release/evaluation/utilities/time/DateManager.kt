package amrabed.android.release.evaluation.utilities.time

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.utilities.preferences.Preferences
import android.content.Context
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.chrono.IslamicChronology

object DateManager {
    fun getDatabaseKey(dayShift: Int) = DateTime().minusDays(dayShift).withTimeAtStartOfDay().millis

    fun getDayDifference(date: Long) = Duration(DateTime(date), DateTime()).standardDays

    fun getDate(context: Context, date: Long): String {
        if (Preferences.isHijriCalendar(context)) {
            val hijriDate = DateTime(date, IslamicChronology.getInstance())
            val month = context.resources.getStringArray(R.array.months)[hijriDate.monthOfYear - 1]
            return hijriDate.toString(context.getString(R.string.hijriShortFormatPattern)) + " " + month
        }
        return DateTime(date).toString(context.getString(R.string.shortFormatPattern))
    }

    fun getDate(context: Context, date: DateTime, pattern: String): String {
        if (Preferences.isHijriCalendar(context)) {
            val hijriDate = date.withChronology(IslamicChronology.getInstance())
            val month = context.resources.getStringArray(R.array.months)[hijriDate.monthOfYear - 1]
            return hijriDate.toString("d ") + month
        }
        return date.toString(pattern)
    }

    fun getMonth(context: Context, date: DateTime): String {
        if (Preferences.isHijriCalendar(context)) {
            val hijriDate = date.withChronology(IslamicChronology.getInstance())
            return context.resources.getStringArray(R.array.months)[hijriDate.monthOfYear - 1]
        }
        return date.toString("MMM")
    }

    fun getInterval(context: Context, shift: Int, period: Int): DateRange {
        val today = getDateTime(context, DateTime().millis)
        val startDate = when (period) {
            // Start of week is Sunday for most Muslim countries
            Period.WEEK -> today.plusDays(1).withDayOfWeek(1).minusWeeks(shift).minusDays(1)
            Period.MONTH -> today.withDayOfMonth(1).minusMonths(shift)
            else -> today.withDayOfYear(1).minusYears(shift)
        }

        val endDate = when (period) {
            Period.WEEK -> startDate.plusWeeks(1).minusDays(1)
            Period.MONTH -> startDate.withDayOfMonth(startDate.dayOfMonth().maximumValue)
            else -> startDate.withDayOfYear(startDate.dayOfYear().maximumValue)
        }
        return DateRange(startDate, endDate)
    }

    fun getDayCount(date: DateTime, period: Int): Int {
        return when (period) {
            Period.WEEK -> 7
            Period.MONTH -> date.dayOfMonth().maximumValue
            else -> date.dayOfYear().maximumValue
        }
    }

    private fun getDateTime(context: Context, date: Long): DateTime {
        if (Preferences.isHijriCalendar(context)) {
            return DateTime(date, IslamicChronology.getInstance())
        }
        return DateTime(date)
    }

    fun getIntervalStart(context: Context, shift: Int, period: Int): String {
        val dateTime = getInterval(context, shift, period).start
        return when (period) {
            Period.WEEK -> getDate(context, dateTime, "MMM d")
            Period.MONTH -> getMonth(context, dateTime)
            else -> dateTime.toString("yyyy")
        }
    }

    fun getDateTimeText(context: Context, date: DateTime): String {
        return date.toString("EEE ") + getDate(context, date, "MMM d") + " " + date.toLocalTime().toString("h:mm a")
    }
}

object Period {
    const val WEEK = 0
    const val MONTH = 1
    const val YEAR = 2
}

data class DateRange(val start: DateTime, val end: DateTime)