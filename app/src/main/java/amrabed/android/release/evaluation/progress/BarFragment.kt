package amrabed.android.release.evaluation.progress

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Selection
import amrabed.android.release.evaluation.data.entities.Day
import amrabed.android.release.evaluation.data.models.DayViewModel
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.LocalDate
import java.util.*

class BarFragment : Fragment() {
    private val nDays = intArrayOf(7, 30, 365)
    private var position = 0
    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bar, parent, false)
        assert(arguments != null)
        position = requireArguments().getInt(POSITION)
        ViewModelProvider(activity as ViewModelStoreOwner).get(DayViewModel::class.java).dayList
                ?.observe(viewLifecycleOwner, Observer { dayList: List<Day?>? ->
                    StackedBarPlot(context, dayList)
                            .getChart(view)
                })
        return view
    }

    /**
     * Bar chart
     */
    private inner class StackedBarPlot internal constructor(private val context: Context?, dayList: List<Day?>?) {
        private val data: BarData
        private fun setData(days: Int, dayList: List<Day?>?): BarData {
            val entries: MutableList<BarEntry> = ArrayList()
            val n = dayList!!.size
            for (i in (if (n < days) 0 else n - days) until n) {
                val day = dayList[i]
                val diff : Float= Duration(day!!.date, DateTime.now().millis).standardDays.toFloat()
                val ratios = day.ratios // Not in the order we want
                val y = floatArrayOf(ratios[Selection.GOOD.toInt()], ratios[Selection.OK.toInt()], ratios[Selection.BAD.toInt()],
                        ratios[Selection.NONE.toInt()])
                entries.add(BarEntry(diff, y))
            }
            val labels = context!!.resources.getStringArray(R.array.selection_labels)
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

        fun getChart(view: View) {
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

        private val valueFormatter = IValueFormatter { value: Float, _: Entry?, _: Int, _: ViewPortHandler? -> if (value < 2) "" else "$value" }
        private val dateFormatter = arrayOf(
                IAxisValueFormatter { value: Float, _: AxisBase? -> LocalDate.now().minusDays(value.toInt()).toString("EEE") },
                IAxisValueFormatter { value: Float, _: AxisBase? -> LocalDate.now().minusDays(value.toInt()).toString("d MMM") },
                IAxisValueFormatter { value: Float, _: AxisBase? -> LocalDate.now().minusDays(value.toInt()).toString("MMM") }
        )

        init {
            data = setData(nDays[position], dayList)
        }
    }

    companion object {
        private const val POSITION = "Number of displayed days"
        fun newInstance(position: Int): BarFragment {
            val fragment = BarFragment()
            val args = Bundle()
            args.putInt(POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }
}