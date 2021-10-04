package amrabed.android.release.evaluation.main.assessment

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Record
import amrabed.android.release.evaluation.core.Status
import amrabed.android.release.evaluation.core.Task
import amrabed.android.release.evaluation.databinding.ListItemBinding
import amrabed.android.release.evaluation.models.RecordViewModel
import amrabed.android.release.evaluation.models.TaskViewModel
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

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

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View {
        val listView = inflater.inflate(R.layout.list, parent, false) as RecyclerView
        listView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        taskViewModel.taskList?.observe(viewLifecycleOwner, { taskList ->
            pageViewModel.getDayList(date)?.observe(viewLifecycleOwner, { recordList ->
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
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(ListItemBinding.inflate(LayoutInflater.from(context), parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(list[position])

        override fun getItemCount() = list.size
    }

    inner class ViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root), NoteEditor.Listener {

        fun bind(task: Task) {
            binding.task = task
            binding.record = try {
                recordList.first { it.task == task.id }
            } catch (e: NoSuchElementException) {
                Record(date, task.id, Status.NONE.value)
            }
        }

        fun showTaskDetails(task: Task) {
            taskViewModel.select(task)
            findNavController().navigate(R.id.taskDetails, bundleOf(Pair(TASK, task)))
        }

        fun showNoteEditor(record: Record) = NoteEditor(record, this).show(childFragmentManager, null)

        fun updateStatus(record: Record, status: Status) {
            record.selection = status.value
            setStatusIcon(binding.statusIcon, record)
            recordList.add(record)
            toggle()
        }

        fun toggle() {
            val isHidden = binding.dropDown.root.visibility == View.GONE
            binding.dropDown.root.visibility = if (isHidden) View.VISIBLE else View.GONE
            binding.card.elevation = if (isHidden) 50f else 0f
        }

        override fun onNoteSet(record: Record, note: String) {
            record.note = if (TextUtils.isEmpty(note)) null else note
            recordList.add(record)
        }
    }
}

@BindingAdapter("android:src")
fun setStatusIcon(icon: ImageView, record: Record) {
    icon.setImageResource(Status.of(record.selection).icon)
}

const val TASK = "task"