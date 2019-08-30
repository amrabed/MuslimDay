package amrabed.android.release.evaluation.progress;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

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
		getActivity().setTitle(R.string.menu_progress);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		final View view = inflater.inflate(R.layout.progress, parent, false);
		plot.getChart(view);
		return view;
	}
}