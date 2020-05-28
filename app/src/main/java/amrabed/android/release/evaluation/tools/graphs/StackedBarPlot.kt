package amrabed.android.release.evaluation.tools.graphs

import amrabed.android.release.evaluation.core.Selection
import amrabed.android.release.evaluation.data.entities.Day
import amrabed.android.release.evaluation.utilities.time.DateManager
import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import org.joda.time.DateTime
import org.joda.time.Duration
import kotlin.math.max

/**
 * Stacked bar chart
 */
class StackedBarPlot(private val context: Context, private val dayList: List<Day>, private val period: Int) : Plot {
    private val data: BarData = setData()

    private fun setData(): BarData {
        val last = dayList.size
        val entries = dayList.subList(max(0, last - nDays[period]), last).map { day ->
            BarEntry(Duration(day.date, DateTime().millis).standardDays.toFloat(), day.ratios)
        }
        val data = BarDataSet(entries, null).apply { setColors(Selection.colors, context) }
        return BarData(data).apply {
            setValueTextSize(12f)
            setValueTextColor(Color.WHITE)
            setValueFormatter { value, _, _, _ -> "" + if (value > 1) value.toInt() else "" }
        }
    }

    override fun show(chart: Chart<*>) {
        val barChart = chart as BarChart
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
        barChart.axisLeft.isEnabled = false
        barChart.setMaxVisibleValueCount(10)
        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(false)
        barChart.legend.isEnabled = false
        barChart.enableScroll()
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = dateFormatter[period]
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.setDrawGridLines(false)
        xAxis.isGranularityEnabled = true
        xAxis.granularity = (if (period > 1) 30 else 1).toFloat()
        barChart.description.isEnabled = false
        barChart.setScaleEnabled(false)
        barChart.data = data
        barChart.setFitBars(true)
        barChart.invalidate()
    }

    private val dateFormatter = arrayOf(
            IAxisValueFormatter { value, _ -> DateTime().minusDays(value.toInt()).toString("EEE") },
            IAxisValueFormatter { value, _ -> DateManager(context).getDate(DateTime().minusDays(value.toInt()), "d MMM") },
            IAxisValueFormatter { value, _ -> DateManager(context).getMonth(DateTime().minusDays(value.toInt())) }
    )
}

val nDays = intArrayOf(7, 30, 365)