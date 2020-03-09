package amrabed.android.release.evaluation.progress.item

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Selection
import amrabed.android.release.evaluation.data.entities.Day
import amrabed.android.release.evaluation.data.models.DayViewModel
import amrabed.android.release.evaluation.locale.LocaleManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.util.*

class PieFragment : Fragment(), OnSeekBarChangeListener {
    private val dayCount = intArrayOf(7, 30, 365)
    private var id: String? = null
    private var position = 0
    private var primaryPieChart: PieChart? = null
    private var secondaryPieChart: PieChart? = null
    private var progressText: TextView? = null
    private var primaryDataSet: PieDataSet? = null
    private var secondaryDataSet: PieDataSet? = null
    private var dayList: List<Day?>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.pie, container, false)
        val args = arguments
        if (args != null) {
            position = args.getInt(POSITION)
            id = args.getString(ID)
        }
        ViewModelProvider(activity as ViewModelStoreOwner).get(DayViewModel::class.java).dayList
                ?.observe(viewLifecycleOwner, Observer { dayList: List<Day?>? ->
                    this.dayList = dayList
                    if (position == dayCount.size) {
                        view.findViewById<View>(R.id.num_days).visibility = View.VISIBLE
                        val seekBar = view.findViewById<SeekBar>(R.id.seekbar)
                        seekBar.max = dayList!!.size
                        seekBar.progress = dayList.size / 2 + 1
                        seekBar.setOnSeekBarChangeListener(this)
                        progressText = view.findViewById(R.id.seekbar_value)
                        setProgressText(dayList.size / 2 + 1, seekBar)
                        (view.findViewById<View>(R.id.min) as TextView).setText(R.string.min)
                        val max = "" + dayList.size
                        (view.findViewById<View>(R.id.max) as TextView).text = max
                    }
                    primaryPieChart = view.findViewById(R.id.current)
                    secondaryPieChart = view.findViewById(R.id.previous)
                    primaryDataSet = formatPieChart(primaryPieChart, true)
                    secondaryDataSet = formatPieChart(secondaryPieChart, false)
                    updatePieCharts(if (position == dayCount.size) dayList!!.size / 2 + 1 else dayCount[position])
                })
        return view
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
        updatePieCharts(progress)
        setProgressText(progress, seekBar)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
    private fun updatePieCharts(nDays: Int) {
        updatePieChart(primaryPieChart, primaryDataSet, getRange(nDays))
        updatePieChart(secondaryPieChart, secondaryDataSet, getRange(nDays, dayList!!.size - nDays))
    }

    private fun updatePieChart(pieChart: PieChart?, dataSet: PieDataSet?, days: List<Day?>) {
        val counters = IntArray(4)
        for (day in days) {
            if (id != null) {
                // Get task selection for the day
                counters[day!!.getSelection(id!!).toInt()]++
            }
        }
        dataSet!!.values = getEntries(counters)
        pieChart!!.data = PieData(dataSet)
        pieChart.invalidate()
    }

    private fun formatPieChart(pieChart: PieChart?, isPrimary: Boolean): PieDataSet {
//        final int description = isPrimary ? R.string.current_period : R.string.previous_period;
        val textSize = if (isPrimary) 18 else 12
        val text = if (isPrimary) resources.getStringArray(R.array.current_period)[position] else resources.getStringArray(R.array.previous_period)[position]
        pieChart!!.centerText = text
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.setCenterTextSize(textSize.toFloat())
        pieChart.isRotationEnabled = false
        pieChart.transparentCircleRadius = 0f
        pieChart.setHoleColor(Color.TRANSPARENT)
        //        pieChart.setHoleRadius(75);
        val dataSet = PieDataSet(null, null)
        dataSet.setColors(*colors)
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = textSize.toFloat()
        dataSet.sliceSpace = 1f
        dataSet.valueFormatter = IValueFormatter { value: Float, _: Entry?, _: Int, _: ViewPortHandler? -> "" + if (value > 0) value.toInt() else "" }
        return dataSet
    }

    private fun getEntries(count: IntArray): ArrayList<PieEntry> {
        val entries = ArrayList<PieEntry>(4)
        for (i in count.indices) {
            entries.add(PieEntry(count[i].toFloat(), i))
        }
        return entries
    }

    private val colors: IntArray
        get() {
            val colors = IntArray(4)
            val resources = resources
            val selectionColors: IntArray = Selection.colors
            for (i in selectionColors.indices) {
                colors[i] = resources.getColor(selectionColors[i])
            }
            return colors
        }

    private fun setProgressText(progress: Int, seekBar: SeekBar) {
        val value = "" + progress
        var position = seekBar.x - progressText!!.width / 2.0f
        val context = context
        position += if (context != null && LocaleManager.isEnglish(context)) {
            progress * seekBar.width / seekBar.max.toFloat()
        } else {
            seekBar.width - progress * seekBar.width / seekBar.max.toFloat()
        }
        progressText!!.visibility = if (progress == seekBar.max || progress == 0) View.INVISIBLE else View.VISIBLE
        progressText!!.text = value
        progressText!!.x = position
    }

    private fun getRange(rangeSize: Int): List<Day?> {
        return getRange(rangeSize, dayList!!.size)
    }

    private fun getRange(rangeSize: Int, end: Int): List<Day?> {
        val start = end - rangeSize
        return dayList!!.subList(if (start >= 0) start else 0, if (end > 0) end else 0)
    }

    companion object {
        private const val ID = "ID"
        private const val POSITION = "POSITION"
        fun newInstance(taskId: String?, position: Int): Fragment {
            val fragment = PieFragment()
            val args = Bundle()
            args.putString(ID, taskId)
            args.putInt(POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }
}