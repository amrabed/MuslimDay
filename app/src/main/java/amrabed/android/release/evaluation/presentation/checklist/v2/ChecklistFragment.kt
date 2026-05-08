package amrabed.android.release.evaluation.presentation.checklist.v2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import amrabed.android.release.evaluation.data.AppDatabase
import amrabed.android.release.evaluation.data.repository.RitualRepositoryImpl
import amrabed.android.release.evaluation.domain.repository.PrayerTimeRepositoryPlaceholder

class ChecklistFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val db = AppDatabase.get(requireContext())!!
        val repository = RitualRepositoryImpl(db.ritualDao(), db.ritualLogDao(), PrayerTimeRepositoryPlaceholder())
        val viewModel = ViewModelProvider(this, ChecklistViewModelFactory(repository))[ChecklistViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                ChecklistScreen(viewModel = viewModel)
            }
        }
    }
}
