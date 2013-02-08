package amrabed.android.release.evaluation;

import java.util.Calendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

public class ApplicationEvaluation extends Application
{
	static Database db;// = new Database(getApplicationContext());
	private Locale locale = null;

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		if (locale != null)
		{
			newConfig.locale = locale;
			Locale.setDefault(locale);
			getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
		}
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		db = new Database(this);
		scheduleDatabaseUpdate();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

		Configuration config = getBaseContext().getResources().getConfiguration();

		String lang = settings.getString("language", "");
		if (!"".equals(lang) && !config.locale.getLanguage().equals(lang))
		{
			locale = new Locale(lang);
			Locale.setDefault(locale);
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
		}
	}
	
	void scheduleDatabaseUpdate()
	{
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(this, DatabaseTimer.class), 0);
		Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
	}
}