package amrabed.android.release.evaluation.main.assessment

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.data.entities.Day
import amrabed.android.release.evaluation.models.DayViewModel
import amrabed.android.release.evaluation.utilities.time.DateManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.assessment.*

/**
 * Assessment section
 */
class AssessmentSection : Fragment() {
    private val viewModel by activityViewModels<DayViewModel>()

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.assessment, parent, false)
        viewModel.dayList?.observe(viewLifecycleOwner, Observer { dayList: List<Day> ->
            pager.adapter = SectionPagerAdapter(dayList)
            pager.currentItem = dayList.size - 1
        })
        return view
    }

    private inner class SectionPagerAdapter(private val dayList: List<Day?>) :
            FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            viewModel.selectDay(dayList[position])
            return DayFragment().apply { arguments = bundleOf(Pair(DATE, dayList[position]!!.date)) }
        }

        override fun getCount(): Int {
            return dayList.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return DateManager(requireContext()).getDate(dayList[position]!!.date)
        }
    }
}

const val DATE = "date"