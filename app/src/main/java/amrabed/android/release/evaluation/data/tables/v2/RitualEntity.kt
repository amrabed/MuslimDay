package amrabed.android.release.evaluation.data.tables.v2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rituals")
data class RitualEntity(
    @PrimaryKey val id: String,
    val title: String?,
    val defaultIndex: Int = -1,
    val prayerGroup: String,
    val sortOrder: Int,
    val activeDays: Int = 127,
    val isHidden: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
