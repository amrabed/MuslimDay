package amrabed.android.release.evaluation.plot;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.app.ApplicationEvaluation;
import amrabed.android.release.evaluation.core.Day;

/**
 * Bar chart
 *
 * @author AmrAbed
 */
public class StackedBarPlot
{
	private final Context context;
	private final BarData data;

	public StackedBarPlot(Context context)
	{
		this.context = context;
		data = setData();
	}

	private BarData setData()
	{
		final List<BarEntry> entries = new ArrayList<>();
		final List<Day> dayList = ApplicationEvaluation.getDatabase().getAllEntries();
		for (int i = 0; i < dayList.size(); i++)
		{
			final Day entry = dayList.get(i);
			final long diff = new Duration(entry.getDate(), DateTime.now().getMillis()).getStandardDays();
			final float total = (float) entry.getTotalNumber();
			final float none = total - entry.getBadRatio() - entry.getGoodRatio() - entry.getOkRatio();
			final float[] y = {entry.getGoodRatio(), entry.getOkRatio(), entry.getBadRatio(), none};
			entries.add(new BarEntry(diff, y));
		}

		final String[] labels = context.getResources().getStringArray(R.array.selection_labels);

		final BarDataSet dataset = new BarDataSet(entries, null);
		dataset.setColors(new int[]{R.color.yes, R.color.ok, R.color.no, android.R.color.darker_gray}, context);
		dataset.setStackLabels(labels);

		final BarData data = new BarData(dataset);
		data.setValueTextColor(Color.WHITE);
		data.setValueTextSize(12f);
		data.setValueFormatter(valueFormatter);
//		data.setHighlightEnabled(true);
//		data.setBarWidth(0.9f);
		return data;
	}

	public BarChart getChart(int id, View view)
	{
		final BarChart chart = (BarChart) view.findViewById(id);
		updateStyle(chart);
		chart.setData(data);
		chart.setFitBars(true);
		chart.invalidate();
		return chart;
	}

	private void updateStyle(BarChart chart)
	{
		chart.getAxisLeft().setAxisMinimum(0f);
		chart.getAxisRight().setEnabled(false);
		chart.getAxisLeft().setEnabled(false);
//		chart.setMaxVisibleValueCount(7);

		chart.setPinchZoom(false);
		chart.setDrawGridBackground(false);
		chart.setDrawBarShadow(false);
		chart.setDrawValueAboveBar(false);

		final Legend legend = chart.getLegend();
//		legend.setDrawInside(false);
		legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
		legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

		final XAxis xAxis = chart.getXAxis();
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis.setValueFormatter(dateFormatter);
		xAxis.setDrawGridLines(false);
		xAxis.setGranularity(1);
		xAxis.setGranularityEnabled(true);
//		xAxis.setLabelCount(7, true);

		chart.getDescription().setEnabled(false);
	}

	private static final IValueFormatter valueFormatter = new IValueFormatter()
	{
		@Override
		public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler)
		{
			if (value == 0) return "";
			return String.valueOf((int) value);
		}
	};

	private final IAxisValueFormatter dateFormatter = new IAxisValueFormatter()
	{
		@Override
		public String getFormattedValue(float value, AxisBase axis)
		{
			return LocalDate.now().minusDays((int) value).toString("EEE");
		}
	};

}
