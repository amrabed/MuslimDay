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
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import org.joda.time.DateTime

/**
 * Progress Section
 */
class ProgressSection : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View? {
        val view = inflater.inflate(R.layout.progress, parent, false)
        view.findViewById<ViewPager>(R.id.pager).adapter = PagerAdapter()
        return view
    }

    private inner class PagerAdapter :
            FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return IntervalFragment().apply { arguments = bundleOf(Pair(POSITION, position)) }
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return resources.getStringArray(R.array.progress)[position]
        }
    }
}

/**
 * Fragment to show weekly, monthly, and yearly progress
 */
class IntervalFragment : Fragment() {
    private val viewModel by activityViewModels<DayViewModel>()

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.interval_fragment, parent, false)

        val shift = nDays[requireArguments().getInt(POSITION)]
        viewModel.getRange(DateTime().minusDays(shift).millis, DateTime().millis)
                ?.observe(viewLifecycleOwner, Observer {
                    StackedBarPlot(requireContext(), requireArguments().getInt(POSITION), it)
                            .show(view.findViewById(R.id.chart))
                })

        return view
    }
}

val nDays = intArrayOf(7, 30, 365)
internal const val POSITION = "position"