package amrabed.android.release.evaluation.main.edit

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Task
import amrabed.android.release.evaluation.databinding.EditListBinding
import amrabed.android.release.evaluation.databinding.EditorItemBinding
import amrabed.android.release.evaluation.models.TaskViewModel
import amrabed.android.release.evaluation.tools.ItemTouchHandler
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlin.math.max
import kotlin.math.min

class EditSection : Fragment() {
    private val model by activityViewModels<TaskViewModel>()
    private lateinit var taskList: MutableList<Task>
    private lateinit var binding: EditListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        EditListBinding.inflate(inflater, container, false).apply {
            binding = this
            model.taskList?.observe(viewLifecycleOwner) {
                taskList = it
                listView.adapter = Adapter(it)
                listView.addItemDecoration(
                    DividerItemDecoration(
                        context,
                        DividerItemDecoration.VERTICAL
                    )
                )
                if (requireArguments().getBoolean(NEW_ITEM_ADDED)) {
                    showMessage(R.string.added)
                    listView.smoothScrollToPosition(it.lastIndex)
                }
            }
        }.root

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

    private fun showMessage(stringRes: Int) {
        Snackbar.make(binding.listView.rootView, stringRes, Snackbar.LENGTH_LONG).show()
    }

    private inner class Adapter(val list: MutableList<Task>) :
            RecyclerView.Adapter<Adapter.ViewHolder>(), ItemTouchHandler.Listener {

        private val touchHelper = ItemTouchHelper(ItemTouchHandler(requireContext(), this))

        init {
            touchHelper.attachToRecyclerView(binding.listView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(EditorItemBinding.inflate(LayoutInflater.from(context), parent, false))

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun onDrag(holder: RecyclerView.ViewHolder) {
            touchHelper.startDrag(holder)
        }

        override fun onItemMoved(source: RecyclerView.ViewHolder, destination: RecyclerView.ViewHolder) {
            val from = source.bindingAdapterPosition
            val to = destination.bindingAdapterPosition
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
            val position = holder.bindingAdapterPosition
            val task = list[position]
            if (task.isControlledBySettings()) {
                showMessage(R.string.settingsItem)
                return
            }
            model.delete(list[position])
            list.removeAt(position)
            notifyItemRemoved(position)
            Snackbar.make(binding.listView.rootView, R.string.deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) {
                        list.add(position, task)
                        model.undo()
                        notifyItemInserted(position)
                    }
                    .show()
        }

        override fun onItemHidden(holder: RecyclerView.ViewHolder) {
            val position = holder.bindingAdapterPosition
            list[position].hide()
            model.update(list[position])
            notifyItemChanged(position)
            showMessage(R.string.hidden)
        }

        private inner class ViewHolder(private val binding: EditorItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(task: Task) {
                binding.content.text = task.getTitle(requireContext())

                val textColor = ContextCompat.getColor(
                    requireContext(),
                    if (task.isHidden()) android.R.color.darker_gray else android.R.color.black
                )
                binding.content.setTextColor(textColor)
                binding.hidden.visibility = if (task.isHidden()) View.VISIBLE else View.GONE
                binding.hidden.setOnClickListener {
                    model.select(task)
                    ActiveDaysPicker(object : ActiveDaysPicker.Listener {
                        override fun onActiveDaysSet() {
                            model.update(task)
                        }
                    }).show(childFragmentManager, null)
                }
                itemView.setOnClickListener {
                    if (task.isControlledBySettings()) {
                        showMessage(R.string.settingsItem)
                    } else {
                        model.select(task)
                        findNavController().navigate(R.id.taskEditor)
                    }
                }

                binding.reorderHandle.setOnTouchListener { view, event ->
                    view.performClick()
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        onDrag(this)
                    }
                    false
                }
            }
        }
    }
}