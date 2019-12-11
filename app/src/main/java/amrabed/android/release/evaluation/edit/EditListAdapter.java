package amrabed.android.release.evaluation.edit;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.data.entities.Task;
import amrabed.android.release.evaluation.data.repositories.TaskRepository;
import amrabed.android.release.evaluation.edit.drag.DragListener;


public class EditListAdapter extends RecyclerView.Adapter<EditListAdapter.ViewHolder> {

    private final Context context;
    private final DragListener listener;

    private List<Task> list;
    private LinkedList<Modification> modifications = new LinkedList<>();

    EditListAdapter(Context context, DragListener listener, List<Task> list) {
        this.context = context;
        this.listener = listener;
        this.list = list;
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
        if (isRegularItem(position)) {
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
            list.add(to, list.remove(from).setCurrentIndex(to));
            // All items are already moved to their correct final position in list
            // Update current index of tasks between initial and final positions
            for(int i = Math.min(to, from); i <= Math.max(to, from); i++) {
                modifications.add(new Modification(list.get(i).setCurrentIndex(i), Modification.UPDATE));
            }
            notifyItemMoved(from, to);
        }
    }

    void removeItem(RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();
        if (isRegularItem(position)) {
            modifications.add(new Modification(list.get(position), Modification.DELETE));
            list.remove(position);
        }
        notifyItemRemoved(position);
    }

    void putBack(int position, Task item) {
        list.add(position, item);
        modifications.pop();
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
                .setPositiveButton(R.string.ok, (dialog, button) -> {
                    final String text = editText.getText().toString();
                    if (isNewItem) {
                        final Task task = new Task(list.size()).setCurrentTitle(text);
                        modifications.add(new Modification(task, Modification.ADD));
                        list.add(position + 1, task);
                        notifyItemInserted(position + 1);
                        Toast.makeText(context, R.string.added, Toast.LENGTH_LONG).show();
                    } else {
                        modifications.add(new Modification(list.get(position), Modification.UPDATE));
                        list.get(position).setCurrentTitle(text);
                        notifyItemChanged(position);
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, whichButton) -> dialog.dismiss())
                .create().show();
    }

    void restoreDefaults() {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.reset))
                .setMessage(context.getString(R.string.confirm_reset))
                .setCancelable(true)
                .setNegativeButton(context.getString(R.string.dialog_no),
                        (dialog, which) -> dialog.cancel())
                .setPositiveButton(context.getString(R.string.dialog_yes),
                        (dialog, which) -> {
//                            list = TaskList.getDefault();
                            notifyDataSetChanged();
                        })
                .create().show();
    }


    void commit() {
        final TaskRepository repository = new TaskRepository(context);
        while (!modifications.isEmpty()) {
            final Modification modification = modifications.pollFirst();
            if (modification != null) {
                switch (modification.operation) {
                    case Modification.ADD:
                        repository.addTask(modification.task);
                        break;
                    case Modification.DELETE:
                        repository.deleteTask(modification.task);
                        break;
                    case Modification.UPDATE:
                        repository.updateTask(modification.task);
                        break;
                    default:
                }
            }
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
                    modifications.add(new Modification(list.get(position), Modification.UPDATE));
                    notifyItemChanged(position);
                })
                .create().show();
    }

    public List<Task> getList() {
        return list;
    }

    void setList(List<Task> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    boolean isChanged() {
        return !modifications.isEmpty();
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