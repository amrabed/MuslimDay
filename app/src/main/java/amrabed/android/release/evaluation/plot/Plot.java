package amrabed.android.release.evaluation.plot;

import android.content.Context;
import android.view.View;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.joda.time.DateTime;

import amrabed.android.release.evaluation.R;

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

    public IAxisValueFormatter getDateFormatter()
    {
        return dateFormatter;
    }

    protected abstract ChartData setData(Context context);

    protected abstract Chart getChart(int id, View view);

    private final IAxisValueFormatter dateFormatter = new IAxisValueFormatter()
    {
        @Override
        public String getFormattedValue(float value, AxisBase axis)
        {
            return new DateTime((long) value).toString(context.getString(R.string.datetime_short_format_pattern));
        }
    };

}
