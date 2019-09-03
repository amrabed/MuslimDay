package amrabed.android.release.evaluation.eval;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.joda.time.LocalDate;

import java.util.List;

import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.core.DayEntry;
import amrabed.android.release.evaluation.core.DayList;

/**
 * PagerAdapter for scrolling between days
 */
class SectionPagerAdapter extends FragmentPagerAdapter {
    private final Context context;
    private final List<DayEntry> dayList;

    SectionPagerAdapter(Context context, FragmentManager manager) {
        super(manager);
        this.context = context;
        this.dayList = DayList.get();
    }

    @Override
    public Fragment getItem(int position) {
        return DayFragment.getInstance(dayList.get(position));
    }

    @Override
    public int getCount() {
        return dayList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return new LocalDate(dayList.get(position).getDate())
                .toString(context.getString(R.string.datetime_short_format_pattern));
    }
}

