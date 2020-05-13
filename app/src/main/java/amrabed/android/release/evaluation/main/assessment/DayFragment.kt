package amrabed.android.release.evaluation.main.assessment

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.data.entities.Day
import amrabed.android.release.evaluation.data.entities.Task
import amrabed.android.release.evaluation.models.DayViewModel
import amrabed.android.release.evaluation.models.TaskViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

/**
 * Fragment to display list of active tasks for the day
 */
class DayFragment : Fragment() {
    private val dayViewModel by activityViewModels<DayViewModel>()
    private val taskViewModel by activityViewModels<TaskViewModel>()

    private val date by lazy { requireArguments().getLong(DATE) }
    private lateinit var day: Day

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View? {
        val listView = inflater.inflate(R.layout.list, parent, false) as RecyclerView
        listView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        taskViewModel.taskList?.observe(viewLifecycleOwner, Observer { taskList ->
            dayViewModel.selectedDay.observe(viewLifecycleOwner, Observer { day ->
                this.day = day
                listView.adapter = Adapter(taskList.filter { it.isVisible(context, date) })
            })
        })
        return listView
    }

    private inner class Adapter(var list: List<Task>) : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val view = holder.itemView
            val task = list[position]
            view.setOnClickListener {
                val id = list[position].id
                val selection = day.getSelection(id).next()
                day.setSelectionAt(id, selection.value)
                task.history[date] = selection.value
                view.selection.setImageResource(selection.icon)
                dayViewModel.updateDay(day)
            }
            val title = task.getTitle(context)
            view.text.text = title
            view.selection.setImageResource(day.getSelection(task.id).icon)
            view.pie.setOnClickListener {
                dayViewModel.selectTask(task)
                findNavController().navigate(R.id.taskProgress)
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}