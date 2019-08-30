package amrabed.android.release.evaluation.notification;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Device Boot Receiver
 */

public class BootReceiver extends BroadcastReceiver
{
	private static final String TAG = BootReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			Notifier.scheduleNotifications(context);

		}
	}

	public static void enable(Context context)
	{
		Log.i(TAG, "Enabling boot receiver");
		context.getPackageManager().setComponentEnabledSetting(
				new ComponentName(context, BootReceiver.class),
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);

		Notifier.scheduleNotifications(context);
	}

	public static void disable(Context context)
	{
		Log.i(TAG, "Disabling boot receiver");
		Notifier.cancelNotifications(context);

		context.getPackageManager().setComponentEnabledSetting(
				new ComponentName(context, BootReceiver.class),
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
	}
}

