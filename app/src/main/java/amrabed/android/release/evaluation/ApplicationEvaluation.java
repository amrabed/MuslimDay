package amrabed.android.release.evaluation;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.joda.time.LocalTime;

import amrabed.android.release.evaluation.db.Database;
import amrabed.android.release.evaluation.db.DatabaseTimer;
import amrabed.android.release.evaluation.notification.BootReceiver;

public class ApplicationEvaluation extends Application
{
	private static ApplicationEvaluation instance;

	private Database db;

	public static ApplicationEvaluation getInstance()
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
		instance = this;
		db = new Database(this);

		scheduleDatabaseUpdate();
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		if (settings.getBoolean("notification", true))
		{
			BootReceiver.enable(this);
		}
	}

	@Override
	public void onTerminate()
	{
		db.close();
		super.onTerminate();
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