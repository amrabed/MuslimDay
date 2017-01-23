package amrabed.android.release.evaluation;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;

import java.util.Locale;

import amrabed.android.release.evaluation.core.DayEntry;
import amrabed.android.release.evaluation.db.Database;
import amrabed.android.release.evaluation.db.DatabaseUpdater;
import amrabed.android.release.evaluation.edit.OnBackPressedListener;
import amrabed.android.release.evaluation.preferences.Preferences;
import amrabed.android.release.evaluation.sync.SyncActivity;

/**
 * Main Activity
 *
 * @author AmrAbed
 */
public class MainActivity extends SyncActivity
{
	private static final String TAG = MainActivity.class.getName();

	private NavigationDrawer drawer;

	private Locale locale = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		final Configuration config = getBaseContext().getResources().getConfiguration();
		final String language = Preferences.getLanguage(this);
		if (!"".equals(language) && !config.locale.getLanguage().equals(language))
		{
			locale = new Locale(language);
			setLocale(config);
		}

		setContentView(R.layout.main_activity);

		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		drawer = new NavigationDrawer(this).create(savedInstanceState, toolbar);

		final Database db = ApplicationEvaluation.getDatabase();
		for (int i = 0; i < 7; i++)
		{
			db.insertDay(new DayEntry(DatabaseUpdater.today.minusDays(i).getMillis()));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)
	{
		super.onSaveInstanceState(outState, outPersistentState);
		drawer.saveState(outState);
	}

	boolean isReentry = false;

	@Override
	public void onBackPressed()
	{
		if (drawer.isOpen())
		{
			drawer.close();
		}
		else
		{
			final Fragment fragment = getFragmentManager().findFragmentById(R.id.content);
			if (!isReentry && fragment != null && fragment instanceof OnBackPressedListener)
			{
				isReentry = true;
				((OnBackPressedListener) fragment).onBackPressed();
			}
			else
			{
				super.onBackPressed();
			}
		}
	}

	private void setLocale(Configuration config)
	{
		Locale.setDefault(locale);
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());

	}

	void restart(boolean shouldShowDialog)
	{
		if (!shouldShowDialog)
		{
			finish();
			android.os.Process.killProcess(android.os.Process.myPid());
			startActivity(new Intent(MainActivity.this, MainActivity.class));
			return;
		}
		// Show confirmation dialog to restart app, so user can see what's going on
		new AlertDialog.Builder(this)
				.setMessage(R.string.restart)
				.setCancelable(true)
				.setPositiveButton(R.string.res_yes, new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						// Restart Application
						restart(false);
					}
				})
				.create().show();
	}
}
