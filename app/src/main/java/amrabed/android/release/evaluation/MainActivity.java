package amrabed.android.release.evaluation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import amrabed.android.release.evaluation.app.ApplicationEvaluation;
import amrabed.android.release.evaluation.db.DatabaseEntry;
import amrabed.android.release.evaluation.db.DatabaseUpdater;

/**
 * Main Activity
 *
 * @author AmrAbed
 */
public class MainActivity extends AppCompatActivity implements
		NavigationView.OnNavigationItemSelectedListener
{
	private static final String INDEX_KEY = "Navigation index key";

	private DrawerLayout drawer;
	private List<DatabaseEntry> entries;
	private int navigationIndex;
	private NavigationView navigationView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		if (savedInstanceState != null)
		{
			navigationIndex = savedInstanceState.getInt(INDEX_KEY);
		}
		loadCurrentFragment();

		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		navigationView = (NavigationView) findViewById(R.id.navigation);
		navigationView.setNavigationItemSelectedListener(this);
		navigationView.getMenu().getItem(navigationIndex).setChecked(true);

		ApplicationEvaluation
				.getDatabase().insert(new DatabaseEntry(DatabaseUpdater.today.getMillis(), 0));
		entries = ApplicationEvaluation.getDatabase().getAllEntries();


		ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer,
				toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

		drawer.addDrawerListener(actionBarDrawerToggle);
		actionBarDrawerToggle.syncState();
	}


	private void loadCurrentFragment()
	{
		Fragment fragment;
		switch (navigationIndex)
		{
			case 5:
				fragment = new SettingsSection();
				break;
			case 4:
				fragment = new EditSection();
				break;
			case 3:
				fragment = new PreferenceSection();
				break;
			case 2:
				fragment = new GuideSection();
				break;
			case 1:
				fragment = new PreferenceSection();
				break;
			case 0:
			default:
				fragment = new DaySection();
		}
		getFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
	}

	@Override
	public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)
	{
		super.onSaveInstanceState(outState, outPersistentState);
		outState.putInt(INDEX_KEY, navigationIndex);
	}

	@Override
	public void onBackPressed()
	{
		if (drawer.isDrawerOpen(navigationView))
		{
			drawer.closeDrawers();
		}
		else
		{
			super.onBackPressed();
		}

	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.nav_eval:
				navigationIndex = 0;
				setTitle(R.string.evaluation);
				getFragmentManager().beginTransaction()//.addToBackStack(null)
						.replace(R.id.content, new DaySection()).commit();
				break;
			case R.id.nav_progress:
				navigationIndex = 1;
				setTitle(R.string.menu_progress);
				getFragmentManager().beginTransaction()//.addToBackStack(null)
						.replace(R.id.content, new ProgressSection()).commit();
				break;
			case R.id.nav_guide:
				navigationIndex = 2;
				setTitle(R.string.menu_progress);
				getFragmentManager().beginTransaction()//.addToBackStack(null)
						.replace(R.id.content, new GuideSection()).commit();
				break;
			case R.id.nav_preferences:
				navigationIndex = 3;
				getFragmentManager().beginTransaction()//.addToBackStack(null)
						.replace(R.id.content, new PreferenceSection()).commit();
				break;
			case R.id.nav_edit:
				navigationIndex = 4;
				getFragmentManager().beginTransaction()//.addToBackStack(null)
						.replace(R.id.content, new EditSection()).commit();
				break;
			case R.id.nav_Settings:
				navigationIndex = 5;
				getFragmentManager().beginTransaction()//.addToBackStack(null)
						.replace(R.id.content, new SettingsSection()).commit();
				break;
			case R.id.nav_help:
				new AlertDialog.Builder(this)
						.setTitle("Help")
						.setView(getLayoutInflater().inflate(R.layout.help_dialog, null))
						.create().show();
				break;
			case R.id.nav_about:
				new AlertDialog.Builder(this)
						.setTitle(R.string.menu_about)
						.setMessage(R.string.about_content1)
						.create().show();
				break;
		}
		invalidateOptionsMenu();
		drawer.closeDrawers();
		return true;
	}
}
