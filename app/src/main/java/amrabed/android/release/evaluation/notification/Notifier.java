package amrabed.android.release.evaluation.notification;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.TaskStackBuilder;

import org.joda.time.LocalTime;

import amrabed.android.release.evaluation.MainActivity;
import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.locale.LocaleManager;

/**
 * Notifier to set up and show reminder notifications
 */

public class Notifier extends IntentService {
    private static final String TAG = Notifier.class.getName();
    private static PendingIntent intent;

    public Notifier() {
        super(TAG);
    }

    private static void showNotification(Context context) {
        Log.i(TAG, "Showing Notification");
        final PendingIntent intent = TaskStackBuilder.create(context)
                .addParentStack(MainActivity.class)
                .addNextIntent(new Intent(context, MainActivity.class))
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        final Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.notification_content))
                .setContentIntent(intent)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setAutoCancel(true)
                .build();
        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(0, notification);
        }
    }

    public static void createNotificationChannel(Context appContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationChannel channel =
                    new NotificationChannel(Notifier.TAG, appContext.getString(R.string.app_name),
                            NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(appContext.getString(R.string.app_name));
            final NotificationManager manager = appContext.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LocaleManager.setLocale(this);
        showNotification(this);
    }

    public static void scheduleNotifications(Context context) {
        Log.i(TAG, "Scheduling notification");
        final PendingIntent intent = getPendingIntent(context);
        getAlarmManager(context).setInexactRepeating(AlarmManager.RTC,
                new LocalTime(19, 0).toDateTimeToday().getMillis(),
                AlarmManager.INTERVAL_DAY, intent);
    }

    public static void cancelNotifications(Context context) {
        Log.i(TAG, "Cancelling notification");
        getAlarmManager(context).cancel(getPendingIntent(context));
    }

    private static PendingIntent getPendingIntent(Context context) {
        if (intent != null) {
            return intent;
        }
        intent = PendingIntent.getService(context.getApplicationContext(), 0,
                new Intent(context, Notifier.class), PendingIntent.FLAG_UPDATE_CURRENT);
        return intent;
    }

    private static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }
}
