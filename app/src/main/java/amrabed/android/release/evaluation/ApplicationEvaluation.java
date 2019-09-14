package amrabed.android.release.evaluation;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import amrabed.android.release.evaluation.core.TaskList;
import amrabed.android.release.evaluation.db.Database;
import amrabed.android.release.evaluation.db.DatabaseUpdater;
import amrabed.android.release.evaluation.locale.LocaleManager;
import amrabed.android.release.evaluation.notification.BootReceiver;

public class ApplicationEvaluation extends Application
{
	private static final String IS_FIRST_RUN = "is first run";

	private static ApplicationEvaluation instance;

	private Database db;

	private static ApplicationEvaluation getInstance()
	{
		return instance;
	}

	public static Database getDatabase()
	{
		return getInstance().db;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		LocaleManager.setLocale(this);


		instance = this;
		db = new Database(this);

		getApplicationContext().startService(new Intent(getApplicationContext(), DatabaseUpdater.class));
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		if (settings.getBoolean("notification", true))
		{
			BootReceiver.enable(this);
		}

		if (settings.getBoolean(IS_FIRST_RUN, true))
		{
			settings.edit().putBoolean(IS_FIRST_RUN, false).apply();
			db.saveList(TaskList.getDefault(this));
		}
	}

	@Override
	public void onTerminate()
	{
		db.close();
		super.onTerminate();
	}
}