package amrabed.android.release.evaluation.main.edit

import amrabed.android.release.evaluation.models.TaskViewModel
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import org.joda.time.DateTime
import org.joda.time.LocalTime

class ReminderTimePicker(private val listener: TimePickerDialog.OnTimeSetListener) : DialogFragment() {
    private val model by activityViewModels<TaskViewModel>()
    private lateinit var reminder: LocalTime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.selectedTask.observe(activity as LifecycleOwner) { task ->
            reminder = if (task.reminder != null) DateTime.parse(task.reminder)
                .toLocalTime() else LocalTime()
        }
    }

    override fun onCreateDialog(state: Bundle?) = TimePickerDialog(context, listener, reminder.hourOfDay, reminder.minuteOfHour, false)
}