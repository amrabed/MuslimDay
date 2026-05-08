package amrabed.android.release.evaluation.presentation.checklist.v2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import amrabed.android.release.evaluation.domain.repository.RitualRepository

class ChecklistViewModelFactory(
    private val repository: RitualRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChecklistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChecklistViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
