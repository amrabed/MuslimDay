package amrabed.android.release.evaluation.utilities.notification

import amrabed.android.release.evaluation.MainActivity
import amrabed.android.release.evaluation.R
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

abstract class BaseReminder : BroadcastReceiver() {
    companion object {
        fun getAlarmManager(context: Context) = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        fun getNotificationManager(context: Context) = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        fun getNotificationBuilder(context: Context, channelId: String): NotificationCompat.Builder {
            return NotificationCompat.Builder(context, channelId)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo)
                    .setContentIntent(buildContentIntent(context))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        }

        fun buildNotificationChannel(context: Context, channelId: String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getNotificationManager(context).createNotificationChannel(NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT))
            }
        }

        private fun buildContentIntent(context: Context): PendingIntent {
            return TaskStackBuilder.create(context)
                    .addParentStack(MainActivity::class.java)
                    .addNextIntent(Intent(context, MainActivity::class.java))
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}