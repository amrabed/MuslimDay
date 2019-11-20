package amrabed.android.release.evaluation.edit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.core.Task;
import amrabed.android.release.evaluation.core.TaskList;
import amrabed.android.release.evaluation.edit.drag.DragListener;
import amrabed.android.release.evaluation.edit.drag.ItemTouchHandler;
import amrabed.android.release.evaluation.locale.LocaleManager;

/**
 * Editor fragment used to edit list items
 */
public class EditSection extends AppCompatActivity implements DragListener, View.OnClickListener {

    private static final String LIST = "task list";
    private static final String IS_CHANGED = "isChanged";

    private ItemTouchHelper touchHelper;
    private EditorListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setLocale(this);
        setContentView(R.layout.editor);

        final RecyclerView listView = findViewById(R.id.list);
        final TaskList list = (savedInstanceState != null) ?
                (TaskList) savedInstanceState.getSerializable(LIST) : TaskList.getCurrent(this);
        final boolean isChanged = (savedInstanceState != null) && savedInstanceState.getBoolean(IS_CHANGED);

        adapter = new EditorListAdapter(this, this, list, isChanged);

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
        outState.putSerializable(LIST, adapter.getList());
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
}
