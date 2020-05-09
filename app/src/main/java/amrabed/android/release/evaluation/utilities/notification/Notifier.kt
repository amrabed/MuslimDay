package amrabed.android.release.evaluation.utilities.notification

import amrabed.android.release.evaluation.MainActivity
import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.utilities.locale.LocaleManager
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import org.joda.time.LocalTime

/**
 * Notifier
 */
class Notifier : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if ("android.intent.action.BOOT_COMPLETED" == intent.action) {
            scheduleNotifications(context)
        }
    }

    companion object {
        fun toggle(context: Context?, isEnabled: Boolean) {
            if (context != null) {
                if (isEnabled) {
                    Log.i(NAME, "Enabling boot receiver")
                    context.packageManager.setComponentEnabledSetting(
                            ComponentName(context, Notifier::class.java),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP)
                    scheduleNotifications(context)
                } else {
                    Log.i(NAME, "Disabling boot receiver")
                    cancelNotifications(context)
                    context.packageManager.setComponentEnabledSetting(
                            ComponentName(context, Notifier::class.java),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP)
                }
            }
        }

        private var intent: PendingIntent? = null

        private fun scheduleNotifications(context: Context?) {
            Log.i(NAME, "Scheduling notification")
            val intent = getPendingIntent(context)
            getAlarmManager(context).setInexactRepeating(AlarmManager.RTC,
                    LocalTime(19, 0).toDateTimeToday().millis,
                    AlarmManager.INTERVAL_DAY, intent)
        }

        private fun cancelNotifications(context: Context?) {
            Log.i(NAME, "Cancelling notification")
            getAlarmManager(context).cancel(getPendingIntent(context))
        }

        private fun getPendingIntent(context: Context?): PendingIntent? {
            if (intent != null) {
                return intent
            }
            intent = PendingIntent.getBroadcast(context, 0,
                    Intent(context, NotificationHandler::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
            return intent
        }

        private fun getAlarmManager(context: Context?): AlarmManager {
            return context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }

        private val NAME = Notifier::class.java.name
    }

    class NotificationHandler : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null) {
                LocaleManager.setLocale(context.applicationContext)
                showNotification(context)
            }
        }

        private fun showNotification(context: Context) {
            val channelId = "Reminder Channel"
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.createNotificationChannel(NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT))
            }
            Log.i(NAME, "Showing Notification")
            val intent = TaskStackBuilder.create(context)
                    .addParentStack(MainActivity::class.java)
                    .addNextIntent(Intent(context, MainActivity::class.java))
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(context.getString(R.string.appName))
                    .setContentText(context.getString(R.string.notificationContent))
                    .setContentIntent(intent)
                    .setAutoCancel(true)
                    .build()
            manager.notify(0, notification)
        }
    }
}