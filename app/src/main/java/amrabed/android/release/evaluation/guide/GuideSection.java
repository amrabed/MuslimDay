package amrabed.android.release.evaluation.guide;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.ListFragment;

import amrabed.android.release.evaluation.FragmentHelper;
import amrabed.android.release.evaluation.R;

/**
 * Guide section
 */

public class GuideSection extends ListFragment
{
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		if (getContext() != null) {
			setListAdapter(new ArrayAdapter<>(getContext(),
					android.R.layout.simple_list_item_activated_1,
					getResources().getStringArray(R.array.titles)));
		}
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id)
	{
		showDetails(position);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		FragmentHelper.setTitle(R.string.menu_guide, getActivity());
	}

	private static final int[] ENTRIES = {R.raw.wakeup, R.raw.brush, R.raw.night, R.raw.fasting,
			R.raw.sunna, R.raw.fajr, R.raw.quran, R.raw.memorize, R.raw.morning, R.raw.duha,
			R.raw.sports, R.raw.friday, R.raw.work, R.raw.cong, R.raw.prayer, R.raw.rawateb,
			R.raw.evening, R.raw.isha, R.raw.wetr, R.raw.diet, R.raw.manners, R.raw.honesty,
			R.raw.backbiting, R.raw.gaze, R.raw.wudu, R.raw.sleep};

	private void showDetails(int index)
	{
		FragmentHelper.loadFragment(DetailsFragment.newInstance(ENTRIES[index], getResources().getStringArray(R.array.titles)[index]), getFragmentManager());
	}
}
