package amrabed.android.release.evaluation.main.assessment

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Record
import amrabed.android.release.evaluation.core.Selection
import amrabed.android.release.evaluation.core.Task
import amrabed.android.release.evaluation.models.RecordViewModel
import amrabed.android.release.evaluation.models.TaskViewModel
import android.os.Bundle
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
    private val myViewModel by lazy {
        // Get ViewModel for the current day fragment. The date is used as the key for the ViewModel
        ViewModelProvider(activity as ViewModelStoreOwner).get(date.toString(), RecordViewModel::class.java)
    }

    private lateinit var listView: RecyclerView
    private var taskSelections = hashMapOf<String, Selection>()

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View? {
        listView = inflater.inflate(R.layout.list, parent, false) as RecyclerView
        listView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        myViewModel.getDayList(date)?.observe(viewLifecycleOwner, Observer { list ->
            list.map { day -> taskSelections.put(day.task, Selection.of(day.selection)) }
            taskViewModel.taskList?.observe(viewLifecycleOwner, Observer { taskList ->
                listView.adapter = Adapter(taskList.filter { it.isVisible(requireContext(), date) })
            })
        })
        return listView
    }

    private inner class Adapter(val list: List<Task>) : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(task: Task) {
            itemView.text.text = task.getTitle(requireContext())
            itemView.selection.setImageResource(taskSelections[task.id]?.icon
                    ?: Selection.NONE.icon)
            itemView.card.elevation = 0f
            itemView.dropDown.visibility = View.GONE
            itemView.dropDown.done.setOnClickListener { select(task.id, Selection.GOOD) }
            itemView.dropDown.neutral.setOnClickListener { select(task.id, Selection.OK) }
            itemView.dropDown.missed.setOnClickListener { select(task.id, Selection.BAD) }
            itemView.dropDown.none.setOnClickListener { select(task.id, Selection.NONE) }
            itemView.setOnClickListener { toggle() }
            itemView.pie.setOnClickListener {
                taskViewModel.select(task)
                findNavController().navigate(R.id.taskDetails, bundleOf(Pair(TASK, task)))
            }
        }

        private fun select(id: String, selection: Selection) {
            itemView.selection.setImageResource(selection.icon)
            recordViewModel.updateRecord(Record(date, id, selection.value, null))
            toggle()
        }

        private fun toggle() {
            val isHidden = itemView.dropDown.visibility == View.GONE
            itemView.dropDown.visibility = if (isHidden) View.VISIBLE else View.GONE
            itemView.card.elevation = if (isHidden) 50f else 0f
        }
    }
}

const val TASK = "task"