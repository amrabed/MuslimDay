package amrabed.android.release.evaluation.tools.graphs

import amrabed.android.release.evaluation.core.Selection
import amrabed.android.release.evaluation.data.tables.SelectionCount
import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IValueFormatter

class Pie(val context: Context, private val counters: List<SelectionCount>, private val period: Int, val position: Int, private val dayCount: Int) : Plot {

    private val dataSet = PieDataSet(null, null).apply {
        val frequencies = FloatArray(Selection.values().size).apply {
            counters.map { this[it.selection.toInt()] = it.count.toFloat() }
            this[Selection.NONE.ordinal] += dayCount - sum()
        }
        values = frequencies.map { PieEntry(it) }
        colors = Selection.colors.map { ContextCompat.getColor(context, it) }
        valueTextColor = Color.WHITE
        valueTextSize = 12f
        sliceSpace = 0.5f
        valueFormatter = IValueFormatter { value, _, _, _ -> "" + if (value > minDays[period]) value.toInt() else "" }
    }

    override fun show(chart: Chart<*>) {
        val pieChart = chart as PieChart
        pieChart.description?.isEnabled = false
        pieChart.legend?.isEnabled = false
        pieChart.isRotationEnabled = false
        pieChart.transparentCircleRadius = 0f
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.data = PieData(dataSet)
        pieChart.invalidate()
    }

    companion object {
        private val minDays = intArrayOf(0, 1, 10)
    }
}