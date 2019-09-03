package amrabed.android.release.evaluation.progress;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import amrabed.android.release.evaluation.R;

/**
 * Progress Section
 */
public class ProgressSection extends Fragment
{
	private StackedBarPlot plot;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		plot = new StackedBarPlot(getActivity());
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

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		final View view = inflater.inflate(R.layout.bar, parent, false);
		plot.getChart(view);
		return view;
	}
}