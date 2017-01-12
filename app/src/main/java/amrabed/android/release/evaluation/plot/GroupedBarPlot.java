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

public class GroupedBarPlot extends Plot
{
	GroupedBarPlot(Context context)
	{
		super(context);
	}

	public BarData setData(Context context)
	{
		final List<BarEntry> yesEntries = new ArrayList<>();
		final List<BarEntry> okEntries = new ArrayList<>();
		final List<BarEntry> noEntries = new ArrayList<>();

		for (DatabaseEntry entry : ApplicationEvaluation.getDatabase().getAllEntries())
		{
			final float total = (float) entry.getTotalNumber();
			final float okRatio = total - entry.getBadRatio() - entry.getGoodRatio();
			yesEntries.add(new BarEntry(entry.getDate(), entry.getGoodRatio()));
			okEntries.add(new BarEntry(entry.getDate(), okRatio));
			noEntries.add(new BarEntry(entry.getDate(), entry.getBadRatio()));
		}
//		final String[] labels = {, context.getString(R.string.no_w),
//				context.getString(R.string.no_wo)};
//		final BarDataSet dataset = new BarDataSet(entries, null);
//		dataset.setColors(new int[]{R.color.yes, R.color.ok, R.color.no}, context);
//		dataset.setStackLabels(labels);
//		dataset.setDrawValues(true);
//		dataset.setVisible(true);
		final BarData data = new BarData(
				new BarDataSet(yesEntries, context.getString(R.string.yes)),
				new BarDataSet(okEntries, context.getString(R.string.no_w)),
				new BarDataSet(noEntries, context.getString(R.string.no_wo)));
		data.setBarWidth(0.4f);
		return data;
	}

	public BarChart getChart(int id, View view)
	{
		final BarChart chart = (BarChart) view.findViewById(id);
		chart.setDrawGridBackground(false);
		chart.setNoDataText("No data available yet");
		chart.getXAxis().setValueFormatter(getDateFormatter());
		chart.setData((BarData) getData());
		chart.groupBars(0, 0.05f, 0.02f);
		chart.invalidate();
		return chart;
	}
}
