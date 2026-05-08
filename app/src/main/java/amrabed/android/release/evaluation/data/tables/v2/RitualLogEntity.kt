package amrabed.android.release.evaluation.data.tables.v2

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ritual_logs",
    foreignKeys = [
        ForeignKey(
            entity = RitualEntity::class,
            parentColumns = ["id"],
            childColumns = ["ritualId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("date"), Index("ritualId", "date", unique = true)]
)
data class RitualLogEntity(
    @PrimaryKey val id: String,
    val ritualId: String,
    val date: String, // ISO date string: YYYY-MM-DD
    val completedAt: Long?,
    val isComplete: Boolean,
    val note: String? = null
)
