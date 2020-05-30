package amrabed.android.release.evaluation.main.assessment

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Task
import amrabed.android.release.evaluation.models.DayViewModel
import amrabed.android.release.evaluation.tools.graphs.Pie
import amrabed.android.release.evaluation.utilities.time.DateManager
import amrabed.android.release.evaluation.utilities.time.Period
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

class TaskProgressFragment : Fragment() {
    private val viewModel by activityViewModels<DayViewModel>()
    private val task by lazy { requireArguments().getParcelable(TASK) as Task? }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.task_progress, container, false).apply {
            viewModel.getDayCount()?.observe(viewLifecycleOwner, Observer {
                val dayCount = it ?: 0
                weekly.adapter = PieAdapter(task, Period.WEEK, dayCount)
                monthly.adapter = PieAdapter(task, Period.MONTH, dayCount)
                yearly?.adapter = PieAdapter(task, Period.YEAR, dayCount)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.title = task?.getTitle(requireContext())
    }

    inner class PieAdapter(val task: Task?, private val period: Int, private val dayCount: Int) :
            RecyclerView.Adapter<PieHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PieHolder {
            val pie = LayoutInflater.from(context).inflate(R.layout.pie, parent, false)
            return PieHolder(pie)
        }

        override fun getItemCount(): Int {
            return 1 + dayCount / days[period]
        }

        override fun onBindViewHolder(holder: PieHolder, position: Int) {
            holder.itemView.label.text = getLabel(position)
            val interval = DateManager.getInterval(requireContext(), position, period)
            val dayCount = DateManager.getDayCount(interval.start, period)
            viewModel.taskHistoryByDateRange(task!!.id, interval)?.observe(viewLifecycleOwner, Observer {
                Pie(requireContext(), it, period, position, dayCount).show(holder.itemView.pie)
            })
        }

        private fun getLabel(position: Int): String {
            return when (position) {
                0 -> resources.getStringArray(R.array.currentPeriod)[period]
                1 -> resources.getStringArray(R.array.previousPeriod)[period]
                else -> DateManager.getIntervalStart(requireContext(), position, period)
            }
        }
    }

    inner class PieHolder(view: View) : RecyclerView.ViewHolder(view)

    companion object {
        private val days = intArrayOf(7, 30, 365)
    }
}