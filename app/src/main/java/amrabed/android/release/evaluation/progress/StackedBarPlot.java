package amrabed.android.release.evaluation.progress;

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
import amrabed.android.release.evaluation.core.DayEntry;
import amrabed.android.release.evaluation.core.DayList;
import amrabed.android.release.evaluation.core.Selection;

/**
 * Bar chart
 */
class StackedBarPlot
{
	private final Context context;
	private final BarData data;

	StackedBarPlot(Context context)
	{
		this.context = context;
		data = setData();
	}

	private BarData setData()
	{
		final List<BarEntry> entries = new ArrayList<>();
		final DayList dayList = DayList.load();
		for (int i = 0; i < dayList.size(); i++)
		{
			final DayEntry entry = dayList.get(i);
			final long diff = new Duration(entry.getDate(), DateTime.now().getMillis()).getStandardDays();
			final float[] ratios = entry.getRatios(); // Not in the order we want
			final float[] y = {ratios[Selection.GOOD], ratios[Selection.OK], ratios[Selection.BAD],
					ratios[Selection.NONE]};
			entries.add(new BarEntry(diff, y));
		}

		final String[] labels = context.getResources().getStringArray(R.array.selection_labels);
		final int[] colors = Selection.getColors(); // Also not in the desired order
		final BarDataSet dataset = new BarDataSet(entries, null);
		dataset.setColors(new int[]{colors[Selection.GOOD], colors[Selection.OK],
				colors[Selection.BAD], colors[Selection.NONE]}, context);
		dataset.setStackLabels(labels);

		final BarData barData = new BarData(dataset);
		barData.setValueTextColor(Color.WHITE);
		barData.setValueTextSize(12f);
		barData.setValueFormatter(valueFormatter);
		barData.setHighlightEnabled(true);
		return barData;
	}

	void getChart(View view)
	{
		final BarChart chart = view.findViewById(R.id.chart);
		updateStyle(chart);
		chart.setData(data);
		chart.setFitBars(true);
		chart.invalidate();
	}

	private void updateStyle(BarChart chart)
	{
		chart.getAxisLeft().setAxisMinimum(0f);
		chart.getAxisRight().setEnabled(false);
		chart.getAxisLeft().setEnabled(false);
		chart.setMaxVisibleValueCount(40);

		chart.setPinchZoom(false);
		chart.setDrawGridBackground(false);
		chart.setDrawBarShadow(false);
		chart.setDrawValueAboveBar(false);

		final Legend legend = chart.getLegend();
		legend.setDrawInside(false);
		legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
		legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

		final XAxis xAxis = chart.getXAxis();
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis.setValueFormatter(dateFormatter);
		xAxis.setDrawGridLines(false);
		xAxis.setGranularity(1);
		xAxis.setGranularityEnabled(true);

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
