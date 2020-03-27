package amrabed.android.release.evaluation.eval

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Selection
import amrabed.android.release.evaluation.data.entities.Day
import amrabed.android.release.evaluation.data.entities.Task
import amrabed.android.release.evaluation.data.models.DayViewModel
import amrabed.android.release.evaluation.data.models.TaskViewModel
import amrabed.android.release.evaluation.progress.item.ProgressFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Fragment to display list of active tasks for the day
 */
class DayFragment : Fragment() {
    private var listView: RecyclerView? = null
    private var day: Day? = null
    private var model: DayViewModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.day_list, container, false)
        val adapter = Adapter(ArrayList())
        listView = view as RecyclerView
        listView!!.adapter = adapter
        if (savedInstanceState != null) {
            val layoutManager = listView!!.layoutManager
            layoutManager?.onRestoreInstanceState(savedInstanceState.getParcelable(POSITION))
        }
        val activity = activity
        if (activity != null) {
            activity.setTitle(R.string.evaluation)
            listView!!.addItemDecoration(DividerItemDecoration(activity,
                    DividerItemDecoration.VERTICAL))
            if (arguments != null) {
                model = ViewModelProvider(activity)
                        .get(arguments!!.getInt(POSITION).toString(), DayViewModel::class.java)
                model?.selected?.observe(viewLifecycleOwner, Observer { day: Day? ->
                    this.day = day
                    adapter.updateList()
                })
            }
        }
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (listView != null) {
            val layoutManager = listView!!.layoutManager
            if (layoutManager != null) {
                outState.putParcelable(POSITION, layoutManager.onSaveInstanceState())
            }
        }
    }

    override fun onDetach() {
        if (model != null) {
            model!!.updateDay(day)
        }
        super.onDetach()
    }

    private fun loadFragment(fragment: Fragment) {
        val activity = activity
        activity?.supportFragmentManager?.beginTransaction()?.addToBackStack(null)?.replace(R.id.content, fragment)?.commit()
    }

    private inner class Adapter(list: MutableList<Task?>) : RecyclerView.Adapter<ViewHolder>() {
        private var list: MutableList<Task?>?
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val task = list!![position]
            if (task != null && task.isVisible(context, day)) {
                holder.itemView.setOnClickListener { view: View ->
                    val id = list!![position]!!.id
                    val selection = Selection(day!!.getSelection(id)).next()
                    day!!.setSelectionAt(id, selection.value)
                    (view.findViewById<View>(R.id.selection) as ImageView).setImageResource(selection.icon)
                }
                val title = task.getTitle(context)
                holder.textView.text = title
                holder.selection
                        .setImageResource(Selection.getIcon(day!!.getSelection(task.id)))
                holder.pie.setOnClickListener { loadFragment(ProgressFragment.newInstance(task.id, task.getTitle(this@DayFragment.context))) }
            } else {
                holder.itemView.systemUiVisibility = View.GONE
            }
        }

        override fun getItemCount(): Int {
            return list!!.size
        }

        fun updateList() {
            ViewModelProvider(activity as ViewModelStoreOwner).get(TaskViewModel::class.java).taskList
                    ?.observe(this@DayFragment, Observer { taskList: MutableList<Task?>? ->
                        list = taskList
                        val iterator = list!!.iterator()
                        while (iterator.hasNext()) {
                            if (!iterator.next()!!.isVisible(context, day)) {
                                iterator.remove()
                            }
                        }
                        notifyDataSetChanged()
                    })
        }

        init {
            this.list = list
        }
    }

    private inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val selection: ImageView = view.findViewById(R.id.selection)
        val textView: TextView = view.findViewById(R.id.text)
        val pie: ImageView = view.findViewById(R.id.pie)
    }

    companion object {
        private const val POSITION = "Position"
        fun newInstance(position: Int): DayFragment {
            val args = Bundle()
            args.putInt(POSITION, position)
            val fragment = DayFragment()
            fragment.arguments = args
            return fragment
        }
    }
}