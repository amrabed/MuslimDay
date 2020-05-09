package amrabed.android.release.evaluation.main.assessment

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Selection
import amrabed.android.release.evaluation.data.entities.Day
import amrabed.android.release.evaluation.data.entities.Task
import amrabed.android.release.evaluation.models.DayViewModel
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlinx.android.synthetic.main.task_progress.*
import org.joda.time.DateTimeConstants
import org.joda.time.Days
import org.joda.time.LocalDate
import kotlin.math.max
import kotlin.math.min

class TaskProgressFragment : Fragment() {

    private val viewModel by activityViewModels<DayViewModel>()

    private var dayList: List<Day?>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.task_progress, container, false)
        viewModel.dayList?.observe(viewLifecycleOwner, Observer { this.dayList = it })

        viewModel.selectedTask.observe(viewLifecycleOwner, Observer<Task?> { task ->
            activity?.title = task?.getTitle(context)
            weekly.adapter = PieAdapter(task, WEEK)
            monthly.adapter = PieAdapter(task, MONTH)
            yearly?.adapter = PieAdapter(task, YEAR)
        })
        return view
    }


    inner class PieAdapter(val task: Task?, private val period: Int) :
            RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        inner class PieHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val pie = LayoutInflater.from(context).inflate(R.layout.pie, parent, false)
            return PieHolder(pie)
        }

        override fun getItemCount(): Int {
            val count = dayList!!.size / dayCount[period]
            return if (count > 0) count else 1
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val label = when (position) {
                0 -> {
                    resources.getStringArray(R.array.currentPeriod)[period]
                }
                1 -> {
                    resources.getStringArray(R.array.previousPeriod)[period]
                }
                else -> {
                    val date = LocalDate(dayList?.last()?.date).minusDays(position * dayCount[period])
                    when (period) {
                        WEEK -> date.withDayOfWeek(DateTimeConstants.SUNDAY).toString("M/d")
                        MONTH -> date.toString("MMM")
                        else -> date.toString("yyyy")
                    }
                }
            }
            holder.itemView.findViewById<TextView>(R.id.label).text = label
            buildPieChart(holder.itemView.findViewById(R.id.current), position)
        }

        private fun buildPieChart(pieChart: PieChart?, position: Int) {
            pieChart?.description?.isEnabled = false
            pieChart?.legend?.isEnabled = false
            pieChart?.isRotationEnabled = false
            pieChart?.transparentCircleRadius = 0f
            pieChart?.setHoleColor(Color.TRANSPARENT)
//            pieChart?.holeRadius = 60f;
            val dataSet = PieDataSet(null, null)
            dataSet.colors = Selection.colors.map { ContextCompat.getColor(context!!, it) }
            dataSet.valueTextColor = Color.WHITE
            dataSet.valueTextSize = 12f
            dataSet.sliceSpace = 0.5f
            dataSet.valueFormatter = IValueFormatter { value: Float, _: Entry?, _: Int, _: ViewPortHandler? -> "" + if (value > minDays[period]) value.toInt() else "" }

            val counters = IntArray(4)
            getRange(position).forEach { day ->
                // Get task selection for the day
                counters[day!!.getSelection(task!!.id).toInt()]++
            }

            dataSet.values = counters.map { PieEntry(it.toFloat()) }
            pieChart!!.data = PieData(dataSet)
            pieChart.invalidate()

        }

        /**
         * Get day range for the given position, e.g. current week/month/year for position 0
         */
        private fun getRange(position: Int): List<Day?> {
            val last = dayList!!.size - 1
            val lastDay = LocalDate(dayList?.last()?.date)
            val diff = 1 + Days.daysBetween(when (period) {
                // Start of week is Sunday for many Muslim countries
                WEEK -> lastDay.withDayOfWeek(7).minusDays(7).minusWeeks(position)
                MONTH -> lastDay.withDayOfMonth(1).minusMonths(position)
                else -> lastDay.withDayOfYear(1).minusYears(position)
            }, lastDay).days

            val startDate = lastDay.minusDays(diff)
            val start = last - diff
            val end = start + when (period) {
                WEEK -> 7
                MONTH -> startDate.dayOfMonth().maximumValue
                else -> startDate.dayOfYear().maximumValue
            }
            return dayList!!.subList(max(0, start), min(end, last))
        }
    }

    companion object {
        private val dayCount = intArrayOf(7, 30, 365)
        private val minDays = intArrayOf(0, 1, 10)
        private const val WEEK = 0
        private const val MONTH = 1
        private const val YEAR = 2
    }
}