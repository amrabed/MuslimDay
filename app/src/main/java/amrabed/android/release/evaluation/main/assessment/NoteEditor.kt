package amrabed.android.release.evaluation.main.assessment

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Record
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment

/**
 * Dialog with EditText for editing task note
 */
class NoteEditor(val record: Record, private val listener: Listener) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.editor_dialog, null)
        val editor = view.findViewById<EditText>(R.id.editor)
        editor.setHint(R.string.note)
        editor.setText(record.note)

        return AlertDialog.Builder(context)
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok) { _, _ -> listener.onNoteSet(record, editor.text.toString()) }
                .create()
    }

    interface Listener {
        fun onNoteSet(record: Record, note: String)
    }
}