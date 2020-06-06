package amrabed.android.release.evaluation.main.edit

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Task
import amrabed.android.release.evaluation.models.TaskViewModel
import amrabed.android.release.evaluation.utilities.notification.TaskReminder
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.edit_fragment.*
import org.joda.time.LocalTime

class TaskFragment : Fragment(), TimePickerDialog.OnTimeSetListener, ActiveDaysPicker.Listener {
    private val model by activityViewModels<TaskViewModel>()
    private lateinit var task: Task

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View {
        return inflater.inflate(R.layout.edit_fragment, parent, false).also {
            model.selectedTask.observe(viewLifecycleOwner, Observer {
                task = it
                val currentTitle = task.getTitle(requireContext())
                title.setText(currentTitle)
                reminderSwitch.isChecked = task.reminder != null
                days.setOnClickListener { ActiveDaysPicker(this).show(childFragmentManager, null) }
                reminderSwitch.setOnClickListener { setReminder() }
                reminder.setOnClickListener { setReminder() }
                done.setOnClickListener { save(currentTitle) }
            })
        }
    }

    private fun setReminder() {
        if (reminderSwitch.isChecked) {
            ReminderTimePicker(this).show(childFragmentManager, null)
        } else {
            task.reminder = null
            TaskReminder.cancel(requireContext(), task)
        }
    }

    private fun save(currentTitle: String?) {
        if (TextUtils.isEmpty(title.text)) {
            Snackbar.make(requireView().rootView, R.string.emptyName, Snackbar.LENGTH_LONG).show()
        } else {
            if (currentTitle != title.text.toString().trim()) {
                task.title = title.text.toString()
            }
            if (TextUtils.isEmpty(currentTitle)) {
                model.add(task)
                findNavController().navigate(R.id.listEditor, bundleOf(Pair(NEW_ITEM_ADDED, true)))
            } else {
                model.update(task)
                findNavController().popBackStack()
            }
        }
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        TaskReminder.schedule(requireContext(), task.nextReminder(LocalTime(hour, minute)))
        reminderSwitch.isChecked = true
    }

    override fun onActiveDaysSet() {
    }
}

const val NEW_ITEM_ADDED = "position"