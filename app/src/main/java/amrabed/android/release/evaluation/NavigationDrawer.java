package amrabed.android.release.evaluation;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;

import amrabed.android.release.evaluation.about.AboutSection;
import amrabed.android.release.evaluation.about.HelpSection;
import amrabed.android.release.evaluation.eval.EvaluationSection;
import amrabed.android.release.evaluation.guide.GuideSection;
import amrabed.android.release.evaluation.preferences.PreferenceSection;
import amrabed.android.release.evaluation.preferences.SettingsSection;
import amrabed.android.release.evaluation.progress.ProgressSection;

/**
 * Navigation Drawer
 */
// ToDo: Simplify
public class NavigationDrawer implements NavigationView.OnNavigationItemSelectedListener,
        FragmentManager.OnBackStackChangedListener {
    private static final String FRAGMENT_KEY = "Current fragment";

    private final MainActivity activity;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private Fragment fragment;

    NavigationDrawer(MainActivity activity) {
        this.activity = activity;
    }

    NavigationDrawer create(Bundle savedInstanceState, Toolbar toolbar) {

        drawer = activity.findViewById(R.id.drawer_layout);

        navigationView = activity.findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState != null) {
            fragment = getFragmentManager().getFragment(savedInstanceState, FRAGMENT_KEY);
            selectItem(fragment);
        } else {
            loadFragment(R.id.nav_eval);
        }

        getFragmentManager().addOnBackStackChangedListener(this);

        return this;
    }

    boolean isOpen() {
        return drawer.isDrawerOpen(navigationView);
    }

    void saveState(Bundle outState) {
        getFragmentManager().putFragment(outState, FRAGMENT_KEY, fragment);
    }

    void close() {
        drawer.closeDrawers();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        loadFragment(item.getItemId());
        close();
        return true;
    }

    @Override
    public void onBackStackChanged() {
        selectItem(getFragmentManager().findFragmentById(R.id.content));
        activity.invalidateOptionsMenu();
    }

    private void loadFragment(int id) {
        switch (id) {
            case R.id.nav_eval:
                activity.setTitle(R.string.evaluation);
                fragment = new EvaluationSection();
                getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
                return;
            case R.id.nav_progress:
                fragment = new ProgressSection();
                break;
            case R.id.nav_edit:
                drawer.closeDrawer(navigationView);
                activity.startEditorActivity();
                return;
            case R.id.nav_preferences:
                fragment = new PreferenceSection();
                break;
            case R.id.nav_Settings:
                fragment = new SettingsSection();
                break;
            case R.id.nav_guide:
                fragment = new GuideSection();
                break;
            case R.id.nav_help:
                fragment = new HelpSection();
                break;
            case R.id.nav_about:
                fragment = new AboutSection();
                break;
            case R.id.nav_sign_out:
                activity.signOut();
                return;
            default:
        }
        selectItem(fragment);
        getFragmentManager().beginTransaction().addToBackStack(null)
                .replace(R.id.content, fragment).commit();
    }

    private FragmentManager getFragmentManager() {
        return activity.getSupportFragmentManager();
    }

    private void selectItem(Fragment fragment) {
        if (fragment != null) {
            Integer index = map.get(fragment.getClass());
            if (index != null) {
                navigationView.getMenu().getItem(index).setChecked(true);
                activity.setTitle(TITLES[index]);
            }
        }
    }

    private static final HashMap<Class<? extends Fragment>, Integer> map = new HashMap<>();

    static {
        map.put(EvaluationSection.class, 0);
        map.put(ProgressSection.class, 1);
        map.put(PreferenceSection.class, 3);
        map.put(SettingsSection.class, 4);
        map.put(GuideSection.class, 5);
        map.put(HelpSection.class, 6);
        map.put(AboutSection.class, 7);
    }

    private static final int[] TITLES = {R.string.evaluation, R.string.menu_progress,
            R.string.menu_edit, R.string.menu_preferences,
            R.string.menu_settings, R.string.menu_guide, R.string.menu_help, R.string.menu_about};
}
