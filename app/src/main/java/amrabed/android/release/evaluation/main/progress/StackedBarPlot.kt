package amrabed.android.release.evaluation.main.progress

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Selection
import amrabed.android.release.evaluation.data.entities.Day
import android.content.Context
import android.graphics.Color
import android.view.View
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.LocalDate
import java.util.*

/**
 * Stacked bar chart
 */
class StackedBarPlot(private val context: Context?, dayList: List<Day?>?, val position: Int) {
    private val data: BarData = setData(intArrayOf(7, 30, 365)[position], dayList)

    private fun setData(days: Int, dayList: List<Day?>?): BarData {
        val entries: MutableList<BarEntry> = ArrayList()
        val n = dayList!!.size
        for (i in (if (n < days) 0 else n - days) until n) {
            val day = dayList[i]
            val diff: Float = Duration(day!!.date, DateTime.now().millis).standardDays.toFloat()
            val ratios = day.ratios // Not in the order we want
            val y = floatArrayOf(ratios[Selection.GOOD.toInt()], ratios[Selection.OK.toInt()], ratios[Selection.BAD.toInt()],
                    ratios[Selection.NONE.toInt()])
            entries.add(BarEntry(diff, y))
        }
        val labels = context!!.resources.getStringArray(R.array.selectionLabels)
        val colors: IntArray = Selection.colors // Also not in the desired order
        val dataset = BarDataSet(entries, null)
        dataset.setColors(intArrayOf(colors[Selection.GOOD.toInt()], colors[Selection.OK.toInt()],
                colors[Selection.BAD.toInt()], colors[Selection.NONE.toInt()]), context)
        dataset.stackLabels = labels
        val barData = BarData(dataset)
        barData.setValueTextColor(Color.WHITE)
        barData.setValueTextSize(12f)
        barData.setValueFormatter(valueFormatter)
        barData.isHighlightEnabled = true
        return barData
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

    private val valueFormatter = IValueFormatter { value, _, _, _ -> if (value > 0) "" + value.toInt() else "" }
    private val dateFormatter = arrayOf(
            IAxisValueFormatter { value, _ -> LocalDate.now().minusDays(value.toInt()).toString("EEE") },
            IAxisValueFormatter { value, _ -> LocalDate.now().minusDays(value.toInt()).toString("d MMM") },
            IAxisValueFormatter { value, _ -> LocalDate.now().minusDays(value.toInt()).toString("MMM") }
    )
}