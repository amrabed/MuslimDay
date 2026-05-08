package amrabed.android.release.evaluation.domain.model

data class RitualLog(
    val id: String,
    val ritualId: String,
    val date: String, // ISO date string: YYYY-MM-DD
    val completedAt: Long?,
    val isComplete: Boolean,
    val note: String? = null
)
