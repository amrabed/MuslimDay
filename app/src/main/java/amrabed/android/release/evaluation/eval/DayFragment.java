package amrabed.android.release.evaluation.eval;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTime;

import amrabed.android.release.evaluation.guide.DetailsFragment;
import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.ApplicationEvaluation;
import amrabed.android.release.evaluation.core.Activity;
import amrabed.android.release.evaluation.core.ActivityList;
import amrabed.android.release.evaluation.core.DayEntry;
import amrabed.android.release.evaluation.core.Selection;

public class DayFragment extends ListFragment
{
	private static final String TAG = "args";

	DayEntry entry;
	MyAdapter adapter;

	ActivityList list;

	public static DayFragment getInstance(DayEntry entry)
	{
		final DayFragment section = new DayFragment();
		final Bundle args = new Bundle();
		args.putParcelable(TAG, entry);
		section.setArguments(args);
		return section;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		final Bundle args = getArguments();
		if (args != null)
		{
			entry = args.getParcelable(TAG);
		}
//		else
//		{
//			// Should never be called .. left for history reasons !
//			entry = ApplicationEvaluation.getDatabase()
//					.getEntry(new DateTime().withTimeAtStartOfDay().getMillis());
//		}

		list = ActivityList.getDayList(getActivity(), entry.getDate());
		adapter = new MyAdapter(getActivity(), R.layout.list_item, list);
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
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final View view = info.targetView;
		final int position = info.position;

		switch (item.getItemId())
		{
			case R.id.not_yet:
				respond(new Selection(Selection.NONE), position, view);
				break;
			case R.id.yes:
				respond(new Selection(Selection.GOOD), position, view);
				break;
			case R.id.no_w:
				respond(new Selection(Selection.OK), position, view);
				break;
			case R.id.no_wo:
				respond(new Selection(Selection.BAD), position, view);
				break;
			default:
				return super.onContextItemSelected(item);
		}
		return true;

	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id)
	{
		respond(new Selection(entry.getSelection(getId(position))).next(), position, view);
	}

	private void respond(Selection selection, int position, View view)
	{
		entry.setSelectionAt(getId(position), selection.getValue());
		setIcon((TextView) view.findViewById(R.id.text), selection.getIcon());
		ApplicationEvaluation.getDatabase().updateDay(entry);
		PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
				.putLong("LAST_UPDATE", DateTime.now().getMillis()).apply();
	}

	private String getId(int position)
	{
		return list.get(position).getId();
	}

	void setIcon(TextView tv, int icon)
	{
		if (getResources().getConfiguration().locale.getDisplayName().toLowerCase()
				.contains("english"))
		{
			tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0);
		}
		else
		{
			tv.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
		}
	}

	private void showDetails(int entry, String title)
	{
		getActivity().getFragmentManager().beginTransaction().addToBackStack(null)
				.replace(R.id.content, DetailsFragment.newInstance(entry, title))
				.commit();
	}

	class MyAdapter extends ArrayAdapter<Activity>
	{
		MyAdapter(Context context, int layout, ActivityList list)
		{
			super(context, layout, list);
		}

		@NonNull
		@Override
		public View getView(int position, View view, @NonNull ViewGroup parent)
		{
			ViewHolder viewHolder;
			Activity activity = getItem(position);
			if (view == null)
			{
				view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
				viewHolder = new ViewHolder(view);
//				textView = (TextView) view.findViewById(android.R.id.text1);
				view.setTag(viewHolder);
			}
			else
			{
				viewHolder = (ViewHolder) view.getTag();
			}
			final String title = activity.getTitle(getContext());
			viewHolder.textView.setText(title);
			setIcon(viewHolder.textView, Selection.getIcon(entry.getSelection(getId(position))));

			final int entry = activity.getGuideEntry();
			if (entry != 0)
			{
				viewHolder.icon.setVisibility(View.VISIBLE);
				viewHolder.icon.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						showDetails(entry, title);
					}
				});
			}
			else
			{
				viewHolder.icon.setVisibility(View.GONE);
				viewHolder.icon.setOnClickListener(null);
			}
			return view;
		}

		class ViewHolder
		{
			private final TextView textView;
			private final ImageView icon;

			ViewHolder(View view)
			{
				textView = (TextView) view.findViewById(R.id.text);
				icon = (ImageView) view.findViewById(R.id.icon);
			}

		}
	}
}
