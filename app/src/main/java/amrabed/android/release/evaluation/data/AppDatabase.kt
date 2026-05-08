package amrabed.android.release.evaluation.data

import amrabed.android.release.evaluation.core.Record
import amrabed.android.release.evaluation.core.Task
import amrabed.android.release.evaluation.data.converters.ActiveDaysConverter
import amrabed.android.release.evaluation.data.converters.SelectionsConverter
import amrabed.android.release.evaluation.data.migrations.*
import amrabed.android.release.evaluation.data.tables.History
import amrabed.android.release.evaluation.data.tables.TaskTable
import amrabed.android.release.evaluation.data.tables.v2.*
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Database(
    entities = [Record::class, Task::class, RitualEntity::class, RitualLogEntity::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(SelectionsConverter::class, ActiveDaysConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskTable(): TaskTable
    abstract fun history(): History
    abstract fun ritualDao(): RitualDao
    abstract fun ritualLogDao(): RitualLogDao

    class Callback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val list = arrayOfNulls<Task>(Task.DEFAULT_LIST.size)
            for (i in list.indices) {
                list[i] = Task(defaultIndex = i) // sets the default and current index of each task
            }
            writeExecutor.execute {
                list.forEach { database!!.taskTable().insertTask(it!!) }
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
                        database = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java, DATABASE_NAME
                        )
                            .addMigrations(Migration2To3(), Migration3To4(), Migration4To5())
                            .addCallback(Callback())
                            .build()
                    }
                }
            }
            return database
        }
    }
}
