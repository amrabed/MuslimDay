package amrabed.android.release.evaluation.edit

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.data.converters.ActiveDaysConverter
import amrabed.android.release.evaluation.data.entities.Task
import amrabed.android.release.evaluation.data.repositories.TaskRepository
import amrabed.android.release.evaluation.edit.drag.DragListener
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.math.max
import kotlin.math.min

class EditListAdapter (private val context: Context, private val listener: DragListener, val list: MutableList<Task?>?) : RecyclerView.Adapter<EditListAdapter.ViewHolder>() {
    private val modifications = LinkedList<Modification>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.editor_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = list!![position]
        holder.titleText.text = task!!.getTitle(context)
        holder.reorderHandle.setOnTouchListener { _: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                listener.onDrag(holder)
            }
            false
        }
        if (isRegularItem(position)) {
            holder.titleText.setOnClickListener { addOrEditItem(position, false) }
            holder.daySelector.visibility = View.VISIBLE
            holder.daySelector.setOnClickListener { setDisplayDays(position) }
        } else {
            holder.titleText.setOnClickListener { Toast.makeText(context, R.string.fasting_preference, Toast.LENGTH_LONG).show() }
            holder.daySelector.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    fun moveItem(source: RecyclerView.ViewHolder, destination: RecyclerView.ViewHolder) {
        val from = source.adapterPosition
        val to = destination.adapterPosition
        if (from != to) {
            list?.add(to, list.removeAt(from)?.setCurrentIndex(to))
            // All items are already moved to their correct final position in list
            // Update current index of tasks between initial and final positions
            for (i in min(to, from)..max(to, from)) {
                modifications.add(Modification(list!![i]!!.setCurrentIndex(i), Modification.UPDATE))
            }
            notifyItemMoved(from, to)
        }
    }

    fun removeItem(holder: RecyclerView.ViewHolder) {
        val position = holder.adapterPosition
        if (isRegularItem(position)) {
            modifications.add(Modification(list!![position], Modification.DELETE))
            list.removeAt(position)
        }
        notifyItemRemoved(position)
    }

    fun putBack(position: Int, item: Task?) {
        list?.add(position, item)
        modifications.pop()
        notifyItemInserted(position)
    }

    private fun isRegularItem(position: Int): Boolean {
        return list!![position]!!.getTitle(context) != context.getString(R.string.fasting_title)
    }

    fun addNewItem() {
        addOrEditItem(list!!.size - 1, true)
    }

    private fun addOrEditItem(position: Int, isNewItem: Boolean) {
        val editText = LayoutInflater.from(context)
                .inflate(R.layout.edit_dialog, null) as EditText
        if (!isNewItem) {
            editText.setText(list!![position]!!.getTitle(context))
        }
        AlertDialog.Builder(context)
                .setTitle(if (isNewItem) R.string.add else R.string.edit)
                .setView(editText)
                .setPositiveButton(R.string.ok) { _: DialogInterface?, _: Int ->
                    val text = editText.text.toString()
                    if (isNewItem) {
                        val task = Task(list!!.size).setCurrentTitle(text)
                        modifications.add(Modification(task, Modification.ADD))
                        list.add(position + 1, task)
                        notifyItemInserted(position + 1)
                        listener.onItemAdded(position + 1)
                    } else {
                        modifications.add(Modification(list!![position], Modification.UPDATE))
                        list[position]!!.setCurrentTitle(text)
                        notifyItemChanged(position)
                    }
                }
                .setNegativeButton(R.string.cancel) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                .create().show()
    }

    fun commit() {
        val repository = TaskRepository(context)
        while (!modifications.isEmpty()) {
            val modification = modifications.pollFirst()
            if (modification != null) {
                when (modification.operation) {
                    Modification.ADD -> repository.addTask(modification.task)
                    Modification.DELETE -> repository.deleteTask(modification.task)
                    Modification.UPDATE -> repository.updateTask(modification.task)
                    else -> {
                    }
                }
            }
        }
    }

    /**
     * Show dialog to choose days when the selected item should be shown in the list
     *
     * @param position position of the selected item in the list
     */
    private fun setDisplayDays(position: Int) {
        val task = list!![position]
        val selected = task?.getActiveDays(context.resources.getInteger(R.integer.day_shift))
                ?: BooleanArray(7)
        AlertDialog.Builder(context)
                .setTitle(R.string.select_days_title)
                .setMultiChoiceItems(R.array.days, selected
                ) { _: DialogInterface?, which: Int, isChecked: Boolean ->
                    val day = context.resources
                            .getStringArray(R.array.day_values)[which].toInt()
                    list[position]!!.setActiveDay(day, isChecked)
                }
                .setPositiveButton(R.string.ok) { _: DialogInterface?, _: Int ->
                    modifications.add(Modification(list[position], Modification.UPDATE))
                    notifyItemChanged(position)
                }
                .create().show()
    }

    fun hide(holder: RecyclerView.ViewHolder) {
        val position = holder.adapterPosition
        val b: Byte = 0
        list!![position]!!.activeDays = ActiveDaysConverter().setActiveDays(b)
        modifications.add(Modification(list[position], Modification.UPDATE))
        notifyItemChanged(position)
    }

    val isChanged: Boolean
        get() = !modifications.isEmpty()

    /**
     * ViewHolder for the list item view
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reorderHandle: ImageView = view.findViewById(R.id.reorder_handle)
        val titleText: TextView = view.findViewById(R.id.content)
        val daySelector: ImageView = view.findViewById(R.id.days)
    }
}