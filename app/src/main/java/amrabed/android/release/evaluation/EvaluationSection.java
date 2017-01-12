package amrabed.android.release.evaluation;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.LocalDate;

import java.util.List;

import amrabed.android.release.evaluation.app.ApplicationEvaluation;
import amrabed.android.release.evaluation.db.DatabaseEntry;

/**
 * Evaluation section
 *
 * @author AmrAbed
 */

public class EvaluationSection extends Fragment
{
    private List<DatabaseEntry> entries;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.day_view, parent, false);

        //ToDo: Move to ASyncAdapter
        entries = ApplicationEvaluation.getDatabase().getAllEntries();

        final ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        // ToDo: API 14
        pager.setAdapter(new SectionsPagerAdapter(getChildFragmentManager()));
        pager.setCurrentItem(entries.size() - 1);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_sync:
                ((MainActivity) getActivity()).sync();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return DaySection.getInstance(entries.get(position).getDate());
        }

        @Override
        public int getCount()
        {
            return entries.size();
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return new LocalDate(entries.get(position).getDate())
                    .toString(getString(R.string.datetime_short_format_pattern));
        }
    }


}
