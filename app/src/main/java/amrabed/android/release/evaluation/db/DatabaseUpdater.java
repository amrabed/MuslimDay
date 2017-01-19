package amrabed.android.release.evaluation.db;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.joda.time.DateTime;

import amrabed.android.release.evaluation.ApplicationEvaluation;
import amrabed.android.release.evaluation.core.DayEntry;

public class DatabaseUpdater extends Service
{
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		insertEntry(today.getMillis());
		stopSelf();
		return START_NOT_STICKY;

	}

	public static DateTime today = new DateTime().withTimeAtStartOfDay();


	void insertEntry(long date)
	{
		ApplicationEvaluation.getDatabase().insertDay(new DayEntry(date));
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
}
