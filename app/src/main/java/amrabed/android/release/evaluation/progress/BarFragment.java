package amrabed.android.release.evaluation.progress;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.core.DayEntry;
import amrabed.android.release.evaluation.core.DayList;
import amrabed.android.release.evaluation.core.Selection;

public class BarFragment extends Fragment {

    private static final String POSITION = "Number of displayed days";
    private final int[] DAYS = {7, 30, 365};

    private int position;

    static BarFragment newInstance(int position) {
        final BarFragment fragment = new BarFragment();
        final Bundle args = new Bundle();
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.bar, parent, false);
        assert getArguments() != null;
        position = getArguments().getInt(POSITION);
        new StackedBarPlot(getContext()).getChart(view);
        return view;
    }

    /**
     * Bar chart
     */
    private class StackedBarPlot {
        private final Context context;
        private final BarData data;

        StackedBarPlot(Context context) {
            this.context = context;
            data = setData(DAYS[position]);
        }

        private BarData setData(int days) {
            final List<BarEntry> entries = new ArrayList<>();
            final List<DayEntry> dayList = DayList.get();
            final int n = dayList.size();
            for (int i = (n < days ? 0 : n - days); i < n; i++) {
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

        void getChart(View view) {
            final BarChart chart = view.findViewById(R.id.chart);
            chart.getAxisLeft().setAxisMinimum(0f);
            chart.getAxisRight().setEnabled(false);
            chart.getAxisLeft().setEnabled(false);
            chart.setMaxVisibleValueCount(10);

            chart.setDrawGridBackground(false);
            chart.setDrawBarShadow(false);
            chart.setDrawValueAboveBar(false);
            chart.getLegend().setEnabled(false);

            chart.enableScroll();

            final XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(dateFormatter[position]);
            xAxis.setAvoidFirstLastClipping(true);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularityEnabled(true);
            xAxis.setGranularity(position > 1 ? 30 : 1);

            chart.getDescription().setEnabled(false);
            chart.setScaleEnabled(false);

            chart.setData(data);

            chart.setFitBars(true);
            chart.invalidate();
        }

        private final IValueFormatter valueFormatter = (value, e, d, v) -> (value < 2) ? "" : String.valueOf((int) value);

        private final IAxisValueFormatter[] dateFormatter = {
                (value, axis) -> LocalDate.now().minusDays((int) value).toString("EEE"),
                (value, axis) -> LocalDate.now().minusDays((int) value).toString("d MMM"),
                (value, axis) -> LocalDate.now().minusDays((int) value).toString("MMM")
        };
    }
}
