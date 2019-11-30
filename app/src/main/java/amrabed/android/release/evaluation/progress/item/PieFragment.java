package amrabed.android.release.evaluation.progress.item;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.core.Selection;
import amrabed.android.release.evaluation.data.entities.Day;
import amrabed.android.release.evaluation.data.models.DayViewModel;
import amrabed.android.release.evaluation.locale.LocaleManager;

public class PieFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private static final String ID = "ID";
    private static final String POSITION = "POSITION";

    private final int[] DAYS = {7, 30, 365};

    private String id;
    private int position;

    private PieChart primaryPieChart;
    private PieChart secondaryPieChart;

    private TextView progressText;
    private PieDataSet primaryDataSet;
    private PieDataSet secondaryDataSet;

    private List<Day> dayList;

    static Fragment newInstance(String taskId, int position) {
        final PieFragment fragment = new PieFragment();
        Bundle args = new Bundle();
        args.putString(ID, taskId);
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.pie, container, false);
        final Bundle args = getArguments();
        if (args != null) {
            position = args.getInt(POSITION);
            id = args.getString(ID);
        }

        ViewModelProviders.of(this).get(DayViewModel.class).getDayList()
                .observe(this, dayList -> {
                    this.dayList = dayList;
                    if (position == DAYS.length) {
                        view.findViewById(R.id.num_days).setVisibility(View.VISIBLE);
                        final SeekBar seekBar = view.findViewById(R.id.seekbar);
                        seekBar.setMax(dayList.size());
                        seekBar.setProgress(dayList.size() / 2 + 1);
                        seekBar.setOnSeekBarChangeListener(this);
                        progressText = view.findViewById(R.id.seekbar_value);
                        setProgressText(dayList.size() / 2 + 1, seekBar);
                        ((TextView) view.findViewById(R.id.min)).setText(R.string.min);
                        final String max = "" + dayList.size();
                        ((TextView) view.findViewById(R.id.max)).setText(max);
                    }

                    primaryPieChart = view.findViewById(R.id.current);
                    secondaryPieChart = view.findViewById(R.id.previous);
                    primaryDataSet = formatPieChart(primaryPieChart, true);
                    secondaryDataSet = formatPieChart(secondaryPieChart, false);
                    updatePieCharts(position == DAYS.length ? dayList.size() / 2 + 1 : DAYS[position]);
                });

        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        updatePieCharts(progress);
        setProgressText(progress, seekBar);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void updatePieCharts(int nDays) {
        UpdatePieChart(primaryPieChart, primaryDataSet, getRange(nDays));
        UpdatePieChart(secondaryPieChart, secondaryDataSet, getRange(nDays, dayList.size() - nDays));
    }

    private void UpdatePieChart(final PieChart pieChart, final PieDataSet dataSet, final List<Day> days) {
        final int[] counters = new int[4];
        for (Day day : days) {
            if (id != null) {
                // Get task selection for the day
                counters[day.getSelection(id)]++;
            }
        }
        dataSet.setValues(getEntries(counters));
        pieChart.setData(new PieData(dataSet));
        pieChart.invalidate();
    }

    private PieDataSet formatPieChart(PieChart pieChart, boolean isPrimary) {
//        final int description = isPrimary ? R.string.current_period : R.string.previous_period;
        final int textSize = isPrimary ? 18 : 12;

        final String text = isPrimary ? getResources().getStringArray(R.array.current_period)[position] : getResources().getStringArray(R.array.previous_period)[position];

        pieChart.setCenterText(text);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setCenterTextSize(textSize);
        pieChart.setRotationEnabled(false);
        pieChart.setTransparentCircleRadius(0);
        pieChart.setHoleColor(Color.TRANSPARENT);
//        pieChart.setHoleRadius(75);

        final PieDataSet dataSet = new PieDataSet(null, null);
        dataSet.setColors(getColors());
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(textSize);
        dataSet.setSliceSpace(1);
        dataSet.setValueFormatter((value, e, d, v) -> "" + (value > 0 ? ((int) value) : ""));

        return dataSet;
    }

    private ArrayList<PieEntry> getEntries(int[] count) {
        final ArrayList<PieEntry> entries = new ArrayList<>(4);
        for (int i = 0; i < count.length; i++) {
            entries.add(new PieEntry((float) count[i], i));
        }
        return entries;
    }

    private int[] getColors() {
        final int[] colors = new int[4];
        final Resources resources = getResources();
        final int[] selectionColors = Selection.getColors();
        for (int i = 0; i < selectionColors.length; i++) {
            colors[i] = resources.getColor(selectionColors[i]);
        }
        return colors;
    }

    private void setProgressText(int progress, SeekBar seekBar) {
        final String value = "" + progress;
        float position = seekBar.getX() - progressText.getWidth() / 2.0f;
        final Context context = getContext();
        if (context != null && LocaleManager.isEnglish(context)) {
            position += progress * seekBar.getWidth() / seekBar.getMax();
        } else {
            position += seekBar.getWidth() - progress * seekBar.getWidth() / seekBar.getMax();
        }
        progressText.setVisibility((progress == seekBar.getMax() || progress == 0) ? View.INVISIBLE : View.VISIBLE);
        progressText.setText(value);
        progressText.setX(position);
    }

    private List<Day> getRange(int rangeSize) {
        return getRange(rangeSize, dayList.size());
    }

    private List<Day> getRange(int rangeSize, int end) {
        final int start = end - rangeSize;
        return dayList.subList(start >= 0 ? start : 0, end > 0 ? end : 0);
    }
}