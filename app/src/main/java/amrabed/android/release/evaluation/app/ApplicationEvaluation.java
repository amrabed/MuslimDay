package amrabed.android.release.evaluation.app;

import java.util.Calendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import amrabed.android.release.evaluation.api.ApiManager;
import amrabed.android.release.evaluation.db.Database;
import amrabed.android.release.evaluation.db.DatabaseTimer;
import amrabed.android.release.evaluation.utilities.Notifier;

public class ApplicationEvaluation extends Application
{
	private static ApplicationEvaluation instance;

	private Database db;
	private ApiManager apiManager;
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
			newConfig.locale = locale;
			Locale.setDefault(locale);
			getBaseContext().getResources().updateConfiguration(newConfig,
					getBaseContext().getResources().getDisplayMetrics());
		}
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		instance = this;
		db = new Database(this);
		apiManager = new ApiManager(getApplicationContext());

		Notifier.scheduleNotifications(this);
		scheduleDatabaseUpdate();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

		Configuration config = getBaseContext().getResources().getConfiguration();

		final String language = settings.getString("language", "");
		if (!"".equals(language) && !config.locale.getLanguage().equals(language))
		{
			locale = new Locale(language);
			Locale.setDefault(locale);
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config,
					getBaseContext().getResources().getDisplayMetrics());
		}
	}

	public static ApiManager getApiManager()
	{
		return instance.apiManager;
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
		final PendingIntent pendingIntent = PendingIntent
				.getBroadcast(getApplicationContext(), 0, new Intent(this, DatabaseTimer.class), 0);
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		((AlarmManager) getSystemService(ALARM_SERVICE)).setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, pendingIntent);
	}
}