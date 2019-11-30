package amrabed.android.release.evaluation.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import amrabed.android.release.evaluation.data.converters.ActiveDaysConverter;
import amrabed.android.release.evaluation.data.converters.SelectionsConverter;
import amrabed.android.release.evaluation.data.entities.Day;
import amrabed.android.release.evaluation.data.entities.Task;
import amrabed.android.release.evaluation.data.migrations.Migration2To3;
import amrabed.android.release.evaluation.data.tables.DayTable;
import amrabed.android.release.evaluation.data.tables.TaskTable;
import amrabed.android.release.evaluation.preferences.Preferences;

@Database(entities = {Day.class, Task.class}, version = 3)
@TypeConverters({SelectionsConverter.class, ActiveDaysConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "activities";

    public abstract TaskTable taskTable();
    public abstract DayTable dayTable();

    private static volatile AppDatabase database;
    static final ExecutorService writeExecutor = Executors.newFixedThreadPool(4);

    static AppDatabase get(final Context context) {
        if (database == null) {
            synchronized (AppDatabase.class) {
                if (database == null) {
                    database = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                            .addMigrations(new Migration2To3())
                            .addCallback(new Callback(context))
                            .build();
                }
            }
        }
        return database;
    }

    private static class Callback extends RoomDatabase.Callback {
        private static final DateTime TODAY = new DateTime().withTimeAtStartOfDay();

        private Context context;

        private Callback(Context context) {
            this.context = context;
        }

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            final Task[] list = new Task[Task.DEFAULT_LIST.length];
            for (int i = 0; i < list.length; i++) {
                list[i] = new Task(i); // sets the default index of each task
            }
            writeExecutor.execute(() -> database.taskTable().insertTasks(list));
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            writeExecutor.execute(() -> database.dayTable().insert(new Day(TODAY)));
            // Add any missing days in the last month
            final DateTime lastAddedDay = new DateTime(Preferences.getLastAddedDay(context));
            if (lastAddedDay.isBefore(TODAY)) {
                final int diff = Days.daysBetween(lastAddedDay, TODAY).getDays();
                final Day[] days = new Day[(diff > 31 ? 31 : diff)];
                for (int i = 1; i < days.length; i++) {
                    days[i] = new Day(TODAY.minusDays(i));
                }
                writeExecutor.execute(() -> database.dayTable().insert(days));
                Preferences.setLastAddedDay(context, TODAY.getMillis());
            }
        }
    }
}