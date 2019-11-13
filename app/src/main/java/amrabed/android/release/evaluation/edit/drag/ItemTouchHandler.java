package amrabed.android.release.evaluation.edit.drag;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import amrabed.android.release.evaluation.R;

public class ItemTouchHandler extends ItemTouchHelper.SimpleCallback {

    private final Context context;
    private final DragListener listener;

    public ItemTouchHandler(Context context, DragListener listener) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.START | ItemTouchHelper.END);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull ViewHolder source, @NonNull ViewHolder destination) {
        listener.onItemMoved(source, destination);
        return true;
    }

    @Override
    public void onSwiped(@NonNull ViewHolder holder, int direction) {
        listener.onItemRemoved(holder);
    }

    /**
     * Change background color of the view when the item is dragged or swiped
     */
    @Override
    public void onSelectedChanged(ViewHolder holder, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        }

        super.onSelectedChanged(holder, actionState);
    }

    /**
     * Reset the background color of the view when the item is dropped
     */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull ViewHolder holder) {
        super.clearView(recyclerView, holder);
        holder.itemView.setBackgroundColor(0);
    }

    /**
     * Show delete icon and change background to red when swiping
     *
     * Based on https://medium.com/@zackcosborn/step-by-step-recyclerview-swipe-to-delete-and-undo-7bbae1fce27e
     */
    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView,
                            @NonNull ViewHolder holder, float dx, float dy, int actionState,
                            boolean isCurrentlyActive) {
        super.onChildDraw(canvas, recyclerView, holder, dx, dy, actionState, isCurrentlyActive);

        if(actionState != ItemTouchHelper.ACTION_STATE_SWIPE) return;

        final Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        final ColorDrawable background = new ColorDrawable(Color.RED);

        final View view = holder.itemView;
        final int offset = 20;

        if(icon != null) {
            int iconMargin = (view.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = view.getTop() + iconMargin;
            int iconBottom = iconTop + icon.getIntrinsicHeight();


            if (dx > 0) { // Swiping to the right
                int iconLeft = view.getLeft() + iconMargin;
                int iconRight = iconLeft + icon.getIntrinsicWidth();
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(view.getLeft(), view.getTop(),
                        view.getLeft() + ((int) dx) + offset, view.getBottom());
            } else if (dx < 0) { // Swiping to the left
                int iconRight = view.getRight() - iconMargin;
                int iconLeft = view.getRight() - iconMargin - icon.getIntrinsicWidth();
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(view.getRight() + ((int) dx) - offset,
                        view.getTop(), view.getRight(), view.getBottom());
            } else { // view is unSwiped
                background.setBounds(0, 0, 0, 0);
            }
            background.draw(canvas);
            icon.draw(canvas);
        }
    }
}
