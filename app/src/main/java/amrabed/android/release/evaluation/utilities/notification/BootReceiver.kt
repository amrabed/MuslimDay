package amrabed.android.release.evaluation.utilities.notification

import amrabed.android.release.evaluation.data.Repository
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.preference.PreferenceManager

/**
 * Boot-completed broadcast receiver
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if ("android.intent.action.BOOT_COMPLETED" == intent.action) {
            Log.i(name, "Enabling boot receiver")
            context.packageManager.setComponentEnabledSetting(
                    ComponentName(context, BootReceiver::class.java),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP)
            // Schedule daily reminder
            DailyReminder.toggle(context, PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notification", false))
            // Schedule task reminders
            Repository(context.applicationContext).getTaskReminders()?.observeForever { tasks ->
                tasks.forEach { TaskReminder.schedule(context, it) }
            }
        }
    }

    private val name = BootReceiver::class.java.name
}