package amrabed.android.release.evaluation.main.edit

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.data.entities.Task
import amrabed.android.release.evaluation.models.TaskViewModel
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.edit_fragment.*

class TaskFragment : Fragment() {
    private val model by activityViewModels<TaskViewModel>()

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.edit_fragment, parent, false)
        model.selected.observe(viewLifecycleOwner, Observer { task ->
            val name = task?.getTitle(requireContext())
            title.setText(name)
            days.setOnClickListener { setActiveDays(task) }
            reminder.setOnClickListener {
                reminderSwitch.toggle()
                setReminder()
            }
            reminderSwitch.setOnClickListener { setReminder() }
            done.setOnClickListener {
                if (TextUtils.isEmpty(title.text)) {
                    Snackbar.make(view.rootView, R.string.emptyName, Snackbar.LENGTH_LONG).show()
                } else {
                    task.title = title.text.toString()
                    if (TextUtils.isEmpty(name)) {
                        model.add(task)
                        findNavController().navigate(R.id.edit, bundleOf(Pair(NEW_ITEM_ADDED, true)))
                    } else {
                        model.update(task)
                        findNavController().navigate(R.id.edit)
                    }
                }
            }
        })
        return view
    }

    private fun setReminder() {
        if (reminderSwitch.isChecked) {
            val listener = TimePickerDialog.OnTimeSetListener { _, _, _ ->
                //ToDo: schedule reminder notification
            }
            TimePickerDialog(context, listener, 0, 0, false).apply {
                setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok)) { _, _ -> dismiss() }
                setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel)) { _, _ -> reminderSwitch.isChecked = false }
                show()
            }
        }
    }

    private fun setActiveDays(task: Task) {
        val selected = task.getActiveDays(requireContext().resources.getInteger(R.integer.dayShift))
        AlertDialog.Builder(context)
                .setTitle(R.string.selectDaysTitle)
                .setMultiChoiceItems(R.array.days, selected
                ) { _, which, isChecked ->
                    val day = requireContext().resources.getStringArray(R.array.dayValues)[which].toInt()
                    task.setActiveDay(day, isChecked)
                }
                .setPositiveButton(R.string.ok, null)
                .create().show()
    }
}

internal const val NEW_ITEM_ADDED = "position"