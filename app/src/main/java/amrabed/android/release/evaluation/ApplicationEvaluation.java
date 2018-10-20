package amrabed.android.release.evaluation;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import org.joda.time.LocalTime;

import java.util.Locale;

import amrabed.android.release.evaluation.core.TaskList;
import amrabed.android.release.evaluation.db.Database;
import amrabed.android.release.evaluation.db.DatabaseTimer;
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
		final Configuration config = getBaseContext().getResources().getConfiguration();
//		final String language = Preferences.getLanguage(this);
		setLocale(config);


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
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setLocale(newConfig);
	}

	private void setLocale(Configuration config) {
		final String language = PreferenceManager.getDefaultSharedPreferences(this)
				.getString("language", "en");
		if (!config.locale.getLanguage().equals(language)) {
			Locale locale = new Locale(language);
			Locale.setDefault(locale);
			config.locale = locale;
//		getBaseContext().createConfigurationContext(config);
			getBaseContext().getResources().updateConfiguration(config,
					getBaseContext().getResources().getDisplayMetrics());
		}

	}

	private void scheduleDatabaseUpdate()
	{
		// Add day entry to database even if app is not started on that day
		((AlarmManager) getSystemService(ALARM_SERVICE)).setInexactRepeating(AlarmManager.RTC,
				new LocalTime(0, 0).toDateTimeToday().getMillis(),
				AlarmManager.INTERVAL_DAY,
				PendingIntent.getBroadcast(getApplicationContext(), 0,
						new Intent(this, DatabaseTimer.class), 0));
	}
}