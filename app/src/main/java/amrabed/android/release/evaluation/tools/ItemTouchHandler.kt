package amrabed.android.release.evaluation.tools

import amrabed.android.release.evaluation.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHandler(private val context: Context, private val listener: Listener) :
        ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.START or ItemTouchHelper.END) {
    override fun onMove(recyclerView: RecyclerView, source: RecyclerView.ViewHolder, destination: RecyclerView.ViewHolder): Boolean {
        listener.onItemMoved(source, destination)
        return true
    }

    override fun onSwiped(holder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.START) {
            listener.onItemRemoved(holder)
        } else {
            listener.onItemHidden(holder)
        }
    }

    /**
     * Change background color of the view when the item is dragged or swiped
     */
    override fun onSelectedChanged(holder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            holder!!.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
        }
        super.onSelectedChanged(holder, actionState)
    }

    /**
     * Reset the background color of the view when the item is dropped
     */
    override fun clearView(recyclerView: RecyclerView, holder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, holder)
        holder.itemView.setBackgroundColor(0)
    }

    /**
     * Show delete icon and change background to red when swiping
     *
     * Based on https://medium.com/@zackcosborn/step-by-step-recyclerview-swipe-to-delete-and-undo-7bbae1fce27e
     */
    override fun onChildDraw(canvas: Canvas, recyclerView: RecyclerView,
                             holder: RecyclerView.ViewHolder, dx: Float, dy: Float, actionState: Int,
                             isCurrentlyActive: Boolean) {
        super.onChildDraw(canvas, recyclerView, holder, dx, dy, actionState, isCurrentlyActive)
        if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) return
        val icon = ContextCompat.getDrawable(context, if (dx > 0) R.drawable.ic_delete else R.drawable.ic_hide)
        val background = ColorDrawable(ContextCompat.getColor(context, if (dx > 0) android.R.color.holo_red_dark else android.R.color.holo_orange_dark))
        val view = holder.itemView
        val offset = 20
        if (icon != null) {
            val iconMargin = (view.height - icon.intrinsicHeight) / 2
            val iconTop = view.top + iconMargin
            val iconBottom = iconTop + icon.intrinsicHeight
            when {
                dx > 0 -> { // Swiping to the right
                    val iconLeft = view.left + iconMargin
                    val iconRight = iconLeft + icon.intrinsicWidth
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    background.setBounds(view.left, view.top,
                            view.left + dx.toInt() + offset, view.bottom)
                }
                dx < 0 -> { // Swiping to the left
                    val iconRight = view.right - iconMargin
                    val iconLeft = view.right - iconMargin - icon.intrinsicWidth
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    background.setBounds(view.right + dx.toInt() - offset,
                            view.top, view.right, view.bottom)
                }
                else -> { // view is unSwiped
                    background.setBounds(0, 0, 0, 0)
                }
            }
            background.draw(canvas)
            icon.draw(canvas)
        }
    }

    interface Listener {
        fun onDrag(holder: RecyclerView.ViewHolder)
        fun onItemMoved(source: RecyclerView.ViewHolder, destination: RecyclerView.ViewHolder)
        fun onItemRemoved(holder: RecyclerView.ViewHolder)
        fun onItemHidden(holder: RecyclerView.ViewHolder)
    }
}