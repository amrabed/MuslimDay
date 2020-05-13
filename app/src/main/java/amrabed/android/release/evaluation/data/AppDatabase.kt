package amrabed.android.release.evaluation.data

import amrabed.android.release.evaluation.data.converters.ActiveDaysConverter
import amrabed.android.release.evaluation.data.converters.SelectionsConverter
import amrabed.android.release.evaluation.data.entities.Day
import amrabed.android.release.evaluation.data.entities.Task
import amrabed.android.release.evaluation.data.migrations.Migration2To3
import amrabed.android.release.evaluation.data.tables.DayTable
import amrabed.android.release.evaluation.data.tables.TaskTable
import amrabed.android.release.evaluation.utilities.preferences.Preferences
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import org.joda.time.Days
import org.joda.time.LocalDate
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Database(entities = [Day::class, Task::class], version = 3)
@TypeConverters(SelectionsConverter::class, ActiveDaysConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskTable(): TaskTable
    abstract fun dayTable(): DayTable
    class Callback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val list = arrayOfNulls<Task>(Task.DEFAULT_LIST.size)
            for (i in list.indices) {
                list[i] = Task(defaultIndex = i) // sets the default and current index of each task
            }
            writeExecutor.execute { database!!.taskTable().insertTasks(*list) }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            // Add any missing days of the last month
            val today = LocalDate()
            val lastAddedDay = LocalDate(Preferences.getLastAddedDay(context))
            if (lastAddedDay.isBefore(today)) {
                val diff = Days.daysBetween(lastAddedDay, today).days
                val days = arrayOfNulls<Day>(if (diff > 31) 31 else diff)
                for (i in days.indices) {
                    days[i] = Day(today.minusDays(i))
                }
                writeExecutor.execute { database!!.dayTable().insert(*days) }
                Preferences.setLastAddedDay(context, today.toDateTimeAtCurrentTime().millis)
            }
        }

    }

    companion object {
        const val DATABASE_NAME = "activities"

        @Volatile
        private var database: AppDatabase? = null
        val writeExecutor: Executor = Executors.newFixedThreadPool(4)
        operator fun get(context: Context): AppDatabase? {
            if (database == null) {
                synchronized(AppDatabase::class.java) {
                    if (database == null) {
                        database = Room.databaseBuilder(context.applicationContext,
                                        AppDatabase::class.java, DATABASE_NAME)
                                .addMigrations(Migration2To3())
                                .addCallback(Callback(context))
                                .build()
                    }
                }
            }
            return database
        }
    }
}