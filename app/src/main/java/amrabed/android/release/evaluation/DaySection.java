package amrabed.android.release.evaluation;

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
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import amrabed.android.release.evaluation.app.ApplicationEvaluation;
import amrabed.android.release.evaluation.core.Activity;
import amrabed.android.release.evaluation.core.ActivityList;
import amrabed.android.release.evaluation.core.Day;
import amrabed.android.release.evaluation.core.Selection;

public class DaySection extends ListFragment
{
	private static final String TAG = "args";

	Day entry;
	MyAdapter adapter;

	ActivityList list;

	public static DaySection getInstance(long date)
	{
		final DaySection section = new DaySection();
		final Bundle args = new Bundle();
		args.putLong(TAG, date);
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
			entry = ApplicationEvaluation.getDatabase().getEntry(args.getLong(TAG));
		}
		else
		{
			// Should never be called .. left for history reasons !
			entry = ApplicationEvaluation.getDatabase()
					.getEntry(new DateTime().withTimeAtStartOfDay().getMillis());
		}
		list = ActivityList.getDayList(getActivity(), entry.getDate());
		adapter = new MyAdapter(getActivity(), R.layout.list_item, list);
//		readItems();
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
				respond(new Selection(Selection.Value.NA), position, view);
				break;
			case R.id.yes:
				respond(new Selection(Selection.Value.GOOD), position, view);
				break;
			case R.id.no_w:
				respond(new Selection(Selection.Value.OK), position, view);
				break;
			case R.id.no_wo:
				respond(new Selection(Selection.Value.BAD), position, view);
				break;
			default:
				return super.onContextItemSelected(item);
		}
		return true;

	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id)
	{
		respond(new Selection(entry.getSelectionAt(position)).getNext(), position, view);
	}

	private void respond(Selection selection, int position, View view)
	{
		entry.updateSelectionAt(position, selection.getValue());
		setIcon((TextView) view.findViewById(R.id.text), selection.getIcon());
		ApplicationEvaluation.getDatabase().update(entry.getDate(), entry.getSelections());
		PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
				.putLong("LAST_UPDATE", DateTime.now().getMillis()).apply();
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

//	private void readItems()
//	{
//		try
//		{
//			itemList.clear();
//			FileInputStream in = getActivity().openFileInput(EditSection.LIST_FILE);
//			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//			String line;
//			while ((line = reader.readLine()) != null)
//			{
//				if (isIncluded(line))
//				{
//					itemList.add(line);
//				}
//			}
//		}
//		catch (FileNotFoundException x)
//		{
//			boolean isMale = PreferenceManager
//					.getDefaultSharedPreferences(getActivity().getBaseContext())
//					.getBoolean("gender", true);
//
//			String items[] = getResources()
//					.getStringArray(isMale ? R.array.m_activities : R.array.f_activities);
//			for (String item : items)
//			{
//				if (isIncluded(item))
//				{
//					itemList.add(item);
//				}
//			}
//		}
//		catch (Exception x)
//		{
//			Log.e(getClass().getName(), x.toString());
//		}
//		adapter.notifyDataSetChanged();
//		ApplicationEvaluation.getDatabase().update(entry.getDate(), (short) itemList.size());
//		// PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putLong("LAST_UPDATE",
//		// Calendar.getInstance().getTimeInMillis()).commit();
//	}

	private boolean isIncluded(String s)
	{
		boolean isFriday = (new LocalDate(entry.getDate()).getDayOfWeek() == DateTimeConstants.FRIDAY);
		return !(((!entry.isRecitingDay()) && (s.contains(getString(R.string.recite_q)))) ||
				((!entry.isDietDay()) && (s.contains((getString(R.string.diet_q))))) ||
				((!entry.isMemorizingDay()) && (s.contains((getString(R.string.memorize_q))))) ||
				((!entry.isFastingDay()) && (s.contains((getString(R.string.fasting_q))))) ||
				((!isFriday) && (s.contains((getString(R.string.bath_q))))));
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
//				setIcon(textView, 0);
			}
			final String title = activity.getTitle(getContext());
			viewHolder.textView.setText(title);
			setIcon(viewHolder.textView, Selection.Icon.list[entry.getSelectionAt(position)]);

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
