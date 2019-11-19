package amrabed.android.release.evaluation.edit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import amrabed.android.release.evaluation.ApplicationEvaluation;
import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.core.Task;
import amrabed.android.release.evaluation.core.TaskList;
import amrabed.android.release.evaluation.edit.drag.DragListener;


public class EditorListAdapter extends RecyclerView.Adapter<EditorListAdapter.ViewHolder> {

    private final Context context;
    private final DragListener listener;

    private TaskList list;
    private boolean isChanged;

    EditorListAdapter(Context context, DragListener listener, TaskList list, boolean isChanged) {
        this.context = context;
        this.listener = listener;
        this.list = list;
        this.isChanged = isChanged;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.editor_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Task task = list.get(position);
        holder.titleText.setText(task.getTitle(context));
        holder.reorderHandle.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                listener.onDrag(holder);

            }
            return false;
        });
        if(isRegularItem(position)) {
            holder.titleText.setOnClickListener(view -> addOrEditItem(position, false));
            holder.daySelector.setVisibility(View.VISIBLE);
            holder.daySelector.setOnClickListener(view -> setDisplayDays(position));
        } else {
            holder.titleText.setOnClickListener(view -> Toast.makeText(context, R.string.fasting_preference, Toast.LENGTH_LONG).show());
            holder.daySelector.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    void moveItem(RecyclerView.ViewHolder source, RecyclerView.ViewHolder destination) {
        int from = source.getAdapterPosition();
        int to = destination.getAdapterPosition();
        if (from != to) {
            isChanged = true;
            if(from < to) {
                list.add(to, list.remove(from));
                for(int i = from; i < to - 1; i++) {
                    list.add(i, list.remove(i + 1));
                }
            } else {
                for(int i = from; i > to; i--) {
                    list.add(i, list.remove(i - 1));
                }
            }
            notifyItemMoved(from, to);
        }
    }

    void removeItem(RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();
        if(isRegularItem(position)) {
            isChanged = true;
            list.remove(position);
        }
        notifyItemRemoved(position);
    }

    void putBack(int position, Task item) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    private boolean isRegularItem(int position) {
       return !list.get(position).getTitle(context).equals(context.getString(R.string.fasting_title));
    }

    void addNewItem() {
        addOrEditItem(list.size() - 1, true);
    }

    private void addOrEditItem(final int position, final boolean isNewItem) {
        final EditText editText = (EditText) LayoutInflater.from(context)
                .inflate(R.layout.edit_dialog, null);
        if (!isNewItem) {
            editText.setText(list.get(position).getTitle(context));
        }
        new AlertDialog.Builder(context)
                .setTitle(isNewItem ? R.string.add : R.string.edit)
                .setView(editText)
                .setPositiveButton(R.string.ok, (dialog, whichButton) -> {
                    final String text = editText.getText().toString();
                    if (isNewItem) {
                        list.add(position + 1, new Task().setCurrentTitle(text));
                        notifyItemInserted(position + 1);
                        Toast.makeText(context, R.string.added, Toast.LENGTH_LONG).show();
                    } else {
                        list.get(position).setCurrentTitle(text);
                        notifyItemChanged(position);
                    }
                    isChanged = true;
                })
                .setNegativeButton(R.string.cancel, (dialog, whichButton) -> dialog.dismiss())
                .create().show();
    }

    void restoreDefaults()
    {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.reset))
                .setMessage(context.getString(R.string.confirm_reset))
                .setCancelable(true)
                .setNegativeButton(context.getString(R.string.dialog_no),
                        (dialog, which) -> dialog.cancel())
                .setPositiveButton(context.getString(R.string.dialog_yes),
                        (dialog, which) -> {
                            list = TaskList.getDefault(context);
                            notifyDataSetChanged();
                        })
                .create().show();
    }

    void save(@Nullable final Activity activity) {
        if(isChanged)
        {
            new AlertDialog.Builder(context)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.confirm_save)
                    .setCancelable(false)
                    .setPositiveButton(R.string.save,
                            (dialog, which) -> {
                                isChanged = false;
                                ApplicationEvaluation.getDatabase().saveList(list);
                                goHome(activity);
                            })
                    .setNegativeButton(R.string.discard, (dialog, which) -> {
                        dialog.cancel();
                        goHome(activity);
                    })
                    .create().show();
        }
        else
        {
            Toast.makeText(context, R.string.no_update, Toast.LENGTH_SHORT).show();
            goHome(activity);
        }
    }

    private void goHome(@Nullable Activity activity) {
        if (activity != null) {
            activity.onBackPressed();
            activity.recreate();
        }
    }

    /**
     * Show dialog to choose days when the selected item should be shown in the list
     *
     * @param position position of the selected item in the list
     */
    private void setDisplayDays(final int position) {
        final Task task = list.get(position);
        final boolean[] selected = (task == null) ? new boolean[7] :
                task.getActiveDays(context.getResources().getInteger(R.integer.day_shift));
        new AlertDialog.Builder(context)
                .setTitle(R.string.select_days_title)
                .setMultiChoiceItems(R.array.days, selected,
                        (dialogInterface, which, isChecked) -> {
                            final int day = Integer.parseInt(context.getResources()
                                    .getStringArray(R.array.day_values)[which]);
                            list.get(position).setActiveDay(day, isChecked);
                        })
                .setPositiveButton(R.string.ok, (dialogInterface, which) -> {
                    isChanged = true;
                    notifyItemChanged(position);
                })
                .create().show();
    }

    public TaskList getList() {
        return list;
    }

    boolean isChanged() {
        return isChanged;
    }

    /**
     * ViewHolder for the list item view
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView reorderHandle;
        final TextView titleText;
        final ImageView daySelector;

        ViewHolder(View view) {
            super(view);
            reorderHandle = view.findViewById(R.id.reorder_handle);
            titleText = view.findViewById(R.id.content);
            daySelector = view.findViewById(R.id.days);
        }
    }
}
