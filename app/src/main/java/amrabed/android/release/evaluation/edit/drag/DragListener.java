package amrabed.android.release.evaluation.edit.drag;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public interface DragListener {
    void onDrag(ViewHolder holder);

    void onItemMoved(ViewHolder source, ViewHolder destination);

    void onItemRemoved(ViewHolder holder);
}
