package amrabed.android.release.evaluation.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration4To5 : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create Ritual table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `rituals` (
                `id` TEXT NOT NULL,
                `title` TEXT,
                `defaultIndex` INTEGER NOT NULL DEFAULT -1,
                `prayerGroup` TEXT NOT NULL,
                `sortOrder` INTEGER NOT NULL DEFAULT 0,
                `activeDays` INTEGER NOT NULL DEFAULT 127,
                `isHidden` INTEGER NOT NULL DEFAULT 0,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """.trimIndent())

        // Create RitualLog table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS `ritual_logs` (
                `id` TEXT NOT NULL,
                `ritualId` TEXT NOT NULL,
                `date` TEXT NOT NULL,
                `completedAt` INTEGER,
                `isComplete` INTEGER NOT NULL DEFAULT 0,
                `note` TEXT,
                PRIMARY KEY(`id`),
                FOREIGN KEY(`ritualId`) REFERENCES `rituals`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
        """.trimIndent())

        // Create indexes
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_ritual_logs_date` ON `ritual_logs` (`date`)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_ritual_logs_ritualId_date` ON `ritual_logs` (`ritualId`, `date`)")
    }
}
