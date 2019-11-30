package amrabed.android.release.evaluation.eval;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import org.joda.time.LocalDate;

import java.util.List;
import java.util.Objects;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.data.entities.Day;
import amrabed.android.release.evaluation.data.models.DayViewModel;

/**
 * Evaluation section
 */
public class EvaluationSection extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.day_view, parent, false);
        final ViewPager pager = view.findViewById(R.id.pager);

        ViewModelProviders.of(this).get(DayViewModel.class).getDayList()
                .observe(this, dayList -> {
                    pager.setAdapter(new SectionPagerAdapter(dayList));
                    pager.setCurrentItem(dayList.size() - 1);
                });
        return view;
    }

    private class SectionPagerAdapter extends FragmentPagerAdapter {
        private final List<Day> dayList;

        private SectionPagerAdapter(List<Day> dayList) {
            super(getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.dayList = dayList;
        }

        @Override
        @NonNull
        public Fragment getItem(int position) {
            ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                    .get(String.valueOf(position), DayViewModel.class)
                    .select(dayList.get(position));
            return DayFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return dayList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return new LocalDate(dayList.get(position).date)
                    .toString(getString(R.string.datetime_short_format_pattern));
        }
    }
}