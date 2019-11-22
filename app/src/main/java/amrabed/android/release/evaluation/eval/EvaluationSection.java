package amrabed.android.release.evaluation.eval;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.joda.time.LocalDate;

import java.util.List;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.core.DayEntry;
import amrabed.android.release.evaluation.core.DayList;

/**
 * Evaluation section
 */

public class EvaluationSection extends Fragment
{
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		final View view = inflater.inflate(R.layout.day_view, parent, false);
		final ViewPager pager = view.findViewById(R.id.pager);

		//ToDo: Move to AsyncTaskLoader
		final List<DayEntry> dayList = DayList.get();
		pager.setAdapter(new SectionPagerAdapter(getActivity(), getChildFragmentManager()));
		pager.setCurrentItem(dayList.size() - 1);

		return view;
	}

	private class SectionPagerAdapter extends FragmentPagerAdapter {
		private final Context context;
		private final List<DayEntry> dayList;

		SectionPagerAdapter(Context context, FragmentManager manager) {
			super(manager, SectionPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
			this.context = context;
			this.dayList = DayList.get();
		}

		@Override
		@NonNull
		public Fragment getItem(int position) {
			return DayFragment.getInstance(dayList.get(position));
		}

		@Override
		public int getCount() {
			return dayList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return new LocalDate(dayList.get(position).getDate()).toString(context.getString(R.string.datetime_short_format_pattern));
		}
	}
}
