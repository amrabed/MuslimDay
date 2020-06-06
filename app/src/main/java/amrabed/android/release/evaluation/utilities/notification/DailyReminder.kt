package amrabed.android.release.evaluation.utilities.notification

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.utilities.locale.LocaleManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import org.joda.time.LocalTime

/**
 * Utility to schedule, cancel, and show daily reminder
 */
class DailyReminder : BaseReminder() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.i(NAME, "Handling notification for intent: $intent")
        LocaleManager.setLocale(context.applicationContext)
        showNotification(context)
    }

    companion object {
        private val NAME = DailyReminder::class.java.name

        fun toggle(context: Context, isEnabled: Boolean) {
            if (isEnabled) {
                schedule(context)
            } else {
                cancel(context)
            }
        }

        private fun schedule(context: Context) {
            Log.i(NAME, "Scheduling daily reminder")
            val intent = PendingIntent.getBroadcast(context, 0, Intent(context, DailyReminder::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
            getAlarmManager(context).setInexactRepeating(AlarmManager.RTC,
                    LocalTime(19, 0).toDateTimeToday().millis,
                    AlarmManager.INTERVAL_DAY, intent)
        }

        private fun cancel(context: Context) {
            Log.i(NAME, "Cancelling notification")
            val intent = PendingIntent.getBroadcast(context, 0, Intent(context, DailyReminder::class.java), PendingIntent.FLAG_NO_CREATE)
            if (intent != null) getAlarmManager(context).cancel(intent)
        }

        private fun showNotification(context: Context) {
            Log.i(NAME, "Showing daily reminder notification")
            val channelId = "Daily Reminder Channel"
            buildNotificationChannel(context, channelId)

            val notification = getNotificationBuilder(context, channelId)
                    .setContentTitle(context.getString(R.string.appName))
                    .setContentText(context.getString(R.string.notificationContent))
                    .build()
            getNotificationManager(context).notify(0, notification)
        }
    }
}