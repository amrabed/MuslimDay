package amrabed.android.release.evaluation.tools.graphs

import amrabed.android.release.evaluation.data.entities.Day
import amrabed.android.release.evaluation.utilities.time.DateManager
import android.content.Context
import com.github.mikephil.charting.charts.Chart
import org.joda.time.Days
import kotlin.math.max
import kotlin.math.min

interface Plot {

    fun show(chart: Chart<*>)

    /**
     * Get day range for the given position, e.g. current week/month/year for shift 0
     */
    fun getRange(context: Context, dayList: List<Day>, shift: Int, period: Int): List<Day?> {
        val last = dayList.lastIndex
        val lastDay = DateManager(context).getDateTime(dayList.last().date)
        val diff = 1 + Days.daysBetween(when (period) {
            // Start of week is Sunday for many Muslim countries
            Period.WEEK -> lastDay.withDayOfWeek(7).minusDays(7).minusWeeks(shift)
            Period.MONTH -> lastDay.withDayOfMonth(1).minusMonths(shift)
            else -> lastDay.withDayOfYear(1).minusYears(shift)
        }, lastDay).days

        val startDate = lastDay.minusDays(diff)
        val start = last - diff
        val end = start + when (period) {
            Period.WEEK -> 7
            Period.MONTH -> startDate.dayOfMonth().maximumValue
            else -> startDate.dayOfYear().maximumValue
        }
        return dayList.subList(max(0, start), min(end, last))
    }

    object Period {
        const val WEEK = 0
        const val MONTH = 1
        const val YEAR = 2
    }
}