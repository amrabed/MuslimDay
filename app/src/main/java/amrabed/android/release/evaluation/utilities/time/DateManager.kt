package amrabed.android.release.evaluation.utilities.time

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.utilities.preferences.Preferences
import android.content.Context
import org.joda.time.DateTime
import org.joda.time.chrono.IslamicChronology

class DateManager(val context: Context) {
    fun getDateTime(date: Long): DateTime {
        if (Preferences.isHijriCalendar(context)) {
            return DateTime(date, IslamicChronology.getInstance())
        }
        return DateTime(date)
    }

    fun getDate(date: Long): String {
        if (Preferences.isHijriCalendar(context)) {
            val hijriDate = DateTime(date, IslamicChronology.getInstance())
            val month = context.resources.getStringArray(R.array.months)[hijriDate.monthOfYear - 1]
            return hijriDate.toString(context.getString(R.string.hijriShortFormatPattern)) + " " + month
        }
        return DateTime(date).toString(context.getString(R.string.shortFormatPattern))
    }

    fun getDate(date: DateTime, pattern: String): String {
        if (Preferences.isHijriCalendar(context)) {
            val hijriDate = date.withChronology(IslamicChronology.getInstance())
            return hijriDate.toString("d ") + context.resources.getStringArray(R.array.months)[hijriDate.monthOfYear - 1]
        }
        return date.toString(pattern)
    }

    fun getMonth(date: DateTime): String {
        if (Preferences.isHijriCalendar(context)) {
            val hijriDate = date.withChronology(IslamicChronology.getInstance())
            return context.resources.getStringArray(R.array.months)[hijriDate.monthOfYear - 1]
        }
        return date.toString("MMM")
    }

    fun getYear(date: DateTime): String {
        if (Preferences.isHijriCalendar(context)) {
            return date.withChronology(IslamicChronology.getInstance()).toString("yyyy")
        }
        return date.toString("yyyy")
    }
}