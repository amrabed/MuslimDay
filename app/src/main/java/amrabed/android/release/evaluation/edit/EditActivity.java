package amrabed.android.release.evaluation.edit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.data.entities.Task;
import amrabed.android.release.evaluation.data.models.TaskViewModel;
import amrabed.android.release.evaluation.edit.drag.DragListener;
import amrabed.android.release.evaluation.edit.drag.ItemTouchHandler;
import amrabed.android.release.evaluation.locale.LocaleManager;

/**
 * Editor fragment used to edit list items
 */
public class EditActivity extends AppCompatActivity implements DragListener, View.OnClickListener {

    private static final String LIST = "task list";
    private static final String IS_CHANGED = "isChanged";

    private ItemTouchHelper touchHelper;
    private EditorListAdapter adapter;

    private TaskViewModel model;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setLocale(this);
        setContentView(R.layout.editor);

        final RecyclerView listView = findViewById(R.id.list);

        model = ViewModelProviders.of(this).get(TaskViewModel.class);
        if (savedInstanceState != null) {
            final boolean isChanged = savedInstanceState.getBoolean(IS_CHANGED);
            List<Task> list = (List<Task>) savedInstanceState.getSerializable(LIST);
            adapter = new EditorListAdapter(this, this, list, isChanged);
        } else {
            adapter = new EditorListAdapter(this, this, new ArrayList<>(), false);
            model.getTaskList().observe(this, adapter::setList);
        }


        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(listView.getContext()));
        listView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        touchHelper = new ItemTouchHelper(new ItemTouchHandler(this, this));
        touchHelper.attachToRecyclerView(listView);

        findViewById(R.id.fab).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocaleManager.setLocale(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(LIST, (Serializable) adapter.getList());
        outState.putBoolean(IS_CHANGED, adapter.isChanged());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reset:
                adapter.restoreDefaults();
                return true;
            case R.id.menu_add:
                adapter.addNewItem();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        checkSaved();
    }

    @Override
    public void onDrag(RecyclerView.ViewHolder holder) {
        touchHelper.startDrag(holder);
    }

    @Override
    public void onItemMoved(RecyclerView.ViewHolder source, RecyclerView.ViewHolder destination) {
        adapter.moveItem(source, destination);
    }

    @Override
    public void onItemRemoved(RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();
        Task item = adapter.getList().get(position);
        adapter.removeItem(holder);
        showUndoMessage(position, item);
    }

    /**
     * Handles FAB click by saving the modified list
     *
     * @param view the clicked FAB
     */
    @Override
    public void onClick(View view) {
        checkSaved();
    }

    private void showUndoMessage(final int position, final Task item) {
        final View view = getCurrentFocus();
        if (view != null) {
            Snackbar.make(view, R.string.deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, v -> adapter.putBack(position, item))
                    .show();
        }
    }

    private void checkSaved() {
        if (adapter.isChanged()) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.confirm_save)
                    .setCancelable(false)
                    .setPositiveButton(R.string.save,
                            (dialog, which) -> {
                                adapter.save();
                                setResult(RESULT_OK);
                                finish();
                            })
                    .setNegativeButton(R.string.discard, (dialog, which) -> {
                        dialog.cancel();
                        finish();
                    })
                    .create().show();
        } else {
            finish();
        }
    }


    public class EditorListAdapter extends RecyclerView.Adapter<EditorListAdapter.ViewHolder> {

        private final Context context;
        private final DragListener listener;

        private List<Task> list;
        private boolean isChanged;

        EditorListAdapter(Context context, DragListener listener, List<Task> list, boolean isChanged) {
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
                isChanged = true;
                if (from < to) {
                    list.add(to, list.remove(from));
                    for (int i = from; i < to - 1; i++) {
                        list.add(i, list.remove(i + 1));
                    }
                } else {
                    for (int i = from; i > to; i--) {
                        list.add(i, list.remove(i - 1));
                    }
                }
                notifyItemMoved(from, to);
            }
        }

        void removeItem(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            if (isRegularItem(position)) {
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

        void restoreDefaults() {
            new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.reset))
                    .setMessage(context.getString(R.string.confirm_reset))
                    .setCancelable(true)
                    .setNegativeButton(context.getString(R.string.dialog_no),
                            (dialog, which) -> dialog.cancel())
                    .setPositiveButton(context.getString(R.string.dialog_yes),
                            (dialog, which) -> {
//                                list = TaskList.getDefault(context);
                                notifyDataSetChanged();
                            })
                    .create().show();
        }

        void save() {
            model.addTasks(list);
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

        private void setList(List<Task> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public List<Task> getList() {
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
}
