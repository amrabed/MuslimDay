package amrabed.android.release.evaluation.main.assessment

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.data.entities.Day
import amrabed.android.release.evaluation.data.entities.Task
import amrabed.android.release.evaluation.models.DayViewModel
import amrabed.android.release.evaluation.tools.graphs.Pie
import amrabed.android.release.evaluation.tools.graphs.Plot
import amrabed.android.release.evaluation.utilities.time.DateManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.pie.view.*
import kotlinx.android.synthetic.main.task_progress.*
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants

class TaskProgressFragment : Fragment() {

    private val viewModel by activityViewModels<DayViewModel>()

    private lateinit var dayList: List<Day>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.task_progress, container, false)
        viewModel.dayList?.observe(viewLifecycleOwner, Observer { this.dayList = it })

        viewModel.selectedTask.observe(viewLifecycleOwner, Observer<Task?> { task ->
            activity?.title = task?.getTitle(requireContext())
            weekly.adapter = PieAdapter(task, Plot.Period.WEEK)
            monthly.adapter = PieAdapter(task, Plot.Period.MONTH)
            yearly?.adapter = PieAdapter(task, Plot.Period.YEAR)
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
            val count = dayList.size / dayCount[period]
            return if (count > 0) count else 1
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.label.text = getLabel(position)
            Pie(requireContext(), dayList, task!!.id, period, position).show(holder.itemView.pie)
        }

        private fun getLabel(position: Int): String {
            return when (position) {
                0 -> resources.getStringArray(R.array.currentPeriod)[period]
                1 -> resources.getStringArray(R.array.previousPeriod)[period]
                else -> {
                    val date = DateTime(dayList.last().date).minusDays(position * dayCount[period])
                    when (period) {
                        Plot.Period.WEEK -> DateManager(requireContext()).getDate(date.withDayOfWeek(DateTimeConstants.SUNDAY).minusDays(7), "MMM d")
                        Plot.Period.MONTH -> DateManager(requireContext()).getMonth(date)
                        else -> DateManager(requireContext()).getYear(date)
                    }
                }
            }
        }
    }

    companion object {
        private val dayCount = intArrayOf(7, 30, 365)
    }
}