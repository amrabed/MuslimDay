package amrabed.android.release.evaluation.main.edit

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Task
import amrabed.android.release.evaluation.models.TaskViewModel
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

/**
 * Dialog with EditText for editing task title
 */
class TitleEditor : DialogFragment() {
    private val viewModel by activityViewModels<TaskViewModel>()
    private lateinit var task: Task

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        viewModel.selectedTask.observe(activity as LifecycleOwner, Observer { task = it })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.title_editor, null)
        val titleEditor = view.findViewById<EditText>(R.id.titleEditor)
        val currentTitle = task.getTitle(requireContext())
        titleEditor.setText(currentTitle)

        return AlertDialog.Builder(context)
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok) { _, _ ->
                    if (TextUtils.isEmpty(titleEditor.text.toString())) {
                        Snackbar.make(view.rootView, R.string.emptyName, Snackbar.LENGTH_LONG).show()
                    } else {
                        val title = titleEditor.text.toString().trim()
                        if (title != currentTitle) {
                            task.title = title
                            viewModel.update(task)
                        }
                    }
                }.create()
    }
}