package amrabed.android.release.evaluation.eval;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.DateTime;

import amrabed.android.release.evaluation.ApplicationEvaluation;
import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.core.DayEntry;
import amrabed.android.release.evaluation.core.Selection;
import amrabed.android.release.evaluation.core.Task;
import amrabed.android.release.evaluation.core.TaskList;
import amrabed.android.release.evaluation.guide.DetailsFragment;
import amrabed.android.release.evaluation.progress.ProgressFragment;

public class DayFragment extends Fragment {
    private static final String TAG = "args";
    private static final String POSITION = "Position";

    private RecyclerView listView;
    private DayEntry entry;

    private TaskList list;

    static DayFragment getInstance(DayEntry entry) {
        final DayFragment section = new DayFragment();
        final Bundle args = new Bundle();
        args.putParcelable(TAG, entry);
        section.setArguments(args);
        return section;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        if (args != null) {
            entry = args.getParcelable(TAG);
        }
        list = TaskList.getDayList(getActivity(), entry.getDate());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.day_list, container, false);

        listView = (RecyclerView) view;
        listView.setAdapter(new MyAdapter());

        final Context context = getContext();
        if (context != null) {
            listView.addItemDecoration(new DividerItemDecoration(context,
                    DividerItemDecoration.VERTICAL));
        }
        if (savedInstanceState != null) {
            RecyclerView.LayoutManager layoutManager = listView.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(POSITION));
            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        final Activity activity = getActivity();
        if (activity != null) {
            activity.setTitle(R.string.evaluation);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        RecyclerView.LayoutManager layoutManager = listView.getLayoutManager();
        if (layoutManager != null) {
            outState.putParcelable(POSITION, layoutManager.onSaveInstanceState());
        }
    }

    private void respond(Selection selection, int position, View view) {
        entry.setSelectionAt(getId(position), selection.getValue());
        ((ImageView) view.findViewById(R.id.selection)).setImageResource(selection.getIcon());
        ApplicationEvaluation.getDatabase().updateDay(entry);
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                .putLong("LAST_UPDATE", DateTime.now().getMillis()).apply();
    }

    private void loadFragment(Fragment fragment) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null).replace(R.id.content, fragment).commit();
        }
    }

    private String getId(int position) {
        return list.get(position).getId();
    }

    class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
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
            holder.itemView.setOnClickListener(view ->
                    respond(new Selection(entry.getSelection(getId(position))).next(), position, view));
            if (task != null) {
                final String title = task.getTitle(getContext());
                holder.textView.setText(title);
                holder.selection
                        .setImageResource(Selection.getIcon(entry.getSelection(task.getId())));

                final int entry = task.getGuideEntry();
                if (entry != 0) {
                    holder.icon.setVisibility(View.VISIBLE);
                    holder.icon.setOnClickListener(v -> loadFragment(DetailsFragment.newInstance(entry, title)));
                } else {
                    holder.icon.setVisibility(View.INVISIBLE);
                    holder.icon.setOnClickListener(null);
                }

                holder.pie.setOnClickListener(v -> loadFragment(ProgressFragment.newInstance(task.getId(), task.getTitle(DayFragment.this.getContext()))));

            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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