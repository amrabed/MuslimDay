package amrabed.android.release.evaluation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import amrabed.android.release.evaluation.app.ApplicationEvaluation;
import amrabed.android.release.evaluation.db.DatabaseEntry;
import amrabed.android.release.evaluation.main.Selection;

public class DaySection extends ListFragment
{
	private static final String TAG = "args";

	DatabaseEntry entry;
	List<String> itemList = new ArrayList<>();
	MyAdapter adapter;

	public static DaySection getInstance(long date)
	{
		DaySection section = new DaySection();
		final Bundle args = new Bundle();
		args.putLong(TAG, date);
		section.setArguments(args);
		return section;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		getActivity().getMenuInflater().inflate(R.menu.main_options, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_sync:
				// ToDo: handle sync
				((MainActivity) getActivity()).handleSyncRequest();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		Bundle args = getArguments();
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
		adapter = new MyAdapter(getActivity(), android.R.layout.simple_list_item_1, itemList);
		readItems();
		setListAdapter(adapter);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		getActivity().setTitle(getResources().getStringArray(R.array.sections)[0]);
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
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		View view = info.targetView;
		int position = info.position;

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
	public void onListItemClick(ListView l, View view, int position, long id)
	{
		respond(new Selection(entry.getSelectionAt(position)).getNext(), position, view);
	}

	private void respond(Selection selection, int position, View view)
	{
		entry.updateSelectionAt(position, selection.getValue());
		setIcon((TextView) view.findViewById(android.R.id.text1), selection.getIcon());
		ApplicationEvaluation.getDatabase().update(entry.getDate(), entry.getSelections());
		PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
				.putLong("LAST_UPDATE", Calendar.getInstance().getTimeInMillis()).apply();
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
//		if(icon == 0)
//		{
//			textView.setActivated(false);
//		}
//		else
//		{
//			textView.setActivated(true);
//		}

	}

	private void readItems()
	{
		try
		{
			itemList.clear();
			FileInputStream in = getActivity().openFileInput(EditSection.LIST_FILE);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (isIncluded(line))
				{
					itemList.add(line);
				}
			}
		}
		catch (FileNotFoundException x)
		{
			boolean isMale = PreferenceManager
					.getDefaultSharedPreferences(getActivity().getBaseContext())
					.getBoolean("gender", true);

			String items[] = getResources()
					.getStringArray(isMale ? R.array.m_activities : R.array.f_activities);
			for (String item : items)
			{
				if (isIncluded(item))
				{
					itemList.add(item);
				}
			}
		}
		catch (Exception x)
		{
			Log.e(getClass().getName(), x.toString());
		}
		adapter.notifyDataSetChanged();
		ApplicationEvaluation.getDatabase().update(entry.getDate(), (short) itemList.size());
		// PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putLong("LAST_UPDATE",
		// Calendar.getInstance().getTimeInMillis()).commit();
	}

	private boolean isIncluded(String s)
	{
		boolean isFriday = (new LocalDate(entry.getDate())
				.getDayOfWeek() == DateTimeConstants.FRIDAY);
		return !(((!entry.isRecitingDay()) && (s.contains(getString(R.string.recite_q)))) ||
				((!entry.isDietDay()) && (s.contains((getString(R.string.diet_q))))) ||
				((!entry.isMemorizingDay()) && (s.contains((getString(R.string.memorize_q))))) ||
				((!entry.isFastingDay()) && (s.contains((getString(R.string.fasting_q))))) ||
				((!isFriday) && (s.contains((getString(R.string.bath_q))))));
	}

	class MyAdapter extends ArrayAdapter<String>
	{
		MyAdapter(Context context, int layout, List<String> list)
		{
			super(context, layout, list);
		}

		@NonNull
		@Override
		public View getView(int position, View view, @NonNull ViewGroup parent)
		{
			TextView textView;
			String txt = itemList.get(position);
			if (view == null)
			{
				view = LayoutInflater.from(getActivity())
						.inflate(android.R.layout.simple_list_item_activated_1, parent, false);
				textView = (TextView) view.findViewById(android.R.id.text1);
				view.setTag(textView);
			}
			else
			{
				textView = (TextView) view.getTag();
				setIcon(textView, 0);
			}
			textView.setText(txt);
			setIcon(textView, Selection.Icon.list[entry.getSelectionAt(position)]);
			return view;
		}
	}

}
