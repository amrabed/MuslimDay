package amrabed.android.release.evaluation.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration class to migrate from SQLiteDatabase (version 2) to RoomDatabase (version 3)
 */
class Migration2To3 : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE new_days (" +
                "date INTEGER PRIMARY KEY NOT NULL," +
                "selections BLOB)")
        database.execSQL("INSERT INTO new_days (date, selections) SELECT * FROM days")
        database.execSQL("DROP TABLE days")
        database.execSQL("ALTER TABLE new_days RENAME TO days")
        database.execSQL("CREATE TABLE tasks (" +
                "id TEXT PRIMARY KEY NOT NULL," +
                "defaultIndex INTEGER NOT NULL DEFAULT -1," +
                "currentIndex INTEGER NOT NULL," +
                "currentTitle TEXT," +
                "activeDays INTEGER NOT NULL DEFAULT 0x7f)")
        database.execSQL("INSERT INTO tasks (id, defaultIndex, currentIndex, currentTitle, activeDays) " +
                "SELECT uniqueId, defaultIndex, currentIndex, currentTitle, activeDays FROM list")
        database.execSQL("DROP TABLE list")
    }
}