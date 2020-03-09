package amrabed.android.release.evaluation.notification

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log

/**
 * Device Boot Receiver
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if ("android.intent.action.BOOT_COMPLETED" == intent.action) {
            Notifier.scheduleNotifications(context)
        }
    }

    companion object {
        private val TAG = BootReceiver::class.java.name
        fun enable(context: Context) {
            Log.i(TAG, "Enabling boot receiver")
            context.packageManager.setComponentEnabledSetting(
                    ComponentName(context, BootReceiver::class.java),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP)
            Notifier.scheduleNotifications(context)
        }

        fun disable(context: Context?) {
            Log.i(TAG, "Disabling boot receiver")
            Notifier.cancelNotifications(context)
            context!!.packageManager.setComponentEnabledSetting(
                    ComponentName(context, BootReceiver::class.java),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP)
        }
    }
}