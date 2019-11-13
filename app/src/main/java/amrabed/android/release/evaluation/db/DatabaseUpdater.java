package amrabed.android.release.evaluation.db;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.joda.time.DateTime;
import org.joda.time.Days;

import amrabed.android.release.evaluation.ApplicationEvaluation;
import amrabed.android.release.evaluation.core.DayEntry;
import amrabed.android.release.evaluation.preferences.Preferences;

public class DatabaseUpdater extends Service {
    private static final DateTime TODAY = new DateTime().withTimeAtStartOfDay();

    public int onStartCommand(Intent intent, int flags, int startId) {
        update();
        stopSelf();
        return START_NOT_STICKY;

    }

    private void update() {
        final DateTime lastAddedDay = new DateTime(Preferences.getLastAddedDay(this));
        if (lastAddedDay.isBefore(TODAY)) {
            final Database db = ApplicationEvaluation.getDatabase();
            final int days = Days.daysBetween(lastAddedDay, TODAY).getDays();
            for (int i = 0; i < (days > 31 ? 31 : days); i++) {
                db.insertDay(new DayEntry(TODAY.minusDays(i).getMillis()));
            }
            Preferences.setLastAddedDay(this, TODAY.getMillis());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
