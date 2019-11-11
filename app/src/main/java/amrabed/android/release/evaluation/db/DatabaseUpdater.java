package amrabed.android.release.evaluation.db;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.joda.time.DateTime;

import amrabed.android.release.evaluation.ApplicationEvaluation;
import amrabed.android.release.evaluation.core.DayEntry;

public class DatabaseUpdater extends Service
{
	private static final DateTime TODAY = new DateTime().withTimeAtStartOfDay();

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		update();
		stopSelf();
		return START_NOT_STICKY;

	}

	private static void update()
	{
		final Database db = ApplicationEvaluation.getDatabase();
		for(int i = 0; i < 31; i++) {
			db.insertDay(new DayEntry(TODAY.minusDays(i).getMillis()));
		}
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
}
