package amrabed.android.release.evaluation.main.assessment

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Task
import amrabed.android.release.evaluation.databinding.PieBinding
import amrabed.android.release.evaluation.databinding.TaskDetailsBinding
import amrabed.android.release.evaluation.main.edit.ActiveDaysPicker
import amrabed.android.release.evaluation.main.edit.ReminderTimePicker
import amrabed.android.release.evaluation.main.edit.TitleEditor
import amrabed.android.release.evaluation.models.RecordViewModel
import amrabed.android.release.evaluation.models.TaskViewModel
import amrabed.android.release.evaluation.tools.graphs.Pie
import amrabed.android.release.evaluation.utilities.notification.TaskReminder
import amrabed.android.release.evaluation.utilities.time.DateManager
import amrabed.android.release.evaluation.utilities.time.Period
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.joda.time.DateTime
import org.joda.time.LocalTime

class TaskDetailsFragment : Fragment() {
    private val recordViewModel by activityViewModels<RecordViewModel>()
    private val taskViewModel by activityViewModels<TaskViewModel>()

    private lateinit var binding: TaskDetailsBinding
    private lateinit var task: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View =
        TaskDetailsBinding.inflate(inflater, parent, false).apply {
            taskViewModel.selectedTask.observe(viewLifecycleOwner, TaskObserver())
        }.root

    override fun onResume() {
        super.onResume()
        activity?.title = task.getTitle(requireContext())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.task, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.taskEditor) {
            TitleEditor().show(childFragmentManager, null)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    inner class TaskObserver : Observer<Task>, TimePickerDialog.OnTimeSetListener,
        ActiveDaysPicker.Listener {
        override fun onChanged(it: Task) {
            task = it
            binding.activeDays.text = getActiveDaysText()
            binding.reminder.text = getReminderText()
            binding.reminderSwitch.isChecked = task.reminder != null

            binding.activeDays.setOnClickListener {
                ActiveDaysPicker(this).show(
                    childFragmentManager,
                    null
                )
            }
            binding.reminder.setOnClickListener {
                ReminderTimePicker(this).show(
                    childFragmentManager,
                    null
                )
            }
            binding.reminderSwitch.setOnClickListener {
                if (binding.reminderSwitch.isChecked) {
                    ReminderTimePicker(this).show(childFragmentManager, null)
                } else {
                    task.reminder = null
                    TaskReminder.cancel(requireContext(), task)
                    binding.reminder.text = getReminderText()
                    taskViewModel.update(task)
                }
            }
            recordViewModel.getDayCount()?.observe(viewLifecycleOwner, { count ->
                val dayCount = count ?: 0
                binding.weekly.adapter = PieAdapter(task, Period.WEEK, dayCount)
                binding.monthly.adapter = PieAdapter(task, Period.MONTH, dayCount)
                binding.yearly.adapter = PieAdapter(task, Period.YEAR, dayCount)
            })
        }

        override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
            TaskReminder.schedule(requireContext(), task.nextReminder(LocalTime(hour, minute)))
            taskViewModel.update(task)
            binding.reminder.text = getReminderText()
            if (task.reminder != null) {
                binding.reminderSwitch.isChecked = true
                val message = getString(R.string.nextReminderSet).replace(
                    "####",
                    DateManager.getDateTimeText(requireContext(), DateTime.parse(task.reminder)),
                    true
                )
                Snackbar.make(requireView().rootView, message, Snackbar.LENGTH_LONG).show()
            }
        }

        override fun onActiveDaysSet() {
            binding.activeDays.text = getActiveDaysText()
            taskViewModel.update(task)
        }

        private fun getActiveDaysText() = when {
            task.isHidden() -> getString(R.string.inActive)
            task.activeDays.all { it } -> getString(R.string.everyDay)
            else -> resources.getStringArray(R.array.days)
                .filterIndexed { i, _ -> task.getActiveDays(requireContext())[i] }.toString()
                .removeSurrounding("[", "]")
        }

        private fun getReminderText() =
            if (task.reminder != null) DateTime.parse(task.reminder).toLocalTime()
                .toString("hh:mm a") else getString(R.string.reminderNotSet)
    }

    inner class PieAdapter(val task: Task?, private val period: Int, private val dayCount: Int) :
        RecyclerView.Adapter<PieHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            PieHolder(PieBinding.inflate(LayoutInflater.from(context), parent, false))

        override fun getItemCount() = 1 + dayCount / DAYS[period]
        override fun onBindViewHolder(holder: PieHolder, position: Int) =
            holder.bind(position, period)
    }

    inner class PieHolder(private val binding: PieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, period: Int) {
            binding.label.text = getLabel(position, period)
            val interval = DateManager.getInterval(requireContext(), position, period)
            val dayCount = DateManager.getDayCount(interval.start, period)
            recordViewModel.taskHistoryByDateRange(task.id, interval)
                ?.observe(viewLifecycleOwner, {
                    Pie(requireContext(), it, period, position, dayCount).show(binding.pie)
                })
        }

        private fun getLabel(position: Int, period: Int): String {
            return when (position) {
                0 -> resources.getStringArray(R.array.currentPeriod)[period]
                1 -> resources.getStringArray(R.array.previousPeriod)[period]
                else -> DateManager.getIntervalStart(requireContext(), position, period)
            }
        }
    }
}

private val DAYS = intArrayOf(7, 30, 365)