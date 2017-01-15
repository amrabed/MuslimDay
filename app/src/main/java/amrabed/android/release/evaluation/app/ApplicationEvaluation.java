package amrabed.android.release.evaluation.app;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import org.joda.time.LocalTime;

import java.util.Locale;

import amrabed.android.release.evaluation.db.Database;
import amrabed.android.release.evaluation.db.DatabaseTimer;
import amrabed.android.release.evaluation.notification.BootReceiver;

public class ApplicationEvaluation extends Application
{
	private static ApplicationEvaluation instance;

	private Database db;
	private Locale locale = null;

	public static ApplicationEvaluation getInstance()
	{
		return instance;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		if (locale != null)
		{
			setLocale(newConfig);
		}
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		instance = this;
		db = new Database(this);

		scheduleDatabaseUpdate();
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		if (settings.getBoolean("notification", true))
		{
			BootReceiver.enable(this);
		}

		final Configuration config = getBaseContext().getResources().getConfiguration();
		final String language = settings.getString("language", "");
		if (!"".equals(language) && !config.locale.getLanguage().equals(language))
		{
			locale = new Locale(language);
			setLocale(config);
		}
	}

	private void setLocale(Configuration config)
	{
		Locale.setDefault(locale);
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());

	}

	public static boolean isEnglish()
	{
		return instance.getResources().getConfiguration().locale.getLanguage().equals("en");
	}

	@Override
	public void onTerminate()
	{
		db.close();
		super.onTerminate();
	}

	public static Database getDatabase()
	{
		return getInstance().db;
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