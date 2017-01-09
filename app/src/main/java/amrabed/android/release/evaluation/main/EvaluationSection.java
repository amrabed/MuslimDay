package amrabed.android.release.evaluation.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.LocalDate;

import java.util.List;

import amrabed.android.release.evaluation.DaySection;
import amrabed.android.release.evaluation.MainActivity;
import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.app.ApplicationEvaluation;
import amrabed.android.release.evaluation.db.DatabaseEntry;

/**
 * Evaluation section
 *
 * @author AmrAbed
 */

public class EvaluationSection extends Fragment
{
	private List<DatabaseEntry> entries;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		final View view = inflater.inflate(R.layout.activity_main, null);

		//ToDo: Move to ASyncAdapter
		entries = ApplicationEvaluation.getDatabase().getAllEntries();
		final ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
		pager.setAdapter(new SectionsPagerAdapter(getChildFragmentManager()));

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main_options, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_sync:
				((MainActivity) getActivity()).handleSyncRequest();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{

		SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			return DaySection.getInstance(entries.get(position).getDate());
		}

		@Override
		public int getCount()
		{
			return entries.size();
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			return new LocalDate(entries.get(position).getDate()).toString("EEE");
		}
	}


}
