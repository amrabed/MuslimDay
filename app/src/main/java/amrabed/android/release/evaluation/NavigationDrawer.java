package amrabed.android.release.evaluation;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import amrabed.android.release.evaluation.edit.EditSection;
import amrabed.android.release.evaluation.eval.EvaluationSection;
import amrabed.android.release.evaluation.guide.GuideSection;
import amrabed.android.release.evaluation.preferences.PreferenceSection;
import amrabed.android.release.evaluation.progress.ProgressSection;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

/**
 * Navigation Drawer
 *
 * @author AmrAbed
 */
// ToDo: Simplify
public class NavigationDrawer implements NavigationView.OnNavigationItemSelectedListener,
		FragmentManager.OnBackStackChangedListener
{
	private static final String INDEX_KEY = "Navigation index key";
	private static final String FRAGMENT_KEY = "Current fragment";

	private final Activity activity;

	private int currentIndex;

	private DrawerLayout drawer;
	private NavigationView navigationView;

	private Fragment fragment;

	NavigationDrawer(Activity activity)
	{
		this.activity = activity;
	}

	public NavigationDrawer create(Bundle savedInstanceState, Toolbar toolbar)
	{

		drawer = activity.findViewById(R.id.drawer_layout);

		navigationView = activity.findViewById(R.id.navigation);
		navigationView.setNavigationItemSelectedListener(this);

		final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer,
				toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

		drawer.addDrawerListener(toggle);
		toggle.syncState();

		if (savedInstanceState != null)
		{
			currentIndex = savedInstanceState.getInt(INDEX_KEY);
			fragment = getFragmentManager().getFragment(savedInstanceState, FRAGMENT_KEY);
		}
		else
		{
			loadFragment(R.id.nav_eval);
		}

		activity.getFragmentManager().addOnBackStackChangedListener(this);

		return this;
	}

	boolean isOpen()
	{
		return drawer.isDrawerOpen(navigationView);
	}

	void saveState(Bundle outState)
	{
		outState.putInt(INDEX_KEY, currentIndex);
		getFragmentManager().putFragment(outState, FRAGMENT_KEY, fragment);
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
				default:

		}
		activity.invalidateOptionsMenu();
		close();
		return true;
	}

	private void loadFragment(int id)
	{
		currentIndex = id;
		switch (id)
		{
			case R.id.nav_Settings:
				activity.setTitle(R.string.menu_settings);
				fragment = new SettingsSection();
				selectItem(5);
				break;
			case R.id.nav_preferences:
				activity.setTitle(R.string.menu_preferences);
				fragment = new PreferenceSection();
				selectItem(4);
				break;
			case R.id.nav_edit:
				activity.setTitle(R.string.menu_edit);
				fragment = new EditSection();
				selectItem(3);
				break;
			case R.id.nav_guide:
				activity.setTitle(R.string.menu_guide);
				fragment = new GuideSection();
				selectItem(2);
				break;
			case R.id.nav_progress:
				activity.setTitle(R.string.menu_progress);
				fragment = new ProgressSection();
				selectItem(1);
				break;
			case R.id.nav_eval:
			default:
				activity.setTitle(R.string.evaluation);
				fragment = new EvaluationSection();
				getFragmentManager().popBackStack();
				getFragmentManager().beginTransaction()//.addToBackStack(null)
						.replace(R.id.content, fragment).commit();
				selectItem(0);
				return;
		}
		getFragmentManager().beginTransaction().addToBackStack(null)
				.replace(R.id.content, fragment).commit();
	}

	private FragmentManager getFragmentManager()
	{
		return activity.getFragmentManager();
	}

	private void selectItem(int i)
	{
		navigationView.getMenu().getItem(i).setChecked(true);
	}

	@Override
	public void onBackStackChanged()
	{
		fragment = getFragmentManager().findFragmentById(R.id.content);
		if (fragment instanceof EvaluationSection)
		{
			selectItem(0);
		}
		else if (fragment instanceof ProgressSection)
		{
			selectItem(1);
		}
		else if (fragment instanceof GuideSection)
		{
			selectItem(2);
		}
		else if (fragment instanceof EditSection)
		{
			selectItem(3);
		}
		else if (fragment instanceof PreferenceSection)
		{
			selectItem(4);
		}
		else if (fragment instanceof SettingsSection)
		{
			selectItem(5);
		}
	}
}
