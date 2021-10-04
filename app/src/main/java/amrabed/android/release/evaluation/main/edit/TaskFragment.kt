package amrabed.android.release.evaluation.main.edit

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Task
import amrabed.android.release.evaluation.databinding.EditFragmentBinding
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
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import org.joda.time.LocalTime

class TaskFragment : Fragment(), TimePickerDialog.OnTimeSetListener, ActiveDaysPicker.Listener {
    private val model by activityViewModels<TaskViewModel>()
    private lateinit var task: Task
    private lateinit var binding: EditFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View {
        binding = EditFragmentBinding.inflate(inflater, parent, false)
        return binding.root.also {
            model.selectedTask.observe(viewLifecycleOwner, {
                task = it
                val currentTitle = task.getTitle(requireContext())
                binding.title.setText(currentTitle)
                binding.reminderSwitch.isChecked = task.reminder != null
                binding.days.setOnClickListener {
                    ActiveDaysPicker(this).show(childFragmentManager, null)
                }
                binding.reminderSwitch.setOnClickListener { setReminder() }
                binding.reminder.setOnClickListener { setReminder() }
                binding.done.setOnClickListener { save(currentTitle) }
            })
        }
    }

    private fun setReminder() {
        if (binding.reminderSwitch.isChecked) {
            ReminderTimePicker(this).show(childFragmentManager, null)
        } else {
            task.reminder = null
            TaskReminder.cancel(requireContext(), task)
        }
    }

    private fun save(currentTitle: String?) {
        if (TextUtils.isEmpty(binding.title.text)) {
            Snackbar.make(requireView().rootView, R.string.emptyName, Snackbar.LENGTH_LONG).show()
        } else {
            if (currentTitle != binding.title.text.toString().trim()) {
                task.title = binding.title.text.toString()
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
        binding.reminderSwitch.isChecked = true
    }

    override fun onActiveDaysSet() {
    }
}

const val NEW_ITEM_ADDED = "position"