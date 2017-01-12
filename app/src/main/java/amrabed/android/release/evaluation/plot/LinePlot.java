package amrabed.android.release.evaluation.plot;

import android.content.Context;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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

public class LinePlot extends Plot
{
	private final LineData data;

	public LinePlot(Context context)
	{
		super(context);
		data = setData(context);
	}

	protected LineData setData(Context context)
	{
		final List<Entry> yesEntries = new ArrayList<>();
		final List<Entry> okEntries = new ArrayList<>();
		final List<Entry> noEntries = new ArrayList<>();

		for (DatabaseEntry entry : ApplicationEvaluation.getDatabase().getAllEntries())
		{
			final float total = (float) entry.getTotalNumber();
			final float okRatio = total - entry.getBadRatio() - entry.getGoodRatio();
			yesEntries.add(new Entry(entry.getDate(), entry.getGoodRatio()));
			okEntries.add(new Entry(entry.getDate(), okRatio));
			noEntries.add(new Entry(entry.getDate(), entry.getBadRatio()));
		}

		final LineDataSet dataset1 = new LineDataSet(yesEntries, context.getString(R.string.yes));
		dataset1.setAxisDependency(YAxis.AxisDependency.LEFT);
		dataset1.setColor(context.getResources().getColor(R.color.yes));
		final LineDataSet dataset2 = new LineDataSet(okEntries, context.getString(R.string.no_w));
		dataset2.setAxisDependency(YAxis.AxisDependency.LEFT);
		dataset2.setColor(context.getResources().getColor(R.color.ok));
		final LineDataSet dataset3 = new LineDataSet(noEntries, context.getString(R.string.no_wo));
		dataset3.setAxisDependency(YAxis.AxisDependency.LEFT);
		dataset3.setColor(context.getResources().getColor(R.color.no));

		return new LineData(dataset1, dataset2, dataset3);
	}

	public LineData getData()
	{
		return data;
	}

	@Override
	public LineChart getChart(int id, View view)
	{
		final LineChart chart = (LineChart) view.findViewById(id);
		chart.setDrawGridBackground(false);
		chart.setPadding(10, 0, 10, 0);
		chart.getXAxis().setValueFormatter(getDateFormatter());
		chart.setData(data);
		chart.invalidate();
		return chart;
	}


//	public LineChart getChart()
//	{
//		final LineChart chart = new LineChart(context);
//		chart.setDrawGridBackground(false);
//		chart.setNoDataText("No data available yet");
//		chart.getXAxis().setValueFormatter(new IAxisValueFormatter()
//		{
//			@Override
//			public String getFormattedValue(float value, AxisBase axis)
//			{
//				return new DateTime((long) value).toString("E d MMM");
//			}
//		});
//		chart.setData(data);
////		chart.groupBars(0, 0.05f, 0.02f);
//		chart.invalidate();
//		return chart;
//	}
}
