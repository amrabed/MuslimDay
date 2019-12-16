package amrabed.android.release.evaluation.edit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;

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
    private EditListAdapter adapter;

    private RecyclerView listView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleManager.setLocale(this);
        setContentView(R.layout.editor);

        listView = findViewById(R.id.list);

        adapter = new EditListAdapter(this, this, new ArrayList<>());

        listView.setAdapter(adapter);
        listView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        ViewModelProviders.of(this).get(TaskViewModel.class).getTaskList()
                .observe(this, adapter::setList);

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
        if (item.getItemId() == R.id.menu_add) {
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
        final int position = holder.getAdapterPosition();
        final Task item = adapter.getList().get(position);
        adapter.removeItem(holder);
        final View view = getCurrentFocus();
        if (view != null) {
            Snackbar.make(view, R.string.deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, v -> adapter.putBack(position, item))
                    .show();
        }
    }

    @Override
    public void onItemHidden(RecyclerView.ViewHolder holder) {
        adapter.hide(holder);
        final View view = getCurrentFocus();
        if (view != null) {
            Snackbar.make(view, R.string.hidden, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemAdded(int position) {
        final View view = getCurrentFocus();
        if (view != null) {
            Snackbar.make(view, R.string.added, Snackbar.LENGTH_LONG).show();
        }
        listView.smoothScrollToPosition(position);
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

    private void checkSaved() {
        if (adapter.isChanged()) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.confirm_save)
                    .setCancelable(false)
                    .setPositiveButton(R.string.save,
                            (dialog, which) -> {
                                adapter.commit();
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
