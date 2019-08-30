package amrabed.android.release.evaluation;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import amrabed.android.release.evaluation.core.DayEntry;
import amrabed.android.release.evaluation.db.Database;
import amrabed.android.release.evaluation.db.DatabaseUpdater;
import amrabed.android.release.evaluation.edit.OnBackPressedListener;
import amrabed.android.release.evaluation.locale.LocaleManager;
import amrabed.android.release.evaluation.sync.SyncActivity;

/**
 * Main Activity
 */
public class MainActivity extends SyncActivity
{
	private NavigationDrawer drawer;

	private boolean isReentry = false;

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState)
	{
		super.onSaveInstanceState(outState, outPersistentState);
		drawer.saveState(outState);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		LocaleManager.setLocale(this);

		setContentView(R.layout.main_activity);

		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		drawer = new NavigationDrawer(this).create(savedInstanceState, toolbar);

		final Database db = ApplicationEvaluation.getDatabase();
		db.insertDay(new DayEntry(DatabaseUpdater.TODAY.getMillis()));

	}

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
			if (!isReentry && fragment instanceof OnBackPressedListener)
			{
				isReentry = true;
				((OnBackPressedListener) fragment).onBackPressed();
			}
			else
			{
				drawer.onBackStackChanged();
				super.onBackPressed();
			}
		}
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
