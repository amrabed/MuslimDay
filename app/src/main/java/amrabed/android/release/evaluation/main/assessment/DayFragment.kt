package amrabed.android.release.evaluation.main.assessment

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Record
import amrabed.android.release.evaluation.core.Status
import amrabed.android.release.evaluation.core.Task
import amrabed.android.release.evaluation.models.RecordViewModel
import amrabed.android.release.evaluation.models.TaskViewModel
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.selection.view.*

/**
 * Fragment to display list of active tasks for the day
 */
class DayFragment : Fragment() {
    private val date by lazy { requireArguments().getLong(DATE) }
    private val taskViewModel by activityViewModels<TaskViewModel>()
    private val recordViewModel by activityViewModels<RecordViewModel>()
    private val pageViewModel by lazy {
        // Get ViewModel for the current day fragment. The date is used as the key for the ViewModel
        ViewModelProvider(activity as ViewModelStoreOwner).get(date.toString(), RecordViewModel::class.java)
    }

    private var recordList = mutableSetOf<Record>()

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View? {
        val listView = inflater.inflate(R.layout.list, parent, false) as RecyclerView
        listView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        taskViewModel.taskList?.observe(viewLifecycleOwner, Observer { taskList ->
            pageViewModel.getDayList(date)?.observe(viewLifecycleOwner, Observer { recordList ->
                this.recordList = recordList.toMutableSet()
                listView.adapter = Adapter(taskList.filter { it.isVisible(requireContext(), date) })
            })
        })
        return listView
    }

    override fun onDetach() {
        super.onDetach()
        recordList.forEach { record -> recordViewModel.updateRecord(record) }
    }

    private inner class Adapter(val list: List<Task>) : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false))
        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])
        override fun getItemCount() = list.size
    }

    private inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view), NoteEditor.Listener {
        fun bind(task: Task) {
            val record = try {
                recordList.first { it.task == task.id }
            } catch (e: NoSuchElementException) {
                Record(date, task.id, Status.NONE.value)
            }
            view.text.text = task.getTitle(requireContext())
            view.selection.setImageResource(Status.of(record.selection).icon)
            view.card.elevation = 0f
            view.dropDown.visibility = View.GONE
            view.note.imageAlpha = if (record.note != null) 0xff else 0x2a
            view.note.setOnClickListener { NoteEditor(record, this).show(childFragmentManager, null) }
            view.dropDown.done.setOnClickListener { updateStatus(record, Status.DONE) }
            view.dropDown.partial.setOnClickListener { updateStatus(record, Status.PARTIAL) }
            view.dropDown.neutral.setOnClickListener { updateStatus(record, Status.EXCUSE) }
            view.dropDown.missed.setOnClickListener { updateStatus(record, Status.MISSED) }
            view.dropDown.none.setOnClickListener { updateStatus(record, Status.NONE) }
            view.setOnClickListener { toggle() }
            view.pie.setOnClickListener {
                taskViewModel.select(task)
                findNavController().navigate(R.id.taskDetails, bundleOf(Pair(TASK, task)))
            }
        }

        private fun updateStatus(record: Record, status: Status) {
            view.selection.setImageResource(status.icon)
            record.selection = status.value
            recordList.add(record)
            toggle()
        }

        private fun toggle() {
            val isHidden = view.dropDown.visibility == View.GONE
            view.dropDown.visibility = if (isHidden) View.VISIBLE else View.GONE
            view.card.elevation = if (isHidden) 50f else 0f
        }

        override fun onNoteSet(record: Record, note: String) {
            record.note = if (TextUtils.isEmpty(note)) null else note
            view.note.imageAlpha = if (record.note != null) 0xff else 0x2a
            recordList.add(record)
        }
    }
}

const val TASK = "task"