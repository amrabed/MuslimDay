package amrabed.android.release.evaluation.tools.graphs

import amrabed.android.release.evaluation.core.Selection
import amrabed.android.release.evaluation.data.entities.Day
import amrabed.android.release.evaluation.utilities.time.Period
import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IValueFormatter
import org.joda.time.Days
import org.joda.time.LocalDate
import kotlin.math.max
import kotlin.math.min

class Pie(val context: Context, private val dayList: List<Day?>?, private val taskId: String, private val period: Int, val position: Int) {

    private val dataSet = PieDataSet(null, null).apply {
        val counters = IntArray(Selection.values().size)
        getRange(position).forEach { counters[it!!.getSelection(taskId).value.toInt()]++ }
        values = counters.map { PieEntry(it.toFloat()) }
        colors = Selection.colors.map { ContextCompat.getColor(context, it) }
        valueTextColor = Color.WHITE
        valueTextSize = 12f
        sliceSpace = 0.5f
        valueFormatter = IValueFormatter { value, _, _, _ -> "" + if (value > minDays[period]) value.toInt() else "" }
    }

    fun show(pieChart: PieChart?) {
        pieChart?.description?.isEnabled = false
        pieChart?.legend?.isEnabled = false
        pieChart?.isRotationEnabled = false
        pieChart?.transparentCircleRadius = 0f
        pieChart?.setHoleColor(Color.TRANSPARENT)
        pieChart?.data = PieData(dataSet)
        pieChart?.invalidate()
    }

    /**
     * Get day range for the given position, e.g. current week/month/year for position 0
     */
    private fun getRange(position: Int): List<Day?> {
        val last = dayList!!.size - 1
        val lastDay = LocalDate(dayList.last()?.date)
        val diff = 1 + Days.daysBetween(when (period) {
            // Start of week is Sunday for many Muslim countries
            Period.WEEK -> lastDay.withDayOfWeek(7).minusDays(7).minusWeeks(position)
            Period.MONTH -> lastDay.withDayOfMonth(1).minusMonths(position)
            else -> lastDay.withDayOfYear(1).minusYears(position)
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

    companion object {
        private val minDays = intArrayOf(0, 1, 2)
    }
}