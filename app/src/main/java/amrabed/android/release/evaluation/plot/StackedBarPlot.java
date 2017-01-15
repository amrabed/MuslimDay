package amrabed.android.release.evaluation.plot;

import android.content.Context;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

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
	private final BarData data;

	private int count;

	public StackedBarPlot(Context context)
	{
		super(context);
		data = setData(context);
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
		count = entries.size();

		final String[] labels = context.getResources().getStringArray(R.array.selection_labels);

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
		updateStyle(chart);
		chart.setData(data);
		chart.setFitBars(true);
		chart.invalidate();
		return chart;
	}

	protected void updateStyle(BarChart chart)
	{
		chart.getAxisRight().setEnabled(false);
		chart.getAxisLeft().setEnabled(false);

		chart.setMaxVisibleValueCount(40);
		chart.setPinchZoom(false);
		chart.setDrawGridBackground(false);
		chart.setDrawBarShadow(false);
		chart.setDrawValueAboveBar(false);

		final Legend legend = chart.getLegend();
		legend.setDrawInside(true);
		legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
		legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
//        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);

		final XAxis xAxis = chart.getXAxis();
		xAxis.setValueFormatter(getDateFormatter());
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis.setDrawGridLines(false);
		xAxis.setLabelCount(count);

		chart.getDescription().setEnabled(false);
	}
}
