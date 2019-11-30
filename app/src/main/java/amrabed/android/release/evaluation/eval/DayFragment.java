package amrabed.android.release.evaluation.eval;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.core.Selection;
import amrabed.android.release.evaluation.data.entities.Day;
import amrabed.android.release.evaluation.data.entities.Task;
import amrabed.android.release.evaluation.data.models.DayViewModel;
import amrabed.android.release.evaluation.data.models.TaskViewModel;
import amrabed.android.release.evaluation.guide.DetailsFragment;
import amrabed.android.release.evaluation.progress.item.ProgressFragment;

/**
 * Fragment to display list of active tasks for the day
 */
public class DayFragment extends Fragment {
    private static final String POSITION = "Position";

    private RecyclerView listView;
    private Day day;
    private DayViewModel model;

    static DayFragment newInstance(int position) {
        final Bundle args = new Bundle();
        args.putInt(POSITION, position);
        DayFragment fragment = new DayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.day_list, container, false);
        final Adapter adapter = new Adapter(new ArrayList<>());
        listView = (RecyclerView) view;
        listView.setAdapter(adapter);

        if (savedInstanceState != null) {
            final RecyclerView.LayoutManager layoutManager = listView.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(POSITION));
            }
        }

        final FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.setTitle(R.string.evaluation);
            listView.addItemDecoration(new DividerItemDecoration(activity,
                    DividerItemDecoration.VERTICAL));

            if (getArguments() != null) {
                model = ViewModelProviders.of(getActivity())
                        .get(String.valueOf(getArguments().getInt(POSITION)), DayViewModel.class);

                model.getSelected().observe(this, day -> {
                    this.day = day;
                    adapter.updateList();
                });
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (listView != null) {
            final RecyclerView.LayoutManager layoutManager = listView.getLayoutManager();
            if (layoutManager != null) {
                outState.putParcelable(POSITION, layoutManager.onSaveInstanceState());
            }
        }
    }

    @Override
    public void onDetach() {
        if(model != null) {
            model.updateDay(day);
        }
        super.onDetach();
    }

    private void loadFragment(Fragment fragment) {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null).replace(R.id.content, fragment).commit();
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private List<Task> list;

        private Adapter(List<Task> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final Task task = list.get(position);
            if (task != null && task.isVisible(getContext(), day)) {
            holder.itemView.setOnClickListener(view -> {
                final String id = list.get(position).id;
                final Selection selection = new Selection(day.getSelection(id)).next();
                day.setSelectionAt(id, selection.getValue());
                ((ImageView) view.findViewById(R.id.selection)).setImageResource(selection.getIcon());
            });
            final String title = task.getTitle(getContext());
            holder.textView.setText(title);
            holder.selection
                    .setImageResource(Selection.getIcon(day.getSelection(task.id)));

            final int entry = task.guideEntry;
            if (entry != 0) {
                holder.icon.setVisibility(View.VISIBLE);
                holder.icon.setOnClickListener(v -> loadFragment(DetailsFragment.newInstance(entry, title)));
            } else {
                holder.icon.setVisibility(View.INVISIBLE);
                holder.icon.setOnClickListener(null);
            }

            holder.pie.setOnClickListener(v -> loadFragment(ProgressFragment.newInstance(task.id, task.getTitle(DayFragment.this.getContext()))));
            } else {
                holder.itemView.setSystemUiVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private void updateList() {
            ViewModelProviders.of(DayFragment.this).get(TaskViewModel.class).getTaskList()
                    .observe(DayFragment.this, taskList -> {
                        list = taskList;
                        final Iterator<Task> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            if (!iterator.next().isVisible(getContext(), day)) {
                                iterator.remove();
                            }
                        }
                        notifyDataSetChanged();

                    });
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView selection;
        private final TextView textView;
        private final ImageView icon;
        private final ImageView pie;

        ViewHolder(View view) {
            super(view);
            selection = view.findViewById(R.id.selection);
            textView = view.findViewById(R.id.text);
            icon = view.findViewById(R.id.icon);
            pie = view.findViewById(R.id.pie);
        }
    }
}