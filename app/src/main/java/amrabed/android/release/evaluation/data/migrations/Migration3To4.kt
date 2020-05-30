package amrabed.android.release.evaluation.data.migrations

import amrabed.android.release.evaluation.data.converters.SelectionsConverter
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration class to migrate RoomDatabase from version 3 to version 4
 */
class Migration3To4 : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {

        database.execSQL("CREATE TABLE new_tasks (" +
                "id TEXT PRIMARY KEY NOT NULL, " +
                "defaultIndex INTEGER NOT NULL DEFAULT -1, " +
                "currentIndex INTEGER NOT NULL, " +
                "title TEXT, " +
                "activeDays INTEGER NOT NULL DEFAULT 0x7f, " +
                "reminder TEXT)")
        database.execSQL("INSERT INTO new_tasks (id, defaultIndex, currentIndex, title, activeDays) " +
                "SELECT id, defaultIndex, currentIndex, currentTitle, activeDays FROM tasks")
        database.execSQL("DROP TABLE tasks")
        database.execSQL("ALTER TABLE new_tasks RENAME TO tasks")


        database.execSQL("CREATE TABLE history (" +
                "date INTEGER NOT NULL, task TEXT NOT NULL, " +
                "selection INTEGER NOT NULL, note TEXT, " +
                "PRIMARY KEY(date, task))")

        val cursor = database.query("SELECT * FROM days")
        while (cursor.moveToNext()) {
            val date = cursor.getLong(0)
            val selections = SelectionsConverter().deserialize(cursor.getBlob(1))
            selections?.mapKeys { entry ->
                val values = ContentValues()
                values.put("date", date)
                values.put("task", entry.key)
                values.put("selection", entry.value)
                database.insert("history", SQLiteDatabase.CONFLICT_ROLLBACK, values)
            }
        }
    }
}