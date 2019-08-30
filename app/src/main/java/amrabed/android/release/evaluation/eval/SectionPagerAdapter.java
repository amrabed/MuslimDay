package amrabed.android.release.evaluation.eval;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;

import androidx.legacy.app.FragmentPagerAdapter;

import org.joda.time.LocalDate;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.core.DayList;

/**
 * PagerAdapter for scrolling between days
 */

class SectionPagerAdapter extends FragmentPagerAdapter
{
	private final Context context;
	private final DayList dayList;

	SectionPagerAdapter(Context context, FragmentManager manager, DayList dayList)
	{
		super(manager);
		this.context = context;
		this.dayList = dayList;
	}

	@Override
	public Fragment getItem(int position)
	{
		return DayFragment.getInstance(dayList.get(position));
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
				.toString(context.getString(R.string.datetime_short_format_pattern));
	}
}

