package amrabed.android.release.evaluation;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.LocalDate;

import java.util.List;

import amrabed.android.release.evaluation.app.ApplicationEvaluation;
import amrabed.android.release.evaluation.core.Day;

/**
 * Evaluation section
 *
 * @author AmrAbed
 */

public class EvaluationSection extends Fragment implements LoaderManager.LoaderCallbacks<List<Day>>
{
	private static final String TAG = EvaluationSection.class.getName();

	private List<Day> dayList;
	//	private DayList dayList;
	private ViewPager pager;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		final View view = inflater.inflate(R.layout.day_view, parent, false);
		pager = (ViewPager) view.findViewById(R.id.pager);

//		getLoaderManager().initLoader(0, null, this);
		//ToDo: Move to ASyncAdapter
		dayList = ApplicationEvaluation.getDatabase().getAllEntries();
		pager.setAdapter(new SectionsPagerAdapter(getChildFragmentManager()));
		pager.setCurrentItem(this.dayList.size() - 1);

		return view;
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
			return DaySection.getInstance(dayList.get(position).getDate());
		}

		@Override
		public int getCount()
		{
			return dayList.size();
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			return new LocalDate(dayList.get(position).getDate())
					.toString(getString(R.string.datetime_short_format_pattern));
		}
	}

	@Override
	public void onLoaderReset(Loader<List<Day>> loader)
	{
		Log.e(TAG, "OnLoaderReset");
	}

	@Override
	public Loader<List<Day>> onCreateLoader(int i, Bundle bundle)
	{
		return new AsyncTaskLoader<List<Day>>(getActivity())
		{
			@Override
			public List<Day> loadInBackground()
			{
				return ApplicationEvaluation.getDatabase().getAllEntries();
			}
		};
	}

	@Override
	public void onLoadFinished(Loader<List<Day>> loader, List<Day> dayList)
	{
		Log.i(TAG, "Load finished: " + dayList.toString());
		this.dayList = dayList;
		// ToDo: API 14
		pager.setAdapter(new SectionsPagerAdapter(getChildFragmentManager()));
		pager.setCurrentItem(this.dayList.size() - 1);

	}
}
