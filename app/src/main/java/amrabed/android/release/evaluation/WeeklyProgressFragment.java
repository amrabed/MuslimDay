package amrabed.android.release.evaluation;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.LineData;

import amrabed.android.release.evaluation.plot.LinePlot;

/**
 *
 */

public class WeeklyProgressFragment extends Fragment
{
	private LinePlot plot;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		plot = new LinePlot(getActivity());
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
	{
		final View view = inflater.inflate(R.layout.progress, parent, false);
		plot.getChart(R.id.chart, view);
		return view;
	}
}
