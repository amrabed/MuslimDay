package amrabed.android.release.evaluation.eval;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.ListFragment;

import org.joda.time.DateTime;

import amrabed.android.release.evaluation.ApplicationEvaluation;
import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.core.DayEntry;
import amrabed.android.release.evaluation.core.Selection;
import amrabed.android.release.evaluation.core.Task;
import amrabed.android.release.evaluation.core.TaskList;
import amrabed.android.release.evaluation.guide.DetailsFragment;
import amrabed.android.release.evaluation.progress.ProgressFragment;

public class DayFragment extends ListFragment {
    private static final String TAG = "args";

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

        MyAdapter adapter = new MyAdapter(getActivity(), list);
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle();
        if (getActivity() != null) {
            getListView().scrollTo(0, getActivity().getPreferences(0).getInt("Position", 0));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            getActivity().getPreferences(0).edit().putInt("Position", getListView().getScrollY())
                    .apply();
        }
    }

    @Override
    public void onListItemClick(@NonNull ListView listView, @NonNull View view, int position, long id) {
        respond(new Selection(entry.getSelection(getId(position))).next(), position, view);
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

    private void setTitle() {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.setTitle(R.string.evaluation);
        }
    }

    class MyAdapter extends ArrayAdapter<Task> {
        MyAdapter(Context context, TaskList list) {
            super(context, R.layout.list_item, list);
        }

        @NonNull
        @Override
        public View getView(int position, View view, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            final Task task = getItem(position);
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            if (task != null) {
                final String title = task.getTitle(getContext());
                viewHolder.textView.setText(title);
                viewHolder.selection
                        .setImageResource(Selection.getIcon(entry.getSelection(task.getId())));

                final int entry = task.getGuideEntry();
                if (entry != 0) {
                    viewHolder.icon.setVisibility(View.VISIBLE);
                    viewHolder.icon.setOnClickListener(v -> loadFragment(DetailsFragment.newInstance(entry, title)));
                } else {
                    viewHolder.icon.setVisibility(View.INVISIBLE);
                    viewHolder.icon.setOnClickListener(null);
                }

                viewHolder.pie.setOnClickListener(v -> loadFragment(ProgressFragment.newInstance(task.getId(), task.getTitle(DayFragment.this.getContext()))));

            }
            return view;
        }
    }

    class ViewHolder {
        private final ImageView selection;
        private final TextView textView;
        private final ImageView icon;
        private final ImageView pie;

        ViewHolder(View view) {
            selection = view.findViewById(R.id.selection);
            textView = view.findViewById(R.id.text);
            icon = view.findViewById(R.id.icon);
            pie = view.findViewById(R.id.pie);
        }

    }
}