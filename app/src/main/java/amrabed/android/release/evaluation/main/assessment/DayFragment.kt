package amrabed.android.release.evaluation.main.assessment

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Selection
import amrabed.android.release.evaluation.data.entities.Day
import amrabed.android.release.evaluation.data.entities.Task
import amrabed.android.release.evaluation.models.DayViewModel
import amrabed.android.release.evaluation.models.TaskViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

/**
 * Fragment to display list of active tasks for the day
 */
class DayFragment : Fragment() {
    private val date by lazy { requireArguments().getLong(DATE) }
    private val taskViewModel by activityViewModels<TaskViewModel>()
    private val dayViewModel by activityViewModels<DayViewModel>()
    private val myViewModel by lazy {
        // Get ViewModel for the current day fragment. The date is used as the key for the ViewModel
        ViewModelProvider(activity as ViewModelStoreOwner).get(date.toString(), DayViewModel::class.java)
    }

    private lateinit var listView: RecyclerView
    private var day: Day? = null

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View? {
        listView = inflater.inflate(R.layout.list, parent, false) as RecyclerView
        listView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        myViewModel.selectedDay.observe(viewLifecycleOwner, Observer { day ->
            this.day = day
            taskViewModel.taskList?.observe(viewLifecycleOwner, Observer { taskList ->
                listView.adapter = Adapter(taskList.filter { it.isVisible(requireContext(), date) })
            })
        })
        return listView
    }

    override fun onDetach() {
        myViewModel.updateDay(day)
        super.onDetach()
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
            itemView.selection.setImageResource(day!!.getSelection(task.id).icon)
            itemView.card.elevation = 0f
            itemView.dropDown.visibility = View.GONE
            itemView.pie.setOnClickListener {
                dayViewModel.selectTask(task)
                findNavController().navigate(R.id.taskProgress)
            }
            itemView.setOnClickListener {
                toggle()
                val dropDown = itemView.dropDown
                dropDown.findViewById<Button>(R.id.done).setOnClickListener {
                    itemView.selection.setImageResource(R.drawable.ic_check)
                    day?.setSelectionAt(task.id, Selection.GOOD.value)
                    toggle()
                }
                dropDown.findViewById<Button>(R.id.neutral).setOnClickListener {
                    itemView.selection.setImageResource(R.drawable.ic_neutral)
                    day?.setSelectionAt(task.id, Selection.OK.value)
                    toggle()
                }
                dropDown.findViewById<Button>(R.id.missed).setOnClickListener {
                    itemView.selection.setImageResource(R.drawable.ic_clear)
                    day?.setSelectionAt(task.id, Selection.BAD.value)
                    toggle()
                }
                dropDown.findViewById<Button>(R.id.none).setOnClickListener {
                    itemView.selection.setImageResource(0)
                    day?.setSelectionAt(task.id, Selection.NONE.value)
                    toggle()
                }
            }
        }

        private fun toggle() {
            val isHidden = itemView.dropDown.visibility == View.GONE
            itemView.dropDown.visibility = if (isHidden) View.VISIBLE else View.GONE
            itemView.card.elevation = if (isHidden) 100f else 0f
        }
    }
}