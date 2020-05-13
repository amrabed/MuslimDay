package amrabed.android.release.evaluation.tools.graphs

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Selection
import amrabed.android.release.evaluation.data.entities.Day
import android.content.Context
import android.view.View
import com.github.mikephil.charting.charts.BarChart
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
class StackedBarPlot(private val context: Context, private val dayList: List<Day>, val position: Int) {
    private val data: BarData = setData()

    private fun setData(): BarData {
        val n = dayList.size
        val entries = dayList.subList(max(0, n - nDays[position]), n).map { day ->
            BarEntry(Duration(day.date, DateTime().millis).standardDays.toFloat() - 1, day.ratios)
        }
        val data = BarDataSet(entries, null).apply { setColors(Selection.colors, context) }
        return BarData(data).apply { setDrawValues(false) }
    }

    fun show(view: View) {
        val chart: BarChart = view.findViewById(R.id.chart)
        chart.axisLeft.axisMinimum = 0f
        chart.axisRight.isEnabled = false
        chart.axisLeft.isEnabled = false
        chart.setMaxVisibleValueCount(10)
        chart.setDrawGridBackground(false)
        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(false)
        chart.legend.isEnabled = false
        chart.enableScroll()
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = dateFormatter[position]
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.setDrawGridLines(false)
        xAxis.isGranularityEnabled = true
        xAxis.granularity = (if (position > 1) 30 else 1).toFloat()
        chart.description.isEnabled = false
        chart.setScaleEnabled(false)
        chart.data = data
        chart.setFitBars(true)
        chart.invalidate()
    }

    //    private val valueFormatter = IValueFormatter { value, _, _, _ -> if (value > 0) "" + value.toInt() else "" }
    private val dateFormatter = arrayOf(
            IAxisValueFormatter { value, _ -> DateTime().minusDays(value.toInt()).toString("EEE") },
            IAxisValueFormatter { value, _ -> DateTime().minusDays(value.toInt()).toString("d MMM") },
            IAxisValueFormatter { value, _ -> DateTime().minusDays(value.toInt()).toString("MMM") }
    )
}

val nDays = intArrayOf(7, 30, 365)