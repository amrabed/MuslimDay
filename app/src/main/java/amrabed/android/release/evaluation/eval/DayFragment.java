package amrabed.android.release.evaluation.eval;

import android.app.ListFragment;
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

import org.joda.time.DateTime;

import amrabed.android.release.evaluation.ApplicationEvaluation;
import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.core.DayEntry;
import amrabed.android.release.evaluation.core.Selection;
import amrabed.android.release.evaluation.core.Task;
import amrabed.android.release.evaluation.core.TaskList;
import amrabed.android.release.evaluation.guide.DetailsFragment;
import androidx.annotation.NonNull;

public class DayFragment extends ListFragment
{
	private static final String TAG = "args";

	private DayEntry entry;

	private TaskList list;

	public static DayFragment getInstance(DayEntry entry)
	{
		final DayFragment section = new DayFragment();
		final Bundle args = new Bundle();
		args.putParcelable(TAG, entry);
		section.setArguments(args);
		return section;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		final Bundle args = getArguments();
		if (args != null)
		{
			entry = args.getParcelable(TAG);
		}
		list = TaskList.getDayList(getActivity(), entry.getDate());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{

		MyAdapter adapter = new MyAdapter(getActivity(), list);
		setListAdapter(adapter);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		getActivity().setTitle(R.string.evaluation);
		getListView().scrollTo(0, getActivity().getPreferences(0).getInt("Position", 0));
	}

	@Override
	public void onPause()
	{
		super.onPause();
		getActivity().getPreferences(0).edit().putInt("Position", getListView().getScrollY())
				.apply();
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id)
	{
		respond(new Selection(entry.getSelection(getId(position))).next(), position, view);
	}

	private void respond(Selection selection, int position, View view)
	{
		entry.setSelectionAt(getId(position), selection.getValue());
		((ImageView) view.findViewById(R.id.selection)).setImageResource(selection.getIcon());
		ApplicationEvaluation.getDatabase().updateDay(entry);
		PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
				.putLong("LAST_UPDATE", DateTime.now().getMillis()).apply();
	}

	private String getId(int position)
	{
		return list.get(position).getId();
	}

	class MyAdapter extends ArrayAdapter<Task>
	{
		MyAdapter(Context context, TaskList list)
		{
			super(context, R.layout.list_item, list);
		}

		@NonNull
		@Override
		public View getView(int position, View view, @NonNull ViewGroup parent)
		{
			ViewHolder viewHolder;
			Task task = getItem(position);
			if (view == null)
			{
				view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
				viewHolder = new ViewHolder(view);
				view.setTag(viewHolder);
			}
			else
			{
				viewHolder = (ViewHolder) view.getTag();
			}
			if (task != null)
			{
				final String title = task.getTitle(getContext());
				viewHolder.textView.setText(title);
				viewHolder.selection
						.setImageResource(Selection.getIcon(entry.getSelection(task.getId())));

				final int index = task.getGuideEntry();
				if (index != 0)
				{
					viewHolder.icon.setVisibility(View.VISIBLE);
					viewHolder.icon.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View view)
						{
							showDetails(index);
						}
					});
				}
				else
				{
					viewHolder.icon.setVisibility(View.INVISIBLE);
					viewHolder.icon.setOnClickListener(null);
				}
			}
			return view;
		}

		private void showDetails(int entry)
		{
			getActivity().getFragmentManager().beginTransaction().addToBackStack(null)
					.replace(R.id.content, DetailsFragment.newInstance(entry))
					.commit();
		}

		class ViewHolder
		{
			private final ImageView selection;
			private final TextView textView;
			private final ImageView icon;

			ViewHolder(View view)
			{
				selection = view.findViewById(R.id.selection);
				textView = view.findViewById(R.id.text);
				icon = view.findViewById(R.id.icon);
			}

		}
	}
}
