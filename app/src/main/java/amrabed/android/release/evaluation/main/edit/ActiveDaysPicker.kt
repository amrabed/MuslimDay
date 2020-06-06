package amrabed.android.release.evaluation.main.edit

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Task
import amrabed.android.release.evaluation.models.TaskViewModel
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

class ActiveDaysPicker(private val listener: Listener) : DialogFragment() {
    private val viewModel by activityViewModels<TaskViewModel>()
    private lateinit var task: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.selectedTask.observe(activity as LifecycleOwner, Observer { task = it })
    }

    override fun onCreateDialog(state: Bundle?): Dialog {
        val selected = task.getActiveDays(requireContext())
        return AlertDialog.Builder(context)
                .setTitle(R.string.selectDaysTitle)
                .setPositiveButton(R.string.ok) { _, _ -> listener.onActiveDaysSet() }
                .setMultiChoiceItems(R.array.days, selected) { _, which, isChecked ->
                    val day = requireContext().resources.getStringArray(R.array.dayValues)[which].toInt()
                    task.setActiveDay(day, isChecked)
                }
                .create()
    }

    interface Listener {
        fun onActiveDaysSet()
    }
}