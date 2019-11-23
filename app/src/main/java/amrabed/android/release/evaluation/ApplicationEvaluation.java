package amrabed.android.release.evaluation;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import org.joda.time.DateTime;

import amrabed.android.release.evaluation.core.DayEntry;
import amrabed.android.release.evaluation.core.TaskList;
import amrabed.android.release.evaluation.db.Database;
import amrabed.android.release.evaluation.locale.LocaleManager;
import amrabed.android.release.evaluation.notification.BootReceiver;
import amrabed.android.release.evaluation.notification.Notifier;

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
		db.insertDay(new DayEntry(new DateTime().withTimeAtStartOfDay().getMillis()));

		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		if (settings.getBoolean("notification", true))
		{
			Notifier.createNotificationChannel(this);
		}

		if (settings.getBoolean(IS_FIRST_RUN, true))
		{
			settings.edit().putBoolean(IS_FIRST_RUN, false).apply();
			BootReceiver.enable(this);
			db.saveList(TaskList.getDefault(this));
		}
	}

	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		LocaleManager.setLocale(this);
	}

	@Override
	public void onTerminate()
	{
		db.close();
		super.onTerminate();
	}
}