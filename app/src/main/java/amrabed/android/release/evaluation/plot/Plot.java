package amrabed.android.release.evaluation.plot;

import android.content.Context;
import android.view.View;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.ChartData;

/**
 * Plot class
 *
 * @author AmrAbed
 */

abstract class Plot
{
	private final Context context;
	private final ChartData data;

	Plot(Context context)
	{
		this.context = context;
		data = setData(context);
	}

	public Context getContext()
	{
		return context;
	}

	public ChartData getData()
	{
		return data;
	}

	protected abstract ChartData setData(Context context);

	protected abstract Chart getChart(int id, View view);
}
