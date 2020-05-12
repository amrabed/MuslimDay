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
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

/**
 * Fragment to display list of active tasks for the day
 */
class DayFragment : Fragment() {
    private val viewModel by activityViewModels<DayViewModel>()

    private lateinit var listView: RecyclerView
    private var day: Day? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        listView = inflater.inflate(R.layout.list, container, false) as RecyclerView
        viewModel.selectedDay.observe(viewLifecycleOwner, Observer { day: Day? ->
            this.day = day
            populateList(day)
        })

        if (savedInstanceState != null) {
            listView.layoutManager?.onRestoreInstanceState(savedInstanceState.getParcelable(POSITION))
        }
        listView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        return listView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val layoutManager = listView.layoutManager
        if (layoutManager != null) {
            outState.putParcelable(POSITION, layoutManager.onSaveInstanceState())
        }
    }

    override fun onDetach() {
        viewModel.updateDay(day)
        super.onDetach()
    }

    private fun populateList(day: Day?) {
        ViewModelProvider(activity as ViewModelStoreOwner).get(TaskViewModel::class.java).taskList
                ?.observe(viewLifecycleOwner, Observer { list ->
                        val iterator = list.iterator()
                        while (iterator.hasNext()) {
                            if (!iterator.next().isVisible(context, day)) {
                                iterator.remove()
                            }
                        }
                        listView.adapter = Adapter(list)
                })
    }

    private inner class Adapter(var list: MutableList<Task>) : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val task = list[position]
            if (task.isVisible(context, day)) {
                holder.itemView.setOnClickListener { view: View ->
                    val id = list[position].id
                    val selection = Selection(day!!.getSelection(id)).next()
                    day!!.setSelectionAt(id, selection.value)
                    (view.findViewById<View>(R.id.selection) as ImageView).setImageResource(selection.icon)
                }
                val title = task.getTitle(context)
                holder.textView.text = title
                holder.selection
                        .setImageResource(Selection.getIcon(day!!.getSelection(task.id)))
                holder.pie.setOnClickListener {
                    viewModel.selectTask(task)
                    findNavController().navigate(R.id.taskProgress)
                }
            } else {
                holder.itemView.systemUiVisibility = View.GONE
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val selection: ImageView = view.findViewById(R.id.selection)
        val textView: TextView = view.findViewById(R.id.text)
        val pie: ImageView = view.findViewById(R.id.pie)
    }

    companion object {
        private const val POSITION = "Position"
    }
}