package amrabed.android.release.evaluation.presentation.checklist.v2

import amrabed.android.release.evaluation.domain.model.ChecklistGroup
import amrabed.android.release.evaluation.domain.model.PrayerGroup
import amrabed.android.release.evaluation.domain.model.Ritual
import org.joda.time.LocalDate

data class ChecklistUiState(
    val date: String,
    val groups: List<ChecklistGroup> = emptyList(),
    val totalCount: Int = 0,
    val completedCount: Int = 0,
    val isReadOnly: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val dateLabel: String get() = LocalDate.parse(date).toString("EEE, MMM d")
}

sealed class ChecklistEvent {
    data class ToggleRitual(val ritualId: String, val isComplete: Boolean) : ChecklistEvent()
    data class NavigateToDate(val date: String) : ChecklistEvent()
    data class AddRitual(val ritual: Ritual) : ChecklistEvent()
    data class UpdateRitual(val ritual: Ritual) : ChecklistEvent()
    data class HideRitual(val ritualId: String) : ChecklistEvent()
    data class DeleteRitual(val ritualId: String) : ChecklistEvent()
    data class ReorderRituals(
        val ritualIds: List<String>,
        val prayerGroup: PrayerGroup
    ) : ChecklistEvent()
    object RestoreDefaults : ChecklistEvent()
}
