package amrabed.android.release.evaluation;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import org.joda.time.LocalTime;

import amrabed.android.release.evaluation.core.TaskList;
import amrabed.android.release.evaluation.db.Database;
import amrabed.android.release.evaluation.db.DatabaseTimer;
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

		scheduleDatabaseUpdate();
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

	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		LocaleManager.setLocale(this, newConfig);
	}


	private void scheduleDatabaseUpdate()
	{
		// Add day entry to database even if app is not started on that day
		final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		if (alarmManager != null) {
			alarmManager.setInexactRepeating(AlarmManager.RTC,
					new LocalTime(0, 0).toDateTimeToday().getMillis(),
					AlarmManager.INTERVAL_DAY,
					PendingIntent.getBroadcast(getApplicationContext(), 0,
							new Intent(this, DatabaseTimer.class), 0));
		}
	}
}