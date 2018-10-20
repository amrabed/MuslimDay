package amrabed.android.release.evaluation.db;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.joda.time.DateTime;

import amrabed.android.release.evaluation.ApplicationEvaluation;
import amrabed.android.release.evaluation.core.DayEntry;

public class DatabaseUpdater extends Service
{
	public static final DateTime TODAY = new DateTime().withTimeAtStartOfDay();

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		insertEntry(TODAY.getMillis());
		stopSelf();
		return START_NOT_STICKY;

	}

	private void insertEntry(long date)
	{
		ApplicationEvaluation.getDatabase().insertDay(new DayEntry(date));
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
}
