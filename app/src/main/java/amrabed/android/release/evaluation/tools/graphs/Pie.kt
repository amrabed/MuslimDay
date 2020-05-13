package amrabed.android.release.evaluation.tools.graphs

import amrabed.android.release.evaluation.core.Selection
import amrabed.android.release.evaluation.data.entities.Day
import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IValueFormatter

class Pie(val context: Context, private val dayList: List<Day>, private val taskId: String, private val period: Int, val position: Int) : Plot {

    private val dataSet = PieDataSet(null, null).apply {
        val counters = IntArray(Selection.values().size)
        getRange(context, dayList, position, period).forEach { counters[it!!.getSelection(taskId).value.toInt()]++ }
        values = counters.map { PieEntry(it.toFloat()) }
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
        private val minDays = intArrayOf(0, 1, 2)
    }
}