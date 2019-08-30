package amrabed.android.release.evaluation.eval;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.core.DayList;

/**
 * Evaluation section
 */

public class EvaluationSection extends Fragment //implements LoaderManager.LoaderCallbacks<List<Day>>
{
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		final View view = inflater.inflate(R.layout.day_view, parent, false);
		final ViewPager pager = view.findViewById(R.id.pager);

		//ToDo: Move to AsyncTaskLoader
		final DayList dayList = DayList.load();
		pager.setAdapter(new SectionPagerAdapter(getActivity(), getChildFragmentManager(), dayList));
		pager.setCurrentItem(dayList.size() - 1);

		return view;
	}
}
