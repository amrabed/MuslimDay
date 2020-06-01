package amrabed.android.release.evaluation.main.progress

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.models.DayViewModel
import amrabed.android.release.evaluation.tools.graphs.StackedBarPlot
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import org.joda.time.DateTime

/**
 * Progress Section
 */
class ProgressSection : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View =
            inflater.inflate(R.layout.progress, parent, false).apply {
                val pager = findViewById<ViewPager2>(R.id.pager)
                pager.adapter = PagerAdapter()
                TabLayoutMediator(findViewById(R.id.title), pager) { tab, position ->
                    tab.text = resources.getStringArray(R.array.progress)[position]
                }.attach()
            }

    private inner class PagerAdapter : FragmentStateAdapter(requireActivity()) {
        override fun createFragment(position: Int) = IntervalFragment().apply { arguments = bundleOf(Pair(POSITION, position)) }
        override fun getItemCount() = 3
    }
}

/**
 * Fragment to show weekly, monthly, and yearly progress
 */
class IntervalFragment : Fragment() {
    private val viewModel by activityViewModels<DayViewModel>()

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View =
            inflater.inflate(R.layout.interval_fragment, parent, false).apply {
                val shift = nDays[requireArguments().getInt(POSITION)]
                viewModel.getRange(DateTime().minusDays(shift).millis, DateTime().millis)
                        ?.observe(viewLifecycleOwner, Observer {
                            StackedBarPlot(requireContext(), requireArguments().getInt(POSITION), it)
                                    .show(findViewById(R.id.chart))
                        })
            }
}

val nDays = intArrayOf(6, 30, 365)
const val POSITION = "position"