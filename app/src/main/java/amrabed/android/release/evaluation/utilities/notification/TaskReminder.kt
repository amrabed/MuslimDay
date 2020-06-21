package amrabed.android.release.evaluation.utilities.notification

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.core.Record
import amrabed.android.release.evaluation.core.Status
import amrabed.android.release.evaluation.core.Task
import amrabed.android.release.evaluation.data.Repository
import amrabed.android.release.evaluation.utilities.locale.LocaleManager
import amrabed.android.release.evaluation.utilities.time.DateManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.os.bundleOf
import org.joda.time.DateTime
import org.joda.time.LocalDateTime

/**
 * Utility to schedule, cancel, and show task reminders
 */
class TaskReminder : BaseReminder() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(NAME, "Handling notification for intent: $intent")
        LocaleManager.setLocale(context.applicationContext)
        val task = intent.extras?.getBundle(NAME)?.getParcelable(TASK) as Task?
        if (task?.reminder != null) {
            showNotification(context, task)
            schedule(context, task.nextReminder(LocalDateTime.parse(task.reminder).toLocalTime()))
            Repository(context).addTask(task)
        }
    }

    companion object {
        private val NAME = TaskReminder::class.java.name

        fun schedule(context: Context, task: Task?) {
            if (task?.reminder != null) {
                Log.i(NAME, "Scheduling notification for task ${task.id} at ${task.reminder}")
                val intent = Intent(context, TaskReminder::class.java).putExtra(NAME, bundleOf(Pair(TASK, task)))
                val pendingIntent = PendingIntent.getBroadcast(context, task.id.hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT)
                getAlarmManager(context).set(AlarmManager.RTC, DateTime.parse(task.reminder).millis, pendingIntent)
            }
        }

        fun cancel(context: Context, task: Task) {
            Log.i(NAME, "Cancelling notification for task: ${task.id}")
            val intent = PendingIntent.getBroadcast(context, task.id.hashCode(), Intent(context, TaskReminder::class.java), PendingIntent.FLAG_NO_CREATE)
            if (intent != null) getAlarmManager(context).cancel(intent)
        }

        fun showNotification(context: Context, task: Task?) {
            Log.i(NAME, "Showing notification for task: ${task?.id}")
            val channelId = "Task Reminder Channel"
            buildNotificationChannel(context, channelId)

            val actionIntents = Status.values().map {
                PendingIntent.getBroadcast(context, task?.id.hashCode(),
                        Intent(context, ActionHandler::class.java).setAction(it.name).putExtra(NAME, task?.id),
                        PendingIntent.FLAG_CANCEL_CURRENT)
            }

            val notification = getNotificationBuilder(context, channelId)
                    .setGroup(NAME)
                    .setContentTitle(task?.getTitle(context))
                    .setContentText(context.getString(R.string.reminderNotificationText))
                    .setContentInfo(DateTime.parse(task?.reminder).toLocalTime().toString("h:mm a"))
                    .addAction(Status.NONE.icon, context.getString(Status.NONE.title), actionIntents[Status.NONE.ordinal])
                    .addAction(Status.MISSED.icon, context.getString(Status.MISSED.title), actionIntents[Status.MISSED.ordinal])
                    .addAction(Status.DONE.icon, context.getString(Status.DONE.title), actionIntents[Status.DONE.ordinal])
                    .build()

            getNotificationManager(context).notify(task?.id.hashCode(), notification)
        }
    }

    class ActionHandler : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(NAME, "Handling action ${intent.action} for intent $intent")
            val taskId = intent.extras?.getString(NAME)
            val record = Record(DateManager.getDatabaseKey(0), taskId!!, Status.valueOf(intent.action!!).value)
            Repository(context.applicationContext).updateRecord(record)
            getNotificationManager(context).cancel(taskId.hashCode())
        }
    }
}

const val TASK = "Task"