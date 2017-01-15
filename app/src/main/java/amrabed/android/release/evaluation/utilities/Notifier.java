package amrabed.android.release.evaluation.utilities;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.joda.time.LocalTime;

import amrabed.android.release.evaluation.MainActivity;
import amrabed.android.release.evaluation.R;

/**
 * Notifier to set up and show reminder notifications
 *
 * @author AmrAbed
 */

public class Notifier extends IntentService
{
	private static final String TAG = Notifier.class.getName();
	private static PendingIntent intent;

	public Notifier()
	{
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		showNotification(this);
	}

	public static void showNotification(Context context)
	{
		Log.i(TAG, "Showing Notification");
		final PendingIntent intent = TaskStackBuilder.create(context)
				.addParentStack(MainActivity.class)
				.addNextIntent(new Intent(context, MainActivity.class))
				.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		final Notification notification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.icon)
				.setContentTitle(context.getString(R.string.app_name))
				.setContentText(context.getString(R.string.notification_content))
				.setContentIntent(intent)
				.setAutoCancel(true)
				.build();
		((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
				.notify(0, notification);
	}

	public static void scheduleNotifications(Context context)
	{
		Log.i(TAG, "Scheduling notification");
		getAlarmManager(context).setInexactRepeating(AlarmManager.RTC,
				new LocalTime(19, 0).toDateTimeToday().getMillis(),
				AlarmManager.INTERVAL_DAY, getPendingIntent(context));
	}

	public static void cancelNotifications(Context context)
	{
		Log.i(TAG, "Cancelling notification");
		getAlarmManager(context).cancel(getPendingIntent(context));
	}

	public static PendingIntent getPendingIntent(Context context)
	{
		if (intent != null)
		{
			return intent;
		}
		intent = PendingIntent.getService(context.getApplicationContext(), 0,
				new Intent(context, Notifier.class), PendingIntent.FLAG_UPDATE_CURRENT);
		return intent;
	}

	private static AlarmManager getAlarmManager(Context context)
	{
		return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}
}
