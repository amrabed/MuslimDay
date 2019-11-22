package amrabed.android.release.evaluation.progress;

import android.app.Activity;
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

import amrabed.android.release.evaluation.R;

/**
 * Progress Section
 */
public class ProgressSection extends Fragment
{
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		final View view = inflater.inflate(R.layout.progress, parent, false);
		final ViewPager pager = view.findViewById(R.id.pager);

		pager.setAdapter(new PagerAdapter(getChildFragmentManager()));
		return view;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Activity activity = getActivity();
		if (activity != null) {
			activity.setTitle(R.string.menu_progress);
		}
	}

	private class PagerAdapter extends FragmentPagerAdapter {
		PagerAdapter(FragmentManager manager) {
			super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		}

		@NonNull
		@Override
		public Fragment getItem(int position) {
			return BarFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getResources().getStringArray(R.array.progress)[position];
		}
	}

}