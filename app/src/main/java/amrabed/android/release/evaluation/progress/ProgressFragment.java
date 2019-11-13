package amrabed.android.release.evaluation.progress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.Objects;

import amrabed.android.release.evaluation.R;


public class ProgressFragment extends Fragment {

    private static final String ID = "ID";
    private static final String TITLE = "TITLE";

    private String id;
    private String title;

    public static Fragment newInstance(String taskId, String title) {
        final ProgressFragment fragment = new ProgressFragment();
        Bundle args = new Bundle();
        args.putString(ID, taskId);
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).setTitle(title);
        final View view = inflater.inflate(R.layout.progress, parent, false);
        final ViewPager pager = view.findViewById(R.id.pager);

        final Bundle args = getArguments();
        if (args != null) {
            title = args.getString(TITLE);
            id = args.getString(ID);
        }
        pager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        return view;
    }

    private class PagerAdapter extends FragmentPagerAdapter {
        PagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return PieFragment.newInstance(id, position);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getStringArray(R.array.progress)[position];
        }

    }
}
