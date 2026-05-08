package amrabed.android.release.evaluation.domain.model

data class DailyChecklist(
    val date: String,
    val groups: List<ChecklistGroup>
) {
    val totalCount: Int get() = groups.sumOf { it.rituals.size }
    val completedCount: Int get() = groups.sumOf { it.rituals.count { r -> r.log?.isComplete == true } }
    val completionRate: Float get() = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount
}

data class ChecklistGroup(
    val prayerGroup: PrayerGroup,
    val prayerTime: String?,   // today's calculated prayer time for this group (e.g. "5:43 AM")
    val rituals: List<RitualWithLog>
)

data class RitualWithLog(
    val ritual: Ritual,
    val log: RitualLog?           // null = not yet logged today
)
