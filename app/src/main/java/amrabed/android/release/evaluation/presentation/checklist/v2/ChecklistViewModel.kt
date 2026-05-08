package amrabed.android.release.evaluation.presentation.checklist.v2

import amrabed.android.release.evaluation.domain.repository.RitualRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.joda.time.LocalDate

class ChecklistViewModel(
    private val repository: RitualRepository
) : ViewModel() {

    private val _currentDate = MutableStateFlow(LocalDate.now().toString())
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ChecklistUiState> = _currentDate
        .flatMapLatest { date ->
            repository.observeDailyChecklist(date)
                .map { checklist ->
                    val today = LocalDate.now()
                    val requestedDate = LocalDate.parse(date)
                    ChecklistUiState(
                        date = date,
                        groups = checklist.groups,
                        totalCount = checklist.totalCount,
                        completedCount = checklist.completedCount,
                        isReadOnly = requestedDate.isBefore(today)
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ChecklistUiState(date = _currentDate.value, isLoading = true)
        )

    fun onEvent(event: ChecklistEvent) {
        when (event) {
            is ChecklistEvent.ToggleRitual -> {
                if (uiState.value.isReadOnly) return
                viewModelScope.launch {
                    repository.toggleRitual(event.ritualId, uiState.value.date, event.isComplete)
                }
            }
            is ChecklistEvent.NavigateToDate -> {
                _currentDate.value = event.date
            }
            is ChecklistEvent.AddRitual -> {
                viewModelScope.launch {
                    repository.addRitual(event.ritual)
                }
            }
            is ChecklistEvent.UpdateRitual -> {
                viewModelScope.launch {
                    repository.updateRitual(event.ritual)
                }
            }
            is ChecklistEvent.HideRitual -> {
                viewModelScope.launch {
                    repository.hideRitual(event.ritualId)
                }
            }
            is ChecklistEvent.DeleteRitual -> {
                viewModelScope.launch {
                    repository.deleteRitual(event.ritualId)
                }
            }
            is ChecklistEvent.ReorderRituals -> {
                viewModelScope.launch {
                    repository.reorderRituals(event.ritualIds, event.prayerGroup)
                }
            }
            ChecklistEvent.RestoreDefaults -> {
                // Implementation for restore defaults
            }
        }
    }
}
