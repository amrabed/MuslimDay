package amrabed.android.release.evaluation.edit;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.core.Task;
import amrabed.android.release.evaluation.core.TaskList;
import amrabed.android.release.evaluation.edit.drag.DragListener;
import amrabed.android.release.evaluation.edit.drag.ItemTouchHandler;

/**
 * Editor fragment used to edit list items
 */
public class EditSection extends Fragment implements OnBackPressedListener, DragListener, View.OnClickListener {

    private static final String LIST = "tasklist";
    private static final String IS_CHANGED = "isChanged";
    private ItemTouchHelper touchHelper;
    private EditorListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.editor, container, false);
        final RecyclerView listView = view.findViewById(R.id.list);
        final Context context = getContext();
        final TaskList list = (savedInstanceState != null) ?
                (TaskList) savedInstanceState.getSerializable(LIST) : TaskList.getCurrent(context);
        final boolean isChanged = (savedInstanceState != null) && savedInstanceState.getBoolean(IS_CHANGED);

        adapter = new EditorListAdapter(context, this, list, isChanged);

        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(listView.getContext()));
        if(context != null) {
            listView.addItemDecoration(new DividerItemDecoration(context,
                    DividerItemDecoration.VERTICAL));
        }

        touchHelper = new ItemTouchHelper(new ItemTouchHandler(context, this));
        touchHelper.attachToRecyclerView(listView);

        final FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(LIST, adapter.getList());
        outState.putBoolean(IS_CHANGED, adapter.isChanged());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        if (getActivity() != null) {
            getActivity().getMenuInflater().inflate(R.menu.menu_edit, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
    public void onBackPressed()
    {
        // TODO: Handle this in a better way
        adapter.save(getActivity());
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
     * @param view the clicked FAB
     */
    @Override
    public void onClick(View view) {
        adapter.save(null);
    }

    private void showUndoMessage(final int position, final Task item) {
        final View view = getView();
        if(view != null) {
            final Snackbar snackbar = Snackbar.make(getView(), R.string.deleted,
                    Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.putBack(position, item);
                }
            });
            snackbar.show();
        }
    }
}
