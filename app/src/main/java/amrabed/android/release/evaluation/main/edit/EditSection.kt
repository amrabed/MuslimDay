package amrabed.android.release.evaluation.main.edit

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.data.converters.ActiveDaysConverter
import amrabed.android.release.evaluation.data.entities.Task
import amrabed.android.release.evaluation.models.TaskViewModel
import amrabed.android.release.evaluation.tools.ItemTouchHandler
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.edit_list.*
import kotlinx.android.synthetic.main.editor_item.view.*
import kotlin.math.max
import kotlin.math.min

class EditSection : Fragment() {
    private val model by activityViewModels<TaskViewModel>()
    private lateinit var taskList: MutableList<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.edit_list, container, false)
        model.taskList?.observe(viewLifecycleOwner, Observer {
            taskList = it
            listView.adapter = Adapter(it)
            listView?.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            if (requireArguments().getBoolean(NEW_ITEM_ADDED)) {
                Snackbar.make(listView.rootView, R.string.added, Snackbar.LENGTH_LONG).show()
                listView?.smoothScrollToPosition(it.lastIndex)
            }
        })
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                val position = taskList.size
                model.select(Task(index = position, title = ""))
                findNavController().navigate(R.id.taskEditor, bundleOf(Pair(NEW_ITEM_ADDED, position)))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private inner class Adapter(val list: MutableList<Task>) :
            RecyclerView.Adapter<Adapter.ViewHolder>(), ItemTouchHandler.Listener {
        private inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

        private val touchHelper = ItemTouchHelper(ItemTouchHandler(requireContext(), this))

        init {
            touchHelper.attachToRecyclerView(listView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.editor_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val task = list[position]
            val isHiddenTask = !task.activeDays.reduce { a, b -> a or b }
            val isDisabledTask = task.guideEntry in listOf(R.raw.fasting, R.raw.cong)

            holder.itemView.content.text = task.getTitle(context)

            val textColor = ContextCompat.getColor(requireContext(), if (isHiddenTask) android.R.color.darker_gray else android.R.color.black)
            holder.itemView.content.setTextColor(textColor)
            holder.itemView.hidden.visibility = if (isHiddenTask) View.VISIBLE else View.GONE
            if (isDisabledTask) {
                holder.itemView.setOnClickListener {
                    Snackbar.make(listView.rootView, R.string.settingsItem, Snackbar.LENGTH_LONG).show()
                }
            } else {
                holder.itemView.setOnClickListener {
                    model.select(task)
                    findNavController().navigate(R.id.taskEditor)
                }
            }

            holder.itemView.reorderHandle.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    onDrag(holder)
                }
                false
            }
        }

        override fun onDrag(holder: RecyclerView.ViewHolder) {
            touchHelper.startDrag(holder)
        }

        override fun onItemMoved(source: RecyclerView.ViewHolder, destination: RecyclerView.ViewHolder) {
            val from = source.adapterPosition
            val to = destination.adapterPosition
            if (from != to) {
                list.add(to, list.removeAt(from).apply { index = to })
                // All items are already moved to their correct final position in list
                // Update current index of tasks between initial and final positions
                for (i in min(to, from)..max(to, from)) {
                    model.move(list[i].apply { index = i })
                }
                notifyItemMoved(from, to)
            }
        }

        override fun onItemRemoved(holder: RecyclerView.ViewHolder) {
            val position = holder.adapterPosition
            val item = list[position]
            if (item.getTitle(context) != requireContext().getString(R.string.fastingTitle)) {
                model.delete(list[position])
                list.removeAt(position)
            }
            notifyItemRemoved(position)
            Snackbar.make(listView.rootView, R.string.deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) {
                        list.add(position, item)
                        model.undo()
                        notifyItemInserted(position)
                    }
                    .show()
        }

        override fun onItemHidden(holder: RecyclerView.ViewHolder) {
            val position = holder.adapterPosition
            val b: Byte = 0
            list[position].activeDays = ActiveDaysConverter().setActiveDays(b)
            model.update(list[position])
            notifyItemChanged(position)
            Snackbar.make(listView.rootView, R.string.hidden, Snackbar.LENGTH_LONG).show()
        }
    }
}