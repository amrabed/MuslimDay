package amrabed.android.release.evaluation.plot;

import android.content.Context;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.app.ApplicationEvaluation;
import amrabed.android.release.evaluation.db.DatabaseEntry;

/**
 * Bar chart
 *
 * @author AmrAbed
 */
public class StackedBarPlot extends Plot
{
	final BarData data;

	public StackedBarPlot(Context context)
	{
		super(context);
		data = setData(getContext());
	}

	@Override
	protected BarData setData(Context context)
	{
		final List<BarEntry> entries = new ArrayList<>();
		for (DatabaseEntry entry : ApplicationEvaluation.getDatabase().getAllEntries())
		{
			final float total = (float) entry.getTotalNumber();
			final float okRatio = total - entry.getBadRatio() - entry.getGoodRatio();
			final float[] y = {100 * entry.getGoodRatio() / total,
					100 * okRatio / total, 100 * entry.getBadRatio() / total};
			entries.add(new BarEntry(entry.getDate(), y));
		}

		final String[] labels = {context.getString(R.string.yes), context.getString(R.string.no_w),
				context.getString(R.string.no_wo)};

		final BarDataSet dataset = new BarDataSet(entries, null);
		dataset.setColors(new int[]{R.color.yes, R.color.ok, R.color.no}, context);
		dataset.setStackLabels(labels);
		dataset.setDrawValues(true);
		dataset.setVisible(true);

		final BarData data = new BarData(dataset);
		data.setBarWidth(0.9f);
		return data;
	}

	@Override
	public BarChart getChart(int id, View view)
	{
		final BarChart chart = (BarChart) view.findViewById(id);
		chart.setDrawGridBackground(false);
		chart.setNoDataText("No data available yet");
		chart.setFitBars(true);
		chart.getXAxis().setValueFormatter(getDateFormatter());
		chart.setData(data);
		chart.invalidate();
		return chart;
	}
}
