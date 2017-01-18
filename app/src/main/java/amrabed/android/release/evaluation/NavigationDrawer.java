package amrabed.android.release.evaluation;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Navigation Drawer
 *
 * @author AmrAbed
 */

public class NavigationDrawer implements NavigationView.OnNavigationItemSelectedListener
{
	private static final String INDEX_KEY = "Navigation index key";

	private final Activity activity;

	private int currentIndex;

	private DrawerLayout drawer;
	private NavigationView navigationView;

	public NavigationDrawer(Activity activity)
	{
		this.activity = activity;
	}

	public NavigationDrawer create(Bundle savedInstanceState, Toolbar toolbar)
	{
		if (savedInstanceState != null)
		{
			currentIndex = savedInstanceState.getInt(INDEX_KEY);
		}

		loadFragment(currentIndex);

		drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);

		navigationView = (NavigationView) activity.findViewById(R.id.navigation);
		navigationView.setNavigationItemSelectedListener(this);
		navigationView.getMenu().getItem(currentIndex).setChecked(true);

		final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer,
				toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

		drawer.addDrawerListener(toggle);
		toggle.syncState();

		return this;
	}

	public boolean isOpen()
	{
		return drawer.isDrawerOpen(navigationView);
	}

	public void saveState(Bundle outState)
	{
		outState.putInt(INDEX_KEY, currentIndex);
	}

	public void close()
	{
		drawer.closeDrawers();
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.nav_eval:
			case R.id.nav_progress:
			case R.id.nav_guide:
			case R.id.nav_preferences:
			case R.id.nav_Settings:
			case R.id.nav_edit:
				loadFragment(item.getItemId());
				break;
			case R.id.nav_help:
				new android.app.AlertDialog.Builder(activity)
						.setTitle(R.string.menu_help)
						.setView(activity.getLayoutInflater().inflate(R.layout.help_dialog, null))
						.create().show();
				break;
			case R.id.nav_about:
				new android.app.AlertDialog.Builder(activity)
						.setTitle(R.string.menu_about)
						.setMessage(R.string.about_content1)
						.create().show();
				break;
		}
		activity.invalidateOptionsMenu();
		close();
		return true;
	}

	public void loadFragment(int id)
	{
		currentIndex = id;
		Fragment fragment;
		switch (currentIndex)
		{
			case R.id.nav_Settings:
				activity.setTitle(R.string.menu_settings);
				fragment = new SettingsSection();
				break;
			case R.id.nav_edit:
				activity.setTitle(R.string.menu_edit);
				fragment = new EditSection();
//				fragment = new Editor();
				break;
			case R.id.nav_progress:
				activity.setTitle(R.string.menu_progress);
				fragment = new ProgressSection();
				break;
			case R.id.nav_guide:
				activity.setTitle(R.string.menu_guide);
				fragment = new GuideSection();
				break;
			case R.id.nav_preferences:
				activity.setTitle(R.string.menu_preferences);
				fragment = new PreferenceSection();
				break;
			case R.id.nav_eval:
			default:
				activity.setTitle(R.string.evaluation);
				fragment = new EvaluationSection();
		}
		activity.getFragmentManager().beginTransaction()//.addToBackStack(null)
				.replace(R.id.content, fragment).commit();
	}
}
